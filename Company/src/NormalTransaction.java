import java.time.LocalDateTime;
import java.io.*;
import java.util.*;
public class NormalTransaction extends Transaction implements Serializable {
    private static double NormalTransactionsProfit = 0.0;
    NormalTransaction(double es, String ec, String xc, int day, double conversionRate, double normCommission, double currParity, LocalDateTime local) {
        super(es, ec, xc, day, local);
        double x = roundAvoid(conversionRate, 4);
        this.TransactionProfit = roundAvoid(normCommission * es * x, 4); // profit obtained in exit currency
        this.ExitSum = roundAvoid(es * x - this.TransactionProfit, 4); // exit sum in exit currency
        if (xc.equals("dolars") == false) {
            this.TransactionProfit = roundAvoid(currParity * this.TransactionProfit, 4);
        }
        NormalTransactionsProfit += this.TransactionProfit;
    }
    double getNormalTransactionsProfit() {
        return NormalTransactionsProfit;
    }
    void setNormalTransactionsProfit(double val) {
        NormalTransactionsProfit = val;
    }
    public String toString() {
        return super.toString();
    }
}