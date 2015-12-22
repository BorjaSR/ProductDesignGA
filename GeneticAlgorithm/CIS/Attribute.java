import java.util.ArrayList;

public class Attribute {
	
	public String name;
	public int MIN;
	public int MAX;
	private ArrayList<Boolean> availableValues;
	private ArrayList<Integer> scoreValues;
	
	public Attribute(String name, int mIN, int mAX) {
		super();
		this.name = name;
		MIN = mIN;
		MAX = mAX;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMIN() {
		return MIN;
	}

	public void setMIN(int mIN) {
		MIN = mIN;
	}

	public int getMAX() {
		return MAX;
	}
	public void setMAX(int mAX) {
		MAX = mAX;
	}
<<<<<<< HEAD

=======
	
>>>>>>> version2_stephania16
	public ArrayList<Boolean> getAvailableValues() {
		return availableValues;
	}

<<<<<<< HEAD
	public void setAbailableValues(ArrayList<Boolean> values) {
=======
	public void setavailableValues(ArrayList<Boolean> values) {
>>>>>>> version2_stephania16
		this.availableValues = values;
	}

	public ArrayList<Integer> getScoreValues() {
		return scoreValues;
	}

	public void setScoreValues(ArrayList<Integer> scoreValues) {
		this.scoreValues = scoreValues;
	}

<<<<<<< HEAD
	
	
}
=======

}
>>>>>>> version2_stephania16
