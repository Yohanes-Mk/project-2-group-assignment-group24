package warehouse;

public class Transaction {
    private static int next = 1;

    private final String id;         // T1, T2, ... (invoices)
    private final String clientId;
    private final String productId;
    private final int quantity;
    private final double unitPrice;
    private final double total;

    public Transaction(String clientId, String productId, int quantity, double unitPrice){
        this.id = "T" + (next++);
        this.clientId = clientId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.total = unitPrice * quantity;
    }

    public String getId(){ return id; }
    public String getClientId(){ return clientId; }
    public String getProductId(){ return productId; }
    public int getQuantity(){ return quantity; }
    public double getUnitPrice(){ return unitPrice; }
    public double getTotal(){ return total; }

    @Override
    public String toString(){
        return id + " | " + clientId + " | " + productId + " | qty=" + quantity +
               " | $" + String.format("%.2f", unitPrice) + " | total=$" + String.format("%.2f", total);
    }
}
