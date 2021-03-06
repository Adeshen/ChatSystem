package Controller;

import Model.ChatManager;
import Model.Data.MsgData;
import Model.Data.Userdata;
import Model.DatabaseModel;
import Model.Tool;
import View.*;
import View.Alert;
import View.Dialog;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

public class Controller {
    private Dialog dialog;
    private Register register;
    private Forget forget;
    //    private MainWindow mainWindow;
    public static Userdata userdata;
    private Homepage homepage;
    public static DatabaseModel database;
    private AlterPerson alterPerson;
    public static FriendPage friendPage;
    public static SearchFriend searchFriend;
    private HeadProtrait headProtrait;
    private String friendName;
    private String friendHead;
    public static Alert alert;
    public static MainWindow mainWindow;
    public static Creategroup creategroup;
    public static GroupPage groupPage;
    public Controller() throws IOException {
//        database = new DatabaseModel();
//
//        headProtrait=new HeadProtrait();
//        dialog = new Dialog();
//
//        register = new Register();
//
//        userdata = new Userdata();
//
//        alert=new Alert();
//
//        mainWindow=new MainWindow();
//
//        homepage=new Homepage();
//
//        searchFriend=new SearchFriend();
//
//        friendPage=new FriendPage();



        dialog = new Dialog();
        register = new Register();
        userdata = new Userdata();
        database = new DatabaseModel();
        forget = new Forget();
        mainWindow = new MainWindow();
        homepage = new Homepage();
        alterPerson = new AlterPerson();
        alert = new Alert();
        friendPage = new FriendPage();
        searchFriend = new SearchFriend();
        headProtrait = new HeadProtrait();
        MsgData.msg = new Vector<>();
        MsgData.MsgMap = new HashMap<>();
        MsgData.accountList = new Vector<>();
        creategroup=new Creategroup();
        groupPage=new GroupPage();
        database.connect();
        dialog.show();
    }


    /*
       ?????????????????????id???????????????????????????????????????????????????
     */
    private Object $(window window, String id) {
        return (Object) window.getRoot().lookup("#" + id);
    }

    public void Exec() {
        headProtrait.setModailty(register);
        headProtrait.setModailty(alterPerson);
        alert.setModailty(mainWindow);
        alert.setModailty(searchFriend);
        database.connect();
        dialogExec();
        registerExec();
        alterPersonExec();
        dialog.show();
        ChatManager.getInstance().setMainWindow(mainWindow);
        initEvent();
//        headProtrait.show();
//        homepage.show();
//        alert.show();
        FriendInfo();
        OptionHead();
        find();
        SearchFriends();
        sendMsgExec();
        saveRemark();
        dialog.show();
//        friendPage.show();
    }


    /*
        ???????????????????????????
     */
    public void initEvent() {
        ((Button) $(dialog,"register") ).setOnAction(
                event->{
                    dialog.hide();
                    dialog.clear();
                    register.show();
                    System.out.println("switch to register!");
                }
        );
        ((Button) $(mainWindow, "maximization")).setOnAction(event -> {
            searchFriend.clear();
            searchFriend.show();

        });
        ((Button) $(dialog, "register")).setOnAction(event -> {
            dialog.hide();
            dialog.clear();
            register.show();
        });
        ((Button) $(register, "back")).setOnAction(event -> {
            register.hide();
            register.clear();
            dialog.show();
        });
        ((Button) $(dialog, "getBack")).setOnAction(event -> {
            dialog.hide();
            dialog.clear("Password");
            forget.show();

        });
//        ((Button) $(forget, "cancel")).setOnAction(event -> {
//            forget.hide();
//            forget.clear();
//            dialog.show();
//        });
        ((Button) $(mainWindow, "more")).setOnAction(event -> {
            homepage.show();
        });
        ((Button) $(homepage, "alter")).setOnAction(event -> {
            alterPerson.setUserData(userdata.getUserdata());
            alterPerson.show();
        });
        ((Button) $(register, "ChooseHead")).setOnAction(event -> {
            headProtrait.show();
        });
        ((Button) $(mainWindow, "createGroup")).setOnAction(event -> {
            creategroup.show();
        });

    }
    /*
           ????????????????????????????????????enter???????????????
     */
    private void dialogExec() {
        ((Button) $(dialog, "enter")).setOnAction(event -> {
                    dialog.resetErrorTip();
                    /*?????????????????????*/
//            try{
////                database.exec("INSERT INTO user set account=?  ", "15123456");
//
//                String account="123456",name="abc",password="123456",age="16",sex="man",phone="17827008614";
//                database.exec("INSERT INTO user set account=?,name=?, password=? ,age=? ,sex=?, phone=?, background=?", account, name, password, age, sex, phone,"background1");
//            }catch (SQLException E){
//                E.printStackTrace();
//            }

                    String UserName = ((TextField) $(dialog, "UserName")).getText();
                    String Password = ((PasswordField) $(dialog, "Password")).getText();
//            System.out.println(UserName+"?????????"+Password);
                    if (UserName.equals("") || Password.equals("")) {
                        if (UserName.equals("")) {
                            dialog.setErrorTip("accountError", "??????????????????");
                        }
                        if (Password.equals("")) {
                            dialog.setErrorTip("passwordError", "??????????????????");
                        }
                    } else {
                        ResultSet resultSet = null;
                        try {
                            resultSet = database.execResult("SELECT * FROM user WHERE account=?", UserName);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        try{
                            /*??????????????????user??? ?????????????????????*/
                            if(resultSet.next()){   //
                                if (resultSet.getString("password")!=null
                                        &&resultSet.getString("password").equals(Password)){
                                    ResultSet set = database.execResult("SELECT * FROM dialog WHERE account = ?", UserName);
                                    if (set.next()&&false) {  //????????????
                                        dialog.setErrorTip("accountError", "??????????????????????????????????????????!");
                                    } else {
                                        database.exec("INSERT INTO dialog set account=?,last_seen=?", UserName,"2022-06-04 20:15:45");//????????????
                                        System.out.println("login successfully!");
                                        userdata.setUserdata(resultSet);
                                        dialog.close();
                                        homepage.setUserData(userdata.getUserdata());
                                        //?????????
                                        mainWindow.setHead(userdata.getHead());
                                        mainWindow.setPersonalInfo(userdata.getName(), userdata.getAccount(), userdata.getAddress(), userdata.getPhone());
                                        mainWindow.show();

                                        ResultSet resultSet1 = database.execResult("SELECT head,account,remark FROM user,companion WHERE account = Y_account AND I_account=?", UserName);

                                        //????????????
                                        mainWindow.addFriend("system", "WeChat????????????","????????????");
                                        ((Label) $(mainWindow, "Y_account")).setText("WeChat????????????");
                                        MsgData.msg.add(new Vector<>());
                                        MsgData.accountList.add("WeChat????????????");
                                        MsgData.msgTip.put("WeChat????????????", 0);

                                        while (resultSet1.next()) {
                                            MsgData.msg.add(new Vector<>());
                                            String temp = resultSet1.getString("account");
                                            MsgData.accountList.add(temp);
                                            MsgData.msgTip.put(temp, 0);
                                            mainWindow.addFriend(resultSet1.getString("head"), resultSet1.getString("account"), resultSet1.getString("remark"),database, friendPage);
                                        }
                                        mainWindow.addLeft("system", "????????????WeChat,????????????????????????!");
                                        MsgData.msg.get(0).add("WeChat???????????? ????????????WeChat,????????????????????????!");

                                        //???????????????
                                        ((TextField) $(mainWindow, "input")).setDisable(true);
                                        ((Button) $(mainWindow, "send")).setDisable(true);

                                        Vector<String> groups= Tool.findGoupByMember(UserName);
                                        for(String group:groups){
                                            MsgData.msg.add(new Vector<>());

                                            MsgData.accountList.add(group);
                                            MsgData.msgTip.put(group, 0);
                                            mainWindow.addGroup("head10",group,group);
                                        }

                                        //????????????????????????
                                        mainWindow.getFriendList().getSelectionModel().select(0);

                                        //????????????????????????
                                        ResultSet resultSet2 = database.execResult("SELECT Y_account FROM companion WHERE I_account=? AND Y_account in (SELECT account FROM dialog)", UserName);
                                        while (resultSet2.next()) {
                                            int i = MsgData.accountList.indexOf(resultSet2.getString("Y_account"));
                                            if (i!=-1) {
                                                mainWindow.getFriendVector().get(i).setOnline();//?????????????????????????????????
                                            }
                                        }
                                        mainWindow.getFriendVector().get(0).setOnline();//?????????????????????
                                        ChatManager.getInstance().connect("127.0.0.1", UserName);//???????????????
                                        //????????????
//                                setHeadPortrait(((Button)$(mainWindow,"background")),"background",resultSet.getString("background"));
                                        mainWindow.show();
                                    }
//                            alert.setInformation("????????????");
//                            boolean ok=alert.exec();
                                }else{
                                    dialog.setErrorTip("passwordError", "???????????????");
                                }

                            }else{
                                dialog.setErrorTip("accountError", "??????????????????");
                            }
                        }catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
    /*
    ??????:
    ?????????,?????????????????????
    */
    private void registerExec(){
        ((Button)$(register,"register")).setOnAction(
                event->{
                    register.resetErrorTip();
                    /*???????????????????????????*/
                    String account = ((TextField) $(register, "account")).getText();
                    String name = ((TextField) $(register, "name")).getText();
                    String password = ((PasswordField) $(register, "password")).getText();
                    String rePassword = ((PasswordField) $(register, "rePassword")).getText();
                    String age = ((TextField) $(register, "age")).getText();
                    String sex;
                    String phone = ((TextField) $(register, "phone")).getText();

                    RadioButton man = ((RadioButton) $(register, "man"));

                    /*????????????????????????*/
                    String accountRegExp = "^[0-9,a-z,A-Z,\\u4e00-\\u9fa5]{1,15}$";
                    String nameRegExp = "^[a-z,A-Z,\\u4e00-\\u9fa5]{1,32}$";   //??????????????????+????????????
//                    String phoneRegExp = "^(((13[0-9])|(15[0-3][5-9])|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
                    String phoneRegExp="^1[0-9]{10}";
                    String ageRegExp = "^\\d{1,3}$";
                    String passwordReExp = "^[a-z,A-Z,0-9]{6,20}$";
                    String rePasswordRegExp = "^[a-z,A-Z,0-9]{6,20}$";
                    if (account.equals("") || name.equals("") || password.equals("") || rePassword.equals("") || age.equals("") || phone.equals("")) {
                        if (account.equals("")) {
                            register.setErrorTip("accountError", "??????????????????");
                        }
                        if (name.equals("")) {
                            register.setErrorTip("nameError", "??????????????????");
                        }
                        if (password.equals("")) {
                            register.setErrorTip("passwordError", "??????????????????");
                        }
                        if (rePassword.equals("")) {
                            register.setErrorTip("rePasswordError", "??????????????????");
                        }
                        if (age.equals("")) {
                            register.setErrorTip("ageError", "??????????????????");
                        }
                        if (phone.equals("")) {
                            register.setErrorTip("phoneError", "?????????????????????");
                        }
                    }
                    else if (!Pattern.matches(accountRegExp, account) || !Pattern.matches(nameRegExp, name) || !Pattern.matches(passwordReExp, password) || !Pattern.matches(rePasswordRegExp, rePassword) || !Pattern.matches(ageRegExp, age) || !Pattern.matches(phoneRegExp, phone)) {

                        if (!Pattern.matches(accountRegExp, account)) {
                            register.setErrorTip("accountError", "?????????,????????????????????????15??????????????????????????????");
                        }
                        if (!Pattern.matches(nameRegExp, name)) {
                            register.setErrorTip("nameError", "?????????????????????");
                        }
                        if (!Pattern.matches(passwordReExp, password)) {
                            register.setErrorTip("passwordError", "?????????,??????????????????6-20???????????????????????????");
                        }
                        if (!Pattern.matches(rePasswordRegExp, rePassword)) {
                            register.setErrorTip("rePasswordError", "?????????,??????????????????6-20???????????????????????????");
                        }
                        if (!Pattern.matches(ageRegExp, age)) {
                            register.setErrorTip("ageError", "??????????????????????????????");
                        }
                        if (!Pattern.matches(phoneRegExp, phone)) {
                            register.setErrorTip("phoneError", "????????????????????????");
                        }
                    }
                    else{
                        try {
                            ResultSet resultSet = database.execResult("SELECT * FROM user WHERE account=?",account);
                            if (!resultSet.next()) {
                                if (password.equals(rePassword)) {
                                    if (man.isSelected()) {
                                        sex = "man";
                                    } else {
                                        sex = "woman";
                                    }
                                    try {
                                        database.exec("INSERT INTO user set account=?,name=?, password=? ,age=? ,sex=?, phone=?, background=?", account, name, password, age, sex, phone,"background1");
                                        System.out.println("");
                                        register.close();
                                        register.clear();
                                        alert.setInformation("????????????");

                                        dialog.show();
                                        boolean ok=alert.exec();

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    register.setErrorTip("rePasswordError", "????????????????????????");
                                }

                            }
                            else
                            {
                                register.setErrorTip("accountError", "?????????,??????????????????");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    //??????????????????
    public void alterPersonExec() {
        ((Button) $(alterPerson, "submit")).setOnAction(event -> {
            alterPerson.resetErrorTip();
            String sex;
            String account = ((Label) $(alterPerson, "account")).getText();
            String label = ((TextArea) $(alterPerson, "label")).getText();
            String name = ((TextField) $(alterPerson, "name")).getText();
            String age = ((TextField) $(alterPerson, "age")).getText();
            String address = ((TextField) $(alterPerson, "address")).getText();
            String phone = ((TextField) $(alterPerson, "phone")).getText();
            String nameRegExp = "^[a-z,A-Z,\\u4e00-\\u9fa5]{1,100}$";
            // String phoneRegExp = "^(((13[0-9])|(15[0-3][5-9])|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
            String phoneRegExp = "^1[0-9]{10}";
            String ageRegExp = "^\\d{1,3}$";
            RadioButton man = ((RadioButton) $(alterPerson, "man"));
            RadioButton woman = ((RadioButton) $(alterPerson, "woman"));
            if (name.equals("") || phone.equals("") || age.equals("")) {
                if (name.equals("")) {
                    alterPerson.setErrorTip("nameError", "??????????????????");
                }
                if (phone.equals("")) {
                    alterPerson.setErrorTip("phoneError", "?????????????????????");
                }
                if (age.equals("")) {
                    alterPerson.setErrorTip("ageError", "??????????????????");
                }
            } else if (!Pattern.matches(nameRegExp, name) || !Pattern.matches(phoneRegExp, phone) || !Pattern.matches(ageRegExp, age)) {
                if (!Pattern.matches(nameRegExp, name)) {
                    alterPerson.setErrorTip("nameError", "?????????????????????");
                }
                if (!Pattern.matches(phoneRegExp, phone)) {
                    alterPerson.setErrorTip("phoneError", "????????????????????????");
                }
                if (!Pattern.matches(ageRegExp, age)) {
                    alterPerson.setErrorTip("ageError", "?????????????????????,?????????????????????");
                }
            } else {
                if (man.isSelected()) {
                    sex = "man";
                } else {
                    sex = "woman";
                }
                try {
                    userdata.setPhone(phone);
                    userdata.setLabel(label);
                    userdata.setSex(sex);
                    userdata.setAddress(address);
                    userdata.setName(name);
                    userdata.setAge(Integer.parseInt(age));
                    database.exec("UPDATE user SET label=?, name=?, age=?, address=?, phone=?, sex=?,head=? WHERE account=?", label, name, age, address, phone, sex, userdata.getHead(), account);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                homepage.setUserData("label", label);
                homepage.setUserData("name", name);
                homepage.setUserData("age", age);
                homepage.setUserData("address", address);
                homepage.setUserData("phone", phone);
                homepage.setUserData("sex", sex);
                setHeadPortrait(((Button) $(homepage, "head")), userdata.getHead());
                setHeadPortrait(((Button) $(homepage, "background")), "head1",userdata.getHead());
                setHeadPortrait(((Button) $(mainWindow, "individual")), userdata.getHead());
                mainWindow.setPersonalInfo(account,name,address,phone);
                alterPerson.close();
            }

        });
        alterHead();

    }
    public void FriendInfo(){

        ((Button) $(mainWindow,"moref")).setOnAction(event -> {
            int index = mainWindow.getFriendList().getSelectionModel().getSelectedIndex();
            String account = MsgData.accountList.get(index);
            if(account.equals("WeChat????????????"))
            {
                return;
            }
            else {
                if (friendPage.isShowing()) {
                    friendPage.close();
                }
                try {
                    ResultSet resultSet = database.execResult("SELECT * FROM user WHERE account=?", account);
                    resultSet.next();
                    friendPage.setFriendData(resultSet,((Label)$(mainWindow,"Y_account")).getText());
                    friendPage.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public void saveRemark(){
        ((Button) $(friendPage,"submit")).setOnAction(event -> {
            String remark = ((TextField) $(friendPage,"remark")).getText();
            if(remark.equals("")){
                return ;
            }
            int index = MsgData.accountList.indexOf(((Label) $(friendPage,"account")).getText());
            if(index==-1)
            {
                return ;
            }
            int index1 = mainWindow.getFriendList().getSelectionModel().getSelectedIndex();
            if(index == index1) {
                mainWindow.getFriendVector().get(index).setText(remark);
                ((Label) $(mainWindow,"Y_account")).setText(remark);
            }
            else
            {
                mainWindow.getFriendVector().get(index).setText(remark);
            }
            try {
                database.exec("UPDATE companion SET remark=? WHERE I_account=? AND Y_account =?",remark,userdata.getAccount(),((Label) $(friendPage,"account")).getText());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    public void OptionHead() {
        ((Button) $(headProtrait, "submit")).setOnAction((ActionEvent event) -> {
            RadioButton one = ((RadioButton) $(headProtrait, "one"));
            RadioButton two = ((RadioButton) $(headProtrait, "two"));
            RadioButton three = ((RadioButton) $(headProtrait, "three"));
            RadioButton four = ((RadioButton) $(headProtrait, "four"));
            RadioButton five = ((RadioButton) $(headProtrait, "five"));
            RadioButton six = ((RadioButton) $(headProtrait, "six"));
            RadioButton seven = ((RadioButton) $(headProtrait, "seven"));
            RadioButton eight = ((RadioButton) $(headProtrait, "eight"));
            RadioButton nine = ((RadioButton) $(headProtrait, "nine"));
            RadioButton ten = ((RadioButton) $(headProtrait, "ten"));
            if (one.isSelected()) {
                userdata.setHead("head1");
            } else if (two.isSelected()) {
                userdata.setHead("head2");
            } else if (three.isSelected()){
                userdata.setHead("head3");
            } else if (four.isSelected()) {
                userdata.setHead("head4");
            } else if (five.isSelected()){
                userdata.setHead("head5");
            } else if (six.isSelected()) {
                userdata.setHead("head6");
            }
            else if(seven.isSelected()){
                userdata.setHead("head7");
            }
            else if(eight.isSelected()){
                userdata.setHead("head8");
            }
            else if(nine.isSelected()){
                userdata.setHead("head9");
            }
            else if(ten.isSelected()){
                userdata.setHead("head10");
            }
            setHeadPortrait(((Button) $(register, "HeadPortrait")), userdata.getHead());
//            setHeadPortrait(((Button) $(alterPerson, "head")), userdata.getHead());
//            setHeadPortrait(((Button) $(alterPerson, "background")),"head1", userdata.getHead());
            headProtrait.close();
        });
    }

    public void find() {
        ((TextField) $(mainWindow, "search")).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = ((TextField) $(mainWindow, "search")).getText();
                int i = MsgData.accountList.indexOf(text);
                if (i!=-1) {
                    ((ListView) $(mainWindow, "FirendList")).getSelectionModel().select(i);
                }
                else
                {
//                    ((ListView) $(mainWindow, "FirendList")).getSelectionModel().select(0);
                    alert.setInformation("!?????????????????????");
                    alert.exec();
                    return;
                }
            }

        });
    }
    public static void setHeadPortrait(Button button, String head) {
        String ps=String.format("-fx-background-image: url(\"file:src/main/resources/View/Fxml/CSS/Image/head/%s.jpg\")", head);
        button.setStyle(ps);
        System.out.println(button.getStyle());
    }
    public static void setHeadPortrait(Button button, String file,String bg) {
        button.setStyle(String.format("-fx-background-image: url(\"file:src/main/resources/View/Fxml/CSS/Image/%s/%s.jpg\")",file, bg));
    }
    public void alterHead() {
        ((Button) $(alterPerson, "replace")).setOnAction(event -> {
            headProtrait.show();
        });
    }


    public void SearchFriends() {
        ((TextField) $(searchFriend, "textInput")).setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER) {
                searchFriend.clear();
                String UserName = ((TextField) $(searchFriend, "textInput")).getText();
                ((TextField) $(searchFriend,"textInput")).clear();
                if (UserName.equals("")) {
                    alert.setInformation("???????????????!");
                    alert.exec();
                } else if (UserName.equals(userdata.getAccount())) {
                    alert.setInformation("???????????????????????????!");
                    alert.exec();
                } else {
                    ResultSet resultSet = null;
                    try {
                        if(UserName.charAt(0)=='#') {
                            resultSet = database.execResult("select * from groupmember where groupid=?",UserName);
                            boolean flag = false;
                            while (resultSet.next()) {
                                if (resultSet.getString("groupid").indexOf(UserName) != -1) {
                                    searchFriend.add(resultSet.getString("head"), resultSet.getString("groupid"), mainWindow);
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                alert.setInformation("??????????????????!");
                                alert.exec();
                            }
                        }else {
                            resultSet = database.execResult("SELECT head,account FROM user WHERE account!=? AND account not in (SELECT Y_account FROM companion WHERE I_account = ?) ", userdata.getAccount(), userdata.getAccount());
                            boolean flag = false;
                            while (resultSet.next()) {
                                if (resultSet.getString("account").indexOf(UserName) != -1) {
                                    searchFriend.add(resultSet.getString("head"), resultSet.getString("account"), mainWindow);
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                alert.setInformation("??????????????????!");
                                alert.exec();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                return;
            }
        });
    }



    private  void SendGroup(String youAccount){
        try{
            ResultSet resultSet = database.execResult("SELECT * FROM groupmember WHERE groupid=?", youAccount);
            if(resultSet.next()){
                String input = ((TextField) $(mainWindow, "input")).getText();
                if (!input.equals("")) {
                    String line =youAccount ;
                    ResultSet resultSet_mem = database.execResult("SELECT * FROM groupmember WHERE groupid=?", youAccount);
                    while(resultSet_mem.next()){
                        line+=" "+resultSet_mem.getString("member");
                    }
                    /*??????????????????*/
                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                    Date date = new Date(System.currentTimeMillis());
                    database.exec("INSERT INTO chatrecord set owner=?,sender=?,msg=?,head=?,time=?",youAccount, userdata.getAccount(),input, userdata.getHead(),formatter.format(date));
//                            "INSERT INTO dialog set account=?,last_seen=?"
                    mainWindow.addRight(userdata.getHead(), input);//?????????????????????
                    System.out.println(line);
                    try {
//                        ChatManager.getInstance().send(line);//?????????????????????
                        int i = MsgData.accountList.indexOf(youAccount);//????????????
                        if (i != -1) {
                            MsgData.msg.get(i).add(userdata.getAccount() + " " + input);
                        }
                        ((TextField) $(mainWindow, "input")).clear();//????????????
                    } catch (Exception e) {
                        alert.setInformation("??????????????????!");
                        alert.exec();
                        e.printStackTrace();
                    }
                } else {
                    return;   //input???????????????
                }
            }else{
                alert.setInformation("????????????????????????!");
                alert.exec();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void sendMsgExec() {
        ((Button) $(mainWindow, "send")).setOnAction(event -> {
            //??????????????????????????????
            String youAccount = MsgData.accountList.get(mainWindow.getFriendList().getSelectionModel().getSelectedIndex());
            if(youAccount.charAt(0)=='#'){   //id#????????????
                SendGroup(youAccount);
            }else{    //?????????????????????
                try {
                    //?????????????????????
                    ResultSet resultSet = database.execResult("SELECT * FROM dialog WHERE account=?", youAccount);
                    if (resultSet.next()) {
                        String input = ((TextField) $(mainWindow, "input")).getText();
                        if (!input.equals("")) {
                            String line = userdata.getAccount() + " " + youAccount + " " + input;
                            mainWindow.addRight(userdata.getHead(), input);//?????????????????????
                            System.out.println(line);
                            try {
                                ChatManager.getInstance().send(line);//?????????????????????
                                int i = MsgData.accountList.indexOf(youAccount);//??????????????????
                                if (i != -1) {
                                    MsgData.msg.get(i).add(userdata.getAccount() + " " + input);
                                }
                                ((TextField) $(mainWindow, "input")).clear();//????????????
                            } catch (Exception e) {
                                alert.setInformation("??????????????????!");
                                alert.exec();
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                    else
                    {
                        alert.setInformation("?????????????????????????????????????????????????????????!");
                        alert.exec();
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            }

        });
        ((TextField)$(mainWindow,"input")).setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER){
                String youAccount = MsgData.accountList.get(mainWindow.getFriendList().getSelectionModel().getSelectedIndex());

                if(youAccount.charAt(0)=='#'){
                    SendGroup(youAccount);
                }else{
                    try {
                        //?????????????????????
                        ResultSet resultSet = database.execResult("SELECT * FROM dialog WHERE account=?", youAccount);
                        if (resultSet.next()||true) {
                            String input = ((TextField) $(mainWindow, "input")).getText();
                            if (!input.equals("")) {
                                String line = userdata.getAccount() + " " + youAccount + " " + input;
                                mainWindow.addRight(userdata.getHead(), input);//?????????????????????
                                System.out.println(line);
                                try {
                                    ChatManager.getInstance().send(line);//?????????????????????
                                    System.out.println("sendok");
                                    int i = MsgData.accountList.indexOf(youAccount);//??????????????????
                                    if (i != -1) {
                                        MsgData.msg.get(i).add(userdata.getAccount() + " " + input);
                                    }
                                    ((TextField) $(mainWindow, "input")).clear();//????????????
                                } catch (Exception e) {
                                    alert.setInformation("??????????????????!");
                                    alert.exec();
                                    e.printStackTrace();
                                }
                            } else {
                                return;
                            }
                        }
                        else
                        {
                            alert.setInformation("?????????????????????????????????????????????????????????!");
                            alert.exec();
                        }
                    } catch(SQLException e){
                        e.printStackTrace();
                    }
                }


            }
        });
    }



}
