package tcp;

import game.Point;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient {
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    public GameClient(){
        init();
    }
    private void init(){
        try{
            socket = new Socket("127.0.0.1",8001);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            objIn = new MyObjectInputStream(in);
            objOut = new MyObjectOutputStream(out);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Object getObject(){                               //读取对象
        Object o = null;
        try{
            o = objIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }
    public User getUser(){                              //读取用户对象
        User u = null;
        try{
            u = (User)objIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return u;
    }
    public void sendObject(Object object){              //发送对象
        try{
            objOut.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

