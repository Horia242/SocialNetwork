package com.example.ex2;
import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ro.ubbcluj.map.model.FriendshipRequestDTO;
import ro.ubbcluj.map.model.FriendshipRequestStatus;
import ro.ubbcluj.map.model.MessageDTO;
import ro.ubbcluj.map.model.UserDto;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDetailsBoxForNewMsgController {

    private RootService rootService;
    private String loggedInUserEmail;

    @FXML
    private HBox hboxUserDetailsForNewMsg;
    @FXML
    private ImageView imgSendNewMsg;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelFirstName;
    @FXML
    private Label labelLastName;
    @FXML
    private AnchorPane anchorDashboardRootPane;
    private String message;

    public void setRootService(RootService rootService) {
        this.rootService = rootService;
    }


    public void setData(UserDto<String> userDto, int descriptionImageFlag,String message) {
        labelFirstName.setText(userDto.getFirstName());
        labelLastName.setText(userDto.getLastName());
        labelEmail.setText(userDto.getUserID());
        Tooltip imageViewTooltip = new Tooltip("");
        Image imgFriendshipStatus = null;
        this.message = message;
        switch (descriptionImageFlag) {
            case 0 -> {
                imgFriendshipStatus = new LocatedImage("icons/icons8_paper_plane_30px.png");
                imageViewTooltip.setText("You didn't sent the message to this user");
            }
            case 1 -> {
                imgFriendshipStatus = new LocatedImage("icons/accept.png");
                imageViewTooltip.setText("You sent the message to this user");
            }
            default -> {
            }
        }
        if(imgFriendshipStatus != null) {
            imgSendNewMsg.setImage(imgFriendshipStatus);
            Tooltip.install(imgSendNewMsg,imageViewTooltip);
        }
    }

    @FXML
    private void handleSendNewMsg()  {
        if(rootService.getNetworkService() != null){
                List<UserDto<String>> recipients = new ArrayList<>();
                recipients.add(new UserDto<>(labelEmail.getText(), "", ""));
                rootService.getNetworkService().sendMessage(new MessageDTO(new UserDto<>("ex@ex.com", "", ""), recipients, message, LocalDateTime.now(), 0L));
                imgSendNewMsg.setImage(new LocatedImage("icons/accept.png"));
                Tooltip.install(imgSendNewMsg, new Tooltip("You sent the message to this user"));
        }
    }
}


