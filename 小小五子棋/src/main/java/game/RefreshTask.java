package game;

import javax.swing.*;

/***
 * 主窗口刷新线程**/
public class RefreshTask implements Runnable {
    private JFrame jf;                //刷新窗口
    private boolean refresh;          //是否刷新
    private static int FPS = 30;      //每秒刷新次数
    public RefreshTask(JFrame jf){
        this.jf = jf;
        refresh = true;
    }
    @Override
    public void run() {
        while(refresh){
            try{
                jf.repaint();
                Thread.sleep(1000/FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void overTask(){            //停止刷新
        refresh = false;
    }
}
