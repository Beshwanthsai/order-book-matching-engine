package engine;

import model.Order;
import model.Side;
import model.Trade;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MatchingEngine implements Runnable {
    private final BlockingQueue<Order> orderQueue;
    private final List<Order> buyOrders;
    private final List<Order> sellOrders;

//     tryLock() allows the engine to back off instead of waiting forever. synchronized cannot timeout.

    private final ReentrantLock lock = new ReentrantLock();


//      volatile is sufficient because marketOpen is a single variable written by one thread and read by multiple threads volatile would NOT be sufficient for counters.

    private volatile boolean marketOpen = true;

    public MatchingEngine(BlockingQueue<Order> orderQueue) {

        this.orderQueue = orderQueue;

        this.buyOrders = new ArrayList<>();
        this.sellOrders = new ArrayList<>();
    }

    public void closeMarket() {
        marketOpen = false;
    }

    @Override
    public void run() {
        while (marketOpen || !orderQueue.isEmpty()) {
            try {
                Order order = orderQueue.poll(
                        500,
                        TimeUnit.MILLISECONDS
                );
                if (order == null) {
                    continue;
                }
                processOrder(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Matching Engine Stopped");
    }

    private void processOrder(Order order) {
        try {
            if (lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    if (order.getSide() == Side.BUY) {
                        buyOrders.add(order);
                    } else {
                        sellOrders.add(order);
                    }
                    attemptMatch();
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attemptMatch() {
        for (Order buy : buyOrders) {
            for (Order sell : sellOrders) {
                if (buy.getPrice() >= sell.getPrice()) {
                    Trade trade =
                            new Trade(buy, sell, sell.getPrice());
                    System.out.println("MATCH FOUND -> " + trade);

                    buyOrders.remove(buy);
                    sellOrders.remove(sell);
                    return;
                }
            }
        }
    }

    public List<Order> getBuyOrders() {
        return buyOrders;
    }

    public List<Order> getSellOrders() {
        return sellOrders;
    }
}