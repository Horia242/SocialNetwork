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


public class SubscribeController {
    RootService rootService;
    private String loggedInUsername;
    @FXML
    private Button btnSubscribe;

    @FXML
    private Label labelDateSubscribe;

    @FXML
    private Label labelTitleSubscribe;

    @FXML
    private Pane paneSubscribeEvent;

    @FXML
    private Text txtEventId;

    public void initRootService(RootService rootService){
        this.rootService = rootService;
    }
    public void initLoggedInUsername(String loggedInUsername){
        this.loggedInUsername = loggedInUsername;
    }
    public void initEventInfos(String description, LocalDate localDate,Long eventId){
        this.labelTitleSubscribe.setText(description);
        this.labelDateSubscribe.setText(localDate.toString());
        this.txtEventId.setText(eventId.toString());
    }

    public void handleSubscribeButtonClicked(){
            rootService.getNetworkServicePag().subscribeUserToEvent(new EventDTO(Long.parseLong(txtEventId.getText()),null,null,null)
                                                                    ,new UserDto<String>(loggedInUsername,null,null));

    }
}
