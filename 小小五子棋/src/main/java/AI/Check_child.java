package AI;

public class Check_child {
    public int x;
    public int y;
    public String color;
    public Check_child prev;
    public boolean check = false;       //检查棋型时是否已被检测过
    public Check_child(String C, Check_child Prev){
        color = C;
        prev = Prev;
    }
    public void Set(int X,int Y){
        x = X;
        y = Y;
    }
}
