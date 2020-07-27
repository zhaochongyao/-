package game;

import com.sun.tools.javac.Main;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrame implements WindowListener {
    private JFrame jf;          //主窗口
    private RefreshTask task;   //重绘线程
    private GamePanel gamePanel;  //游戏面板
    private PointTask pointTask;    //接收线程
    private InfoPanel infoPanel;  //信息面板
    private User user;           //用户
    public MainFrame(User user){
        this.user = user;
        initFrame();
        initThread();
    }

    private void initFrame(){       //初始化主窗口
        jf = new JFrame();
        gamePanel = new GamePanel(user);
        infoPanel = new InfoPanel(user,gamePanel);
        jf.setSize(1400,830);            //设置窗口大小
        jf.setLayout(null);
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  //设置窗口关闭方式
        jf.setLocationRelativeTo(null);                 //设置窗口位置
        jf.setTitle("小小五子棋");                   //设置标题
        jf.addWindowListener(this);             //添加窗口监听器
        infoPanel.setLocation(1000,0);
        jf.add(infoPanel);
        jf.add(gamePanel);
        jf.setVisible(true);
    }
    private void initThread(){      //初始化线程
        task = new RefreshTask(jf);
        pointTask = new PointTask(gamePanel,infoPanel);
        new Thread(task).start();
        new Thread(pointTask).start();
    }

    public static void main(String[] args) {
        //new MainFrame(new User());
       new LogInFrame();
    }
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        task.overTask();            //窗口关闭时终止刷新线程
        pointTask.overTask();       //窗口关闭时终止接收线程
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
class PointTask implements Runnable{
    private GamePanel gamePanel;
    private InfoPanel infoPanel;
    private boolean isGoing;
    public PointTask(GamePanel gamePanel,InfoPanel infoPanel) {
        this.gamePanel = gamePanel;
        this.infoPanel = infoPanel;
        isGoing = true;
    }
    @Override
    public void run() {
        while(isGoing){

            if(gamePanel.isBegin()){
                gamePanel.getClient().sendObject(gamePanel.getUser());
                gamePanel.setBegin(false);
            }
            Object o = gamePanel.getClient().getObject();
            System.out.println("收到");
            Point p = null;
            User u = null;
            if(o.getClass().getName().equals("game.Point")){
               p = (Point) o;
            }
            if(o.getClass().getName().equals("model.User")){
                u = (User) o;
            }
            if(o.getClass().getName().equals("java.lang.String")){
                String option = (String) o;
                if(option.equals("rePoint")){
                    int a = JOptionPane.showConfirmDialog(null,"对方申请悔棋,是否同意?","通知",JOptionPane.YES_NO_OPTION);
                    if(a==0){
                        gamePanel.setGameGoing(false);
                        gamePanel.getClient().sendObject("okToRePoint");
                    }
                }
                if(option.equals("okToRePoint")){
                    gamePanel.setGameGoing(true);
                }
                if(option.equals("video")){
                    gamePanel.setVideo(false);
                    gamePanel.setReGame(false);
                    gamePanel.setRePoint(false);
                    gamePanel.remake();
                }
                if(option.equals("videoOver")){
                    gamePanel.setVideo(true);
                    gamePanel.setReGame(true);
                    gamePanel.setRePoint(true);
                }
                if(option.contains("chat")){
                    System.out.println("客户端收到消息");
                    String line = option.substring(4);
                    infoPanel.addMessage(line,true);
                }
                if(option.equals("peace")){
                    int a = JOptionPane.showConfirmDialog(null,"对方申请和棋,是否同意?","通知",JOptionPane.YES_NO_OPTION);
                    if(a==0){
                        gamePanel.setGameGoing(false);
                        gamePanel.getClient().sendObject("okToPeace");
                    }
                }
            }
            if(p!=null){
                gamePanel.point(p);
            }
            if(u!=null){
                gamePanel.setOppositeUser(u);
                infoPanel.setOppositeUser(u);
                System.out.println("收到用户");
            }

            System.out.println("结束");
        }
    }
    public void overTask(){
        isGoing = false;
    }
}