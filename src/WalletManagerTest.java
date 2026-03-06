import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;



public class WalletManagerTest {
    

    @Test
    public void testAddAccountAndTotalBalance() {

        System.err.println("Something went wrong");

        WalletManager manager = new WalletManager();

        Account acc1 = new Account("Cash", 1000);
        Account acc2 = new Account("Bank", 2000);

        manager.addAccount(acc1);
        manager.addAccount(acc2);

        double total = manager.getTotalNetBalance();

        assertEquals(3000, total);
    }

    @Test
    public void testRecordTransactionDeposit() {

        WalletManager manager = new WalletManager();
        Account acc = new Account("Cash", 1000);

        manager.addAccount(acc);
        Account acc2 = new Account("Bank", 1000);
        Transaction t = new Transaction("1", "test1", "Transfer",TransactionType.TRANSFER,Category.SALARY,LocalDate.now(), acc2, acc, 500.00, TransactionStatus.COMPLETED); 
    //(String id, String details, String note, TransactionType type, Category category,
    //   LocalDate date, Account fromAccount, Account toAccount, double amount,
    //   TransactionStatus status)
        manager.recordTransaction(t);

        System.out.println("status" + t.getStatus() + "balance" + acc.getBalance());
        assertEquals(1500.00, acc.getBalance());
    }

    @Test
    public void testRecordTransactionWithdraw() {

        WalletManager manager = new WalletManager();
        Account acc = new Account("Cash", 400);

        manager.addAccount(acc);
        Account acc2 = new Account("Bank", 1000);
        Transaction t = new Transaction("2", "test2", "Withdraw", TransactionType.EXPENSE, Category.OTHERS, LocalDate.now(), acc, acc2, -300.00, TransactionStatus.COMPLETED);
        manager.recordTransaction(t);

        assertEquals(700.00, acc.getBalance());
    }

    
}