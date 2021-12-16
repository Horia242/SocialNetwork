package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubbcluj.map.Service.FriendshipService;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.Service.UserService;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.repository.FriendshipRequestRepository;
import ro.ubbcluj.map.repository.MessageRepository;
import ro.ubbcluj.map.repository.Repository;
import ro.ubbcluj.map.repository.dbo.FriendshipRepoDbo;
import ro.ubbcluj.map.repository.dbo.FriendshipRequestsDbo;
import ro.ubbcluj.map.repository.dbo.MessageRepoDbo;
import ro.ubbcluj.map.repository.dbo.UserRepoDbo;
import ro.ubbcluj.map.ui.Console;
import ro.ubbcluj.model.validators.FriendshipTupleIdValidator;
import ro.ubbcluj.model.validators.MessagesValidator;
import ro.ubbcluj.model.validators.UserStringIdValidator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HelloApplication extends Application {


    private Connection connection = null;

    private void setDataBaseConnection(String url,String username,String password){
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        String username = "postgres";
        String password = "polopolo123";
        setDataBaseConnection(url,username,password);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        Repository<String, ApplicationUser> repoUser = new UserRepoDbo(connection , new UserStringIdValidator());
        Repository<Tuple<String, String>, Friendship> repoFriendship = new FriendshipRepoDbo(connection, new FriendshipTupleIdValidator());
        FriendshipService serviceFriendship = new FriendshipService(repoFriendship);
        UserService serviceUser = new UserService(repoUser);
        MessageRepository<Long, Message> messageRepository = new MessageRepoDbo(connection);
        FriendshipRequestRepository<Long, FriendshipRequest> RepoFriendshipRequest = new FriendshipRequestsDbo(connection);
        NetworkService service = new NetworkService(serviceFriendship, new FriendshipTupleIdValidator(), serviceUser, new UserStringIdValidator(), messageRepository, new MessagesValidator(), RepoFriendshipRequest);
       AppEventsController c = fxmlLoader.getController();
        c.setRootService(new RootService(service));
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        connection.close();
    }
}