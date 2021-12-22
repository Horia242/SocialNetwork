package com.example.ex2;

import com.example.ex2.rootService.RootService;
import com.example.ex2.utils.FriendshipRequestForDisplayUseDTO;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashboardController {

    RootService rootService;
    String loggedInUsername;

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

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    public void init(){
        movableDashboard();
        requestNotifyCircle();
        toolTip();
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
        int requestsNumber = this.rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(labelUsername.getText()).size();
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
        //tooltipAcceptFriendshipRequest.setStyle("-fx-background-color:#E4E9E8; -fx-text-fill: black;");
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

    @FXML
    private void handleMouseEvent(MouseEvent event){
        if(event.getSource().equals(btnShowFriends) || event.getSource().equals(labelFriends)){
            displayUserFriends(labelUsername.getText());
        }
        if(event.getSource().equals(btnShowFriendRequests) || event.getSource().equals(labelFriendRequests)){
            displayUserFriendsRequests(labelUsername.getText());
        }
        if(event.getSource().equals(textFieldSearchUser)){
            vboxSearchResult.getChildren().clear();
            vboxSearchResult.toBack();
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

    @FXML
    private void handleSearchUser(){
        if(!textFieldSearchUser.getText().isEmpty()){
            vboxSearchResult.getChildren().clear();
            try {
                List<FrienshipDto> userFriends = rootService.getNetworkService().getFriendshipList(labelUsername.getText());
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
                            Predicate<FrienshipDto> isBetweenUsersFriends = frienshipDto -> frienshipDto.getUser1().getUserID().equals(userDto.getUserID())
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
        rootService.getNetworkService().deleteFriendship(selectedUser.getUserID(),labelUsername.getText());
        displayUserFriends(labelUsername.getText());

    }

    @FXML
    private void handleAcceptImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if(tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.APPROVED);
            rootService.getNetworkService().updateFriendshipRequestStatus(friendshipRequestDTO);
            displayUserFriendsRequests(labelUsername.getText());
        }
    }

    @FXML
    private void handleDeclineImageView() throws RepoError, InsufficientDataToExecuteTaskException {
        if(tabviewRequests.getSelectionModel().getSelectedItem() != null && tabviewRequests.getSelectionModel().getSelectedItem().getStatus().equals(FriendshipRequestStatus.PENDING)) {
            FriendshipRequestForDisplayUseDTO selectedRequest = tabviewRequests.getSelectionModel().getSelectedItem();
            FriendshipRequestDTO friendshipRequestDTO = selectedRequest.getFriendshipRequestDTO();
            friendshipRequestDTO.setStatus(FriendshipRequestStatus.REJECTED);
            rootService.getNetworkService().updateFriendshipRequestStatus(friendshipRequestDTO);
            displayUserFriendsRequests(labelUsername.getText());
        }
    }

    private void displayUserFriends(String userEmail){
        tabviewFriends.getItems().clear();
        tabcolEmail.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tabcolFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tabcolLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        try {

            for(FrienshipDto friendshipDto: rootService.getNetworkService().getFriendshipList(userEmail)){
                UserDto<String> userDto;
                if(friendshipDto.getUser1().getUserID().equals(userEmail))
                    userDto = friendshipDto.getUser2();
                else
                    userDto = friendshipDto.getUser1();
                tabviewFriends.getItems().add(new UserDto<String>(userDto.getUserID(), userDto.getFirstName(), userDto.getLastName()));
            }
        } catch (InsufficientDataToExecuteTaskException | RepoError e) {
            e.printStackTrace();
        }
        pnlFriends.toFront();
    }

    private void displayUserFriendsRequests(String userEmail) {
        tabviewRequests.getItems().clear();
        tbl_nume.setCellValueFactory(new PropertyValueFactory<>("name"));
        tbl_prenume.setCellValueFactory(new PropertyValueFactory<>("surname"));
        tbl_data.setCellValueFactory(new PropertyValueFactory<>("status"));
        tbl_status.setCellValueFactory(new PropertyValueFactory<>("date"));
        for (FriendshipRequestDTO<String> friendshipRequestDTO : rootService.getNetworkService().getAllPendingFriendshipRequestForOneUser(userEmail)) {
            tabviewRequests.getItems().add(new FriendshipRequestForDisplayUseDTO<String>(friendshipRequestDTO.getFrom().getFirstName(),friendshipRequestDTO.getFrom().getLastName(),friendshipRequestDTO.getStatus(),friendshipRequestDTO.getDate(),friendshipRequestDTO));
        }
        pnlFriendRequests.toFront();
    }

    private List<UserDto> getUserNamesStartingWith(String startsWith){
        Predicate<UserDto> userDtoPredicateStartsWith = stringUserDto -> stringUserDto.getFirstName().startsWith(startsWith) || stringUserDto.getLastName().startsWith(startsWith);
        return rootService.getNetworkService().getAllUsers().stream().filter(userDtoPredicateStartsWith).collect(Collectors.toList());
    }

}
