package game;

import java.io.Serializable;

public class Point implements Serializable {                //用于服务器与客户端中传输下棋点
    private int x;
    private int y;
    private boolean color;
    public Point(int x, int y, boolean color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isColor() {
        return color;
    }
}