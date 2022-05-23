package Controller;
import View.*;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DialogContraller extends WindowControler{
    Dialog dialog;

    public DialogContraller(Dialog d){
        dialog=d;

    }
    public Object $(String id){
        return dialog.$(id);
    }

    public void show(){
        dialog.show();
    }

    private void init(){
        ((Button) $("enter")).setOnAction(
                Event->{
                    dialog.resetErrorTip();
                    String UserName = ((TextField) $("UserName")).getText();
                    String Password = ((PasswordField) $("Password")).getText();

                }
        );
    }

}
