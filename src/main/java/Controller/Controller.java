package Controller;

import Model.Data.Userdata;
import Model.DatabaseModel;
import View.*;
import View.Alert;
import View.Dialog;
import javafx.event.Event;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    //    public static SearchFriend searchFriend;
    private HeadProtrait headProtrait;
    private String friendName;
    private String friendHead;
    public static Alert alert;
    public static MainWindow mainWindow;


    public Controller() throws IOException {
        database = new DatabaseModel();

        dialog = new Dialog();

        register = new Register();

        userdata = new Userdata();

        alert=new Alert();

        mainWindow=new MainWindow();
    }


    /*
       根据窗口名，加id号，可以找到窗口中相应控件的对象。
     */
    private Object $(window window, String id) {
        return (Object) window.getRoot().lookup("#" + id);
    }

    public void Exec() {
        database.connect();
        dialogExec();
        registerExec();
        dialog.show();
        initEvent();
//        alert.show();
    }


    /*
        完成窗口之间的跳转
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


    }

    /*
           窗口登录逻辑，，，主要是enter按钮的编写
     */
    private void dialogExec() {
        ((Button) $(dialog, "enter")).setOnAction(event -> {
            dialog.resetErrorTip();

            /*数据库插入测试*/
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
//            System.out.println(UserName+"密码："+Password);
            if (UserName.equals("") || Password.equals("")) {
                if (UserName.equals("")) {
                    dialog.setErrorTip("accountError", "！未输入账号");
                }
                if (Password.equals("")) {
                    dialog.setErrorTip("passwordError", "！未输入密码");
                }
            } else {
                ResultSet resultSet = null;
                try {
                    resultSet = database.execResult("SELECT * FROM user WHERE account=?", UserName);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try{
                    /*第一次查询：user表 里面有没有账号*/
                    if(resultSet.next()){
                        if (resultSet.getString("password")!=null
                                &&resultSet.getString("password").equals(Password)){
                            ResultSet set = database.execResult("SELECT * FROM dialog WHERE account = ?", UserName);
                            if (set.next()) {
                                dialog.setErrorTip("accountError", "该账号已经登入，不能重复登入!");
                            } else {
//                                database.exec("INSERT INTO dialog account=?", UserName);//登入记录
                                System.out.println("login successfully!");
                                userdata.setUserdata(resultSet);
                                mainWindow.setPersonalInfo(userdata.getName(), userdata.getAccount(), userdata.getAddress(), userdata.getPhone());
                                mainWindow.show();
                            }

//                            alert.setInformation("登录成功");
//                            boolean ok=alert.exec();

                        }else{
                            dialog.setErrorTip("passwordError", "！密码有误"+resultSet.getString("password"));

                        }

                    }else{
                        dialog.setErrorTip("accountError", "！账号未注册");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
         }
        );
    }
    /*
    注册:
    钟悦东,完成插入数据库
    */
    private void registerExec(){
        ((Button)$(register,"register")).setOnAction(
                event->{
                    register.resetErrorTip();
                    /*获取各个框中的信息*/
                    String account = ((TextField) $(register, "account")).getText();
                    String name = ((TextField) $(register, "name")).getText();
                    String password = ((PasswordField) $(register, "password")).getText();
                    String rePassword = ((PasswordField) $(register, "rePassword")).getText();
                    String age = ((TextField) $(register, "age")).getText();
                    String sex;
                    String phone = ((TextField) $(register, "phone")).getText();

                    RadioButton man = ((RadioButton) $(register, "man"));

                    /*格式的正则匹配式*/
                    String accountRegExp = "^[0-9,a-z,A-Z,\\u4e00-\\u9fa5]{1,15}$";
                    String nameRegExp = "^[a-z,A-Z,\\u4e00-\\u9fa5]{1,32}$";   //只能英文字母+中文字符
//                    String phoneRegExp = "^(((13[0-9])|(15[0-3][5-9])|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
                    String phoneRegExp="^1[0-9]{10}";
                    String ageRegExp = "^\\d{1,3}$";
                    String passwordReExp = "^[a-z,A-Z,0-9]{6,20}$";
                    String rePasswordRegExp = "^[a-z,A-Z,0-9]{6,20}$";
                    if (account.equals("") || name.equals("") || password.equals("") || rePassword.equals("") || age.equals("") || phone.equals("")) {
                        if (account.equals("")) {
                            register.setErrorTip("accountError", "！未输入账号");
                        }
                        if (name.equals("")) {
                            register.setErrorTip("nameError", "！未输入姓名");
                        }
                        if (password.equals("")) {
                            register.setErrorTip("passwordError", "！未输入密码");
                        }
                        if (rePassword.equals("")) {
                            register.setErrorTip("rePasswordError", "！未输入密码");
                        }
                        if (age.equals("")) {
                            register.setErrorTip("ageError", "！未输入年龄");
                        }
                        if (phone.equals("")) {
                            register.setErrorTip("phoneError", "！未输入电话号");
                        }
                    }
                    else if (!Pattern.matches(accountRegExp, account) || !Pattern.matches(nameRegExp, name) || !Pattern.matches(passwordReExp, password) || !Pattern.matches(rePasswordRegExp, rePassword) || !Pattern.matches(ageRegExp, age) || !Pattern.matches(phoneRegExp, phone)) {

                        if (!Pattern.matches(accountRegExp, account)) {
                            register.setErrorTip("accountError", "！错误,账号是长度不超过15位的中文和英文和数字");
                        }
                        if (!Pattern.matches(nameRegExp, name)) {
                            register.setErrorTip("nameError", "！姓名格式错误");
                        }
                        if (!Pattern.matches(passwordReExp, password)) {
                            register.setErrorTip("passwordError", "！错误,密码是长度在6-20位的英文字母和数字");
                        }
                        if (!Pattern.matches(rePasswordRegExp, rePassword)) {
                            register.setErrorTip("rePasswordError", "！错误,密码是长度在6-20位的英文字母和数字");
                        }
                        if (!Pattern.matches(ageRegExp, age)) {
                            register.setErrorTip("ageError", "！错误年龄只能是数字");
                        }
                        if (!Pattern.matches(phoneRegExp, phone)) {
                            register.setErrorTip("phoneError", "！电话号格式错误");
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
                                        alert.setInformation("注册成功");

                                        dialog.show();
                                        boolean ok=alert.exec();

                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    register.setErrorTip("rePasswordError", "！两次密码不一致");
                                }

                            }
                            else
                            {
                                register.setErrorTip("accountError", "！错误,该账号已存在");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

}
