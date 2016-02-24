import java.util.ArrayList;
import java.util.HashMap;

public class Product implements Cloneable{
	
	public HashMap<Attribute, Integer> attributeValue; //cada producto tiene sus atributos y su valor
	public int fitness; //valor de fitness de cada producto
	
	public Product() {
		super();
	}

	public Product(HashMap<Attribute, Integer> product, int fit) {
		super();
		attributeValue = product;
		fitness = fit;
	}

	public HashMap<Attribute, Integer> getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(HashMap<Attribute, Integer> product) {
		attributeValue = product;
	}
	
	public int getFitnessProduct()
	{
		return fitness;
	}
	
	public void setFitnessProduct(int fit) {
		this.fitness = fit;
	}
	
	/**Creates a deep copy of Product*/
	public Product clone(){
		Product product = new Product(this.attributeValue, this.fitness);
		return product;
	}
}



