package com.example.ex2;
import com.dlsc.formsfx.model.event.FieldEvent;
import com.example.ex2.rootService.RootService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.scene.layout.Panel;
import ro.ubbcluj.map.Service.FriendshipService;
import ro.ubbcluj.map.Service.UserService;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.repository.FriendshipRequestRepository;
import ro.ubbcluj.map.repository.MessageRepository;
import ro.ubbcluj.map.repository.Repository;
import ro.ubbcluj.map.repository.dbo.FriendshipRepoDbo;
import ro.ubbcluj.map.repository.dbo.FriendshipRequestsDbo;
import ro.ubbcluj.map.repository.dbo.MessageRepoDbo;
import ro.ubbcluj.map.repository.dbo.UserRepoDbo;
import ro.ubbcluj.model.validators.FriendshipTupleIdValidator;
import ro.ubbcluj.model.validators.MessagesValidator;
import ro.ubbcluj.model.validators.UserStringIdValidator;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import ro.ubbcluj.map.Service.NetworkService;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class AppEventsController {

    private Parent root;
    private Stage stage;
    private Scene scene;
    private RootService rootService;

    @FXML
    private HBox btnClose;
    @FXML
    private Button btnSignUp;
    @FXML
    private ImageView btnBack;
    @FXML
    private Button btnGetStarted;
    @FXML
    private Pane pnlSignUp;
    @FXML
    private Pane pnlLogIn;
    @FXML
    private TextField txtFieldEmail;
    @FXML
    private PasswordField passFieldPassword;

   public void setRootService(RootService rootService) {
         this. rootService =rootService;
    }

    @FXML
    private final DashboardController dashboardController = new DashboardController();
    @FXML
    private void handleButtonAction(ActionEvent event){
                if(event.getSource().equals(btnSignUp)) {
                    pnlSignUp.toFront();
                }
                if(event.getSource().equals(btnGetStarted)){
                    //save a new user
                  //  rootService.getNetworkService().addUser()
                }
    }
    @FXML
    private void onLoginButton(ActionEvent event) throws IOException{
        try{
            String txtUsername = txtFieldEmail.getText();
            String password = passFieldPassword.getText();
            if( rootService.getNetworkService().logIN(txtUsername,password) ){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboard.fxml"));
                root = loader.load();
                DashboardController dashboardController = loader.getController();
                dashboardController.setLoggedInUserEmail(txtUsername);
                dashboardController.setRootService(rootService);
                dashboardController.init();
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    @FXML
    private void handleButtonGetStarted(ActionEvent event){
    }
    @FXML
    private void handleMouseEvent(MouseEvent event){
            if(event.getSource() .equals( btnClose)){
               Platform.exit();
            }
            if(event.getSource().equals(btnBack)){
                    pnlLogIn.toFront();
            }
    }

}