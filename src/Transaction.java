import java.time.LocalDate;

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

  // Transaction Status like complete, pending
  private TransactionStatus status;

  // Constructor
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

  // 🌟 3. ปรับปรุง Logic การคำนวณเงิน
  public void processTransaction() {
    // ดักเงื่อนไข: ถ้ายังไม่เสร็จสิ้น จะยังไม่เอาเงินไปคำนวณ
    if (this.status != TransactionStatus.COMPLETED) {
      System.out
          .println("⏳ รายการ [" + this.details + "] สถานะเป็น " + this.status + " (ยอดเงินในบัญชียังไม่ถูกอัปเดต)");
      return;
    }

    // ถ้าสถานะ COMPLETED แล้ว ค่อยทำงานตามปกติ
    if (this.type == TransactionType.EXPENSE && fromAccount != null) {
      fromAccount.deductFunds(amount);
      System.out.println("✅ หักเงินรายจ่าย [" + this.details + "] จำนวน " + amount + " เรียบร้อย");
    } else if (this.type == TransactionType.INCOME && toAccount != null) {
      toAccount.addFunds(amount);
      System.out.println("✅ รับเงินเข้า [" + this.details + "] จำนวน " + amount + " เรียบร้อย");
    } else if (this.type == TransactionType.TRANSFER && fromAccount != null && toAccount != null) {
      fromAccount.deductFunds(amount);
      toAccount.addFunds(amount);
      System.out.println("✅ โอนเงิน [" + this.details + "] จำนวน " + amount + " เรียบร้อย");
    }
  }

  // 🌟 4. เพิ่มฟังก์ชันสำหรับเปลี่ยนสถานะในภายหลัง (เช่น เปลี่ยนจาก Pending ->
  // Completed)
  public void completeTransaction() {
    if (this.status != TransactionStatus.COMPLETED) {
      this.status = TransactionStatus.COMPLETED;
      System.out.println("🔄 อัปเดตสถานะรายการ [" + this.details + "] เป็น COMPLETED");
      processTransaction(); // พอสถานะเปลี่ยนเป็นเสร็จสิ้น ค่อยเรียกฟังก์ชันคำนวณเงิน
    }
  }

  // Getter สำหรับดึงสถานะไปแสดงผลบนหน้าเว็บ
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
