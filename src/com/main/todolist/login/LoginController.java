package com.main.todolist.login;

import com.main.todolist.db.DB;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private DB db = DB.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boolean accountCreated = false;

        accountCreated = db.hasRowInTableForThisCondition("user_info", "");

        if (accountCreated) {
            createAccountButton.setVisible(false);

        } else {
            enterButton.setVisible(false);
            deleteAccountButton.setVisible(false);
            deleteInformationCB.setVisible(false);
        }
    }

    @FXML
    private TextField usernameTF;

    @FXML
    private PasswordField passwordPF;

    @FXML
    private Button enterButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private CheckBox deleteInformationCB;

    @FXML
    private Label warningsLabel;

    @FXML
    private Button createAccountButton;

    @FXML
    void createAccountButtonPressed(ActionEvent event) {
        String username = usernameTF.getText().trim();
        if (username.equals("")) {
            msg("Имя пользователя не должно быть пустым");
        } else {
            String password = passwordPF.getText().trim();
            if (password.equals("")) {
                msg("Пароль не должен быть пустым");
            } else {

                db.iud("insert into user_info(username,password) values('" + username + "','" + password + "')");
                msg("Профиль успешно создан");
                createAccountButton.setVisible(false);
                enterButton.setVisible(true);
                deleteAccountButton.setVisible(true);
                deleteInformationCB.setVisible(true);

            }
        }
    }

    public void msg(String msg) {
        warningsLabel.setText(msg);
    }

    @FXML
    void deleteAccountButtonPressed(ActionEvent event) {

        String username = usernameTF.getText().trim();
        String password = passwordPF.getText().trim();

        if (db.hasRowInTableForThisCondition("user_info", "where username='" + username + "' and password='" + password + "'")) {

            db.iud("truncate table user_info");
            msg("Пользователь удален");
            
            if(deleteInformationCB.isSelected()){
            db.iud("truncate table tasks");
            db.iud("truncate table categories");
            }
            
            enterButton.setVisible(false);
            deleteAccountButton.setVisible(false);
            deleteInformationCB.setVisible(false);
            createAccountButton.setVisible(true);
        } else {
            msg("Неверные данные");
        }

    }

    @FXML
    void deleteInformationCBPressed(ActionEvent event) {

    }

    @FXML
    void enterButtonPressed(ActionEvent event) {

        String username = usernameTF.getText().trim();
        String password = passwordPF.getText().trim();

        if (db.hasRowInTableForThisCondition("user_info", "where username='" + username + "' and password='" + password + "'")) {
        try{
            Stage s=new Stage();
                s.setTitle("Список");
                s.getIcons().add(new Image("file:"+String.valueOf(Paths.get(System.getProperty("user.dir"),"files","main.png"))));
                s.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/com/main/todolist/list/list.fxml"));
       Parent root=loader.load();
       Scene scene=new Scene(root);
       s.setScene(scene);
       s.show();
        } catch (Exception ex){
              ex.printStackTrace();
        }
        
        } else {
            msg("Неверные данные");
        }
    }

}
