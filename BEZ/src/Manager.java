import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.ArrayList;


public class Manager {
    public double currentBalance;
    public Month selectedMonth;
    public int currentYear;
    public int totalDaysinMonth;
    public List<Transaction> transactionList;

    public void setInfoBudget(Month month, int year, double amount) {
    this.selectedMonth = month;
    this.currentBalance = amount;
    this.currentYear = year;
    this.totalDaysinMonth = month.length(Year.isLeap(year));
    this.transactionList = new ArrayList<Transaction>();
    }

    public void addTransaction(Transaction t){
        this.transactionList.add(t);
        this.currentBalance += t.amount;
    }

    public double getDailyAverageAvaiable(int today){
    int remainingDays = totalDaysinMonth - today + 1;
    double DailyBudget = currentBalance / remainingDays;
    System.out.println(String.format("%.2f", DailyBudget));
    return currentBalance / remainingDays;
    }

   public void MonthlySummary(){
        System.out.println(currentBalance);
    }



}