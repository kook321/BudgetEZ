import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class TransactionTest {

    @Test
    public void testIncomeTransaction() {

        Account acc = new Account("Bank", 1000);

        Transaction t = new Transaction(
                "1",
                "Salary",
                "monthly salary",
                TransactionType.INCOME,
                Category.SALARY,
                LocalDate.now(),
                null,
                acc,
                500,
                TransactionStatus.COMPLETED
        );

        t.processTransaction();

        assertEquals(1500, acc.getBalance());
    }

    @Test
    public void testExpenseTransaction() {

        Account acc = new Account("Cash", 1000);

        Transaction t = new Transaction(
                "2",
                "Food",
                "lunch",
                TransactionType.EXPENSE,
                Category.FOOD,
                LocalDate.now(),
                acc,
                null,
                200,
                TransactionStatus.COMPLETED
        );

        t.processTransaction();

        assertEquals(800, acc.getBalance());
    }

    @Test
    public void testTransferTransaction() {

        Account from = new Account("Cash", 1000);
        Account to = new Account("Bank", 500);

        Transaction t = new Transaction(
                "3",
                "Transfer",
                "move money",
                TransactionType.TRANSFER,
                Category.OTHERS,
                LocalDate.now(),
                from,
                to,
                300,
                TransactionStatus.COMPLETED
        );

        t.processTransaction();

        assertEquals(700, from.getBalance());
        assertEquals(800, to.getBalance());
    }

    @Test
    public void testPendingTransactionDoesNotChangeBalance() {

        Account acc = new Account("Cash", 1000);

        Transaction t = new Transaction(
                "4",
                "Pending salary",
                "waiting",
                TransactionType.INCOME,
                Category.SALARY,
                LocalDate.now(),
                null,
                acc,
                500,
                TransactionStatus.PENDING
        );

        t.processTransaction();

        assertEquals(1000, acc.getBalance());
    }

    @Test
    public void testCompleteTransactionChangesBalance() {

        Account acc = new Account("Bank", 1000);

        Transaction t = new Transaction(
                "5",
                "Salary",
                "pending salary",
                TransactionType.INCOME,
                Category.SALARY,
                LocalDate.now(),
                null,
                acc,
                500,
                TransactionStatus.PENDING
        );

        t.completeTransaction();

        assertEquals(1500, acc.getBalance());
        assertEquals(TransactionStatus.COMPLETED, t.getStatus());
    }
}
