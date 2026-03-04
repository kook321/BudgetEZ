import java.util.ArrayList;
import java.util.List;

public class WalletManager {
  private List<Account> accounts;
  private List<Transaction> transactionHistory;

  public WalletManager() {
    this.accounts = new ArrayList<>();
    this.transactionHistory = new ArrayList<>();
  }

  // เพิ่มบัญชีใหม่เข้าระบบ
  public void addAccount(Account acc) {
    accounts.add(acc);
  }

  // บันทึกธุรกรรมและประมวลผลเงิน
  public void recordTransaction(Transaction t) {
    t.processTransaction(); // อัปเดตยอดเงินในบัญชี
    transactionHistory.add(t); // เก็บเข้าประวัติ
  }

  // ดูยอดเงินรวมทุกบัญชี (Dashboard Overview)
  public double getTotalNetBalance() {
    double total = 0;
    for (Account acc : accounts) {
      total += acc.getBalance();
    }
    return total;
  }
}
