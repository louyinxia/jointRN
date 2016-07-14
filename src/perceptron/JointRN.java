package perceptron;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import perceptron.State;
import utils.Tool;
import utils.Group;
//Joint Model For recognition and Normalization 
public class JointRN {
	//-traintestEval E:/test/trainData E:/test/testData E:/test/testData E:/test/out/out_1031/model E:/test/out/out_1031// 60 E:/test/dict 16 log " " " " true
	public Double MINVALUE = -Double.MAX_VALUE;
	public String train_file = "";
	public String dev_file = "";
	public String test_file = "";
	public String output_file = "out";
	public String model_file = "";
	public String sense_file = ""; // normalization dictionary
	public String lmChar_file = ""; // Char-based language model
	public String lmWord_file = ""; // word-base language model
	public String evaluationError_file = "erresult";
	public int number_of_iterations = 10;
	public boolean bNewTrain = true;
	public String output_path = "";
	public int number_of_test = 0;
	public int number_of_dev = 0;
	public int number_of_train = 0;
	public int search_width = 16;	
	
	public HashMap<String, Integer> hsngramChar; // char lm
	public HashMap<String, Integer> hsngramWord; // word lm

	public List<String> arrTrainSource; // training sentences
	public List<State> bestStates;// best states
	public List<Integer> goldActions = null;
	public State goldSentenceState = null;

	public List<String> arrTestSource; // test sentences
	public List<String> arrTestResult;

	public List<String> arrDevSource; // developing sentences
	public List<String> arrDevResult;

	int curRoundIndexForTrain = 0;
	int preRoundIndexForTrain = 0;
	public Model model = new Model(); // model
	int curTrainIterCorrectInstance = 0;

	private String CurSentence = ""; // current sentence
	public State[] agenda; // state set

	public BufferedWriter bwlog;
	public SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss SSS ");
	HeapSort heapSort = new HeapSort();
	
	//features
	public String train_POS_file = "";
//	public String dev_POS_file = "";
    public String test_POS_file = "";
    public String train_stem_file = "";
    public String test_stem_file = "";
    public List<String> diseaseAbbres;
	public List<String> trainTokenPOSSentences; // train token POS
//	public List<String> devTokenPOSSentences;
	public List<String> testTokenPOSSentences; // test token POS
	public List<String> trainTokenStemSentences;// stem sentences of train
	public List<String> testTokenStemSentences; 
	public List<String> trainPosArray; //the POS of each train sentence
	public List<String> testPosArray; //the POS of each test sentence
//	public List<String> devPosArray; //	
	public List<String> trainStemArray; //the stem of each train sentence
	public List<String> testStemArray; //the stem of each test sentence
//	public List<String> devStemArray; // the stem of eash dec sentence
	public Tool tool ;
	
//	public static int MAXNUM = 5000;
	public HashMap<String, String> hmWordSenseSet = new HashMap<String, String>();
	public boolean IsSPModel = true; // true for joint segmentation and pos
										// tagging model. false for joint
										// segmentation, pos tagging and
										// normalization
	public boolean isTrain = true;

	/**
	 * initial training
	 * 
	 * @param train_file
	 * @param number_of_train
	 * @param model_file
	 * @param number_of_iterations
	 * @param bNewTrain
	 * @param search_width
	 * @param outfile_path
	 * @param sense_file
	 * @param out_path
	 * @param lmChar_file
	 * @param lmWord_file
	 */
	public JointRN(String train_file, int number_of_train, String model_file,
			int number_of_iterations, boolean bNewTrain, int search_width,
			String outfile_path, String sense_file, String out_path,
			String lmChar_file, String lmWord_file,Tool tool) {
		this.train_file = train_file;
		this.number_of_train = number_of_train;
		this.model_file = model_file;
		this.number_of_iterations = number_of_iterations;
		this.bNewTrain = bNewTrain;
		this.search_width = search_width;
		this.output_path = out_path;
		this.sense_file = sense_file;
		this.lmChar_file = lmChar_file;
		this.lmWord_file = lmWord_file;
		try {
			bwlog = new BufferedWriter(new FileWriter(this.output_path
					+ "log.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public JointRN() {
	}

	/**
	 * initial
	 * 
	 * @param test_file
	 * @param model_file
	 * @param output_file
	 * @param evaluationError_file
	 * @param search_width
	 * @param sense_file
	 * @param out_path
	 * @param lm_file
	 * @param lmWord_file
	 */
	public JointRN(String test_file, String model_file, String output_file,
			String evaluationError_file, int search_width, String sense_file,
			String out_path, String lm_file, String lmWord_file,Tool tool) {
		this.test_file = test_file;
		// this.number_of_test = number_of_test;
		this.model_file = model_file;
		this.output_file = output_file;
		this.evaluationError_file = evaluationError_file;
		this.search_width = search_width;
		this.sense_file = sense_file;
		this.output_path = out_path;
		this.lmChar_file = lmChar_file;
		this.lmWord_file = lmWord_file;
		try {
			bwlog = new BufferedWriter(new FileWriter(this.output_path
					+ "log.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * initial
	 * 
	 * @param test_file
	 * @param model_file
	 * @param output_file
	 * @param evaluationError_file
	 * @param search_width
	 * @param sense_file
	 * @param out_path
	 * @param lm_file
	 * @param lmWord_file
	 */
	public JointRN(String train_file, int number_of_train, String model_file,
			int number_of_iterations, boolean bNewTrain, int search_width,
			String test_file, int number_of_test, String dev_file,
			int number_of_dev, String output_path, String sense_file,
			String log_file,String bigram_file, Tool tool) {
		this.train_file = train_file;
		this.number_of_train = number_of_train;
		this.model_file = model_file;
		this.number_of_iterations = number_of_iterations;
		this.bNewTrain = bNewTrain;
		this.search_width = search_width;
		this.test_file = test_file;
		this.number_of_test = number_of_test;
		this.output_path = output_path;
		this.dev_file = dev_file;
		this.number_of_dev = number_of_dev;
		this.sense_file = sense_file;
		this.lmWord_file = bigram_file;
		this.tool = new Tool();
		
		this.tool.CTDdisease = tool.CTDdisease; //before tool must be have "this"
		this.train_POS_file = tool.trainPOS;
//		this.dev_POS_file = tool.devPOS;
		this.test_POS_file = tool.testPOS;
		this.train_stem_file = tool.trainStem;
		this.test_stem_file = tool.testStem;
		this.trainTokenPOSSentences = new ArrayList<String>();
//		this.devTokenPOSSentences = new ArrayList<String>();
	    this.testTokenPOSSentences = new ArrayList<String>();
	    this.trainTokenStemSentences = new ArrayList<String>();
	    this.testTokenStemSentences = new ArrayList<String>();
	    
	    this.trainPosArray = new ArrayList<String>();
//	    this.devPosArray = new ArrayList<String>();
	    this.testPosArray = new ArrayList<String>();	
	    this.trainStemArray = new ArrayList<String>();
	    this.testStemArray = new ArrayList<String>();
	    this.tool.brownCluster = tool.brownCluster;
	    this.tool.wcr = tool.wcr;
	    this.tool.commonDiseaseNames = new Group();
		try {
			bwlog = new BufferedWriter(new FileWriter(output_path + log_file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * training, developing, test and evaluation simultaneously
	 * @throws Exception
	 */
	public void trainDevTestProcess() throws Exception {		
		initialSense();
		initialTrain();
		initialTest();
		initialDev();
		initialLM();
		initialPOSOrStem();
	
		curRoundIndexForTrain = preRoundIndexForTrain;
		for (int n = 0; n < number_of_iterations; n++) {
			bwlog.write("train round begin:" + n + "  start "
					+ df.format(new Date()) + "\r\n");
			curTrainIterCorrectInstance = 0;
			for (int i = 0; i < this.arrTrainSource.size(); i++) {				
				
				this.CurSentence = UnTagSentence(this.arrTrainSource.get(i));
				this.goldActions = getGoldActions(this.arrTrainSource.get(i));
				
				this.trainPosArray.clear();	
				this.trainPosArray = setTokenPosArray(i,this.trainTokenPOSSentences); //set tokenPosArray of each sentence
				
				this.trainStemArray.clear();
				this.trainStemArray = setTokenStemArray(i,this.trainTokenStemSentences);

				if(new StringTokenizer(this.CurSentence," ").countTokens() != goldActions.size() -1){	
					System.out.println("error...");
				}
				trainer(n, i);
			}
			model.AveWeight(curRoundIndexForTrain);
			model.save(model_file + n);
			bwlog.write("train round end:" + n + "   " + df.format(new Date())
					+ "\r\n");
			bwlog.write("develop start:" + "\r\n");
			// development data
			/*this.arrDevResult.clear();
			for (int i = 0; i < arrDevSource.size(); i++) {

				this.CurSentence = UnTagSentence(this.arrDevSource.get(i));
				this.devPosArray.clear();
				this.devPosArray = setTokenPosArray(i,this.devTokenPOSSentences);
				this.arrDevResult.add(Decoder());
			}
			save(this.arrDevResult, output_path + this.output_file + n);

			Evaluator eva = new Evaluator(this.arrDevResult, arrDevSource,
					bwlog, this.output_path + evaluationError_file + n);
			eva.diseaseComputer();	
			bwlog.write("develop end:" + "\r\n");
			bwlog.write("test start:" + "\r\n");*/
			// test data
			this.arrTestResult.clear();
			this.isTrain = false;
			for (int i = 0; i < arrTestSource.size(); i++) {
				this.CurSentence = UnTagSentence(this.arrTestSource.get(i));
				
				this.testPosArray.clear();	
				this.testPosArray = setTokenPosArray(i,this.testTokenPOSSentences);
				
				this.testStemArray.clear();
				this.testStemArray = setTokenPosArray(i,this.testTokenStemSentences);
				this.arrTestResult.add(Decoder());
			}
			save(this.arrTestResult, output_path + this.output_file + n);
			Evaluator eva_2 = new Evaluator(this.arrTestResult, arrTestSource, bwlog,
					this.output_path + evaluationError_file + n);

			eva_2.diseaseComputer();			

			bwlog.write("test end:" + "\r\n");
			bwlog.flush();
		}
		model.save(model_file);
		bwlog.flush();
		bwlog.close();
	}

	/**
	 * train process
	 * 
	 * @throws Exception
	 */
	public void trainProcess() throws Exception {
		initialTrain();
		this.initialSense();
		curRoundIndexForTrain = preRoundIndexForTrain;
		for (int n = 0; n < number_of_iterations; n++) {
			curTrainIterCorrectInstance = 0;
			for (int i = 0; i < arrTrainSource.size(); i++) {
				this.CurSentence = UnTagSentence(this.arrTrainSource.get(i));
				this.goldActions = getGoldActions(this.arrTrainSource.get(i));
				trainer(n, i);
			}
			model.AveWeight(curRoundIndexForTrain);
		}
		model.save(model_file);
	}

	/**
	 * test process
	 */
	public void testProcess() {
		try {
			initialTest();
			this.initialSense();
			this.arrTestResult.clear();
			this.model.load(model_file);
			for (int i = 0; i < arrTestSource.size(); i++) {
				this.CurSentence = UnTagSentence(this.arrTestSource.get(i));
				this.arrTestResult.add(Decoder());
			}
			save(this.arrTestResult, this.output_file);
			Evaluator eva = new Evaluator(this.arrTestResult, arrTestSource,
					bwlog, this.output_path + this.evaluationError_file);
			eva.diseaseComputer();

			bwlog.flush();
			bwlog.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * test process, no evaluation
	 */
	public void testNoEvalProcess() {
		try {
			initialTest();
			this.initialSense();
			this.model.load(model_file);
			this.arrTestResult.clear();
			for (int i = 0; i < arrTestSource.size(); i++) {				
				this.CurSentence = this.arrTestSource.get(i);
				this.arrTestResult.add(Decoder());
			}
			save(this.arrTestResult, this.output_file);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 *  POS feature and stem feature	
	 */
	public void initialPOSOrStem(){
		initialTokenPOSOrStem(this.train_POS_file, this.trainTokenPOSSentences);
//	    initialTokenPOSOrStem(this.dev_POS_file, this.devTokenPOSSentences);
	    initialTokenPOSOrStem(this.test_POS_file, this.testTokenPOSSentences);
	    initialTokenPOSOrStem(this.train_stem_file, this.trainTokenStemSentences);
//	    initialTokenPOSOrStem(this.dev_Stem_file, this.devTokenStemSentences);
	    initialTokenPOSOrStem(this.test_stem_file, this.testTokenStemSentences);  
	}

	/**
	 * initial pos
	 */
	private void initialTokenPOSOrStem(String filePath, List<String> tokenList){		
		File file = new File(filePath);
		BufferedInputStream fis;
		try{
			fis = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF8"));
			String line = "";
			while((line=reader.readLine())!= null){
				if(line.trim().length()>0){
					tokenList.add(line);
				}
			}
		}catch(Exception e){
			e.printStackTrace();			
		}		
	}
	/**
	 * set TokenPosArray of each sentence
	 */
	public List<String> setTokenPosArray(int i, List<String> sentences){
		List<String> posArray = new ArrayList<String>();
		String currentTrainToken = sentences.get(i);
		String[] tokenPoses = currentTrainToken.split(" ");
		for(int j=0; j<tokenPoses.length; j++){
			String temp = tokenPoses[j];
			posArray.add(temp);			
		}
		return posArray;		
	}
	/**
	 * set TokenPosArray of each sentence
	 */
	public List<String> setTokenStemArray(int i, List<String> sentences){
		List<String> posArray = new ArrayList<String>();
		String currentTrainToken = sentences.get(i);
		String[] tokenPoses = currentTrainToken.split(" ");
		for(int j=0; j<tokenPoses.length; j++){
			String temp = tokenPoses[j];
			posArray.add(temp);			
		}
		return posArray;		
	}

	/**
	 * initial test
	 */
	private void initialTest() {	
		this.arrTestSource = new ArrayList<String>();
		this.arrTestResult = new ArrayList<String>();
		File file = new File(this.test_file);
		BufferedInputStream fis;
		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis, "UTF8"));
			String line = "";
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (this.number_of_test > 0 && i >= this.number_of_test)
					break;
				if (line.trim().length() > 0) {
					this.arrTestSource.add(line.trim());
					i++;
				}
			}
			reader.close();
			this.number_of_test = this.arrTestSource.size();
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * initial develop data
	 */
	private void initialDev() {		
		this.arrDevSource = new ArrayList<String>();
		this.arrDevResult = new ArrayList<String>();
		File file = new File(this.dev_file);
		BufferedInputStream fis;
		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis, "UTF8"));
			String line = "";
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (this.number_of_dev > 0 && i >= this.number_of_dev)
					break;
				if (line.trim().length() > 0) {
					this.arrDevSource.add(line.trim());
					i++;
				}
			}
			reader.close();
			this.number_of_dev = this.arrDevSource.size();
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *  initial training
	 */
	private void initialTrain() {
		this.arrTrainSource = new ArrayList<String>();
		if (this.bNewTrain == true) {
			this.model.newFeatureTemplates();
			this.model.init(this.train_file, bNewTrain);
		} else {
			this.preRoundIndexForTrain = this.model.load(model_file);
			this.model.init(this.train_file, bNewTrain);
		}
		File file = new File(this.train_file);
		BufferedInputStream fis;
		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis, "UTF8"));
			String line = "";
			int i = 0;

			while ((line = reader.readLine()) != null) {
				if (this.number_of_train > 0 && i >= this.number_of_train)
					break;
				if (line.trim().length() > 0) {
					this.arrTrainSource.add(line.trim());
					i++;
				}
			}
			reader.close();
			fis.close();
			this.number_of_train = this.arrTrainSource.size();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * trainer
	 * @param round
	 * @param sentenceIndex
	 * @throws Exception
	 */	 
	public void trainer(int round, int sentenceIndex) throws Exception {
		this.agenda = new State[this.search_width];
		this.agenda[0] = new State();
		this.agenda[0].bIsGold = true;
		long st1 = System.nanoTime();
		curRoundIndexForTrain++;
		String[] arrCurWord = this.CurSentence.split(" ");
		int wordlen = arrCurWord.length;

		for (int i = 0; i <= wordlen; i++) {
			String curSChar = "";
			if (i < wordlen){
				curSChar = arrCurWord[i];	
			}
			int agendaLen = 1;
			if (i == 0)
				agendaLen = 1;
			else
				agendaLen = agenda.length;
			State[] temAgenda = new State[this.search_width];
			for (int o = 0; o < this.search_width; o++) {
				temAgenda[o] = new State();
				temAgenda[o].score = MINVALUE;
				temAgenda[o].bIsGold = false;
				temAgenda[o].curIndex = i;
               			
			}
			long st2 = System.nanoTime();
			for (int j = 0; j < agendaLen; j++) {
				agenda[j].curIndex = i;
				
				State state = agenda[j];
				
				if (i > 0 && state.bStart)//i>0, state.bStart=true,这是不正确的，正常的是i>0, state.bStart等于false
					continue;

				ArrayList<String> arrSenseSet = new ArrayList();
				if (state.size >= 1) {
					arrSenseSet = GetNormalSetByWord(state.arrWord[state.size - 1]);
				}
				if (i > 0 && i < wordlen) {//middle
					if(state.size>=1 && state.arrTag[state.size-1].equals("Y")){
						State tempCands = Append(state, curSChar, null, false); 																			
						if (tempCands.score > temAgenda[0].score) {
							if (state.bIsGold == true && this.goldActions.get(i) == 1000) {
								tempCands.bIsGold = true;
							} else
								tempCands.bIsGold = false;
							heapSort.BestAgendaSort(temAgenda, tempCands);
						}
					}
				}
				if (i == wordlen) {//finish
					if (arrSenseSet.size() > 0) {
						for (int m = 0; m < arrSenseSet.size(); m++) {
							State temCand = Finish(state, null, false, arrSenseSet.get(m));// end action
							if (temCand.score >= temAgenda[0].score) {
								if (state.bIsGold == true && this.goldActions.get(i) == 2000) {
									if (temCand.size >= 1) {
										if (equalNormal(temCand,this.goldSentenceState, 1) == true) {
											temCand.bIsGold = true;
										} else {
											temCand.bIsGold = false;
										}
									} else {
										temCand.bIsGold = true;
									}
								} else
									temCand.bIsGold = false;
								heapSort.BestAgendaSort(temAgenda, temCand);
							}
						}
					}
				} else {
					for (int k = 0; k < State.arrPOS.length; k++) {
						if (i == 0) {
							{
								State temCand = Sep(state, curSChar, k, null,false, "");
								if (temCand.score >= temAgenda[0].score) {
									if (state.bIsGold == true && this.goldActions.get(i) == k) {
										temCand.bIsGold = true;
									} else
										temCand.bIsGold = false;
									heapSort.BestAgendaSort(temAgenda, temCand);
								}
							}
						}
						
						if (arrSenseSet.size() > 0) {
							for (int m = 0; m < arrSenseSet.size(); m++) {
								State temCand = Sep(state, curSChar, k,
											null, false, arrSenseSet.get(m));// sep action
								if (temCand.score >= temAgenda[0].score) {

										if (state.bIsGold == true
												&& this.goldActions.get(i) == k) {
											if (equalNormal(temCand,
													this.goldSentenceState, 2) == true) {
												temCand.bIsGold = true;
											} else {
												temCand.bIsGold = false;
											}
										} else
											temCand.bIsGold = false;

										heapSort.BestAgendaSort(temAgenda,
												temCand);
									}
								}
							}					
					}
				}
			}
			// System.out.println("1:" + (System.nanoTime() - st2));
			//wi执行完
			st2 = System.nanoTime();
			this.agenda = temAgenda;			
			boolean bEqual = false;
			for (int m = 0; m < this.agenda.length; m++) {
				if (this.agenda[m].bIsGold == true) {
					bEqual = true; //agenda[m] 预测正确，退出for(m)循环，直接预测下一个单词wi+1预测。
					break;
				}
			}

			if (bEqual == false)
			{
				State bestState = Best(this.agenda, 1)[0];
				List<Integer> predActions = bestState.hisActions;
				double bestScore = bestState.score;
				double[] scores = updateParameters(predActions, bestState);//预测不正确，需要进行参数更新
				if (Math.abs(bestScore - scores[0]) > 0.00001) {
//					System.out.println(bestScore+"score not match..."+ scores[0]);
				}
				if (scores[1] - scores[0] > 0.00001) {
//					System.out.println("gold score larger...");
				}
				return;
			}
		}
		//sentence 执行完之后，进行参数更新
		long st2 = System.nanoTime();
		this.agenda = Best(this.agenda, 1);
		if (this.agenda[0].bIsGold == false) {
			List<Integer> predActions = this.agenda[0].hisActions;
			if (predActions.size() != goldActions.size()) {
				System.out.println("action num do not match.....");
			}
			double bestScore = this.agenda[0].score;
			double[] scores = updateParameters(predActions, this.agenda[0]);
			// curRoundIndexForTrain++;
			if (Math.abs(bestScore - scores[0]) > 0.00001) {
//				System.out.println("score not match...");
			}
			if (scores[1] - scores[0] > 0.00001) {
//				System.out.println("gold score larger...");
			}
	} else {
			this.curTrainIterCorrectInstance++;
//			System.out.println(String.format("Corrected : %d",	goldActions.size()));
		}		
	}

	/**
	 * 瑙ｇ爜鍣�
	 * 
	 * @return
	 * @throws Exception
	 */
	public String Decoder() throws Exception {
		this.agenda = new State[this.search_width];
		this.agenda[0] = new State();
		String[] arrCurWord = this.CurSentence.split(" ");
		int wordlen = arrCurWord.length;
		for (int i = 0; i <= wordlen; i++) {
			String curSChar = "";
			if (i < wordlen)
				curSChar = arrCurWord[i];		
			int agendaLen = 1;
			if (i == 0)
				agendaLen = 1;
			else
				agendaLen = agenda.length;

			State[] temAgenda = new State[this.search_width];
			for (int o = 0; o < this.search_width; o++) {
				temAgenda[o] = new State();
				temAgenda[o].score = MINVALUE;
			}
			for (int j = 0; j < agendaLen; j++) {
				State state = agenda[j];
				if (i > 0 && state.bStart)
					continue;

				ArrayList<String> arrSenseSet = new ArrayList<String>(); 
				if (state.size >= 1) {
					arrSenseSet = GetNormalSetByWord(state.arrWord[state.size - 1]);
				}

				if (i > 0 && i < wordlen) {
					if(state.size>=1 && state.arrTag[state.size-1].equals("Y")){					
						State tempCands = Append(state, curSChar, null, true); // append
						if (tempCands.score > temAgenda[0].score) { 
							heapSort.BestAgendaSort(temAgenda, tempCands);
						}
					}
				}
				if (i == wordlen) {
					if (arrSenseSet.size() > 0) {
						for (int m = 0; m < arrSenseSet.size(); m++) {
							State temCand = Finish(state, null, true,
									arrSenseSet.get(m));
							if (temCand.score > temAgenda[0].score) {
								heapSort.BestAgendaSort(temAgenda, temCand);
							}
						}
					}
				} else {
					for (int k = 0; k < State.arrPOS.length; k++) {
						if (i == 0) {
							State temCand = Sep(state, curSChar, k, null, true,
									"");
							if (temCand.score > temAgenda[0].score) {
								heapSort.BestAgendaSort(temAgenda, temCand);
							}
						}
							if (arrSenseSet.size() > 0) {
								for (int m = 0; m < arrSenseSet.size(); m++) {
									State temCand = Sep(state, curSChar, k,
											null, true, arrSenseSet.get(m));
									if (temCand.score > temAgenda[0].score) {
										heapSort.BestAgendaSort(temAgenda, temCand);
									}
								}
							}
					}
				}
			}

			this.agenda = temAgenda; 
		}
		this.agenda = Best(this.agenda, 1);

		return this.agenda[0].toString();
	}

	/**
	 * computing top k labeling sequences.
	 * @param temAgenda
	 * @param k
	 * @return
	 */
	public State[] Best(State[] temAgenda, int k) {
		State[] retAgenda = new State[k];
		HeapSort heapSort = new HeapSort();
		if (search_width > temAgenda.length)
			return temAgenda;
		retAgenda = heapSort.heapSortK(temAgenda, temAgenda.length, k);
		return retAgenda;
	}
    
	/**
	 * Get features
	 * @param state
	 * @param fvs
	 * @param bAverage
	 * @return
	 * e:entity, t:tag, l:label(Y,N), w:word, w_1(w-1):the before of w_0, w1:the after of w_0
	 */
	public double GetLocalFeaturesScore(State state, List<String> fvs,
			boolean bAverage) {
		
		double dScore = 0.0;
		String w_0 = "", w_1 = "", w_2 = "", w1 = "", w2 = "";		
		String e_0 = "", e_1 = "", e_2 = "", l_0 = "", l_1 = "", l_2 = "";
		String start_w_0 = "", end_w_0 = "";
		String start_e_1 = "", end_e_1 = "", end_e_2 = "";
		String n_1 = "", n_2 = "", n_3 = ""; 											
		String start_n_1 = "", end_n_1 = "", end_n_2 = "";
		int len_n_1 = 0, len_n_2 = 0;
		int len_e_0 = 0, len_e_1 = 0, len_e_2 = 0;
		int size = state.size;
		int curIndex = state.curIndex;		
		String normalSent = "";
		List<String> posArray = new ArrayList<String>();
		List<String> stemArray = new ArrayList<String>();
		
		String p_0="", p_1 = "", p_2 ="", s_0 = "", s_1 = "", s_2 = "";
		if(isTrain){
			posArray = this.trainPosArray;
			stemArray = this.trainStemArray;			
		}		
		else {
			posArray = this.testPosArray;
			stemArray = this.testStemArray;
		}
		//initialize p_0, p_1,p_2
	   if(curIndex < posArray.size()){
		   p_0 = posArray.get(curIndex);
		}else{
			p_0 = "*p*";
		}			
		if(curIndex == 0){
			p_1 = "*P*";
			p_2 = "*P*";
		}
		if(curIndex >= 1  && curIndex < posArray.size()){			
			p_1 = posArray.get(curIndex - 1);
		}else{
			p_1 = "*P*";
		}
		if(curIndex >= 2 && curIndex < posArray.size()){			
			p_2 = posArray.get(curIndex - 2);
		}else{
			p_2 = "*P*";
		}
		if(p_0.indexOf("_")!= -1){
			p_0 = p_0.substring(p_0.indexOf("_")+1);
		}
		if(p_1.indexOf("_")!= -1){
			p_1 = p_1.substring(p_1.indexOf("_")+1);
		}
		if(p_2.indexOf("_")!= -1){
			p_2 = p_2.substring(p_2.indexOf("_")+1);
		}
		//initialize s_0, s_1,s_2
		   if(curIndex < stemArray.size()){
			   s_0 = posArray.get(curIndex);
			   s_0 = s_0.substring(s_0.lastIndexOf("_")+1, s_0.length());

			}else{
				s_0 = "*s*";
			}			
			if(curIndex == 0){
				s_1 = "*s*";
				s_2 = "*s*";
			}
			if(curIndex >= 1  && curIndex < stemArray.size()){			
				s_1 = posArray.get(curIndex - 1);
				s_1 = s_1.substring(s_1.lastIndexOf("_")+1, s_1.length());
			}else{
				s_1 = "*s*";
			}
			if(curIndex >= 2 && curIndex < posArray.size()){			
				s_2 = posArray.get(curIndex - 2);
				s_2 = s_2.substring(s_2.lastIndexOf("_")+1, s_2.length());
			}else{
				s_2 = "*s*";
			}	

		if (size > 0) {
			if (state.lastAction != 2000) {
				e_0 = state.arrWord[size - 1];
				l_0 = state.arrTag[size - 1];				
				e_1 = "*S*";
				l_1 = "*T*";
				n_1 = "*S*";
				e_2 = "*S*";
				l_2 = "*T*";
				n_2 = "*S*";
				if (size > 1) {
					e_1 = state.arrWord[size - 2];
					l_1 = state.arrTag[size - 2];
					n_1 = state.arrNormal[size - 2];			
				}
				if (size > 2) {
					e_2 = state.arrWord[size - 3];
					l_2 = state.arrTag[size - 3];
					n_2 = state.arrNormal[size - 3];				
				}
				if (size > 3) {
					n_3 = state.arrNormal[size - 4];
				}
				for (int j = size - 3; j >= 0; j--) {
					if(normalSent.length()>0)
						normalSent = state.arrNormal[j] +"#"+normalSent;
					else
						normalSent = state.arrNormal[j];			
					int temlen = new StringTokenizer(normalSent,"#").countTokens();
					if (temlen > 3) {
						break;
					}
				}
			} else {
				e_0 = "*S*";
				l_0 = "*T*";
				e_1 = state.arrWord[size - 1];
				l_1 = state.arrTag[size - 1];
				n_1 = state.arrNormal[size - 1];				
				e_2 = "*S*";
				l_2 = "*T*";
				if (size > 1) {
					e_2 = state.arrWord[size - 2];
					l_2 = state.arrTag[size - 2];
					n_2 = state.arrNormal[size - 2];
				}
				if (size > 2) {
					n_3 = state.arrNormal[size - 3];
				}
				for (int j = size - 2; j >= 0; j--) {
				
					normalSent = state.arrNormal[j] +"#"+normalSent;
					
					int temlen = new StringTokenizer(normalSent,"#").countTokens();
					if (temlen > 3) {//
						break;
					}
				}
			}			
			String temNormsent="";			
			if(new StringTokenizer(normalSent,"#").countTokens() > 3){			
				for(int j = new StringTokenizer(normalSent,"#").countTokens()-3; j<new StringTokenizer(normalSent,"#").countTokens(); j++){
					if(j<normalSent.split("#").length-1){
						temNormsent += normalSent.split("#")[j] +"#";
					}else{
						temNormsent += normalSent.split("#")[j];
					}
				}
			}
			
			normalSent = temNormsent + n_1;
			if(e_0.equals("*S*")){
				len_e_0 = 0;
			}else{
				len_e_0 = new StringTokenizer(e_0,"#").countTokens();
				if(len_e_0 > 8)
					len_e_0 = 8;				
			}

			if (e_1.equals("*S*")) {
				len_e_1 = 0;
			} else {
				len_e_1 = new StringTokenizer(e_1,"#").countTokens();
				if (len_e_1 > 8)
					len_e_1 = 8;
			}
			if (e_2.equals("*S*")) {
				len_e_2 = 0;
			} else {
				len_e_2 = new StringTokenizer(e_2,"#").countTokens();
				if (len_e_2 > 8)
					len_e_2 = 8;
			}
        
			if (n_1.equals("*S*")) {
				len_n_1 = 0;
			} else {				
				len_n_1 = new StringTokenizer(n_1,"#").countTokens();
				if (len_e_1 > 8)
					len_n_1 = 8;
			}

			if (n_2.equals("*S*")) {
				len_n_2 = 0;
			} else {				
				len_n_2 = new StringTokenizer(n_2,"#").countTokens();
				if (len_n_2 > 8)
					len_n_2 = 8;
			}
			String[] temE0s =  e_0.split("#");
			String[] temE1s =  e_1.split("#");
			String[] temE2s = e_2.split("#");
			if (state.lastAction != 1000 && state.lastAction >= 0) {//1000:append, 0,1 sep, 2000 finish; SEP
				w_0 = e_0;				
				if (len_e_1 == 1) {
					w_1 = temE1s[0];
					if (len_e_2 > 0) {
						w_2 = temE2s[len_e_2 - 1];
					} else {
						w_2 = "S2";
					}
				} else if (len_e_1 > 1) {
					w_1 = temE1s[len_e_1-1];
					w_2 = temE1s[len_e_1-2];
				} else {
					w_1 = "S1";
					w_2 = "S2";
				}
			} else if (state.lastAction == 1000) {
				w_0 = temE0s[temE0s.length-1];				
				if (temE0s.length == 2) {
					w_1 = temE0s[0];					
					if (len_e_1 > 0) {
						w_2 = temE1s[len_e_1-1];					
					} else {
						w_2 = "S2";						
					}
				} else if(temE0s.length>2){
					w_1 = temE0s[temE0s.length-2];
					w_2 = temE0s[temE0s.length-3];					
				}
			}			

			if (len_e_1 > 0) {
				start_e_1 = temE1s[0];
				end_e_1 = temE1s[len_e_1-1];
			} else {
				start_e_1 = "S1";
				end_e_1 = "S1";
			}
			if (len_e_2 > 0) {
				end_e_2 = temE2s[len_e_2-1];
			} else {
				end_e_2 = "S2";
			}
			if (len_n_1 > 0) {
				start_n_1 = n_1.split("#")[0];
				end_n_1 = n_1.split("#")[len_n_1 - 1];				
			} else {
				start_n_1 = "S1";
				end_n_1 = "S1";
			}
			if (len_n_2 > 0) {
				end_n_2 = n_2.split("#")[len_n_2 -1];
			} else {
				end_n_2 = "S2";
			}		
		 
			Feature fe = null;
			String strfeat = null;
			
			if (state.lastAction == 1000) {// append
				strfeat = "OrgConsecutiveWords=" + w_1 + w_0;//1
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapOrgConsecutiveWords.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight : fe.weight;				

				strfeat = "OrgTagByChar=" + l_0 + w_0;//2
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapOrgTagByChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight : fe.weight;
			
				strfeat = "OrgTaggedCharByFirstChar=" + w_0 + l_0 + w_1;//3
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapOrgTaggedCharByFirstChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight : fe.weight;
				
				strfeat = "OrgTaggedCharByFirstTaggedChar=" + w_0 + l_0 + w_1 + l_1;//4
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapOrgTaggedCharByFirstTaggedChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight : fe.weight;
				
                String temW_0 = e_0.replaceAll("#", " ");	 //run a w_0			
				if(this.tool.CTDdisease.list.contains(temW_0))//5
				{
					strfeat = "OrgIsDisease=1";
					if (fvs != null)
						fvs.add(strfeat);
					fe = model.m_mapOrgIsDisease.get(strfeat);
					if (fe != null)
						dScore += bAverage ? fe.aveWeight : fe.weight;					
				} 	
				
			} else {	//SEP	

				strfeat = "OrgTagCharPOS=" + w_0 + p_0;//1
				if (fvs != null)
					fvs.add(strfeat);
				fe = model. m_mapOrgTagCharPOS.get(strfeat);
				if (fe != null)
				   dScore += bAverage ? fe.aveWeight : fe.weight;
					
				strfeat = "OrgTagPOS=" + w_1 + w_0 + p_0 ;//2
				 if (fvs != null)
					fvs.add(strfeat);
				  fe = model.  m_mapOrgTagPOS.get(strfeat);
				if (fe != null)
				    dScore += bAverage ? fe.aveWeight : fe.weight;
				if(len_e_1 > 1){
				    strfeat = "OrgCharsAndPOSAndTags=" + w_2 + w_1 + p_2 + p_1 + l_1;//3
				    if (fvs != null)
					fvs.add(strfeat);
				       fe = model.m_mapOrgCharsAndPOSAndTags.get(strfeat);
				    if (fe != null)
					   dScore += bAverage ? fe.aveWeight : fe.weight;
				    }
				//w_1 prefix and suffix
				int len_w_1 = w_1.length()>4 ? 4: w_1.length();	//4			
				String prefix = w_1.substring(0,len_w_1);
				strfeat = "OrgCharPrefix=" + prefix;
				if (fvs != null)
					fvs.add(strfeat);  
				fe = model. m_mapOrgCharPrefix.get(strfeat);
				if (fe != null)
				   dScore += bAverage ? fe.aveWeight : fe.weight;				
				String suffix = w_1.substring(w_1.length() - len_w_1, w_1.length());
				strfeat = "OrgCharSuffix=" + suffix;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model. m_mapOrgCharSuffix.get(strfeat);
				if (fe != null)
				   dScore += bAverage ? fe.aveWeight : fe.weight;				  
				strfeat = "OrgCurrentTag=" + e_1 + l_1; //5
				if(fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapOrgCurrentTag.get(strfeat); 
				if (fe != null) 
					dScore += bAverage ? fe.aveWeight : fe.weight;	
				// 6
				 strfeat = "OrgLastWordByLastChar=" + end_e_2 + end_e_1;
				 if(fvs != null)
					 fvs.add(strfeat); 
				 fe = model.m_mapOrgLastWordByLastChar.get(strfeat);
                 if (fe !=  null)
	                dScore += bAverage ? fe.aveWeight : fe.weight;	                  				
				if(this.tool.commonDiseaseNames.diseaseTerms.contains(w_1))//7
				{
					strfeat = "OrgLastCharVector=1";
					if (fvs != null)
						fvs.add(strfeat);
					fe = model. m_mapOrgLastCharVector.get(strfeat);
					if (fe != null)
						dScore += bAverage ? fe.aveWeight : fe.weight;					
				}							
				//Normalization
				strfeat = "NorSeenWords=" + n_1; //1
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorSeenWords.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//3
			   strfeat = "NorFirstAndLastChars=" + start_n_1 + end_n_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorFirstAndLastChars.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;   
				//5
				strfeat = "NorLengthByFirstChar=" + start_n_1 + len_n_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorLengthByFirstChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//6
				strfeat = "NorCurrentWordLastChar=" + end_n_2 + "_" + n_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorCurrentWordLastChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//8
				strfeat = "NorLengthByLastWord=" + n_2 + len_n_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorLengthByLastWord.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//9
				strfeat = "NorLastLengthByWord=" + len_n_2 + n_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorLastLengthByWord.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//10
				strfeat = "NorCurrentTag=" + n_1 + l_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorCurrentTag.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;	
				//14
				strfeat = "NorLastTagByWord=" + n_1 + l_2;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorLastTagByWord.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//15
				strfeat = "NorTagByLastChar=" + end_n_1 + l_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorTagByLastChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
		       //16
				strfeat = "NorLastTagByTag=" + l_1 + l_0;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorLastTagByTag.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
			  //25 
				strfeat = "NorFirstCharLastWordByWord=" + start_n_1 + w_0;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorFirstCharLastWordByWord.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//27		
				strfeat = "NorTagWordTag=" + l_2 + n_1 + l_0;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorTagWordTag.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
				//28
				strfeat = "NorWordTagTag=" + n_2 + l_1 + l_0;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorWordTagTag.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;
               //29
				strfeat = "NorTagByFirstChar=" + start_n_1 + l_1;
				if (fvs != null)
					fvs.add(strfeat);
				fe = model.m_mapNorTagByFirstChar.get(strfeat);
				if (fe != null)
					dScore += bAverage ? fe.aveWeight * 1.0 : fe.weight * 1.0;		
				//30
				if(len_n_1 >= 2){
					int idx = n_1.lastIndexOf("#");
					String n_1LastWord = n_1.substring(idx+1, n_1.length());
					String temp = n_1.substring(0, idx);
					int idxTemp = temp.lastIndexOf("#");
					String n_1Sub = temp.substring(idxTemp+1, temp.length());
					String bigram = n_1Sub + " " + n_1LastWord;
					bigram = bigram.toLowerCase();					
					Integer type2 = hsngramWord.get(bigram);
					if (type2 != null) 
					{
						strfeat = "Gram2=Word" + type2;
						if (fvs != null)
							fvs.add(strfeat);
						fe = model.m_mapGram2.get(strfeat);
						if (fe != null)
							dScore += bAverage ? fe.aveWeight : fe.weight;
					}
				}else if(len_n_1 == 1){					
					if(len_n_2 ==1){
						String bigram = n_2 + " " + n_1;
						bigram = bigram.toLowerCase();					
						Integer type2 = hsngramWord.get(bigram);
						if (type2 != null) 
						{
							strfeat = "Gram2=Word" + type2;
							if (fvs != null)
								fvs.add(strfeat);
							fe = model.m_mapGram2.get(strfeat);
							if (fe != null)
								dScore += bAverage ? fe.aveWeight : fe.weight;
						}
					}else{
						int idx = n_2.lastIndexOf("#");
						String n_2LastWord = n_2.substring(idx+1, n_2.length());
						String bigram = n_2LastWord + " " + n_1;
						bigram = bigram.toLowerCase();
						Integer type2 = hsngramWord.get(bigram);
						if (type2 != null) 
						{
							strfeat = "Gram2=Word" + type2;
							if (fvs != null)
								fvs.add(strfeat);
							fe = model.m_mapGram2.get(strfeat);
							if (fe != null)
								dScore += bAverage ? fe.aveWeight : fe.weight;
						}						
					}					
				}				
			}
		}
		state.score += dScore;
		return dScore;
	}

	/**
	 *  SEP action
	 * 
	 * @param state
	 * @param character
	 * @param pos
	 * @return
	 */
	private State Sep(State state, String curChar, int POSID, List<String> fvs,
			boolean bAverage, String preWordSense) {
		State newState = new State(state);
		newState.Sep(curChar, POSID, preWordSense);
		if (newState.score == this.MINVALUE)
			newState.score = 0;

		GetLocalFeaturesScore(newState, fvs, bAverage);
		return newState;
	}

	/**
	 * Finish action
	 * 
	 * @param state
	 * @param character
	 * @param pos
	 * @return
	 */
	private State Finish(State state, List<String> fvs, boolean bAverage,
			String preWordSense) {
		State newState = new State(state);
		newState.Finish(preWordSense);
		if (newState.score == this.MINVALUE)
			newState.score = 0;
		GetLocalFeaturesScore(newState, fvs, bAverage);
		return newState;
	}

	/**
	 * append action
	 * 
	 * @param state
	 * @param character
	 * @return
	 * @throws Exception
	 */
	private State Append(State state, String curChar, List<String> fvs,
			boolean bAverage) throws Exception {
		State newState = new State(state);
		newState.Add(curChar);
		if (newState.score == this.MINVALUE)
			newState.score = 0;
		GetLocalFeaturesScore(newState, fvs, bAverage);
		return newState;
	}

	/**
	 * output file
	 * 
	 * @param arrlist
	 */
	private void save(List<String> arrlist, String filename) {
		FileWriter fw;
		try {
			fw = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fw); 
			for (int i = 0; i < arrlist.size(); i++) {
				bw.write(arrlist.get(i) + "\r\n");
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JointRN bs = new JointRN();

		// bs.sentence=s;
		// bs.goldTagSquence = g;
		String r = "";
		try {
			for (int i = 0; i < 10; i++) {
				// bs.trainer(i);
			}
			// bs.model.load("e:\\a.txt");
			r = bs.Decoder();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(r);
	}

	/**
	 * update parameters
	 * @param predActions
	 * @param bestState
	 * @return
	 */
	public double[] updateParameters(List<Integer> predActions, State bestState){
		State tmpState = new State();
		State goldState = new State();
		double[] scores = new double[2];
		scores[0] = 0.0;
		scores[1] = 0.0;
		double currentScore;
		int p = 0;
		for (; p < predActions.size(); p++) {
			String curSChar = "";
			String preTmpWordSense = "";
			String preGoldWordSense = "";			
			String [] curwords= this.CurSentence.split(" ");
			if (p < curwords.length)
				curSChar = curwords[p];
			
			int predAction = predActions.get(p);
			int goldAction = goldActions.get(p);

			if (tmpState.size >= 1) {
				preTmpWordSense = predAction == 2000 ? this.GetPreNormal(
						bestState, p, true) : this.GetPreNormal(bestState, p,
						false);
				preTmpWordSense = preTmpWordSense == null ? ""
						: preTmpWordSense;
			}
			if (goldState.size >= 1) {
				preGoldWordSense = goldAction == 2000 ? this.GetPreNormal(
						this.goldSentenceState, p, true) : this.GetPreNormal(
						this.goldSentenceState, p, false);
				preGoldWordSense = preGoldWordSense == null ? ""
						: preGoldWordSense;
			}
			
			if (predAction == goldAction) {
				if (predAction < 1000 && predAction >= 0) {//preAction=0,Y ; 1表示N, sep
					String curTmpWordSense = this.GetCurNormal(bestState, p);
					curTmpWordSense = curTmpWordSense == null ? ""
							: curTmpWordSense;
					String curGoldWordSense = this.GetCurNormal(
							this.goldSentenceState, p);
					curGoldWordSense = curGoldWordSense == null ? ""
							: curGoldWordSense;
					if (curTmpWordSense.equals(curGoldWordSense) == false)
						break;

					tmpState.Sep(curSChar, predAction, preTmpWordSense);
					goldState.Sep(curSChar, predAction, preGoldWordSense);
				} else if (predAction == 1000) {//append
					tmpState.Add(curSChar);
					goldState.Add(curSChar);
				} else if (predAction == 2000) {//finish
					System.out.println("Impossible.....");
					tmpState.Finish(preTmpWordSense);
					goldState.Finish(preGoldWordSense);
				} else {
					System.out.println("error gold action: "
							+ Integer.toString(predAction));
				}
				// System.out.println("aa"+ tmpState.toString());
				currentScore = GetLocalFeaturesScore(tmpState, null, false);
				scores[0] += currentScore;
				scores[1] += currentScore;
			} else {//predAction 不等于 goldAction
				break;
			}
		}
		if (p >= predActions.size()) {
//			System.out.println("Impossible.....");
		}
		List<String> goldFeatures = new ArrayList<String>();
		List<String> predFeatures = new ArrayList<String>();
		for (; p < predActions.size(); p++) { //predAction 不等于 goldAction 时，p的值
			String curSChar = "";
			String preTmpWordSense = "";
			String preGoldWordSense = "";
			String [] curwords= this.CurSentence.split(" ");
			if (p < curwords.length)
				curSChar = curwords[p];

			int predAction = predActions.get(p);
			int goldAction = goldActions.get(p);
			if (tmpState.size >= 1) {
				preTmpWordSense = (predAction == 2000) ? this.GetPreNormal(
						bestState, p, true) : this.GetPreNormal(bestState, p,
						false);
				preTmpWordSense = preTmpWordSense == null ? ""
						: preTmpWordSense;
			}
			if (goldState.size >= 1) {
				preGoldWordSense = (goldAction == 2000) ? this.GetPreNormal(
						this.goldSentenceState, p, true) : this.GetPreNormal(
						this.goldSentenceState, p, false);
				preGoldWordSense = preGoldWordSense == null ? ""
						: preGoldWordSense;
			}

			if (predAction < 1000 && predAction >= 0) {
				tmpState.Sep(curSChar, predAction, preTmpWordSense);
			} else if (predAction == 1000) {
				tmpState.Add(curSChar);
			} else if (predAction == 2000) {
				tmpState.Finish(preTmpWordSense);
			} else {
//				System.out.println("error gold action: "
//						+ Integer.toString(predAction));
			}

			currentScore = GetLocalFeaturesScore(tmpState, predFeatures,false);

			scores[0] += currentScore;

			if (goldAction < 1000 && goldAction >= 0) {
				goldState.Sep(curSChar, goldAction, preGoldWordSense);
			} else if (goldAction == 1000) {
				goldState.Add(curSChar);
			} else if (goldAction == 2000) {
				goldState.Finish(preGoldWordSense);
			} else {
//				System.out.println("error gold action: "
//						+ Integer.toString(goldAction));
			}
			// System.out.println("bb "+ goldState.toString());
			currentScore = GetLocalFeaturesScore(goldState, goldFeatures,false);
			scores[1] += currentScore;
		}

		model.UpdateWeighth(goldFeatures, 1, curRoundIndexForTrain);
		model.UpdateWeighth(predFeatures, -1, curRoundIndexForTrain);

		return scores;
	}

	//
	public static String UnTagSentence(String tagSequence) {
		String sentence = "";
		StringTokenizer token = new StringTokenizer(tagSequence, " ");
		while (token.hasMoreElements()) {
			String tempStr = token.nextToken();
			int index = tempStr.indexOf("_");
			// System.out.println("untag "+tempStr);
			String theWordsense = tempStr.substring(0, index);
			int wordIndex = theWordsense.indexOf("|");
			String theWord = "";
			if (wordIndex < 0) {
				theWord = theWordsense;
			} else {
				theWord = theWordsense.substring(0, wordIndex);
			}
			
			// String thePOS = tempStr.substring(index+1, tempStr.length());
			sentence = sentence +" "+ theWord.replace("#", " ");
		}
		return sentence.trim();
	}

	/**
	 * action: 1000 denotes app, 2000 denotes final
	 * 
	 * @param tagSequence
	 * @return
	 */
	public List<Integer> getGoldActions(String tagSequence) {
		List<Integer> goldActions = new ArrayList<Integer>();
		this.goldSentenceState = new State();
		StringTokenizer token = new StringTokenizer(tagSequence, " ");
		int wordNumber = 0;
		while (token.hasMoreElements()) {
			String tempStr = token.nextToken();
			int index = tempStr.indexOf("_");
			String theWordSense = tempStr.substring(0, index);
			int wordIndex = theWordSense.indexOf("|");
			String theWord = "";
			String theSense = "";
			if (wordIndex < 0) {
				theWord = theWordSense;				
				theSense = theWord;
			} else {
				theWord = theWordSense.substring(0, wordIndex);				
				theSense = theWordSense.substring(wordIndex + 1,
						theWordSense.length());
			}

			String thePOS = tempStr.substring(index + 1, tempStr.length());
			int thePOSID = -1;
			for (int idx = 0; idx < State.arrPOS.length; idx++) {
				if (thePOS.equalsIgnoreCase(State.arrPOS[idx])) {
					thePOSID = idx;
					break;
				}
			}
			goldActions.add(thePOSID);

			int theWordLen = new StringTokenizer(theWord,"#").countTokens();
			for(int idx =1; idx < theWordLen; idx++){
				goldActions.add(1000);

			}
			goldSentenceState.arrWord[wordNumber] = theWord;
			goldSentenceState.arrNormal[wordNumber] = theSense;
			goldSentenceState.arrTag[wordNumber] = thePOS;
			wordNumber++;

		}
		goldSentenceState.size = wordNumber;

		goldActions.add(2000);
		return goldActions;
	}

	public boolean CanSeperate(State state, String curSChar, String thePOS) {
		// boolean bValid = true;

		if (model.m_startCharFreq.containsKey(curSChar)
				&& model.m_startCharFreq.get(curSChar) > 10
				&& !model.m_startCharPOSSets.get(curSChar).containsKey(thePOS)) {
			return false;
		}
		int length = state.size;
		if (length >= 1) {
			String theLastWord = state.arrWord[length - 1];
			String theLastPos = state.arrTag[length - 1];
			String theLastSense = state.arrNormal[length - 1];

			if ((model.m_wordFreq.containsKey(theLastWord)
					&& model.m_wordFreq.get(theLastWord) > 10 && !model.m_wordPOSSets
					.get(theLastWord).containsKey(theLastPos))
					|| (model.m_wordFreq.containsKey(theLastSense)
							&& model.m_wordFreq.get(theLastSense) > 10 && !model.m_wordPOSSets
							.get(theLastSense).containsKey(theLastPos))) {
				return false;
			}

			if (model.m_posCloseSet.containsKey(theLastPos)
					&& (!model.m_posCloseSet.get(theLastPos).contains(
							theLastWord) && !model.m_posCloseSet
							.get(theLastPos).contains(theLastSense))) {
				return false;
			}

			if (theLastSense != null && theLastSense.length() > 0) {
				if ((model.m_wordFreq.containsKey(theLastSense) && model.m_wordFreq
						.get(theLastSense) > 10)) {
					if (model.m_wordPOSSets.containsKey(theLastSense)
							&& !model.m_wordPOSSets.get(theLastSense)
									.containsKey(theLastPos)) {
						return false;
					}
				}

				if (model.m_posCloseSet.containsKey(theLastPos)
						&& !model.m_posCloseSet.get(theLastPos).contains(
								theLastSense)) {
					return false;
				}
			}
		}
		return true;
	}

	public ArrayList<String> GetNormalSetByWord(String word) {
		ArrayList<String> senseSet = new ArrayList<String>();
		senseSet.add(word);
		String strTemp = this.hmWordSenseSet.get(word);
		if (strTemp != null && strTemp.length() > 0) {
			StringTokenizer token = new StringTokenizer(strTemp, "\\|");
			while (token.hasMoreElements()) {
				senseSet.add(token.nextToken());
			}
		}
		return senseSet;
	}

	public void initialSense() {
		this.hmWordSenseSet = new HashMap<String, String>();
		if (this.sense_file == null || this.sense_file.length() == 0)
			return;
		File file = new File(this.sense_file);
		BufferedInputStream fis;
		try {
			fis = new BufferedInputStream(new FileInputStream(file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis, "UTF8"));
			String line = "";

			while ((line = reader.readLine()) != null) {
				if (line.trim().length() > 0) {
					line = line.trim();
					int index = line.indexOf(" ");
					String word = line.substring(0, index);
					String senses = line.substring(index + 1, line.length());
					hmWordSenseSet.put(word, senses);
				}
			}
			reader.close();

			fis.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	// initial language model
	public void initialLM() {
		hsngramChar = new HashMap<String, Integer>();
		hsngramWord = new HashMap<String, Integer>();
		if (this.lmChar_file != null && this.lmChar_file.length() > 0) {
			File file = new File(this.lmChar_file);
			BufferedInputStream fis;
			try {
				fis = new BufferedInputStream(new FileInputStream(file));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fis, "UTF8"));// 
				String line = "";

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0) {
						line = line.trim();
						StringTokenizer token = new StringTokenizer(line, "\t");
						int count = token.countTokens();
						if (count == 2) {
							String word = token.nextToken();
//							token.nextToken();
							int iType = Integer.parseInt(token.nextToken());
							hsngramChar.put(word, iType);

						}

					}
				}
				reader.close();
				fis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (this.lmWord_file != null && this.lmWord_file.length() > 0) {
			File file1 = new File(this.lmWord_file);
			BufferedInputStream fis1;
			try {
				fis1 = new BufferedInputStream(new FileInputStream(file1));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fis1, "UTF8"));
				String line = "";
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0) {
						line = line.trim();
						StringTokenizer token = new StringTokenizer(line, "\t");
						int count = token.countTokens();
						if (count == 2) {
							String word = token.nextToken();
							//token.nextToken();
							int iType = Integer.parseInt(token.nextToken());
							hsngramWord.put(word, iType);
						}
					}
				}
				reader.close();
				fis1.close();
			} catch (Exception e) {			
				e.printStackTrace();
			}
		}

	}

	public String GetPreNormal(State state, int index, boolean isFinish) {
		String strRet = "";
		if (isFinish == true) {
			return state.arrNormal[state.size - 1];
		}
		int k = -1;
		for (int i = 0; i < state.size; i++) {			
//			k += state.arrWord[i].split("#").length;
			k += new StringTokenizer(state.arrWord[i],"#").countTokens();
			if (k >= index) {
				if (i > 0)
					strRet = state.arrNormal[i - 1];
				break;
			}
		}
		return strRet;
	}

	public String GetCurNormal(State state, int index) {
		String strRet = "";
		int k = -1;
		for (int i = 0; i < state.size; i++) {
			k += state.arrWord[i].length();
			if (k >= index) {
				strRet = state.arrNormal[i];
				break;
			}
		}

		return strRet;
	}

	public boolean equalNormal(State trainState, State goldState, int k) {
		boolean bRet = false;
		if (trainState.arrNormal[trainState.size - k] == null
				|| trainState.arrNormal[trainState.size - k].length() < 1) {
			if (goldState.arrNormal[trainState.size - k] == null
					|| goldState.arrNormal[trainState.size - k].length() < 1) {
				bRet = true;
			} else {
				bRet = false;
			}
		} else {
			if (goldState.arrNormal[trainState.size - k] == null
					|| goldState.arrNormal[trainState.size - k].length() < 1) {
				bRet = false;
			} else {
				if (trainState.arrNormal[trainState.size - k]
						.equals(this.goldSentenceState.arrNormal[trainState.size
								- k])) {
					bRet = true;
				} else {
					bRet = false;
				}
			}
		}
		return bRet;
	}	
	public String CovertAddSpace(String Str) {
		String sRet = "";
		for (int i = 0; i < Str.length(); i++) {
			sRet += Str.substring(i, i + 1) + " ";
		}
		return sRet.trim();
	}
}
