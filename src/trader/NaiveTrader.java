package trader;

public class NaiveTrader implements Runnable {

    @Override
    public void run() {

        System.out.println("Submitting hardcoded orders...");
    }

    public static void main(String[] args) {

        NaiveTrader trader = new NaiveTrader();

        Thread thread = new Thread(trader);

        // start() creates a new thread and executes run()
        thread.start();

        /* run() executes on the current thread and does not create a new thread.Creating one thread per task does not scale
         because each thread consumes memory and CPU.*/
    }
}