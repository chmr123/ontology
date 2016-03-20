package traceability.groupVerbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Arff {
	static Stemmer stemmer = new Stemmer();
	ArrayList<String> stopwords = new ArrayList<String>();
	public Arff() throws IOException{
		File file = new File("stopwords.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			stopwords.add(line);
		}
		br.close();
	}
	public void createArffVerbObjAP(LinkedHashMap<String, Set<String>> verb_obj, LinkedHashSet<String> uniqueTermSet) throws IOException{
		FileWriter fw = new FileWriter("verbObj.arff");
		fw.write("@relation verbObj\n");
		for(String noun : uniqueTermSet){
			fw.write("@attribute " + noun.replaceAll(" ", "_") + " numeric\n");
		}
		fw.write("@data\n");
		
		ArrayList<String> uniqueTerms = new ArrayList<String>(uniqueTermSet);
		for(String key : verb_obj.keySet()){
			Set<String> terms = verb_obj.get(key);
			for(int i = 0; i < uniqueTerms.size() - 1; i++){
				String t = uniqueTerms.get(i);
				if(terms.contains(t))
					fw.write("1,");
				else
					fw.write("0,");
			}	
			String t = uniqueTerms.get(uniqueTerms.size() - 1);
			if(terms.contains(t))
				fw.write("1\n");
			else
				fw.write("0\n");
			
		}
		
		fw.flush();
		fw.close();
	}
	
	public void createArffObjVerbAP(LinkedHashMap<String, Set<String>> obj_verb, LinkedHashSet<String> uniqueTermSet) throws IOException{
		FileWriter fw = new FileWriter("objVerb.arff");
		fw.write("@relation objVerb\n");
		for(String verb : uniqueTermSet){
			fw.write("@attribute " + verb + " numeric\n");
		}
		fw.write("@data\n");
		
		ArrayList<String> uniqueTerms = new ArrayList<String>(uniqueTermSet);
		for(String key : obj_verb.keySet()){
			Set<String> terms = obj_verb.get(key);
			for(int i = 0; i < uniqueTerms.size() - 1; i++){
				String t = uniqueTerms.get(i);
				if(terms.contains(t))
					fw.write("1,");
				else
					fw.write("0,");
			}	
			String t = uniqueTerms.get(uniqueTerms.size() - 1);
			if(terms.contains(t))
				fw.write("1\n");
			else
				fw.write("0\n");
			
		}
		
		fw.flush();
		fw.close();
	}
	
	public void createArffVerbObjFreq(LinkedHashMap<String, Set<String>> verb_obj, LinkedHashSet<String> uniqueTermSet, ArrayList<String[]> allpairs) throws IOException{
		FileWriter fw = new FileWriter("verbObjFreq.arff");
		fw.write("@relation verbObj\n");
		for(String noun : uniqueTermSet){
			fw.write("@attribute " + noun.replaceAll(" ", "_") + " numeric\n");
		}
		fw.write("@data\n");
		
		ArrayList<String> uniqueTerms = new ArrayList<String>(uniqueTermSet);
		for(String key : verb_obj.keySet()){
			Set<String> terms = verb_obj.get(key);
			for(int i = 0; i < uniqueTerms.size() - 1; i++){
				String t = uniqueTerms.get(i);
				if(terms.contains(t)){
					int freq = 0;
					for(String[] pair : allpairs){
						if(stem(removeStopWords(pair[0])).equals(key) && stem(removeStopWords(pair[1])).equals(t)) freq++;
					}				
					fw.write(freq + ",");
				}
				else
					fw.write("0,");
			}	
			String t = uniqueTerms.get(uniqueTerms.size() - 1);
			if(terms.contains(t)){
				int freq = 0;
				for(String[] pair : allpairs){
					if(stem(removeStopWords(pair[0])).equals(key) && stem(removeStopWords(pair[1])).equals(t)) freq++;
				}
			
					fw.write(freq + "\n");
				}
			else
				fw.write("0\n");
			
		}
		
		fw.flush();
		fw.close();
	}
	
	public void createArffObjVerbFreq(LinkedHashMap<String, Set<String>> obj_verb, LinkedHashSet<String> uniqueTermSet, ArrayList<String[]> allpairs) throws IOException{
		FileWriter fw = new FileWriter("objVerbFreq.arff");
		fw.write("@relation objVerb\n");
		for(String verb : uniqueTermSet){
			fw.write("@attribute " + verb + " numeric\n");
		}
		fw.write("@data\n");
		
		ArrayList<String> uniqueTerms = new ArrayList<String>(uniqueTermSet);
		for(String key : obj_verb.keySet()){
			Set<String> terms = obj_verb.get(key);
			for(int i = 0; i < uniqueTerms.size() - 1; i++){
				String t = uniqueTerms.get(i);
				if(terms.contains(t)){
					int freq = 0;
					for(String[] pair : allpairs){
						if(stem(removeStopWords(pair[1])).equals(key) && stem(removeStopWords(pair[0])).equals(t)) freq++;
					}
				
						fw.write(freq + ",");
					}
				else
					fw.write("0,");
			}	
			String t = uniqueTerms.get(uniqueTerms.size() - 1);
			if(terms.contains(t)){
				int freq = 0;
				for(String[] pair : allpairs){
					if(stem(removeStopWords(pair[1])).equals(key) && stem(removeStopWords(pair[0])).equals(t)) freq++;
				}
			
					fw.write(freq + "\n");
				}
			else
				fw.write("0\n");
			
		}
		
		fw.flush();
		fw.close();
	}
	
	private String stem(String term){
		String[] splitted = term.split(" ");
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
