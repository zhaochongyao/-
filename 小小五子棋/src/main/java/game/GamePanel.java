package game;

import model.User;
import tcp.GameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel {
    private MyActionListener listener;
    private boolean begin;
    private Boolean color;
    private User user;
    private User oppositeUser;
    private boolean gameGoing;
    private int[][] map;
    private GameClient client;
    private JButton Game;         //开始游戏
    private JButton reGame;       //再来一局按钮
    private JButton rePoint;      //悔棋
    private JButton Video;        //游戏回放
    private JButton lose;         //认输
    private JButton peace;       //和棋
    private Image background;     //背景图片
    private Image image1;
    private Image image2;
    public GamePanel(User user){
        this.user = user;
        init();
        begin = false;
    }
    public void remake(){
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                map[i][j] = 0;
            }
        }
    }
    public void setOppositeUser(User u){
        oppositeUser = u;
    }
    public void setVideo(boolean flag) {
        Video.setVisible(flag);
    }

    public void setRePoint(boolean flag){
        rePoint.setEnabled(flag);
    }
    public void setReGame(boolean flag) {
        this.reGame.setVisible(flag);
    }

    public boolean isBegin() {
        return begin;
    }

    public void setBegin(boolean begin) {
        this.begin = begin;
    }

    public User getUser() {
        return user;
    }

    public void setGameGoing(boolean gameGoing) {
        this.gameGoing = gameGoing;
    }

    private void init(){                        //按钮组件和成员变量初始化
        gameGoing = false;
        background = Toolkit.getDefaultToolkit().getImage("src/main/java/image/background.jpg");    //图片初始化
        image1 = Toolkit.getDefaultToolkit().getImage("src/main/java/image/image1.jpg");
        image2 = Toolkit.getDefaultToolkit().getImage("src/main/java/image/image2.jpg");
        client = new GameClient();
        listener = new MyActionListener(client,this);
        setSize(1000,800);
        map = new int[15][15];
        reGame = new JButton("再来一局");
        reGame.setVisible(false);
        Game = new JButton("开始游戏");
        rePoint = new JButton("悔棋");
        rePoint.setEnabled(false);
        Video = new JButton("游戏回放");
        Video.setVisible(false);
        lose = new JButton("认输");
        lose.setVisible(false);
        peace = new JButton("和棋");
        peace.setVisible(true);
        peace.addActionListener(listener);
        lose.addActionListener(listener);
        Video.addActionListener(listener);
        rePoint.addActionListener(listener);
        Game.addActionListener(listener);
        reGame.addActionListener(listener);
        add(peace);
        add(rePoint);
        add(reGame);
        add(Game);
        add(Video);
        add(lose);
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(gameGoing){
                    int x = (e.getX()-150)/50;
                    int y = (e.getY()-50)/50;
                    int a = ((e.getX()-100)%50);
                    int b = (e.getY()%50);
                    if(a>25){
                        x++;
                    }
                    if(b>25){
                        y++;
                    }
                    if(map[x][y]==0){
                        Point p = new Point(x,y,color);
                        point(p);
                        client.sendObject(p);
                        gameGoing = false;
                        rePoint.setEnabled(true);
                    }
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;                                                    //绘制半透明棋盘
        Composite composite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
        g2d.drawImage(background,0,0,null);
        g2d.setColor(new Color(0xAABBAA));
        g2d.fill3DRect(0,0,getWidth(),getHeight(),true);
        g2d.setComposite(composite);
        g2d.setFont(new Font("隶书",Font.ITALIC|Font.BOLD,48));
        g2d.setColor(Color.BLUE);
        if(image1!=null&&user!=null&&image2!=null&&oppositeUser!=null){                                         //绘制头像及昵称信息
            if(color!=null){
                g2d.drawString(user.getName(),25,500);
                g2d.drawString(oppositeUser.getName(),875,500);
                if(color){
                    g2d.drawImage(image1,25,350,100,100,null);
                    g2d.drawImage(image2,875,350,100,100,null);
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(50,600,50,50);
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(900,600,50,50);
                }else{
                    g2d.drawImage(image2,25,350,100,100,null);
                    g2d.drawImage(image1,875,350,100,100,null);
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(50,600,50,50);
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(900,600,50,50);
                }

            }
        }
        g.setColor(Color.BLACK);

        g.drawRect(150,50,700,700);                            //绘制棋盘线
        for (int i = 0; i < 15; i++) {
            g.drawLine(150+i*50,50,150+i*50,750);
        }
        for (int i = 0; i < 15; i++) {
            g.drawLine(150,50+i*50,850,50+i*50);
        }
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if(map[i][j]==1){          //白棋
                    g.setColor(Color.WHITE);
                    g.fillOval(150+i*50-13,50+j*50-13,26,26);
                }
                if(map[i][j]==-1){
                    g.setColor(Color.BLACK);
                    g.fillOval(150+i*50-13,50+j*50-13,26,26);
                }
            }
        }
        paintComponents(g);
    }

    public void point(Point p){                              //下子

        if(p.getX()==-1&&p.getY()==-1)                //初始化颜色
        {
            if(color==null){
                begin = true;
            }
            color = p.isColor();
            gameGoing = !p.isColor();

        }
        else if(p.getX()==-2&&p.getY()==-2){          //胜利信息
            String msg;
            if(p.isColor()){
                msg = "白棋胜利";
            }else{
                msg = "黑棋胜利";
            }
            gameGoing = false;
            JOptionPane.showMessageDialog(
                    this,
                    msg,
                    "游戏结束",
                    JOptionPane.INFORMATION_MESSAGE
            );
            reGame.setVisible(true);
            Video.setVisible(true);
        }else if(p.getX()==-3&&p.getY()==-3){       //认输信息
            String msg;
            if(p.isColor()){
                msg = "白棋认输,黑棋胜利";
            }else{
                msg = "黑棋认输,白棋胜利";
            }
            gameGoing = false;
            JOptionPane.showMessageDialog(
                    this,
                    msg,
                    "游戏结束",
                    JOptionPane.INFORMATION_MESSAGE
            );
            reGame.setVisible(true);
            Video.setVisible(true);
        }
        else if(p.getX()==-4&&p.getY()==-4){
            gameGoing = false;
            JOptionPane.showMessageDialog(
                    this,
                    "和棋",
                    "游戏结束",
                    JOptionPane.INFORMATION_MESSAGE
            );
            reGame.setVisible(true);
            Video.setVisible(true);
        }
        else {                                              //下子信息
            if(p.isColor()){
                if(map[p.getX()][p.getY()]==-1){
                    map[p.getX()][p.getY()] = 0;
                }else{
                    map[p.getX()][p.getY()] = 1;
                }
            }else{
                if(map[p.getX()][p.getY()]==1){
                    map[p.getX()][p.getY()] = 0;
                }else{
                    map[p.getX()][p.getY()] = -1;
                }
            }
            if(p.isColor()!=color){
                Game.setVisible(false);
                gameGoing = true;
                rePoint.setEnabled(false);
                lose.setVisible(true);
            }
        }
    }

    public GameClient getClient() {
        return client;
    }
}
class MyActionListener implements ActionListener{
    private GameClient client;
    private GamePanel gamePanel;

    public MyActionListener(GameClient client, GamePanel gamePanel) {
        this.client = client;
        this.gamePanel = gamePanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("开始游戏")){
            String line = JOptionPane.showInputDialog(null, "请输入对手的IP地址：\n", "连接", JOptionPane.PLAIN_MESSAGE);
            client.sendObject("/"+line);
        }
        if(e.getActionCommand().equals("再来一局")){
            gamePanel.remake();
            client.sendObject("/ask/remake/");
            client.sendObject("1");
            gamePanel.setReGame(false);
            gamePanel.setVideo(false);
        }
        if(e.getActionCommand().equals("悔棋")){
            gamePanel.setRePoint(false);
            gamePanel.getClient().sendObject("rePoint");
        }
        if(e.getActionCommand().equals("游戏回放")){
            gamePanel.getClient().sendObject("video");
        }
        if(e.getActionCommand().equals("认输")){
            gamePanel.getClient().sendObject("lose");
        }
        if(e.getActionCommand().contains("和棋")){
            gamePanel.getClient().sendObject("peace");
        }
    }
}
