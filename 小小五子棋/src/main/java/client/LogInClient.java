package client;

import model.User;
import tcp.MyObjectOutputStream;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


public class LogInClient {
    private Socket socket;
    OutputStream out;
    ObjectOutputStream obj;
    public LogInClient(){
        init();
    }
    private void init(){
        try{
            socket = new Socket("127.0.0.1",8000);              //连接服务器
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public User sendLogIn(User user){                                   //发送登录信息
        try{
            out = socket.getOutputStream();
            obj = new MyObjectOutputStream(out);
            obj.writeObject(new String("UserLog"));
            obj.writeObject(user);
            InputStream in = socket.getInputStream();
            ObjectInputStream reader = new ObjectInputStream(in);
            User u = (User)reader.readObject();
            obj.writeObject(null);
            obj.writeObject(null);
            if(u!=null){
                u.setIp(socket.getInetAddress().toString());
                return u;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean sendRegister(User user){                             //发送注册信息
        try {
            out = socket.getOutputStream();
            obj = new MyObjectOutputStream(out);
            obj.writeObject(new String("UserRegister"));
            obj.writeObject(user);
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String result = reader.readLine();
            obj.writeObject(null);
            obj.writeObject(null);
            if(result.equals("Register"))
                return true;
            else if(result.equals("DisRegister"))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}


