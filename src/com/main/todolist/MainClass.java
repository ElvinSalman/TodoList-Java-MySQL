package com.main.todolist;


import com.main.todolist.test.TestClass;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainClass extends Application{
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage s) throws Exception {
        s.setTitle("Вход");
         s.getIcons().add(new Image("file:"+String.valueOf(Paths.get(System.getProperty("user.dir"),"files","login.png"))));
        FXMLLoader loader=new FXMLLoader(getClass().getResource("login/login.fxml"));
       Parent root=loader.load();
       Scene scene=new Scene(root);
       s.setScene(scene);
       s.show();
//       TestClass.insertTestInformationToDatabase();
    }
}
