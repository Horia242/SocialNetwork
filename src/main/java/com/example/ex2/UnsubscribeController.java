package com.example.ex2;

import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ro.ubbcluj.map.model.EventDTO;
import ro.ubbcluj.map.model.UserDto;

import java.time.LocalDate;

public class UnsubscribeController {
    RootService rootService;
    String loggedInUsername;
    @FXML
    private Button btnUnsubscribe;

    @FXML
    private Label labelDateUnsubscribe;

    @FXML
    private Label labelTitleUnsubscribe;

    @FXML
    private Pane paneUnsubscribeEvent;

    @FXML
    private Text txtEventId;


    public void initRootService(RootService rootService){
        this.rootService = rootService;
    }
    public void initLoggedInUsername(String loggedInUsername){
        this.loggedInUsername = loggedInUsername;
    }
    public void initEventInfos(String description, LocalDate localDate, Long eventId){
        this.labelTitleUnsubscribe.setText(description);
        this.labelDateUnsubscribe.setText(localDate.toString());
        this.txtEventId.setText(eventId.toString());
    }

    @FXML
    public void handleOnUnsubscribeButtonClick(){
        rootService.getNetworkServicePag().unsubscribeUserToEvent(new EventDTO(Long.parseLong(txtEventId.getText()),null,null,null)
                                                                    ,new UserDto<String>(loggedInUsername,null,null));
        //notify in service
    }
}
