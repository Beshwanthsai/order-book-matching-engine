import confirmation.TradeConfirmer;
import engine.MatchingEngine;
import model.Order;
import model.Side;
import trader.TraderTask;

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

        BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();

        List<CompletableFuture<Boolean>> confirmationFutures = new ArrayList<>();

        /*
         * FixedThreadPool is used because
         * the number of traders is known.
         *
         * CachedThreadPool could create
         * unbounded threads.
         */
        ExecutorService traderPool = Executors.newFixedThreadPool(traderOrders.size());

        ExecutorService confirmationPool = Executors.newFixedThreadPool(4);

        TradeConfirmer tradeConfirmer = new TradeConfirmer(confirmationPool);

        MatchingEngine matchingEngine = new MatchingEngine(orderQueue, tradeConfirmer, confirmationFutures);

        Thread engineThread = new Thread(matchingEngine);

        engineThread.start();

        List<Future<Integer>> traderFutures = new ArrayList<>();

        for (List<Order> orders : traderOrders.values()) {
            traderFutures.add(traderPool.submit(new TraderTask(orders, orderQueue)));
        }

        int totalOrdersSubmitted = 0;
        for (Future<Integer> future : traderFutures) {
            try {
                totalOrdersSubmitted += future.get();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        matchingEngine.closeMarket();

        try {

            engineThread.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        /*
         * Waiting once using allOf().join()
         * preserves concurrency.
         *
         * Calling get() immediately after
         * submission would serialize execution.
         */
        CompletableFuture.allOf(
                confirmationFutures.toArray(
                        new CompletableFuture[0]
                )
        ).join();

        traderPool.shutdown();
        confirmationPool.shutdown();

        try {

            traderPool.awaitTermination(
                    5,
                    TimeUnit.SECONDS
            );

            confirmationPool.awaitTermination(
                    5,
                    TimeUnit.SECONDS
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }

        System.out.println("\n===== FINAL SUMMARY =====");

        System.out.println(
                "Orders Submitted : "
                        + totalOrdersSubmitted
        );

        System.out.println(
                "Trades Matched : "
                        + matchingEngine.getMatchedTrades()
        );

        System.out.println(
                "Confirmations Succeeded : "
                        + tradeConfirmer.getSuccessCount()
        );

        System.out.println(
                "Confirmations Failed : "
                        + tradeConfirmer.getFailureCount()
        );

        int unmatchedOrders =
                matchingEngine.getBuyOrders().size()
                        + matchingEngine.getSellOrders().size();

        System.out.println(
                "Unmatched Orders Remaining : "
                        + unmatchedOrders
        );

        scanner.close();
    }
}