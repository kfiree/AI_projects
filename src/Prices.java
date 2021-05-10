public enum Prices{
    twoSides(6),
    twoUpAndDown(7),
    moveOne(5);

    private final int value;

    Prices(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}