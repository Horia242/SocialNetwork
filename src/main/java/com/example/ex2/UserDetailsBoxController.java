package com.example.ex2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ro.ubbcluj.map.model.FriendshipRequestDTO;
import ro.ubbcluj.map.model.FriendshipRequestStatus;
import ro.ubbcluj.map.model.UserDto;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;

import java.util.Objects;

public class UserDetailsBoxController {

    private NetworkService service;
    private String loggedInUserEmail ;
    @FXML
    private HBox hboxUserDetails;

    @FXML
    private Label labelEmail;

    @FXML
    private Label labelFirstName;

    @FXML
    private Label labelLastName;
    @FXML
    private ImageView imgSendFriendshipRequest;

    public void setService(NetworkService service) {
        this.service = service;
    }

    public void setLoggedInUserEmail(String loggedInUserEmail){
        this.loggedInUserEmail = loggedInUserEmail;
    }

    /**
     *
     * @param userDto UserDto<String>
     * @param descriptionImageFlag set to 0 if the user that is logged in is a friend of @param userDto
     *                             ,1 if the friendship request between these users is pending
     *                             ,2 if these users aren't friends
     *

     */
    public void setData(UserDto<String> userDto, int  descriptionImageFlag){
        labelFirstName.setText(userDto.getFirstName());
        labelLastName.setText(userDto.getLastName());
        labelEmail.setText(userDto.getUserID());

            Image imgFriendshipStatus;

            if ( descriptionImageFlag == 0) {
                imgFriendshipStatus = new LocatedImage("icons/icons8_ok_30px.png");

            }
            else {
                if(descriptionImageFlag == 1 )
                {
                    imgFriendshipStatus = new LocatedImage("icons/icons8_paper_plane_30px.png");
                }
            else{
                    imgFriendshipStatus = new LocatedImage("icons/icons8_friends_30px_2.png");

                }
            }
            imgSendFriendshipRequest.setImage(imgFriendshipStatus);

    }

    String getImagePath(Image currentImage){
        return currentImage instanceof LocatedImage
                ? ((LocatedImage) currentImage).getPath()
                : null;
    }

    @FXML
    private void handleRequestImage(){
        String url = getImagePath(imgSendFriendshipRequest.getImage());
        if(url.compareTo("icons/icons8_friends_30px_2.png") == 0)
        {
            if(service != null){
                try {
                    service.sendFriendshipRequest(new FriendshipRequestDTO<>(new UserDto<String>(loggedInUserEmail,"",""),
                            new UserDto<String>(labelEmail.getText(),"",""), FriendshipRequestStatus.PENDING));
                    imgSendFriendshipRequest.setImage(new LocatedImage("icons/icons8_paper_plane_30px.png"));
                } catch (InsufficientDataToExecuteTaskException | RepoError e) {
                  //Users are already friends
                }
            }
        }
    }



}
