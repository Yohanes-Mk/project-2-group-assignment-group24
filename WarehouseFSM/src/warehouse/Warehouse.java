package warehouse;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Warehouse {
    private final ClientList clients = new ClientList();
    private final ProductCatalog products = new ProductCatalog();

    // IDs automatically go in order
    private final AtomicInteger clientSeq = new AtomicInteger(1);
    private final AtomicInteger productSeq = new AtomicInteger(1);

    public String addClient(String name, String address){
        String id = "C" + clientSeq.getAndIncrement();
        Client c = new Client(id, name, address);
        clients.addClient(c);
        return id;
    }

    public String addProduct(String name, int stock, double price){
        String id = "P" + productSeq.getAndIncrement();
        Product p = new Product(id, name, stock, price);
        products.addProduct(p);
        return id;
    }

    public void addOrUpdateWishlistItem(String clientId, String productId, int qty){
        Client c = clients.findClient(clientId);
        if(c == null) throw new IllegalArgumentException("Client not found: " + clientId);
        Product p = products.findProduct(productId);
        if(p == null) throw new IllegalArgumentException("Product not found: " + productId);
        c.addOrUpdateWishlistItem(productId, qty);
    }

    public Collection<Client> getAllClients(){ return clients.getAllClients(); }
    public Collection<Product> getAllProducts(){ return products.getAllProducts(); }

    public List<String> getWishlistForClient(String clientId){
        Client c = clients.findClient(clientId);
        if(c == null) throw new IllegalArgumentException("Client not found: " + clientId);
        List<String> out = new ArrayList<>();
        for(WishlistItem wi : c.getWishlist()){
            Product p = products.findProduct(wi.getProductId());
            String pname = (p == null) ? "(unknown)" : p.getName();
            out.add(wi.getProductId() + " | " + pname + " | qty=" + wi.getQuantity());
        }
        return out;
    }

    // Full version additions are below

    // Buy everything on wishlist; fill from stock; shortfalls go to waitlist
    public void placeOrder(String clientId){
        Client c = clients.findClient(clientId);
        if(c == null) throw new IllegalArgumentException("Client not found: " + clientId);

        List<WishlistItem> items = new ArrayList<>(c.getWishlist());

        for (WishlistItem wi : items){
            Product p = products.findProduct(wi.getProductId());
            if(p == null) continue;

            int want = wi.getQuantity();
            if (want <= 0) continue;

            int canFill = Math.min(want, p.getStock());
            int shortfall = want - canFill;

            if (canFill > 0){
                p.setStock(p.getStock() - canFill);
                Transaction t = new Transaction(clientId, p.getId(), canFill, p.getPrice());
                c.addTransaction(t);
            }

            if (shortfall > 0){
                p.enqueueWait(clientId, shortfall);
            }

            wi.setQuantity(0);
        }
        c.clearZeroQtyWishlist();
    }

    // Payment reduces balance
    public void recordPayment(String clientId, double amount){
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        Client c = clients.findClient(clientId);
        if(c == null) throw new IllegalArgumentException("Client not found: " + clientId);
        c.applyPayment(amount);
    }

    // Shipment: fill waitlist first, auto-create transactions; leftover goes to stock
    public void receiveShipment(String productId, int quantity){
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        Product p = products.findProduct(productId);
        if(p == null) throw new IllegalArgumentException("Product not found: " + productId);

        int arriving = quantity;

        var it = p.getWaitlist().iterator();
        while (it.hasNext() && arriving > 0){
            var entry = it.next();
            String cid = entry.getKey();
            int need = entry.getValue();

            int fill = Math.min(need, arriving);
            if (fill > 0){
                Client c = clients.findClient(cid);
                if (c != null){
                    Transaction t = new Transaction(cid, p.getId(), fill, p.getPrice());
                    c.addTransaction(t);
                }
                arriving -= fill;
                need -= fill;
            }
            if (need <= 0) it.remove(); else entry.setValue(need);
        }

        if (arriving > 0) p.setStock(p.getStock() + arriving);
    }

    public List<String> getWaitlistView(String productId){
        Product p = products.findProduct(productId);
        if(p == null) throw new IllegalArgumentException("Product not found: " + productId);
        List<String> out = new ArrayList<>();
        for (var e : p.getWaitlist()){
            out.add(e.getKey() + " x" + e.getValue());
        }
        return out;
    }

    public List<Transaction> getTransactionsForClient(String clientId){
        Client c = clients.findClient(clientId);
        if(c == null) throw new IllegalArgumentException("Client not found: " + clientId);
        return c.getTransactions();
    }
}
