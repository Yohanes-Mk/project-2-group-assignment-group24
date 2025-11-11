package warehouse;

import java.util.*;

public class ProductCatalog {
    private final Map<String, Product> products = new LinkedHashMap<>();
    public void addProduct(Product p){ products.put(p.getId(), p); }
    public Product findProduct(String id){ return products.get(id); }
    public Collection<Product> getAllProducts(){ return Collections.unmodifiableCollection(products.values()); }
}
