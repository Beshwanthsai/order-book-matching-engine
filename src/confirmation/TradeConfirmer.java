package confirmation;

import model.Trade;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TradeConfirmer {

    private final ExecutorService executorService;

    private int successCount = 0;
    private int failureCount = 0;

    public TradeConfirmer(ExecutorService executorService) {
        this.executorService = executorService;
    }
    public CompletableFuture<Boolean> confirmTrade(Trade trade) {
        return CompletableFuture.supplyAsync(() -> {
                    Random random = new Random();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    // Simulate 10% failure
                    if (random.nextInt(10) == 0) {

                        throw new RuntimeException(
                                "Confirmation Failed"
                        );
                    }
                    return true;
                }, executorService).exceptionally(ex -> {
                    System.out.println("FAILED -> " + trade);
                    failureCount++;
                    return false;
                }).thenApply(result -> {
                    if (result) {
                        successCount++;
                        System.out.println("CONFIRMED -> "+ trade);
                    }

                    return result;
                });
    }

    public int getSuccessCount() {
        return successCount;
    }
    public int getFailureCount() {
        return failureCount;
    }
}

/*completableFuture allows confirmation and it does the processing without blcking the matching engine
where calling future,get() would serialize the execution and it will just defeat the concurrency
 */