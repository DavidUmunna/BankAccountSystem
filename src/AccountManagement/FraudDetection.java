package AccountManagement;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FraudDetection {
        public static final ReentrantLock fraudLock = new ReentrantLock();
        public static final Condition TransferSlotAvailable=fraudLock.newCondition(); 

        public static int activeTransfers = 0;
        public static  final Integer maxTransfers=3;

        public static void enterTransfers() {
            fraudLock.lock();
            try {
                while (activeTransfers >= maxTransfers) {
                    try {
                        TransferSlotAvailable.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                activeTransfers++;
            } finally {
                fraudLock.unlock();
            }
        }

        public static void exitTransfers() {
            fraudLock.lock();
            try {
                activeTransfers--;
                TransferSlotAvailable.signalAll();
            } finally {
                fraudLock.unlock();
            }
        }
}
