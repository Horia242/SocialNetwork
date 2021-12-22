package com.example.ex2;
import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import ro.ubbcluj.map.model.FriendshipRequestDTO;
import ro.ubbcluj.map.model.FriendshipRequestStatus;
import ro.ubbcluj.map.model.UserDto;
import ro.ubbcluj.map.Service.NetworkService;
import ro.ubbcluj.map.myException.InsufficientDataToExecuteTaskException;
import ro.ubbcluj.map.myException.RepoError;
import java.time.LocalDate;




public class UserDetailsBoxController  {

    private RootService rootService;
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
    @FXML
    private AnchorPane anchorDashboardRootPane;


    public void setRootService(RootService rootService) {
        this.rootService = rootService;
    }

    public void setLoggedInUserEmail(String loggedInUserEmail){
        this.loggedInUserEmail = loggedInUserEmail;
    }

    /**
     *
     * @param userDto UserDto<String>
     * @param descriptionImageFlag set to 0 if the user that is logged in is a friend of @param userDto
     *                             ,1 if the user logged in  sent a friendship request(that is now pending) to the user with id equal to @param userDto id
     *                             ,2 if these users aren't friends
     *                             ,3 if user logged in has a pending friendship request from userDto
     *                             ,4 if the userDto it's the logged-in user himself

     */
    public void setData(UserDto<String> userDto, int  descriptionImageFlag){
        labelFirstName.setText(userDto.getFirstName());
        labelLastName.setText(userDto.getLastName());
        labelEmail.setText(userDto.getUserID());

            Image imgFriendshipStatus = null;

        switch (descriptionImageFlag) {
            case 0 -> imgFriendshipStatus = new LocatedImage("icons/icons8_ok_30px.png");
            case 1 -> imgFriendshipStatus = new LocatedImage("icons/icons8_paper_plane_30px.png");
            case 2 -> imgFriendshipStatus = new LocatedImage("icons/icons8_plus30px.png");
            case 3 -> imgFriendshipStatus = new LocatedImage("icons/icons8_handshake_orange.png");
            case 4 -> imgFriendshipStatus = new LocatedImage("icons/icons8_adobe_media_encoder_30px_1.png");
            default -> {
            }
        }
            if(imgFriendshipStatus != null) {
                imgSendFriendshipRequest.setImage(imgFriendshipStatus);
            }

    }

    String getImagePath(Image currentImage){
        return currentImage instanceof LocatedImage
                ? ((LocatedImage) currentImage).getPath()
                : null;
    }

    /**
     * Controls the actions for a friendshipRequest for a user found using the searchBar
     */
    @FXML
    private void handleRequestImage(){
        String url = getImagePath(imgSendFriendshipRequest.getImage());
        switch(url){
            case "icons/icons8_plus30px.png":
                if(rootService.getNetworkService() != null){
                    try {
                        rootService.getNetworkService().sendFriendshipRequest(new FriendshipRequestDTO<>(new UserDto<String>(loggedInUserEmail,"",""),
                                new UserDto<String>(labelEmail.getText(),"",""), FriendshipRequestStatus.PENDING, LocalDate.now()));
                        imgSendFriendshipRequest.setImage(new LocatedImage("icons/icons8_paper_plane_30px.png"));
                    } catch (InsufficientDataToExecuteTaskException | RepoError e) {
                        //Users are already friends
                    }
                }
                break;
            case "icons/icons8_handshake_orange.png":
                try {
                    rootService.getNetworkService().updateFriendshipRequestStatus(new FriendshipRequestDTO<>(new UserDto<String>(labelEmail.getText(),"",""),
                            new UserDto<String>(loggedInUserEmail,"",""), FriendshipRequestStatus.APPROVED, null));
                    imgSendFriendshipRequest.setImage(new LocatedImage("icons/icons8_ok_30px.png"));
                    //notify() - pentru main window;


                } catch (InsufficientDataToExecuteTaskException | RepoError e) {
                    e.printStackTrace();
                }
                break;
            case "icons/icons8_paper_plane_30px.png":
                try {
                    if(rootService.getNetworkService().deletePendingFriendshipRequest(
                            new FriendshipRequestDTO<>(new UserDto<String>(loggedInUserEmail,"","")
                            ,new UserDto<String>(labelEmail.getText(),"",""), FriendshipRequestStatus.PENDING, null))){
                        imgSendFriendshipRequest.setImage(new LocatedImage("icons/icons8_plus30px.png"));
                    }
                } catch (InsufficientDataToExecuteTaskException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }



}
