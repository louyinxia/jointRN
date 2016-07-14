package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
/*
 * Build a dictionary based on HumanDO with all lowercase ones.
 */
public class HumanDO {
	public HashSet<String> list; 
	int maxNumOfWordPerEntry; // The entry whose numbers of words > this will be ignored
	
	public HumanDO(String path) {
		list = new HashSet<String>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String thisLine = null;
			
			while ((thisLine = br.readLine()) != null) {
				thisLine = thisLine.trim().replaceAll(",", "");//read ctd字典时，把单词之间的逗号去掉				
				list.add(thisLine);
			}
			br.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * Whether the dictionary contains the "word" with case ignoring
	 */
	public boolean contains(String word) {
		return list.contains(word.trim().toLowerCase());
	}
}
