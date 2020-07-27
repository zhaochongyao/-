package tcp;
import game.Point;

import java.util.ArrayList;
import java.util.Stack;

public class CheckMap {
    private int[][] map;
    private Stack<Point> history;
    private ArrayList<Point> list;
    public CheckMap(){
        map = new int[15][15];
        history = new Stack<>();
        list = new ArrayList<>();
        remake();
    }

    public void remake(){                                   //重置棋盘
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                map[i][j] = 0;
            }
        }
    }
    public void point(int x,int y,boolean color){                   //下棋
        if(color){
            map[x][y] = 1;
        }else{
            map[x][y] = -1;
        }
        history.push(new Point(x,y,color));
        list.add(new Point(x,y,color));
    }
    public Point disPoint(){                                       //悔棋
        if(!history.empty()){
            return history.pop();
        }
        return null;
    }

    public ArrayList<Point> getList() {
        return list;
    }

    public int judgeWin(int x, int y){                                       //判断是否有一方获得胜利
        int cnt=1;
        int col=x,row=y;
        while(--col>0 && map[row][col]==map[y][x]) ++cnt;
        col=x;row=y;
        while(++col<=14 && map[row][col]==map[y][x]) ++cnt;
        if(cnt>=5){
            return 1;
        }
        // 判断一列是否五子连珠
        col=x;row=y;
        cnt=1;
        while(--row>0 && map[row][col]==map[y][x]) ++cnt;
        col=x;row=y;
        while(++row<=14 && map[row][col]==map[y][x]) ++cnt;
        if(cnt>=5){
            return 1;
        }
        // 判断左对角线是否五子连珠
        col=x;row=y;
        cnt=1;
        while(--col>0 && --row>0 && map[row][col]==map[y][x]) ++cnt;
        col=x;row=y;
        while(++col<=14 && ++row<=14 && map[row][col]==map[y][x]) ++cnt;
        if(cnt>=5){
            return 1;
        }
        // 判断右对角线是否五子连珠
        col=x;row=y;
        cnt=1;
        while(++row<=14 && --col>0 && map[row][col]==map[y][x]) ++cnt;
        col=x;row=y;
        while(--row>0 && ++col<=14 && map[row][col]==map[y][x]) ++cnt;
        if(cnt>=5){
            return 1;
        }
        return 0;
    }

}
