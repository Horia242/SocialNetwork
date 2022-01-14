package com.example.ex2;
import com.example.ex2.rootService.RootService;
import com.example.ex2.utils.FriendshipRequestForDisplayUseDTO;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.effect.BoxBlur;
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
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.w3c.dom.Document;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;
import ro.ubbcluj.map.repository.paging.PageRequest;
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
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class DashboardController  implements Observer<NetworkServiceTask>{

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
    private boolean stopLoadingSearchResults = false;
    private boolean ok=true;
    private boolean onUpcomingEventDisplay;
    private List<UserDto<String>> recipientsList = new ArrayList<>();
    @FXML
    private TextField friend_email_label;
    private final int scrollPaneMessagesHeight = 417;
    private final int vboxMessagesTextHeight = 413;
    private String currentChatPartnerShowingId;
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private int sendMessagePlusSignClickCount = 0;
    private LocalDate currentDatePicked;
    private Page page;
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
    private ImageView btnSendMsg;
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
    private ImageView imgSendMessage1;
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
    private Label labelUpcomingEventsNumber;
    @FXML
    private ScrollPane scrollPaneConversations;
    @FXML
    private ScrollPane scrollPaneSearch;
    @FXML
    private HBox hboxFriends;
    String hovered = "";
    @FXML
    private HBox hboxRequests;
    @FXML
    private ScrollPane scrollPaneMessages1;
    @FXML
    private HBox hboxEvent;
    @FXML
    private ImageView imgEvent;
    @FXML
    private Label labelEvent;
    @FXML
    private Pane paneEvents;
    @FXML
    private Pane paneEventsDetails;
    @FXML
    private DatePicker datePickerEvents;
    @FXML
    private TextField txtFieldEventDescription;
    @FXML
    private ImageView imgCreateEvent;
    @FXML
    private ImageView imgGoBackEventsInfo;
    @FXML
    private Pane panePlanEvent;
    @FXML
    private DatePicker datePickerEventsInfo;
    @FXML
    private VBox vboxEvents;
    @FXML
    private ScrollPane paneDisplayEvents;
    @FXML
    private Circle circleUpcomingEventsNr;
    @FXML
    private Label txtUpcomingEventsCount;
    @FXML
    private Pane paneUserMessageEvents;
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

    private Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {

        @Override
        public DateCell call(final DatePicker datePicker) {
            return new DateCell() {
                @Override public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    //if flag is true and date is within range, set style
                    Optional<EventDTO> answer = rootService.getNetworkServicePag()
                                .findAllEvents()
                                .stream().filter(eventDTO -> item.isEqual(eventDTO.getEventDate()))
                                .findFirst();
                    if(answer.isPresent()){
                        setStyle("-fx-background-color: #C3E9F2;");
                    }
                }
            };
        }
    };



    public void init() {

        movableDashboard();
        page = new Page(rootService.getNetworkService().findUserById(loggedInUsername)
                ,rootService.getNetworkService().getUnreadMessagesCount(loggedInUsername)
                ,rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(loggedInUsername).size());
        requestNotifyCircle();
            messageNotifyCircle();
        upcomingEventsNotifyCircle();
        upcomingEventsStartNotification();
        toolTip();

        System.out.println(datePickerEvents.getChronology().dateNow());
           datePickerEvents.setDayCellFactory(dayCellFactory);
           datePickerEventsInfo.setDayCellFactory(dayCellFactory);
           datePickerEventsInfo.setOnAction(event -> {
               currentDatePicked = datePickerEventsInfo.getValue();
               displayEventsInAGivenDate(currentDatePicked);

           });

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
            if (newValue.doubleValue() == 1.0 && !stopLoadingConversations) {
                System.out.println("Bottom!");
                loadNextConversationPartners();
            }
        });
        scrollPaneSearch.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0 && !stopLoadingSearchResults) {
                System.out.println("Bottom - results search!");
                continueDisplayingSearchResults();
            }
        });
        btnSendMsg.setOnMouseClicked(mouseEvent ->
                {
                    if(sendMessagePlusSignClickCount == 0){
                        pnlConversation.toBack();
                        pnlSendMsg.toFront();
                        composeMessageMode = true;
                        this.recipientsList.clear();
                        vboxSearchResultForNewMsg.getChildren().clear();
                        txtFieldTypeMessage1.clear();
                        texfFieldSearchUserForNewMsg.clear();
                        sendMessagePlusSignClickCount = 1;
                    }
                    else{
                        sendMessagePlusSignClickCount = 0;
                        composeMessageMode = false;
                        pnlConversation.toFront();
                        this.recipientsList.clear();
                        vboxMessagesText.toFront();
                    }
                }
                );
        text_field_welcome.setText("Welcome "+rootService.getNetworkService().findUserById(loggedInUsername).getFirstName()+"!");
        text_field_welcome.setFont(Font.font("Comic Sans MS",20));
    }

    public void setOpenedConversationController(ConversationPartnerDetailsController openedConversationController) {
        this.openedConversationController = openedConversationController;
        recipientsList = new ArrayList<>();
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

    private void upcomingEventsStartNotification(){
        int upcomingNr = rootService.getNetworkServicePag().getUpcomingEventsForOneUser(new UserDto<String>(loggedInUsername,null,null)).size();
        if(upcomingNr > 0){
            LocatedImage img = new LocatedImage("icons/icons8_timetable_30px.png");
            String evPluralOrSingular = "event";
            if(upcomingNr > 1) evPluralOrSingular = "events";
            Notifications upcomingEvents = Notifications.create()
                    .title("Schedule notification")
                    .text("You have " + String.valueOf(upcomingNr) + " upcoming " + evPluralOrSingular + ".")
                    .hideAfter(Duration.INDEFINITE)
                    .graphic(new ImageView(img))
                    .position(Pos.CENTER)
                    .hideCloseButton()
                    .onAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            rootAnchorPane.setEffect(null);
                        }
                    });
            BoxBlur blur = new BoxBlur(3,3,3);
            rootAnchorPane.setEffect(blur);
            upcomingEvents.show();

        }
    }

    private void upcomingEventsNotifyCircle(){
        int upcomingNr = rootService.getNetworkServicePag().getUpcomingEventsForOneUser(new UserDto<String>(loggedInUsername,null,null)).size();
        if(upcomingNr > 0){
            circleUpcomingEventsNr.setFill(Paint.valueOf("#ee0d06"));
            txtUpcomingEventsCount.setText(String.valueOf(upcomingNr));
        }
        else{
            circleUpcomingEventsNr.setFill(Paint.valueOf("#eaeae9"));
        }
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
    private void toolTip() {
        Tooltip tooltipAcceptFriendshipRequest = new Tooltip("Accept request");
        Tooltip tooltipDeclineFriendshipRequest = new Tooltip("Decline request");
        Tooltip tooltipDeleteFriend = new Tooltip("Delete friend");
        Tooltip.install(btn_accept, tooltipAcceptFriendshipRequest);
        Tooltip.install(btn_decline, tooltipDeclineFriendshipRequest);
        Tooltip.install(imgDeleteFriend, tooltipDeleteFriend);
        Tooltip.install(btnSignOut, new Tooltip("Sign out"));
        Tooltip.install(btnSignOut1, new Tooltip("Sign out"));
        Tooltip.install(btnSendMsg, new Tooltip("Compose a new message"));
        Tooltip.install(circleUpcomingEventsNr, new Tooltip("You have some upcoming events"));
    }
    private void messageNotifyCircle() {
        page.setMessageCount(rootService.getNetworkService().getUnreadMessagesCount(loggedInUsername));
        if (page.getMessageCount() > 0) {
            circleMessageNumber.setFill(Paint.valueOf("#ee0d06"));
            txtMessageCount.setText(String.valueOf(page.getMessageCount()));
        } else {
            circleMessageNumber.setFill(Paint.valueOf("#eaeae9"));
        }
    }



    public void setRootService(RootService rootService) {
        this.rootService = rootService;
        rootService.getNetworkServicePag().addObserver(this);
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
        //pnlConversation.toFront();
        vboxMessagesText.toFront();
        scrollPaneMessages.toFront();

    }


    private void resetHover(int hboxNr){

        switch (hboxNr) {
            case 0 -> {
                hboxFriends.getStyleClass().add("boxHovered");
                hboxRequests.getStyleClass().add("box1");
                hboxChat.getStyleClass().add("box1");
                hboxEvent.getStyleClass().add("box1");
                hboxPDF.getStyleClass().add("box1");
            }
            case 1 -> {
                hboxFriends.getStyleClass().add("box1");
                hboxRequests.getStyleClass().add("boxHovered");
                hboxChat.getStyleClass().add("box1");
                hboxEvent.getStyleClass().add("box1");
                hboxPDF.getStyleClass().add("box1");
            }
            case 2 -> {
                hboxFriends.getStyleClass().add("box1");
                hboxRequests.getStyleClass().add("box1");
                hboxChat.getStyleClass().add("boxHovered");
                hboxEvent.getStyleClass().add("box1");
                hboxPDF.getStyleClass().add("box1");
            }
            case 3 -> {
                hboxFriends.getStyleClass().add("box1");
                hboxRequests.getStyleClass().add("box1");
                hboxChat.getStyleClass().add("box1");
                hboxEvent.getStyleClass().add("boxHovered");
                hboxPDF.getStyleClass().add("box1");
            }
            case 4 -> {
                hboxFriends.getStyleClass().add("box1");
                hboxRequests.getStyleClass().add("box1");
                hboxChat.getStyleClass().add("box1");
                hboxEvent.getStyleClass().add("box1");
                hboxPDF.getStyleClass().add("boxHovered");
            }
            default -> {
            }
        }
    }
   private void resetStyles(){
        hboxRequests.getStyleClass().clear();
        hboxFriends.getStyleClass().clear();
        hboxChat.getStyleClass().clear();
        hboxEvent.getStyleClass().clear();
        hboxPDF.getStyleClass().clear();
    }

    private boolean composeMessageMode = false;

    @FXML
    private void handleMouseEvent(MouseEvent event) throws RepoError, InsufficientDataToExecuteTaskException {
        if (event.getSource().equals(btnShowFriends) || event.getSource().equals(labelFriends)) {
            displayUserFriends(labelUsername.getText());
            resetStyles();
            resetHover(0);
            stopLoadingConversations = false;
            paneAccountPage.toBack();
            pnlFriends.toFront();

        }
        if (event.getSource().equals(btnShowFriendRequests) || event.getSource().equals(labelFriendRequests)) {
            displayUserFriendsRequests(labelUsername.getText());
            resetStyles();
            resetHover(1);
            pnlFriendRequests.toFront();
            paneAccountPage.toBack();
            requestNotifyCircle();
            pnlFriendRequests.toFront();
            pnlFriends.toBack();
            stopLoadingConversations = false;
        }


        if(event.getSource().equals(textFieldSearchUser)){
            vboxSearchResult.getChildren().clear();
            vboxSearchResult.toBack();
        }
        if(event.getSource().equals(hboxChat)) {
            displayUserConversationPartners(loggedInUsername);
            resetStyles();
            resetHover(2);
            }
            if (event.getSource().equals(textFieldSearchUser)) {
                vboxSearchResult.getChildren().clear();
                vboxSearchResult.toBack();
            }
            if (event.getSource().equals(hboxChat)) {
                composeMessageMode = false;
                displayUserConversationPartners(loggedInUsername);
                messageNotifyCircle();
                pnlFriends.toBack();
                stats_pane.toBack();
                pnlFriendRequests.toBack();
                paneEvents.toBack();
                paneReply.toBack();
                empty_pane.toFront();
                pnlChat.toFront();
                pnlConversation.toFront();
                pane_for_basic_messages.toFront();
            }

            if (event.getSource().equals(hboxEvent) || event.getSource().equals(labelEvent) || event.getSource().equals(imgEvent)) {
                resetStyles();
                resetHover(3);
                paneEvents.toFront();
                paneEventsDetails.toFront();
                displayUpcomingEvents();
                datePickerEventsInfo.getEditor().clear();
            }
            if (event.getSource().equals(imgCreateEvent)) {
                panePlanEvent.toFront();
            }
            if (event.getSource().equals(imgGoBackEventsInfo)) {
                paneEventsDetails.toFront();
            }

                if (event.getSource().equals(hboxPDF) || event.getSource().equals(imgPdf) || event.getSource().equals(labelPdf)) {
                    pnlFriends.toBack();
                    pnlFriendRequests.toBack();
                    pnlConversation.toBack();
                    pnlChat.toBack();
                    pnlSendMsg.toBack();
                    paneAccountPage.toBack();
                    empty_pane.toFront();
                    stats_pane.toFront();
                    resetStyles();
                    resetHover(4);
                }
                if (event.getSource().equals(btnSendMsg)) {
                    pnlConversation.toBack();
                    pnlSendMsg.toFront();
                    composeMessageMode = true;
                }
                if (event.getSource().equals(pdf_generator_for_messages)) {
                    generate_PDF_for_new_messages();
                }
                if (event.getSource().equals(pdf_generator_for_friends)) {
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
    private void handleSendNewMessage() {
        String message = txtFieldTypeMessage1.getText();
        vboxSearchResultForNewMsg.getChildren().get(0);
    }


    @FXML
    private void handleSendMessage() {
        if (composeMessageMode) {
            String message = txtFieldTypeMessage1.getText();
            if(message.length() > 0) {
                rootService.getNetworkServicePag().sendMessage(new MessageDTO(new UserDto<String>(loggedInUsername, "", ""), recipientsList, txtFieldTypeMessage1.getText(), LocalDateTime.now(), 0L));
                composeMessageMode = false;
                pnlFriends.toBack();
                pnlFriendRequests.toBack();
                pnlChat.toFront();
                pnlConversation.toFront();
            }
        } else {
            String message =  txtFieldTypeMessage.getText();

            if (scrollPaneMessages.getHeight() < scrollPaneMessagesHeight) {
                if (labelForReplies.getText().equals("Reply To:")) {
                    rootService.getNetworkServicePag().replyAll(new MessageDTO(new UserDto<String>(labelSenderUserId.getText(), null, null)
                                    , List.of(new UserDto<String>(loggedInUsername, null, null))
                                    , labelMessageRepliedTo.getText()
                                    , LocalDateTime.now()
                                    , Long.parseLong(labelSelectedMessageId.getText()))
                            , loggedInUsername
                            , message);
                } else {
                    if (labelForReplies.getText().equals("Reply All:")) {
                        MessageDTO original = rootService.getNetworkServicePag().findOneMessageById(Long.parseLong(labelSelectedMessageId.getText()));
                        rootService.getNetworkServicePag().replyAll(original, loggedInUsername, message);
                    }
                }
            } else {
                rootService.getNetworkServicePag().sendMessage(new MessageDTO(new UserDto<String>(loggedInUsername, null, null)
                        , List.of(new UserDto<String>(currentChatPartnerShowingId, null, null)),
                        message, LocalDateTime.now(), 0L));
            }

            List<MessageDTO> lastMessageSent = rootService.getNetworkServicePag().getConversationHistory(loggedInUsername, currentChatPartnerShowingId, new PageRequest(0, 1), SortingOrder.DESC);
            dealWithTheLastMessageSent(lastMessageSent.get(0));

            //displayUserConversationMessages(currentChatPartnerShowingId);
            txtFieldTypeMessage.clear();
            displayUserConversationMessages(currentChatPartnerShowingId);
        }

        txtFieldTypeMessage.clear();
        txtFieldTypeMessage1.clear();

    }


    private int pageUsersStartingWith  = 0;
    private int pageSizeUserStartingWith = 3;
    private List<UserDto<String>> getSearchResultsPage(){
        List<UserDto<String>> users = rootService.getNetworkServicePag().getUsersForWhichNameStartsWith(textFieldSearchUser.getText(),new PageRequest(pageUsersStartingWith,pageSizeUserStartingWith));
        if(users == null){stopLoadingSearchResults = true;return null;}
        pageUsersStartingWith += 1;
        return users;
    }

    private void continueDisplayingSearchResults() {
        List<UserDto<String>> users = getSearchResultsPage();
        if (users != null) {
            for (UserDto<String> userDto : users) {
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

                        if (rootService.getNetworkServicePag().areFriends(userDto.getUserID(), loggedInUsername)) {
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
                    } else {
                        controller.setData(userDto, 4);
                    }
                    vboxSearchResult.getChildren().add(hBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
                }




    @FXML
    private void handleSearchUser(){
        stopLoadingSearchResults = false;
        pageUsersStartingWith  = 0;
        vboxSearchResult.getChildren().clear();
        if(!textFieldSearchUser.getText().isEmpty()) {
            continueDisplayingSearchResults();
            vboxSearchResult.toFront();
        }
    }

    public void addRecipient(UserDto<String> userDto) {
        recipientsList.add(userDto);
    }

    @FXML
    private void handleSearchUserForNewMsg() throws IOException, NoSuchFieldException, IllegalAccessException {
                                    if (!texfFieldSearchUserForNewMsg.getText().isEmpty()) {
                                        vboxSearchResultForNewMsg.getChildren().clear();
                                        List<UserDto<String>> users = rootService.getNetworkServicePag().getUsersForWhichNameStartsWith(texfFieldSearchUserForNewMsg.getText(), new PageRequest(0, 8));
                                        if (users != null) {
                                            for (UserDto<String> userDto : users) {
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
                                }

    @FXML
    private void handleDeleteUser() {
        UserDto<String> selectedUser = tabviewFriends.getSelectionModel().getSelectedItem();
        rootService.getNetworkServicePag().deleteFriendship(selectedUser.getUserID(),labelUsername.getText());
        //displayUserFriends(labelUsername.getText());

    }

    @FXML
    private void handleAcceptImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if (tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO<String> selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO<String> friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.APPROVED);
            rootService.getNetworkServicePag().updateFriendshipRequestStatus(friendshipRequestDTO);
           // displayUserFriendsRequests(labelUsername.getText());
          //  page.setFrCount(rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(loggedInUsername).size());

            requestNotifyCircle();
        }
    }

    @FXML
    private void handleDeclineImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if (tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO<String> selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO<String> friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.REJECTED);
            rootService.getNetworkServicePag().updateFriendshipRequestStatus(friendshipRequestDTO);
            displayUserFriendsRequests(labelUsername.getText());
            page.setFrCount(rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(loggedInUsername).size());
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
    private void continueAddingFriendsWhenScrollReachesBottom() {
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

    private void displayUserFriends (String userEmail){
                    friendsDisplayCurrentPage = 0;
                    tabviewFriends.getItems().clear();
                    tabcolEmail.setCellValueFactory(new PropertyValueFactory<>("userID"));
                    tabcolFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
                    tabcolLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
                    continueAddingFriendsWhenScrollReachesBottom();
                    pnlFriends.toFront();

                }

 private int requestsPageSize = 10;
    private List<FriendshipRequestDTO<String>> getRequestsPage () {
                    List<FriendshipRequestDTO<String>> all = rootService.getNetworkServicePag().getAllPendingFriendshipRequestForOneUser(loggedInUsername, new PageRequest(requestsDisplayCurrentPage, requestsPageSize));
                    if (all == null) return null;
                    requestsDisplayCurrentPage += 1;
                    return all;
                }
                private void continueAddingRequestsWhenScrollReachesBottom () {
                    List<FriendshipRequestDTO<String>> all = getRequestsPage();
                    if (all != null) {
                        for (FriendshipRequestDTO<String> friendshipRequestDTO : all) {
                            tabviewRequests.getItems().add(new FriendshipRequestForDisplayUseDTO<String>(friendshipRequestDTO.getFrom().getFirstName(), friendshipRequestDTO.getFrom().getLastName(), friendshipRequestDTO.getStatus(), friendshipRequestDTO.getDate(), friendshipRequestDTO));
                        }
                    }
                }
  private void displayUserFriendsRequests (String userEmail){
                    tabviewRequests.getItems().clear();

                    requestsDisplayCurrentPage = 0;
                    if (initFriendshipRequestDisplay) {
                        tbl_nume.setCellValueFactory(new PropertyValueFactory<>("name"));
                        tbl_prenume.setCellValueFactory(new PropertyValueFactory<>("surname"));
                        tbl_data.setCellValueFactory(new PropertyValueFactory<>("status"));
                        tbl_status.setCellValueFactory(new PropertyValueFactory<>("date"));
                        continueAddingRequestsWhenScrollReachesBottom();
                        pnlFriendRequests.toFront();

                        tbl_nume.setCellValueFactory(new PropertyValueFactory<>("name"));
                        tbl_prenume.setCellValueFactory(new PropertyValueFactory<>("surname"));
                        tbl_data.setCellValueFactory(new PropertyValueFactory<>("status"));
                        tbl_status.setCellValueFactory(new PropertyValueFactory<>("date"));
                        for (FriendshipRequestDTO<String> friendshipRequestDTO : rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(userEmail)) {
                            tabviewRequests.getItems().add(new FriendshipRequestForDisplayUseDTO<String>(friendshipRequestDTO.getFrom().getFirstName(), friendshipRequestDTO.getFrom().getLastName(), friendshipRequestDTO.getStatus(), friendshipRequestDTO.getDate(), friendshipRequestDTO));
                        }
                    }
                }
   private List<UserDto<String>> getCurrentConversationPage () {
                        List<UserDto<String>> all = rootService.getNetworkServicePag().getAllUserConversationPartners(loggedInUsername, new PageRequest(conversationsDisplayCurrentPage, conversationDisplayPageSize));
                        if (all == null) {
                            stopLoadingConversations = true;
                            return null;
                        }
                        conversationsDisplayCurrentPage += 1;
                        return all;
                    }
   private void loadNextConversationPartners () {
                        List<UserDto<String>> all = getCurrentConversationPage();
                        if (all != null) {
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

                        public void displayUserConversationPartners (String userEmail){
                            conversationsDisplayCurrentPage = 0;
                            vboxConversationPartners.getChildren().clear();
                            vboxConversationPartners.setSpacing(7);
                            loadNextConversationPartners();
                        }

                        private void updateScrollPaneView (String conversationPartnerEmail, List < MessageDTO > messageDTOToAdd){
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
                        private void handleOnMessagePaneScroll () {
                            String[] pair = labelMessageSenderUsername.getText().split(":");
                            int pageNumber = openedConversationController.getCurrentPage() + 1;
                            openedConversationController.setCurrentPage(pageNumber);
                            int pageSize = (pageNumber + 1) * openedConversationController.getPageSize();
                            List<MessageDTO> result = rootService.getNetworkServicePag().getConversationHistory(pair[1], loggedInUsername, new PageRequest(0, pageSize), SortingOrder.DESC);
                            if (result != null) {
                                updateScrollPaneView(pair[1], result);
                                currentDisplayedConversationFullLoaded = true;
                            }
                        }

                        @FXML
                        private void scrollingStarted () {
                            scrollPaneMessages.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue.doubleValue() == 0) {
                                    handleOnMessagePaneScroll();

                                }
                            });
                        }

                        public void displayUserConversationMessages (String conversationPartnerEmail){
                            this.scrollTop = false;
                            this.currentChatPartnerShowingId = conversationPartnerEmail;
                            labelMessageSenderUsername.setText("From:" + currentChatPartnerShowingId);
                            vboxMessagesText.getChildren().clear();
                            currentDisplayedConversationFullLoaded = false;
                            vboxMessagesText.setSpacing(8);
                            handleOnMessagePaneScroll();
                            scrollPaneMessages.setVvalue(scrollPaneMessages.getVmax());
                            scrollPaneMessages.toFront();
                            scrollPaneMessages.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                                if (newValue.doubleValue() == 1.0 && scrollBottom) {
                                    System.out.println("Bottom!");
                                    scrollBottom = false;
                                    scrollingStarted();
                                }
                            });
                            vboxMessagesText.heightProperty().addListener(new ChangeListener<Number>() {
                                @Override
                                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                                    if (!scrollTop) {
                                        scrollPaneMessages.setVvalue((Double) newValue);
                                        scrollTop = true;
                                        scrollPaneMessages.setVvalue((Double) newValue);
                                    }
                                }
                            });
                        }
                        @FXML
                        private AnchorPane rootAnchorPane;

                        @FXML
                        public void saveEvent () {
                            if (!txtFieldEventDescription.getText().isEmpty() && datePickerEvents.getValue() != null) {
                                if (datePickerEvents.getValue().compareTo(LocalDate.now()) < 0) {
                                    Notifications incorrectDate = Notifications.create()
                                            .title("Invalid date")
                                            .text("You shouldn't pick a day in the past")
                                            .hideAfter(Duration.seconds(4))
                                            .position(Pos.CENTER);
                                    incorrectDate.show();
                                } else {
                                    rootService.getNetworkServicePag().saveEvent(new EventDTO(0L, txtFieldEventDescription.getText(), datePickerEvents.getValue(), new ArrayList<>()));
                                    txtFieldEventDescription.clear();
                                    datePickerEvents.getEditor().clear();
                                    LocatedImage img = new LocatedImage("icons/icons8_Done_30px.png");
                                    Notifications succesNotif = Notifications.create()
                                            .title("Notification")
                                            .text("You successfully planned an event")
                                            .graphic(new ImageView(img))
                                            .hideAfter(Duration.seconds(6))
                                            .position(Pos.CENTER);

                                    succesNotif.show();
                                }

                            }
                        }


                        private boolean userSubscribedToEvent (EventDTO event){
                            if (event.getSubscribers() == null || event.getSubscribers().size() == 0)
                                return false;
                            return event.getSubscribers().stream().anyMatch(user -> user.getUserID().equals(loggedInUsername));
                        }

                        /**
                         *
                         * @param all must be a valid list of EventDTO's
                         * @param flag is 1 when you wish to display upcoming events,0 to display events within a picked date
                         */
                        private void showEvents (List < EventDTO > all,int flag){
                            paneDisplayEvents.toFront();
                            vboxEvents.getChildren().clear();
                            vboxEvents.setSpacing(8L);
                            if (flag == 1) vboxEvents.getChildren().add(new Label("UPCOMING EVENTS:"));
                            for (EventDTO eventDTO : all) {
                                if (userSubscribedToEvent(eventDTO)) {
                                    Pane unsubscribe = new Pane();
                                    //unsubscribe view
                                    try {
                                        FXMLLoader fxmlLoaderUnsubscribe = new FXMLLoader();
                                        fxmlLoaderUnsubscribe.setLocation(getClass().getResource("unsubscribeEventBox.fxml"));
                                        unsubscribe = fxmlLoaderUnsubscribe.load();
                                        UnsubscribeController unsubscribeController = fxmlLoaderUnsubscribe.getController();
                                        unsubscribeController.initRootService(this.rootService);
                                        unsubscribeController.initLoggedInUsername(this.loggedInUsername);
                                        unsubscribeController.initEventInfos(eventDTO.getDescription(), eventDTO.getEventDate(), eventDTO.getId());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    vboxEvents.getChildren().add(unsubscribe);
                                } else {
                                    //subscribe view
                                    Pane subscribe = new Pane();
                                    try {
                                        FXMLLoader fxmlLoaderSubscribe = new FXMLLoader();
                                        fxmlLoaderSubscribe.setLocation(getClass().getResource("subscribeEventBox.fxml"));
                                        subscribe = fxmlLoaderSubscribe.load();
                                        SubscribeController subscribeController = fxmlLoaderSubscribe.getController();
                                        subscribeController.initRootService(this.rootService);
                                        subscribeController.initLoggedInUsername(this.loggedInUsername);
                                        subscribeController.initEventInfos(eventDTO.getDescription(), eventDTO.getEventDate(), eventDTO.getId());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    vboxEvents.getChildren().add(subscribe);
                                }
                            }

                        }


                        private void displayUpcomingEvents () {
                            List<EventDTO> all = rootService.getNetworkServicePag().getUpcomingEventsForOneUser(new UserDto<String>(loggedInUsername, null, null));
                            if (all != null && all.size() > 0) {
                                showEvents(all, 1);
                                onUpcomingEventDisplay = true;
                            } else {
                                vboxEvents.getChildren().clear();
                                paneDisplayEvents.toBack();
                                paneUserMessageEvents.toFront();
                            }

                        }

                        private void displayEventsInAGivenDate (LocalDate dateGiven){
                            onUpcomingEventDisplay = false;
                            if (datePickerEventsInfo.getValue() != null) {
                                List<EventDTO> all = rootService.getNetworkServicePag().findAllInAGivenDate(dateGiven);
                                if (all != null && all.size() > 0) {
                                    showEvents(all, 0);
                                }
                            }
                        }

                        @Override
                        public void update (NetworkServiceTask event){

                            switch (event.getEventType()) {
                                case MESSAGE ->{ displayUserConversationPartners(loggedInUsername);messageNotifyCircle();}
                                case FRIENDSHIP -> displayUserFriends(labelUsername.getText());
                                case FriendshipRequests -> displayUserFriendsRequests(labelUsername.getText());
                                case EVENTS_SUBSCRIPTION -> {
                                    if (onUpcomingEventDisplay) {
                                        displayUpcomingEvents();
                                    } else {
                                        displayEventsInAGivenDate(currentDatePicked);
                                    }
                                    upcomingEventsNotifyCircle();
                                }
                                default -> {
                                }
                            }
                        }


                    private List<UserDto<String>> getUserNamesStartingWith (String startsWith){
                        Predicate<UserDto<String>> userDtoPredicateStartsWith = stringUserDto -> stringUserDto.getFirstName().startsWith(startsWith) || stringUserDto.getLastName().startsWith(startsWith);
                        return rootService.getNetworkService().getAllUsers().stream().filter(userDtoPredicateStartsWith).collect(Collectors.toList());
                    }

                    private void generate_PDF_for_friends_and_messages () throws RepoError, InsufficientDataToExecuteTaskException {
                        String Line;
                        List<FriendshipDto> friendshipDtoList = rootService.getNetworkService().getFriendshipsByDate(loggedInUsername, from_date_select.getValue().atStartOfDay(), to_date_select.getValue().atStartOfDay());
                        List<UserDto<String>> userDtoList = rootService.getNetworkService().getAllUserConversationPartners(loggedInUsername);
                        try (PDDocument doc = new PDDocument()) {

                            PDPage myPage = new PDPage();
                            doc.addPage(myPage);

                            try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                                cont.beginText();

                                cont.setFont(PDType1Font.TIMES_ROMAN, 18);
                                cont.setLeading(14.5f);

                                cont.newLineAtOffset(10, 700);
                                cont.showText("Activity from " + from_date_select.getValue() + " and before " + to_date_select.getValue() + ":");
                                cont.newLine();
                                cont.newLine();
                                cont.newLine();
                                if (friendshipDtoList.size() != 0) {

                                    if (friendshipDtoList.size() != 1)
                                        cont.showText("You have " + friendshipDtoList.size() + " new friends:");
                                    else
                                        cont.showText("You have " + friendshipDtoList.size() + " new friend:");
                                    cont.newLine();

                                    for (FriendshipDto<String> friendshipDto : friendshipDtoList) {
                                        if (friendshipDto.getUser1().getUserID().equals(loggedInUsername))
                                            Line = friendshipDto.getUser2().getFirstName() + " " + friendshipDto.getUser2().getLastName();
                                        else
                                            Line = friendshipDto.getUser1().getFirstName() + " " + friendshipDto.getUser1().getLastName();
                                        cont.showText(Line);
                                        cont.newLine();
                                    }
                                } else {
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

                                for (UserDto<String> userDto : userDtoList) {
                                    List<MessageDTO> messageDTOList = rootService.getNetworkService().getConversationHistoryByDate(loggedInUsername, userDto.getUserID(), from_date_select.getValue().atStartOfDay(), to_date_select.getValue().atStartOfDay());
                                    if (messageDTOList.size() != 0) {
                                        cont.showText(userDto.getUserID() + " : " + messageDTOList.size());
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

                    private void generate_PDF_for_new_messages () {
                    String Line;

                    try (PDDocument doc = new PDDocument()) {

                        PDPage myPage = new PDPage();
                        doc.addPage(myPage);

                        try (PDPageContentStream cont = new PDPageContentStream(doc, myPage)) {

                            cont.beginText();
                            cont.setFont(PDType1Font.TIMES_ROMAN, 12);
                            cont.setLeading(14.5f);
                            cont.newLineAtOffset(235, 700);

                            List<MessageDTO> messageDTOList = rootService.getNetworkService().getConversationHistoryByDate(loggedInUsername, friend_email_label.getText(), from_date_select.getValue().minusDays(1L).atStartOfDay(), to_date_select.getValue().plusDays(1L).atStartOfDay());
                            if (messageDTOList.size() != 0) {
                                for (MessageDTO messageDTO : messageDTOList) {
                                    Line = messageDTO.getFrom().getFirstName() + " " + messageDTO.getFrom().getLastName() + " : " + messageDTO.getMessage();
                                    cont.showText(Line);
                                    cont.newLine();
                                }
                            } else {
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