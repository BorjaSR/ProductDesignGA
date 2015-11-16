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
	static final int KNOWN_ATTRIBUTES = 100; /* % of attributes known for all producers */
	static final int SPECIAL_ATTRIBUTES = 33; /* % of special attributes known for some producers */
	static final int MUT_PROB_CUSTOMER_PROFILE = 33; /* % of mutated attributes in a customer profile */
	static final int CROSSOVER_PROB = 80; /* % of crossover */
	static final int MUTATION_PROB = 1; /* % of mutation */
	static final int NUM_GENERATIONS = 100; /* number of generations */
	static final int NUM_POPULATION = 20; /* number of population */
	static final int RESP_PER_GROUP = 20; /* We divide the respondents of each profile in groups of RESP_PER_GROUP respondents */
	static final int NUM_EXECUTIONS = 20; /* number of executions */
	
	//static final String SOURCE = "D:\Pablo\EncuestasCIS.xlsx";
    static final int  SHEET_AGE_STUDIES = 1;
    static final int SHEET_POLITICAL_PARTIES = 2;
    static final String EOF = "EOF";
    
    private HashMap<Attribute,Integer> TotalAttribute;
	private LinkedList<Producer> Producers;
	
	/*INPUT VARIABLES*/
	private int Number_Attributes; /*Number of attributes*/
	private int Number_Producers; /*Number of producers*/
	private int Number_CustomerProfile; /*Number of customer profiles*/
	
	
	/*GA VARIABLES*/
	private int BestWSC; /*Stores the best wsc found*/
   // private HashMap<,Integer> Population;   Private mPopu As List(Of List(Of Integer))
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
      

	public static void main(String[] args) throws IOException {
		// An excel file name. You can create a file name with a full path information.
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
			 
			/* When we have a sheet object in hand we can iterator on each sheet's rows and on each row's cells. 
			 We store the data read on an ArrayList so that we can printed the content of the excel to the console. */
			 Iterator rows = sheet.rowIterator();
			 while (rows.hasNext()) {
				 XSSFRow row = (XSSFRow) rows.next();
				 Iterator cells = row.cellIterator();
				 List data = new ArrayList();
				 while (cells.hasNext()) {
					 XSSFCell cell = (XSSFCell) cells.next();
					//  System.out.println("Añadiendo Celda: " + cell.toString());
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
		 showExcelData(sheetData);
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
	
	/**Computing the weighted score of the producer
       prodInd is the index of the producer*/
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
	
	/** Computing the score of a product given the customer profile index
        custProfInd and the product*/
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
