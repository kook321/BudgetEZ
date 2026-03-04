

public class Transaction{
    private static int nextId = 1; // ตัวนับกลาง
    public int id;
    public String date;
    public double amount;
    public String description;
    public Categorys category;


    public Transaction( String date, 
                        double amount, 
                        String description, 
                        Categorys category){
        this.id = nextId++; // สร้าง id อัตโนมัติ
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.category = category;
    }

    public double calculateNewBalance(double currentBalance){
        return currentBalance = currentBalance + this.amount;
        
    }

    // public void Info(){
    //     System.out.println(id);
    //     System.out.println(date);
    //     System.out.println(amount);
    //     System.out.println(description);
    //     System.out.println(category);
    // }

}

