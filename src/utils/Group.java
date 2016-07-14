package utils;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Group {
	public List<String> diseaseTerms;
	 public List<String> bodyParts;
	 public List<String> humanAbilities;
	public Group(){
	 diseaseTerms = new ArrayList<String>(Arrays.asList("disorder", "condition", "syndrome",	
			"symptom", "abnormality", "issue", "drug", "event", "impairment","diagnosis", "dysfunction",
			"toxicity", "neurotoxicity", "insufficiency", "effusion", "deficit", "decrease", "injury", 
			"lesion","decline", "cholestasis", "apnoea", "defect", "depression",	"hyperplasia",	
			"damage", "neuropathy",	"depression", "thrombosis",	"seizure",	"abuse", "thrombosis",
			"hemorrhage",  "regurgitation", "thromboemboli", "pain","illness", "abortion",
			"abortions", "illnesses", "neoplasms", "diseases", "disorders", "conditions", "syndromes",
			"toxicities", "neurotoxicities", "insufficiencies", "effusions", "deficits", "decreases",
			"injuries", "lesions", "declines", "cholestasises", "defects", "depressions", "thrombosises",
			"seizures",	"pains","tumour", "cancer", "neoplasm", "tumours", "cancers", "neoplasms", 			
			"carcinomas", "complication", "complications", "carcinoma", "ganglioma", "ganglioglioma", 
			"pneumonia","choreoathetosis","diabetics", "gangliomas", "gangliogliomas","cystitis","cystitises","myopathy","hepatitis","bleeding","oedema"));
		 bodyParts = new ArrayList<String>(Arrays.asList("pulmonary", "neuronocular", "orbital", "breast",
				 "respiratory","renal", "hepatic", "liver", "hart", "eye", "pulmonary", "ureter", "bladder", "pleural",
				 "ventricular","pericardial", "colorectal", "head", "neck", "pancreaticobiliary", "cardiac", "leg","infarct",
				 "intestinal", "back", "cardiovascular", "gastrointestinal", "myocardial", "kidney", "bile",
				 "ovarian", "tongue", "palate", "lip", "intrahepatic", "extrahepatic", "memorygastric","pelvis","pelvis","vagina"));
		 humanAbilities = new ArrayList<String>(Arrays.asList("visual", "auditory", "learning", "opisthotonu", "sensory", "motor", "atonic",
				 "memory", "social", "emotion"));

		 
	 }
	
	public boolean isCommonDisease(String word){
		String temp = word.trim();
		for(String term:diseaseTerms){
			if(term.equals(temp.toLowerCase()))
				return true;
		}
		for(String part:bodyParts)
			if(part.equals(temp.toLowerCase()))
				return true;
		for(String ability:humanAbilities)
			if(ability.equals(temp.toLowerCase()))
				return true;
			
		return false;
	}

}
