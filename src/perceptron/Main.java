package perceptron;

import java.io.FileInputStream;
import java.util.Properties;
import utils.Tool;
import utils.HumanDO;
public class Main {
	/**
	 * 
	 * @param args -traintestEval the properties file
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception{		
		if (args.length < 1) {
			SNThelp();
			return;
		}		
		FileInputStream fis = new FileInputStream(args[1]);
		Properties properties = new Properties();
		properties.load(fis);
		fis.close();
		if (args[0].trim().equals("-traintestEval")) {			
			// training, test and evaluation simultaneously			
            String train_file = properties.getProperty("trainFile");
			String dev_file = properties.getProperty("devFile");
			String test_file = properties.getProperty("testFile");
			String model_file =properties.getProperty("modelFile");
			String output_path = properties.getProperty("outPath");			
			int number_of_iterations = Integer.parseInt(properties.getProperty("number_of_iterations"));
			String sense_file = properties.getProperty("dictFile");
			int search_width = Integer.parseInt(properties.getProperty("search_width"));
			String log_file = properties.getProperty("logFile");	
			boolean bNewTrain = Boolean.parseBoolean(properties.getProperty("bNewTrain"));
			String trainPOS = properties.getProperty("trainPOS");		
			String testPOS = properties.getProperty("testPOS");				
			String CTDdisease = properties.getProperty("CTDTrainDevDisease");
			String  bigram_file = properties.getProperty("bigramFile");
			
			int number_of_train = -1;
			int number_of_test = -1;
			int number_of_dev = -1;
			
			Tool tool = new Tool();
			tool.trainPOS = trainPOS;
			tool.testPOS = testPOS;
			tool.CTDdisease =new HumanDO(CTDdisease);	
	
			JointRN rn = new JointRN(train_file, number_of_train, model_file,
					number_of_iterations, bNewTrain, search_width, test_file,
					number_of_test, dev_file, number_of_dev, output_path,
					sense_file, log_file, bigram_file,tool);
		
		  rn.trainDevTestProcess();
		} else {
			SNThelp();
		}
	}
	
	private static void SNThelp() {
		System.out.println("parameter error, please input again!");
		System.out.println("-traintestEval  <train_file> <dev_file> <test_file> <model_file> <output_path> <number of iterations> <search_width> <dic_file> <log_file> <charlm_file> <wordlm-file> <bNewTrain>");
		return;
	}
}
