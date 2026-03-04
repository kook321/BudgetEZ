import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppServer {
  static List<Account> accounts = new ArrayList<>();
  static List<Transaction> history = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    DatabaseManager.initialize();

    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    server.createContext("/api/transaction", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        // อนุญาตให้หน้าเว็บส่งคำสั่ง POST, PUT, DELETE มาได้ (CORS)
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
          exchange.sendResponseHeaders(204, -1);
          return;
        }

        String method = exchange.getRequestMethod();

        // --- กรณีเพิ่มใหม่ (POST) หรือ แก้ไข (PUT) ---
        if ("POST".equals(method) || "PUT".equals(method)) {
          String requestBody = new String(exchange.getRequestBody().readAllBytes());
          String[] data = requestBody.split("\\|");

          // ถ้าเป็น PUT (แก้ไข) ข้อมูลช่องแรกสุดจะเป็น ID
          int offset = "PUT".equals(method) ? 1 : 0;
          String id = "PUT".equals(method) ? data[0] : "TX" + System.currentTimeMillis();

          LocalDate date = LocalDate.parse(data[0 + offset]);
          TransactionType type = TransactionType.valueOf(data[1 + offset]);
          String name = data[2 + offset];
          Category category = Category.valueOf(data[3 + offset]);
          double amount = Double.parseDouble(data[4 + offset]);
          TransactionStatus status = TransactionStatus.valueOf(data[5 + offset]);
          Account fromAcc = (data[6 + offset].equals("None") || data[6 + offset].equals("-")) ? null
              : new Account(data[6 + offset], 0);
          Account toAcc = (data[7 + offset].equals("None") || data[7 + offset].equals("-")) ? null
              : new Account(data[7 + offset], 0);
          String note = data.length > (8 + offset) ? data[8 + offset] : "-";

          Transaction tx = new Transaction(id, name, note, type, category, date, fromAcc, toAcc, amount, status);

          if ("POST".equals(method))
            DatabaseManager.saveTransaction(tx);
          else
            DatabaseManager.updateTransaction(tx, id);

          exchange.sendResponseHeaders(200, "OK".getBytes().length);
          OutputStream os = exchange.getResponseBody();
          os.write("OK".getBytes());
          os.close();
        }
        // --- กรณีลบ (DELETE) ---
        else if ("DELETE".equals(method)) {
          String id = new String(exchange.getRequestBody().readAllBytes()); // รับ ID มาตรงๆ
          DatabaseManager.deleteTransaction(id);
          exchange.sendResponseHeaders(200, "OK".getBytes().length);
          OutputStream os = exchange.getResponseBody();
          os.write("OK".getBytes());
          os.close();
        }
      }
    });
    // 🌟 Endpoint จัดการบัญชี (GET, POST, PUT, DELETE)
    server.createContext("/api/account", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equals(method)) {
          exchange.sendResponseHeaders(204, -1);
          return;
        }

        if ("GET".equals(method)) {
          String json = DatabaseManager.getAllAccountsAsJSON();
          byte[] bytes = json.getBytes("UTF-8");
          exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
          exchange.sendResponseHeaders(200, bytes.length);
          OutputStream os = exchange.getResponseBody();
          os.write(bytes);
          os.close();
        } else if ("POST".equals(method) || "PUT".equals(method)) {
          String body = new String(exchange.getRequestBody().readAllBytes());
          String[] data = body.split("\\|");

          if ("PUT".equals(method))
            DatabaseManager.updateAccount(Integer.parseInt(data[0]), data[1], Double.parseDouble(data[2]));
          else
            DatabaseManager.saveAccount(data[0], Double.parseDouble(data[1]));

          exchange.sendResponseHeaders(200, "OK".getBytes().length);
          OutputStream os = exchange.getResponseBody();
          os.write("OK".getBytes());
          os.close();
        } else if ("DELETE".equals(method)) {
          int id = Integer.parseInt(new String(exchange.getRequestBody().readAllBytes()));
          DatabaseManager.deleteAccount(id);
          exchange.sendResponseHeaders(200, "OK".getBytes().length);
          OutputStream os = exchange.getResponseBody();
          os.write("OK".getBytes());
          os.close();
        }
      }
    });
    // 🌟 Endpoint สำหรับให้หน้าเว็บดึงข้อมูลประวัติ (Transactions) ทั้งหมด
    server.createContext("/api/data", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if ("GET".equals(exchange.getRequestMethod())) {
          String responseText = DatabaseManager.getAllTransactionsAsJSON();
          byte[] responseBytes = responseText.getBytes("UTF-8");

          exchange.sendResponseHeaders(200, responseBytes.length);
          OutputStream os = exchange.getResponseBody();
          os.write(responseBytes);
          os.close();
        }
      }
    });

    // 🌟 Endpoint สำหรับจัดการ Budget
    server.createContext("/api/budget", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equals(method)) {
          exchange.sendResponseHeaders(204, -1);
          return;
        }

        if ("GET".equals(method)) {
          String json = DatabaseManager.getBudgetAsJSON();
          byte[] bytes = json.getBytes("UTF-8");
          exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
          exchange.sendResponseHeaders(200, bytes.length);
          OutputStream os = exchange.getResponseBody();
          os.write(bytes);
          os.close();
        } else if ("POST".equals(method)) {
          String body = new String(exchange.getRequestBody().readAllBytes());
          String[] data = body.split("\\|");
          DatabaseManager.updateBudget(data[0], Double.parseDouble(data[1]));
          exchange.sendResponseHeaders(200, "OK".getBytes().length);
          OutputStream os = exchange.getResponseBody();
          os.write("OK".getBytes());
          os.close();
        }
      }
    });

    server.start();
    System.out.println("🚀 Backend Server รันแล้วที่พอร์ต 8080...");
    // 🌟 เปิดหน้าต่าง Desktop App ทันทีที่เซิร์ฟเวอร์รันเสร็จ
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new UI().setVisible(true);
      }
    });
  }
}
