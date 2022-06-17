package View;

import Controller.Controller;
import Model.Data.MsgData;
import Model.Tool;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class memberItem {
    private Button head;
    private Label information;
    private Button friend_add;
    private Pane pane;
    private String friendHead;
    memberItem(String ihead,String iaccount){
        head = new Button();
        friend_add = new Button();
        information = new Label();
        pane = new Pane();
        pane.getChildren().add(head);
        pane.getChildren().add(information);
        pane.setPrefSize(300,60);
        head.setPrefSize(56,56);
        friend_add.setPrefSize(25,25);
        friend_add.getStyleClass().add("add");
        friend_add.setLayoutX(280);
        friend_add.setLayoutY(14);
        friend_add.setTooltip(new Tooltip("添加好友"));
        pane.getChildren().add(friend_add);
        head.setLayoutX(2);
        head.setLayoutY(2);
        information.setPrefSize(200,32);
        information.setLayoutX(65);
        information.setLayoutY(5);
        head.getStyleClass().add("head");
        information.getStyleClass().add("information");
        pane.getStyleClass().add("ListItem");
        setText(iaccount);
        setHead(ihead);
    }
    public Pane getPane() {
        return pane;
    }
    public void setText(String text){
        information.setText(text);
    }
    public String getText(){
        return information.getText();
    }
    public void setHead(String head)
    {
        setHeadPortrait(this.head,head);
        friendHead = head;
    }
    public String getFriendHead(){
        return friendHead;
    }

    public Button getFriend_add(){
        return friend_add;
    }
    public void setHeadPortrait(Button button, String head){
        System.out.println(head);
        if(head==null){
            button.setStyle("-fx-background-image: url('file:src/main/resources/View/Fxml/CSS/Image/head/head1.jpg')");
        }else{
            button.setStyle(String.format("-fx-background-image: url('file:src/main/resources/View/Fxml/CSS/Image/head/%s.jpg')",head));
        }
//        button.setStyle(String.format("-fx-background-image: url('file:src/main/resources/View/Fxml/CSS/Image/head/head1.jpg')",head));
    }

    /**添加好友到主页面
     * @param mainWindow
     */
    public void setActionForAdd(MainWindow mainWindow){
        friend_add.setOnAction(event -> {
            String friendName = information.getText();
            int index =  SearchFriend.friendVector.indexOf(friendName);
            if(index!=-1)
            {
                Controller.searchFriend.getFriendList().getItems().remove(index);
                SearchFriend.friendVector.remove(index);
            }
            //加消息
            MsgData.msg.add(new Vector<>());
            MsgData.accountList.add(friendName);
            MsgData.msgTip.put(friendName,0);

            if(friendName.charAt(0)=='#'){  //群聊添加
                try{
                    Controller.database.exec("insert into groupmember set groupid=?, member=?,head=?",
                            friendName,Controller.userdata.getAccount(),Controller.userdata.getHead());
                    mainWindow.addGroup("head10",friendName,friendName);
                    Controller.database.exec("insert into chatrecord set owner=?,sender=?,msg=?,head=?,time=?",
                            friendName,Controller.userdata.getAccount(),
                            "大家好!我是"+Controller.userdata.getAccount(),Controller.userdata.getHead(), Tool.getTime());
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{

                try {
                    try {
//                    ChatManager.getInstance().send("###@ " + Controller.userdata.getAccount() + " " + friendName);
                    } catch (Exception e) {
                        Controller.alert.setInformation("!添加好友失败");
                        Controller.alert.exec();
                        e.printStackTrace();
                    }
                    ResultSet resultSet=Controller.database.execResult("Select * from companion where I_account=? or Y_Account=?",friendName,friendName);
                    if(resultSet.next()){
                        return;
                    }
                    Controller.database.exec("INSERT INTO companion VALUES(?,?,?)", Controller.userdata.getAccount(), friendName, friendName);
                    Controller.database.exec("INSERT INTO companion VALUES(?,?,?)", friendName, Controller.userdata.getAccount(), Controller.userdata.getAccount());
                    mainWindow.addFriend(friendHead, friendName,friendName, Controller.database, Controller.friendPage);
                    MsgData.msg.add(new Vector<>());
                    MsgData.accountList.add("WeChat聊天助手");
                    MsgData.msgTip.put("WeChat聊天助手", 0);
                    //是否在线
                    resultSet = Controller.database.execResult("SELECT * FROM dialog WHERE account=?", friendName);
                    if (resultSet.next())
                        mainWindow.getFriendVector().get(MsgData.accountList.size()-1).setOnline();
                    else
                        mainWindow.getFriendVector().get(MsgData.accountList.size()-1).setOutline();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
