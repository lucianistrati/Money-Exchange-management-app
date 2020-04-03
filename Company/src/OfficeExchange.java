import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class OfficeExchange { //This class is implemented as a SingleTon Class
    private static OfficeExchange single_instance = new OfficeExchange(); // Used for declaring singleton class
    private int CurrentDay = 0;
    private double PremiumTax = 100.0; // 100 dolars
    private double PremiumCommission = 0.005; // 0.5% commission for premium clients
    private double NormalCommission = 0.01; //1% commission for normal clients
    private double TotalProfit = 0.0;
    private HashMap < String, Double > CurrenciesAmounts;
    private HashMap < String, Double > CurrenciesParities;
    private ArrayList < HashMap < String, Double >> CurrenciesParitiesHistory;
    private Set < String > AllCurrencies;
    private ArrayList < Client > AllClients;
    private ArrayList < Transaction > AllTransactions;
    private enum TransactionType {
        Normal,
        Premium
    }
    private OfficeExchange() {
        this.AllClients = new ArrayList < Client > ();
        this.AllTransactions = new ArrayList < Transaction > ();
        this.CurrenciesAmounts = new HashMap < String, Double > ();
        this.CurrenciesParities = new HashMap < String, Double > ();
        this.CurrenciesParitiesHistory = new ArrayList < HashMap < String, Double >> ();
        this.AllCurrencies = new HashSet < String > ();

    }
    public static OfficeExchange getInstance() {
        return single_instance;
    }

    public static double roundAvoid(double value, int places) {
  /*This static method is truncating every double value that is passed to it
  to the first #places decimals, in this case, we will want to truncate to the first 4 decimals because that is the practice
  in the financial sector.
  */
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    void addToTotalProfit(double sum) {
        this.TotalProfit += sum;
    }

    double getTotalProfit() {
        return this.TotalProfit;
    }
    void setTotalProfit(double val) {
        this.TotalProfit = val;
    }

    int getCurrentDay() {
        return this.CurrentDay;
    }
    void setCurrentDay(int day) {
        this.CurrentDay = day;
    }

    double getPremiumTax() {
        return PremiumTax;
    }
    void setPremiumTax(double val) {
        this.PremiumTax = val;
    }

    double getPremiumCommission() {
        return this.PremiumCommission;
    }
    void setPremiumCommission(double val) {
        this.PremiumCommission = val;
    }

    double getNormalCommission() {
        return this.NormalCommission;
    }
    void setNormalCommission(double val) {
        this.NormalCommission = val;
    }

    void addPremiumClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the premium client");
        String myName = sc.nextLine();
        LocalDateTime myObj = LocalDateTime.now();
        PremiumClient c = new PremiumClient(myName, this.CurrentDay, myObj);
        this.addToTotalProfit(this.PremiumTax);
        this.AllClients.add(c);
        System.out.println(this.AllClients.get(AllClients.size() - 1));
    }
    void addNormalClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the normal client");
        String myName = sc.nextLine();
        LocalDateTime myObj = LocalDateTime.now();
        NormalClient d = new NormalClient(myName, this.CurrentDay, myObj);
        this.AllClients.add(d);
        System.out.println(this.AllClients.get(AllClients.size() - 1));
    }

    void proceedWithPremiumTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the premium client");
        String myName = sc.nextLine();
        System.out.println("Enter the initial currency");
        String myInitialCurrency = sc.nextLine();
        System.out.println("Enter the final currency");
        String myFinalCurrency = sc.nextLine();
        try {
            if (AllCurrencies.contains(myInitialCurrency) == false || AllCurrencies.contains(myFinalCurrency) == false) {
                throw new MyInputException("One of your selected currencies does not exist in our DataBase");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("Enter the sum of money");
        double mySumOfMoney = Double.parseDouble(sc.nextLine());
        boolean foundClient = false;
        int positionFound = -1;
        for (int i = 0; i < this.AllClients.size(); i++) {
            if (this.AllClients.get(i).getName().equals(myName)) {
                foundClient = true;
                positionFound = i;
                break;
            }
        }
        try {
            if (foundClient == false) {
                throw new MyInputException("The normal client does not exist try to create it");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        double x, conv = this.obtainConversionRate(myInitialCurrency, myFinalCurrency);
        LocalDateTime myObj = LocalDateTime.now();
        if (this.CurrenciesAmounts.get(myFinalCurrency) > conv * mySumOfMoney) {
            NormalTransaction t = new NormalTransaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), conv, this.PremiumCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj);
            this.TotalProfit += t.getTransactionProfit();
            Transaction u = new Transaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), myObj);
            this.AllClients.get(positionFound).addTransaction(u, this.getCurrentDay(), conv, this.PremiumCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj);
            this.AllTransactions.add(t);
            x = t.getExitSum();
            System.out.println("Your money are " + x + " " + myFinalCurrency);
            this.setCurrencyAmount(myInitialCurrency, this.CurrenciesAmounts.get(myInitialCurrency) + mySumOfMoney); //change the volume on entry currency
            this.setCurrencyAmount(myFinalCurrency, this.CurrenciesAmounts.get(myFinalCurrency) - t.getExitSum()); //change the volume on exit currency
        } else {
            System.out.println("Insufficient currency in the HOUSE for your transaction, sorry");
        }

    }


    void proceedWithNormalTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the normal client");
        String myName = sc.nextLine();
        System.out.println("Enter the initial currency");
        String myInitialCurrency = sc.nextLine();
        System.out.println("Enter the final currency");
        String myFinalCurrency = sc.nextLine();
        try {
            if (AllCurrencies.contains(myInitialCurrency) == false || AllCurrencies.contains(myFinalCurrency) == false) {
                throw new MyInputException("One of your selected currencies does not exist in our DataBase");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("Enter the sum of money");
        double mySumOfMoney = Double.parseDouble(sc.nextLine());
        boolean foundClient = false;
        int positionFound = -1;
        for (int i = 0; i < this.AllClients.size(); i++) {
            if (AllClients.get(i).getName().equals(myName)) {
                foundClient = true;
                positionFound = i;
                break;
            }
        }
        try {
            if (foundClient == false) {
                throw new MyInputException("The normal client does not exist try to create it");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        double x, conv = this.obtainConversionRate(myInitialCurrency, myFinalCurrency);
        LocalDateTime myObj = LocalDateTime.now();
        if (this.CurrenciesAmounts.get(myFinalCurrency) > conv * mySumOfMoney) {
            NormalTransaction t = new NormalTransaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), conv, this.NormalCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj);
            this.TotalProfit += t.getTransactionProfit();
            Transaction u = new Transaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), myObj);
            this.AllClients.get(positionFound).addTransaction(u, this.getCurrentDay(), conv, this.NormalCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj);
            this.AllTransactions.add(t);

            x = t.getExitSum();
            System.out.println("Your money are " + x + " " + myFinalCurrency);
            this.setCurrencyAmount(myInitialCurrency, this.CurrenciesAmounts.get(myInitialCurrency) + mySumOfMoney); //change the volume on entry currency
            this.setCurrencyAmount(myFinalCurrency, this.CurrenciesAmounts.get(myFinalCurrency) - t.getExitSum()); //change the volume on exit currency

        } else {
            System.out.println("Insufficient currency in the HOUSE for your transaction, sorry");
        }

    }
    void supplementCurrency() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the currency?");
        String entry = sc.nextLine();
        try {
            if (AllCurrencies.contains(entry) == false) {
                throw new MyInputException("The currency is not registered in our Database");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("What is the amount you would like to deposit?");
        double x = Double.parseDouble(sc.nextLine()), u = this.CurrenciesAmounts.get(entry);
        try {
            if (x <= 0.0) {
                throw new MyInputException("The amount of money to deposit can not be negative or zero");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.CurrenciesAmounts.put(entry, u + x);

    }

    double getLastTransactionProfit() {
        return this.AllTransactions.get(AllTransactions.size() - 1).TransactionProfit;
    }
    void incrementCurrentDay() {
        this.CurrentDay += 1;
        CurrenciesParitiesHistory.add(CurrenciesParities);
        this.actualizeCurrencies();
        System.out.println("One day passed. The new exchange is:");
        for (String s: this.CurrenciesParities.keySet()) {
            System.out.println(s + ":" + this.CurrenciesParities.get(s));
        }
    }
    void addCurrency(String s) {
        this.AllCurrencies.add(s);
    }

    double obtainConversionRate(String entry, String exit) {
        double first, second;
        String a, b;
        if (entry.equals("dolars") || exit.equals("dolars")) {
            return this.CurrenciesParities.get(entry + "-->" + exit);
        } else {
            a = new String(entry + "-->dolars");
            b = new String("dolars-->" + exit);
        }
        first = this.CurrenciesParities.get(a);
        second = this.CurrenciesParities.get(b);
        return roundAvoid(first * second, 4);
    }

    void modifyPremiumTax() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new tax for premium clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x <= 0.0) {
                throw new MyInputException("The tax for premimum clients cannot be zero or less than zero");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumTax(x);
        System.out.println("The new tax for premium clients is " + x + "$");
    }
    void modifyNormalCommission() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new commission for premium clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x < 0.0 || x >= 1.0) {
                throw new MyInputException("The normal commision has to be between 0.01 and 1.00");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumCommission(x);
        System.out.println("The new commission for normal clients is " + x * 100 + "%");
    }
    void modifyPremiumCommission() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new commission for normal clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x < 0.0 || x >= 1.0) {
                throw new MyInputException("The premium commision has to be between 0.01 and 1.00");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumCommission(x);
        System.out.println("The new commission for premium clients is " + x * 100 + "%");
    }
    void getTotalProfitPerDay() {
        double total = 0;
        System.out.println("What is the day for which you want to know the profit?");
        Scanner sc = new Scanner(System.in);
        int day = sc.nextInt();
        for (int i = 0; i < AllTransactions.size(); i++) {
            if (AllTransactions.get(i).getTransactionDay() == day) {
                total += AllTransactions.get(i).getTransactionProfit();
            }
        }
        System.out.println("The total profit in the day  " + day + " is " + roundAvoid(total, 4));
    }
    void getTransactionHistoryOfAClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the name of the client?");
        String myName = sc.nextLine();
        for (int i = 0; i < AllClients.size(); i++) {
            if (AllClients.get(i).name.equals((myName))) {
                if (AllClients.get(i) instanceof NormalClient) {
                    NormalClient A = ((NormalClient) AllClients.get(i));
                    System.out.println(A);
                    System.out.println(myName + " transactions are: ");
                    ArrayList < NormalTransaction > B = A.getMyNormalTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        System.out.println(B.get(i));
                    }
                } else {
                    PremiumClient A = ((PremiumClient) AllClients.get(i));
                    System.out.println(A);
                    System.out.println(myName + " transactions are: ");
                    ArrayList < PremiumTransaction > B = A.getMyPremiumTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        System.out.println(B.get(i));
                    }
                }
    /*
    for(int j=0;j<AllClients.get(i). ;j++){

    }*/
                break;
            }
        }
    }
    void getAllCurrencies() {
        System.out.println("All currencies are: ");
        for (String key: this.AllCurrencies) {
            System.out.println(key + " with the quantity: " + this.CurrenciesAmounts.get(key));
        }
    }
    void getLastTransactionInformations() {
        System.out.println(this.AllTransactions.get(AllTransactions.size() - 1));
    }
    void getInformationsAboutAllTransactionsInACertainDay() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the day for which you want to know the informations?");
        int day = sc.nextInt();
        for (int i = 0; i < AllTransactions.size(); i++) {
            if (AllTransactions.get(i).getTransactionDay() == day) {
                System.out.println(AllTransactions.get(i));
            }
        }
    }
    double getCurrenciesParities(String xc, String s) {
        if (xc.equals("dolars"))
            return CurrenciesParities.get(s + "-->dolars");
        return CurrenciesParities.get(xc + "-->dolars");
    }

    double getCurrencyAmount(String currency) {
        return this.CurrenciesAmounts.get(currency);
    }
    void setCurrencyAmount(String currency, Double amount) {
        CurrenciesAmounts.put(currency, amount);
    }

    void setCurrencyPair(String firstCurrency, String secondCurrency, Double exchangeCoefficient) {
        String oneWay = firstCurrency + "-->" + secondCurrency;
        this.CurrenciesParities.put(oneWay, roundAvoid(exchangeCoefficient, 4));
        String reverseWay = secondCurrency + "-->" + firstCurrency;
        this.CurrenciesParities.put(reverseWay, roundAvoid(1 / exchangeCoefficient, 4));
    }

    static double generateRandomModificationCoefficient(double number) {
        double min = number - number * 0.05, max = number + number * 0.05;
        Random r = new Random();
        double randomValue;
        do {
            randomValue = min + (max - min) * r.nextDouble();
        } while (randomValue <= 0);
        return randomValue;
    }

    void actualizeCurrencies() {
  /*
  This function generates for a currency pair a fluctuation of at most 5%(either on plus or on minus) in order to make
  the currencies flow. This happens after each day and a day passes after 3 made operations, any operations are considered
  to have the same length of time, from adding a new client to a new transaction for a client we already have.
   */
        Set < String > keys = CurrenciesParities.keySet();
        String prefix = "dolars-->";
        for (String key: keys) {
            if (key.startsWith(prefix) == true) {
                double newCoefficient = generateRandomModificationCoefficient(CurrenciesParities.get(key));
                CurrenciesParities.put(key, roundAvoid(newCoefficient, 4));
                int firstDelimitation = key.indexOf("-");
                int lastDelimitation = key.indexOf(">");
                String newKey = key.substring(lastDelimitation + 1) + "-->" + key.substring(0, firstDelimitation);
                CurrenciesParities.put(newKey, roundAvoid(1 / newCoefficient, 4));
            }
        }
    }
    /*
    void drawHistoryParity(){
        String entry,exit;
        Scanner sc=new Scanner(System.in);
        System.out.println("What is your first currency?");
        entry=sc.nextLine();
        System.out.println("What is your second currency?");
        exit=sc.nextLine();
    }
    */
    void saveAllDataToFile() {
        try {
            FileOutputStream f = new FileOutputStream("HouseHistory.txt");
            ObjectOutputStream o = new ObjectOutputStream(f);
            for (int i = 0; i < AllClients.size(); i++) {
                if (AllClients.get(i) instanceof NormalClient) {
                    NormalClient A = ((NormalClient) AllClients.get(i));
                    o.writeObject(A);
                    //o.writeObject(AllClients.get(i).getName() + " transactions are: ");
                    ArrayList < NormalTransaction > B = A.getMyNormalTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        o.writeObject(B.get(i));
                    }
                } else {
                    PremiumClient A = ((PremiumClient) AllClients.get(i));
                    o.writeObject(A);
                    //o.writeObject(AllClients.get(i).getName() + " transactions are: ");
                    ArrayList < PremiumTransaction > B = A.getMyPremiumTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        o.writeObject(B.get(i));
                    }
                }
            }
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } finally {
            System.out.println("All the details were written successfully in the HouseHistory.txt file");
        }
    }
}