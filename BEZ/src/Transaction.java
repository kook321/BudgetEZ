

public class Transaction{
    public int id;
    public String date;
    public double amount;
    public String description;
    public Categorys category;


    public Transaction(int id, String date, double amount, String description, Categorys category){
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public double calculateNewBalance(double currentBalance){
        currentBalance = currentBalance + this.amount;
        System.out.println(currentBalance);
    }

    public void Info(){
        System.out.println(id);
        System.out.println(date);
        System.out.println(amount);
        System.out.println(description);
        System.out.println(category);
    }

}

