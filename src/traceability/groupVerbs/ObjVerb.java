package traceability.groupVerbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;


public class ObjVerb {
	static Stemmer stemmer = new Stemmer();
	ArrayList<String> stopwords = new ArrayList<String>();
	public ObjVerb() throws IOException{
		File file = new File("stopwords.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			stopwords.add(line);
		}
		br.close();
	}
	
	public void getObjVerb(ArrayList<String[]> pairs_vp) throws IOException {
	//public void getObjVerb() throws IOException {
		File[] filelisthigh = new File("highxml").listFiles();
		File[] filelistlow = new File("lowxml").listFiles();
		
		int listh = filelisthigh.length;
		int listl = filelistlow.length;
		
		XMLreader xmlreader = new XMLreader("highxml", "lowxml");
		
		LinkedHashSet<String> uniqueNouns = new LinkedHashSet<String>();
		LinkedHashMap<String, Set<String>> obj_verb = new LinkedHashMap<String, Set<String>>();
		ArrayList<String[]> pairs = new ArrayList<String[]>(); 
		pairs.addAll(pairs_vp);
		ArrayList<String> allVerbs = new ArrayList<String>();
		LinkedHashSet<String> uniqueVerbs = new LinkedHashSet<String>();
		LinkedHashSet<String> uniqueVerbsNoStem = new LinkedHashSet<String>();
		
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
				 if(f.isFiltered(pair[0]) || f.isFiltered(pair[1])) continue;
				 if(pair[1].equals("@") || pair[1].equals("~")) continue;
				 pairs.add(pair);
				 allVerbs.add(stem(pair[0]));
				 //uniqueVerbs.add(stem(pair[0]));
	        }
		}
		
		for(int i = 0; i < listl; i++){
			//System.out.println("Extracting verb-object from high file " + i);
			
			//String filename = filelisthigh[i].getName().replace(".xml", "");
			ArrayList<XMLmodel> llist = xmlreader.getLowDependency(i);
			for (int j=0;j<llist.size();j++){
	               // System.out.println(hlist.get(j).type+": ("+hlist.get(j).governor+","+hlist.get(j).dependent+")");
				 String[] pair = new String[2];
				 pair[0] = llist.get(j).governor.toLowerCase();
				 pair[1] = llist.get(j).dependent.toLowerCase();
				 if(f.isFiltered(pair[0]) || f.isFiltered(pair[1])) continue;
				 if(pair[1].equals("@") || pair[1].equals("~")) continue;
				 pairs.add(pair);
				 allVerbs.add(stem(pair[0]));
				 //uniqueVerbs.add(stem(pair[0]));
	        }
		}
		
		//FileWriter fw3 = new FileWriter("allpairs_obj_verbs.txt");
		for(String[] pair : pairs){
			// fw3.write(pair[0] + " " + removeStopWords(pair[1]) + "\n");
			 String stemmedNoun = stem(removeStopWords(pair[1]));
			 uniqueNouns.add(stemmedNoun);
			 uniqueVerbs.add(stem(pair[0]));
			 uniqueVerbsNoStem.add(pair[0]);
		}
		//fw3.flush();
		//fw3.close();
		
		FileWriter fw4 = new FileWriter("obj_verbs.txt", true);
		for(String noun : uniqueNouns){
			//if(f.getIDF(noun) < 1) continue;
			Set<String> verbs = new HashSet<String>();
			for(String[] pair : pairs){
				if(stem(removeStopWords(pair[1])).equals(noun)){
					//if(f.getIDF(pair[0]) < 1) continue;
					String stemmedVerb = stem(pair[0]);
					verbs.add(stemmedVerb);
				}
			}
			fw4.write(noun + ":" + verbs + "\n");
			obj_verb.put(noun, verbs);
		}
		fw4.flush();
		fw4.close();
		
		
		FileWriter fw = new FileWriter("allnouns.txt");
		for(String key : obj_verb.keySet()){
			//System.out.println(key + " " + verb_obj.get(key));
			fw.write(key + "\n");
		}
		fw.flush();
		fw.close();
		//System.out.println(allVerbs.size());
		//System.out.println(uniqueVerbs.size());
		//System.out.println(obj_verb.size());

		
		Arff arff = new Arff();
		arff.createArffObjVerbAP(obj_verb,uniqueVerbs);
		arff.createArffObjVerbFreq(obj_verb, uniqueVerbs, pairs);
	}
	
	public String stem(String term){
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
	
	public String removeStopWords(String term){
		//System.out.println("Term is " + term);
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
