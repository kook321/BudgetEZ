import java.util.ArrayList;
import java.util.List;

/**
 * Manages accounts and transaction history.
 */
public class WalletManager {
  private List<Account> accounts;
  private List<Transaction> transactionHistory;

  /**
   * Initializes the WalletManager with empty lists.
   */
  public WalletManager() {
    this.accounts = new ArrayList<>();
    this.transactionHistory = new ArrayList<>();
  }

  /**
   * Adds a new account to the system.
   *
   * @param acc The account to add.
   */
  public void addAccount(Account acc) {
    accounts.add(acc);
  }

  /**
   * Records a transaction, processes its balance, and saves it to history.
   *
   * @param t The transaction to record.
   */
  public void recordTransaction(Transaction t) {
    t.processTransaction(); // Update account balances
    transactionHistory.add(t); // Save to history
  }

  /**
   * Calculates the total net balance across all accounts.
   *
   * @return The sum of all account balances.
   */
  public double getTotalNetBalance() {
    double total = 0;
    for (Account acc : accounts) {
      total += acc.getBalance();
    }
    return total;
  }
}
