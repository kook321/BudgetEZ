import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Month;
//import java.time.LocalDate;

public class UI extends JFrame {

    private Manager manager;

    private JComboBox<Month> monthBox;
    private JComboBox<Integer> yearBox;
    private JTextField budgetField;
   // private JLabel resultLabel;

    private DefaultTableModel tableModel;

    public UI() {
        super("BudgetEZ");

        manager = new Manager();

        initComponents();

        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {

        setLayout(new BorderLayout());

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(0,2));

        monthBox = new JComboBox<>(Month.values());

        yearBox = new JComboBox<>();
        for (int y = 2000; y <= 2077; y++) {
            yearBox.addItem(y);
        }

        budgetField = new JTextField();

        JButton setBudgetBtn = new JButton("Set Budget");
        setBudgetBtn.addActionListener(e -> setBudget());

        topPanel.add(new JLabel("Month:"));
        topPanel.add(monthBox);

        topPanel.add(new JLabel("Year:"));
        topPanel.add(yearBox);

        topPanel.add(new JLabel("Budget:"));
        topPanel.add(budgetField);

        topPanel.add(setBudgetBtn);

        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {"ID","Date","Amount","Description","Category"};
        tableModel = new DefaultTableModel(columns,0);
        JTable table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== BUTTON PANEL =====
        JPanel bottomPanel = new JPanel();

        JButton addIncomeBtn = new JButton("Add Income");
        JButton addExpenseBtn = new JButton("Add Expense");
        JButton summaryBtn = new JButton("Monthly Summary");
        JButton avgBtn = new JButton("Calculate Avg From Date");


        addIncomeBtn.addActionListener(e -> addIncome());
        addExpenseBtn.addActionListener(e -> addExpense());
        summaryBtn.addActionListener(e -> showSummary());
        avgBtn.addActionListener(e -> calculateFromInputDate());
        

        bottomPanel.add(addIncomeBtn);
        bottomPanel.add(addExpenseBtn);
        bottomPanel.add(summaryBtn);
        bottomPanel.add(avgBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setBudget(){
        try{
            Month month = (Month) monthBox.getSelectedItem();
            int year = (Integer) yearBox.getSelectedItem();
            double budget = Double.parseDouble(budgetField.getText());

            manager.setInfoBudget(month, year, budget);

            JOptionPane.showMessageDialog(this,"Budget Set Successfully!");

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Invalid Input");
        }
    }

    private void addIncome(){

    try{
        int day = Integer.parseInt(
                JOptionPane.showInputDialog("Day (1-31):")
        );

        String desc = JOptionPane.showInputDialog("Description:");

        double amount = Double.parseDouble(
                JOptionPane.showInputDialog("Amount:")
        );

        // เลือก Category
        Categorys category = (Categorys) JOptionPane.showInputDialog(
                this,
                "Select Category:",
                "Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Categorys.values(),
                Categorys.Salary
        );

        String fullDate = day + "-" +
                manager.selectedMonth.getValue() + "-" +
                manager.currentYear;

        Income income = new Income(
                fullDate,
                amount,
                desc,
                category,
                "General"
            );

        manager.addTransaction(income);
        refreshTable();

    }catch(Exception e){
        JOptionPane.showMessageDialog(this,"Invalid input");
    }
}

    private void addExpense(){

    try{
        int day = Integer.parseInt(
                JOptionPane.showInputDialog("Day (1-31):")
        );

        String desc = JOptionPane.showInputDialog("Description:");

        double amount = Double.parseDouble(
                JOptionPane.showInputDialog("Amount:")
        );

        Categorys category = (Categorys) JOptionPane.showInputDialog(
                this,
                "Select Category:",
                "Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                Categorys.values(),
                Categorys.Food
        );

        String fullDate = day + "-" +
                manager.selectedMonth.getValue() + "-" +
                manager.currentYear;

        Expense expense = new Expense(
            fullDate,
            amount,
            desc,
            category,
            true
            );

    manager.addTransaction(expense);
    refreshTable();

    }catch(Exception e){
        JOptionPane.showMessageDialog(this,"Invalid input");
    }
}

    private void showSummary(){

        JOptionPane.showMessageDialog(this,
                "Current Balance: " +
                String.format("%.2f", manager.getCurrentBalance())
        );
    }

    private void refreshTable(){

    // ล้างแถวเก่าทั้งหมด
    tableModel.setRowCount(0);

    // ใส่ข้อมูลใหม่จาก transactionList
    for(Transaction t : manager.getTransactionList()){

        tableModel.addRow(new Object[]{
                t.id,
                t.date,
                t.amount,
                t.description,
                t.category
        });
    }
}

private void calculateFromInputDate(){

    try{

        int today = Integer.parseInt(
                JOptionPane.showInputDialog("Enter today's date:")
        );

        double daily = manager.calculateDailyAvailableFromDate(today);

        JOptionPane.showMessageDialog(this,
                "Available per day: " +
                String.format("%.2f", daily)
        );

    }catch(Exception e){
        JOptionPane.showMessageDialog(this,"Invalid input");
    }
}

    public static void main(String[] args) {
        new UI();
    }
}