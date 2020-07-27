package AI;

import java.util.ArrayList;

public class Check_Board {
    public static String Black = "B";
    public static String White = "W";
    public ArrayList<Check_child> Black_child;         //存储黑棋
    public ArrayList<Check_child> White_child;         //存储白棋
    public Check_child[][] Array_Board;               //存储棋盘
    public int Black_check_count=0;                      //已下黑棋的数目
    public int White_check_count=0;                      //已下白棋的数目
    public Check_Board(){
        Array_Board = new Check_child[15][15];
        init();
    }
    private void init(){
        Black_child = new ArrayList<>();
        White_child = new ArrayList<>();
        Check_child l1 = new Check_child(Black,null);
        Black_child.add(l1);
        Check_child l2 = new Check_child(White,null);
        White_child.add(l2);
        for (int i = 1; i < 3000; i++) {
            Black_child.add(new Check_child(Black,Black_child.get(i-1)));
            White_child.add(new Check_child(White,White_child.get(i-1)));
        }
    }

    public void Black_point(int x,int y){
        //Check_child b1 = Black_child.get(Black_check_count);
        Check_child b1 = new Check_child(Black,null);
        b1.Set(x,y);
        Array_Board[x][y] = b1;
        Black_check_count++;
    }
    public void White_point(int x,int y){
       // Check_child w1 = White_child.get(White_check_count);
        Check_child w1 = new Check_child(White,null);
        w1.Set(x,y);
        Array_Board[x][y] = w1;
        White_check_count++;
    }
    public void set_null(int x,int y){
        Array_Board[x][y] = null;
    }
    public int[][] Transform(){
        int [][] list = new int [15][15];
        for (int i = 0; i < Array_Board.length; i++) {
            for (int i1 = 0; i1 < Array_Board[0].length; i1++) {
                if(Array_Board[i][i1]!=null){
                if(Array_Board[i][i1].color==Black)    //黑棋置-1
                    list[i][i1] = -1;
                if(Array_Board[i][i1].color==White)    //白棋置 1
                    list[i][i1] = 1;
                }
            }
        }
        return list;
    }
    public boolean Judge_Victory(int[][]list){
        return false;
    }


}
