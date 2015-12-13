import java.util.ArrayList;
import java.util.LinkedList;

public class Producer {
	
	public ArrayList<Attribute> AvailableAttribute;
	public Product product;
	
	public Producer() {
		
	}

	public Producer(ArrayList<Attribute> availableAttribute, Product product) {
		super();
		AvailableAttribute = availableAttribute;
		this.product = product;
	}

	public ArrayList<Attribute> getAvailableAttribute() {
		return AvailableAttribute;
	}

	public void setAvailableAttribute(ArrayList<Attribute> availableAttribute) {
		AvailableAttribute = availableAttribute;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	
}
