public class Expense extends Transaction{
   // public boolean isNecessary

     public Expense(int id, String date, double amount,
                  String description, Categorys category,
                  String source) {
                super(id, date, amount > 0 ? -amount : amount, description, category);  // เรียกของแม่
        //this.source = source;
    }

    @Override
    public double calculateNewBalance(double currentBalance){
        currentBalance += this.amount;
        return super.calculateNewBalance(currentBalance);
    }
}
