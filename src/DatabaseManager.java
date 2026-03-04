import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
  // กำหนดตำแหน่งไฟล์ Database (จะถูกสร้างอัตโนมัติในโฟลเดอร์โปรเจกต์)
  private static final String URL = "jdbc:sqlite:finance.db";

  public static void initialize() {
    try {
      // 1. บังคับโหลด Driver (สังเกตตัวพิมพ์ใหญ่ JDBC)
      Class.forName("org.sqlite.JDBC");

      // 2. เชื่อมต่อและสร้างตาราง
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();

      stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
          "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
          "name TEXT UNIQUE, " +
          "balance REAL)");

      stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
          "id TEXT PRIMARY KEY, " +
          "tx_date TEXT, " +
          "type TEXT, " +
          "name TEXT, " +
          "category TEXT, " +
          "amount REAL, " +
          "status TEXT, " +
          "from_account TEXT, " +
          "to_account TEXT, " +
          "note TEXT)");

      stmt.execute("CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT)");
      // ใส่ค่าเริ่มต้นถ้ายังไม่มี
      stmt.execute("INSERT OR IGNORE INTO settings(key, value) VALUES('budget_mode', 'MONTHLY')");
      stmt.execute("INSERT OR IGNORE INTO settings(key, value) VALUES('budget_amount', '0')");

      stmt.close();
      conn.close();
      System.out.println("✅ Database tables initialized (finance.db)");

    } catch (ClassNotFoundException e) {
      System.err.println("❌ หาไฟล์ Driver (.jar) ไม่เจอ: " + e.getMessage());
    } catch (SQLException e) {
      System.err.println("❌ Database Error: " + e.getMessage());
    }
  }

  public static void saveTransaction(Transaction tx) {
    String sql = "INSERT INTO transactions(id, tx_date, type, name, category, amount, status, from_account, to_account, note) VALUES(?,?,?,?,?,?,?,?,?,?)";

    try {
      // บังคับโหลด Driver ก่อนเชื่อมต่อ
      Class.forName("org.sqlite.JDBC");

      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);

      pstmt.setString(1, "TX" + System.currentTimeMillis());
      pstmt.setString(2, tx.getDate() != null ? tx.getDate().toString() : LocalDate.now().toString());
      pstmt.setString(3, tx.getType().toString());
      pstmt.setString(4, tx.getDetails());
      pstmt.setString(5, tx.getCategory().toString());
      pstmt.setDouble(6, tx.getAmount());
      pstmt.setString(7, tx.getStatus().toString());
      pstmt.setString(8, tx.getFromAccount() != null ? tx.getFromAccount().getName() : null);
      pstmt.setString(9, tx.getToAccount() != null ? tx.getToAccount().getName() : null);
      pstmt.setString(10, tx.getNote());

      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      System.out.println("💾 บันทึก Transaction ลง Database สำเร็จ!");

    } catch (ClassNotFoundException e) {
      System.err.println("❌ หาไฟล์ Driver (.jar) ไม่เจอ: " + e.getMessage());
    } catch (SQLException e) {
      System.err.println("❌ Error saving transaction: " + e.getMessage());
    }
  }

  // 🌟 ดึงข้อมูลทั้งหมดจาก Database แปลงเป็นรูปแบบ JSON ส่งให้หน้าเว็บ
  public static String getAllTransactionsAsJSON() {
    StringBuilder json = new StringBuilder("[\n");
    String sql = "SELECT * FROM transactions ORDER BY tx_date ASC";

    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);

      boolean first = true;
      while (rs.next()) {
        if (!first)
          json.append(",\n");
        json.append("  {");
        json.append("\"id\":\"").append(rs.getString("id")).append("\",");
        json.append("\"date\":\"").append(rs.getString("tx_date")).append("\",");
        json.append("\"type\":\"").append(rs.getString("type")).append("\",");
        json.append("\"name\":\"").append(rs.getString("name")).append("\",");
        json.append("\"category\":\"").append(rs.getString("category")).append("\",");
        json.append("\"amount\":").append(rs.getDouble("amount")).append(",");
        json.append("\"status\":\"").append(rs.getString("status")).append("\",");
        json.append("\"from\":\"").append(rs.getString("from_account")).append("\",");
        json.append("\"to\":\"").append(rs.getString("to_account")).append("\",");
        json.append("\"note\":\"").append(rs.getString("note") != null ? rs.getString("note") : "-").append("\"");
        json.append("}");
        first = false;
      }
      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Error fetching transactions: " + e.getMessage());
    }
    json.append("\n]");
    return json.toString();
  }

  // 🌟 ฟังก์ชันลบรายการ
  public static void deleteTransaction(String id) {
    String sql = "DELETE FROM transactions WHERE id = ?";
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      System.out.println("🗑️ ลบ Transaction ID: " + id + " สำเร็จ!");
    } catch (Exception e) {
      System.err.println("❌ Error deleting transaction: " + e.getMessage());
    }
  }

  // 🌟 ฟังก์ชันอัปเดตรายการเดิม
  public static void updateTransaction(Transaction tx, String id) {
    String sql = "UPDATE transactions SET tx_date=?, type=?, name=?, category=?, amount=?, status=?, from_account=?, to_account=?, note=? WHERE id=?";
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, tx.getDate() != null ? tx.getDate().toString() : LocalDate.now().toString());
      pstmt.setString(2, tx.getType().toString());
      pstmt.setString(3, tx.getDetails());
      pstmt.setString(4, tx.getCategory().toString());
      pstmt.setDouble(5, tx.getAmount());
      pstmt.setString(6, tx.getStatus().toString());
      pstmt.setString(7, tx.getFromAccount() != null ? tx.getFromAccount().getName() : null);
      pstmt.setString(8, tx.getToAccount() != null ? tx.getToAccount().getName() : null);
      pstmt.setString(9, tx.getNote());
      pstmt.setString(10, id); // ระบุว่าแก้อันไหน
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
      System.out.println("🔄 อัปเดต Transaction ID: " + id + " สำเร็จ!");
    } catch (Exception e) {
      System.err.println("❌ Error updating transaction: " + e.getMessage());
    }
  }

  // 🌟 ดึงข้อมูล Account ทั้งหมดส่งให้หน้าเว็บ
  public static String getAllAccountsAsJSON() {
    StringBuilder json = new StringBuilder("[\n");
    String sql = "SELECT * FROM accounts";
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      boolean first = true;
      while (rs.next()) {
        if (!first)
          json.append(",\n");
        json.append("  {");
        json.append("\"id\":").append(rs.getInt("id")).append(",");
        json.append("\"name\":\"").append(rs.getString("name")).append("\",");
        json.append("\"initialBalance\":").append(rs.getDouble("balance")).append(",");
        json.append("\"balance\":").append(rs.getDouble("balance"));
        json.append("}");
        first = false;
      }
      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Error fetching accounts: " + e.getMessage());
    }
    json.append("\n]");
    return json.toString();
  }

  // 🌟 เซฟ Account ลง Database
  public static void saveAccount(String name, double balance) {
    String sql = "INSERT INTO accounts(name, balance) VALUES(?,?)";
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, name);
      pstmt.setDouble(2, balance);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Error: " + e.getMessage());
    }
  }

  // 🌟 อัปเดต Account เดิม
  public static void updateAccount(int id, String name, double balance) {
    String sql = "UPDATE accounts SET name=?, balance=? WHERE id=?";
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, name);
      pstmt.setDouble(2, balance);
      pstmt.setInt(3, id);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Error: " + e.getMessage());
    }
  }

  // 🌟 ลบ Account
  public static void deleteAccount(int id) {
    String sql = "DELETE FROM accounts WHERE id=?";
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
      pstmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Error: " + e.getMessage());
    }
  }

  // 🌟 ดึงข้อมูล Budget ส่งให้หน้าเว็บ
  public static String getBudgetAsJSON() {
    String mode = "MONTHLY";
    double amount = 0;
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM settings WHERE key IN ('budget_mode', 'budget_amount')");
      while (rs.next()) {
        if (rs.getString("key").equals("budget_mode"))
          mode = rs.getString("value");
        if (rs.getString("key").equals("budget_amount"))
          amount = Double.parseDouble(rs.getString("value"));
      }
      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Budget Fetch Error: " + e.getMessage());
    }

    return "{\"mode\":\"" + mode + "\", \"amount\":" + amount + "}";
  }

  // 🌟 บันทึกข้อมูล Budget
  public static void updateBudget(String mode, double amount) {
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(URL);
      PreparedStatement pstmt = conn.prepareStatement("UPDATE settings SET value=? WHERE key=?");

      pstmt.setString(1, mode);
      pstmt.setString(2, "budget_mode");
      pstmt.executeUpdate();
      pstmt.setString(1, String.valueOf(amount));
      pstmt.setString(2, "budget_amount");
      pstmt.executeUpdate();

      pstmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println("❌ Budget Update Error: " + e.getMessage());
    }
  }
}
