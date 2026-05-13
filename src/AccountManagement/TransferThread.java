package AccountManagement;


public class TransferThread extends Thread{
    private AccountThread accountThread1;
    private AccountThread accountThread2;
    private Integer TrasferAmount;
    

    public TransferThread(AccountThread giving,AccountThread receiving,Integer amount)
    {
        accountThread1=giving;
        accountThread2=receiving;
        TrasferAmount=amount;
    }

    @Override
    public void run(){
        System.out.println("Transfer Thread");

        while(BankAccount.running.get()){
           



               if(accountThread1.getBalance()>TrasferAmount){
                      FraudDetection.enterTransfers();
                       try {
                           BankAccount.removeFromBalance(accountThread1.getName(),TrasferAmount);
                           BankAccount.addToBalance(accountThread2.getName(),TrasferAmount);
                        } finally {
                            FraudDetection.exitTransfers();
                           
                        }
                    
               }

            

        }
        

    }


}
