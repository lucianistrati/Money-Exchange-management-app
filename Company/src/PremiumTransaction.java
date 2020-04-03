import java.time.LocalDateTime;
import java.io.*;
import java.util.*;
public class PremiumTransaction extends Transaction implements Serializable {
    private static double PremiumTransactionsProfit = 0.0;
    PremiumTransaction(double es, String ec, String xc, int day, double conversionRate, double premCommission, double currParity, LocalDateTime local) {
        super(es, ec, xc, day, local);
        double x = roundAvoid(conversionRate, 4);
        this.TransactionProfit = roundAvoid(premCommission * es * x, 4); //profit obtained in exit currency
        this.ExitSum = roundAvoid(es * x - this.TransactionProfit, 4); //exit sum in exit currency
        if (xc != "dolars") {
            this.TransactionProfit = roundAvoid(currParity * this.TransactionProfit, 4);
        }
        PremiumTransactionsProfit += this.TransactionProfit;
    }
    double getPremiumTransactionsProfit() {
        return PremiumTransactionsProfit;
    }
    void setPremiumTransactionsProfit(double val) {
        PremiumTransactionsProfit = val;
    }
    public String toString() {
        return super.toString();
    }
}