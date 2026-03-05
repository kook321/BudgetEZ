import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class UI extends JFrame {

  private final Color BG_MAIN = new Color(30, 30, 30);
  private final Color BG_CARD = new Color(45, 45, 45);
  private final Color TEXT_COLOR = new Color(255, 255, 255);
  private final Color BTN_BLUE = new Color(0, 123, 255);
  private final Color BTN_TEAL = new Color(23, 162, 184);
  private final Color BTN_GREEN = new Color(40, 167, 69);
  private final Color BTN_RED = new Color(220, 53, 69);

  private DefaultTableModel txTableModel;
  private JTable txTable;
  private JLabel sumIncomeLabel, sumExpenseLabel, todayBudgetLabel, monthBudgetLabel, netBalanceLabel;
  private JPanel accountsListPanel;

  public UI() {
    super("Balance Sheet Dashboard");

    // Fix thai font, up to os system
    // You can change the font to be compatible with your operating system
    Font thaiFont = new Font("SansSerif", Font.PLAIN, 14);
    Font thaiFontBold = new Font("SansSerif", Font.BOLD, 13);

    UIManager.put("Label.font", thaiFont);
    UIManager.put("Button.font", thaiFontBold);
    UIManager.put("ComboBox.font", thaiFont);
    UIManager.put("TextField.font", thaiFont);
    UIManager.put("Table.font", thaiFont);
    UIManager.put("TableHeader.font", thaiFontBold);
    UIManager.put("OptionPane.messageFont", thaiFont);
    UIManager.put("OptionPane.buttonFont", thaiFontBold);

    initComponents();
    setSize(1280, 720);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    refreshData();
  }

  private void initComponents() {
    getContentPane().setBackground(BG_MAIN);
    setLayout(new BorderLayout(15, 15));
    ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // ================= LEFT PANEL =================
    JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
    leftPanel.setBackground(BG_MAIN);
    leftPanel.setPreferredSize(new Dimension(320, 0));

    // Actions Card
    JPanel actionCard = createCardPanel("Actions");
    actionCard.setLayout(new GridLayout(0, 1, 0, 10));

    JButton newTxBtn = createButton("+ New Transaction", BTN_BLUE);
    newTxBtn.addActionListener(e -> showTransactionDialog(null));
    JButton accBtn = createButton("⚙ Manage Accounts", BTN_GREEN);
    accBtn.addActionListener(e -> manageAccountsDialog());
    JButton budgetBtn = createButton("⌂ Set Budget", BTN_TEAL);
    budgetBtn.addActionListener(e -> setBudget());
    JButton refreshBtn = createButton("↺ Refresh Data", new Color(100, 100, 100));
    refreshBtn.addActionListener(e -> refreshData());

    actionCard.add(newTxBtn);
    actionCard.add(accBtn);
    actionCard.add(budgetBtn);
    actionCard.add(refreshBtn);

    // Summary Card
    JPanel summaryCard = createCardPanel("Summary");
    summaryCard.setLayout(new BorderLayout(0, 10));

    JPanel summaryGrid = new JPanel(new GridLayout(2, 2, 10, 10));
    summaryGrid.setBackground(BG_CARD);

    sumIncomeLabel = createSummaryBox("Total Income", new Color(76, 175, 80));
    sumExpenseLabel = createSummaryBox("Total Expense", new Color(244, 67, 54));
    todayBudgetLabel = createSummaryBox("Today's Budget Left", BTN_TEAL);
    monthBudgetLabel = createSummaryBox("Monthly Budget Left", BTN_TEAL);

    summaryGrid.add(sumIncomeLabel);
    summaryGrid.add(sumExpenseLabel);
    summaryGrid.add(todayBudgetLabel);
    summaryGrid.add(monthBudgetLabel);

    netBalanceLabel = createSummaryBox("Net Balance", TEXT_COLOR);

    summaryCard.add(summaryGrid, BorderLayout.CENTER);
    summaryCard.add(netBalanceLabel, BorderLayout.SOUTH);

    // Accounts Card
    JPanel accountsCard = createCardPanel("My Accounts");
    accountsCard.setLayout(new BorderLayout());
    accountsListPanel = new JPanel();
    accountsListPanel.setLayout(new BoxLayout(accountsListPanel, BoxLayout.Y_AXIS));
    accountsListPanel.setBackground(BG_CARD);
    accountsCard.add(new JScrollPane(accountsListPanel), BorderLayout.CENTER);

    JPanel leftWrapper = new JPanel(new BorderLayout(0, 15));
    leftWrapper.setBackground(BG_MAIN);
    leftWrapper.add(actionCard, BorderLayout.NORTH);
    leftWrapper.add(summaryCard, BorderLayout.CENTER);

    leftPanel.add(leftWrapper, BorderLayout.NORTH);
    leftPanel.add(accountsCard, BorderLayout.CENTER);
    add(leftPanel, BorderLayout.WEST);

    // ================= RIGHT PANEL =================
    JPanel rightPanel = createCardPanel("Transaction History");
    rightPanel.setLayout(new BorderLayout(0, 10));

    String[] columns = { "ID", "Date", "Name", "Category", "Type", "Amount", "From", "To", "Status", "Note" };
    txTableModel = new DefaultTableModel(columns, 0) {
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    txTable = new JTable(txTableModel);

    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(txTableModel);
    txTable.setRowSorter(sorter);

    txTable.getColumnModel().getColumn(0).setMinWidth(0);
    txTable.getColumnModel().getColumn(0).setMaxWidth(0);

    txTable.setBackground(BG_CARD);
    txTable.setForeground(TEXT_COLOR);
    txTable.setGridColor(new Color(85, 85, 85));
    txTable.getTableHeader().setBackground(new Color(61, 61, 61));
    txTable.getTableHeader().setForeground(TEXT_COLOR);
    txTable.setRowHeight(30);
    rightPanel.add(new JScrollPane(txTable), BorderLayout.CENTER);

    JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    tableActions.setBackground(BG_CARD);
    JButton editBtn = createButton("Edit Selected", new Color(255, 152, 0));
    editBtn.addActionListener(e -> editSelectedTransaction());
    JButton delBtn = createButton("Delete Selected", BTN_RED);
    delBtn.addActionListener(e -> deleteSelectedTransaction());
    tableActions.add(editBtn);
    tableActions.add(delBtn);
    rightPanel.add(tableActions, BorderLayout.SOUTH);

    add(rightPanel, BorderLayout.CENTER);
  }

  // --- Helpers การสร้าง UI แนว Web App ---
  private JPanel createCardPanel(String title) {
    JPanel panel = new JPanel(new BorderLayout(0, 10));
    panel.setBackground(BG_CARD);
    panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1, true),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    JLabel titleLabel = new JLabel(title);
    titleLabel.setForeground(TEXT_COLOR);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    panel.add(titleLabel, BorderLayout.NORTH);
    return panel;
  }

  private JButton createButton(String text, Color bgColor) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    return btn;
  }

  private JLabel createSummaryBox(String title, Color valColor) {
    JLabel label = new JLabel();
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setOpaque(true);
    label.setBackground(new Color(61, 61, 61));
    label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    updateSummaryBox(label, title, 0.00, valColor);
    return label;
  }

  private void updateSummaryBox(JLabel label, String title, double value, Color color) {
    String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    String html = "<html><div style='text-align: center; font-family: sans-serif;'>"
        + "<div style='color: #aaaaaa; font-size: 11px; margin-bottom: 5px;'>" + title + "</div>"
        + "<div style='color: " + hexColor + "; font-size: 16px; font-weight: bold;'>" + String.format("%,.2f", value)
        + "</div>"
        + "</div></html>";
    label.setText(html);
  }

  private void updateBudgetBox(JLabel label, String title, double value, boolean noLimit) {
    if (noLimit) {
      String html = "<html><div style='text-align: center; font-family: sans-serif;'>"
          + "<div style='color: #aaaaaa; font-size: 11px; margin-bottom: 5px;'>" + title + "</div>"
          + "<div style='color: #aaaaaa; font-size: 14px; font-weight: bold;'>Unlimited</div>"
          + "</div></html>";
      label.setText(html);
    } else {
      Color color = value < 0 ? new Color(244, 67, 54) : new Color(23, 162, 184);
      updateSummaryBox(label, title, value, color);
    }
  }

  // ================= LOGIC =================

  private void showTransactionDialog(String editId) {
    JPanel form = new JPanel(new GridLayout(9, 2, 5, 5));
    JTextField dateField = new JTextField(LocalDate.now().toString());
    JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
    JTextField nameField = new JTextField();
    JComboBox<Category> catBox = new JComboBox<>(Category.values());
    JTextField amountField = new JTextField();
    JComboBox<TransactionStatus> statusBox = new JComboBox<>(TransactionStatus.values());
    JTextField noteField = new JTextField("-");

    JComboBox<String> fromBox = new JComboBox<>();
    fromBox.addItem("None");
    JComboBox<String> toBox = new JComboBox<>();
    toBox.addItem("None");

    List<String> accData = DatabaseManager.getAccountNames();
    for (String s : accData) {
      String accName = s.split(":")[1]; // 🌟 โชว์ชื่อบัญชี (Index 1)
      fromBox.addItem(accName);
      toBox.addItem(accName);
    }

    form.add(new JLabel("Date (yyyy-mm-dd):"));
    form.add(dateField);
    form.add(new JLabel("Type:"));
    form.add(typeBox);
    form.add(new JLabel("Name:"));
    form.add(nameField);
    form.add(new JLabel("Category:"));
    form.add(catBox);
    form.add(new JLabel("From Account:"));
    form.add(fromBox);
    form.add(new JLabel("To Account:"));
    form.add(toBox);
    form.add(new JLabel("Amount:"));
    form.add(amountField);
    form.add(new JLabel("Status:"));
    form.add(statusBox);
    form.add(new JLabel("Note:"));
    form.add(noteField);

    if (editId != null) {
      int row = txTable.getSelectedRow();
      dateField.setText(txTable.getValueAt(row, 1).toString());
      nameField.setText(txTable.getValueAt(row, 2).toString());
      catBox.setSelectedItem(Category.valueOf(txTable.getValueAt(row, 3).toString()));
      typeBox.setSelectedItem(TransactionType.valueOf(txTable.getValueAt(row, 4).toString()));
      amountField.setText(txTable.getValueAt(row, 5).toString());
      fromBox.setSelectedItem(txTable.getValueAt(row, 6) != null ? txTable.getValueAt(row, 6).toString() : "None");
      toBox.setSelectedItem(txTable.getValueAt(row, 7) != null ? txTable.getValueAt(row, 7).toString() : "None");
      statusBox.setSelectedItem(TransactionStatus.valueOf(txTable.getValueAt(row, 8).toString()));
      noteField.setText(txTable.getValueAt(row, 9) != null ? txTable.getValueAt(row, 9).toString() : "-");
    }

    int result = JOptionPane.showConfirmDialog(this, form, editId == null ? "New Transaction" : "Edit Transaction",
        JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      try {
        Account fAcc = fromBox.getSelectedItem().equals("None") ? null
            : new Account(fromBox.getSelectedItem().toString(), 0);
        Account tAcc = toBox.getSelectedItem().equals("None") ? null
            : new Account(toBox.getSelectedItem().toString(), 0);

        Transaction tx = new Transaction(
            editId == null ? "TX" + System.currentTimeMillis() : editId,
            nameField.getText(), noteField.getText(),
            (TransactionType) typeBox.getSelectedItem(), (Category) catBox.getSelectedItem(),
            LocalDate.parse(dateField.getText()), fAcc, tAcc,
            Double.parseDouble(amountField.getText()), (TransactionStatus) statusBox.getSelectedItem());

        if (editId == null)
          DatabaseManager.saveTransaction(tx);
        else
          DatabaseManager.updateTransaction(tx, editId);

        refreshData();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid Input Format!");
      }
    }
  }

  private void editSelectedTransaction() {
    int row = txTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Please select a row to edit.");
      return;
    }
    String id = txTableModel.getValueAt(txTable.convertRowIndexToModel(row), 0).toString();
    showTransactionDialog(id);
  }

  private void deleteSelectedTransaction() {
    int row = txTable.getSelectedRow();
    if (row == -1) {
      JOptionPane.showMessageDialog(this, "Please select a row to delete.");
      return;
    }
    if (JOptionPane.showConfirmDialog(this, "Delete this transaction?", "Confirm",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      String id = txTableModel.getValueAt(txTable.convertRowIndexToModel(row), 0).toString();
      DatabaseManager.deleteTransaction(id);
      refreshData();
    }
  }

  // 🌟 ระบบตั้งงบประมาณ (เลือกรายเดือน/รายวันได้)
  private void setBudget() {
    JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
    JComboBox<String> modeBox = new JComboBox<>(new String[] { "MONTHLY", "DAILY" });
    JTextField amtF = new JTextField();

    // ดึงค่าเก่ามาโชว์ในช่อง
    String budgetJson = DatabaseManager.getBudgetAsJSON();
    String currentMode = budgetJson.contains("\"mode\":\"MONTHLY\"") ? "MONTHLY" : "DAILY";
    String amountStr = budgetJson.split("\"amount\":")[1].replace("}", "").trim();
    modeBox.setSelectedItem(currentMode);
    amtF.setText(amountStr);

    form.add(new JLabel("Budget Mode:"));
    form.add(modeBox);
    form.add(new JLabel("Amount (THB):"));
    form.add(amtF);

    if (JOptionPane.showConfirmDialog(this, form, "Set Budget",
        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
      try {
        DatabaseManager.updateBudget(modeBox.getSelectedItem().toString(), Double.parseDouble(amtF.getText()));
        refreshData();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid number");
      }
    }
  }

  // 🌟 ระบบจัดการบัญชี (เพิ่มใหม่ / แก้ไข / ลบ)
  private void manageAccountsDialog() {
    JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));

    JComboBox<String> accCombo = new JComboBox<>();
    accCombo.addItem("--- Create New Account ---");
    List<String> accData = DatabaseManager.getAccountNames();
    for (String s : accData) {
      accCombo.addItem(s.split(":")[1]); // โชว์แค่ชื่อบัญชี
    }

    JTextField nameF = new JTextField();
    JTextField balF = new JTextField();

    form.add(new JLabel("Select to Edit:"));
    form.add(accCombo);
    form.add(new JLabel("Account Name:"));
    form.add(nameF);
    form.add(new JLabel("Initial Balance:"));
    form.add(balF);

    // ดึงข้อมูลมาเติมให้อัตโนมัติเวลาสลับ Dropdown
    accCombo.addActionListener(e -> {
      int idx = accCombo.getSelectedIndex();
      if (idx == 0) {
        nameF.setText("");
        balF.setText("");
      } else {
        String[] parts = accData.get(idx - 1).split(":");
        nameF.setText(parts[1]);
        balF.setText(parts[2]);
      }
    });

    Object[] options = { "Save", "Delete", "Cancel" };
    int result = JOptionPane.showOptionDialog(this, form, "Manage Accounts",
        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
        null, options, options[0]);

    if (result == 0) { // กด Save
      try {
        int idx = accCombo.getSelectedIndex();
        if (idx == 0) {
          DatabaseManager.saveAccount(nameF.getText(), Double.parseDouble(balF.getText()));
        } else {
          int id = Integer.parseInt(accData.get(idx - 1).split(":")[0]); // เอา ID ไปอัปเดต
          DatabaseManager.updateAccount(id, nameF.getText(), Double.parseDouble(balF.getText()));
        }
        refreshData();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid number");
      }
    } else if (result == 1) { // กด Delete
      int idx = accCombo.getSelectedIndex();
      if (idx > 0) {
        int id = Integer.parseInt(accData.get(idx - 1).split(":")[0]);
        DatabaseManager.deleteAccount(id);
        refreshData();
      } else {
        JOptionPane.showMessageDialog(this, "Cannot delete 'Create New Account'");
      }
    }
  }

  private void refreshData() {
    txTableModel.setRowCount(0);
    Object[][] data = DatabaseManager.getTransactionsForSwingTable();
    for (Object[] row : data)
      txTableModel.addRow(row);

    accountsListPanel.removeAll();
    List<String> accs = DatabaseManager.getAccountNames();
    double net = 0;
    for (String a : accs) {
      String[] parts = a.split(":"); // 0=ID, 1=Name, 2=Balance
      JLabel l = new JLabel(parts[1] + " = " + String.format("%,.2f", Double.parseDouble(parts[2])) + " THB");
      l.setForeground(TEXT_COLOR);
      l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
      accountsListPanel.add(l);
      net += Double.parseDouble(parts[2]);
    }
    accountsListPanel.revalidate();
    accountsListPanel.repaint();

    double inc = 0, exp = 0, todayExp = 0, monthExp = 0;
    LocalDate today = LocalDate.now();

    for (Object[] row : data) {
      if (row[8].toString().equals("COMPLETED")) {
        double amt = (double) row[5];
        if (row[4].toString().equals("INCOME")) {
          inc += amt;
        } else if (row[4].toString().equals("EXPENSE")) {
          exp += amt;
          LocalDate txDate = LocalDate.parse(row[1].toString());
          if (txDate.equals(today))
            todayExp += amt;
          if (txDate.getMonth() == today.getMonth() && txDate.getYear() == today.getYear())
            monthExp += amt;
        }
      }
    }

    String budgetJson = DatabaseManager.getBudgetAsJSON();
    String mode = budgetJson.contains("\"mode\":\"MONTHLY\"") ? "MONTHLY" : "DAILY";
    String amountStr = budgetJson.split("\"amount\":")[1].replace("}", "").trim();
    double budgetAmount = Double.parseDouble(amountStr);

    int daysInMonth = today.lengthOfMonth();
    double dailyLimit = 0, monthlyLimit = 0;

    if (budgetAmount > 0) {
      if (mode.equals("MONTHLY")) {
        monthlyLimit = budgetAmount;
        dailyLimit = budgetAmount / daysInMonth;
      } else {
        dailyLimit = budgetAmount;
        monthlyLimit = budgetAmount * daysInMonth;
      }
    }

    updateSummaryBox(sumIncomeLabel, "Total Income", inc, new Color(76, 175, 80));
    updateSummaryBox(sumExpenseLabel, "Total Expense", exp, new Color(244, 67, 54));
    updateSummaryBox(netBalanceLabel, "Net Balance", (net + inc - exp), TEXT_COLOR);

    updateBudgetBox(todayBudgetLabel, "Today's Budget Left", dailyLimit - todayExp, budgetAmount == 0);
    updateBudgetBox(monthBudgetLabel, "Monthly Budget Left", monthlyLimit - monthExp, budgetAmount == 0);
  }
}
