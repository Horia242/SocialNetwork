package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import ro.ubbcluj.map.model.ConversationDTO;
import ro.ubbcluj.map.model.MessageDTO;
import ro.ubbcluj.map.model.UserDto;

import java.util.List;

public class ConversationPartnerDetailsController {

    private RootService rootService;
    private String loggedInUsername;
    private UserDto<String> currentChatPartner;
    DashboardController dashboardController;
    @FXML
    private Label labelFirstLastName;
    @FXML
    private Label labelUserEmail;
    @FXML
    private Label labelLastMessageText;
    @FXML
    private Pane paneNotifCircle;
    @FXML
    private Label labelNrNotifications;

    public void init(RootService rootService,String loggedInUsername,UserDto<String> currentChatPartner,DashboardController dashboardController){
            this.rootService = rootService;
            this.loggedInUsername = loggedInUsername;
            this.currentChatPartner= currentChatPartner;
            this.dashboardController = dashboardController;
            labelUserEmail.setText(currentChatPartner.getUserID());
        labelFirstLastName.setText(currentChatPartner.getFirstName()+" "+currentChatPartner.getLastName());
        List<MessageDTO> messageDTOList = rootService.getNetworkService().getConversationHistory(loggedInUsername,currentChatPartner.getUserID());
        labelLastMessageText.setText(messageDTOList.get(messageDTOList.size()-1).getMessage());
        handleConversationUnreadMessagesNotifications();

    }
    public void setRootService(RootService rootService){this.rootService = rootService;}
    public void setLoggedInUsername(String loggedInUsername){this.loggedInUsername = loggedInUsername;}

    @FXML
    public void handleOnMouseClicked(){
        dashboardController.displayUserConversationMessages(currentChatPartner.getUserID());
        dashboardController.setLabelMessageSenderUsername("From:"+currentChatPartner.getUserID());
        ConversationDTO conversationDTO = new ConversationDTO(
                new UserDto<String>(currentChatPartner.getUserID(),null,null)
                ,new UserDto<String>(loggedInUsername,null,null)
                ,0L);
        rootService.getNetworkService().setConversationLastReadMessage(conversationDTO);

        handleConversationUnreadMessagesNotifications();
    }

    private void handleConversationUnreadMessagesNotifications(){
        ConversationDTO conversationDTO = new ConversationDTO(
                new UserDto<String>(currentChatPartner.getUserID(),null,null)
                ,new UserDto<String>(loggedInUsername,null,null)
                ,0L);
        int NrOfUnreadMessages = rootService.getNetworkService().findOneConversationUnreadMessages(conversationDTO);
        if(NrOfUnreadMessages > 0 ){
            paneNotifCircle.setStyle("-fx-background-color: red;\n" +
                    "-fx-background-radius: 100%;");
            labelNrNotifications.setText(String.valueOf(NrOfUnreadMessages));
        }
        else {
            paneNotifCircle.setStyle("-fx-background-color:  #FAF6D4;\n" +
                    "-fx-background-radius: 100%;");
        }

    }

}
