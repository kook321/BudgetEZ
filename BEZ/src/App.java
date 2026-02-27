import java.time.Month;
//import java.time.Year;
//import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        Manager kook = new Manager();
        kook.setInfoBudget(Month.FEBRUARY, 2000, 30000);
        kook.getDailyAverageAvaiable(29);


        Transaction kook1 = new Transaction(1, "01-02-2000", 10000, "Kaprao", Categorys.Food);
        kook.addTransaction( kook1);
        kook.getDailyAverageAvaiable(29);

        kook1.Info();

        kook1.calculateNewBalance(kook.currentBalance);


    }

}
