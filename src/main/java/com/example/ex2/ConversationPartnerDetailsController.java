package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    public void init(RootService rootService,String loggedInUsername,UserDto<String> currentChatPartner,DashboardController dashboardController){
            this.rootService = rootService;
            this.loggedInUsername = loggedInUsername;
            this.currentChatPartner= currentChatPartner;
            this.dashboardController = dashboardController;
            labelUserEmail.setText(currentChatPartner.getUserID());
        labelFirstLastName.setText(currentChatPartner.getFirstName()+" "+currentChatPartner.getLastName());
        List<MessageDTO> messageDTOList = rootService.getNetworkService().getConversationHistory(loggedInUsername,currentChatPartner.getUserID());
        labelLastMessageText.setText(messageDTOList.get(messageDTOList.size()-1).getMessage());

    }
    public void setRootService(RootService rootService){this.rootService = rootService;}
    public void setLoggedInUsername(String loggedInUsername){this.loggedInUsername = loggedInUsername;}

    @FXML
    public void handleOnMouseClicked(){
        dashboardController.displayUserConversationMessages(currentChatPartner.getUserID());
        dashboardController.setLabelMessageSenderUsername("From:"+currentChatPartner.getUserID());
    }
}
