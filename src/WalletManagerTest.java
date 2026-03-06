import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WalletManagerTest {

    @Test
    public void testAddAccountAndTotalBalance() {

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

        Transaction t = new Transaction(acc, 500);
        manager.recordTransaction(t);

        assertEquals(1500, acc.getBalance());
    }

    @Test
    public void testRecordTransactionWithdraw() {

        WalletManager manager = new WalletManager();
        Account acc = new Account("Cash", 1000);

        manager.addAccount(acc);

        Transaction t = new Transaction(acc, -300);
        manager.recordTransaction(t);

        assertEquals(700, acc.getBalance());
    }
}