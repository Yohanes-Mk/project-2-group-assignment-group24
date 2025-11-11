package warehouse;

import java.util.*;

public class Client {
    private final String id;
    private String name;
    private String address;

    // Wishlist keyed by productId
    private final Map<String, WishlistItem> wishlist = new LinkedHashMap<>();

    // Full version: balance & transactions
    private double balance = 0.0;
    private final List<Transaction> transactions = new ArrayList<>();

    public Client(String id, String name, String address) {
        this.id = id; this.name = name; this.address = address;
    }

    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getAddress(){ return address; }

    // Wishlist operations
    public void addOrUpdateWishlistItem(String productId, int qty){
        if(qty <= 0) { wishlist.remove(productId); return; }
        WishlistItem item = wishlist.get(productId);
        if(item == null) wishlist.put(productId, new WishlistItem(productId, qty));
        else item.setQuantity(qty);
    }
    public Collection<WishlistItem> getWishlist(){
        return Collections.unmodifiableCollection(wishlist.values());
    }
    public void clearZeroQtyWishlist() {
        wishlist.values().removeIf(wi -> wi.getQuantity() <= 0);
    }

    // Full version accounting
    public void addCharge(double amount) { balance += amount; }
    public void applyPayment(double amount) { balance -= amount; }
    public double getBalance() { return balance; }

    public void addTransaction(Transaction t) {
        transactions.add(t);
        addCharge(t.getTotal());
    }
    public List<Transaction> getTransactions(){
        return Collections.unmodifiableList(transactions);
    }

    @Override
    public String toString(){
        return id + " | " + name + " | " + address + " | Balance:$" + String.format("%.2f", balance);
    }
}
