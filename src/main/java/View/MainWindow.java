package View;
import Controller.Controller;
//import Model.ChatManager;
import Model.DatabaseModel;
import Model.Tool;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javafx.scene.control.Button;

/**

 *
 * 主窗口
 */
public class MainWindow extends window {
    private ListView friendList;
    public ListView chatList;
    private Vector<friendListItem> friendVector;
    private Vector<ChatListItem> chatVector;
    private ContextMenu contextMenu;
    private Vector<MenuItem> rightItem;
    private double xOffset;
    private double yOffset;
    public MainWindow() throws IOException {
        root = FXMLLoader.load(getClass().getResource("Fxml/MainWindow.fxml"));
        Scene scene = new Scene(root, 923, 700);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setResizable(false);
        setTitle("We Chat");
        friendList = ((ListView) $("FirendList"));

        chatList = ((ListView) $("ChatList"));
        friendVector = new Vector<>();
        contextMenu = new ContextMenu();
        rightItem = new Vector<>();
        rightItem.add(new MenuItem("  添加好友  "));
        rightItem.add(new MenuItem("  设置      "));
        rightItem.add(new MenuItem("  最小化    "));
        rightItem.add(new MenuItem("  退出      "));
//        BackgroundMenu();
        setIcon();
        move();
        quit();
        minimiser();
        initTooltip();
        chatflush();

    }
    public void initTooltip(){
        ((Button) $("maximization")).setTooltip(new Tooltip("添加好友"));
        ((Button) $("setting")).setTooltip(new Tooltip("设置"));
        ((Button) $("individual")).setTooltip(new Tooltip("个人资料"));
        ((Button) $("moref")).setTooltip(new Tooltip("好友资料"));
        ((TextField) $("search")).setTooltip(new Tooltip("查找好友"));
        ((Button) $("send")).setTooltip(new Tooltip("发送"));
    }

    /**右键菜单
     *
     *
     */
    @Override
    public void move(){
        root.setOnMousePressed(event -> {
            contextMenu.getItems().clear();
            if(event.getButton() == MouseButton.SECONDARY){
                rightItem.get(0).setOnAction(event1 -> {
                    Controller.searchFriend.show();
                });
                rightItem.get(1).setOnAction(event1 -> {
//                  设置页未完成
                });
                rightItem.get(2).setOnAction(event1 -> {
                    setIconified(true);
                });
                rightItem.get(3).setOnAction(event1 -> {
                    close();
                    try {
                        Controller.database.exec("DELETE FROM dialog WHERE account  = ?", Controller.userdata.getAccount());
                        try {
//                            ChatManager.getInstance().send("#### "+ Controller.userdata.getAccount()+" ####");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                });
                contextMenu.getItems().addAll(rightItem);
                contextMenu.show(this,event.getScreenX(),event.getScreenY());
            }
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
            getRoot().setCursor(Cursor.CLOSED_HAND);

        });
        root.setOnMouseDragged(event -> {

            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);


        });
        root.setOnMouseReleased(event -> {
            root.setCursor(Cursor.DEFAULT);

        });
    }
    //别人的消息
    public void addLeft(String head,String Msg){
        chatList.getItems().add(new ChatListItem().Left(head,Msg, Tool.getWidth(Msg),Tool.getHight(Msg)));
    }
    //自己的消息
    public void addRight(String head,String Msg){
        chatList.getItems().add(new ChatListItem().Right(head,Msg,Tool.getWidth(Msg),Tool.getHight(Msg)));
    }
    public void setHead(String Head){
        setHeadPortrait(((Button) $("individual")),Head);
    }
    @Override
    public void quit() {//退出
        ((Button) $("quit1")).setTooltip(new Tooltip("退出"));
        ((Button) $("quit1")).setOnAction(event -> {
            close();
            try {
                Controller.database.exec("DELETE FROM dialog WHERE account  = ?", Controller.userdata.getAccount());
                try {
//                    ChatManager.getInstance().send("#### "+ Controller.userdata.getAccount()+" ####");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
    }

    @Override
    public void minimiser() {
        ((Button) $("minimiser1")).setTooltip(new Tooltip("最小化"));
        ((Button) $("minimiser1")).setOnAction(event -> {
            setIconified(true);
        });
    }

    public static void setHeadPortrait(Button button, String head){
        if(head==null){
            head="head1";
        }
        button.setStyle(String.format("-fx-background-image: url('file:src/main/resources/View/Fxml/CSS/Image/head/%s.jpg')",head));
    }
    public static void setHeadPortrait(Button button ,String background,String file){

        button.setStyle(String.format("-fx-background-image: url('file:src/main/resources/View/Fxml/CSS/Image/%s/%s.jpg')",file,background));
    }

    /**

     */
    public void addFriend(String head, String account,String remark, DatabaseModel database,FriendPage friendPage){
        friendVector.add(new friendListItem(head,account,remark));
        int index = friendVector.size()-1;
        friendVector.get(index).setActionForMsgTip();
        friendVector.get(index).setActionForInfo(database, friendPage, account);
        friendVector.get(index).setActionForSendMsg(this,account, Controller.userdata.getHead());
        friendVector.get(index).setActionForClear(this);
        friendVector.get(index).setActionForDelete(database,this, Controller.userdata.getAccount());
        friendList.getItems().add(friendVector.get(friendVector.size()-1).getPane());
    }
    public void addFriend(String head,String account,String remark){
        int index = friendVector.size();
        friendVector.add(new friendListItem(head,account,remark));
        friendVector.get(index).setActionForSendMsg(this,account, Controller.userdata.getHead());
        friendVector.get(index).setActionForMsgTip();
        friendVector.get(index).setActionForClear(this);
        (friendVector.get(index)).setActionForDelete(Controller.database,this, Controller.userdata.getAccount());
        friendList.getItems().add(friendVector.get(friendVector.size()-1).getPane());
    }
    public void addGroup(String head ,String account,String remark){
        int index = friendVector.size();
        friendVector.add(new groupListItem(head,account,remark));
        friendVector.get(index).setActionForSendMsg(this,account, Controller.userdata.getHead());
        friendVector.get(index).setActionForMsgTip();

        ((groupListItem)friendVector.get(index)).setActionForClear(this);
        ((groupListItem)friendVector.get(index)).setActionForDelete(Controller.database,this, Controller.userdata.getAccount());
        ((groupListItem)friendVector.get(index)).setMainWindowAccount(Controller.userdata.getAccount());
        friendList.getItems().add(friendVector.get(friendVector.size()-1).getPane());
    }

    public Vector<friendListItem> getFriendVector() {
        return friendVector;
    }
    public ListView getFriendList() {
        return friendList;
    }
    public void setPersonalInfo(String account,String name,String address,String phone)
    {
//        ((Label) $("myAccount")).setText(account);
        ((Label) $("myName")).setText(name);
//        ((Label) $("myAddress")).setText(address);
//        ((Label) $("myPhone")).setText(phone);
    }
    public String selectItem;
    public void chatflush(){

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
//                Timer timer = new Timer();//先new一个定时器
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {//我测试的是自动更新一个函数结果
//                                while(selectItem==null);
//                                System.out.println("flush");
//                                selectItem.flush(selectItem.friendName,selectItem.friendHead);
//                            }
//                        });
//                    }
//                },100,500);//定时器的延迟时间及间隔时间，不要设置太小
            }
        });
//        t.start();

        root.setOnMousePressed(event -> {

        });
    }


}

//
// 聊天列表项类
class ChatListItem{
    private Pane pane;
    private Button head;
    private TextArea text;
    private Pane left;
    private Pane right;
    private Button arrow;
    public ChatListItem(){
        pane = new Pane();
        head = new Button();
        text = new TextArea();
        pane.setPrefSize(589,150);
        left = new Pane();
        right = new Pane();
        arrow = new Button();
        arrow.setDisable(false);
        arrow.setPrefSize(32,32);
        left.setPrefSize(580,70);
        right.setPrefSize(580,70);
        head.getStyleClass().add("head");
        pane.getStyleClass().add("pane");
        left.getStyleClass().add("pane");
        right.getStyleClass().add("pane");
        head.setPrefSize(50,50);
        text.setPrefSize(480,50);
        text.setWrapText(true);
        text.setEditable(false);
    }
    public Pane Left(String ihead,String itext,double width,double hight){//别人的消息
        text.getStyleClass().add("lefttext");
        arrow.getStyleClass().add("leftarrow");
        pane.setPrefHeight(40+hight);
        left.setPrefHeight(30+hight);
        head.setLayoutY(10);
        head.setLayoutX(80);
        text.setPrefSize(width,hight);
        text.setLayoutX(180);
        text.setLayoutY(30);
//        arrow.setLayoutY(40);
//        arrow.setLayoutX(48);
        text.setText(itext);
        MainWindow.setHeadPortrait(head,ihead);
        left.getChildren().add(head);
        left.getChildren().add(text);
        left.getChildren().add(arrow);
        pane.getChildren().add(left);
        return pane;
    }
    public Pane Right(String ihead,String itext,double width,double hight){//自己的消息
        text.getStyleClass().add("righttext");
        arrow.getStyleClass().add("rightarrow");
        head.setLayoutY(10);
        head.setLayoutX(300);
        pane.setPrefHeight(40+hight);
        right.setPrefHeight(30+hight);
        text.setPrefSize(width,hight);
        //text=480 0 text = 470 10 460 20 480-width
        text.setLayoutY(30);
        text.setLayoutX(280-width);
        arrow.setLayoutY(4000);
        arrow.setLayoutX(4750);
        text.setText(itext);
        MainWindow.setHeadPortrait(head,ihead);
        right.getChildren().add(head);
        right.getChildren().add(text);
        right.getChildren().add(left);
        right.getChildren().add(arrow);
        right.setLayoutX(150);
        pane.getChildren().add(right);
        return pane;
    }

}


