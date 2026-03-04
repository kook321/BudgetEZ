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
    this.currentBalance = t.calculateNewBalance(this.currentBalance);
}

    public double calculateDailyAvailableFromDate(int today){

    double balanceUntilToday = currentBalance;

    // รวมเฉพาะรายการก่อนวันที่เลือก
    for(Transaction t : transactionList){

        String[] parts = t.date.split("-");
        int day = Integer.parseInt(parts[0]);

        if(day >= today){
            balanceUntilToday -= t.amount;
        }
    }

    int remainingDays = totalDaysinMonth - today + 1;

    if(remainingDays <= 0){
        return 0;
    }

    return balanceUntilToday / remainingDays;
}

   public void MonthlySummary(){
        System.out.println(currentBalance);
    }

    public List<Transaction> getTransactionList() {
    return transactionList;
}

public double getCurrentBalance() {
    return currentBalance;
}



}