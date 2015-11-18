import java.util.HashMap;
import java.util.LinkedList;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

public class Main {
	static final int KNOWN_ATTRIBUTES = 100; /*
												 * % of attributes known for all
												 * producers
												 */
	static final int SPECIAL_ATTRIBUTES = 33; /*
												 * % of special attributes known
												 * for some producers
												 */
	static final int MUT_PROB_CUSTOMER_PROFILE = 33; /*
														 * % of mutated
														 * attributes in a
														 * customer profile
														 */
	static final int CROSSOVER_PROB = 80; /* % of crossover */
	static final int MUTATION_PROB = 1; /* % of mutation */
	static final int NUM_GENERATIONS = 100; /* number of generations */
	static final int NUM_POPULATION = 20; /* number of population */
	static final int RESP_PER_GROUP = 20; /*
											 * We divide the respondents of each
											 * profile in groups of
											 * RESP_PER_GROUP respondents
											 */
	static final int NUM_EXECUTIONS = 20; /* number of executions */

	// static final String SOURCE = "D:\Pablo\EncuestasCIS.xlsx";
	static final int SHEET_AGE_STUDIES = 1;
	static final int SHEET_POLITICAL_PARTIES = 2;
	static final String EOF = "EOF";

	private HashMap<Attribute, Integer> TotalAttribute;
	public static ArrayList<Attribute> AttributesTotal = new ArrayList<>();
	private LinkedList<Producer> Producers;
<<<<<<< .merge_file_a04280

	/* INPUT VARIABLES */
	private int Number_Attributes; /* Number of attributes */
	private int Number_Producers; /* Number of producers */
	private int Number_CustomerProfile; /* Number of customer profiles */

	/* GA VARIABLES */
	private int BestWSC; /* Stores the best wsc found */
	// private HashMap<,Integer> Population; Private mPopu As List(Of List(Of
	// Integer))
	private HashMap<String, Integer> Fitness; /*
												 * mFitness(i) = wsc of mPopu(i)
												 */

	/* STATISTICAL VARIABLES */
	private LinkedList<Integer> Results;
	private LinkedList<Integer> Initial_Results;

	/*
	 * ' Represents the list of attributes, its possible values, and its
	 * possible valuations: ' mAttributes(i)(j) = valuation for attribute number
	 * i, value number j Private mAttributes As List(Of Integer) Private
	 * mProducers As List(Of ClsProducer) ' Represents the customer profiles: '
	 * mCustProf(i)(j)(k) = valuation for the customer type number i, '
	 * attribute number j, value k of attribute (each attribute can take k
	 * possible values) Private mCustProf As List(Of List(Of List(Of Decimal)))
	 * Private mCustProfAux As List(Of List(Of List(Of Integer))) Private
	 * mCustProfNum As List(Of Integer) ' Number of customers of each customer
	 * profile
	 */
=======
	
	/*INPUT VARIABLES*/
	private int Number_Attributes; /*Number of attributes*/
	private int Number_Producers; /*Number of producers*/
	private int Number_CustomerProfile; /*Number of customer profiles*/
	
	
	/*GA VARIABLES*/
	private int BestWSC; /*Stores the best wsc found*/
    private HashMap<Producer,Integer> Population;   //Private mPopu As List(Of List(Of Integer))
    private HashMap<String,Integer> Fitness; /*mFitness(i) = wsc of mPopu(i)*/
    
	/* STATISTICAL VARIABLES*/
	private LinkedList<Integer> Results;
    private LinkedList<Integer> Initial_Results;
    
    private LinkedList<Attribute> AttributesList;
    private LinkedList<Producer> ProducerList;
    private LinkedList<CustomerProfile> CustomerProfileList;
    private LinkedList<CustomerProfile> CustomerProfileListAux;
    private LinkedList<Integer> NumberCustomerProfile;
/*
    ' Represents the list of attributes, its possible values, and its possible valuations:
    ' mAttributes(i)(j) = valuation for attribute number i, value number j
    Private mAttributes As List(Of Integer)
    Private mProducers As List(Of ClsProducer)
    ' Represents the customer profiles:
    ' mCustProf(i)(j)(k) = valuation for the customer type number i, 
    ' attribute number j, value k of attribute (each attribute can take k possible values)
    Private mCustProf As List(Of List(Of List(Of Decimal)))
    Private mCustProfAux As List(Of List(Of List(Of Integer)))
    Private mCustProfNum As List(Of Integer) ' Number of customers of each customer profile
*/
      
>>>>>>> .merge_file_a01084

	public static void main(String[] args) throws IOException {
		// An excel file name. You can create a file name with a full path
		// information.
		String filename = "EncuestasCIS.xlsx";
		// Create an ArrayList to store the data read from excel sheet.
		List sheetData = new ArrayList();
		FileInputStream fis = null;
		try {
			// Create a FileInputStream that will be use to read the excel file.
			fis = new FileInputStream(filename);

			// Create an excel workbook from the file system.
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			// Get the first sheet on the workbook.
			XSSFSheet sheet = workbook.getSheetAt(0);

			/*
			 * When we have a sheet object in hand we can iterator on each
			 * sheet's rows and on each row's cells. We store the data read on
			 * an ArrayList so that we can printed the content of the excel to
			 * the console.
			 */
			Iterator rows = sheet.rowIterator();
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				Iterator cells = row.cellIterator();
				List data = new ArrayList();
				while (cells.hasNext()) {
					XSSFCell cell = (XSSFCell) cells.next();
					// System.out.println("Añadiendo Celda: " +
					// cell.toString());
					data.add(cell);
				}
				sheetData.add(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	
		generateAttributeValor(sheetData);
		showAttributes();
		//showExcelData(sheetData);
	}

	// Creating the attributes and the possible values of them
	private static void generateAttributeValor(List sheetData) {

		int MIN_VAL = 1;
		
		double number_valors = 0.0;
		for (int i = 4; i < sheetData.size(); i++) {
			// System.out.println("Celda [" + i + ", 0]: ");

			if (number_valors == 0) {
				Cell cell = (Cell) ((List) sheetData.get(i)).get(0);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					number_valors = cell.getNumericCellValue() + 1;
					AttributesTotal.add(new Attribute("Attribute " + (AttributesTotal.size()+1), MIN_VAL, (int)number_valors-1));
				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					if(cell.getRichStringCellValue().equals("MMM"))
						break;
				}
			}
			number_valors--;
		}
	}
	
	private static void showAttributes(){
		for(int k = 0; k < AttributesTotal.size(); k++){
			System.out.println(AttributesTotal.get(k).getName());
			System.out.println(AttributesTotal.get(k).getMIN());
			System.out.println(AttributesTotal.get(k).getMAX());
		}
	}

	private static void showExcelData(List sheetData) {
		// Iterates the data and print it out to the console.
		for (int i = 0; i < sheetData.size(); i++) {
			List list = (List) sheetData.get(i);
			for (int j = 0; j < list.size(); j++) {
				Cell cell = (Cell) list.get(j);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					System.out.print(cell.getNumericCellValue());
				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					System.out.print(cell.getRichStringCellValue());
				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					System.out.print(cell.getBooleanCellValue());
				}
				if (j < list.size() - 1) {
					System.out.print(", ");
				}
			}
			System.out.println("");
		}
	}
	
	/**Creating available attributes for the producer*/
	private HashMap<CustomerProfile, Boolean> createAvailable()
	{
		HashMap<CustomerProfile, Boolean> availableAttr = new HashMap<CustomerProfile, Boolean>();
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		
		/*All producers know the first ATTRIBUTES_KNOWN % of the attributes*/
		for(int i = 0; i < limit; i++)
		{
			availableAttr.get(new LinkedList<Boolean>());
			for(int j = 0; j < AttributesList.get(i) - 1; j++)
			{
				availableAttr.get(i).add(true);
			}
		}
		
		/*The remaining attributes are only known by SPECIAL_ATTRIBUTES % producers*/
		for(int i = limit; i < Number_Attributes; i++)
		{
			availableAttr.add(new LinkedList<Boolean>());
			availableAttr.get(i).add(true);
			double rndVal = Math.random();
			for(int j = 1; j < AttributesList.get(i) - 1; j++)
			{
				if(rndVal < (SPECIAL_ATTRIBUTES /100) && Math.random() < 0.5)
				{
					/* Furthermore, with a 50% of probabilities it can know this attribute*/
                    availableAttr.get(i).add(true);					
				}
				else 
				{
					availableAttr.get(i).add(false);					
				}
			}
		}
		return availableAttr;
		
	}

	
	/**Creating a random product*/
	private LinkedList<Integer> createRndProduct(HashMap<CustomerProfile, Boolean> availableAttr)
	{
		LinkedList<Integer> product = new LinkedList<Integer>();
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		int attrVal;
		for(int i = 0; i < limit; i++)
		{
			attrVal = (int)(Math.floor(AttributesList.get(i) * Math.random()));
			product.add(attrVal);
		}
		
		for(int i = limit; i < Number_Attributes; i++)
		{
			boolean attrFound = false;
			while(!attrFound)
			{
				attrVal = (int)(Math.floor(AttributesList.get(i) * Math.random()));
				if(availableAttr.get(i)(attrVal)) attrFound = true;
			}
			product.add(attrVal);
		}
		return product;
	}
	
	
	/**Creating a product near various customer profiles*/
	private LinkedList<Integer> createNearProduct(HashMap<CustomerProfile, Boolean> availableAttr, int nearCustProfs)
	{
		/*TODO: improve having into account the sub-profiles*/
		LinkedList<Integer> product = new LinkedList<Integer>();
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		int attrVal;
		LinkedList<Integer> custProfsInd = new LinkedList<Integer>();
		
		for(int i = 1; i < nearCustProfs; i++)
		{
			custProfsInd.add((int) Math.floor(Number_CustomerProfile * Math.random()));
		}
		for(int i = 0; i < Number_Attributes; i++)
		{
			attrVal = chooseAttribute(i, custProfsInd, availableAttr);
			product.add(attrVal);
		}
		return product;
	}
	 
 	/**Chosing an attribute near to the customer profiles given*/
	private int chooseAttribute(int attrInd, LinkedList<CustomerProfile> custProfInd, HashMap<CustomerProfile, Boolean> availableAttr)
	{
		int attrVal;
		LinkedList<Integer> possibleAttr;
		for(int i = 0; i < AttributesList.size() - 1; i++)
		{
			/*We count the valoration of each selected profile for attribute attrInd value i*/
			possibleAttr.add(0);
			for(int j = 0; j < custProfInd.size() - 1; j++)
			{
				possibleAttr.get(i) += CustomerProfileList(custProfsInd(j))(attrInd)(i);
			}
		}
		attrVal = getMaxAttrVal(attrInd,possibleAttr, availableAttr);
		
		return attrVal;
	}
	
	
	/**Chosing the attribute with the maximum score for the customer profiles given*/
	private int getMaxAttrVal(int attrInd, LinkedList<Integer> possibleAttr, HashMap<CustomerProfile, Boolean> availableAttr)
	//ByRef possibleAttr As List(Of Decimal), _
    //ByVal availableAttr As List(Of List(Of Boolean)
	{
		int attrVal = -1;
		double max = -1;
		for(int i = 0; i< possibleAttr.size(); i++)
		{
			if(availableAttr.get(attrInd)(i) && possibleAttr.get(i) > max) /*If availableAttr(attrInd)(i) AndAlso possibleAttr(i) > max*/
			{
				max = possibleAttr.get(i);
				attrVal = i;
			}
		}
		
		return attrVal;
	}
	 
	
	/**Creating the initial population*/
	private void createInitPopu(){
		
	}
	
	
	private int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception
	{
		int score = 0;
		switch(numOfValsOfAttr){
			case 2: {
				if(valOfAttrCust == valOfAttrProd) score = 10;
				else score = 0;
			}
			break;
			case 3: {
				if(valOfAttrCust == valOfAttrProd) score = 10;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 5;
				else score = 0;
			} break;
			case 4: {
				if(valOfAttrCust == valOfAttrProd) score = 10;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
				else score = 0;
			} break;
			case 5: {
				if(valOfAttrCust == valOfAttrProd) score = 10;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
				else score = 0;
			} break;
			case 11: {
				if(valOfAttrCust == valOfAttrProd) score = 10;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 6;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 4;
				else if(Math.abs(valOfAttrCust - valOfAttrProd) == 4) score = 2;
				else score = 0;
			} break;
			default: throw new Exception("Error in scoreAttribute() function: " +
                    "Number of values of the attribute unexpected");
		}
		return score;
	}
 
	/** Creates a deep copy of a List(Of Integer)*/
	private LinkedList<Integer> deepCopy(LinkedList<Integer> toBeCopied)
	{
		LinkedList<Integer> c = new LinkedList<Integer>();
		for(int i = 0; i < toBeCopied.size() - 1; i++)
		{
			c.add(toBeCopied.get(i));
		}
		return c;
	}
	
	/**Chosing the father in a random way taking into account the fitness*/
	private int chooseFather(double fitnessSum)
	{
		int fatherPos = 0;
		double rndVal = fitnessSum * Math.random();
		double accumulator = Fitness.get(fatherPos);
		while(rndVal > accumulator)
		{
			fatherPos += 1;
			accumulator += Fitness.get(fatherPos);
		}
		return fatherPos;
	}

	/**Method that creates an individual parameter passed mutating individual.
       The mutation is to add / remove a joint solution.*/
	private LinkedList<Integer> mutate(LinkedList<Integer> indiv){
		LinkedList<Integer> mutant = new LinkedList<Integer>();
		double mutation;
		int attrVal;
		
		mutant = deepCopy(indiv);
		//with mutant
			for(int i = 0; i < Number_Attributes; i++)
			{
				/*Random value in range [0,100)*/
				mutation = 100 * Math.random();
				if(mutation <= MUTATION_PROB)
				{
					boolean attrFound = false;
					while(!attrFound)
					{
						attrVal = (int)(Math.floor((AttributesList.get(i)) * Math.random()));
						if(ProducerList.get(0).AvailableAttribute.get(i)//(attrVal)) attrFound = true;
					}
					// .Item(i) = attrVal
				}
				
			}
		return mutant;
	}
	
	/***Computing the weighted score of the producer
        prodInd is the index of the producer**/
	private int computeWSC(Product product, int prodInd)
	{	int wsc = 0;
		boolean isTheFavourite;
		int meScore;
		int score;
		int k;
		int numTies;
		for(int i = 0; i < Number_CustomerProfile - 1; i++)
		{
			for(int j = 0; j < CustomerProfileListAux.get(i).getCustomerProfile().size() - 1; j++)
			{
				isTheFavourite = true;
				numTies = 1;
				meScore = scoreProduct(i,j,product);
				k = 0;
				while(isTheFavourite && k < Number_Producers)
				{
					if(k != prodInd)
					{
						score = scoreProduct(i,j, ProducerList.get(k).product);
						if(score > meScore) isTheFavourite = false;
						else if(score == meScore) numTies += 1;
					}
					k++;
				}
				/*TODO: When there exists ties we loose some voters because of decimals (undecided voters)*/
				if(isTheFavourite)
				{
					if((j == (CustomerProfileListAux.get(i).getCustomerProfile().size() - 1)) && ((NumberCustomerProfile.get(i) % RESP_PER_GROUP) != 0))
					{
						wsc += (NumberCustomerProfile.get(i) % RESP_PER_GROUP) / numTies;
					}
					else{
						wsc += RESP_PER_GROUP / numTies;
					}
				}
					
			}
		}
	
		return wsc;
	}
	
	
	private int scoreProduct(int custProfInd, int custSubProfInd, Product product)
	{
		int score = 0;
		for(int i = 0; i < Number_Attributes - 1; i++)
		{
			score += scoreAttribute(AttributesList.get(i), CustomerProfileListAux.get(custSubProfInd), product);//////////
			 // score += scoreAttribute(mAttributes(i), mCustProfAux(custProfInd)(custSubProfInd)(i), product(i))
		}
		return score;
	}
	
	/**Computing the sum of the fitness of all the population*/
    private int computeFitnessSum()
    {
    	int sum = 0;
    	for(int i = 0; i < Fitness.size() - 1; i++)
    	{
    		sum += Fitness.get(i);
    	}
    	return sum;
    }
     
     	
	/**Showing the wsc of the rest of products*/
	private void showWSC(){
		int wsc;
		int wscSum = 0;
		int custSum = 0;
		for(int i = 0; i < Number_Producers - 1; i++)
		{
			wsc = computeWSC(Producers.get(i).product, i);
			wscSum += wsc;
		}
		
	}
   
	/** Auxiliary methods statiscticPD()*/
	private double computeVariance(double mean){
		double sqrSum = 0;
		for(int i = 0; i < NUM_EXECUTIONS; i++){
			sqrSum += Math.pow( Results.get(i) - mean, 2); 
		}
		return (sqrSum/NUM_EXECUTIONS);
	}
	
}
