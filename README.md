# Bank Account System

A multithreaded bank account simulation built in Java, demonstrating concurrent programming concepts including monitors, `ReentrantLock`, `Condition` variables, `AtomicBoolean`, and thread lifecycle management — presented through a dark-themed Swing UI.

---

## Features

- Three concurrent bank accounts, each represented as a thread
- Manual transfers via a UI — choose source account, destination, and amount
- Transfer concurrency limited to a maximum of 3 simultaneous transfers (enforced via a monitor)
- Live balance display, refreshed every 500ms
- Atomic debit + credit — balances are never inconsistent mid-transfer
- Clean thread stopping via atomic flags

---

## Project Structure

```
BankAccountSystem/
├── src/
│   ├── psuedo.txt                        # Pseudo code overview
│   └── AccountManagement/
│       ├── BankAccount.java              # Shared state & balance operations
│       ├── AccountThread.java            # Thread representing a bank account
│       ├── TransferThread.java           # Thread performing a single transfer
│       ├── FraudDetection.java           # Monitor: gates concurrent transfers
│       └── BankUI.java                   # Swing UI entry point
└── README.md
```

---

## Class Breakdown

### `BankAccount`
Holds all shared static state for the simulation.

| Member | Type | Purpose |
|---|---|---|
| `accounts` | `Map<String, Integer>` | Maps account thread name → balance |
| `accountBalance` | `Integer` | Starting balance for each account (`$20,000`) |
| `running` | `AtomicBoolean` | Global flag — threads loop while `true` |
| `active` | `AtomicBoolean` | Secondary activity flag per thread |
| `addToBalance(id, amount)` | static method | Thread-safe credit operation |
| `removeFromBalance(id, amount)` | static method | Thread-safe debit operation |
| `RandomAmount(min, max)` | static method | Returns a random integer in range |

---

### `FraudDetection` — The Monitor
Acts as a concurrency gate, limiting the number of simultaneous transfers to `maxTransfers` (3).

Uses `ReentrantLock` + `Condition` — the modern Java equivalent of a monitor:

```
enterTransfers()
    acquire fraudLock
    while activeTransfers >= 3:
        await transferSlotAvailable   ← releases lock, suspends thread
    activeTransfers++
    release fraudLock

exitTransfers()
    acquire fraudLock
    activeTransfers--
    signalAll transferSlotAvailable   ← wakes waiting threads
    release fraudLock
```

**Why not `synchronized`?**  
`ReentrantLock` + `Condition` allows named condition variables with `signalAll()`, giving finer control than `synchronized`'s single anonymous wait-set. It also enforces lock release via `finally`, preventing lock leaks.

---

### `AccountThread`
Each instance represents a bank account and runs as a thread. Its `getName()` is used as its account ID in the shared `accounts` map.

```
run():
    register self in threadHash
    while running && active:
        for each other account:
            pick random transfer amount (5,000 – 7,000)
            create TransferThread(self → target, amount)
            start transfer thread
            join transfer thread       ← wait before initiating next
```

`transfer.join()` ensures an account thread waits for one transfer to complete before spawning the next, preventing unbounded thread creation.

---

### `TransferThread`
Performs a single transfer between two accounts. Runs until `BankAccount.running` is `false`.

```
run():
    while running:
        if giving.balance > transferAmount:
            FraudDetection.enterTransfers()    ← blocks if 3 in progress
            try:
                removeFromBalance(giving)
                addToBalance(receiving)        ← atomic: lock held across both
            finally:
                FraudDetection.exitTransfers()
```

The debit and credit are performed inside the same `FraudDetection` lock scope, making them a single atomic operation — no other thread can observe a balance mid-transfer.

---

### `BankUI`
Swing `JFrame` entry point. All UI updates happen on the Event Dispatch Thread (EDT).

**Layout:**
- **Header** — title, subtitle, live status message (success/error)
- **Account cards** — one card per account showing thread name and live balance
- **Transfer bar** — From dropdown, To dropdown, amount field, Transfer button

**Transfer flow (button click):**
1. Validate From ≠ To and amount > 0
2. Spawn background thread to avoid blocking EDT
3. Check sufficient balance
4. Call `FraudDetection.enterTransfers()` → perform atomic transfer → `exitTransfers()`
5. Post result back to EDT via `SwingUtilities.invokeLater()`

**Balance refresh:**  
A `javax.swing.Timer` fires every 500ms on the EDT, reading the live `accounts` map and updating each balance label.

---

## Concurrency Design

```
┌─────────────────────────────────────────────────────────┐
│                        BankUI (EDT)                     │
│   Transfer button click → background Thread spawned     │
└──────────────────────────┬──────────────────────────────┘
                           │
                    calls enterTransfers()
                           │
┌──────────────────────────▼──────────────────────────────┐
│                  FraudDetection (Monitor)                │
│   ReentrantLock + Condition                             │
│   Blocks if activeTransfers >= 3                        │
│   Grants entry, increments counter                      │
└──────────────────────────┬──────────────────────────────┘
                           │
          ┌────────────────▼─────────────────┐
          │         BankAccount.accounts      │
          │   removeFromBalance(giving)       │
          │   addToBalance(receiving)         │  ← atomic pair
          └────────────────┬─────────────────┘
                           │
                    calls exitTransfers()
                           │
                  decrements counter,
                  signals waiting threads
```

---

## How to Run

1. Open in IntelliJ IDEA or VS Code with the Java extension
2. Run `BankUI.main()`
3. The window shows three accounts each starting at **$20,000.00**
4. Select a **From** account, a **To** account, enter an amount, click **Transfer**
5. Balances update live every 500ms

---

## Key Concepts Demonstrated

| Concept | Where used |
|---|---|
| `Thread` subclassing | `AccountThread`, `TransferThread` |
| `AtomicBoolean` | `BankAccount.running`, `BankAccount.active` |
| `ReentrantLock` | `FraudDetection.fraudLock` |
| `Condition` / `await` / `signalAll` | `FraudDetection.transferSlotAvailable` |
| Monitor pattern | `FraudDetection` class |
| Atomic compound operations | Debit + credit under single lock scope |
| `Thread.join()` | `AccountThread` waits for each `TransferThread` |
| Swing EDT safety | `SwingUtilities.invokeLater()` for all UI updates |
| `javax.swing.Timer` | Live balance refresh without blocking EDT |
