import java.awt.*;

public enum Operator
{

    TWO_LEFT("R", 6, new Point(0,1)),
    TWO_UP("D", 7, new Point(1,0)),
    TWO_RIGHT("L", 6, new Point(0, -1)),
    TWO_DOWN("U", 7, new Point(-1, 0)),
    LEFT("R", 5, new Point(0, 1)),
    UP("D", 5, new Point(1,0)),
    RIGHT("L", 5, new Point(0, -1)),
    DOWN("U", 5, new Point(-1, 0));


    // declaring private variable for getting values
    private final String symbol;
    private final Point tile;
    private final int price;
    static private int onePrice = 5;


    // getter method
    public String symbol()
    {
        return this.symbol;
    }

    static public int onePrice(){
        return onePrice;
    }

    public Point tile()
    {
        return this.tile;
    }

    public int price()
    {
        return this.price;
    }


    // enum constructor - cannot be public or protected
    Operator(String symbol, int price, Point tile)
    {
        this.price = price;
        this.symbol = symbol;
        this.tile = tile;
    }
}


