package utils;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Iterator;

public class BrownClustering {
	Map<String,String> tokenToClustering = new HashMap<String,String>();
	String brownClusteringFile;
	public BrownClustering(String brownFile){
		super();
		brownClusteringFile = brownFile;
		
	}
	public void setTokenToClustering ( )throws Exception{
		BufferedReader bfinput = new BufferedReader(new InputStreamReader(new FileInputStream(brownClusteringFile),"utf-8"));
		String line = bfinput.readLine();
		while(line != null){
			String[] splits = line.split("\t");
			if(splits.length <2){
				System.out.println("brown clustering is wrong");
				break;
			}
			tokenToClustering.put(splits[1], splits[0]);
			line = bfinput.readLine();
		}
		bfinput.close();		
	}
	public Map<String, String> getTokenToClustering(){
		return this.tokenToClustering;
	}
	public String getClustering (String token){
		String clustering = new String();
		Iterator iter = tokenToClustering.entrySet().iterator();
		
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry)iter.next();
			String tempToken = (String) entry.getKey();
			if(token.equals(tempToken)){
				clustering = (String)entry.getValue();
				break;
			}
		}
		return clustering;		
	}
	
	@Override
	 public int hashCode() {
			final int prime = 31;
			int result = 1;
			
			result = prime * result + ((brownClusteringFile == null) ? 0 : brownClusteringFile.hashCode());
			result = prime * result + ((tokenToClustering == null) ? 0 : tokenToClustering.hashCode());
			
			return result;
		}

	@Override
	public boolean equals(Object obj) {			 
		if (this == obj)
			return true;
		if (obj == null ||!(obj instanceof BrownClustering))
			return false;		
		final BrownClustering  other = (BrownClustering) obj;
		if (this.brownClusteringFile.equals(other.brownClusteringFile)&& this.tokenToClustering.equals(other.tokenToClustering))
			return true;		 
		return false;
		
	}	

}
