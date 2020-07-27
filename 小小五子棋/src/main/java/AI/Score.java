package AI;

import java.util.ArrayList;

public class Score {
   ArrayList<String> score = new ArrayList<>();
    ArrayList<int[]> flat = new ArrayList<>();   //将棋盘变为n个一维数组
    int ONE = 10;                  //棋型的分数
    int TWO = 100;
    int THREE = 1000;
    int FOUR = 100000;
    int FIVE = 10000000;
    int BLOCKED_ONE = 1;
    int BLOCKED_TWO = 10;
    int BLOCKED_THREE = 100;
    int BLOCKED_FOUR = 1000;
    int TWO_THREE = 100000;        //双三
    int THREE_FOUR = 100000;       //冲四活三

    int COUNT_ONE = 0;                  //棋型的个数
    int COUNT_TWO = 0;
    int COUNT_THREE = 0;
    int COUNT_FOUR = 0;
    int COUNT_FIVE = 0;
    int COUNT_BLOCKED_ONE = 0;
    int COUNT_BLOCKED_TWO = 0;
    int COUNT_BLOCKED_THREE = 0;
    int COUNT_BLOCKED_FOUR = 0;
    int COUNT_TWO_THREE = 0;
    int COUNT_THREE_FOUR = 0;

    int SCORE = 0;
    public void Re_Score(){
        COUNT_ONE = 0;                  //棋型的个数
        COUNT_TWO = 0;
        COUNT_THREE = 0;
        COUNT_FOUR = 0;
        COUNT_FIVE = 0;
        COUNT_BLOCKED_ONE = 0;
        COUNT_BLOCKED_TWO = 0;
        COUNT_BLOCKED_THREE = 0;
        COUNT_BLOCKED_FOUR = 0;
        COUNT_TWO_THREE = 0;
        COUNT_THREE_FOUR = 0;
        SCORE = 0;
    }

    public void judge_check(Check_child[][]list, String color){            //遍历棋盘获得各种棋形
        Re_Score();
        for (int i = 0; i < list.length; i++) {
            for (int i1 = 0; i1 < list[0].length; i1++) {
                if(list[i][i1]!=null){
                    if(list[i][i1].color==color){
                        //先判断右方向
                        int m = i;
                        int n = i1;
                        int count = 1;
                        boolean zuo = false;   //左右是否为null
                        boolean you = false;
                        boolean Continue = true;  //判断完后是否还继续
                        if(m-1>=0&&list[m-1][n]==null)
                            zuo = true;
                        if(m-1>=0&&list[m-1][n]!=null){
                            if(list[m-1][n].color==color&&list[m-1][n].check==true)
                            {Continue = false;}
                        }
                        if(Continue){
                            for (int i2 = 0; i2 < 4; i2++) {
                                if(m+1<15)
                                    m++;
                                else
                                    break;
                                if(list[m][n]==null&&((m+1<15&&list[m+1][n]!=null&&list[m+1][n].color!=color)||(m+1<15&&list[m+1][n]==null)))            //判断活三还是冲四    活二还是活三  眠三还是死二
                                {
                                    you = true;
                                    break;
                                }
                                if(list[m][n]!=null){
                                    if(list[m][n].color==color)
                                        count++;
                                    else
                                        break;
                                }
                            }
                            score.add(add_count(zuo,you,count));
                        }
                        m = i;                                 //向下
                        n = i1;
                        zuo = false;
                        you = false;
                        count = 1;
                        Continue = true;
                        if(n-1>=0&&list[m][n-1]==null)
                            zuo = true;
                        if(n-1>=0&&list[m][n-1]!=null){
                            if(list[m][n-1].color==color&&list[m][n-1].check==true)
                            {Continue = false;}
                        }
                        if(Continue){
                            for (int i2 = 0; i2 < 4; i2++) {
                                if(n+1<15)
                                    n++;
                                else
                                    break;
                                if(list[m][n]==null&&((n+1<15&&list[m][n+1]!=null&&list[m][n+1].color!=color)||(n+1<15&&list[m][n+1]==null)))
                                {
                                    you = true;
                                    break;
                                }
                                if(list[m][n]!=null){
                                    if(list[m][n].color==color)
                                        count++;
                                    else
                                        break;
                                }
                            }

                            score.add(add_count(zuo,you,count));
                        }
                        m = i;                                 //向右下
                        n = i1;
                        zuo = false;
                        you = false;
                        count = 1;
                        Continue = true;
                        if(n-1>=0&&m-1>=0&&list[m-1][n-1]==null)
                            zuo = true;
                        if(n-1>=0&&m-1>=0&&list[m-1][n-1]!=null){
                            if(list[m-1][n-1].color==color&&list[m-1][n-1].check==true)
                            {Continue = false;}
                        }
                        if(Continue){
                            for (int i2 = 0; i2 < 4; i2++) {
                                if(n+1<15&&m+1<15)
                                {
                                    m++;
                                    n++;
                                }
                                else
                                    break;
                                if(list[m][n]==null&&((m+1<15&&n+1<15&&list[m+1][n+1]!=null&&list[m+1][n+1].color!=color)||(m+1<15&&n+1<15&&list[m+1][n+1]==null)))
                                {
                                    you = true;
                                    break;
                                }
                                if(list[m][n]!=null){
                                    if(list[m][n].color==color)
                                        count++;
                                    else
                                        break;
                                }
                            }

                            score.add(add_count(zuo,you,count));
                        }
                        m = i;                                 //向左下
                        n = i1;
                        zuo = false;
                        you = false;
                        count = 1;
                        Continue = true;
                        if(n+1<15&&m-1>=0&&list[m-1][n+1]==null)
                            zuo = true;
                        if(n+1<15&&m-1>=0&&list[m-1][n+1]!=null){
                            if(list[m-1][n+1].color==color&&list[m-1][n+1].check==true)
                            {Continue = false;}
                        }
                        if(Continue){
                            for (int i2 = 0; i2 < 4; i2++) {
                                if(n-1>=0&&m+1<15)
                                {
                                    m++;
                                    n--;
                                }
                                else
                                    break;
                                if(list[m][n]==null&&((m+1<15&&n-1>=0&&list[m+1][n-1]!=null&&list[m+1][n-1].color!=color)||(m+1<15&&n-1>=0&&list[m+1][n-1]==null)))
                                {
                                    you = true;
                                    break;
                                }
                                if(list[m][n]!=null){
                                    if(list[m][n].color==color)
                                        count++;
                                    else
                                        break;
                                }
                            }
                            score.add(add_count(zuo,you,count));
                        }
                        list[i][i1].check = true;
                    }
                }
            }
        }
    }
    private String add_count(boolean zuo,boolean you,int count){
        if(zuo==true&&you==true){
            if(count==1)
                COUNT_ONE++;
            if(count==2)
                COUNT_TWO++;
            if(count==3) {
                COUNT_THREE++;
                return "活三";
            }
            if(count==4)
                COUNT_FOUR++;
            if(count==5)
                COUNT_FIVE++;
        }
        else if(zuo==false&&you==false&&count!=5){
            return "null";
        }
        else {
            if(count==1)
                COUNT_BLOCKED_ONE++;
            if(count==2)
                COUNT_BLOCKED_TWO++;
            if(count==3)
                COUNT_BLOCKED_THREE++;
            if(count==4)
            {
                COUNT_BLOCKED_FOUR++;
                return "冲四";
            }
            if(count==5)
                COUNT_FIVE++;
        }
        return "null";
    }

    public int get_value(Check_child[][] list, String color){
        judge_check(list,color);
        int a=0, b = 0;
        for (int i = 0; i < score.size(); i++) {
            if(score.get(i)=="活三")
                a++;
            if(score.get(i)=="冲四")
                b++;
        }
        if(a>=2)
            COUNT_TWO_THREE++;
        if(a>=1&&b>=1)
            COUNT_THREE_FOUR++;        // 双冲四不用考虑
        SCORE = COUNT_ONE*ONE+COUNT_TWO*TWO+COUNT_THREE*THREE+COUNT_FOUR*FOUR+COUNT_FIVE*FIVE+
                COUNT_BLOCKED_ONE*BLOCKED_ONE+COUNT_BLOCKED_TWO*BLOCKED_TWO+COUNT_BLOCKED_THREE*BLOCKED_THREE+COUNT_BLOCKED_FOUR*BLOCKED_FOUR
                +COUNT_THREE_FOUR*THREE_FOUR+COUNT_TWO_THREE*TWO_THREE;
        return SCORE;

    }
}
