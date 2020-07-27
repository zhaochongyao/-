package tcp;

import model.User;

import java.io.*;
import java.net.Socket;

public class Player {                           //玩家
    private User user;
    private OutputStream out;
    private InputStream in;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private Socket socket;
    private boolean remake;       //重置棋盘
    public Player(Socket socket){
        this.socket = socket;
        try{
            remake = false;
            out = socket.getOutputStream();
            in = socket.getInputStream();
            objOut = new MyObjectOutputStream(out);
            objIn = new MyObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRemake() {
        return remake;
    }

    public void setRemake(boolean remake) {
        this.remake = remake;
    }

    public void setUser(User user){
        this.user = user;
    }

    public ObjectOutputStream getObjOut() {
        return objOut;
    }

    public ObjectInputStream getObjIn() {
        return objIn;
    }
    public String getIp(){
        return socket.getInetAddress().toString();
    }
    public int getPort(){
        return socket.getPort();
    }

    public User getUser() {
        return user;
    }
}
