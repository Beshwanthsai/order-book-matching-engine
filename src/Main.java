import confirmation.TradeConfirmer;
import engine.MatchingEngine;
import model.Order;
import model.Side;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, List<Order>> traderOrders = new HashMap<>();
        System.out.print("Enter number of orders: ");
        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            String traderName = parts[0];
            Side side = Side.valueOf(parts[1]);
            int price = Integer.parseInt(parts[2]);
            int quantity = Integer.parseInt(parts[3]);
            Order order = new Order(traderName, side, price, quantity);
            traderOrders.computeIfAbsent(traderName, k -> new ArrayList<>()).add(order);
        }
        System.out.println("\nGrouped Orders:");
        for (Map.Entry<String, List<Order>> entry
                : traderOrders.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().size() + " orders");
        }
    }
}