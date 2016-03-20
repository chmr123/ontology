package traceability.groupVerbs.cm1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Filter {
	static File highfile = new File("high");
	static File lowfile = new File("low");
	static File[] highfilelist = highfile.listFiles();
	static File[] lowfilelist = lowfile.listFiles();
	static Stemmer stemmer = new Stemmer();
	ArrayList<String> all = new ArrayList<String>();
	ArrayList<String> high = new ArrayList<String>();
	ArrayList<String> low = new ArrayList<String>();
	
	ArrayList<String> stopwords = new ArrayList<String>();
	
	public Filter() throws IOException{
		
		File stopword = new File("stopwords.txt");
		BufferedReader br1 = new BufferedReader(new FileReader(stopword));
		String term;
		while ((term = br1.readLine()) != null) {
			stopwords.add(term);
		}
		br1.close();
		
		for(File file : highfilelist){
			ArrayList<String> eachline = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String onedocument = "";
			while((line = br.readLine()) != null){
				line = stem(removeStopWords(line)).toLowerCase();
				onedocument = onedocument + line + " ";
				eachline.addAll(Arrays.asList(line.split("\\s+")));
				for(String s : eachline){
					all.add(s.toLowerCase().replace(",", ""));
				}
			}
			high.add(onedocument);
		}
		
		for(File file : lowfilelist){
			ArrayList<String> eachline = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String onedocument = "";
			while((line = br.readLine()) != null){
				line = stem(removeStopWords(line)).toLowerCase();
				onedocument = onedocument + line + " ";
				eachline.addAll(Arrays.asList(line.split("\\s+")));
				for(String s : eachline){
					all.add(s.toLowerCase().replace(",", ""));
				}
			}
			low.add(onedocument);
		}
		
		
	}
	
	/*public double getIDF(LinkedHashSet<String> uniqueTerms, String query) throws IOException{
		LinkedHashMap<String, Double> idf = new LinkedHashMap<String, Double>();
		for(String originalTerm : uniqueTerms){
			String term = stem(removeStopWords(originalTerm));
			double df = 0;
			if(!term.contains(" ")){		
				for(String line : high){
					if(Arrays.asList(line.split("\\s+")).contains(term)) {
						df++;
					}
				}
				for(String line : low){
					if(Arrays.asList(line.split("\\s+")).contains(term)) {
					df++;
					}
				}
			
				double N = (double)100;
				double idfvalue = Math.log((N) / (df)) / Math.log(2);		
				idf.put(term, idfvalue);
			}
			else{
				for(String line : high){
					if(line.contains(term)) {
						df++;
					}
				}
				for(String line : low){
					if(line.contains(term)) {
						df++;
					}
				}
				
				double N = (double)100;
				double idfvalue = Math.log((N) / (df)) / Math.log(2);
				idf.put(term, idfvalue);
			}
		}
		
		return idf.get(stem(removeStopWords(query)));
	}*/
	
	public double getIDF(String query) throws IOException{
	
			String term = stem(removeStopWords(query));
			double df = 0;
			double idfvalue;
			if(!term.contains(" ")){		
				for(String line : high){
					if(Arrays.asList(line.split("\\s+")).contains(term)) {
						df++;
					}
				}
				for(String line : low){
					if(Arrays.asList(line.split("\\s+")).contains(term)) {
					df++;
					}
				}
			
				double N = (double)100;
				idfvalue = Math.log((N) / (df)) / Math.log(2);		
			}
			else{
				for(String line : high){
					if(line.contains(term)) {
						df++;
					}
				}
				for(String line : low){
					if(line.contains(term)) {
						df++;
					}
				}
				
				double N = (double)100;
				idfvalue = Math.log((N) / (df)) / Math.log(2);
			}
		
		return idfvalue;
	}
	
	
	public boolean isFiltered(String originalTerm){
		String term = stem(removeStopWords(originalTerm));
		if(!term.contains(" ") && getFrequency(term) <= 1){ 
			//System.out.println(term);
			return true;
		}
		else if(term.length() < 3){
			return true;
		}
		/*else if(!(checkPresenceHigh(term) && checkPresenceLow(term))) 
			return true;*/
		else if(checkPresenceOnlyOne(term) == true)
			return true;
		else
			return false;
	}
	
	private int getFrequency(String term){
		return Collections.frequency(all, term);
	}
	
	private int getNounFrequencyFromPairs(String term, ArrayList<String[]> allpairs){
		int freq = 0;
		for(String[] pair : allpairs){
			if(stem(removeStopWords(pair[1])).equals(term)) 
				freq++;
		}
		return freq;
	}
	
	private int getVerbFrequencyFromPairs(String term, ArrayList<String[]> allpairs){
		int freq = 0;
		for(String[] pair : allpairs){
			if(stem(removeStopWords(pair[0])).equals(term)) 
				freq++;
		}
		return freq;
	}
	
	private boolean checkPresenceHigh(String term){
		for(String line : high){
			if(line.contains(term))
				return true;
		}
		return false;
	}
	
	private boolean checkPresenceLow(String term){
		for(String line : low){
			if(line.contains(term))
				return true;
		}
		return false;
	}
	
	private boolean checkPresenceOnlyOne(String term){
		int freq = 0;
		for(String line : high){
			if(line.contains(term)) {
				freq++;
			}
		}
		for(String line : low){
			if(line.contains(term)) {
				freq++;
			}
		}
		if(freq == 1)
			return true;
		else
			return false;
	}
	
	
	private String stem(String term){
		String[] splitted = term.split("\\s+");
		if(splitted.length == 1){
			char[] chars = term.toCharArray();
			for (char c : chars)
				stemmer.add(c);
			stemmer.stem();
			String stemmedTerm = stemmer.toString();
			return stemmedTerm;
		}
		else{
			String phrase = "";
			for(String t : splitted){
				char[] chars = t.toCharArray();
				for (char c : chars)
					stemmer.add(c);
				stemmer.stem();
				String stemmedTerm = stemmer.toString();
				phrase = phrase + stemmedTerm + " ";
			}
			//return phrase.substring(0,phrase.lastIndexOf(" ")-1);
			return phrase.trim();
		}
	}
	
	private String removeStopWords(String term){
		String[] splitted = term.split(" ");
		if(splitted.length == 1){
			return term;
		}
		else{
			String phrase = "";
			for(String t : splitted){
				if(stopwords.contains(t)) continue;
				phrase = phrase + t + " ";
			}
			//return phrase.substring(0,phrase.lastIndexOf(" ")-1);
			return phrase.trim();
		}
	}
}
