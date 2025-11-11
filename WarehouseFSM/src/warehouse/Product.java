package warehouse;

import java.util.*;

public class Product {
    private final String id;
    private final String name;
    private int stock;
    private final double price;

    // Full version: First In, First Out waitlist of (clientId, qty)
    private final LinkedList<AbstractMap.SimpleEntry<String,Integer>> waitlist = new LinkedList<>();

    public Product(String id, String name, int stock, double price){
        this.id = id; this.name = name; this.stock = stock; this.price = price;
    }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public int getStock(){ return stock; }
    public void setStock(int s){ stock = s; }
    public double getPrice(){ return price; }

    public List<AbstractMap.SimpleEntry<String,Integer>> getWaitlist(){ return waitlist; }
    public void enqueueWait(String clientId, int qty){ waitlist.add(new AbstractMap.SimpleEntry<>(clientId, qty)); }

    @Override
    public String toString(){
        return id + " | " + name + " | stock=" + stock + " | $" + String.format("%.2f", price);
    }
}
