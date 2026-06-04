package trader;

import model.Order;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class TraderTask implements Callable<Integer> {

    private final List<Order> orders;
    private final BlockingQueue<Order> orderQueue;

    public TraderTask(List<Order> orders,
                      BlockingQueue<Order> orderQueue) {
        this.orders = orders;
        this.orderQueue = orderQueue;
    }

    @Override
    public Integer call() throws Exception {

        int submittedOrders = 0;

        for (Order order : orders) {

            orderQueue.put(order);

            submittedOrders++;

            System.out.println(
                    Thread.currentThread().getName()
                            + " submitted "
                            + order
            );

            Thread.sleep(100);
        }

        return submittedOrders;
    }
}