import java.util.ArrayList;
<<<<<<< HEAD
import java.util.LinkedList;
=======
>>>>>>> version2_stephania16

public class Producer {
	
	public ArrayList<Attribute> AvailableAttribute;
	public Product product;
	public ArrayList<Integer> ValuesPopuProducer;
	
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
	
	public ArrayList<Integer> getValuesPopuProducer()
	{
		return ValuesPopuProducer;
	}
	
	public void setValuesPopuProducer(ArrayList<Integer> valuesPopu) {
		this.ValuesPopuProducer = valuesPopu;
	}
	
}
