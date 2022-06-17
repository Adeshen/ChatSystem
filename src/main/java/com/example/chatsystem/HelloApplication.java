package com.example.chatsystem;

import Controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import View.*;
import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        show();
//        stage.setScene(scene);
//        stage.show();
        Controller controller=new Controller();
        controller.Exec();
    }

    private void show() throws IOException{
//        Register register=new Register();
//        register.show();
//        Alert alert=new Alert();
//        alert.show();
//        AlterPerson alterPerson=new AlterPerson();
//        alterPerson.show();
//        Dialog dialog=new Dialog();
//        dialog.show();
//        Forget forget=new Forget();
//        forget.show();
//        FriendPage friendPage=new FriendPage();
//        friendPage.show();
//        HeadProtrait headProtrait=new HeadProtrait();
//        headProtrait.show();
//        Homepage homepage=new Homepage();
//        homepage.show();
    }

    public static void main(String[] args) {
//        ok;
        launch();
    }
}