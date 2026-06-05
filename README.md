# order-book-matching-engine
# Order Book Matching Engine

A Java-based concurrent Order Book Matching Engine that simulates a simplified stock exchange. Multiple trader threads submit BUY and SELL orders concurrently, while a single matching engine thread processes orders and executes trades. Trade confirmations are handled asynchronously using `CompletableFuture`.

## Project Objective

The goal of this project is to demonstrate practical usage of Java Concurrency concepts including:

* Threads and Runnable
* Callable and Future
* ExecutorService and Thread Pools
* BlockingQueue
* volatile
* synchronized
* ReentrantLock
* CompletableFuture

## Project Structure

```text
src/
├── model/
│   ├── Order.java
│   ├── Side.java
│   └── Trade.java
│
├── trader/
│   ├── TraderTask.java
│   └── NaiveTrader.java
│
├── engine/
│   └── MatchingEngine.java
│
├── confirmation/
│   └── TradeConfirmer.java
│
├── pitfalls/
│   └── Pitfalls.java
│
└── Main.java
```

## Concurrency Mapping

| Class          | Concurrency Concept                           |
| -------------- | --------------------------------------------- |
| NaiveTrader    | Runnable and raw Thread                       |
| TraderTask     | Callable and Future                           |
| MatchingEngine | volatile, ReentrantLock, BlockingQueue        |
| TradeConfirmer | CompletableFuture                             |
| Main           | ExecutorService lifecycle management          |
| Pitfalls       | Race Conditions, Deadlocks, Atomic Operations |

## Features

* Concurrent order submission by multiple trader threads
* Producer-consumer design using BlockingQueue
* Single-threaded matching engine
* BUY and SELL order matching
* Asynchronous trade confirmation
* Random confirmation failures (~10%)
* Graceful shutdown of all threads
* Final execution summary

## Matching Rule

A trade is executed when:

```text
BUY Price >= SELL Price
```

Example:

```text
BUY 106
SELL 100
```

Result:

```text
Trade Executed at Price 100
```

## Sample Input

```text
5
TRADER_A BUY 102 10
TRADER_B SELL 100 10
TRADER_C BUY 99 5
TRADER_D SELL 105 5
TRADER_E BUY 106 5
```

## Sample Output

```text
Grouped Orders:

TRADER_A -> 1 orders
TRADER_B -> 1 orders
TRADER_C -> 1 orders
TRADER_D -> 1 orders
TRADER_E -> 1 orders

MATCH FOUND -> Trade{...}

CONFIRMED -> Trade{...}

FAILED -> Trade{...}

===== FINAL SUMMARY =====

Orders Submitted : 5
Trades Matched : 2
Confirmations Succeeded : 1
Confirmations Failed : 1
Unmatched Orders Remaining : 1
```

## Design Decisions

### Why BlockingQueue?

`BlockingQueue` provides a thread-safe producer-consumer mechanism without requiring manual synchronization using wait() and notify().

### Why FixedThreadPool?

The number of traders is known in advance. A fixed-size pool prevents uncontrolled thread creation.

### Why volatile?

The `marketOpen` flag has a single writer and multiple readers. `volatile` guarantees visibility between threads.

### Why ReentrantLock?

`tryLock()` allows the matching engine to back off instead of waiting indefinitely, something that synchronized cannot provide.

### Why CompletableFuture?

Trade confirmations execute asynchronously without blocking the matching engine thread.

## Pitfalls Demonstrated

The project includes standalone demonstrations of:

1. Race Condition
2. Deadlock
3. Why volatile is not enough for counters
4. Why calling Future.get() too early serializes execution

## Technologies Used

* Java 11+
* Java Collections Framework
* java.util.concurrent package
* IntelliJ IDEA
* Git & GitHub

## Author

**Beshwanth Sai Katari**

B.Tech Computer Science and Engineering
KL University

GitHub: https://github.com/Beshwanthsai

Use in Intellij Idea
