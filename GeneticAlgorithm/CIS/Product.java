import java.util.HashMap;

public class Product {
	
	public HashMap<Attribute, Integer> Product;

	public Product(HashMap<Attribute, Integer> product) {
		super();
		Product = product;
	}

	public HashMap<Attribute, Integer> getProduct() {
		return Product;
	}

	public void setProduct(HashMap<Attribute, Integer> product) {
		Product = product;
	}
	
	
	
}
