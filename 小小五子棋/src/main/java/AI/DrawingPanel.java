package AI;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public final class DrawingPanel implements ActionListener, MouseMotionListener,  WindowListener {
    // 常量
    private static final String TITLE              = "五子棋";
    private static final boolean DEBUG             = true; 	  // DeBug 开关
    private static int instances = 0;
    private static Thread shutdownThread = null;


    // 返回主线程是否在运行 main is active
    private static boolean mainIsActive() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        int activeCount = group.activeCount();

        // 在线程组中寻找主线程
        Thread[] threads = new Thread[activeCount];
        group.enumerate(threads);
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            String name = ("" + thread.getName()).toLowerCase();
            if (name.indexOf("main") >= 0 ||
                    name.indexOf("testrunner-assignmentrunner") >= 0) {
                return thread.isAlive();
            }
        }

        // 没有找到主线程
        return false;
    }

    // 自定义一个ImagePanel
    private class ImagePanel extends JPanel  {
        private static final long serialVersionUID = 0;
        private Image image;

        public ImagePanel(Image image) {

            setBackground(new Color(247,203,104));
            setImage(image);
            setBackground(Color.GREEN);     //棋盘背景
            setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
            setAlignmentX(0.0f);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (currentZoom != 1) {
                g2.scale(currentZoom, currentZoom);
            }
            g2.drawImage(image, 0, 0, this);

        }

        public void setImage(Image image) {
            this.image = image;
            repaint();
        }
    }

    // 控件
    private int width, height;             // 窗口 frame 的大小
    private JFrame frame;                  // 总窗口的 frame
    private JPanel panel;                  // 总的画布面板
    private ImagePanel imagePanel;         // 真正的绘画面板
    private BufferedImage image;           // 记录绘图的情况
    private Graphics2D g2;                 // 2D绘图 graphics context
    private JLabel statusBar;              // 状态栏显示鼠标移动的位置
    private Timer timer;                   // 绘制的动画时间
    private Color backgroundColor = Color.WHITE;
    private boolean PRETTY = true;         // 消除锯齿操作true to anti-alias
    private int currentZoom = 1;
    private int initialPixel;              // 初始化每个像素点

    // 根据width和height绘制一个panel
    public DrawingPanel(int width, int height) {


        //synchronized保证在同一时刻最多只有一个线程执行该段代码
        synchronized (getClass()) {
            instances++;
            if (shutdownThread == null) {
                shutdownThread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            while (true) {
                                //完成执行主线程已经挂掉
                                if (instances == 0 && !mainIsActive()) {
                                    try {
                                        System.exit(0);
                                    } catch (SecurityException sex) {}
                                }

                                Thread.sleep(250);
                            }
                        } catch (Exception e) {}
                    }
                });
                shutdownThread.setPriority(Thread.MIN_PRIORITY);
                shutdownThread.start();
            }
        }
        this.width = width;
        this.height = height;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        initialPixel = image.getRGB(0, 0);
        g2 = (Graphics2D) image.getGraphics();
        g2.setColor(Color.BLACK);
        if (PRETTY) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}

            statusBar = new JLabel(" ");
            statusBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panel.setBackground(backgroundColor);
            panel.setPreferredSize(new Dimension(width, height));
            imagePanel = new ImagePanel(image);
            imagePanel.setBackground(backgroundColor);
            panel.setBackground(new Color(247,203,104));
            imagePanel.setBackground(new Color(247,203,104));
            panel.add(imagePanel);

            // 监听鼠标事件
            panel.addMouseMotionListener(this);

            // 主界面窗格
            frame = new JFrame(TITLE);
            frame.addWindowListener(this);
            JScrollPane center = new JScrollPane(panel);
            frame.getContentPane().add(center);
            frame.getContentPane().add(statusBar, "South");
            frame.setBackground(Color.WHITE);

            frame.pack();
            center(frame);
            frame.setVisible(true);

            // 重绘update
            timer = new Timer(50, this);
            timer.start();

    }

    //初始化UI组件
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Timer) {
            // 重绘
            panel.repaint();
        } else if (e.getActionCommand().equals("退出")) {
            exit();
        }
    }

    public void addMouseListener(MouseListener listener) {
        panel.addMouseListener(listener);
    }



    // 清除所有的线/颜色
    public void clear() {
        int[] pixels = new int[width * height];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = initialPixel;
        }
        image.setRGB(0, 0, width, height, pixels, 0, 1);
    }

    // 获得Graphics2D对象
    public Graphics2D getGraphics() {
        return g2;
    }


    // 监听鼠标行为并将坐标显示在statusbar上
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / currentZoom;
        int y = e.getY() / currentZoom;
    }

    // 关闭，退出
    public void windowClosing(WindowEvent event) {
        frame.setVisible(false);
        synchronized (getClass()) {
            instances--;
        }
        frame.dispose();
    }
    // 实现WindowListener必须的方法（这些方法目前未使用）
    public void windowActivated(WindowEvent event) {}
    public void windowClosed(WindowEvent event) {}
    public void windowDeactivated(WindowEvent event) {}
    public void windowDeiconified(WindowEvent event) {}
    public void windowIconified(WindowEvent event) {}
    public void windowOpened(WindowEvent event) {}
    // 把主窗口放到屏幕中间
    private void center(Window frame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();

        int x = Math.max(0, (screen.width - frame.getWidth()) / 2);
        int y = Math.max(0, (screen.height - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    // 退出程序
    private void exit() {
        try {
            System.exit(0);
        } catch (SecurityException e) {
        }
    }
    //获取image
    private BufferedImage getImage() {
        BufferedImage image2;
        image2 = new BufferedImage(width, height, image.getType());
        Graphics g = image2.getGraphics();
        if (DEBUG) System.out.println("getImage setting background to " + backgroundColor);
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);
        g.drawImage(image, 0, 0, panel);
        return image2;
    }


}