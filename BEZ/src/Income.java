public class Income extends Transaction{
    public String source;

    public Income(int id, String date, double amount,
                  String description, Categorys category,
                  String source) {

        super(id, date, amount, description, category);  // เรียกของแม่
        
        this.source = source;
    }
    
    @Override
    public double calculateNewBalance(double currentBalance) {
        currentBalance += this.amount;
        return super.calculateNewBalance(currentBalance);
    }

    public void Info(){

    }
}
