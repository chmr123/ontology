package traceability.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class ActionUnitXMLMain {

	static Set<String> actionunitsHigh = new HashSet<String>();
	static Set<String> actionunitsLow = new HashSet<String>();
	static LinkedHashMap<String, Set<String>> action_unit_onenoun = new LinkedHashMap<String, Set<String>>();
	static Set<String> extractedNouns = new HashSet<String>();
	static Set<String> extractedNouns2 = new HashSet<String>();
	
	//static LinkedHashMap<String, Set<String>> action_unit_low = new LinkedHashMap<String, Set<String>>();
	public static void main(String[] args) throws IOException {
		SimpleHeuristic sh = new SimpleHeuristic();
		LinkedHashMap<String, Set<String>> simpleActionUnits = new LinkedHashMap<String, Set<String>>(); 
		simpleActionUnits= sh.getSimpleActionUnits();
		
		NounPhrases nps = new NounPhrases();
		LinkedHashMap<String, Set<String>> file_np_high = new LinkedHashMap<String, Set<String>>();
		file_np_high = nps.getNounPhrasesHigh();
		
		LinkedHashMap<String, Set<String>> file_np_low = new LinkedHashMap<String, Set<String>>();
		file_np_low = nps.getNounPhrasesLow();
		
		VerbPhrases vps = new VerbPhrases();
		LinkedHashMap<String, Set<String>> file_vp_high = new LinkedHashMap<String, Set<String>>(); 
		file_vp_high = vps.getVerbPhrasesHigh();
		
		LinkedHashMap<String, Set<String>> file_vp_low = new LinkedHashMap<String, Set<String>>();
		file_vp_low = vps.getVerbPhrasesLow();
		
		VerbPhrases2 vps2 = new VerbPhrases2();
		LinkedHashMap<String, Set<String>> file_vp2 = new LinkedHashMap<String, Set<String>>(); 
		file_vp2 = vps2.getActionUnits();
		
		
		
		ArrayList<String> verbs = new ArrayList<String>();
		File verb = new File("verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(verb));
		String line;
		while((line = br.readLine()) != null){
			verbs.add(line);
		}
		
		
		File[] filelisthigh = new File("highxml").listFiles();
		File[] filelistlow = new File("lowxml").listFiles();
		
		int listh = filelisthigh.length;
		int listl = filelistlow.length;
		
		XMLreader xmlreader = new XMLreader("highxml", "lowxml");
		
		
		
		for(int i = 0; i < listh; i++){
			System.out.println("Extracting action units from high file " + i);
			ArrayList<String[]> pairs = new ArrayList<String[]>(); 
			String filename = filelisthigh[i].getName().replace(".xml", "");
			ArrayList<XMLmodel> hlist = xmlreader.getHighDependency(i);
			for (int j=0;j<hlist.size();j++){
	               // System.out.println(hlist.get(j).type+": ("+hlist.get(j).governor+","+hlist.get(j).dependent+")");
				 String[] pair = new String[2];
				 pair[0] = hlist.get(j).governor;
				 pair[1] = hlist.get(j).dependent;
				 pairs.add(pair);
	        }
			extractActionUnitHigh(pairs, verbs, filename, file_np_high);
			
			Set<String> nplist = file_np_high.get(filename);
			Set<String> vplist = file_vp_high.get(filename);
			Set<String> vplist2 = file_vp2.get(filename);
			Set<String> np_simple = simpleActionUnits.get(filename);
			
			ArrayList<String> realnouns = getRealNouns();
			nplist.retainAll(realnouns);
			for(String s : nplist){
				extractedNouns2.add(s);
			}
			//System.out.println(filename + ": " + vplist);
			verbPhrasesAnalysis(filename, vplist, vplist2, nplist, np_simple);
		}
		
		FileWriter fw1 = new FileWriter("actionunitshigh.txt", true);
		for(String s : actionunitsHigh)
			fw1.write(s);
		fw1.flush();
		fw1.close();
		
		for(int i = 0; i < listl; i++){
			System.out.println("Extracting action units from low file " + i);
			ArrayList<String[]> pairs = new ArrayList<String[]>(); 
			String filename = filelistlow[i].getName().replace(".xml", "");
			ArrayList<XMLmodel> llist = xmlreader.getLowDependency(i);
			for (int j=0;j<llist.size();j++){
	               // System.out.println(hlist.get(j).type+": ("+hlist.get(j).governor+","+hlist.get(j).dependent+")");
				 String[] pair = new String[2];
				 pair[0] = llist.get(j).governor;
				 pair[1] = llist.get(j).dependent;
				 pairs.add(pair);
	        }
			extractActionUnitLow(pairs, verbs, filename, file_np_low);
			
			Set<String> nplist = file_np_low.get(filename);
			Set<String> vplist = file_vp_low.get(filename);
			Set<String> vplist2 = file_vp2.get(filename);
			Set<String> np_simple = simpleActionUnits.get(filename);
			
			ArrayList<String> realnouns = getRealNouns();
			nplist.retainAll(realnouns);
			for(String s : nplist){
				extractedNouns2.add(s);
			}
			verbPhrasesAnalysis(filename, vplist, vplist2, nplist, np_simple);
			
		}
		
		FileWriter fw2 = new FileWriter("actionunitslow.txt", true);
		for(String s : actionunitsLow)
			fw2.write(s);
		fw2.flush();
		fw2.close();
		
		FileWriter fw3 = new FileWriter("extractedNouns.txt", true);
		for(String s : extractedNouns){
			fw3.write(s + "\n");
		}
		fw3.flush();
		fw3.close();
		
		FileWriter fw4 = new FileWriter("extractedNouns2.txt", true);
		for(String s : extractedNouns2){
			fw4.write(s + "\n");
		}
		fw4.flush();
		fw4.close();
		
		//Set<String> unextracted = new HashSet<String>();
		/*extractedNouns2.removeAll(extractedNouns);
		
		for(int i = 0; i < listh; i++){
			System.out.println("Extracting more action units from high file " + i);
			ArrayList<String[]> pairs = new ArrayList<String[]>(); 
			String filename = filelisthigh[i].getName().replace(".xml", "");
			ArrayList<XMLmodel> hlist = xmlreader.getHighDependency(i);
			for (int j=0;j<hlist.size();j++){
	               // System.out.println(hlist.get(j).type+": ("+hlist.get(j).governor+","+hlist.get(j).dependent+")");
				 String[] pair = new String[2];
				 pair[0] = hlist.get(j).governor;
				 pair[1] = hlist.get(j).dependent;
				 pairs.add(pair);
	        }
			extractActionUnitHigh(pairs, verbs, filename, file_np_high);
			
			Set<String> nplist = file_np_high.get(filename);
			Set<String> vplist = file_vp_high.get(filename);
			ArrayList<String> realnouns = getRealNouns();
			nplist.retainAll(realnouns);
			for(String s : nplist){
				extractedNouns2.add(s);
			}
			//System.out.println(filename + ": " + vplist);
			verbPhrasesAnalysis2(filename, vplist, nplist);
		}*/
		

	}
	
	public static void extractActionUnitHigh(ArrayList<String[]> pairs, ArrayList<String> verbs, String filename, LinkedHashMap<String, Set<String>> file_np) throws IOException{
		ArrayList<String> realnouns = getRealNouns();
		Set<String> nplist = file_np.get(filename);	
		Set<String> actionunits = new HashSet<String>();
		nplist.retainAll(realnouns);
		for(String[] pair : pairs){
			if(nplist.contains(pair[1]) && verbs.contains(pair[0])){
				actionunitsHigh.add(pair[0] + " " + pair[1] + "\n");
				actionunits.add(pair[0] + " " + pair[1]);
				extractedNouns.add(pair[1]);
			}
		}
		action_unit_onenoun.put(filename, actionunits);
		
	}
	
	public static void extractActionUnitLow(ArrayList<String[]> pairs, ArrayList<String> verbs, String filename, LinkedHashMap<String, Set<String>> file_np) throws IOException{
		ArrayList<String> realnouns = getRealNouns();
		Set<String> nplist = file_np.get(filename);	
		Set<String> actionunits = new HashSet<String>();
		nplist.retainAll(realnouns);
		for(String[] pair : pairs){
			if(nplist.contains(pair[1]) && verbs.contains(pair[0])){
				actionunitsLow.add(pair[0] + " " + pair[1] + "\n");
				actionunits.add(pair[0] + " " + pair[1]);
				extractedNouns.add(pair[1]);
			}
		}
		action_unit_onenoun.put(filename, actionunits);
	}
	
	public static ArrayList<String> getRealNouns() throws IOException {
		ArrayList<String> actionUnits = new ArrayList<String>();
		File file = new File("nouns.txt");
		//File file = new File("shortcutkey.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			actionUnits.add(line.toLowerCase());
		}
		return actionUnits;
	}
	
	public static void verbPhrasesAnalysis(String filename, Set<String> vp, Set<String> vp2, Set<String> np, Set<String> simple) throws IOException{
		Set<String> allActionUnits = new HashSet<String>();
		ArrayList<String> verbs = new ArrayList<String>();
		File verbfile = new File("verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(verbfile));
		String line;
		while((line = br.readLine()) != null){
			verbs.add(line);
		}
		
		//System.out.println("Analyzing file " + filename);
		
		for(String s : vp){
			String[] splitted = s.split(" ");
			String verb = splitted[0];
			ArrayList<String> others = new ArrayList<String>();
			for(int i = 1; i < splitted.length; i++){
				others.add(splitted[i]);
			}
			for(String n : np){
				String[] splittedNoun = n.split("\\s+");
				if(splittedNoun.length > 1){
					boolean contain = true;
					for(String noun : splittedNoun){
						if(!others.contains(noun)){
							contain = false;
							break;
						}
					}
					if(contain == true && verbs.contains(verb)){
						allActionUnits.add(verb + " " + n);
						//fw.write(verb + " " + n + "\n");
						extractedNouns.add(n);
					}
				}
			}
		}
		
		Set<String> au_from_np = action_unit_onenoun.get(filename);
		for(String au : au_from_np){
			allActionUnits.add(au);
			//fw.write(au + "\n");
		}
		
		for(String au : vp2){
			allActionUnits.add(au);
			if(au.indexOf(" ") != -1){
				String noun = au.substring(au.indexOf(" ") + 1);
				extractedNouns.add(noun);
			}
		}
		
		for(String sac : simple){
			allActionUnits.add(sac);
		}
		
		FileWriter fw = new FileWriter("action units.txt", true);
		fw.write(filename + "\n");
		for(String a : allActionUnits){
			fw.write(a + "\n");
		}
		fw.flush();
		fw.close();
	}
	
	public static void verbPhrasesAnalysis2(String filename, Set<String> vp, Set<String> unextractednp) throws IOException{
		ArrayList<String> verbs = new ArrayList<String>();
		File verbfile = new File("verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(verbfile));
		String line;
		while((line = br.readLine()) != null){
			verbs.add(line);
		}
		
		//System.out.println("Analyzing file " + filename);
		FileWriter fw = new FileWriter("action unit from vp.txt", true);
		fw.write(filename + "\n");
		for(String s : vp){
			String[] splitted = s.split(" ");
			String verb = splitted[0];
			ArrayList<String> others = new ArrayList<String>();
			for(int i = 1; i < splitted.length; i++){
				others.add(splitted[i]);
			}
			for(String n : unextractednp){
				String[] splittedNoun = n.split("\\s+");
				if(splittedNoun.length > 1){
					boolean contain = true;
					for(String noun : splittedNoun){
						if(!others.contains(noun)){
							contain = false;
							break;
						}
					}
					if(contain == true && verbs.contains(verb)){
						fw.write(verb + " " + n + "\n");
						extractedNouns.add(n);
					}
				}
				if(splittedNoun.length == 1){
					if(unextractednp.contains(n) && verbs.contains(verb)){
						fw.write(verb + " " + n + "\n");
						extractedNouns.add(n);
					}
				}
			}
		}
		
		Set<String> au_from_np = action_unit_onenoun.get(filename);
		for(String au : au_from_np){
			fw.write(au + "\n");
		}
		fw.flush();
		fw.close();
	}
	


}
