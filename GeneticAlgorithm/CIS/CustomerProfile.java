import java.util.ArrayList;

public class CustomerProfile {
	
	private ArrayList<Attribute> scoreAttributes; //cada customer profile tiene su lista de atributos

	public CustomerProfile(ArrayList<Attribute> scoreAttributes) {
		super();
		this.scoreAttributes = scoreAttributes;
	}

	public ArrayList<Attribute> getScoreAttributes() {
		return scoreAttributes;
	}

	public void setScoreAttributes(ArrayList<Attribute> scoreAttributes) {
		this.scoreAttributes = scoreAttributes;
	}
	
}

