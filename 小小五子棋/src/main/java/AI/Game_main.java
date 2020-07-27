package AI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class Game_main{
    public static boolean Ai_Player = false;           //当前出手
    private static DrawingPanel panel=new DrawingPanel(700,700);
    private static Graphics g1 =panel.getGraphics();
    private static Graphics g2 =panel.getGraphics();
    private static int last_x=-1;
    private static int last_y=-1;
    public static int[][] chessBoard=new int[17][17]; //棋盘棋子的摆放情况：0无子，1黑子，－1白子
    private static HashSet<Point> toJudge=new HashSet<Point>(); // ai可能会下棋的点
    private static int dr[]=new int[]{-1,1,-1,1,0,0,-1,1}; // 方向向量
    private static int dc[]=new int[]{1,-1,-1,1,-1,1,0,0}; //方向向量
    public static final int MAXN=1<<28;
    public static final int MINN=-MAXN;
    private static int searchDeep=4;    //搜索深度
    private static final int size=15;   //棋盘大小
    public static boolean isFinished=false;
    // 初始化函数，绘图
    private static void show_last(int x,int y){
        g2.setColor(Color.red);
        g2.fillOval(x,y,10,10);
    }
    public static void initChessBoard(){

        toJudge.clear();
        panel.clear();
        g1.setColor(Color.BLACK);
        for(int i=45;i<=675;i+=45){
            g1.drawLine(45,i,675,i);
            g1.drawLine(i,45,i,675);
        }
        // 棋盘上的五个定位基本点，图中的小圆圈
        g1.setColor(Color.BLACK);
        g1.fillOval(353,353,14,14);
        g1.fillOval(218,218,14,14);
        g1.fillOval(488,218,14,14);
        g1.fillOval(488,488,14,14);
        g1.fillOval(218,488,14,14);
        // 初始化棋盘
        for(int i=1;i<=15;++i)
            for(int j=1;j<=15;++j)
                chessBoard[i][j]=0;
        // ai先手
        if(Ai_Player){
            g1.fillOval(342,342,30,30);
            last_x = 342;
            last_y = 342;
            chessBoard[8][8]=1;
            Ai_Player = false;
        }
        for(int i=0;i<8;++i)
            if(1<=8+dc[i] && 8+dc[i]<=size && 1<=8+dr[i] && 8+dr[i]<=size){
                Point now=new Point(8+dc[i],8+dr[i]);
                if(!toJudge.contains(now))
                    toJudge.add(now);
            }

    }
    // 通过点击事件，得到棋子位置进行下棋
    public static void putChess(int x,int y){
        if(Ai_Player)
        {
            g1.setColor(Color.BLACK);
            if(last_x!=-1&&last_y!=-1){
                g1.fillOval(last_x,last_y,30,30);
            }
        }
        else
            g1.setColor(Color.WHITE);
        g1.fillOval(x-17,y-17,30,30);
        if(Ai_Player) {
            show_last(x - 7, y - 7);
            last_x = x-17;
            last_y = y-17;
        }
        chessBoard[y/45][x/45]=Ai_Player?1:-1;
        if(isEnd(x/45,y/45)){
            String s= Game_main.Ai_Player?"黑子胜":"白子胜";
            JOptionPane.showMessageDialog(null,s);
            Ai_Player = true;
            initChessBoard();
        }
        else{
            Point p=new Point(x/45,y/45);
            if(toJudge.contains(p))
                toJudge.remove(p);
            for(int i=0;i<8;++i){
                Point now=new Point(p.x+dc[i],p.y+dr[i]);
                if(1<=now.x && now.x<=size && 1<=now.y && now.y<=size && chessBoard[now.y][now.x]==0)
                    toJudge.add(now);
            }
        }
    }
    // ai博弈入口函数
    public static void myAI(){
        Node node=new Node();
        dfs(0,node,MINN,MAXN,null);
        Point now=node.bestChild.p;
        // toJudge.remove(now);
        putChess(now.x*45,now.y*45);
        Ai_Player = false;
    }

    // alpha beta dfs
    private static void dfs(int deep, Node root, int alpha, int beta, Point p){
        if(deep==searchDeep){
            root.mark=getMark();
            return;
        }
        ArrayList<Point> judgeSet=new ArrayList<Point>();
        Iterator it=toJudge.iterator();
        while(it.hasNext()){
            Point now=new Point((Point)it.next());
            judgeSet.add(now);
        }
        it=judgeSet.iterator();
        while(it.hasNext()){
            Point now=new Point((Point)it.next());
            Node node=new Node();
            node.setPoint(now);
            root.addChild(node);
            boolean flag=toJudge.contains(now);
            chessBoard[now.y][now.x]=((deep&1)==1)?-1:1;
            if(isEnd(now.x,now.y)){
                root.bestChild=node;
                root.mark=MAXN*chessBoard[now.y][now.x];
                chessBoard[now.y][now.x]=0;
                return;
            }

            boolean flags[]=new boolean[8]; //标记回溯时要不要删掉
            Arrays.fill(flags,true);
            for(int i=0;i<8;++i){
                Point next=new Point(now.x+dc[i],now.y+dr[i]);
                if(1<=now.x+dc[i] && now.x+dc[i]<=size && 1<=now.y+dr[i] && now.y+dr[i]<=size && chessBoard[next.y][next.x]==0){
                    if(!toJudge.contains(next)){
                        toJudge.add(next);
                    }
                    else flags[i]=false;
                }
            }

            if(flag)
                toJudge.remove(now);
            dfs(deep+1,root.getLastChild(),alpha,beta,now);
            chessBoard[now.y][now.x]=0;
            if(flag)
                toJudge.add(now);
            for(int i=0;i<8;++i)
                if(flags[i])
                    toJudge.remove(new Point(now.x+dc[i],now.y+dr[i]));

            // min层
            if((deep&1)==1){
                if(root.bestChild==null || root.getLastChild().mark<root.bestChild.mark){
                    root.bestChild=root.getLastChild();
                    root.mark=root.bestChild.mark;
                    if(root.mark<=MINN)
                        root.mark+=deep;
                    beta=Math.min(root.mark,beta);
                }
                if(root.mark<=alpha)       //alpha beta剪枝
                    return;
            }
            // max层
            else{
                if(root.bestChild==null || root.getLastChild().mark>root.bestChild.mark){
                    root.bestChild=root.getLastChild();
                    root.mark=root.bestChild.mark;
                    if(root.mark==MAXN)
                        root.mark-=deep;
                    alpha=Math.max(root.mark,alpha);
                }
                if(root.mark>=beta)     //alpha beta剪枝
                    return;
            }
        }
    }

    public static DrawingPanel getPanel() {
        return panel;
    }

    public static int getMark(){                  //遍历每个棋子，并分析四个方向的棋型
        int res=0;
        for(int i=1;i<=size;++i){
            for(int j=1;j<=size;++j){
                if(chessBoard[i][j]!=0){
                    // 行
                    boolean flag1=false,flag2=false;
                    int x=j,y=i;
                    int cnt=1;              //当前棋型的棋子个数
                    int col=x,row=y;
                    while(--col>0 && chessBoard[row][col]==chessBoard[y][x])
                        ++cnt;
                    if(col>0 && chessBoard[row][col]==0) flag1=true;
                    col=x;row=y;
                    while(++col<=size && chessBoard[row][col]==chessBoard[y][x])
                        ++cnt;
                    if(col<=size && chessBoard[row][col]==0) flag2=true;
                    if(flag1 && flag2)
                        res+=chessBoard[i][j]*cnt*cnt;
                    else if(flag1 || flag2) res+=chessBoard[i][j]*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*chessBoard[i][j];
                    // 列
                    col=x;row=y;
                    cnt=1;flag1=false;flag2=false;
                    while(--row>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
                    if(row>0 && chessBoard[row][col]==0) flag1=true;
                    col=x;row=y;
                    while(++row<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
                    if(row<=size && chessBoard[row][col]==0) flag2=true;
                    if(flag1 && flag2)
                        res+=chessBoard[i][j]*cnt*cnt;
                    else if(flag1 || flag2)
                        res+=chessBoard[i][j]*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*chessBoard[i][j];
                    // 左对角线
                    col=x;row=y;
                    cnt=1;flag1=false;flag2=false;
                    while(--col>0 && --row>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
                    if(col>0 && row>0 && chessBoard[row][col]==0) flag1=true;
                    col=x;row=y;
                    while(++col<=size && ++row<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
                    if(col<=size && row<=size && chessBoard[row][col]==0) flag2=true;
                    if(flag1 && flag2)
                        res+=chessBoard[i][j]*cnt*cnt;
                    else if(flag1 || flag2) res+=chessBoard[i][j]*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*chessBoard[i][j];
                    // 右对角线
                    col=x;row=y;
                    cnt=1;flag1=false;flag2=false;
                    while(++row<=size && --col>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
                    if(row<=size && col>0 && chessBoard[row][col]==0) flag1=true;
                    col=x;row=y;
                    while(--row>0 && ++col<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
                    if(row>0 && col<=size && chessBoard[i][j]==0) flag2=true;
                    if(flag1 && flag2)
                        res+=chessBoard[i][j]*cnt*cnt;
                    else if(flag1 || flag2) res+=chessBoard[i][j]*cnt*cnt/4;
                    if(cnt>=5) res=MAXN*chessBoard[i][j];

                }
            }
        }
        return res;
    }


    // 判断是否一方取胜
    public static boolean isEnd(int x,int y){
        // 判断一行是否五子连珠
        int cnt=1;
        int col=x,row=y;
        while(--col>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(++col<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        // 判断一列是否五子连珠
        col=x;row=y;
        cnt=1;
        while(--row>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(++row<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        // 判断左对角线是否五子连珠
        col=x;row=y;
        cnt=1;
        while(--col>0 && --row>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(++col<=size && ++row<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        // 判断右对角线是否五子连珠
        col=x;row=y;
        cnt=1;
        while(++row<=size && --col>0 && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        col=x;row=y;
        while(--row>0 && ++col<=size && chessBoard[row][col]==chessBoard[y][x]) ++cnt;
        if(cnt>=5){
            isFinished=true;
            return true;
        }
        return false;
    }
}


// 树节点
class Node{
    public Node bestChild=null;
    public ArrayList<Node> child=new ArrayList<Node>();
    public Point p=new Point();
    public int mark;
    Node(){
        this.child.clear();
        bestChild=null;
        mark=0;
    }
    public void setPoint(Point r){
        p.x=r.x;
        p.y=r.y;
    }
    public void addChild(Node r){
        this.child.add(r);
    }
    public Node getLastChild(){
        return child.get(child.size()-1);
    }
}

