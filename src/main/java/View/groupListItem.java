package View;


//import Model.ChatManager;
import Controller.Controller;
import Model.Data.MsgData;
import Model.DatabaseModel;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;


public class groupListItem extends friendListItem{

    private String MainWindowAccount;

    public groupListItem(String ihead, String iaccount, String remark) {
        super(ihead, iaccount, remark);
    }

    public void setMainWindowAccount(String account){
        MainWindowAccount=account;
    }

    @Override
    public void flush(String userAccount, String Head){
        String friendAccount = friendName;
        ((TextField) mainWindow.$("input")).setDisable(false);
        ((Button) mainWindow.$("send")).setDisable(false);
        //获取当前好友在好友列表中的位置
        int index = MsgData.accountList.indexOf(friendAccount);//这里是群账号
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
        int member=0;
        try {
            ResultSet resultSet= Controller.database.execResult("Select *from chatrecord where owner=? ",friendAccount);
            Vector<String> names=new Stack<>(),heads=new Stack<>(),msgs=new Stack<>();
            while(resultSet.next()){
                String name=resultSet.getString("sender");
                String head=resultSet.getString("head");
                String msg=resultSet.getString("msg");
                names.add(name);
                heads.add(head);
                msgs.add(msg);
            }
            ((ListView)mainWindow.$("ChatList")).getItems().clear();
            for(int i=Math.max(0,names.size()-10);i<names.size();i++){
                if(names.get(i)==null||heads.get(i)==null||msgs.get(i)==null){
                    continue;
                }
                String name=names.get(i);
                if(name.equals(MainWindowAccount)){
                    mainWindow.addRight(heads.get(i), msgs.get(i));
                }else {
                    mainWindow.addLeft(heads.get(i), msgs.get(i));
                }
            }
//                mainWindow.addRight("dsad","dasd");
            resultSet=Controller.database.execResult("Select *from groupmember where groupid=? ",friendAccount);
            while(resultSet.next()){
                member++;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        ((Label) mainWindow.$("Y_account")).setText("群聊:"+information.getText()+"("+member+")");
        int infocus=mainWindow.chatList.getItems().size();
    }

    @Override
    public void setMenuItem(){
        items = new Vector<>();
        items.add(new MenuItem(""));
        items.add(new MenuItem("查看群资料"));
        items.add(new MenuItem("聊天记录"));
        items.add(new MenuItem("退出群聊"));

        ContextMenu menu = new ContextMenu();
        for(int i=0;i<items.size();i++){
            menu.getItems().add(items.get(i));
        }
        send.setContextMenu(menu);
    }
    public void setActionForGroupInfo(){
        items.get(1).setOnAction(event -> {
            Controller.groupPage.setGroupAccount(friendName);
            Controller.groupPage.Display();
        });
    }
    @Override
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

            }
            try {
                database.exec("DELETE FROM groupmember WHERE groupid=?",friendName);
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

}
