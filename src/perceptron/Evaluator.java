package perceptron;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Evaluator {
	public BufferedWriter bw;
	public BufferedWriter bwlog;
	public String error_file;
	public List<String> arrTestResult;
	public List<String> arrTestStand;	
	// computer P, R, F of disease name recognition and normalization 
	int iDiseCorrect = 0, iDisePred = 0, iDiseGold = 0;
	int iDiseNormCorrect = 0, iDiseNormPred = 0, iDiseNormGold = 0;	

	private static class Sentence {
		String[] words;
		String[] poss;
		String[] senses;
		String chars;
	}

	public Evaluator() {
	}

	public Evaluator(List<String> arrTestResult, List<String> arrTestStand,
			BufferedWriter bwlog, String error_file) {
		this.arrTestResult = arrTestResult;
		this.arrTestStand = arrTestStand;
		this.error_file = error_file;
		this.bwlog = bwlog;

		FileWriter fw;
		try {
			fw = new FileWriter(error_file);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//computer disease P R, F
	
	public void diseaseComputer(){
		for(int i=0; i<arrTestStand.size(); i++){
			String sStand = arrTestStand.get(i);
			String sResult = arrTestResult.get(i);
			processTwoSequence(sStand,sResult);
		}
		Save();
		try{
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void processTwoSequence(String sStand,String sResult){
		ArrayList<String> goldDiseases = new ArrayList<String>();
		ArrayList<String> goldDiseaseSenses = new ArrayList<String>();
		ArrayList<String> preDiseases = new ArrayList<String>();
		ArrayList<String> preDiseasesSenses = new ArrayList<String>();
		
		preInformation(sStand, goldDiseases, goldDiseaseSenses);
		preInformation(sResult, preDiseases, preDiseasesSenses);
		
		iDiseGold += goldDiseases.size();
		iDisePred += preDiseases.size();
		
		iDiseNormGold += goldDiseaseSenses.size();		
		iDiseNormPred += preDiseasesSenses.size();
	
		for(int i=0; i<preDiseases.size(); i++){
			String temPreDisease = preDiseases.get(i);
			for(int j =0 ; j < goldDiseases.size(); j++){
				String temGoldDisease = goldDiseases.get(j);
				if(temPreDisease.equals(temGoldDisease)){
					iDiseCorrect++;
					break;
				}
				
			}
		}
			
		for(int i=0; i<preDiseasesSenses.size(); i++){
			String temPreSense = preDiseasesSenses.get(i);
			for(int j =0 ; j <  goldDiseaseSenses.size(); j++){
				String temGoldSense =  goldDiseaseSenses.get(j);
				if(temPreSense.equals(temGoldSense)){
					iDiseNormCorrect++;
					break;
				}
				
			}
		}
		
		try{
			String temresult = resultProcess(sResult);
		    if (sStand.equals(temresult) == false) {
			   bw.write("stand :" + sStand + "\r\n");
			   bw.write("result:" + temresult + "\r\n");
		}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
		/*
		 * propercess information of String 		 * 
		 */
		public void preInformation(String source, ArrayList<String> words, ArrayList<String> wordSenses){
			 StringTokenizer stoken=new StringTokenizer(source," ");
				while(stoken.hasMoreElements()){
					String tempStr = stoken.nextToken();
					int index = tempStr.indexOf("_");
					String theTag = tempStr.substring(index+1);
					String theWordSense = tempStr.substring(0, index);
					if(theTag.equals("Y")){
						int temIndex = theWordSense.indexOf("|");
						if( temIndex != -1){
							String temDisease = theWordSense.substring(0, temIndex);
							String temDiseaseSense = theWordSense.substring(temIndex+1);
							words.add(temDisease);
							wordSenses.add(temDiseaseSense);					
						}else{
							System.out.println("sStand is error");
					}
				}
			}
					
	}
		

	/**
	 * save result.
	 */
	public void Save() {
		float diseP = (float) (iDiseCorrect * 1.0 / iDisePred);
		float diseR = (float) (iDiseCorrect * 1.0 / iDiseGold);
		float diseF = (float) (2.0 * iDiseCorrect / (iDisePred + iDiseGold));
		
		
		float diseNormalP = (float) (iDiseNormCorrect * 1.0 / iDiseNormPred);
		float diseNormalR = (float) (iDiseNormCorrect * 1.0 / iDiseNormGold);
		float diseNormalF = (float) (2.0 * iDiseNormCorrect / (iDiseNormPred + iDiseNormGold));

		try {
			bwlog.write("recognition result: precise=" + diseP
					+ "     recall rate=" + diseR + "   F=" + diseF + "\r\n");
			
			bwlog.write("Normalization result: precise=" + diseNormalP
					+ "     recall rate=" + diseNormalR + "   F=" + diseNormalF
					+ "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public static int[] reco(Sentence goldSentence, Sentence predSentence) {
		// seg: 0 goldWords 1 predWords
		// seg: 2 recoWords
		// tag: 3 recoPos
		// tag: 4 goldSenses 5 predSenses 6 recoSenses
     	// 7:是否相等， 0：相等，1：不等
			
		int[] predRes = new int[8];

		for (int i = 0; i < 8; i++) {
			predRes[i] = 0;
		}

		String[] goldWords = goldSentence.words;
		String[] goldLabels = goldSentence.poss;
		String[] predWords = predSentence.words;
		String[] predLabels = predSentence.poss;
		String[] goldSenses = goldSentence.senses;
		String[] predSenses = predSentence.senses;		
		
		int m = 0, n = 0;
		for (int i = 0; i < goldWords.length; i++) {
			predRes[0]++;
			if (goldSenses[i] != null && goldSenses[i].length() > 0) {
				predRes[4]++;
			}
		}

		for (int i = 0; i < predWords.length; i++) {
			predRes[1]++;
			if (predSenses[i] != null && predSenses[i].length() > 0) {
				predRes[5]++;
			}
		}
		boolean bequal = true;
		while (m < predWords.length && n < goldWords.length) {
			if (predWords[m].equals(goldWords[n])) {
				predRes[2]++;
				if (predSenses[m].length() > 0
						&& predSenses[m].equals(goldSenses[n])) {
					predRes[6]++;
				} else {
					if (bequal == true)
						bequal = false;
				}
				boolean bTagMatch = false;
				if (predLabels[m].equals(goldLabels[n])) {
					bTagMatch = true;
					predRes[3]++;
				} else {
					if (bequal == true)
						bequal = false;
				}
				m++;
				n++;
			} else {
				if (bequal == true)
					bequal = false;
				int lgold = goldWords[n].length();
				int lpred = predWords[m].length();
				int lm = m + 1;
				int ln = n + 1;
				int sm = m;
				int sn = n;

				while (lm < predWords.length || ln < goldWords.length) {
					if (lgold > lpred && lm < predWords.length) {
						lpred = lpred + predWords[lm].length();
						sm = lm;
						lm++;
					} else if (lgold < lpred && ln < goldWords.length) {
						lgold = lgold + goldWords[ln].length();
						sn = ln;
						ln++;
					} else {
						break;
					}
				}
				m = sm + 1;
				n = sn + 1;
			}
		}
		if (bequal == true)
			predRes[7] = 0;
		else
			predRes[7] = 1;
		return predRes;
	}
//tagSentence convert sentence
	public static Sentence TagSentConvertSentence(String tagSequence) {
		Sentence sent = new Sentence();
		if (tagSequence.trim().length() < 1)
			return sent;
		String[] wordposses = tagSequence.split("\\s+");
		// StringTokenizer st=new StringTokenizer(tagSequence," ");
		sent.poss = new String[wordposses.length];
		sent.words = new String[wordposses.length];
		sent.senses = new String[wordposses.length];
		sent.chars = "";
		for (int idx = 0; idx < wordposses.length; idx++) {
			int index = wordposses[idx].indexOf("_");
			String wordSense = wordposses[idx].substring(0, index);
			int wordIndex = wordSense.indexOf("|");
			if (wordIndex >= 0) {
				String temword = wordSense.substring(0, wordIndex);
				String temsense = wordSense.substring(wordIndex + 1);
				if (temword.equals(temsense)) {
					sent.words[idx] = temword;
					sent.senses[idx] = "";
				} else {
					sent.words[idx] = temword;
					sent.senses[idx] = temsense;
				}
				/*sent.words[idx] = temword;
				sent.senses[idx] = temsense;*/
			} else {
				sent.words[idx] = wordSense;
				sent.senses[idx] = "";
			}
			sent.poss[idx] = wordposses[idx].substring(index + 1);
			sent.chars = sent.chars + sent.words[idx];
		}

		return sent;
	}

	public static Sentence TagSentConvertSentenceSelf(String tagSequence) {
		Sentence sent = new Sentence();
		if (tagSequence.trim().length() < 1)
			return sent;
		String[] wordposses = tagSequence.split("\\s+");
		sent.poss = new String[wordposses.length];
		sent.words = new String[wordposses.length];
		sent.senses = new String[wordposses.length];
		sent.chars = "";
		for (int idx = 0; idx < wordposses.length; idx++) {
			int index = wordposses[idx].indexOf("_");
			String wordSense = wordposses[idx].substring(0, index);
			int wordIndex = wordSense.indexOf("|");
			if (wordIndex >= 0) {
				sent.words[idx] = wordSense.substring(0, wordIndex);
				sent.senses[idx] = wordSense.substring(wordIndex + 1);
			} else {
				sent.words[idx] = wordSense;
				sent.senses[idx] = wordSense;
			}
			sent.poss[idx] = wordposses[idx].substring(index + 1);
			sent.chars = sent.chars + sent.words[idx];
		}

		return sent;
	}

	public static void main(String[] args) {
		String standfile = "E:\\test\\testData";
		String resultfile = "E:\\test\\testResult";
		BufferedWriter bwlog;
		String errorfile = "E:\\test\\temp\\testError";
		List<String> arrTestSource = new ArrayList<String>();
		List<String> arrTestResult = new ArrayList<String>();
		File infile = new File(standfile);
		BufferedInputStream infis;
		File outfile = new File(resultfile);
		BufferedInputStream outfis;
		try {
			bwlog = new BufferedWriter(new FileWriter(
					"E:\\test\\temp\\log"));
			infis = new BufferedInputStream(new FileInputStream(standfile));
			BufferedReader inreader = new BufferedReader(new InputStreamReader(
					infis, "UTF8"));
			String line = "";
			while ((line = inreader.readLine()) != null) {
				if (line.trim().length() > 0) {
					arrTestSource.add(line.trim());
				}
			}
			outfis = new BufferedInputStream(new FileInputStream(resultfile));
			BufferedReader outReader = new BufferedReader(
					new InputStreamReader(outfis, "UTF8"));
			line = "";
			while ((line = outReader.readLine()) != null) {
				if (line.trim().length() > 0) {
					arrTestResult.add(line.trim());
				}
			}
			Evaluator ob = new Evaluator(arrTestResult, arrTestSource, bwlog,
					errorfile);
//			ob.Computer();
			ob.diseaseComputer();
			bwlog.close();
			infis.close();
			outfis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String resultProcess(String result) {
		String rent = "";
		StringTokenizer st = new StringTokenizer(result, " ");
		while (st.hasMoreElements()) {
			String wordposses = st.nextToken();
			int index = wordposses.indexOf("_");
			String wordSense = wordposses.substring(0, index);
			int wordIndex = wordSense.indexOf("|");

			if (wordIndex >= 0) {
				String temword = wordSense.substring(0, wordIndex);
				String temsense = wordSense.substring(wordIndex + 1);
				if (temword.equals(temsense)) {
					rent += temword;
				} else {
					rent += temword + "|" + temsense;
				}
			} else {
				rent += wordSense;
			}
			rent += "_" + wordposses.substring(index + 1) + " ";
		}
		return rent.trim();
	}
}
