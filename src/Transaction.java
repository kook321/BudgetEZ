import java.time.LocalDate;

/**
 * Represents a single financial transaction.
 */
public class Transaction {
  private String id;
  private String details;
  private String note;
  private TransactionType type;
  private Category category;
  private LocalDate date;
  private Account fromAccount;
  private Account toAccount;
  private double amount;
  private TransactionStatus status;

  /**
   * Constructs a new Transaction.
   *
   * @param id          Unique transaction ID.
   * @param details     Name or description of the transaction.
   * @param note        Additional notes.
   * @param type        Transaction type (INCOME, EXPENSE, TRANSFER).
   * @param category    Transaction category.
   * @param date        Date of the transaction.
   * @param fromAccount Source account (can be null for INCOME).
   * @param toAccount   Destination account (can be null for EXPENSE).
   * @param amount      Transaction amount.
   * @param status      Current status (PENDING, COMPLETED).
   */
  public Transaction(String id, String details, String note, TransactionType type, Category category,
      LocalDate date, Account fromAccount, Account toAccount, double amount,
      TransactionStatus status) {
    this.id = id;
    this.details = details;
    this.note = note;
    this.type = type;
    this.category = category;
    this.date = date;
    this.fromAccount = fromAccount;
    this.toAccount = toAccount;
    this.amount = amount;
    this.status = status;
  }

  /**
   * Processes the transaction by updating account balances based on its type.
   * Balances are only updated if the status is COMPLETED.
   */
  public void processTransaction() {
    // Condition: Do not calculate if the transaction is not completed
    if (this.status != TransactionStatus.COMPLETED) {
      System.out.println(
          "⏳ Transaction [" + this.details + "] status is " + this.status + " (Account balance not updated yet)");
      return;
    }

    // Process balances if status is COMPLETED
    if (this.type == TransactionType.EXPENSE && fromAccount != null) {
      fromAccount.deductFunds(amount);
      System.out.println("✅ Expense deducted [" + this.details + "] Amount: " + amount + " successfully");
    } else if (this.type == TransactionType.INCOME && toAccount != null) {
      toAccount.addFunds(amount);
      System.out.println("✅ Income added [" + this.details + "] Amount: " + amount + " successfully");
    } else if (this.type == TransactionType.TRANSFER && fromAccount != null && toAccount != null) {
      fromAccount.deductFunds(amount);
      toAccount.addFunds(amount);
      System.out.println("✅ Transfer completed [" + this.details + "] Amount: " + amount + " successfully");
    }
  }

  /**
   * Updates the transaction status to COMPLETED and processes the balances.
   */
  public void completeTransaction() {
    if (this.status != TransactionStatus.COMPLETED) {
      this.status = TransactionStatus.COMPLETED;
      System.out.println("🔄 Status updated for [" + this.details + "] to COMPLETED");
      processTransaction(); // Process calculation after status change
    }
  }

  // Getters
  public TransactionStatus getStatus() {
    return this.status;
  }

  public LocalDate getDate() {
    return date;
  }

  public TransactionType getType() {
    return type;
  }

  public String getDetails() {
    return details;
  }

  public Category getCategory() {
    return category;
  }

  public double getAmount() {
    return amount;
  }

  public Account getFromAccount() {
    return fromAccount;
  }

  public Account getToAccount() {
    return toAccount;
  }

  public String getNote() {
    return note;
  }
}
