package AccountManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class AccountThread extends Thread {
    AtomicBoolean active=new AtomicBoolean(BankAccount.active.get());
    Map<String ,Integer> accounts=BankAccount.accounts;
    static Map<String,AccountThread> threadHash=new ConcurrentHashMap<>();
    @Override
    public void run() {
        super.run();
        threadHash.put(getName(), this);
        System.out.println("Account"+ getName());
        while(BankAccount.running.get()&& active.get() ){
            for(Map.Entry<String,Integer> entry:accounts.entrySet()){
            if(Objects.equals(entry.getKey(),getName())){
                continue;
            }


                Integer RandomAmount= BankAccount.RandomAmount(5000,7000);
                AccountThread target=threadHash.get(entry.getKey());
                TransferThread transfer=new TransferThread(this,target,RandomAmount);
                transfer.start();
                try {
                    transfer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public Integer getBalance() {
        return accounts.get(getName());
    }
    public static Map<String, AccountThread> getThreadHash() {
        return threadHash;
    }
}
