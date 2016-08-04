package perceptron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class Model {
	public HashMap<String, Set<String>> m_labelCloseSet = new HashMap<String, Set<String>>();
	public HashMap<String, Map<String, Integer>> m_entityLabelSets = new HashMap<String, Map<String, Integer>>();
	public HashMap<String, Integer> m_entityFreq = new HashMap<String, Integer>();
	public HashMap<String, Map<String, Integer>> m_startWordLabelSets = new HashMap<String, Map<String, Integer>>();
	public HashMap<String, Integer> m_startWordFreq = new HashMap<String, Integer>();

	public HashMap<String, Feature> m_mapOrgConsecutiveWords = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgLabelByWord = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgLabeledWordByFirstWord = new HashMap<String, Feature>();
	public HashMap<String,Feature> m_mapOrgLabeledWordByFirstLabeledWord = new HashMap<String,Feature>(); 
	public HashMap<String,Feature> m_mapOrgIsDisease = new HashMap<String,Feature>(); 
	public HashMap<String, Feature> m_mapOrgWordPOS = new HashMap<String, Feature>(); 
	public HashMap<String, Feature> m_mapOrgConsecutiveWordsPOS = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgWordsAndPOSAndLabel = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgWordPrefix = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgWordSuffix = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgCurrentEntityLabel = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgLastEntityByLastWord = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapOrgCommonDiseaseName = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorSeenWord = new HashMap<String, Feature>();	
	public HashMap<String, Feature> m_mapNorFirstAndLastEntities = new HashMap<String, Feature>(); 
	public HashMap<String, Feature> m_mapNorLengthByFirstEntity = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorCurrentEntityLastWord = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLengthByLastEntity = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLastLengthByEntity = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorCurrentWordLabel = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLastLabelByEntity = new HashMap<String, Feature>(); 
	public HashMap<String, Feature> m_mapNorLabelByLastWord = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLastLabelByLabel = new HashMap<String, Feature>(); 
	public HashMap<String, Feature> m_mapNorFirstEntityCurrentWord = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLastLabelEntityLabel = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLastEntityLabelByLabel = new HashMap<String, Feature>();
	public HashMap<String, Feature> m_mapNorLabelByFirstWord = new HashMap<String, Feature>();	
	public HashMap<String, Feature> m_mapGram2 = new HashMap<String, Feature>();
	
	public Model() {
		// loadPosCloseSet();
	}
	public void init(String filename, boolean bNewTrain) {
		if (bNewTrain == true) {
			m_labelCloseSet = new HashMap<String, Set<String>>();
			m_labelCloseSet.put("Y", new HashSet<String>());
			m_labelCloseSet.put("N", new HashSet<String>());			
			m_entityLabelSets = new HashMap<String, Map<String, Integer>>();
			m_entityFreq = new HashMap<String, Integer>();
			m_startWordLabelSets = new HashMap<String, Map<String, Integer>>();
			m_startWordFreq = new HashMap<String, Integer>();
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;
				String[] temstrs = line.trim().split("\\s+");
				for (String tempStr : temstrs) {
					int index = tempStr.indexOf("_");
					if (index == -1) {
						System.out.println("error input line: " + line);
						continue;
					}
					String[] theEntitySense = tempStr.substring(0, index).split("\\|");
					String theEntity = theEntitySense[0];
					String theSense = "";
					if (theEntitySense.length == 2) {
						theSense = theEntitySense[1];
					}
					
					String theFirstWord;
					if(theEntity.indexOf("#") != -1){
						theFirstWord = theEntity.substring(0, theEntity.indexOf("#"));
					}else{
						theFirstWord = theEntity;
					}
					
					String theLabel = tempStr.substring(index + 1,
							tempStr.length());

					if (!m_entityFreq.containsKey(theEntity)) {
						m_entityFreq.put(theEntity, 0);
						m_entityLabelSets.put(theEntity, new HashMap<String, Integer>());
					}
					m_entityFreq.put(theEntity, m_entityFreq.get(theEntity) + 1);

					if (!m_entityLabelSets.get(theEntity).containsKey(theLabel)) {
						m_entityLabelSets.get(theEntity).put(theLabel, 0);
					}
					m_entityLabelSets.get(theEntity).put(theLabel,
							m_entityLabelSets.get(theEntity).get(theLabel) + 1);

					if (!m_startWordFreq.containsKey(theFirstWord)) {
						m_startWordFreq.put(theFirstWord, 0);
						m_startWordLabelSets.put(theFirstWord,	new HashMap<String, Integer>());
					}
					m_startWordFreq.put(theFirstWord,
							m_startWordFreq.get(theFirstWord) + 1);
					if (!m_startWordLabelSets.get(theFirstWord).containsKey(theLabel)) {
						m_startWordLabelSets.get(theFirstWord).put(theLabel, 0);
					}
					m_startWordLabelSets.get(theFirstWord).put(theLabel,m_startWordLabelSets.get(theFirstWord).get(	theLabel) + 1);

					if (m_labelCloseSet.containsKey(theLabel)) {
						m_labelCloseSet.get(theLabel).add(theEntity);
					}

					if (theSense.length() > 0) {
						String theFirstWordSense;
						if(theSense.indexOf("#") != -1){
							theFirstWordSense = theSense.substring(0, theSense.indexOf("#"));
						}else{
							theFirstWordSense = theSense;
						}

						if (!m_entityFreq.containsKey(theSense)) {
							m_entityFreq.put(theSense, 0);
							m_entityLabelSets.put(theSense,	new HashMap<String, Integer>());
						}
						m_entityFreq.put(theSense, m_entityFreq.get(theEntity) + 1);

						if (!m_entityLabelSets.get(theSense).containsKey(theLabel)) {
							m_entityLabelSets.get(theSense).put(theLabel, 0);
						}
						m_entityLabelSets.get(theSense).put(theSense,
								m_entityLabelSets.get(theSense).get(theLabel) + 1);

						if (!m_startWordFreq.containsKey(theFirstWordSense)) {
							m_startWordFreq.put(theFirstWordSense, 0);
							m_startWordLabelSets.put(theFirstWordSense,	new HashMap<String, Integer>());
						}
						m_startWordFreq.put(theFirstWordSense,
								m_startWordFreq.get(theFirstWordSense) + 1);
						if (!m_startWordLabelSets.get(theFirstWordSense).containsKey(theLabel)) {
							m_startWordLabelSets.get(theFirstWordSense).put(theLabel, 0);
						}
						m_startWordLabelSets.get(theFirstWordSense).put(
								theLabel, m_startWordLabelSets.get(theFirstWordSense).get(theLabel) + 1);

						if (m_labelCloseSet.containsKey(theLabel)) {
							m_labelCloseSet.get(theLabel).add(theSense);
						}
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * load file.
	 * 
	 * @param filename
	 * @return
	 */
	public int load(String filename) {
		int preRoundIndexForTrain = 0;
		newFeatureTemplates();
		m_labelCloseSet = new HashMap<String, Set<String>>();
		m_entityLabelSets = new HashMap<String, Map<String, Integer>>();
		m_entityFreq = new HashMap<String, Integer>();
		m_startWordLabelSets = new HashMap<String, Map<String, Integer>>();
		m_startWordFreq = new HashMap<String, Integer>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line = "";			
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;
				String[] temstrs = line.trim().split("\\s+");
				if (temstrs.length == 1) {
					System.out.println("error line: " + line);
					continue;
				}
				if (temstrs[0].equals("weight") && temstrs.length == 6) {
					String name = temstrs[1].trim();
					double dweigth = Double.parseDouble(temstrs[2]);
					double dsum = Double.parseDouble(temstrs[3]);
					int iindex = (int) (Double.parseDouble(temstrs[4]));
					double aveWeight = Double.parseDouble(temstrs[5]);
					if (preRoundIndexForTrain == 0)
						preRoundIndexForTrain = iindex;
					String[] names = name.split("=");
					HashMap<String, Feature> hm = GetFeatureTemplate(names[0]);
					hm.put(name, new Feature(name, dweigth, dsum, iindex, aveWeight));
				} else if (temstrs[0].equals("worddict") && temstrs.length % 2 == 1) {
					String theEntity = temstrs[1];
					int wordfreq = Integer.parseInt(temstrs[2]);
					if (m_entityFreq.containsKey(theEntity)) {
						System.out.println("model word dict word duplication: "	+ theEntity);
					}
					m_entityFreq.put(theEntity, wordfreq);
					m_entityLabelSets.put(theEntity, new HashMap<String, Integer>());
					int sumfreq = 0;
					for (int idx = 3; idx < temstrs.length - 1; idx++) {
						String thePOS = temstrs[idx];
						idx++;
						int curPOSFreq = Integer.parseInt(temstrs[idx]);
						sumfreq += curPOSFreq;
						if (m_entityLabelSets.get(theEntity).containsKey(thePOS)) {
							System.out.println("model word dict pos duplication: "+ theEntity + " " + thePOS);
						}
						m_entityLabelSets.get(theEntity).put(thePOS, curPOSFreq);
					}
					if (sumfreq != wordfreq) {
						System.out.println("model word dict freq does not match: "	+ theEntity);
					}
				} else if (temstrs[0].equals("schardict") && temstrs.length % 2 == 1 && temstrs[1].length() == 1) {
					String theEntity = temstrs[1];
					int wordfreq = Integer.parseInt(temstrs[2]);
					if (m_startWordFreq.containsKey(theEntity)) {
						System.out.println("model start char dict char duplication: "	+ theEntity);
					}
					m_startWordFreq.put(theEntity, wordfreq);
					m_startWordLabelSets.put(theEntity,	new HashMap<String, Integer>());
					int sumfreq = 0;
					for (int idx = 3; idx < temstrs.length - 1; idx++) {
						String thePOS = temstrs[idx];
						idx++;
						int curPOSFreq = Integer.parseInt(temstrs[idx]);
						sumfreq += curPOSFreq;
						if (m_startWordLabelSets.get(theEntity).containsKey(thePOS)) {
							System.out.println("model start char dict pos duplication: "+ theEntity + " " + thePOS);
						}
						m_startWordLabelSets.get(theEntity).put(thePOS, curPOSFreq);
					}
					if (sumfreq != wordfreq) {
						System.out.println("model start char dict freq does not match: "+ theEntity);
					}
				} else if (temstrs[0].equals("closetag")) {
					String thePOS = temstrs[1];
					if (m_labelCloseSet.containsKey(thePOS)) {
						System.out.println("model close tag dict POS duplication: "	+ thePOS);
					}
					m_labelCloseSet.put(thePOS, new HashSet<String>());
					for (int idx = 2; idx < temstrs.length; idx++) {
						if (m_labelCloseSet.get(thePOS).contains(temstrs[idx])) {
							System.out.println("model close tag dict POS word duplication: "+ thePOS + " " + temstrs[idx]);
						}
						m_labelCloseSet.get(thePOS).add(temstrs[idx]);
					}
				} else {
					System.out.println("error line: " + line);
				}

			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return preRoundIndexForTrain;
	}

	/**
	 * write to file.
	 * 
	 * @param filename
	 */
	public void save(String filename) {
		try {
			PrintWriter bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			for (featureName f : featureName.values()) {
				HashMap<String, Feature> hm = GetFeatureTemplate(f.toString());
				for (String theKey : hm.keySet()) {
					Feature tempf = hm.get(theKey);
					bw.println("weight " + tempf.name + " "	+ Double.toString(tempf.weight) + " "+ Double.toString(tempf.sum) + " "
							+ Double.toString(tempf.lastUpdateIndex) + " "+ Double.toString(tempf.aveWeight));	;
				}
			}

			for (String theKey : m_entityFreq.keySet()) {
				String outline = "worddict\t" + theKey + " " + Integer.toString(m_entityFreq.get(theKey));
				for (String thePOSKey : m_entityLabelSets.get(theKey).keySet()) {
					outline = outline + " "	+ thePOSKey	+ " "+ Integer.toString(m_entityLabelSets.get(theKey).get(thePOSKey));
				}
				bw.println(outline);
			}

			for (String theKey : m_startWordFreq.keySet()) {
				String outline = "schardict\t" + theKey + " " + Integer.toString(m_startWordFreq.get(theKey));
				for (String thePOSKey : m_startWordLabelSets.get(theKey).keySet()) {
					outline = outline + " "	+ thePOSKey	+ " " + Integer.toString(m_startWordLabelSets.get(theKey).get(thePOSKey));
				}
				bw.println(outline);
			}

			for (String theKey : m_labelCloseSet.keySet()) {
				String outline = "closetag\t" + theKey;
				for (String theEntityKey : m_labelCloseSet.get(theKey)) {
					outline = outline + " " + theEntityKey;
				}
				bw.println(outline);
			}

			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * update weight.
	 * 
	 * @param oprFeatures
	 * @param iType
	 * @param updateIndex
	 */

	public void UpdateWeighth(List<String> oprFeatures, int iType,
			int updateIndex) {		
		for (String curFeature : oprFeatures) {
			int _index = curFeature.indexOf("=");
			String sTemplateName = curFeature.substring(0, _index);
			HashMap<String, Feature> hm = GetFeatureTemplate(sTemplateName);
			Feature temp = hm.get(curFeature);		
			if (temp != null) {
				if (temp.lastUpdateIndex < updateIndex) {
					temp.sum += (updateIndex - temp.lastUpdateIndex) * temp.weight;
				}
				temp.weight += iType;
				temp.sum += iType;
				temp.lastUpdateIndex = updateIndex;
				hm.put(curFeature, temp);
			} else {			
				hm.put(curFeature, new Feature(curFeature, (double) iType,
						(double) iType, updateIndex, 0.0));
			}
		}
	}

	/**
	 * computer the average of weights.
	 * 
	 * @param curRoundIndexForTrain
	 */

	public void AveWeight(int curRoundIndexForTrain) {
		for (featureName f : featureName.values()) {
			HashMap<String, Feature> hm = GetFeatureTemplate(f.toString());
			Iterator iter = hm.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Feature> entry = (Map.Entry<String, Feature>) iter.next();
				String key = entry.getKey();
				Feature tempf = entry.getValue();
				if (tempf.lastUpdateIndex < curRoundIndexForTrain) {
					tempf.sum += (curRoundIndexForTrain - tempf.lastUpdateIndex) * tempf.weight;
					tempf.lastUpdateIndex = curRoundIndexForTrain;
				}
				tempf.aveWeight = tempf.sum / (curRoundIndexForTrain);
				hm.put(key, tempf);

			}
		}
	}

	public HashMap<String, Feature> GetFeatureTemplate(String featureTemplate) {
		// System.out.println(featureTemplate);
		featureName aa = featureName.valueOf(featureName.class, featureTemplate);
		switch (aa) {
		case OrgConsecutiveWords:
			return m_mapOrgConsecutiveWords;
		case OrgLabelByWord:
			return m_mapOrgLabelByWord;
		case OrgLabeledWordByFirstWord:
			return m_mapOrgLabeledWordByFirstWord;
		case OrgLabeledWordByFirstLabeledWord: 
			return m_mapOrgLabeledWordByFirstLabeledWord;
		case OrgIsDisease:
			return m_mapOrgIsDisease;
		case OrgWordPOS:
			return m_mapOrgWordPOS;
		case OrgConsecutiveWordsPOS: 
			return	m_mapOrgConsecutiveWordsPOS;
		case OrgWordsAndPOSAndLabel : 
			return m_mapOrgWordsAndPOSAndLabel;
		case OrgWordPrefix:
			return m_mapOrgWordPrefix;
		case OrgWordSuffix:
		return m_mapOrgWordSuffix;
		case OrgCurrentEntityLabel:
			return m_mapOrgCurrentEntityLabel;
		case OrgLastEntityByLastWord:
			return m_mapOrgLastEntityByLastWord;
		case OrgCommonDiseaseName:
			return m_mapOrgCommonDiseaseName;
		case NorSeenWord:
			return m_mapNorSeenWord;
		case NorFirstAndLastEntities:
			return m_mapNorFirstAndLastEntities;
		case   NorLengthByFirstEntity: 
		   return m_mapNorLengthByFirstEntity;
		case NorCurrentEntityLastWord:
		    return m_mapNorCurrentEntityLastWord;
		case NorLengthByLastEntity:
			return m_mapNorLengthByLastEntity;
		case 	NorLastLengthByEntity:
			return m_mapNorLastLengthByEntity;
		case 	NorCurrentWordLabel:
			return m_mapNorCurrentWordLabel;
		case   NorLastLabelByEntity:
			return  m_mapNorLastLabelByEntity;
		case  NorLabelByLastWord:
			return  m_mapNorLabelByLastWord ;
		case  NorLastLabelByLabel: 
			return m_mapNorLastLabelByLabel;
		case NorFirstEntityCurrentWord:
			return  m_mapNorFirstEntityCurrentWord;			
		case  NorLastLabelEntityLabel:
			return m_mapNorLastLabelEntityLabel;
		case NorLastEntityLabelByLabel:
			return m_mapNorLastEntityLabelByLabel;
		case  NorLabelByFirstWord:
			return m_mapNorLabelByFirstWord;	
		case Gram2:
		return m_mapGram2;
		}
		return null;
	}

	public enum featureName {
		OrgConsecutiveWords, OrgLabelByWord, OrgLabeledWordByFirstWord, OrgLabeledWordByFirstLabeledWord, OrgIsDisease, 
		OrgWordPOS, OrgConsecutiveWordsPOS, OrgCurrentEntityLabel, OrgWordsAndPOSAndLabel, OrgWordPrefix, OrgWordSuffix,
		OrgLastEntityByLastWord, OrgCommonDiseaseName, NorSeenWord, NorFirstAndLastEntities, NorLengthByFirstEntity,
		NorCurrentEntityLastWord, NorLengthByLastEntity, NorLastLengthByEntity,	NorCurrentWordLabel, NorLastLabelByEntity,
		NorLabelByLastWord, NorLastLabelByLabel, NorFirstEntityCurrentWord, NorLastLabelEntityLabel, NorLastEntityLabelByLabel,
		NorLabelByFirstWord, Gram2;		
	}

	// instantiation features
	public void newFeatureTemplates() {		
		
		m_mapOrgConsecutiveWords = new HashMap<String, Feature>();
		m_mapOrgLabelByWord = new HashMap<String, Feature>();
		m_mapOrgLabeledWordByFirstWord = new HashMap<String, Feature>();
		m_mapOrgLabeledWordByFirstLabeledWord = new HashMap<String,Feature>();
		m_mapOrgIsDisease = new HashMap<String,Feature>();
		m_mapOrgWordPOS = new HashMap<String, Feature>();
		m_mapOrgConsecutiveWordsPOS = new HashMap<String, Feature>();
		m_mapOrgWordsAndPOSAndLabel = new HashMap<String, Feature>();
		m_mapOrgCurrentEntityLabel = new HashMap<String, Feature>(); 
		m_mapOrgLastEntityByLastWord = new HashMap<String, Feature>(); 
		m_mapOrgCommonDiseaseName = new HashMap<String, Feature>();
		m_mapNorSeenWord = new HashMap<String, Feature>(); 
		m_mapNorFirstAndLastEntities = new HashMap<String, Feature>(); 		
		m_mapNorLengthByFirstEntity = new HashMap<String, Feature>();
	    m_mapNorFirstAndLastEntities = new HashMap<String, Feature>(); 
	    m_mapNorLengthByFirstEntity = new HashMap<String, Feature>();
		m_mapNorCurrentEntityLastWord = new HashMap<String, Feature>();
		m_mapNorLengthByLastEntity = new HashMap<String, Feature>();
		m_mapNorLastLengthByEntity = new HashMap<String, Feature>();
		m_mapNorCurrentWordLabel = new HashMap<String, Feature>(); 
	    m_mapNorLastLabelByEntity = new HashMap<String, Feature>(); 
	    m_mapNorLabelByLastWord = new HashMap<String, Feature>();
		m_mapNorLastLabelByLabel = new HashMap<String, Feature>(); 
		m_mapNorFirstEntityCurrentWord = new HashMap<String, Feature>();
	    m_mapNorLastLabelEntityLabel = new HashMap<String, Feature>();
	    m_mapNorLastEntityLabelByLabel = new HashMap<String, Feature>();
		m_mapNorLabelByFirstWord = new HashMap<String, Feature>();
		m_mapGram2 = new HashMap<String, Feature>();
	}

	public void printWeight(BufferedWriter bw) {
		try {
			String strFea = "";
			for (featureName f : featureName.values()) {
				HashMap<String, Feature> hm = GetFeatureTemplate(f.toString());
				Iterator it = hm.keySet().iterator();
				int n = 0;
				strFea = "";
				while (it.hasNext()) {
					Object key = it.next();
					if ((n + 1) % 5 == 0) {
						n = 0;
						bw.write(strFea + "\r\n");
						strFea = "";
					}
					Feature tempf = hm.get(key);
					strFea += tempf.name + "#" + tempf.weight + "#" + tempf.sum
							+ "#" + tempf.lastUpdateIndex + "#"
							+ tempf.aveWeight + " ";
					n++;
				}
			}
			bw.write(strFea.trim() + "\r\n");
			bw.write("end" + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
