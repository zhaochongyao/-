package game;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class InfoPanel extends JPanel {
    private GamePanel gamePanel;
    private User user;                      //用户
    private SimpleDateFormat format;        //日期格式化
    private User oppositeUser;              //对手
    private JTextArea textArea;            //对手信息面板
    private JTextArea chatArea;            //聊天面板
    private JScrollPane scrollPane;        //聊天面板滚动
    private JTextArea sendArea;            //聊天输入框
    private JButton send;                  //发送消息

    public InfoPanel(User user,GamePanel gamePanel){
        this.user = user;
        this.gamePanel = gamePanel;
        init();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("隶书",1,30));
        g.drawString("聊天框",150,325);
    }

    private void init(){

        format = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
        setSize(400,800);
        setLayout(null);
        textArea = new JTextArea();
        textArea.setFont(new Font("微软雅黑",1,15));
        textArea.setBounds(200,0,200,300);
        textArea.setEditable(false);
        chatArea = new JTextArea();
        chatArea.setFont(new Font("宋体",1,15));
        chatArea.setEditable(false);
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setEnabled(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setEnabled(false);
        scrollPane.setBounds(0,350,400,400);
        sendArea = new JTextArea();
        sendArea.setBounds(0,750,300,50);
        send = new JButton("发送");
        send.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sendArea.getText()!=null){
                    gamePanel.getClient().sendObject("chat"+sendArea.getText());
                    addMessage(sendArea.getText(),false);
                    sendArea.setText("");
                }
            }
        });
        send.setBounds(300,750,100,50);
        add(send);
        add(sendArea);
        add(scrollPane);
        add(textArea);
        textArea.append(user.Info()+System.lineSeparator()+System.lineSeparator());
    }
    public void setOppositeUser(User u){
        oppositeUser = u;
        textArea.append("对手信息:"+System.lineSeparator()+oppositeUser.Info());
    }
    public void addMessage(String msg,boolean flag){
        chatArea.append(makeMsg(msg,flag));
    }
    public String makeMsg(String msg,boolean flag){
        Date date = new Date();
        if (flag) {                            //对手信息
            return format.format(date)+"  "+oppositeUser.getName()+System.lineSeparator()+msg+System.lineSeparator();
        }else{                                 //自己信息
            return format.format(date)+"  "+user.getName()+System.lineSeparator()+msg+System.lineSeparator();
        }

    }

}
