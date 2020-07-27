package AI;

import java.util.ArrayList;
import java.util.Random;

public class AI {
    private String color = Check_Board.Black;   //AI的棋子颜色
    private Check_Board check_board;            //棋盘
    private Score score ;                       //棋形
    private Score score1 ;
    private ArrayList<Point> bestpoint = new ArrayList<>();  // AI下子的最优点
    private ArrayList<Point> toJudge = new ArrayList<>();   //AI可以下棋的点

    public  AI(Score s, Score s1, Check_Board c){
        check_board = c;
        score = s;
        score1 = s1;
    }

    class Point{                              //下子点
        int x;
        int y;
        Integer v;
        public Point(int X,int Y){
            x = X;
            y = Y;
        }
    }

    public void hum(int x,int y){              //模拟玩家下子
        if(color== Check_Board.Black)
            check_board.White_point(x,y);
        else
            check_board.Black_point(x,y);
    }
    public void com(int x,int y){              //电脑模拟下子
        if(color== Check_Board.Black)
            check_board.Black_point(x,y);
        else
            check_board.White_point(x,y);
    }
    private  ArrayList<Point> Generator(int deepth, Check_child[][] list, String color){            //估值函数   计算出最大价值的棋形
        ArrayList<Point> hs = new ArrayList<>();
        ArrayList<Point>neighbors = new ArrayList<>();
        ArrayList<Point>next_neighbors = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            for (int i1 = 0; i1 < list[0].length; i1++) {
                if(list[i][i1]==null){
                    if(judge_neighbors(i,i1,list,1))
                        neighbors.add(new Point(i,i1));
                }
            }
        }

        for (int i = 0; i < neighbors.size(); i++) {
            hs.add(neighbors.get(i));
        }
        return hs;
    }
    private boolean judge_neighbors(int i, int i1, Check_child[][] list, int num){                      //计算棋子周围的可下子点
        if((i+num<15&&list[i+num][i1]!=null)||(i-num>=0&&list[i-num][i1]!=null)||(i1+num<15&&list[i][i1+num]!=null)||(i1-num>=0&&list[i][i1-num]!=null))
            return true;
        if((i+num<15&&i1+num<15&&list[i+num][i1+num]!=null)||(i+num<15&&i1-num>=0&&list[i+num][i1-num]!=null))
            return true;
        if((i-num>=0&&i1+num<15&&list[i-num][i1+num]!=null)||(i-num>=0&&i1-num>=0&&list[i-num][i1-num]!=null))
            return true;
        return false;
    }

    public Point maxmin(Check_child[][] list, int depth){                               //极大极小值函数   即计算使ai分数最大的棋形
        int Alpha = Integer.MIN_VALUE;
        int Beta = Integer.MAX_VALUE;
        int best = Integer.MIN_VALUE;
        ArrayList<Point> points = Generator(depth,list,color);
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            com(p.x,p.y);
            int v = min(list,depth,Alpha,Beta);
            if(v==best){
                bestpoint.add(p);
            }
            if(v > best) {
                best = v;
                bestpoint.clear();
                bestpoint.add(p);
            }
            check_board.set_null(p.x,p.y);
        }
        Point result = bestpoint.get(new Random().nextInt(bestpoint.size()));
        return result;
    }
    private int min(Check_child[][]list, int depth, int alpha, int beta){           //找出使玩家分数最小的棋形  同时根据alpha beta剪枝
        int v = evaluate();
        if(depth<=0||check_board.Judge_Victory(check_board.Transform())){
            return v;
        }
        int best = Integer.MAX_VALUE;
        ArrayList<Point> points = Generator(depth,list, Check_Board.White);
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            hum(p.x,p.y);
            v = max(list,depth-1,best > alpha ? best : alpha, beta);
            check_board.set_null(p.x,p.y);
            if(v<best)
            {
                best = v;
            }
            if(v>=beta){
                break;
            }
        }
        return best;
    }
    private int max(Check_child[][]list, int depth, int alpha, int beta){       //找出使ai分数最大的棋形  同时根据alpha beta剪枝
        int v = evaluate();
        if(depth<=0||check_board.Judge_Victory(check_board.Transform())){
            return v;
        }
        int best = Integer.MIN_VALUE;
        ArrayList<Point> points = Generator(depth,list,color);
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            com(p.x,p.y);
            v = min(list,depth-1, alpha, best < beta ? best : beta);
            check_board.set_null(p.x,p.y);
            if(v>best){
                best = v;
            }
            if(v<=alpha){
                break;
            }
        }
        return best;
    }
    public int evaluate(){                                                            //估值函数
        int m = score.get_value(check_board.Array_Board,color);
        int n = score.get_value(check_board.Array_Board, Check_Board.White);
        return (m-n);
    }

}
