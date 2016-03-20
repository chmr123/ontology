package traceability.groupVerbs.cm1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import weka.core.Stopwords;


public class VerbObj {
	static Stemmer stemmer = new Stemmer();
	ArrayList<String> stopwords = new ArrayList<String>();
	LinkedHashMap<String, ArrayList<String>> stemLookup = new LinkedHashMap<String, ArrayList<String>>();
	public VerbObj() throws IOException{
		File file = new File("stopwords.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			stopwords.add(line);
		}
		br.close();
	}
	public void getVerbObj(ArrayList<String[]> pairs_vp) throws IOException {
	//public void getVerbObj() throws IOException {
		File[] filelisthigh = new File("highxml").listFiles();
		File[] filelistlow = new File("lowxml").listFiles();
		
		int listh = filelisthigh.length;
		int listl = filelistlow.length;
		
		XMLreader xmlreader = new XMLreader("highxml", "lowxml");
	
		
		LinkedHashSet<String> uniqueVerbs = new LinkedHashSet<String>();
		LinkedHashMap<String, Set<String>> verb_obj = new LinkedHashMap<String, Set<String>>();
		ArrayList<String[]> pairs = new ArrayList<String[]>(); 
		pairs.addAll(pairs_vp);
		ArrayList<String> allNouns = new ArrayList<String>();
		LinkedHashSet<String> uniqueNouns = new LinkedHashSet<String>();
		LinkedHashSet<String> uniqueNounsNoStem = new LinkedHashSet<String>();
		LinkedHashSet<String> excludedNouns = new LinkedHashSet<String>();
		LinkedHashSet<String> excludedVerbs = new LinkedHashSet<String>();
		
		
		Filter f = new Filter();
		for(int i = 0; i < listh; i++){
			//System.out.println("Extracting verb-object from high file " + i);
			//String filename = filelisthigh[i].getName().replace(".xml", "");
			ArrayList<XMLmodel> hlist = xmlreader.getHighDependency(i);
			for (int j=0;j<hlist.size();j++){
	               // System.out.println(hlist.get(j).type+": ("+hlist.get(j).governor+","+hlist.get(j).dependent+")");
				 String[] pair = new String[2];
				 pair[0] = hlist.get(j).governor.toLowerCase();
				 pair[1] = hlist.get(j).dependent.toLowerCase();
				 if(f.isFiltered(pair[0])){
					 excludedVerbs.add(pair[0]);
					 //fw1.write(pair[0] + " " + pair[1] + "\n");
					 continue;
				 }
				 
				 if(f.isFiltered(pair[1])){
					 excludedNouns.add(pair[1]);
					 //fw1.write(pair[0] + " " + pair[1] + "\n");
					 continue;
				 }
				 
				 if(pair[1].equals("@") || pair[1].equals("~")) continue;
				 pairs.add(pair);
				 allNouns.add(stem(removeStopWords(pair[1])));
				 //uniqueNouns.add(stem(removeStopWords(pair[1])));
	        }
		}
		
		for(int i = 0; i < listl; i++){
			//System.out.println("Extracting verb-object from low file " + i);
			//ArrayList<String[]> pairs = new ArrayList<String[]>(); 
			//String filename = filelistlow[i].getName().replace(".xml", "");
			ArrayList<XMLmodel> llist = xmlreader.getLowDependency(i);
			for (int j=0;j<llist.size();j++){
	               // System.out.println(hlist.get(j).type+": ("+hlist.get(j).governor+","+hlist.get(j).dependent+")");
				 String[] pair = new String[2];
				 pair[0] = llist.get(j).governor.toLowerCase();
				 pair[1] = llist.get(j).dependent.toLowerCase();
				 if(f.isFiltered(pair[0])){
					 excludedVerbs.add(pair[0]);
					 continue;
				 }
				 
				 if(f.isFiltered(pair[1])){
					 excludedNouns.add(pair[1]);
					 continue;
				 }
				 
				 if(pair[1].equals("@") || pair[1].equals("~")) continue;
				 pairs.add(pair);
				 allNouns.add(stem(removeStopWords(pair[1])));
				 //uniqueNouns.add(stem(removeStopWords(pair[1])));
	        }		
		}
		
		FileWriter fw1 = new FileWriter("excluded nouns.txt", true);
		for(String s : excludedNouns){
			fw1.write(s + "\n");
		}
		fw1.flush();
		fw1.close();
		
		FileWriter fw2 = new FileWriter("excluded verbs.txt", true);
		for(String s : excludedVerbs){
			fw2.write(s + "\n");
		}
		fw2.flush();
		fw2.close();
		
		FileWriter fw3 = new FileWriter("allpairs.txt");
		for(String[] pair : pairs){
			 fw3.write(stem(removeStopWords(pair[0])) + " " + stem(removeStopWords(pair[1])) + "\n");
			 String stemmedVerb = stem(pair[0]);
			 uniqueVerbs.add(stemmedVerb);
			 uniqueNouns.add(stem(removeStopWords(pair[1])));
			 uniqueNounsNoStem.add(pair[1]);
		}	
		fw3.flush();
		fw3.close();
		
		FileWriter fw4 = new FileWriter("verb_objs.txt", true);
		for(String verb : uniqueVerbs){	
			//if(f.getIDF(verb) < 1) continue;
			Set<String> objects = new HashSet<String>();
			for(String[] pair : pairs){
				if(stem(pair[0]).equals(verb)){
					//if(f.getIDF(pair[1]) < 1) continue;
					String stemmedObject = stem(removeStopWords(pair[1]));
					objects.add(stemmedObject);
				}
			}
			fw4.write(verb + ":" + objects + "\n");
			verb_obj.put(verb, objects);
		}
		fw4.flush();
		fw4.close();
		
		FileWriter fw = new FileWriter("allverbs.txt");
		for(String key : verb_obj.keySet()){
			//System.out.println(key + " " + verb_obj.get(key)); 
			fw.write(key + "\n");
		}
		fw.flush();
		fw.close();
		//System.out.println(allNouns.size());
		//System.out.println(uniqueNouns.size());
		//System.out.println(verb_obj.size());
		
		
		Arff arff = new Arff();
		arff.createArffVerbObjAP(verb_obj,uniqueNouns);
		arff.createArffVerbObjFreq(verb_obj, uniqueNouns, pairs);

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
