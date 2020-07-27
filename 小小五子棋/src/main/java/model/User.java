package model;

import java.io.Serializable;
import java.util.Random;

public class User implements Serializable {

    private String name;        //用户姓名
    private String account;     //账号
    private String password;    //密码
    private int level;          //等级
    private String ip;          //IP地址
    private int port;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String Info(){
        return "昵称:"+name+System.lineSeparator()+"等级:"+level+System.lineSeparator()+
                "IP:"+ip+"端口："+port;
    }
}
