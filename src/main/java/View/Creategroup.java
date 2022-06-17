package View;

//import java.io.IOException;

import Controller.Controller;
import Model.Data.MsgData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Creategroup extends window {
    private Vector<ListItem> items;
    private ListView friendList;
    public  static Vector<String> friendVector;

    public Creategroup() throws IOException {
        root = FXMLLoader.load(getClass().getResource("Fxml/creategroup.fxml"));
        Scene scene = new Scene(root, 600, 145);
        friendList = ((ListView) $("friendList"));
        ((TextField) $("textInput")).setTooltip(new Tooltip("请输入创建群号"));
        items = new Vector<>();
        friendVector = new Vector<>();
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        setTitle("Creat gourp");
        move();
        quit();
        minimiser();
        setOnCreate();
    }

    public void setOnCreate(){
        ((TextField) $("textInput")).setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.ENTER){
                System.out.println("dsadsadsa");
                String input=((TextField) $("textInput")).getText();
                try{
                    ResultSet resultSet=Controller.database.execResult("Select * from groupmember where groupid=?",input);
                    if(resultSet.next()){
//                        Controller.alert.setInformation("该群已存在");
                        ((Label)$("alert")).setText("该群已存在");
                        return ;
                    }
                    if(input.charAt(0)!='#'){
//                        Controller.alert.setInformation("群号以#开头");
                        ((Label)$("alert")).setText("群号以#开头");
                        return ;
                    }

                    Controller.database.exec("Insert into groupmember set groupid=?,member=?,head=?",
                            input,Controller.userdata.getAccount(),Controller.userdata.getHead());
                    MsgData.msg.add(new Vector<>());
                    MsgData.accountList.add(input);
                    MsgData.msgTip.put(input, 0);
                    Controller.mainWindow.addGroup("head10",input,input);

                }catch (SQLException e){
                    e.printStackTrace();
                }
            }


        });
    }


    @Override
    public void quit() {
        ((Button) $("quit1")).setTooltip(new Tooltip("关闭"));
        ((Button) $("quit1")).setOnAction(event -> {
            close();
        });
    }

    @Override
    public void minimiser() {
        ((Button) $("minimiser1")).setTooltip(new Tooltip("最小化"));
        ((Button) $("minimiser1")).setOnAction(event -> {
            setIconified(true);
        });
    }
    public void clear(){
//        friendList.getItems().clear();
//        friendVector.clear();
    }
//    public ListView getFriendList() {
//        return friendList;
//    }
//    public Vector<ListItem> getItems() {
//        return items;
//    }

}
