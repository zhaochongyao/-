package game;

import AI.Game_main;

import client.LogInClient;
import dao.UserDao;
import model.User;

import javax.print.attribute.standard.JobName;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/*登录界面*/
public class LogInFrame extends JFrame {
    private LogInClient client;     //通信客户端
    private JButton log;            //登录按钮
    private JButton register;       //注册按钮
    private JTextField account;     //账号栏
    private JPasswordField password;//密码栏
    private JButton alone;          //单机游戏
    public LogInFrame(){
        init();
        componentInit();
    }
    private void init(){             //界面初始化
        client = new LogInClient();
        setSize(300,200);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("登录界面");
        setLayout(new GridLayout(2,1));
        setVisible(true);
    }
    private void componentInit(){  //组件初始化
        JLabel l1 = new JLabel("用户名");
        JLabel l2 = new JLabel("密码");
        log = new JButton("登录");
        register = new JButton("注册");
        alone = new JButton("单机游戏");
        account = new JTextField(19);
        password = new JPasswordField(19);
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        p1.add(l1);
        p1.add(account);
        p1.add(l2);
        p1.add(password);
        p2.add(log);
        p2.add(register);
        p2.add(alone);
        add(p1);
        add(p2);
        setVisible(true);
        alone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null,"请选择是否人机先手","选择",JOptionPane.YES_NO_OPTION);
                boolean flag;
                if(option==1){
                    flag = false;
                }else{
                    flag = true;
                }
                Game_main.Ai_Player = flag;
                MyMouseEvent myMouseEvent = new MyMouseEvent();
                Game_main.getPanel().addMouseListener(myMouseEvent);
                Game_main.initChessBoard();
                LogInFrame.this.dispose();
            }
        });
        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client = new LogInClient();
                User user = new User();
                user.setAccount(account.getText());
                user.setPassword(String.valueOf(password.getPassword()));
                User u = client.sendLogIn(user);
                if(u==null){
                    JOptionPane.showMessageDialog(
                            LogInFrame.this,
                            "账号或密码错误！",
                            "通知",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }else{
                    JOptionPane.showMessageDialog(
                            LogInFrame.this,
                            "登录成功",
                            "通知",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    new MainFrame(u);
                    LogInFrame.this.dispose();
                }
            }
        });
        register.addActionListener(new ActionListener() {
            private JTextField ac;
            private JTextField pwd;
            private JTextField name;
            private JButton btn;
            private JFrame jf;
            private String note;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("注册")) {
                    jf = new JFrame("注册界面");
                    jf.setSize(300, 200);
                    jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    jf.setLocationRelativeTo(null);
                    jf.setAlwaysOnTop(true);
                    jf.setLayout(new FlowLayout());
                    JLabel l1 = new JLabel("账号");
                    JLabel l2 = new JLabel("密码");
                    JLabel l3 = new JLabel("昵称");
                    ac = new JTextField(19);
                    pwd = new JTextField(19);
                    name = new JTextField(19);
                    btn = new JButton("确认注册");
                    btn.addActionListener(this);
                    jf.add(l1);
                    jf.add(ac);
                    jf.add(l2);
                    jf.add(pwd);
                    jf.add(l3);
                    jf.add(name);
                    jf.add(btn);
                    jf.setVisible(true);
                }
                if(e.getActionCommand().equals("确认注册")){

                    client = new LogInClient();
                    User user = new User();
                    user.setAccount(ac.getText());
                    user.setLevel(1);
                    user.setPassword(pwd.getText());
                    user.setName(name.getText());

                        if(client.sendRegister(user)){
                            note = "注册成功!";
                        }else{
                            note = "注册失败!用户名重复";
                        }

                    JOptionPane.showMessageDialog(
                            jf,
                            note,
                            "通知",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            }
        });
    }
    public static void main(String[] args) {
        new LogInFrame();
    }
}
// 实现鼠标事件接口
class MyMouseEvent implements MouseListener {
    public void mouseClicked(MouseEvent e){
        int x=round(e.getX()),y=round(e.getY());
        if(x>=45 && x<=675 && y>=45 && y<=675 && Game_main.chessBoard[y/45][x/45]==0 && !Game_main.Ai_Player){
            Game_main.putChess(x,y);
            if(!Game_main.isFinished){
                Game_main.Ai_Player = true;
                Game_main.myAI();
            }
            Game_main.isFinished=false;
        }
    }
    // 得到鼠标点击点附近的棋盘精准点
    public static int round(int x){
        return (x%45<22)?x/45*45:x/45*45+45;
    }
    public void mouseExited(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
}
