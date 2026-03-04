public class Account {
  private String name;
  private double balance;

  public Account(String name, double initialBalance) {
    this.name = name;
    this.balance = initialBalance;
  }

  public String getName() {
    return name;
  }

  public double getBalance() {
    return balance;
  }

  public void addFunds(double amount) {
    this.balance += amount;
  }

  public void deductFunds(double amount) {
    this.balance -= amount;
  }
}
