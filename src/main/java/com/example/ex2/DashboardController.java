package com.example.ex2;
import com.example.ex2.rootService.RootService;
import com.example.ex2.utils.FriendshipRequestForDisplayUseDTO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.w3c.dom.Document;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;
import ro.ubbcluj.map.utils.events.Event;
import ro.ubbcluj.map.utils.events.EventType;
import ro.ubbcluj.map.utils.events.NetworkServiceTask;
import ro.ubbcluj.map.utils.observer.Observer;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DashboardController  implements Observer {


    RootService rootService;
    String loggedInUsername;
    Page page;
    @FXML
    private Pane empty_pane;
    @FXML
    private Label labelPdf;
    @FXML
    private ImageView imgPdf;
    @FXML
    private Label txtFrRequestCount;
    @FXML
    private Label txtMessageCount;
    @FXML
    private Circle circleRequestsNumber;
    @FXML
    private Circle circleMessageNumber;
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
    private Pane pnlConversation;
    @FXML
    private Pane pnlSendMsg;
    @FXML
    private Pane stats_pane;
    @FXML
    private Label labelUsername;
    @FXML
    private Label labelFriends;
    @FXML
    private Label labelFriendRequests;
    @FXML
    private TableView<UserDto<String>> tabviewFriends;
    @FXML
    private TableColumn<UserDto<String>, String> tabcolFirstName;
    @FXML
    private TableColumn<UserDto<String>, String> tabcolLastName;
    @FXML
    private TableColumn<UserDto<String>, String> tabcolEmail;
    @FXML
    private TextField textFieldSearchUser;
    @FXML
    private TextField texfFieldSearchUserForNewMsg;
    @FXML
    private VBox vboxSearchResult;
    @FXML
    private VBox vboxSearchResultForNewMsg;
    @FXML
    private TableView<FriendshipRequestForDisplayUseDTO<String>> tabviewRequests;
    @FXML
    private TableColumn<FriendshipRequestForDisplayUseDTO<String>, String> tbl_nume;
    @FXML
    private TableColumn<FriendshipRequestForDisplayUseDTO<String>, String> tbl_prenume;
    @FXML
    private TableColumn<FriendshipRequestForDisplayUseDTO<String>, String> tbl_data;
    @FXML
    private TableColumn<FriendshipRequestForDisplayUseDTO<String>, String> tbl_status;
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
    private ImageView BtnSendMsg;
    @FXML
    private HBox hboxChat;
    @FXML
    private HBox hboxPDF;
    @FXML
    private Pane pnlChat;
    @FXML
    private Pane pane_for_new_msg;
    @FXML
    private VBox vboxConversationPartners;
    @FXML
    private VBox vboxMessagesText;
    @FXML
    private TextField txtFieldTypeMessage;
    @FXML
    private TextField txtFieldTypeMessage1;
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
    private TextField friend_email_label;
    @FXML
    private DatePicker from_date_select;
    @FXML
    private DatePicker to_date_select;
    @FXML
    private Button pdf_generator_for_messages;
    @FXML
    private Button pdf_generator_for_friends;
    @FXML
    private Label text_field_welcome;
    @FXML
    private Pane pane_for_basic_messages;
    @FXML
    private Pane pane_for_composed_messages;

    private List<UserDto<String>> recipientsList;

    private final int scrollPaneMessagesHeight = 417;
    private final int vboxMessagesTextHeight = 413;
    private String currentChatPartnerShowingId;
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    public void init() {
        movableDashboard();
        page = new Page(rootService.getNetworkService().findUserById(loggedInUsername)
                ,rootService.getNetworkService().getUnreadMessagesCount(loggedInUsername)
                ,rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(loggedInUsername).size());
        requestNotifyCircle();
        messageNotifyCircle();
        toolTip();
        recipientsList = new ArrayList<>();
        text_field_welcome.setText("Welcome "+rootService.getNetworkService().findUserById(loggedInUsername).getFirstName()+"!");
        text_field_welcome.setFont(Font.font("Comic Sans MS",20));


    }

    private void movableDashboard() {
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

    private void requestNotifyCircle() {
        //int requestsNumber = this.rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(labelUsername.getText()).size();
        if (page.getFrCount() > 0) {
            circleRequestsNumber.setFill(Paint.valueOf("#ee0d06"));
            txtFrRequestCount.setText(String.valueOf(page.getFrCount()));

        } else {
            circleRequestsNumber.setFill(Paint.valueOf("#eaeae9"));
        }
    }

    private void messageNotifyCircle() {
        if (page.getMessageCount() > 0) {
            circleMessageNumber.setFill(Paint.valueOf("#ee0d06"));
            txtMessageCount.setText(String.valueOf(page.getMessageCount()));

        } else {
            circleMessageNumber.setFill(Paint.valueOf("#eaeae9"));
        }
    }

    private void toolTip() {
        //tooltipAcceptFriendshipRequest.setStyle("-fx-background-color:#E4E9E8; -fx-text-fill: black;");
        Tooltip tooltipAcceptFriendshipRequest = new Tooltip("Accept request");
        Tooltip tooltipDeclineFriendshipRequest = new Tooltip("Decline request");
        Tooltip tooltipDeleteFriend = new Tooltip("Delete friend");
        Tooltip.install(btn_accept, tooltipAcceptFriendshipRequest);
        Tooltip.install(btn_decline, tooltipDeclineFriendshipRequest);
        Tooltip.install(imgDeleteFriend, tooltipDeleteFriend);
        Tooltip.install(btnSignOut, new Tooltip("Sign out"));
        Tooltip.install(btnSignOut1, new Tooltip("Sign out"));

    }

    public void setRootService(RootService rootService) {
        this.rootService = rootService;
        rootService.getNetworkService().addObserver(this);
    }

    public void setLoggedInUserEmail(String username) {
        labelUsername.setText(username);
        loggedInUsername = username;
    }

    public void setLabelMessageSenderUsername(String username) {
        labelMessageSenderUsername.setText(username);
    }

    //Locul in care se afiseaza mesajele dintr-o conversatie revine la setarile initiale
    @FXML
    private void handleOnHboxTextMessageFieldClick() {
        vboxMessagesText.setPrefHeight(vboxMessagesTextHeight);
        scrollPaneMessages.setPrefHeight(scrollPaneMessagesHeight);
//        pnlConversation.toFront();
        vboxMessagesText.toFront();
        scrollPaneMessages.toFront();
    }

    private boolean composeMessageMode = false;

    @FXML
    private void handleMouseEvent(MouseEvent event) throws RepoError, InsufficientDataToExecuteTaskException {
        if (event.getSource().equals(btnShowFriends) || event.getSource().equals(labelFriends)) {
            displayUserFriends(labelUsername.getText());
            paneAccountPage.toBack();
            pnlFriends.toFront();
        }
        if (event.getSource().equals(btnShowFriendRequests) || event.getSource().equals(labelFriendRequests)) {
            displayUserFriendsRequests(labelUsername.getText());
            pnlFriendRequests.toFront();
            paneAccountPage.toBack();
            requestNotifyCircle();
        }
        if (event.getSource().equals(textFieldSearchUser)) {
            vboxSearchResult.getChildren().clear();
            vboxSearchResult.toBack();
        }
        if (event.getSource().equals(hboxChat)) {
            composeMessageMode=false;
            displayUserConversationPartners(loggedInUsername);
            pnlFriends.toBack();
            stats_pane.toBack();
            pnlFriendRequests.toBack();
            paneReply.toBack();
            empty_pane.toFront();
            pnlChat.toFront();
//            pnlSendMsg.toBack();
            pnlConversation.toFront();
            pane_for_basic_messages.toFront();
        }
        if (event.getSource().equals(hboxPDF) || event.getSource().equals(imgPdf) || event.getSource().equals(labelPdf)){
            pnlFriends.toBack();
            pnlFriendRequests.toBack();
            pnlConversation.toBack();
            pnlChat.toBack();
            pnlSendMsg.toBack();
            paneAccountPage.toBack();
            empty_pane.toFront();
            stats_pane.toFront();
        }
        if (event.getSource().equals(BtnSendMsg)) {
            pnlConversation.toBack();
            pnlSendMsg.toFront();
            composeMessageMode = true;
        }
        if (event.getSource().equals(pdf_generator_for_messages)){
            generate_PDF_for_new_messages();
        }
        if (event.getSource().equals(pdf_generator_for_friends)){
            generate_PDF_for_friends_and_messages();
        }
    }

    @FXML
    private void handleSignOutEvent(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
        try {
            Parent root = loader.load();
            AppEventsController appEventsController = loader.getController();
            appEventsController.setRootService(this.rootService);
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            PDDocument doc = new PDDocument();
            PDPage myPage = new PDPage();
            doc.addPage(myPage);
            doc.save("src/main/resources/prieteni.pdf");
            doc.save("src/main/resources/mesaje.pdf");
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendNewMessage() {
        String message = txtFieldTypeMessage1.getText();
        vboxSearchResultForNewMsg.getChildren().get(0);
    }

    @FXML
    private void handleSendMessage() {
        String message = txtFieldTypeMessage.getText();
        System.out.println(message);
        if (composeMessageMode) {
            //labelForReplies.toBack();
            rootService.getNetworkService().sendMessage(new MessageDTO(new UserDto<String>(loggedInUsername, "", ""), recipientsList, txtFieldTypeMessage1.getText(), LocalDateTime.now(), 0L));
            System.out.println("compose mode");
            recipientsList.clear();
            displayUserConversationPartners(loggedInUsername);
            composeMessageMode=false;
        } else {
            if (scrollPaneMessages.getHeight() < scrollPaneMessagesHeight) {
                if (labelForReplies.getText().equals("Reply To:")) {
                    rootService.getNetworkService().replyAll(new MessageDTO(new UserDto<String>(labelSenderUserId.getText(), null, null)
                                    , List.of(new UserDto<String>(loggedInUsername, null, null))
                                    , labelMessageRepliedTo.getText()
                                    , LocalDateTime.now()
                                    , Long.parseLong(labelSelectedMessageId.getText()))
                            , loggedInUsername
                            , message);
                } else {
                    if (labelForReplies.getText().equals("Reply All:")) {
                        MessageDTO original = rootService.getNetworkService().findOneMessageById(Long.parseLong(labelSelectedMessageId.getText()));
                        rootService.getNetworkService().replyAll(original, loggedInUsername, message);
                    }
                }
            } else {
                rootService.getNetworkService().sendMessage(new MessageDTO(new UserDto<String>(loggedInUsername, null, null)
                        , List.of(new UserDto<String>(currentChatPartnerShowingId, null, null)),
                        message, LocalDateTime.now(), 0L));
            }

            displayUserConversationMessages(currentChatPartnerShowingId);
        }

        txtFieldTypeMessage.clear();
        txtFieldTypeMessage1.clear();
    }


    @FXML
    private void handleSearchUser() {
        if (!textFieldSearchUser.getText().isEmpty()) {
            vboxSearchResult.getChildren().clear();
            try {
                List<FriendshipDto<String>> userFriends = rootService.getNetworkService().getFriendshipList(labelUsername.getText());
                for (UserDto<String> userDto : getUserNamesStartingWith(textFieldSearchUser.getText())) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("userDetailsBox.fxml"));
                    try {
                        HBox hBox = fxmlLoader.load();
                        UserDetailsBoxController controller = fxmlLoader.getController();
                        controller.setRootService(rootService);
                        controller.setLoggedInUserEmail(labelUsername.getText());
                        if (!userDto.getUserID().equals(labelUsername.getText())) {
                            Predicate<FriendshipDto<String>> isBetweenUsersFriends = frienshipDto -> frienshipDto.getUser1().getUserID().equals(userDto.getUserID())
                                    || frienshipDto.getUser2().getUserID().equals(userDto.getUserID());

                            if (userFriends.stream().anyMatch(isBetweenUsersFriends)) {
                                controller.setData(userDto, 0);
                            } else {
                                FriendshipRequestDTO<String> friendshipRequestDTO = rootService.getNetworkService().existsPendingFriendshipRequest(new Tuple<String, String>(labelUsername.getText(), userDto.getUserID()));
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
                        } else {
                            controller.setData(userDto, 4);
                        }
                        vboxSearchResult.getChildren().add(hBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                vboxSearchResult.toFront();
            } catch (InsufficientDataToExecuteTaskException | RepoError e) {
                e.printStackTrace();
            }

        }

    }

    public void addRecipient(UserDto<String> userDto) {
        recipientsList.add(userDto);
    }

    @FXML
    private void handleSearchUserForNewMsg() throws IOException, NoSuchFieldException, IllegalAccessException {
        if (!texfFieldSearchUserForNewMsg.getText().isEmpty()) {
            vboxSearchResultForNewMsg.getChildren().clear();
            for (UserDto<String> userDto : getUserNamesStartingWith(texfFieldSearchUserForNewMsg.getText())) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("userDetailsBoxForNewMsg.fxml"));
                HBox hBox = fxmlLoader.load();
                UserDetailsBoxForNewMsgController controller = fxmlLoader.getController();
                controller.setDashboardController(this);
                controller.setData(userDto, 0, txtFieldTypeMessage1.getText());
                vboxSearchResultForNewMsg.getChildren().add(hBox);
            }


        }

    }

    @FXML
    private void handleDeleteUser() {
        UserDto<String> selectedUser = tabviewFriends.getSelectionModel().getSelectedItem();
        rootService.getNetworkService().deleteFriendship(selectedUser.getUserID(), labelUsername.getText());
        //displayUserFriends(labelUsername.getText());

    }

    @FXML
    private void handleAcceptImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if (tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO<String> selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO<String> friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.APPROVED);
            rootService.getNetworkService().updateFriendshipRequestStatus(friendshipRequestDTO);
            //displayUserFriendsRequests(labelUsername.getText());
            page.setFrCount(rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(loggedInUsername).size());
            requestNotifyCircle();
        }
    }

    @FXML
    private void handleDeclineImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if (tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO<String> selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO<String> friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.REJECTED);
            rootService.getNetworkService().updateFriendshipRequestStatus(friendshipRequestDTO);
            displayUserFriendsRequests(labelUsername.getText());
            page.setFrCount(rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(loggedInUsername).size());
            requestNotifyCircle();
        }
    }


    private void displayUserFriends(String userEmail) {
        tabviewFriends.getItems().clear();
        tabcolEmail.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tabcolFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tabcolLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        try {

            for (FriendshipDto<String> friendshipDto : rootService.getNetworkService().getFriendshipList(userEmail)) {
                UserDto<String> userDto;
                if (friendshipDto.getUser1().getUserID().equals(userEmail))
                    userDto = friendshipDto.getUser2();
                else
                    userDto = friendshipDto.getUser1();
                tabviewFriends.getItems().add(new UserDto<String>(userDto.getUserID(), userDto.getFirstName(), userDto.getLastName()));
            }
        } catch (InsufficientDataToExecuteTaskException | RepoError e) {
            e.printStackTrace();
        }
        //pnlFriends.toFront();
    }

    private void displayUserFriendsRequests(String userEmail) {
        tabviewRequests.getItems().clear();
        tbl_nume.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbl_prenume.setCellValueFactory(new PropertyValueFactory<>("surname"));
        tbl_data.setCellValueFactory(new PropertyValueFactory<>("status"));
        tbl_status.setCellValueFactory(new PropertyValueFactory<>("date"));
        for (FriendshipRequestDTO<String> friendshipRequestDTO : rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(userEmail)) {
            tabviewRequests.getItems().add(new FriendshipRequestForDisplayUseDTO<String>(friendshipRequestDTO.getFrom().getFirstName(), friendshipRequestDTO.getFrom().getLastName(), friendshipRequestDTO.getStatus(), friendshipRequestDTO.getDate(), friendshipRequestDTO));
        }
        //pnlFriendRequests.toFront();
    }

    public void displayUserConversationPartners(String userEmail) {
        vboxConversationPartners.getChildren().clear();
        vboxConversationPartners.setSpacing(7);
        for (UserDto<String> userDto : rootService.getNetworkService().getAllUserConversationPartners(userEmail)) {
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


    private void resizeTextArea(TextArea textArea) {

    }


    public void displayUserConversationMessages(String conversationPartnerEmail) {
        this.currentChatPartnerShowingId = conversationPartnerEmail;
        vboxMessagesText.getChildren().clear();
        vboxMessagesText.setSpacing(8);
        List<MessageDTO> conversation = rootService.getNetworkService().getConversationHistory(conversationPartnerEmail, loggedInUsername);
        ConversationDTO conversationDTO = new ConversationDTO(
                new UserDto<String>(conversationPartnerEmail, null, null)
                , new UserDto<String>(loggedInUsername, null, null)
                , 0L);
        int nrOfUnreadMessages = rootService.getNetworkService().findOneConversationUnreadMessages(conversationDTO);
        int conversationUnread = conversation.size();
        int count = 0;
        int unreadMessageSign = conversationUnread - nrOfUnreadMessages + 1;
        for (MessageDTO messageDTO : conversation) {
            count++;
            if (nrOfUnreadMessages > 0 && count == unreadMessageSign) {
                VBox vboxUnreadSign = new VBox();
                vboxUnreadSign.setStyle("-fx-border-color: #bcb3b3;\n" +
                        "    -fx-border-width: 0px 0px 2px 0px;");
                Label unreadMessage = new Label("Unread messages");
                unreadMessage.setAlignment(Pos.BOTTOM_CENTER);
                VBox.setMargin(vboxUnreadSign, new Insets(0, 20, 0, 20));
                vboxUnreadSign.getChildren().add(unreadMessage);
                vboxMessagesText.getChildren().add(vboxUnreadSign);

            }
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
            MessageDTO messageRepliedTo = rootService.getNetworkService().repliesTo(messageDTO.getId());
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
            vboxMessagesText.heightProperty().addListener(new ChangeListener() {

                @Override
                public void changed(ObservableValue observable, Object oldvalue, Object newValue) {

                    scrollPaneMessages.setVvalue((Double) newValue);
                }
            });
        }
    }

    private List<UserDto<String>> getUserNamesStartingWith(String startsWith) {
        Predicate<UserDto<String>> userDtoPredicateStartsWith = stringUserDto -> stringUserDto.getFirstName().startsWith(startsWith) || stringUserDto.getLastName().startsWith(startsWith);
        return rootService.getNetworkService().getAllUsers().stream().filter(userDtoPredicateStartsWith).collect(Collectors.toList());
    }

    @Override
    public void update(Event event) {
        displayUserFriendsRequests(labelUsername.getText());
        displayUserConversationPartners(loggedInUsername);
        displayUserFriends(labelUsername.getText());
    }

    private void generate_PDF_for_friends_and_messages() throws RepoError, InsufficientDataToExecuteTaskException {
        String Line;
        List<FriendshipDto> friendshipDtoList = rootService.getNetworkService().getFriendshipsByDate(loggedInUsername,from_date_select.getValue().atStartOfDay(),to_date_select.getValue().atStartOfDay());
        List<UserDto<String>> userDtoList = rootService.getNetworkService().getAllUserConversationPartners(loggedInUsername);
        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage();
            doc.addPage(myPage);

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.beginText();

                cont.setFont(PDType1Font.TIMES_ROMAN, 18);
                cont.setLeading(14.5f);

                cont.newLineAtOffset(10, 700);
                cont.showText("Activity from "+from_date_select.getValue()+" and before " + to_date_select.getValue()+ ":");
                cont.newLine();
                cont.newLine();
                cont.newLine();
                if (friendshipDtoList.size()!=0) {

                    if (friendshipDtoList.size()!=1) cont.showText("You have "+ friendshipDtoList.size() +" new friends:");
                    else cont.showText("You have "+ friendshipDtoList.size() +" new friend:");
                    cont.newLine();

                    for (FriendshipDto<String> friendshipDto : friendshipDtoList) {
                        if (friendshipDto.getUser1().getUserID().equals(loggedInUsername))
                            Line = friendshipDto.getUser2().getFirstName() + " " + friendshipDto.getUser2().getLastName();
                        else
                            Line = friendshipDto.getUser1().getFirstName() + " " + friendshipDto.getUser1().getLastName();
                        cont.showText(Line);
                        cont.newLine();
                    }
                }
                else {
                    cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                    cont.setLeading(14.5f);
                    Line = "0 new friends...";
                    cont.showText(Line);
                    cont.newLine();
                }
                cont.newLine();
                cont.newLine();
                cont.showText("New messages:");
                cont.newLine();

                for (UserDto<String> userDto : userDtoList){
                    List <MessageDTO> messageDTOList = rootService.getNetworkService().getConversationHistoryByDate(loggedInUsername, userDto.getUserID(),from_date_select.getValue().atStartOfDay(),to_date_select.getValue().atStartOfDay());
                    if (messageDTOList.size()!=0){
                        cont.showText(userDto.getUserID()+" : "+messageDTOList.size());
                        cont.newLine();
                    }
                }

                cont.endText();

            } catch (IOException e) {
                e.printStackTrace();
            }
            doc.save("src/main/resources/prieteni.pdf");
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void generate_PDF_for_new_messages(){
        String Line;

        try (PDDocument doc = new PDDocument()) {

            PDPage myPage = new PDPage();
            doc.addPage(myPage);

            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                cont.beginText();
                cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                cont.setLeading(14.5f);
                cont.newLineAtOffset(235, 700);

                List<MessageDTO> messageDTOList= rootService.getNetworkService().getConversationHistoryByDate(loggedInUsername, friend_email_label.getText(),from_date_select.getValue().atStartOfDay(),to_date_select.getValue().atStartOfDay());
                if (messageDTOList.size()!=0){
                    for (MessageDTO messageDTO : rootService.getNetworkService().getConversationHistoryByDate(loggedInUsername, friend_email_label.getText(),from_date_select.getValue().atStartOfDay(),to_date_select.getValue().atStartOfDay())) {
                        Line = messageDTO.getFrom().getFirstName() + " " + messageDTO.getFrom().getLastName() + " : " + messageDTO.getMessage();
                        cont.showText(Line);
                        cont.newLine();
                    }
                }
                else{
                    cont.setFont(PDType1Font.TIMES_ROMAN, 16);
                    cont.setLeading(14.5f);
                    Line = "No messages!";
                    cont.showText(Line);
                }
                cont.endText();

            } catch (IOException e) {
                e.printStackTrace();
            }
            doc.save("src/main/resources/mesaje.pdf");
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}