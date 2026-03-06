import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    @Test
    public void testAccountCreation() {

        Account acc = new Account("Cash", 1000);

        assertEquals("Cash", acc.getName());
        assertEquals(1000, acc.getBalance());
    }

    @Test
    public void testAddFunds() {

        Account acc = new Account("Bank", 500);

        acc.addFunds(300);

        assertEquals(800, acc.getBalance());
    }

    @Test
    public void testDeductFunds() {

        Account acc = new Account("Wallet", 1000);

        acc.deductFunds(250);

        assertEquals(750, acc.getBalance());
    }

    @Test
    public void testMultipleTransactions() {

        Account acc = new Account("Cash", 1000);

        acc.addFunds(500);
        acc.deductFunds(200);

        assertEquals(1300, acc.getBalance());
    }
}