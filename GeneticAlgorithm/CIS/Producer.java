import java.util.LinkedList;

public class Producer {
	
	public LinkedList<String> AvailableAttribute;
	public Product product;
	
	public Producer(LinkedList<String> availableAttribute, Product product) {
		super();
		AvailableAttribute = availableAttribute;
		this.product = product;
	}

	public LinkedList<String> getAvailableAttribute() {
		return AvailableAttribute;
	}

	public void setAvailableAttribute(LinkedList<String> availableAttribute) {
		AvailableAttribute = availableAttribute;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}
