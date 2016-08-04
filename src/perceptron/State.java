package perceptron;

import java.util.ArrayList;
import java.util.List;

public class State {
	public int lastAction = 0; 								
	public List<Integer> hisActions = new ArrayList<Integer>();
	public String[] arrEntity; 
	public String[] arrTag; 
	public String[] arrNormal; 
	public static int MAXNUM = 5000;
	public int size = 0;
	public double score = 0;
	public int curIndex = -1;
	public String[] curSentence; //current sentence
 
	public boolean bIsGold = true;
	public boolean bStart = true;

	public static String[] arrLabel = { "Y", "N"};

	public State() {
		this.lastAction = -1;
		this.arrEntity = new String[MAXNUM];
		this.arrTag = new String[MAXNUM];
		this.arrNormal = new String[MAXNUM];
		this.curSentence = new String[MAXNUM];		 
		this.score = 0;
		this.size = 0;
		this.curIndex = 0;
		this.bIsGold = true;
		hisActions = new ArrayList<Integer>();
		bStart = true;	
	
	}

	public State(State newState) {
		this.lastAction = newState.lastAction;
		this.arrEntity = new String[MAXNUM];
		this.arrTag = new String[MAXNUM];
		this.arrNormal = new String[MAXNUM];		
		this.curIndex = newState.curIndex;				
		this.size = newState.size;
		this.score = newState.score;
		this.bIsGold = newState.bIsGold;
		this.curSentence = new String[MAXNUM];		
		
		for(int i = 0; i < newState.curSentence.length; i++){
			this.curSentence[i] = newState.curSentence[i];
		}
		for (int i = 0; i < size; i++) {
			this.arrEntity[i] = newState.arrEntity[i];
			this.arrTag[i] = newState.arrTag[i];
			this.arrNormal[i] = newState.arrNormal[i];
		}
		hisActions = new ArrayList<Integer>();
		for (int act : newState.hisActions) {
			hisActions.add(act);
		}
		bStart = newState.bStart;
	}

	/**
	 * Separate action, which adds the current word as a partial new entity, and
	 * replace the last completed entity by formal entities
	 * 
	 * @param curWord
	 *            : current word
	 * @param labelID
	 *            : label
	 * @param preEntitySense
	 *            : formal entity
	 */
	public void Sep(String curWord, int labelID, String preEntitySense) {
		String label = arrLabel[labelID];
		arrEntity[size] = curWord;
		arrTag[size] = label;
		if (preEntitySense != null && preEntitySense.split("#").length > 0 && size >= 1) {
			arrNormal[size - 1] = preEntitySense;
		}
		this.lastAction = labelID;
		this.hisActions.add(lastAction);
		size++;
		bStart = false;
	}

	/**
	 * Finissh action: processing when all chars in the sentence are segmented.
	 * 
	 * @param preEntitySense
	 */
	public void Finish(String preEntitySense) {
		this.lastAction = 2000;
		this.hisActions.add(lastAction);
		if (preEntitySense != null && size >= 1) {
			arrNormal[size - 1] = preEntitySense;
		}
		bStart = false;
	}

	/**
	 * 
	 * @param curWord
	 */
	public void Add(String curWord) {
		if (size > 0) {
			arrEntity[size - 1] = arrEntity[size - 1] + "#"+ curWord;
			this.lastAction = 1000;
			this.hisActions.add(lastAction);
			bStart = false;
		} else {
			System.out.println("Cannot be mergered!");
		}
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < size; i++) {
			if (arrNormal[i] != null && arrNormal[i].split("#").length > 0) {
				str += arrEntity[i] + "|" + arrNormal[i] + "_" + arrTag[i] + " ";
			} else {
				str += arrEntity[i] + "_" + arrTag[i] + " ";
			}

		}
		return str.trim();
	}

	public String GetSentence() {
		String str = "";
		for (int i = 0; i < size; i++) {
			str += arrEntity[i];
		}
		return str.trim();
	}
}
