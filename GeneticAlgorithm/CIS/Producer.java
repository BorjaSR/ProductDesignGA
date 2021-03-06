import java.util.ArrayList;

public class Producer {
	
	public ArrayList<Attribute> AvailableAttribute; //atributos disponibles, que puedo modificar
	public Product product;	//cada productor tiene un producto

	public Producer() {
		super();
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