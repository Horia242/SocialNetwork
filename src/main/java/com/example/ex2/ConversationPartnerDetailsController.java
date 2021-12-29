package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import ro.ubbcluj.map.model.ConversationDTO;
import ro.ubbcluj.map.model.MessageDTO;
import ro.ubbcluj.map.model.SortingOrder;
import ro.ubbcluj.map.model.UserDto;
import ro.ubbcluj.map.repository.paging.PageRequest;

import java.util.List;

public class ConversationPartnerDetailsController {

    private RootService rootService;
    private String loggedInUsername;
    private UserDto<String> currentChatPartner;
    DashboardController dashboardController;
    private int currentPage = -1;
    private final int pageSize = 12;

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
        List<MessageDTO> lastMessageDTO = rootService.getNetworkServicePag().getConversationHistory(loggedInUsername,currentChatPartner.getUserID(),new PageRequest(0,1), SortingOrder.DESC);
        labelLastMessageText.setText(lastMessageDTO.get(0).getMessage());
        handleConversationUnreadMessagesNotifications();
    }
    public void setRootService(RootService rootService){this.rootService = rootService;}
    public void setLoggedInUsername(String loggedInUsername){this.loggedInUsername = loggedInUsername;}

    @FXML
    public void handleOnMouseClicked(){
        currentPage = 0;
        dashboardController.setOpenedConversationController(this);
        dashboardController.displayUserConversationMessages(currentChatPartner.getUserID());
        dashboardController.setLabelMessageSenderUsername("From:"+currentChatPartner.getUserID());
        ConversationDTO conversationDTO = new ConversationDTO(
                new UserDto<String>(currentChatPartner.getUserID(),null,null)
                ,new UserDto<String>(loggedInUsername,null,null)
                ,0L);
        rootService.getNetworkServicePag().setConversationLastReadMessage(conversationDTO);

        handleConversationUnreadMessagesNotifications();
    }

    public void setCurrentPage(int value){currentPage = value;}
    public int getCurrentPage(){return currentPage;}
    public int getPageSize(){return pageSize;}

    private void handleConversationUnreadMessagesNotifications(){
        ConversationDTO conversationDTO = new ConversationDTO(
                new UserDto<String>(currentChatPartner.getUserID(),null,null)
                ,new UserDto<String>(loggedInUsername,null,null)
                ,0L);
        int NrOfUnreadMessages = rootService.getNetworkServicePag().findOneConversationUnreadMessages(conversationDTO);
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
