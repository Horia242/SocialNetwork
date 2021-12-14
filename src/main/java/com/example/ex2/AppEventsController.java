package com.example.ex2;
import com.dlsc.formsfx.model.event.FieldEvent;
import com.example.ex2.rootService.RootService;
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
    private TextField txtFieldPassword;




    @FXML
    private final DashboardController dashboardController = new DashboardController();
    @FXML
    private void handleButtonAction(ActionEvent event){
                if(event.getSource().equals(btnSignUp)) {
                    pnlSignUp.toFront();
                }
                if(event.getSource().equals(btnGetStarted)){
                    /*Repository<String, ApplicationUser> repoUser = new UserRepoDbo("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "polopolo123", new UserStringIdValidator());
                    repoUser.save(new ApplicationUser());*/
                }
    }
    @FXML
    private void onLoginButton(ActionEvent event) throws IOException{
        Repository<String, ApplicationUser> repoUser = new UserRepoDbo("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "polopolo123", new UserStringIdValidator());
        Repository<Tuple<String, String>, Friendship> repoFriendship = new FriendshipRepoDbo("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "polopolo123", new FriendshipTupleIdValidator());
        FriendshipService serviceFriendship = new FriendshipService(repoFriendship);
        UserService serviceUser = new UserService(repoUser);
        MessageRepository<Long, Message> messageRepository = new MessageRepoDbo("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "polopolo123");
        FriendshipRequestRepository<Long, FriendshipRequest> RepoFriendshipRequest = new FriendshipRequestsDbo("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "polopolo123");
        NetworkService service = new NetworkService(serviceFriendship, new FriendshipTupleIdValidator(), serviceUser, new UserStringIdValidator(), messageRepository, new MessagesValidator(), RepoFriendshipRequest);
        try{
            String txtUsername = txtFieldEmail.getText();
            if(service.loginUsername(txtUsername)){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("userDashboard.fxml"));
                root = loader.load();
                DashboardController dashboardController = loader.getController();
                dashboardController.displayUsername(txtUsername);
                dashboardController.setService(service);
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
                System.exit(0);
            }
            if(event.getSource().equals(btnBack)){
                    pnlLogIn.toFront();
            }
    }
}