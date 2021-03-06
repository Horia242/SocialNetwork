package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ro.ubbcluj.map.Service.*;
import ro.ubbcluj.map.model.*;
import ro.ubbcluj.map.repository.AppUserRepository;
import ro.ubbcluj.map.repository.FriendshipRequestRepository;
import ro.ubbcluj.map.repository.MessageRepository;
import ro.ubbcluj.map.repository.Repository;
import ro.ubbcluj.map.repository.dbo.*;
import ro.ubbcluj.map.repository.paging.PagedAppUserRepository;
import ro.ubbcluj.map.repository.paging.PagedFriendshipRepository;
import ro.ubbcluj.map.repository.paging.PagedFriendshipRequestRepository;
import ro.ubbcluj.map.ui.Console;
import ro.ubbcluj.model.validators.FriendshipTupleIdValidator;
import ro.ubbcluj.model.validators.MessagesValidator;
import ro.ubbcluj.model.validators.UserStringIdValidator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
        PagedAppUserRepository<String, ApplicationUser> repoUser = new UserRepoDboPaginated(connection);
        PagedFriendshipRepository<Tuple<String, String>, Friendship> repoFriendship = new FriendshipRepoDboPaginated(connection);
        FriendshipService serviceFriendship = new FriendshipService(repoFriendship);
        UserService serviceUser = new UserService(repoUser);
        MessageRepository<Long, Message> messageRepository = new MessageRepoDbo(connection);
        PagedFriendshipRequestRepository<Long, FriendshipRequest> repoFriendshipRequest = new FriendshipRequestsDboPaginated(connection);
        NetworkService service = new NetworkService(serviceFriendship, new FriendshipTupleIdValidator(), serviceUser, new UserStringIdValidator(), messageRepository, new MessagesValidator(), new FriendshipRequestsDbo(connection),new ConversationDbo(connection));
        NetworkServiceForPaginatedDbRepo service1 = new NetworkServiceForPaginatedDbRepo(new EventsRepoDbo(connection),serviceFriendship, new FriendshipTupleIdValidator(), serviceUser, new UserStringIdValidator(), new MessagesValidator(), repoFriendshipRequest,new ConversationDbo(connection),new MessageService(new MessageRepoDboPaginated(connection)));
        AppEventsController c = fxmlLoader.getController();
        RootService rootService = new RootService(service1);
        rootService.setNetworkService(service);
        c.setRootService(rootService);
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