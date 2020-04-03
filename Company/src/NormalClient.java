import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.*;
public class NormalClient extends Client implements Serializable {
    private ArrayList < NormalTransaction > MyNormalTransactions;
    public NormalClient(String myName, int day, LocalDateTime local) {
        super(myName, day, local);
        MyNormalTransactions = new ArrayList < NormalTransaction > ();
    }
    ArrayList < NormalTransaction > getMyNormalTransactions() {
        return MyNormalTransactions;
    }
    @Override
    void addTransaction(Transaction x, int a, double b, double c, double d, LocalDateTime l) {
        NormalTransaction y = new NormalTransaction(x.EntrySum, x.EntryCurrency, x.ExitCurrency, a, b, c, d, l);
        this.MyNormalTransactions.add(y);
    }

}