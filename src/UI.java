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
  private JLabel sumIncomeLabel, sumExpenseLabel, netBalanceLabel;
  private JPanel accountsListPanel;

  public UI() {
    super("Balance Sheet Dashboard");
    UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
    UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 12));
    initComponents();
    setSize(1100, 700);
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
    leftPanel.setPreferredSize(new Dimension(300, 0));

    // 1. Actions Card
    JPanel actionCard = createCardPanel("Actions");
    actionCard.setLayout(new GridLayout(4, 1, 0, 10));
    JButton newTxBtn = createButton("+ New Transaction", BTN_BLUE);
    newTxBtn.addActionListener(e -> showTransactionDialog(null)); // null means Create New
    JButton accBtn = createButton("⚙ Manage Accounts", BTN_GREEN);
    accBtn.addActionListener(e -> manageAccountsDialog());
    JButton budgetBtn = createButton("📊 Set Budget", BTN_TEAL);
    budgetBtn.addActionListener(e -> setBudget());
    JButton refreshBtn = createButton("🔄 Refresh Data", new Color(100, 100, 100));
    refreshBtn.addActionListener(e -> refreshData());
    actionCard.add(newTxBtn);
    actionCard.add(accBtn);
    actionCard.add(budgetBtn);
    actionCard.add(refreshBtn);

    // 2. Summary Card
    JPanel summaryCard = createCardPanel("Summary");
    summaryCard.setLayout(new GridLayout(3, 1, 0, 10));
    sumIncomeLabel = new JLabel("Total Income: 0.00");
    sumIncomeLabel.setForeground(new Color(76, 175, 80));
    sumExpenseLabel = new JLabel("Total Expense: 0.00");
    sumExpenseLabel.setForeground(new Color(244, 67, 54));
    netBalanceLabel = new JLabel("Net Balance: 0.00");
    netBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    netBalanceLabel.setForeground(TEXT_COLOR);
    summaryCard.add(sumIncomeLabel);
    summaryCard.add(sumExpenseLabel);
    summaryCard.add(netBalanceLabel);

    // 3. Accounts Card
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
      } // ห้ามดับเบิลคลิกแก้ในตาราง
    };
    txTable = new JTable(txTableModel);

    // 🌟 เพิ่มระบบคลิกหัวตารางเพื่อ Sort
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(txTableModel);
    txTable.setRowSorter(sorter);

    // ซ่อนคอลัมน์ ID (ไว้ใช้หลังบ้าน)
    txTable.getColumnModel().getColumn(0).setMinWidth(0);
    txTable.getColumnModel().getColumn(0).setMaxWidth(0);

    txTable.setBackground(BG_CARD);
    txTable.setForeground(TEXT_COLOR);
    txTable.setGridColor(new Color(85, 85, 85));
    txTable.getTableHeader().setBackground(new Color(61, 61, 61));
    txTable.getTableHeader().setForeground(TEXT_COLOR);
    txTable.setRowHeight(30);
    rightPanel.add(new JScrollPane(txTable), BorderLayout.CENTER);

    // ปุ่มใต้ตาราง
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

  // --- Helper Methods ---
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
      String accName = s.split(":")[0];
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

    // ดึงข้อมูลเก่ามาใส่ถ้าเป็นการ Edit
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

  private void setBudget() {
    String input = JOptionPane.showInputDialog(this, "Monthly Budget (THB):");
    if (input != null && !input.isEmpty()) {
      try {
        DatabaseManager.updateBudget("MONTHLY", Double.parseDouble(input));
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid number");
      }
    }
  }

  private void manageAccountsDialog() {
    JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
    JTextField nameF = new JTextField();
    JTextField balF = new JTextField();
    form.add(new JLabel("Account Name:"));
    form.add(nameF);
    form.add(new JLabel("Initial Balance:"));
    form.add(balF);
    if (JOptionPane.showConfirmDialog(this, form, "Add Account",
        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
      try {
        DatabaseManager.saveAccount(nameF.getText(), Double.parseDouble(balF.getText()));
        refreshData();
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving account");
      }
    }
  }

  private void refreshData() {
    // อัปเดตตาราง
    txTableModel.setRowCount(0);
    Object[][] data = DatabaseManager.getTransactionsForSwingTable();
    for (Object[] row : data)
      txTableModel.addRow(row);

    // อัปเดต Account List ด้านซ้าย
    accountsListPanel.removeAll();
    List<String> accs = DatabaseManager.getAccountNames();
    double net = 0;
    for (String a : accs) {
      String[] parts = a.split(":");
      JLabel l = new JLabel(parts[0] + " = " + parts[1] + " THB");
      l.setForeground(TEXT_COLOR);
      accountsListPanel.add(l);
      net += Double.parseDouble(parts[1]);
    }
    accountsListPanel.revalidate();
    accountsListPanel.repaint();

    // คำนวณรายรับรายจ่ายรวม
    double inc = 0, exp = 0;
    for (Object[] row : data) {
      if (row[8].toString().equals("COMPLETED")) {
        if (row[4].toString().equals("INCOME"))
          inc += (double) row[5];
        if (row[4].toString().equals("EXPENSE"))
          exp += (double) row[5];
      }
    }
    sumIncomeLabel.setText(String.format("Total Income: %.2f", inc));
    sumExpenseLabel.setText(String.format("Total Expense: %.2f", exp));
    netBalanceLabel.setText(String.format("Net Balance: %.2f", (net + inc - exp)));
  }
}
