package View;

//import Model.ChatManager;
import Model.Data.MsgData;
import Model.DatabaseModel;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 *
 * 好友列表项
 */
public class friendListItem{
    protected Button head;
    protected Label information;
    protected Pane pane;
    protected Button send;
    protected String friendHead;
    protected Button MsgTip;
    protected Button state;
    protected String friendName;
    Vector<MenuItem> items;
    protected MainWindow mainWindow;
    public friendListItem(String ihead,String iaccount,String remark){
        head = new Button();
        information = new Label();
        pane = new Pane();
        MsgTip = new Button();
        MsgTip.setPrefSize(20,20);
        MsgTip.setLayoutY(15);
        MsgTip.setLayoutX(275);
        state = new Button();
        state.setPrefSize(15,15);
        state.setLayoutX(55);
        state.setLayoutY(30);
        state.getStyleClass().add("outline");
        pane.getChildren().add(head);
        pane.getChildren().add(state);
        pane.getChildren().add(information);
        MsgTip.getStyleClass().add("no-MsgTip");
        pane.getChildren().add(MsgTip);
        pane.setPrefSize(295,50);
        head.setPrefSize(46,46);
        head.setLayoutX(2);
        head.setLayoutY(2);
        head.setTooltip(new Tooltip("查看好友资料"));
        send = new Button();
        send.setPrefSize(295,50);
        send.getStyleClass().add("sendMsg");
        send.setLayoutX(0);
        send.setLayoutY(0);
        information.setPrefSize(150,30);
        information.setLayoutX(55);
        information.setLayoutY(1);
        head.getStyleClass().add("head");
        information.getStyleClass().add("information");
        pane.getStyleClass().add("ListItem");
        pane.getChildren().add(send);
        items = new Vector<>();
        items.add(new MenuItem(""));
        items.add(new MenuItem("资料"));
        items.add(new MenuItem("清除聊天记录"));
        items.add(new MenuItem("删除联系"));
        setHead(ihead);
        setText(remark);
        friendName = iaccount;
        setMenuItem();
    }
    public Pane getPane() {
        return pane;
    }
    public void setText(String text){
        information.setText(text);
    }
    public void setHead(String head){
        setHeadPortrait(this.head,head);
        friendHead = head;
    }
    public String getText(){
        return information.getText();
    }
    public String getFriendName(){
        return friendName;
    }
    public Button getHead() {
        return head;
    }

    /**显示好友个人信息
     * @param database
     * @param friendPage
     * @param account
     */
    public void setActionForInfo(DatabaseModel database,FriendPage friendPage,String account){
        items.get(1).setOnAction(event -> {

            if(account.equals("WeChat聊天助手"))
            {
                return;
            }
//            else if(account.charAt(0)=='#'){
//                if (friendPage.isShowing()) {
//                    friendPage.close();
//                }
//                try {
//                    ResultSet resultSet = database.execResult("SELECT * FROM user WHERE account=?", account);
//                    resultSet.next();
//                    friendPage.setFriendData(resultSet,information.getText());
//                    friendPage.show();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
            else {
                if (friendPage.isShowing()) {
                    friendPage.close();
                }
                try {
                    ResultSet resultSet = database.execResult("SELECT * FROM user WHERE account=?", account);
                    resultSet.next();
                    friendPage.setFriendData(resultSet,information.getText());
                    friendPage.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 好友项右键菜单
     */
    public void setMenuItem(){
        ContextMenu menu = new ContextMenu();
        for(int i=0;i<items.size();i++){
            menu.getItems().add(items.get(i));
        }
        send.setContextMenu(menu);
    }

    /**
     * 消息提示
     * @param value
     */
    public void addMsgTip(int value){
        MsgTip.getStyleClass().clear();
        MsgTip.getStyleClass().add("MsgTip");
        MsgTip.setText(" "+String.valueOf(value));
    }

    /**
     * 清除消息提示
     */
    public void clearMsgTip(){
        MsgTip.getStyleClass().clear();
        MsgTip.getStyleClass().add("no-MsgTip");
        if(!MsgTip.getText().equals("")){
            MsgTip.setText("");
        }
    }

    /**
     * 好友上线状态
     */
    public void setOnline(){
        state.getStyleClass().clear();
        state.getStyleClass().add("online");
    }

    /**
     * 好友下线状态
     */
    public void setOutline(){
        state.getStyleClass().clear();
        state.getStyleClass().add("outline");
    }

    /**
     * 获取好友状态
     * @return
     */
    public boolean getState(){
        if(state.getStyleClass().equals("online")){
            return true;
        }
        else {
            return false;
        }
    }
    public Vector<MenuItem> getItems() {
        return items;
    }

    public void setActionForSendMsg(MainWindow mainWindow, String userAccount, String Head){
        Timer timer = new Timer();//先new一个定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {//我测试的是自动更新一个函数结果
                        if(mainWindow.selectItem==friendName){
//                            System.out.println("flush");
                            flush(friendName,friendHead);
                        };
                    }
                });
            }
        },100,500);//定时器的延迟时间及间隔时间，不要设置太小
        send.setOnAction(event -> {
            this.mainWindow=mainWindow;
            this.mainWindow.selectItem=friendName;
            flush(friendName,friendHead);
        });
    }


    public void flush(String userAccount, String Head){
        String friendAccount = friendName;
        ((Label) mainWindow.$("Y_account")).setText(information.getText());
        if(friendAccount.equals("WeChat聊天助手"))
        {
            ((TextField) mainWindow.$("input")).setDisable(true);
            ((Button) mainWindow.$("send")).setDisable(true);
        }
        else {
            ((TextField) mainWindow.$("input")).setDisable(false);
            ((Button) mainWindow.$("send")).setDisable(false);
        }
        //获取当前好友在好友列表中的位置
        int index = MsgData.accountList.indexOf(friendAccount);
        if(index!=-1){
            //选中
            mainWindow.getFriendList().getSelectionModel().select(index);
            this.clearMsgTip();//清除消息提示
            MsgData.msgTip.put(friendAccount,0);
        }
        //放入消息映射
        for(int i=0;i<MsgData.accountList.size();i++) {
            MsgData.MsgMap.put(MsgData.accountList.get(i),MsgData.msg.get(i));
        }
//            System.out.println(MsgData.MsgMap);
        //遍历消息映射
        for(Map.Entry<String,Vector<String>> entry : MsgData.MsgMap.entrySet()){
            if(entry.getKey().equals(friendAccount)){
                ((ListView)mainWindow.$("ChatList")).getItems().clear();
                Vector<String> record = entry.getValue();
                for(int i=0;i<record.size();i++){
                    String []current = record.get(i).split(" ");
                    String account = current[0];
                    String Msg = "";
                    for(int j=1;j<current.length;j++){
                        Msg=Msg+current[j]+" ";
                    }
                    if(account.equals(userAccount)){
                        mainWindow.addLeft(friendHead,Msg);
                    }
                    else
                    {
                        mainWindow.addRight(Head,Msg);
                    }
                }
                break;
            }
        }
    }

    public void setActionForClear(MainWindow mainWindow){

        items.get(2).setOnAction(event -> {
            if(friendName.equals("WeChat聊天助手"))
            {
                return ;
            }
            int index = MsgData.accountList.indexOf(friendName);
            if(index!=-1){
                int index1 = mainWindow.getFriendList().getSelectionModel().getSelectedIndex();
                MsgData.msg.get(index).clear();
                MsgData.msgTip.remove(friendName);
                MsgData.MsgMap.remove(friendName);
                if(index == index1){
                    ((ListView)mainWindow.$("ChatList")).getItems().clear();
                }

            }
        });

    }

    public void setActionForDelete(DatabaseModel database,MainWindow mainWindow,String I_account){

        items.get(3).setOnAction(event -> {
            int i = MsgData.accountList.indexOf(friendName);
            if(i!=-1){
                if(i==mainWindow.getFriendList().getSelectionModel().getSelectedIndex())
                {
                    mainWindow.getFriendList().getSelectionModel().select(0);
                    ((Label)mainWindow.$("Y_account")).setText("WeChat聊天助手");
                }
                mainWindow.getFriendVector().remove(i);
                ((ListView) mainWindow.$("FirendList")).getItems().remove(i);
                MsgData.accountList.remove(i);
                MsgData.msg.remove(i);
                MsgData.MsgMap.remove(friendName);

            }/*
            if(((Label)mainWindow.$("Y_account")).getText().equals(information.getText())){
                        mainWindow.getFriendList().getSelectionModel().select(0);
                ((Label)mainWindow.$("Y_account")).setText("WeChat聊天助手");
            }*/
            try {
                database.exec("DELETE FROM companion WHERE I_account = ? AND Y_account = ?",I_account,friendName);
                database.exec("DELETE FROM companion WHERE I_account = ? AND Y_account = ?",friendName,I_account);
                try {
//                    ChatManager.getInstance().send("##@@ "+I_account+" "+friendName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

    }


    public void setActionForMsgTip(){
        if(MsgTip.getStyleClass().equals("no-MsgTip"))
        {
            items.get(0).setText("标为未读");
        }
        else
        {
            items.get(0).setText("标为已读");
        }
        items.get(0).setOnAction(event -> {
            if(items.get(0).getText().equals("标为已读")) {
                MsgTip.getStyleClass().clear();
                MsgTip.getStyleClass().add("no-MsgTip");
                clearMsgTip();
                items.get(0).setText("标为未读");
            }
            else
            {
                MsgTip.getStyleClass().clear();
                MsgTip.getStyleClass().add("MsgTip");
                addMsgTip(1);
                items.get(0).setText("标为已读");

            }
        });
    }

    public void setHeadPortrait(Button button, String head){
        if(head==null)
            head="head1";
        button.setStyle(String.format("-fx-background-image: url('file:src/main/resources/View/Fxml/CSS/Image/head/%s.jpg')",head));
    }
}
