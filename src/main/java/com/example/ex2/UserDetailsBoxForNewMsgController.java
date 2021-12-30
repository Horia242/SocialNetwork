package com.example.ex2;
import com.example.ex2.rootService.RootService;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import ro.ubbcluj.map.model.UserDto;

public class UserDetailsBoxForNewMsgController {

    private RootService rootService;
    private String loggedInUserEmail ;

    @FXML
    private HBox hboxUserDetailsForNewMsg;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelFirstName;
    @FXML
    private Label labelLastName;
    @FXML
    private boolean isChecked;
    @FXML
    private AnchorPane anchorDashboardRootPane;


    public void setRootService(RootService rootService) {
        this.rootService = rootService;
    }


    public void setData(UserDto<String> userDto){
        labelFirstName.setText(userDto.getFirstName());
        labelLastName.setText(userDto.getLastName());
        labelEmail.setText(userDto.getUserID());
    }
}

