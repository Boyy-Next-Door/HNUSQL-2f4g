package client;
import java.io.Serializable;

public class Info implements Serializable{
    private String cookie;
    private String command;

    public Info(String cookie,String command){
        super();
        this.cookie=cookie;
        this.command=command;
    }

    public String getCookie() {
        return cookie;
    }
    public String getCommand(){
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
    public String toString(){
        return "cookie="+cookie+", command="+command;
    }
}
