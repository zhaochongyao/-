package tcp;

import game.Point;
import model.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 游戏服务端
 * 接收客户端发送的游戏数据
 * 发送游戏数据给客户端
 * 根据ip地址匹配客户端进行游戏*/
public class GameServer {

    private CopyOnWriteArrayList<WorkTask> workTasks;
    private CopyOnWriteArrayList<Player> players;
    private ServerSocket serverSocket;
    private ThreadPoolExecutor executor;
    public GameServer(){
        init();
    }
    private void init(){
        workTasks = new CopyOnWriteArrayList<>();
        players = new CopyOnWriteArrayList<>();

        try{

            serverSocket = new ServerSocket(8001);
            executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            FindUserTask task = new FindUserTask(this);
            executor.submit(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addTask(Runnable task){
        executor.submit(task);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void addPlayer(Player player){
        players.add(player);
    }
    public Player findPlayer(String ip){
        for(Player player:players){
            if (player.getIp().equals(ip))
                return player;
        }
        return null;
    }
    public void addWorkTask(WorkTask task){
        workTasks.add(task);
    }
    public boolean getRandomColor(){
        Random random = new Random();
        if(random.nextInt(100)%2==0){
            return true;
        }
        return false;
    }
    public CopyOnWriteArrayList<WorkTask> getWorkTasks() {
        return workTasks;
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();
    }
}
class FindUserTask implements Runnable{
    private GameServer server;

    public FindUserTask(GameServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try{
            while(true){
                Socket s = null;
                s = server.getServerSocket().accept();
                System.out.println("检测到新用户连接...");
                System.out.println("用户地址:"+s.getInetAddress().getHostAddress()+":"+s.getPort());
                Player player = new Player(s);
                WorkTask task = new WorkTask(player,server,null);
                server.addTask(task);
                server.addPlayer(player);
                server.addWorkTask(task);
                for (int i = 0; i < server.getWorkTasks().size(); i++) {
                    System.out.println("现有玩家有:");
                    System.out.println(server.getWorkTasks().get(i).getPlayer().getIp());
                }
                System.out.println("连接成功!");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class WorkTask implements Runnable{
    private CheckMap checkMap;
    private GameServer server;
    private Player player;
    private Player oppositePlayer;
    private WorkTask oppositeWorkTask;
    private boolean color;
    public WorkTask(Player player,GameServer server,CheckMap checkMap) {
        this.player = player;

        oppositePlayer = null;
        this.server = server;
        this.checkMap = checkMap;
    }

    public void setOppositeWorkTask(WorkTask oppositeWorkTask) {
        this.oppositeWorkTask = oppositeWorkTask;
    }

    public void setCheckMap(CheckMap checkMap) {
        this.checkMap = checkMap;
    }

    public void setOppositePlayer(Player oppositePlayer) {
        this.oppositePlayer = oppositePlayer;
    }
    public void remakeMap(){
        checkMap.remake();
    }
    public Player getPlayer() {
        return player;
    }
    public void WriteColor(boolean color) throws IOException {
        player.getObjOut().writeObject(new Point(-1,-1,color));
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public Player linkUser(String ip) throws IOException {                  //根据IP地址匹配玩家
        for (int i = 0; i < server.getWorkTasks().size(); i++) {
            if(server.getWorkTasks().get(i).player.getIp().equals(ip)&&server.getWorkTasks().get(i).player.getPort()!=player.getPort()){
                System.out.println("对方端口:"+server.getWorkTasks().get(i).player.getPort());
                oppositeWorkTask = server.getWorkTasks().get(i);    //设置对手的线程
                oppositeWorkTask.setOppositeWorkTask(this);
                checkMap = new CheckMap();                          //初始化服务器端地图
                oppositeWorkTask.setOppositePlayer(player);
                oppositeWorkTask.setCheckMap(checkMap);
                boolean color = server.getRandomColor();            //随机获取颜色
                this.color = color;
                oppositeWorkTask.setColor(!color);                  //发送颜色给客户端
                oppositeWorkTask.WriteColor(color);
                WriteColor(!color);
                System.out.println("执行完毕");
                return server.getWorkTasks().get(i).player;
            }
        }
        return null;
    }
    @Override
    public void run() {
        try{
            while(true){

                Object o = player.getObjIn().readObject();
                if(o.getClass().getName().equals("java.lang.String")){                          //接收字符串信息
                    String ip = (String) o;
                    if(player.isRemake()&&oppositePlayer.isRemake()){
                        remakeMap();
                        boolean color = server.getRandomColor();
                        oppositeWorkTask.WriteColor(color);
                        WriteColor(!color);
                        player.setRemake(false);
                        oppositePlayer.setRemake(false);
                    }else if(ip.equals("/ask/remake/")){                                //重开信息
                        player.setRemake(true);
                    }else if(ip.equals("rePoint")){                                        //悔棋信息
                        oppositePlayer.getObjOut().writeObject("rePoint");
                    }else if(ip.equals("okToRePoint")){                                 //对手同意悔棋信息
                        oppositePlayer.getObjOut().writeObject("okToRePoint");
                        Point p = checkMap.disPoint();
                        Point point = new Point(p.getX(),p.getY(),!p.isColor());
                        oppositePlayer.getObjOut().writeObject(point);
                        player.getObjOut().writeObject(point);
                    }else if(ip.equals("video")){                                       //游戏回放
                        player.getObjOut().writeObject("video");
                        oppositePlayer.getObjOut().writeObject("video");
                        for (int i = 0; i < checkMap.getList().size(); i++) {
                            Point p = checkMap.getList().get(i);
                            player.getObjOut().writeObject(p);
                            oppositePlayer.getObjOut().writeObject(p);
                            Thread.sleep(1500);
                        }
                        player.getObjOut().writeObject("videoOver");
                        oppositePlayer.getObjOut().writeObject("videoOver");
                    }else if(ip.equals("lose")){                                    //认输信息
                        System.out.println("认输");
                        oppositePlayer.getObjOut().writeObject(new Point(-3,-3,!color));
                        player.getObjOut().writeObject(new Point(-3,-3,!color));
                    }else if(ip.contains("chat")){                                        //聊天框信息
                        System.out.println("收到聊天信息");
                        oppositePlayer.getObjOut().writeObject(ip);
                    }else if(ip.equals("peace")){                                    //和棋信息
                        oppositePlayer.getObjOut().writeObject("peace");
                    }else if(ip.equals("okToPeace")){                               //同意和棋信息
                        player.getObjOut().writeObject(new Point(-4,-4,true));
                        player.getObjOut().writeObject(new Point(-4,-4,true));
                    }
                    else{
                        System.out.println("我的端口号:"+player.getPort());
                        Player p = linkUser(ip);
                        if(p!=null){
                            oppositePlayer = p;
                        }
                    }
                }
                if(oppositePlayer!=null){                                             //下子信息
                    System.out.println(player.getIp()+player.getPort()+"正在接收");
                    if(o.getClass().getName().equals("game.Point")){
                        System.out.println("接收到点");
                        Point p = (Point) o;
                        checkMap.point(p.getX(),p.getY(),p.isColor());
                        oppositePlayer.getObjOut().writeObject(p);
                        player.getObjOut().writeObject(p);
                        if(checkMap.judgeWin(p.getY(),p.getX())==1){
                            System.out.println("检测到胜利");
                            oppositePlayer.getObjOut().writeObject(new Point(-2,-2,p.isColor()));
                            player.getObjOut().writeObject(new Point(-2,-2,p.isColor()));
                        }
                    }
                    if(o.getClass().getName().equals("model.User")){
                        System.out.println("服务器检测到用户");
                        User u = (User) o;
                        oppositePlayer.getObjOut().writeObject(u);
                    }
                }
            }
        } catch (IOException e) {
            server.getWorkTasks().remove(this);
            System.out.println("用户断开连接");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
