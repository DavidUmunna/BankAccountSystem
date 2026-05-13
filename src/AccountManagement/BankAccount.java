package AccountManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BankAccount {
    public static  Map<String,Integer> accounts;
    public static final Integer accountBalance=20000;
    public  static AtomicBoolean running=new AtomicBoolean(false);
    //public static Semaphore transferRegulator=new Semaphore(maxTransfers, true);
    //public static ReentrantLock AccountLock=new ReentrantLock();
    
    public static AtomicBoolean active=new AtomicBoolean(false);

    public  BankAccount() {
        accounts = new HashMap<>();
    }
    public void addAccount(String id){
        accounts.put(id,accountBalance);
    }
    public static void addToBalance(String  ref_id,int amount){
        for(String id:accounts.keySet()){
            if(Objects.equals(id,ref_id)){
                accounts.put(id,accounts.get(id)+amount);
            }
        }
    }

    public static  void removeFromBalance(String ref_id,int amount){
        for(String id :accounts.keySet()){
            if(Objects.equals(id, ref_id)){
                accounts.put(id,accounts.get(id)-amount);
            }
        }
    }


    public  static Integer RandomAmount(int min,int max){
        return new Random().nextInt(max-min)+min;
    }

}
