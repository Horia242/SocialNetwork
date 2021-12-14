package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.model.FriendshipRequestDTO;
import ro.ubbcluj.map.model.FrienshipDto;
import ro.ubbcluj.map.model.Tuple;
import ro.ubbcluj.map.model.UserDto;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DashboardController {

    private NetworkService service;
    private String loggedInUsername;
    private RootService rootService;

    @FXML
    private ImageView btnShowFriends;
    @FXML
    private Pane pnlFriends;
    @FXML
    private Label labelUsername;
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
    private HBox hboxFriends;


    private Parent root;
    private Stage stage;
    private Scene scene;

    public DashboardController() {
        init();
    }

    private void init() {

    }


    @FXML
    public void displayUsername(String username){
        labelUsername.setText(username);
    }

    public void setService(NetworkService networkService){
        this.service = networkService;
    }

    @FXML
    private void handleMouseEvent(MouseEvent event){
        if(event.getSource().equals(btnShowFriends) || event.getSource().equals(hboxFriends)){
                displayUserFriends(labelUsername.getText());
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
                root = loader.load();
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
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
                List<FrienshipDto> userFriends = service.getFriendshipList(labelUsername.getText());
            for(UserDto<String> userDto:getUserNamesStartingWith(textFieldSearchUser.getText()))
                {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("userDetailsBox.fxml"));
                    try{
                        HBox hBox = fxmlLoader.load();
                        UserDetailsBoxController controller = fxmlLoader.getController();
                        controller.setService(service);
                        controller.setLoggedInUserEmail(labelUsername.getText());
                        if(!userDto.getUserID().equals(labelUsername.getText())) {
                            Predicate<FrienshipDto> isBetweenUsersFriends = frienshipDto -> frienshipDto.getUser1().getUserID().equals(userDto.getUserID())
                                    || frienshipDto.getUser2().getUserID().equals(userDto.getUserID());

                            if (userFriends.stream().anyMatch(isBetweenUsersFriends)) {
                                controller.setData(userDto, 0);
                            } else {
                                FriendshipRequestDTO<String> friendshipRequestDTO = service.existsPendingFriendshipRequest(new Tuple<String, String>(labelUsername.getText(), userDto.getUserID()));
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
                service.deleteFriendship(selectedUser.getUserID(),labelUsername.getText());
                displayUserFriends(labelUsername.getText());
    }
    private void displayUserFriends(String userEmail){
        tabviewFriends.getItems().clear();
        tabcolEmail.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tabcolFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tabcolLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        try {

            for(FrienshipDto friendshipDto: service.getFriendshipList(userEmail)){
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

    private List<UserDto> getUserNamesStartingWith(String startsWith){
        Predicate<UserDto> userDtoPredicateStartsWith = stringUserDto -> stringUserDto.getFirstName().startsWith(startsWith) || stringUserDto.getLastName().startsWith(startsWith);
        return service.getAllUsers().stream().filter(userDtoPredicateStartsWith).collect(Collectors.toList());
    }

}
