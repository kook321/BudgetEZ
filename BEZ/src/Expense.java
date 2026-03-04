public class Expense extends Transaction{
    public boolean isNecessary;

     public Expense( String date, 
                    double amount,
                    String description, 
                    Categorys category,
                    boolean isNecessary) {
                
                        super(date, 
                        -Math.abs(amount), 
                        description, 
                        category);  // เรียกของแม่
        this.isNecessary = isNecessary;
    }

    public void Info(){
        System.out.println(id);
        System.out.println(date);
        System.out.println(amount);
        System.out.println(description);
        System.out.println(category);
        System.out.println(isNecessary);
    }
  
}
