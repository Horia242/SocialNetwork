package com.example.ex2;
import com.example.ex2.rootService.RootService;
import com.example.ex2.utils.FriendshipRequestForDisplayUseDTO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;
import ro.ubbcluj.map.repository.paging.PageRequest;
import ro.ubbcluj.map.utils.events.Event;
import ro.ubbcluj.map.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DashboardController  {

    RootService rootService;
    String loggedInUsername;
    private boolean scrollTop = false;
    private boolean scrollBottom = true;
    private boolean currentDisplayedConversationFullLoaded = false;
    private ConversationPartnerDetailsController openedConversationController;
    private int friendsDisplayCurrentPage = 0;
    private int requestsDisplayCurrentPage = 0;
    private int conversationsDisplayCurrentPage = 0;
    private int conversationDisplayPageSize = 7;
    private boolean initFriendsDisplay = true;
    private boolean initFriendshipRequestDisplay = true;
    private boolean initConvPartnersDisplay = true;
    private boolean stopLoadingFriends = false;
    private boolean stopLoadingRequests = false;
    private boolean stopLoadingConversations = false;


    private boolean ok=true;
    @FXML
    private Label txtFrRequestCount;
    @FXML
    private Circle circleRequestsNumber;
    @FXML
    private BorderPane borderPaneDashboard;
    @FXML
    private ImageView btnShowFriends;
    @FXML
    private ImageView btnShowFriendRequests;
    @FXML
    private Pane pnlFriends;
    @FXML
    private Pane pnlFriendRequests;
    @FXML
    private Label labelUsername;
    @FXML
    private Label labelFriends;
    @FXML
    private Label labelFriendRequests;
    @FXML
    private TableView<UserDto<String>> tabviewFriends;
    @FXML
    private TableColumn<UserDto<String>,String> tabcolFirstName;
    @FXML
    private TableColumn<UserDto<String>,String> tabcolLastName;
    @FXML
    private TableColumn<UserDto<String>,String> tabcolEmail;
    @FXML
    private TextField textFieldSearchUser;
    @FXML
    private VBox vboxSearchResult;
    @FXML
    private TableView<FriendshipRequestForDisplayUseDTO<String>> tabviewRequests;
    @FXML
    private TableColumn <FriendshipRequestForDisplayUseDTO<String>, String> tbl_nume;
    @FXML
    private TableColumn <FriendshipRequestForDisplayUseDTO<String>, String> tbl_prenume;
    @FXML
    private TableColumn <FriendshipRequestForDisplayUseDTO<String>, String> tbl_data;
    @FXML
    private TableColumn <FriendshipRequestForDisplayUseDTO<String>, String> tbl_status;
    @FXML
    private ImageView btn_accept;
    @FXML
    private ImageView btn_decline;
    @FXML
    private ImageView imgDeleteFriend;
    @FXML
    private ImageView btnSignOut;
    @FXML
    private ImageView btnSignOut1;
    @FXML
    private HBox hboxChat;
    @FXML
    private Pane pnlChat;
    @FXML
    private VBox vboxConversationPartners;
    @FXML
    private VBox vboxMessagesText;
    @FXML
    private TextField txtFieldTypeMessage;
    @FXML
    private ImageView imgSendMessage;
    @FXML
    private Pane paneReply;
    @FXML
    private ScrollPane scrollPaneMessages;
    @FXML
    private Label labelMessageRepliedTo;
    @FXML
    private Label labelSelectedMessageId;
    @FXML
    private Label labelSenderUserId;
    @FXML
    private Label labelMessageSenderUsername;
    @FXML
    private Pane paneAccountPage;
    @FXML
    private Label labelForReplies;
    @FXML
    private ScrollPane scrollPaneConversations;

    private final int scrollPaneMessagesHeight = 417;
    private final int  vboxMessagesTextHeight = 413;
    private String currentChatPartnerShowingId;
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    public void init(){
        movableDashboard();
        requestNotifyCircle();
        toolTip();
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) tabviewFriends.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 1.0) {
                    continueAddingFriendsWhenScrollReachesBottom();
                }
            });

        });
        Platform.runLater(() -> {
            ScrollBar tvScrollBar = (ScrollBar) tabviewRequests.lookup(".scroll-bar:vertical");
            tvScrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                if ((Double) newValue == 1.0) {
                    continueAddingRequestsWhenScrollReachesBottom();
                }
            });

        });
        scrollPaneConversations.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0 ) {
                System.out.println("Bottom!");
                //scrollingStarted();
                loadNextConversationPartners();
            }
        });

    }

    public void setOpenedConversationController(ConversationPartnerDetailsController openedConversationController) {
        this.openedConversationController = openedConversationController;
    }

    private void movableDashboard(){
        borderPaneDashboard.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        borderPaneDashboard.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });
    }
    private void requestNotifyCircle(){
        int requestsNumber = this.rootService.getNetworkServicePag().getNumberOfAllPendingFriendshipRequestsForOneUser(labelUsername.getText());
        if(requestsNumber > 0){
            circleRequestsNumber.setFill(Paint.valueOf("#ee0d06"));
            txtFrRequestCount.setText(String.valueOf(requestsNumber));
        }
        else
        {
            circleRequestsNumber.setFill(Paint.valueOf("#eaeae9"));
        }
    }
    private void toolTip(){
        Tooltip tooltipAcceptFriendshipRequest = new Tooltip("Accept request");
        Tooltip tooltipDeclineFriendshipRequest = new Tooltip("Decline request");
        Tooltip tooltipDeleteFriend = new Tooltip("Delete friend");
        Tooltip.install(btn_accept,tooltipAcceptFriendshipRequest);
        Tooltip.install(btn_decline,tooltipDeclineFriendshipRequest );
        Tooltip.install(imgDeleteFriend,tooltipDeleteFriend);
        Tooltip.install(btnSignOut,new Tooltip("Sign out"));
        Tooltip.install(btnSignOut1,new Tooltip("Sign out"));
    }
    public void setRootService(RootService rootService){
        this.rootService = rootService;
    }
    public void setLoggedInUserEmail(String username){
        labelUsername.setText(username);
        loggedInUsername = username;
    }
    public  void setLabelMessageSenderUsername(String username){
        labelMessageSenderUsername.setText(username);
    }

    //Locul in care se afiseaza mesajele dintr-o conversatie revine la setarile initiale
    @FXML
    private void handleOnHboxTextMessageFieldClick(){
        vboxMessagesText.setPrefHeight(vboxMessagesTextHeight);
        scrollPaneMessages.setPrefHeight(scrollPaneMessagesHeight);
        vboxMessagesText.toFront();
        scrollPaneMessages.toFront();
    }
    @FXML
    private void handleMouseEvent(MouseEvent event){
        if(event.getSource().equals(btnShowFriends) || event.getSource().equals(labelFriends)){
            displayUserFriends(labelUsername.getText());
        }
        if(event.getSource().equals(btnShowFriendRequests) || event.getSource().equals(labelFriendRequests)){
            displayUserFriendsRequests(labelUsername.getText());
            requestNotifyCircle();
        }
        if(event.getSource().equals(textFieldSearchUser)){
            vboxSearchResult.getChildren().clear();
            vboxSearchResult.toBack();
        }
        if(event.getSource().equals(hboxChat)){
                displayUserConversationPartners(loggedInUsername);
            pnlFriends.toBack();
            pnlFriendRequests.toBack();
            pnlChat.toFront();
        }
    }

    @FXML
    private void handleSignOutEvent(MouseEvent event){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
        try {
            Parent root = loader.load();
            AppEventsController appEventsController = loader.getController();
            appEventsController.setRootService(this.rootService);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dealWithTheLastMessageSent(MessageDTO messageDTO){
        HBox hBox = new HBox();
        Label labelMessageId = new Label(messageDTO.getId().toString());
        labelMessageId.setTextFill(Color.valueOf("#E1E1DF"));
        TextField textField = new TextField();
        textField.setEditable(false);
        textField.setText(messageDTO.getMessage());
        textField.setFont(Font.font("System", 13));
        textField.setPrefWidth(40 + messageDTO.getMessage().length() * 7);
        textField.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                labelMessageRepliedTo.setText(textField.getText());
                labelSelectedMessageId.setTextFill(Color.valueOf("#EAEAE9"));
                labelSelectedMessageId.setText(messageDTO.getId().toString());
                labelSenderUserId.setTextFill(Color.valueOf("#EAEAE9"));
                labelSenderUserId.setText(messageDTO.getFrom().getUserID());
                scrollPaneMessages.toBack();
                vboxMessagesText.setMaxHeight(vboxMessagesTextHeight - 80);
                scrollPaneMessages.setPrefHeight(scrollPaneMessagesHeight - 80);
                if (mouseEvent.getClickCount() == 2) {
                    labelForReplies.setText("Reply All:");
                } else if (mouseEvent.getClickCount() == 1) {
                    labelForReplies.setText("Reply To:");
                }
            }
        });
        Pane pane = new Pane();
        MessageDTO messageRepliedTo = rootService.getNetworkServicePag().repliesTo(messageDTO.getId());
        if (messageRepliedTo != null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("repliesToPane.fxml"));
            try {
                pane = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pane.setStyle("-fx-border-radius: 20;\n" +
                    "    -fx-background-radius: 20;\n" +
                    "    -fx-background-color:#D4D4D4;");
            pane.setPrefWidth(textField.getPrefWidth() + 40);
            pane.getChildren().add(textField);
            textField.setLayoutX(20);
            textField.setLayoutY(29);
            TextField textRepliedTo = new TextField("Replies to:" + messageRepliedTo.getMessage());
            textRepliedTo.setPrefWidth(100);
            textRepliedTo.setPrefHeight(20);
            textRepliedTo.setStyle("-fx-background-color: #D4D4D4;");
            pane.getChildren().add(textRepliedTo);
            hBox.getChildren().add(pane);
        } else {
            hBox.getChildren().add(textField);
            hBox.getChildren().add(labelMessageId);
        }
        if (messageDTO.getFrom().getUserID().equals(loggedInUsername)) {
            textField.setStyle("-fx-background-radius: 15px;" +
                    "-fx-background-color:#B5F2EC;" +
                    "-fx-text-fill: black;");
            hBox.setAlignment(Pos.BASELINE_RIGHT);
        } else {
            textField.setStyle("-fx-background-radius: 15px;\n" +
                    "    -fx-background-color: white;");
            hBox.setAlignment(Pos.BASELINE_LEFT);
        }
        vboxMessagesText.getChildren().add(hBox);
        vboxMessagesText.toFront();
    }
    @FXML
    private void handleSendMessage(){
       String message =  txtFieldTypeMessage.getText();

       if( scrollPaneMessages.getHeight() < scrollPaneMessagesHeight ){
           if(labelForReplies.getText().equals("Reply To:")) {
               rootService.getNetworkServicePag().replyAll(new MessageDTO(new UserDto<String>(labelSenderUserId.getText(), null, null)
                               , List.of(new UserDto<String>(loggedInUsername, null, null))
                               , labelMessageRepliedTo.getText()
                               , LocalDateTime.now()
                               , Long.parseLong(labelSelectedMessageId.getText()))
                       , loggedInUsername
                       , message);
           }
           else{
               if(labelForReplies.getText().equals("Reply All:")){
                   MessageDTO original = rootService.getNetworkServicePag().findOneMessageById(Long.parseLong(labelSelectedMessageId.getText()));
                   rootService.getNetworkServicePag().replyAll(original,loggedInUsername,message);
               }
           }
       }
       else {
           rootService.getNetworkServicePag().sendMessage(new MessageDTO(new UserDto<String>(loggedInUsername, null, null)
                   , List.of(new UserDto<String>(currentChatPartnerShowingId, null, null)),
                   message, LocalDateTime.now(), 0L));
       }

       List<MessageDTO> lastMessageSent = rootService.getNetworkServicePag().getConversationHistory(loggedInUsername,currentChatPartnerShowingId,new PageRequest(0,1),SortingOrder.DESC);
       dealWithTheLastMessageSent(lastMessageSent.get(0));

       //displayUserConversationMessages(currentChatPartnerShowingId);
       txtFieldTypeMessage.clear();
    }

    @FXML
    private void handleSearchUser(){
        if(!textFieldSearchUser.getText().isEmpty()){
            vboxSearchResult.getChildren().clear();
            try {
                List<FriendshipDto<String>> userFriends = rootService.getNetworkServicePag().getFriendshipList(labelUsername.getText(),new PageRequest(friendsDisplayCurrentPage,20));
                for(UserDto<String> userDto:getUserNamesStartingWith(textFieldSearchUser.getText()))
                {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("userDetailsBox.fxml"));
                    try{
                        HBox hBox = fxmlLoader.load();
                        UserDetailsBoxController controller = fxmlLoader.getController();
                        controller.setRootService(rootService);
                        controller.setLoggedInUserEmail(labelUsername.getText());
                        if(!userDto.getUserID().equals(labelUsername.getText())) {
                            Predicate<FriendshipDto<String>> isBetweenUsersFriends = frienshipDto -> frienshipDto.getUser1().getUserID().equals(userDto.getUserID())
                                    || frienshipDto.getUser2().getUserID().equals(userDto.getUserID());

                            if (userFriends.stream().anyMatch(isBetweenUsersFriends)) {
                                controller.setData(userDto, 0);
                            } else {
                                FriendshipRequestDTO<String> friendshipRequestDTO = rootService.getNetworkServicePag().existsPendingFriendshipRequest(new Tuple<String, String>(labelUsername.getText(), userDto.getUserID()));
                                if (friendshipRequestDTO != null) {
                                    if (friendshipRequestDTO.getFrom().getUserID().equals(labelUsername.getText()))
                                        controller.setData(userDto, 1);
                                    else {
                                        if (friendshipRequestDTO.getFrom().getUserID().equals(userDto.getUserID()))
                                            controller.setData(userDto, 3);
                                    }
                                } else {
                                    controller.setData(userDto, 2);
                                }
                            }
                        }
                        else{
                            controller.setData(userDto,4);
                        }
                        vboxSearchResult.getChildren().add(hBox);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                vboxSearchResult.toFront();
            } catch (InsufficientDataToExecuteTaskException | RepoError e) {
                e.printStackTrace();
            }

        }


    }


    @FXML
    private void handleDeleteUser(){
        UserDto<String> selectedUser = tabviewFriends.getSelectionModel().getSelectedItem();
        rootService.getNetworkServicePag().deleteFriendship(selectedUser.getUserID(),labelUsername.getText());
        displayUserFriends(labelUsername.getText());

    }

    @FXML
    private void handleAcceptImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if(tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO<String> selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO<String> friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.APPROVED);
            rootService.getNetworkServicePag().updateFriendshipRequestStatus(friendshipRequestDTO);
            displayUserFriendsRequests(labelUsername.getText());
            requestNotifyCircle();
        }
    }

    @FXML
    private void handleDeclineImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if(tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO<String> selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO<String> friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.REJECTED);
            rootService.getNetworkServicePag().updateFriendshipRequestStatus(friendshipRequestDTO);
            displayUserFriendsRequests(labelUsername.getText());
            requestNotifyCircle();
        }
    }

    private int friendsPageSize = 10;
    private List<FriendshipDto<String>> getFriendsPage(){
        try {
            List<FriendshipDto<String>> all = rootService.getNetworkServicePag().getFriendshipList(loggedInUsername, new PageRequest(friendsDisplayCurrentPage, friendsPageSize));
            if(all == null)return null;
            friendsDisplayCurrentPage += 1;
            return all;
        } catch (InsufficientDataToExecuteTaskException | RepoError e) {
            e.printStackTrace();
        }
        return null;
    }
    private void continueAddingFriendsWhenScrollReachesBottom(){
        List<FriendshipDto<String>> all = getFriendsPage();
        if(all != null) {
            for (FriendshipDto<String> friendshipDto : all) {
                UserDto<String> userDto;
                if (friendshipDto.getUser1().getUserID().equals(loggedInUsername))
                    userDto = friendshipDto.getUser2();
                else
                    userDto = friendshipDto.getUser1();
                tabviewFriends.getItems().add(new UserDto<String>(userDto.getUserID(), userDto.getFirstName(), userDto.getLastName()));
            }
        }
    }

    private void displayUserFriends(String userEmail){
        friendsDisplayCurrentPage = 0;
        tabviewFriends.getItems().clear();
        tabcolEmail.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tabcolFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tabcolLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        continueAddingFriendsWhenScrollReachesBottom();
        pnlFriends.toFront();

    }

    private int requestsPageSize = 10;
    private List<FriendshipRequestDTO<String>> getRequestsPage(){
        List<FriendshipRequestDTO<String>> all  = rootService.getNetworkServicePag().getAllPendingFriendshipRequestForOneUser(loggedInUsername,new PageRequest(requestsDisplayCurrentPage,requestsPageSize));
        if(all == null)return null;
        requestsDisplayCurrentPage += 1;
        return all;
    }

    private void continueAddingRequestsWhenScrollReachesBottom(){
        List<FriendshipRequestDTO<String>> all = getRequestsPage();
        if(all != null) {
            for (FriendshipRequestDTO<String> friendshipRequestDTO : all) {
                tabviewRequests.getItems().add(new FriendshipRequestForDisplayUseDTO<String>(friendshipRequestDTO.getFrom().getFirstName(), friendshipRequestDTO.getFrom().getLastName(), friendshipRequestDTO.getStatus(), friendshipRequestDTO.getDate(), friendshipRequestDTO));
            }
        }
    }
    private void displayUserFriendsRequests(String userEmail) {
        tabviewRequests.getItems().clear();
        requestsDisplayCurrentPage = 0;
        if(initFriendshipRequestDisplay) {
            tbl_nume.setCellValueFactory(new PropertyValueFactory<>("name"));
            tbl_prenume.setCellValueFactory(new PropertyValueFactory<>("surname"));
            tbl_data.setCellValueFactory(new PropertyValueFactory<>("status"));
            tbl_status.setCellValueFactory(new PropertyValueFactory<>("date"));
            continueAddingRequestsWhenScrollReachesBottom();
            pnlFriendRequests.toFront();
        }
    }

    private List<UserDto<String>> getCurrentConversationPage(){
        List<UserDto<String>> all =  rootService.getNetworkServicePag().getAllUserConversationPartners(loggedInUsername,new PageRequest(conversationsDisplayCurrentPage,conversationDisplayPageSize));
        if(all == null){stopLoadingConversations = true;return null;}
        conversationsDisplayCurrentPage += 1;
        return all;
    }
    private void loadNextConversationPartners(){
        List<UserDto<String>> all = getCurrentConversationPage();
        if(all != null) {
            for (UserDto<String> userDto : all) {
                if (!userDto.getUserID().equals(loggedInUsername)) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("conversationPartnerDetails.fxml"));
                    try {
                        Pane paneChatPartner = fxmlLoader.load();
                        ConversationPartnerDetailsController partnerDetailsController = fxmlLoader.getController();
                        partnerDetailsController.init(this.rootService, this.loggedInUsername, userDto, this);
                        vboxConversationPartners.getChildren().add(paneChatPartner);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    public void displayUserConversationPartners(String userEmail){
               conversationsDisplayCurrentPage = 0;
               vboxConversationPartners.getChildren().clear();
               vboxConversationPartners.setSpacing(7);
               loadNextConversationPartners();
    }

    private void updateScrollPaneView(String conversationPartnerEmail ,List<MessageDTO> messageDTOToAdd) {

        //am nevoie de ceva care sa imi returneze ulimul mesaj citit(id-ul lui)
        vboxMessagesText.getChildren().clear();
        Long lastReadMessageID = -1L;
        boolean placeLine = false;
        for (MessageDTO messageDTO : messageDTOToAdd) {
            if (placeLine) {
                VBox vboxUnreadSign = new VBox();
                vboxUnreadSign.setStyle("-fx-border-color: #bcb3b3;\n" +
                        "    -fx-border-width: 0px 0px 2px 0px;");
                Label unreadMessage = new Label("Unread messages");
                unreadMessage.setAlignment(Pos.BOTTOM_CENTER);
                VBox.setMargin(vboxUnreadSign, new Insets(0, 20, 0, 20));
                vboxUnreadSign.getChildren().add(unreadMessage);
                vboxMessagesText.getChildren().add(vboxUnreadSign);
                placeLine = false;
            }
            if(messageDTO.getId().equals(lastReadMessageID))placeLine = true;
            HBox hBox = new HBox();
            Label labelMessageId = new Label(messageDTO.getId().toString());
            labelMessageId.setTextFill(Color.valueOf("#E1E1DF"));
            TextField textField = new TextField();
            textField.setEditable(false);
            textField.setText(messageDTO.getMessage());
            textField.setFont(Font.font("System", 13));
            // textField.setPrefWidth(messageDTO.getMessage().length()*7);
            textField.setPrefWidth(40 + messageDTO.getMessage().length() * 7);
            textField.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    labelMessageRepliedTo.setText(textField.getText());
                    labelSelectedMessageId.setTextFill(Color.valueOf("#EAEAE9"));
                    labelSelectedMessageId.setText(messageDTO.getId().toString());
                    labelSenderUserId.setTextFill(Color.valueOf("#EAEAE9"));
                    labelSenderUserId.setText(messageDTO.getFrom().getUserID());
                    scrollPaneMessages.toBack();
                    vboxMessagesText.setMaxHeight(vboxMessagesTextHeight - 80);
                    scrollPaneMessages.setPrefHeight(scrollPaneMessagesHeight - 80);
                    if (mouseEvent.getClickCount() == 2) {
                        labelForReplies.setText("Reply All:");
                    } else if (mouseEvent.getClickCount() == 1) {
                        labelForReplies.setText("Reply To:");
                    }
                }
            });
            Pane pane = new Pane();
            MessageDTO messageRepliedTo = rootService.getNetworkServicePag().repliesTo(messageDTO.getId());
            if (messageRepliedTo != null) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("repliesToPane.fxml"));
                try {
                    pane = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pane.setStyle("-fx-border-radius: 20;\n" +
                        "    -fx-background-radius: 20;\n" +
                        "    -fx-background-color:#D4D4D4;");
                pane.setPrefWidth(textField.getPrefWidth() + 40);
                pane.getChildren().add(textField);
                textField.setLayoutX(20);
                textField.setLayoutY(29);
                TextField textRepliedTo = new TextField("Replies to:" + messageRepliedTo.getMessage());
                textRepliedTo.setPrefWidth(100);
                textRepliedTo.setPrefHeight(20);
                textRepliedTo.setStyle("-fx-background-color: #D4D4D4;");
                pane.getChildren().add(textRepliedTo);
                hBox.getChildren().add(pane);
            } else {
                hBox.getChildren().add(textField);
                hBox.getChildren().add(labelMessageId);
            }
            if (messageDTO.getFrom().getUserID().equals(loggedInUsername)) {
                textField.setStyle("-fx-background-radius: 15px;" +
                        "-fx-background-color:#B5F2EC;" +
                        "-fx-text-fill: black;");
                hBox.setAlignment(Pos.BASELINE_RIGHT);
            } else {
                textField.setStyle("-fx-background-radius: 15px;\n" +
                        "    -fx-background-color: white;");
                hBox.setAlignment(Pos.BASELINE_LEFT);
            }
            vboxMessagesText.getChildren().add(hBox);
        }
    }

    @FXML
    private void handleOnMessagePaneScroll(){
        String[] pair= labelMessageSenderUsername.getText().split(":");
        int pageNumber = openedConversationController.getCurrentPage() + 1;
        openedConversationController.setCurrentPage(pageNumber);
        int pageSize = (pageNumber+1) * openedConversationController.getPageSize();
        List<MessageDTO> result = rootService.getNetworkServicePag().getConversationHistory(pair[1],loggedInUsername,new PageRequest(0,pageSize),SortingOrder.DESC);
        if(result != null) {
            updateScrollPaneView(pair[1], result);
            currentDisplayedConversationFullLoaded = true;
        }
    }

    @FXML
    private void scrollingStarted(){
        scrollPaneMessages.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() == 0 )
            {
                handleOnMessagePaneScroll();

            }
        });
    }

    public void displayUserConversationMessages(String conversationPartnerEmail){
        this.scrollTop = false;
        this.currentChatPartnerShowingId = conversationPartnerEmail;
        labelMessageSenderUsername.setText("From:"+currentChatPartnerShowingId);
        vboxMessagesText.getChildren().clear();
        currentDisplayedConversationFullLoaded = false;
        vboxMessagesText.setSpacing(8);
        handleOnMessagePaneScroll();

        scrollPaneMessages.setVvalue(scrollPaneMessages.getVmax());
        scrollPaneMessages.toFront();
        scrollPaneMessages.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0 && scrollBottom) {
                System.out.println("Bottom!");
                scrollBottom  = false;
                scrollingStarted();
            }
        });
        vboxMessagesText.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!scrollTop) {
                    scrollPaneMessages.setVvalue((Double) newValue);
                    scrollTop = true;
                }
            }
        });
    }


    private List<UserDto<String>> getUserNamesStartingWith(String startsWith){
        return rootService.getNetworkServicePag().getUsersForWhichNameStartsWith(startsWith);
    }
}
