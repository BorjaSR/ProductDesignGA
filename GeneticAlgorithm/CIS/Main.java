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
	static final int KNOWN_ATTRIBUTES = 60; /* 100
												 * % of attributes known for all
												 * producers
												 */
	static final double SPECIAL_ATTRIBUTES = 40; /* 33
												 * % of special attributes known
												 * for some producers
												 */
	static final int MUT_PROB_CUSTOMER_PROFILE = 33; /*  * % of mutated
														 * attributes in a
														 * customer profile
														 */
	static final int CROSSOVER_PROB = 80; /* % of crossover */
	static final int MUTATION_PROB = 1; /* % of mutation */
	static final int NUM_GENERATIONS = 100; /* number of generations */
	static final int NUM_POPULATION = 20; /* number of population */
	static final int RESP_PER_GROUP = 20; /* * We divide the respondents of each
											 * profile in groups of
											 * RESP_PER_GROUP respondents
											 */
	static final int NEAR_CUST_PROFS = 4;
	static final int NUM_EXECUTIONS = 20; /* number of executions */

	// static final String SOURCE = "D:\Pablo\EncuestasCIS.xlsx";
	static final int SHEET_AGE_STUDIES = 1;
	static final int SHEET_POLITICAL_PARTIES = 2;
	static final String EOF = "EOF";

	private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
	private static ArrayList<Producer> Producers;
	//private static ArrayList<CustomerProfile> CustomerProfiles;

	/* INPUT VARIABLES */
	private static int Number_Attributes; /* Number of attributes */
	private static int Number_Producers = 10; /* Number of producers */
	private static int Number_CustomerProfile = 100; //TODO tiene que venir del excel /* Number of customer profiles */

	/* GA VARIABLES */
	private int BestWSC; /* Stores the best wsc found */
	private ArrayList<Producer> Population;   //Private mPopu As List(Of List(Of Integer))
	private /*HashMap<String,*/ ArrayList<Integer> Fitness; /* * mFitness(i) = wsc of mPopu(i) */

	/* STATISTICAL VARIABLES */
	private LinkedList<Integer> Results;
	private LinkedList<Integer> Initial_Results;
   
   // private LinkedList<Producer> ProducerList;
    private static LinkedList<CustomerProfile> CustomerProfileList;
    private LinkedList<CustomerProfile> CustomerProfileListAux;
    private static LinkedList<Integer> NumberCustomerProfile;

    /*************************************** " AUXILIARY EXCEL METHODS " ***************************************/
    
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
					// System.out.println("A�adiendo Celda: " +
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
		
		generateProducers();
		showProducers();
//		showAttributes();
//		showAvailableAttributes(createAbailableAttributes());
		//showExcelData(sheetData);
	}
	
	
	/*************************************** " PRIVATE METHODS " ***************************************/
	
	private void solvePD() throws Exception
	{
		Math.random();
		generateInput();
		solvePD_GA();
	}
	
	/**Generating the input data 
	 * @throws Exception */
	private void generateInput() throws Exception {
		 /*In this case study the number of attributes mNAttr 
	       of the product is the number of questions of the poll
	       The number of producers mNProd is the number of political parties:
	       MyPP, PP, PSOE, IU, UPyD, CiU*/
		Number_Attributes = 0;
		Number_Producers = 6;

	     /*   readExcelWorksheet(SHEET_POLITICAL_PARTIES)
	        genAttrVal()
	        closeExcel()

	        readExcelWorksheet(SHEET_AGE_STUDIES)
	        genCustomerProfiles()
	        closeExcel()

	        genCustomerProfilesNum()*/
	        divideCustomerProfile();

	     /*   readExcelWorksheet(SHEET_POLITICAL_PARTIES)
	        genProducers()
	        closeExcel()*/
		
	}

	
	/**Solving the PD problem by using a GA*/
	private void solvePD_GA() throws Exception{
		int generation = 0;
		ArrayList<Producer> newPopu = new ArrayList<Producer>();
		ArrayList<Integer> newFitness = new ArrayList<Integer>();
		createInitPopu();
		while(generation < NUM_GENERATIONS)
		{
			generation++;
			newPopu = createNewPopu(newFitness);
			Population = tournament(newPopu, newFitness);
		}
		
		Results.add(BestWSC);
		showWSC();
	}

	/**Generating statistics about the PD problem*/
	private void statisticsPD() throws Exception{
		double mean;
		double initMean;
		double sum = 0; /*sum of customers achieved*/
		double initSum = 0; /*sum of initial customers*/
		int sumCust = 0; /*sum of the total number of customers*/
		double custMean;
		double variance;
		double initVariance;
		double stdDev;
		double initStdDev;
		double percCust; /*% of customers achieved*/
		double initPercCust; /*% of initial customers achieved*/
		String msg;
		
		Results = new LinkedList<Integer>();
		Initial_Results = new LinkedList<Integer>();
		
		Math.random();
		if (Number_Producers == 0) { generateInput(); }
		
		for(int i = 0; i < NUM_EXECUTIONS - 1; i++)
		{
			if (i != 0) /*We reset myPP and create a new product as the first product*/
			{
				for(int j = 0; j < Producers.get(0).getAvailableAttribute().size() - 1; j++)
				{
					@SuppressWarnings("unused")
					Product prod = Producers.get(j).getProduct();
					prod = createNearProduct(Producers.get(j).getAvailableAttribute(), (int)((Number_CustomerProfile * Math.random()) + 1));
				}

			}	
			solvePD_GA();
			sum += Results.get(i);
			initSum += Initial_Results.get(i);
			//sumCust += /*xtNCust.Text*/
		}
		
		mean = sum / NUM_EXECUTIONS;
		initMean = initSum / NUM_EXECUTIONS;
		variance = computeVariance(mean);
		initVariance = computeVariance(initMean);
		stdDev = Math.sqrt(variance);
		initStdDev = Math.sqrt(initVariance);
		custMean = sumCust / NUM_EXECUTIONS;
		percCust = 100 * mean / custMean;
		initPercCust = 100 * initMean / custMean;
		
		/*MOSTRARLO*/
	}
	
	
	/*************************************** " AUXILIARY METHODS GENERATEINPUT()" ***************************************/
	
	/** Creating the attributes and the possible values of them */
	private static void generateAttributeValor(List sheetData) {

			int MIN_VAL = 1;
			
			double number_valors = 0.0;
			for (int i = 4; i < sheetData.size(); i++) {
				// System.out.println("Celda [" + i + ", 0]: ");

				if (number_valors == 0) {
					Cell cell = (Cell) ((List) sheetData.get(i)).get(0);
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						number_valors = cell.getNumericCellValue() + 1;
						TotalAttributes.add(new Attribute("Attribute " + (TotalAttributes.size()+1), MIN_VAL, (int)number_valors-1));
					} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						if(cell.getRichStringCellValue().equals("MMM"))
							break;
					}
				}
				number_valors--;
			}
	}
	
	private static void showAttributes(){
		for(int k = 0; k < TotalAttributes.size(); k++){
			System.out.println(TotalAttributes.get(k).getName());
			System.out.println(TotalAttributes.get(k).getMIN());
			System.out.println(TotalAttributes.get(k).getMAX());
		}
	}
	
	private static void showAvailableAttributes(ArrayList<Attribute> availableAttrs){
		for(int k = 0; k < availableAttrs.size(); k++){
			System.out.println(availableAttrs.get(k).getName());
			System.out.println(availableAttrs.get(k).getMIN());
			System.out.println(availableAttrs.get(k).getMAX());
			for(int j = 0; j < availableAttrs.get(k).getAvailableValues().size(); j++){
				System.out.println(availableAttrs.get(k).getAvailableValues().get(j));
			}
		}
	}
	
	private static void showProducers(){
		for(int i = 0; i < Producers.size(); i++){
			Producer p = Producers.get(i);
			System.out.println("PRODUCTOR " + (i+1));
			for(int j = 0; j < TotalAttributes.size(); j++){
				System.out.print(TotalAttributes.get(j).getName());
				System.out.println(":  Value -> " + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)));
			}
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
	
	
	/**Generating the producers*/
	private static void generateProducers(){
		Producers = new ArrayList<>();
		for (int i = 0; i < Number_Producers; i++){
			Producer new_producer = new Producer();
			new_producer.setAvailableAttribute(createAvailableAttributes());
			new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));
			Producers.add(new_producer);
		}
	}
	
	
	/**Creating different customer profiles*/
	private static void generateCustomerProfiles(){}
	
	
	/**Dividing the customer profiles into sub-profiles
	 * @throws Exception */
	private void divideCustomerProfile() throws Exception{
		int numOfSubProfile;
		CustomerProfileListAux = new LinkedList<CustomerProfile>();
		for(int i = 0; i < Number_CustomerProfile - 1; i++)
		{
			CustomerProfileListAux.add(new CustomerProfile(new ArrayList<Attribute>()));
			numOfSubProfile = CustomerProfileList.get(i).getScoreAttributes().size() / RESP_PER_GROUP;
			if((CustomerProfileList.get(i).getScoreAttributes().size() % RESP_PER_GROUP) != 0)
			{
				numOfSubProfile++;
			}	
			for(int j = 0; j < numOfSubProfile - 1; j++) //We divide into sub-profiles
			{
				CustomerProfileListAux.get(i).getScoreAttributes().add(TotalAttributes.get(j));
				for(int k = 0; k < Number_Attributes - 1; k++) //Each of the sub-profiles choose a value for each of the attributes
				{
					CustomerProfileListAux.get(i).getScoreAttributes().get(j).getScoreValues().add(chooseValueForAttribute(i, k));
					
				}
			}
		}
	}
	
	/**Given an index of a customer profile and the index of an attribute we choose a value
    for that attribute of the sub-profile having into account the values of the poll*/
	private Integer chooseValueForAttribute(int custProfInd, int attrInd) throws Exception {
		int value = 0;
		double total = 0;
		double rndVal;
		boolean found = false;
		double accumulated = 0;
		
		for (int i = 0; i < CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues().size() - 1; i++)
		{
			total += CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues().get(i);
		}
		rndVal = total * Math.random();
		while(!found)
		{
			accumulated += CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues().get(value);
			if(rndVal <= accumulated) found = true;
			else value++;
		
		
			if (value >=  CustomerProfileList.get(custProfInd).getScoreAttributes().size())
				throw new Exception("Error 1 in chooseValueForAttribute() method: Value not found");
		}
		
		if(!found) throw new Exception("Error 2 in chooseValueForAttribute() method: Value not found");
		return value;
	}
	
	/**Generating the numbers of customers of each profile*/
	private static void genCustomerProfilesNum(){
		NumberCustomerProfile = new LinkedList<Integer>();
	}
	
	/**Creating available attributes for the producer*/
	private static ArrayList<Attribute> createAvailableAttributes()
	{
		ArrayList<Attribute> availableAttributes = new ArrayList<>();
		int limit = Number_Attributes * KNOWN_ATTRIBUTES / 100;
		
		/*All producers know the first ATTRIBUTES_KNOWN % of the attributes*/
		for(int i = 0; i < limit - 1; i++){
			Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(), TotalAttributes.get(i).getMAX());
			ArrayList<Boolean> values = new ArrayList<>();
			for(int j = 0; j < attr.getMAX(); j++){
				values.add(true);
			}
			attr.setavailableValues(values);
			availableAttributes.add(attr);
		}
		
		/*The remaining attributes are only known by SPECIAL_ATTRIBUTES % producers*/
		for(int k = limit; k < TotalAttributes.size() - 1; k++){
			Attribute attr = new Attribute(TotalAttributes.get(k).getName(), TotalAttributes.get(k).getMIN(), TotalAttributes.get(k).getMAX());
			ArrayList<Boolean> values = new ArrayList<>();
			
			for(int j = 0; j < attr.getMAX(); j++){
				double rnd = Math.random();
				double rndVal = Math.random();
				/*Furthermore, with a 50% of probabilities it can know this attribute*/
				if(rndVal < (SPECIAL_ATTRIBUTES / 100) && rnd < 0.5)
					values.add(true);
				else
					values.add(false);
			}
			attr.setavailableValues(values);
			availableAttributes.add(attr);
		}
		
		return availableAttributes;
	}
	
	/** Creating a random product*/
	private Product createRndProduct(ArrayList<Attribute> availableAttribute) {
    	Product product = new Product(new HashMap<Attribute,Integer>());
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		int attrVal = 0;
		
		for(int i = 0; i < limit - 1; i++)
		{
			attrVal = (int) ((int) TotalAttributes.get(i).getScoreValues().get(i) * Math.random()); ////////////////verificar////////////
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal); ///////////////////////verificar/////////////
		}
		
		for(int i = limit; i < Number_Attributes - 1; i++)
		{
			boolean attrFound = false;
			while(!attrFound)
			{
				attrVal = (int) ((int) TotalAttributes.get(i).getScoreValues().get(i) * Math.random()); //////////verificar///////////////
			    if(availableAttribute.get(i).getAvailableValues().get(attrVal)) attrFound = true;
			}
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal); /////verificar////
		}
		return product;
	}
	
    /**Creating a product near various customer profiles*/
	private Product createNearProduct(ArrayList<Attribute> availableAttribute, int nearCustProfs) {
		/*TODO: improve having into account the sub-profiles*/
		Product product = new Product(new HashMap<Attribute,Integer>());
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		int attrVal = 0;
		ArrayList<Integer> custProfsInd = new ArrayList<Integer>();
		
		for(int i = 1; i < nearCustProfs; i++)
		{
			custProfsInd.add((int) Math.floor(Number_CustomerProfile * Math.random()));
		}
		for(int i = 0; i < Number_Attributes - 1; i++)
		{
			attrVal = chooseAttribute(i, custProfsInd, availableAttribute);
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal); //////////verificar/////////////
		}
		return product;
	}
	
	private static Product createProduct(ArrayList<Attribute> availableAttrs){

		Product product = new Product(new HashMap<Attribute,Integer>());
		ArrayList<Integer> customNearProfs = new ArrayList<>();
		for (int i = 0; i < NEAR_CUST_PROFS; i++){
			customNearProfs.add((int) Math.floor(Number_CustomerProfile * Math.random()));
		}
		
		HashMap<Attribute, Integer> attrValues = new HashMap<>();
		
		for(int j = 0; j < TotalAttributes.size(); j++){
			attrValues.put(TotalAttributes.get(j), chooseAttribute(j, customNearProfs, availableAttrs));
		}
		product.setAttributeValue(attrValues);
		return product;
	}
	
 	/**Chosing an attribute near to the customer profiles given*/
	private static int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs)
	{
		int attrVal;
		ArrayList<Integer> possibleAttr = new ArrayList<Integer>();
		
		for(int i = 0; i < TotalAttributes.get(attrInd).getAvailableValues().size() - 1; i++)
		{
			/*We count the valoration of each selected profile for attribute attrInd value i*/
			possibleAttr.add(0);
			for(int j = 0; j < custProfInd.size() - 1; j++)
			{
				@SuppressWarnings("unused")
				int possible = possibleAttr.get(i);
				possible += CustomerProfileList.get(custProfInd.get(j)).getScoreAttributes().get(attrInd).getScoreValues().get(i);
			}
		}
		attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);
		
		return attrVal;
	}

	/**Chosing the attribute with the maximum score for the customer profiles given*/
	private static int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr)
	{
		int attrVal = -1;
		double max = -1;
		for(int i = 0; i< possibleAttr.size() - 1; i++)
		{
			if(availableAttr.get(attrInd).getAvailableValues().get(i) && possibleAttr.get(i) > max) 
			{
				max = possibleAttr.get(i);
				attrVal = i;
			}
		}
		
		return attrVal;
	}
	
	
	/*************************************** " AUXILIARY METHODS SOLVEPD_GA()" ***************************************/

	/**Creating the initial population*/
	private void createInitPopu(){
		ArrayList<Producer> mPopu = new ArrayList<Producer>();
		ArrayList<Integer> mFitness = new ArrayList<Integer>();
		
		mPopu.add(deepCopy(Producers.get(0).getValuesPopuProducer()).get(index)); /////////verificar/////////////
		mFitness.add(computeWSC(mPopu.get(0).getProduct(),0));
		BestWSC = mFitness.get(0);
		Initial_Results.add(BestWSC);
		
		for(int i = 0; i < NUM_POPULATION - 1; i++)
		{
			if(i % 2 == 0) /*We create a random product*/
				mPopu.add(createRndProduct(Producers.get(0).getAvailableAttribute()).getAttributeValue(),); /////////verificar//////////
			else /*We create a near product*/
				mFitness.add(createNearProduct(Producers.get(0).getAvailableAttribute(), (int) ((Number_CustomerProfile * Math.random()) + 1)).getAttributeValue().get(i));  /////////??verificar//////////
			
			if(mFitness.get(i) > BestWSC)
			{
				BestWSC = mFitness.get(i);
				ArrayList<Integer> prod = Producers.get(0).getValuesPopuProducer();
				prod  = deepCopy(mPopu.get(i).getValuesPopuProducer()); 
			}
			
		}
		
	}


	/***Computing the weighted score of the producer
    prodInd is the index of the producer
	 * @throws Exception **/
	private int computeWSC(Product product, int prodInd) throws Exception {
		int wsc = 0;
		boolean isTheFavourite;
		int meScore;
    	int score;
		int k;
		int numTies;
		for(int i = 0; i < Number_CustomerProfile - 1; i++)
		{
			for(int j = 0; j < CustomerProfileListAux.get(i).getScoreAttributes().size() - 1; j++)
			{
				isTheFavourite = true;
				numTies = 1;
				meScore = scoreProduct(i,j,product);
				k = 0;
				while(isTheFavourite && k < Number_Producers)
				{
					if(k != prodInd)
					{
						score = scoreProduct(i,j, Producers.get(k).product);
						if(score > meScore) isTheFavourite = false;
						else if(score == meScore) numTies += 1;
					}
					k++;
				}
				/*TODO: When there exists ties we loose some voters because of decimals (undecided voters)*/
				if(isTheFavourite)
				{
					if((j == (CustomerProfileListAux.get(i).getScoreAttributes().size() - 1)) && ((NumberCustomerProfile.get(i) % RESP_PER_GROUP) != 0))
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

	
	/**Computing the score of a product given the customer profile index
    custProfInd and the product*/
	private int scoreProduct(int custProfInd, int custSubProfInd, Product product) throws Exception
	{
		int score = 0;
		for(int i = 0; i < Number_Attributes - 1; i++)
		{
			score += scoreAttribute(TotalAttributes.get(custProfInd).getScoreValues().get(i), CustomerProfileListAux.get(custProfInd).getScoreAttributes().get(custSubProfInd).getScoreValues().get(i), product.getAttributeValue().get(i));//////////
			 // score += scoreAttribute(mAttributes(i), mCustProfAux(custProfInd)(custSubProfInd)(i), product(i))
		}
		return score;
	}
	
	/**Computing the score of an attribute for a product given the
    ' number of values */
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
	private ArrayList<Integer> deepCopy(ArrayList<Integer> toBeCopied)
	{
		ArrayList<Integer> c = new ArrayList<Integer>();
		for(int i = 0; i < toBeCopied.size() - 1; i++)
		{
			c.add(toBeCopied.get(i));
		}
		return c;
	}
	
	/** Creating a new population*/
	@SuppressWarnings("unchecked")
	private ArrayList<Producer> createNewPopu(ArrayList<Integer> fitness) throws Exception {
		int fitnessSum = computeFitnessSum();
		ArrayList<Producer> newPopu = new ArrayList<Producer>();
		int father;
		int mother;
		ArrayList<Integer> son;
		
		fitness = new ArrayList<Integer>();
		for(int i = 0; i < NUM_POPULATION - 1; i++)
		{
			father = chooseFather(fitnessSum);
			mother = chooseFather(fitnessSum);
			son = mutate(breed(father,mother));
			
			newPopu.get(i).getValuesPopuProducer().add(son.get()); //////////////////
			fitness.add(computeWSC(newPopu.get(i).getProduct(),0));
		}
		
		return newPopu;
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

	/**M�todo que dado un padre y una madre los cruza para obtener un hijo.
    Para cada posici�n del array eligiremos aleatoriamente si el hijo heredar�
    esa posici�n del padre o de la madre.*/
	private ArrayList<Integer> breed(int father, int mother) {
		ArrayList<Integer> son = new ArrayList<Integer>();
		/*Random value in range [0,100)*/
		double crossover = 100 * Math.random();
		int rndVal;
		
		if(crossover <= CROSSOVER_PROB)
		{
			for(int i = 0; i < son.size() - 1; i++) //With son
			{
				for(int j = 0; j < Number_Attributes - 1; j++)
				{
					rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/
					if(rndVal == 0) son.add(Population.get(father).getValuesPopuProducer().get(j));
					else son.add(Population.get(mother).getValuesPopuProducer().get(j));
				}
			}
		}
		else
		{
			rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/
			if(rndVal == 0) son = deepCopy(Population.get(father).getValuesPopuProducer());
			else son = deepCopy(Population.get(mother).getValuesPopuProducer());
		}
		return null;
	}
	
	
	/**Method that creates an individual parameter passed mutating individual.
    The mutation is to add / remove a joint solution.
	 * @throws Exception */
	private ArrayList<Integer> mutate(ArrayList<Integer> indiv){
		ArrayList<Integer> mutant = new ArrayList<Integer>();
		double mutation;
		int attrVal = 0;
		
		mutant = deepCopy(indiv);
		for(int i = 0; i < mutant.size() - 1; i++)//with mutant
			for(int j = 0; j < Number_Attributes - 1; j++)
			{
				/*Random value in range [0,100)*/
				mutation = 100 * Math.random();
				if(mutation <= MUTATION_PROB)
				{
					boolean attrFound = false;
					while(!attrFound)
					{
						attrVal = (int)(Math.floor(TotalAttributes.get(j).getScoreValues().get(i) * Math.random()));
						if(Producers.get(0).getAvailableAttribute().get(j).getAvailableValues().get(attrVal)) attrFound = true;
					}
					// .Item(i) = attrVal
					@SuppressWarnings("unused")
					int it = mutant.get(j);
					it = attrVal;
				}
				
			}
		return mutant;
	}
	

	/** M�todo que dada la poblaci�n original y una nueva poblaci�n elige la siguente
    ' generaci�n de individuos. Actualizo la mejor soluci�n encontrada en caso de mejorarla.*/
	 private ArrayList<Producer> tournament(ArrayList<Producer> newPopu, ArrayList<Integer> newFitness) {

		 ArrayList<Producer> nextGeneration = new ArrayList<Producer>();
		for(int i = 0; i < NUM_POPULATION - 1; i++)
		{
			if(Fitness.get(i) >= newFitness.get(i)) nextGeneration.add(deepCopy(Population.get(i).getValuesPopuProducer()).get(index)); ///////////////////
			// An old individual cannot improve the fitness
			else 
				nextGeneration.add(deepCopy(newPopu.get(i).getValuesPopuProducer()).get(index));//////////////////////////
				int fit = Fitness.get(i);
				fit = newFitness.get(i); // We update the fitness of the new individual
				if(newFitness.get(i) > BestWSC)
				{
					BestWSC = newFitness.get(i);
					ArrayList<Integer> producer = Producers.get(0).getValuesPopuProducer(); 
					producer = deepCopy(newPopu.get(i).getValuesPopuProducer());
				}
		}
		return nextGeneration;
	}
	
    
	/**Showing the wsc of the rest of products 
	 * @throws Exception */
	private void showWSC() throws Exception{
		int wsc;
		int wscSum = 0;
		int custSum = 0;
		for(int i = 0; i < Number_Producers - 1; i++)
		{
			wsc = computeWSC(Producers.get(i).getProduct(), i);
			wscSum += wsc;
		}
		for(int j = 0; j < Number_CustomerProfile - 1; j++)
		{
			custSum += NumberCustomerProfile.get(j);
		}
		
		if(wscSum != custSum) throw new Exception("Error in showWSC() method");
		
	}


	/*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

	/** Auxiliary methods statiscticPD()*/
	private double computeVariance(double mean){
		double sqrSum = 0;
		for(int i = 0; i < NUM_EXECUTIONS; i++){
			sqrSum += Math.pow( Results.get(i) - mean, 2); 
		}
		return (sqrSum/NUM_EXECUTIONS);
	}
	
	
	
}