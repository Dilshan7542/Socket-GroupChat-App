package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author :Dilshan
 **/
public class LoginController {

    public TextField txtUserName;

    public void btnLog(ActionEvent actionEvent) throws IOException {
       manageLogin();
    }

    public void txtUserNameAction(ActionEvent actionEvent) throws IOException {
        manageLogin();
    }
    public void manageLogin() throws IOException {
        ClientFormController.userName = txtUserName.getText();
        Stage stage = (Stage) txtUserName.getScene().getWindow();
        stage.close();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../view/ClientForm.fxml")));
        stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        stage.show();
    }
}
