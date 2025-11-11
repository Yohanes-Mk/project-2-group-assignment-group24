package warehouse;

public class WishlistItem {
    private final String productId;
    private int quantity;
    public WishlistItem(String productId, int quantity){
        this.productId = productId; this.quantity = quantity;
    }
    public String getProductId(){ return productId; }
    public int getQuantity(){ return quantity; }
    public void setQuantity(int q){ quantity = q; }
}
