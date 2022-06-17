package View;

import Controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class GroupPage extends window{
    String account;
    ListView list;
    private Vector<memberItem> items;
    public  static Vector<String> friendVector;
//    private Vector<ListItem> items;
    public GroupPage() throws IOException {
        root = FXMLLoader.load(getClass().getResource("Fxml/groupPage.fxml"));
        Scene scene = new Scene(root, 385, 648);;
        scene.setFill(Color.TRANSPARENT);
        items = new Vector<>();
        friendVector = new Vector<>();
        list=((ListView)$("friendList"));
//        list.setPrefSize(375,400);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        setTitle("We Chat");
        setIcon();
        quit();
        move();
        minimiser();
    }

    public void setGroupAccount(String account){
        ((Label)$("account")).setText(account+" 群员表");
        this.account=account;
    }

    public void Display(){
//        if(((ListView)$("Friendlist")).getItems().size()>0)
//
        try{
            list.getItems().clear();
            friendVector.clear();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            ResultSet resultSet=Controller.database.execResult("Select * from groupmember where groupid=?",account);
            while(resultSet.next()){
//                System.out.println("one");
                memberItem item=new memberItem(resultSet.getString("head"),resultSet.getString("member"));
//                list.getItems().add(item);
//                items.add(new ListItem(resultSet.getString("head"),resultSet.getString("member")));
                items.add(item);
                int index  = items.size()-1;
                items.get(index).setActionForAdd(Controller.mainWindow);
                friendVector.add(items.get(index).getText());
                ((ListView)$("friendList")).getItems().add(items.get(index).getPane());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        this.show();
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
}
