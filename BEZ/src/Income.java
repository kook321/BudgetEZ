public class Income extends Transaction{
    public String source;

    public Income( String date, double amount,
                  String description, Categorys category,
                  String source) {

        super( date, amount, description, category);  // เรียกของแม่
        
        this.source = source;
    }
    
  

    public void Info(){
        System.out.println(id);
        System.out.println(date);
        System.out.println(amount);
        System.out.println(description);
        System.out.println(category);
        System.out.println(source);
    }
}
