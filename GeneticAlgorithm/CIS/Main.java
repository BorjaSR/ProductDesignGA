import java.util.HashMap;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.Cell;

public class Main {

	static final int KNOWN_ATTRIBUTES = 100; /*
												 * 100 % of attributes known for
												 * all producers
												 */
	static final double SPECIAL_ATTRIBUTES = 33; /*
													 * 33 % of special
													 * attributes known for some
													 * producers
													 */
	static final int MUT_PROB_CUSTOMER_PROFILE = 33; /*
														 * * % of mutated
														 * attributes in a
														 * customer profile
														 */
	static final int CROSSOVER_PROB = 80; /* % of crossover */
	static final int MUTATION_PROB = 1; /* % of mutation */
	static final int NUM_GENERATIONS = 100; /* number of generations */
	static final int NUM_POPULATION = 20; /* number of population */
	static final int RESP_PER_GROUP = 20; /*
											 * * We divide the respondents of
											 * each profile in groups of
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

	/* INPUT VARIABLES */
	private static int Number_Attributes = 0; /* Number of attributes */
	private static int Number_Producers = 10; /* Number of producers */
	private static int Number_CustomerProfile = 35; // TODO Este dato lo sacamos
													// de la propia lista ->
													// CustomerProfileList.size()//tiene
													// que venir del excel /*
													// Number of customer
													// profiles */

	/* GA VARIABLES */
	private static int BestWSC; /* Stores the best wsc found */
	private static ArrayList<Product> Population; // Private mPopu As List(Of
													// List(Of Integer))
	private static ArrayList<Integer> Fitness; /*
												 * * mFitness(i) = wsc of
												 * mPopu(i)
												 */

	/* STATISTICAL VARIABLES */
	private static ArrayList<Integer> Results = new ArrayList<>();
	private static ArrayList<Integer> Initial_Results = new ArrayList<>();

	private static ArrayList<CustomerProfile> CustomerProfileList = new ArrayList<>();
	private static ArrayList<CustomerProfile> CustomerProfileListAux = new ArrayList<>();
	private static ArrayList<Integer> NumberCustomerProfile;

	/***************************************
	 * " AUXILIARY EXCEL METHODS " * @throws Exception
	 ***************************************/

	public static void main(String[] args) throws Exception {
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

		Math.random();

		generateAttributeValor(sheetData);
		generateCustomerProfiles();
		genCustomerProfilesNum();
		divideCustomerProfile();
		generateProducers();

		solvePD_GA();
		statisticsPD();
		// showProducers();
		// showAttributes();
		// showAvailableAttributes(createAvailableAttributes());
		// showExcelData(sheetData);
	}

	/*************************************** " PRIVATE METHODS " ***************************************/

	/*
	 * private void solvePD() throws Exception { Math.random(); generateInput();
	 * solvePD_GA(); }
	 */

	/**
	 * Generating the input data
	 * 
	 * @throws Exception
	 */
	private static void generateInput() throws Exception {
		/*
		 * In this case study the number of attributes mNAttr of the product is
		 * the number of questions of the poll The number of producers mNProd is
		 * the number of political parties: MyPP, PP, PSOE, IU, UPyD, CiU
		 */
		// Number_Attributes = 0;
		// Number_Producers = 6;

		/*
		 * readExcelWorksheet(SHEET_POLITICAL_PARTIES) genAttrVal() closeExcel()
		 * 
		 * readExcelWorksheet(SHEET_AGE_STUDIES) genCustomerProfiles()
		 * closeExcel()
		 * 
		 * genCustomerProfilesNum()
		 */
		// divideCustomerProfile();

		/*
		 * readExcelWorksheet(SHEET_POLITICAL_PARTIES) genProducers()
		 * closeExcel()
		 */

	}

	/** Solving the PD problem by using a GA */
	private static void solvePD_GA() throws Exception {
		int generation = 0;

		ArrayList<Product> newPopu;
		Clase_Fitness newFitness = new Clase_Fitness(new ArrayList<Integer>());
		createInitPopu();
		while (generation < NUM_GENERATIONS) {
			generation++;
			newPopu = createNewPopu(newFitness.getNewFitness());
			Population = tournament(newPopu, newFitness.getNewFitness());
		}

		Results.add(BestWSC);
		showWSC();
	}

	/** Generating statistics about the PD problem */
	private static void statisticsPD() throws Exception {
		double mean;
		double initMean;
		double sum = 0; /* sum of customers achieved */
		double initSum = 0; /* sum of initial customers */
		int sumCust = 0; /* sum of the total number of customers */
		double custMean;
		double variance;
		double initVariance;
		double stdDev;
		double initStdDev;
		double percCust; /* % of customers achieved */
		double initPercCust; /* % of initial customers achieved */
		String msg;
		List sheetData = new ArrayList<>();

		Results = new ArrayList<Integer>();
		Initial_Results = new ArrayList<Integer>();

		Math.random();
		if (Number_Producers == 0) {
			generateAttributeValor(sheetData);
			generateCustomerProfiles();
			genCustomerProfilesNum();
			divideCustomerProfile();
			generateProducers();
		}

		for (int i = 0; i < NUM_EXECUTIONS; i++) {
			if (i != 0) /*
						 * We reset myPP and create a new product as the first
						 * product
						 */
			{
				for (int j = 0; j < Producers.get(0).getAvailableAttribute().size(); j++) {
					@SuppressWarnings("unused")
					Product prod = Producers.get(j).getProduct();
					prod = createNearProduct(Producers.get(j).getAvailableAttribute(),
							(int) ((Number_CustomerProfile * Math.random()) + 1));
				}

			}
			solvePD_GA();
			sum += Results.get(i);
			initSum += Initial_Results.get(i);
			// sumCust += /*xtNCust.Text*/
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

		/* MOSTRARLO */
		System.out.println("Mean = " + mean);
		System.out.println("Init mean = " + initMean);
		System.out.println("Std dev = " + stdDev);
		System.out.println("Init std dev = " + initStdDev);
		System.out.println("Cust mean = " + custMean);
		System.out.println("% cust = " + percCust);
		System.out.println("% init cust = " + initPercCust);
		System.out.println("Mean = " + mean);
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
					TotalAttributes.add(new Attribute("Attribute " + (TotalAttributes.size() + 1), MIN_VAL,
							(int) number_valors - 1));
					Number_Attributes++;
				} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					if (cell.getRichStringCellValue().equals("MMM"))
						break;
				}
			}
			number_valors--;
		}
	}

	private static void showAttributes() {
		for (int k = 0; k < TotalAttributes.size(); k++) {
			System.out.println(TotalAttributes.get(k).getName());
			System.out.println(TotalAttributes.get(k).getMIN());
			System.out.println(TotalAttributes.get(k).getMAX());
		}
	}

	private static void showAvailableAttributes(ArrayList<Attribute> availableAttrs) {
		for (int k = 0; k < availableAttrs.size(); k++) {
			System.out.println(availableAttrs.get(k).getName());
			System.out.println(availableAttrs.get(k).getMIN());
			System.out.println(availableAttrs.get(k).getMAX());
			for (int j = 0; j < availableAttrs.get(k).getAvailableValues().size(); j++) {
				System.out.println(availableAttrs.get(k).getAvailableValues().get(j));
			}
		}
	}

	private static void showProducers() {
		for (int i = 0; i < Producers.size(); i++) {
			Producer p = Producers.get(i);
			System.out.println("PRODUCTOR " + (i + 1));
			for (int j = 0; j < TotalAttributes.size(); j++) {
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

	/** Generating the producers */
private static void generateProducers() {
		Producers = new ArrayList<>();
		for (int i = 0; i < Number_Producers; i++) {
			Producer new_producer = new Producer();
			new_producer.setAvailableAttribute(createAvailableAttributes());
			new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));
			Producers.add(new_producer);
		}
	}

	/** Creating different customer profiles */
private static void generateCustomerProfiles() {

		// Generate 4 random Customer Profile
		for (int i = 0; i < 4; i++) {
			ArrayList<Attribute> attrs = new ArrayList<>();
			for (int j = 0; j < TotalAttributes.size(); j++) {
				Attribute attr = TotalAttributes.get(j);
				ArrayList<Integer> scoreValues = new ArrayList<>();
				for (int k = 0; k < attr.MAX; k++) {
					int random = (int) (attr.MAX * Math.random());
					scoreValues.add(random);
				}
				attr.setScoreValues(scoreValues);
				attrs.add(attr);
			}
			CustomerProfileList.add(new CustomerProfile(attrs));
		}

		// Create 2 mutants for each basic profile
		for (int i = 0; i < 4; i++) {
			CustomerProfileList.add(mutateCustomerProfile(CustomerProfileList.get(i)));
			CustomerProfileList.add(mutateCustomerProfile(CustomerProfileList.get(i)));
		}

		// Creating 4 isolated profiles
		for (int i = 0; i < 4; i++) {
			ArrayList<Attribute> attrs = new ArrayList<>();
			for (int j = 0; j < TotalAttributes.size(); j++) {
				Attribute attr = TotalAttributes.get(j);
				ArrayList<Integer> scoreValues = new ArrayList<>();
				for (int k = 0; k < attr.MAX; k++) {
					int random = (int) (attr.MAX * Math.random());
					scoreValues.add(random);
				}
				attr.setScoreValues(scoreValues);
				attrs.add(attr);
			}
			CustomerProfileList.add(new CustomerProfile(attrs));
		}
	}

	/**
	 * Generating the numbers of customers of each profile
	 * 
	 * @throws Exception
	 */
	public static void genCustomerProfilesNum() throws Exception {
		NumberCustomerProfile = new ArrayList<>();
		// We take the number of polled of the healthcare barometer
		NumberCustomerProfile.add(7);
		NumberCustomerProfile.add(16);
		NumberCustomerProfile.add(24);
		NumberCustomerProfile.add(60);
		NumberCustomerProfile.add(352);
		NumberCustomerProfile.add(280);
		NumberCustomerProfile.add(479);
		NumberCustomerProfile.add(570);
		NumberCustomerProfile.add(633);
		NumberCustomerProfile.add(566);
		NumberCustomerProfile.add(94);
		NumberCustomerProfile.add(175);
		NumberCustomerProfile.add(241);
		NumberCustomerProfile.add(235);
		NumberCustomerProfile.add(200);
		NumberCustomerProfile.add(128);
		NumberCustomerProfile.add(75);
		NumberCustomerProfile.add(135);
		NumberCustomerProfile.add(343);
		NumberCustomerProfile.add(349);
		NumberCustomerProfile.add(245);
		NumberCustomerProfile.add(94);
		NumberCustomerProfile.add(82);
		NumberCustomerProfile.add(53);
		NumberCustomerProfile.add(188);
		NumberCustomerProfile.add(161);
		NumberCustomerProfile.add(94);
		NumberCustomerProfile.add(82);
		NumberCustomerProfile.add(77);
		NumberCustomerProfile.add(29);
		NumberCustomerProfile.add(252);
		NumberCustomerProfile.add(263);
		NumberCustomerProfile.add(145);
		NumberCustomerProfile.add(75);
		NumberCustomerProfile.add(55);

		if (NumberCustomerProfile.size() != Number_CustomerProfile)
			throw new Exception("Error in function genCustomerProfilesNum()");
	}

	private static CustomerProfile mutateCustomerProfile(CustomerProfile customerProfile) {
		CustomerProfile mutant = new CustomerProfile(null);
		ArrayList<Attribute> attrs = new ArrayList<>();
		for (int i = 0; i < TotalAttributes.size(); i++) {
			Attribute attr = TotalAttributes.get(i);
			ArrayList<Integer> scoreValues = new ArrayList<>();
			for (int k = 0; k < attr.MAX; k++) {
				if (Math.random() < (MUT_PROB_CUSTOMER_PROFILE / 100)) {
					int random = (int) (attr.MAX * Math.random());
					scoreValues.add(random);
				} else
					scoreValues.add(customerProfile.getScoreAttributes().get(i).getScoreValues().get(k));
			}
			attr.setScoreValues(scoreValues);
			attrs.add(attr);
		}
		mutant.setScoreAttributes(attrs);
		return mutant;
	}

	/**
	 * Dividing the customer profiles into sub-profiles
	 * 
	 * @throws Exception
	 */
	private static void divideCustomerProfile() throws Exception {
		int numOfSubProfile;
		CustomerProfileListAux = new ArrayList<CustomerProfile>();
		for (int i = 0; i < CustomerProfileList.size(); i++) {
			CustomerProfileListAux.add(new CustomerProfile(new ArrayList<Attribute>()));
			numOfSubProfile = NumberCustomerProfile.get(i) / RESP_PER_GROUP; // NumberCustomerProfile.get(i)
			if ((NumberCustomerProfile.get(i) % RESP_PER_GROUP) != 0)// CustomerProfileList.get(i).getScoreAttributes().size()
			{
				numOfSubProfile++;
			}
			for (int j = 0; j < numOfSubProfile; j++) // We divide into
															// sub-profiles   - 1
			{
				CustomerProfileListAux.get(i).getScoreAttributes()
						.add(new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(),
								TotalAttributes.get(j).getMAX()));
				for (int k = 0; k < Number_Attributes; k++) // Each of the
																// sub-profiles
																// choose a
																// value for
																// each of the
																// attributes
				{
					CustomerProfileListAux.get(i).getScoreAttributes().get(j).getScoreValues()
							.add(chooseValueForAttribute(i, k));

				}
			}
		}
	}

	/**
	 * Given an index of a customer profile and the index of an attribute we
	 * choose a value for that attribute of the sub-profile having into account
	 * the values of the poll
	 */
	private static Integer chooseValueForAttribute(int custProfInd, int attrInd) throws Exception {
		int value = 0;
		double total = 0;
		double rndVal;
		boolean found = false;
		double accumulated = 0;
		try {
			for (int i = 0; i < CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues()
					.size(); i++) {
				total += CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues().get(i);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		rndVal = total * Math.random();
		while (!found) {
			accumulated += CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues()
					.get(value);
			if (rndVal <= accumulated)
				found = true;
			else
				value++;

			if (value >= CustomerProfileList.get(custProfInd).getScoreAttributes().get(attrInd).getScoreValues().size())
				throw new Exception("Error 1 in chooseValueForAttribute() method: Value not found");
		}

		if (!found)
			throw new Exception("Error 2 in chooseValueForAttribute() method: Value not found");
		return value;
	}
	

	/** Creating available attributes for the producer */
private static ArrayList<Attribute> createAvailableAttributes() {
		ArrayList<Attribute> availableAttributes = new ArrayList<>();
		int limit = Number_Attributes * KNOWN_ATTRIBUTES / 100;

		/* All producers know the first ATTRIBUTES_KNOWN % of the attributes */
		for (int i = 0; i < limit - 1; i++) {
			Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(),
					TotalAttributes.get(i).getMAX());
			ArrayList<Boolean> values = new ArrayList<>();
			for (int j = 0; j < attr.getMAX(); j++) {
				values.add(true);
			}

			attr.setAvailableValues(values);
			availableAttributes.add(attr);
		}

		/*
		 * The remaining attributes are only known by SPECIAL_ATTRIBUTES %
		 * producers
		 */
		for (int k = limit; k < TotalAttributes.size(); k++) {
			Attribute attr = new Attribute(TotalAttributes.get(k).getName(), TotalAttributes.get(k).getMIN(),
					TotalAttributes.get(k).getMAX());
			ArrayList<Boolean> values = new ArrayList<>();

			for (int j = 0; j < attr.getMAX(); j++) {
				double rnd = Math.random();
				double rndVal = Math.random();
				/*
				 * Furthermore, with a 50% of probabilities it can know this
				 * attribute
				 */
				if (rndVal < (SPECIAL_ATTRIBUTES / 100) && rnd < 0.5)
					values.add(true);
				else
					values.add(false);
			}
			attr.setAvailableValues(values);
			availableAttributes.add(attr);
		}

		return availableAttributes;
	}

	/** Creating a random product */
	private static Product createRndProduct(ArrayList<Attribute> availableAttribute) {
		Product product = new Product(new HashMap<Attribute, Integer>(), 0);
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		int attrVal = 0;

		for (int i = 0; i < limit - 1; i++) {
			attrVal = (int) (TotalAttributes.get(i).getScoreValues().get((int) Math.random()));
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);

		}

		for (int i = limit; i < Number_Attributes - 1; i++) {
			boolean attrFound = false;
			while (!attrFound) {
				attrVal = (int) ((int) TotalAttributes.get(i).getScoreValues().get((int) Math.random()));
				if (availableAttribute.get(i).getAvailableValues().get(attrVal))
					attrFound = true;
			}
			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
		}
		return product;
	}

	/** Creating a product near various customer profiles */
private static Product createNearProduct(ArrayList<Attribute> availableAttribute, int nearCustProfs) {
		/* TODO: improve having into account the sub-profiles */
		Product product = new Product(new HashMap<Attribute, Integer>(), 0);
		int limit = (Number_Attributes * KNOWN_ATTRIBUTES) / 100;
		int attrVal = 0;
		ArrayList<Integer> custProfsInd = new ArrayList<Integer>();

		for (int i = 1; i < nearCustProfs; i++) {
			custProfsInd.add((int) Math.floor(Number_CustomerProfile * Math.random()));
		}
		for (int i = 0; i < Number_Attributes - 1; i++) {
			attrVal = chooseAttribute(i, custProfsInd, availableAttribute);

			product.getAttributeValue().put(TotalAttributes.get(i), attrVal);

		}
		return product;
	}

	private static Product createProduct(ArrayList<Attribute> availableAttrs) {

		Product product = new Product(new HashMap<Attribute, Integer>(), 0);
		ArrayList<Integer> customNearProfs = new ArrayList<>();
		for (int i = 0; i < NEAR_CUST_PROFS; i++) {
			customNearProfs.add((int) Math.floor(CustomerProfileList.size() * Math.random()));
		}

		HashMap<Attribute, Integer> attrValues = new HashMap<>();

		for (int j = 0; j < availableAttrs.size(); j++) {
			attrValues.put(availableAttrs.get(j), chooseAttribute(j, customNearProfs, availableAttrs)); // TotalAttributes.get(j)
																										// o
																										// availableAttrs.get(j)
		}
		product.setAttributeValue(attrValues);
		return product;
	}

	/** Chosing an attribute near to the customer profiles given */
	private static int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd,
			ArrayList<Attribute> availableAttrs) {
		int attrVal;
		int possible;
		int nuevo = 0;
		ArrayList<Integer> possibleAttr = new ArrayList<>();

		for (int i = 0; i < availableAttrs.get(attrInd).getAvailableValues().size(); i++) {
			/*
			 * We count the valoration of each selected profile for attribute
			 * attrInd value i
			 */
			possibleAttr.add(0);
			for (int j = 0; j < custProfInd.size(); j++) {
				
				possible = possibleAttr.get(i);
				nuevo =  possible + CustomerProfileList.get(custProfInd.get(j)).getScoreAttributes().get(attrInd)
						.getScoreValues().get(i);

			}
			possibleAttr.set(i, nuevo);
		}
		attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);

		return attrVal;
	}

	/**
	 * Chosing the attribute with the maximum score for the customer profiles
	 * given
	 */
	private static int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr) {
		int attrVal = -1;
		int max = -1;
		for (int i = 0; i < possibleAttr.size(); i++) {
			if (availableAttr.get(attrInd).getAvailableValues().get(i) && possibleAttr.get(i) > max) {
				max = possibleAttr.get(i);
				attrVal = i;
			}
		}
		return attrVal;
	}

	/*************************************** " AUXILIARY METHODS SOLVEPD_GA()" ***************************************/

	/**
	 * Creating the initial population
	 * 
	 * @throws Exception
	 */
	private static void createInitPopu() throws Exception {
		Population = new ArrayList<>();
		Fitness = new ArrayList<>();

		Population.add((Producers.get(0).getProduct()).clone());
		Fitness.add(computeWSC(Population.get(0), 0));
		BestWSC = Fitness.get(0);
		Initial_Results.add(BestWSC);

		for (int i = 1; i < NUM_POPULATION; i++) {
			if (i % 2 == 0) /* We create a random product */
				Population.add(createRndProduct(Producers.get(0).getAvailableAttribute()));
			else /* We create a near product */
				Population.add(createNearProduct(Producers.get(0).getAvailableAttribute(),
						(int) ((Number_CustomerProfile * Math.random()) + 1))); ///////// ?.getAttributeValue().get(i)?verificar//////////

			Fitness.add(computeWSC(Population.get(i), 0));
			if (Fitness.get(i) > BestWSC) {
				BestWSC = Fitness.get(i);
				@SuppressWarnings("unused")
				Product prod = Producers.get(0).getProduct();
				prod = (Population.get(i)).clone();
			}

		}

	}

	/***
	 * Computing the weighted score of the producer prodInd is the index of the
	 * producer
	 * 
	 * @throws Exception
	 **/
	private static int computeWSC(Product product, int prodInd) throws Exception {
		int wsc = 0;
		boolean isTheFavourite;
		int meScore;
		int score;
		int k;
		int numTies;
		for (int i = 0; i < Number_CustomerProfile; i++) {
			for (int j = 0; j < CustomerProfileListAux.size(); j++) // CustomerProfileListAux.get(i).getScoreAttributes().size()
			{
				isTheFavourite = true;
				numTies = 1;
				meScore = scoreProduct(i, j, product);
				k = 0;
				while (isTheFavourite && k < Number_Producers) {
					if (k != prodInd) {
						score = scoreProduct(i, j, Producers.get(k).product);
						if (score > meScore)
							isTheFavourite = false;
						else if (score == meScore)
							numTies += 1;
					}
					k++;
				}
				/*
				 * TODO: When there exists ties we loose some voters because of
				 * decimals (undecided voters)
				 */
				if (isTheFavourite) {
					if ((j == (CustomerProfileListAux.size() - 1))
							&& ((NumberCustomerProfile.get(i) % RESP_PER_GROUP) != 0)) // .get(i).getScoreAttributes()
					{
						wsc += (NumberCustomerProfile.get(i) % RESP_PER_GROUP) / numTies;
					} else {
						wsc += RESP_PER_GROUP / numTies;
					}
				}

			}
		}

		return wsc;
	}

	/**
	 * Computing the score of a product given the customer profile index
	 * custProfInd and the product
	 */
	private static int scoreProduct(int custProfInd, int custSubProfInd, Product product) throws Exception {
		int score = 0;
		for (int i = 0; i < TotalAttributes.size(); i++) {

			//try {
				score += scoreAttribute(
						TotalAttributes.get(i).getScoreValues().size(), CustomerProfileListAux
								.get(custProfInd).getScoreAttributes().get(custSubProfInd).getScoreValues().get(i),
						product.getAttributeValue().get(i));//////////
				// score += scoreAttribute(mAttributes(i),
				// mCustProfAux(custProfInd)(custSubProfInd)(i), product(i))

			/*} catch (Exception e) {
				System.out.println(e);
			}*/
		}
		return score;
	}

	/**
	 * Computing the score of an attribute for a product given the ' number of
	 * values
	 */
	private static int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception {
		int score = 0;
		switch (numOfValsOfAttr) {
		case 2: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else
				score = 0;
		}
			break;
		case 3: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 5;
			else
				score = 0;
		}
			break;
		case 4: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 6;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2)
				score = 2;
			else
				score = 0;
		}
			break;
		case 5: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 6;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2)
				score = 2;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3)
				score = 1;
			else
				score = 0;
		}
			break;
		case 11: {
			if (valOfAttrCust == valOfAttrProd)
				score = 10;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1)
				score = 8;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2)
				score = 6;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3)
				score = 4;
			else if (Math.abs(valOfAttrCust - valOfAttrProd) == 4)
				score = 2;
			else
				score = 0;
		}
			break;
		default:
			throw new Exception(
					"Error in scoreAttribute() function: " + "Number of values of the attribute unexpected");
		}
		return score;
	}

	/** Creating a new population */
	private static ArrayList<Product> createNewPopu(ArrayList<Integer> newFitness) throws Exception {
		int fitnessSum = computeFitnessSum();
		ArrayList<Product> newPopu = new ArrayList<Product>();
		int father;
		int mother;
		Product son; // ArrayList<Product>

		// newFitness = new ArrayList<Integer>();
		for (int i = 0; i < NUM_POPULATION; i++) {
			father = chooseFather(fitnessSum);
			mother = chooseFather(fitnessSum);
			son = mutate(breed(father, mother));

			newPopu.add(son); //////// verificar//////////son.get(i)
			newFitness.add(computeWSC(newPopu.get(i), 0));
		}

		return newPopu;
	}

	/** Computing the sum of the fitness of all the population */
	private static int computeFitnessSum() {
		int sum = 0;
		for (int i = 0; i < Fitness.size(); i++) {
			sum += Fitness.get(i);
		}
		return sum;
	}

	/** Chosing the father in a random way taking into account the fitness */
	private static int chooseFather(double fitnessSum) {
		int fatherPos = 0;
		double rndVal = fitnessSum * Math.random();
		double accumulator = Fitness.get(fatherPos);
		while (rndVal > accumulator) {
			fatherPos += 1;
			accumulator += Fitness.get(fatherPos);
		}
		return fatherPos;
	}

	/**
	 * Método que dado un padre y una madre los cruza para obtener un hijo. Para
	 * cada posición del array eligiremos aleatoriamente si el hijo heredará esa
	 * posición del padre o de la madre.
	 */
	private static Product breed(int father, int mother) {
		Product son = new Product(new HashMap<Attribute, Integer>(), 0); // ArrayList<Product>
																								// son
																								// =
																								// new
																								// ArrayList<Product>();
		/* Random value in range [0,100) */
		double crossover = 100 * Math.random();
		int rndVal;

		if (crossover <= CROSSOVER_PROB) {
			for (int i = 0; i < Fitness.size(); i++) // With
																			// son
			{
				for (int j = 0; j < Number_Attributes; j++) {
					rndVal = (int) (2
							* Math.random()); /*
												 * Generamos aleatoriamente un 0
												 * (padre) o un 1 (madre).
												 */
					if (rndVal == 0)
						son.setFitnessProduct(Population.get(father).getFitnessProduct()); // .getValuesPopuProduct().get(j)
					else
						son.setFitnessProduct(Population.get(mother).getFitnessProduct()); // .getValuesPopuProduct().get(j)
																												// //son.add(Population.get(mother));
				}
			}
		} else {
			rndVal = (int) (2 * Math.random()); /*
												 * Generamos aleatoriamente un 0
												 * (padre) o un 1 (madre).
												 */
			if (rndVal == 0)
				son = Population.get(father).clone();
			else
				son = Population.get(mother).clone();
		}
		return son;
	}

	/**
	 * Method that creates an individual parameter passed mutating individual.
	 * The mutation is to add / remove a joint solution.
	 * 
	 * @throws Exception
	 */
	private static Product mutate(Product indiv) {
		Product mutant = new Product(); // ArrayList<Product> mutant = new
										// ArrayList<Product>();
		double mutation;
		int attrVal = 0;

		mutant = indiv.clone();

		for (int i = 0; i < Fitness.size(); i++)// with
																			// mutant
			for (int j = 0; j < Number_Attributes; j++) {
				/* Random value in range [0,100) */
				mutation = 100 * Math.random();
				if (mutation <= MUTATION_PROB) {
					boolean attrFound = false;
					while (!attrFound) {
						attrVal = (int) (Math.floor(TotalAttributes.get(j).getScoreValues().size() * Math.random()));
						if (Producers.get(0).getAvailableAttribute().get(j).getAvailableValues().get(attrVal))
							attrFound = true;
					}
					// .Item(i) = attrVal
					// int it = mutant.get(j);
					// it = attrVal;
				}

			}
		return mutant;
	}

	/**
	 * Método que dada la población original y una nueva población elige la
	 * siguente ' generación de individuos. Actualizo la mejor solución
	 * encontrada en caso de mejorarla.
	 */
	private static ArrayList<Product> tournament(ArrayList<Product> newPopu, ArrayList<Integer> newFitness) {

		ArrayList<Product> nextGeneration = new ArrayList<Product>();
		for (int i = 0; i < NUM_POPULATION; i++) {
			if (Fitness.get(i) >= newFitness.get(i))
				nextGeneration.add((Population.get(i)).clone());
			else
				nextGeneration.add((newPopu.get(i)).clone());
			int fit = Fitness.get(i);
			fit = newFitness.get(i); // We update the fitness of the new
										// individual
			if (newFitness.get(i) > BestWSC) {
				BestWSC = newFitness.get(i);
				Product product = Producers.get(0).getProduct();
				product = (newPopu.get(i)).clone();
			}
		}
		return nextGeneration;
	}

	/**
	 * Showing the wsc of the rest of products
	 * 
	 * @throws Exception
	 */
	private static void showWSC() throws Exception {
		int wsc;
		int wscSum = 0;
		// int custSum = 0;
		for (int i = 0; i < Number_Producers; i++) {
			wsc = computeWSC(Producers.get(i).getProduct(), i);
			wscSum += wsc;
		}
		/*
		 * for(int j = 0; j < Number_CustomerProfile - 1; j++) { custSum +=
		 * NumberCustomerProfile.get(j); }
		 * 
		 * if(wscSum != custSum) throw new Exception("Error in showWSC() method"
		 * );
		 */

	}

	/*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

	/** Computing the variance */
	private static double computeVariance(double mean) {
		double sqrSum = 0;
		for (int i = 0; i < NUM_EXECUTIONS; i++) {
			sqrSum += Math.pow(Results.get(i) - mean, 2);
		}
		return (sqrSum / NUM_EXECUTIONS);
	}

}