import java.time.LocalDateTime;
import java.io.*;
import java.util.*;
public abstract class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected static int ClientCount = 0;
    protected int ClientID, ClientDay;
    protected LocalDateTime localTime;
    Client(String myName, int day, LocalDateTime local) {
        this.name = myName;
        ClientCount += 1;
        this.ClientID = ClientCount;
        this.ClientDay = day;
        this.localTime = local;
    }

    String getName() {
        return this.name;
    }
    void setName(String str) {
        this.name = str;
    }

    int getClientDay() {
        return this.ClientDay;
    }
    void setClientDay(int day) {
        this.ClientDay = day;
    }

    int getClientID() {
        return this.ClientID;
    }
    void setClientID(int id) {
        this.ClientID = id;
    }

    int getClientCount() {
        return ClientCount;
    }
    void setClientCount(int count) {
        ClientCount = count;
    }

    abstract void addTransaction(Transaction x, int a, double b, double c, double d, LocalDateTime l);

    @Override
    public String toString() {
        return "The Client " + this.name + " with the ID " + this.ClientID + " was added to the database on the day " + this.ClientDay + " at the local date and time " + this.localTime;
        // return new StringBuffer("The Client ").append(this.name).append(" with the ID " ).append(this.ClientID).append(" was added to the database on the day ").append(this.ClientDay).append(" at the local date and time ").append(this.localTime).toString();
    }

}