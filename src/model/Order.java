package model;

public class Order {

    private String traderName;
    private Side side;
    private int price;
    private int quantity;

    public Order(String traderName, Side side, int price, int quantity) {
        this.traderName = traderName;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public String getTraderName() {
        return traderName;
    }

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Order{" +
                "traderName='" + traderName + '\'' +
                ", side=" + side +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}