package models;

/**
 * Created by Mart0
 * Date: 4/29/12
 */
public class BoardPoint {
    private char x;
    private int y;
    private int xInt;

    public BoardPoint(char x, int y) {
        this.x = x;
        this.y = y;
        this.xInt = getBoardInt(x);
    }

    public BoardPoint(int x, int y) {
        this.x = getBoardChar(x);
        this.y = y;
        this.xInt = x;
    }

    public char getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getXInt() {
        return xInt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardPoint)) return false;
        BoardPoint that = (BoardPoint) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = (int) x;
        result = 31 * result + y;
        return result;
    }

    private int getBoardInt(char x) {
        switch (x) {
            case 'A':
                return 1;
            case 'B':
                return 2;
            case 'C':
                return 3;
            case 'D':
                return 4;
            case 'E':
                return 5;
            case 'F':
                return 6;
            case 'G':
                return 7;
            case 'H':
                return 8;
            case 'I':
                return 9;
            case 'J':
                return 10;
            default:
                return -1;
        }
    }

    private char getBoardChar(int x) {
        switch (x) {
            case 1:
                return 'A';
            case 2:
                return 'B';
            case 3:
                return 'C';
            case 4:
                return 'D';
            case 5:
                return 'E';
            case 6:
                return 'F';
            case 7:
                return 'G';
            case 8:
                return 'H';
            case 9:
                return 'I';
            case 10:
                return 'J';
            default:
                return 'Z';
        }
    }

    @Override
    public String toString() {
        return "BoardPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}


