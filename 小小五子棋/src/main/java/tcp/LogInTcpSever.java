package tcp;

import dao.UserDao;
import model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class LogInTcpSever {
    private ServerSocket serverSocket;
    private ThreadPoolExecutor executor;
    private MainTask task;
    public LogInTcpSever(){
        try{
            init();
            task = new MainTask(this);
            executor.submit(task);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void init() throws IOException {
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        serverSocket = new ServerSocket(8000);
    }
    public void addTask(Runnable task){
        executor.submit(task);
    }
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void main(String[] args) {
        new LogInTcpSever();
        new GameServer();
    }
}

class MainTask implements Runnable{
    private LogInTcpSever tcpSever;

    public MainTask(LogInTcpSever tcpSever) {
        this.tcpSever = tcpSever;
    }

    @Override
    public void run() {
        try {                                                           //不断获取新用户的连接
            while (true){
                Socket s = tcpSever.getServerSocket().accept();
                System.out.println("检测到新用户连接...");
                System.out.println("用户地址:"+s.getInetAddress().getHostAddress()+":"+s.getPort());
                tcpSever.addTask(new GetTask(tcpSever,s));              //将用户添加进入子线程
                System.out.println("连接成功!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
class GetTask implements Runnable{
    private LogInTcpSever sever;
    private Socket socket;
    private InputStream in;
    private ObjectInputStream ObjIn;
    private ObjectOutputStream ObjOut;
    private OutputStream out;
    private DataOutputStream write;
    public GetTask(LogInTcpSever sever, Socket socket) {
        this.sever = sever;
        this.socket = socket;
        try{
            out = socket.getOutputStream();
            in = socket.getInputStream();
            ObjIn = new MyObjectInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
            try{
               while(true){

                   String log = null;
                   log = (String)ObjIn.readObject();

                   User user = null;
                   user = (User)ObjIn.readObject();

                   if(log.equals(null)){
                       socket.close();
                       break;
                   }
                   if(log.equals("UserLog")){                                      //用户进行登录操作
                       ObjOut = new ObjectOutputStream(out);
                       if(user!=null){
                           User u = null;
                           if((u=UserDao.getDao().logIn(user))!=null){              //若从数据库中找到用户则登录成功
                                ObjOut.writeObject(u);
                           }
                           else{
                                ObjOut.writeObject(u);
                           }
                       }
                   }
                   if(log.equals("UserRegister")){                                  //用户进行注册操作
                       write = new DataOutputStream(out);
                       if(user!=null){
                           if(UserDao.getDao().findUser(user.getAccount())==null){  //若未从数据库中找到用户则注册成功
                               UserDao.getDao().addUser(user);
                               write.write(("Register"+System.lineSeparator()).getBytes());
                           }else{
                               write.write(("DisRegister"+System.lineSeparator()).getBytes());
                           }
                       }

                   }

               }
            } catch (IOException e) {
                
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(2);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(3);
            }



    }
}


