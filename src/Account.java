/**
 * Represents a financial account (e.g., Cash, Bank, TrueMoney).
 */
public class Account {
  private String name;
  private double balance;

  /**
   * Constructs a new Account.
   *
   * @param name           The name of the account.
   * @param initialBalance The starting balance of the account.
   */
  public Account(String name, double initialBalance) {
    this.name = name;
    this.balance = initialBalance;
  }

  /**
   * Gets the account name.
   *
   * @return Account name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the current balance.
   *
   * @return Current balance.
   */
  public double getBalance() {
    return balance;
  }

  /**
   * Adds funds to the account.
   *
   * @param amount The amount to add.
   */
  public void addFunds(double amount) {
    this.balance += amount;
  }

  /**
   * Deducts funds from the account.
   *
   * @param amount The amount to deduct.
   */
  public void deductFunds(double amount) {
    this.balance -= amount;
  }
}
