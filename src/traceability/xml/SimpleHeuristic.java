package traceability.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;

import edu.stanford.nlp.process.DocumentPreprocessor;

public class SimpleHeuristic {
	public LinkedHashMap<String, Set<String>> getSimpleActionUnits() throws IOException {
		LinkedHashMap<String, Set<String>> allactionunits = new LinkedHashMap<String, Set<String>>();
		ArrayList<String> verbs = new ArrayList<String>();
		File verbfile = new File("verbs.txt");
		BufferedReader br1 = new BufferedReader(new FileReader(verbfile));
		String term;
		while ((term = br1.readLine()) != null) {
			verbs.add(term);
		}
		br1.close();

		ArrayList<String> nouns = new ArrayList<String>();
		File nounfile = new File("nouns.txt");
		BufferedReader br2 = new BufferedReader(new FileReader(nounfile));
		while ((term = br2.readLine()) != null) {
			nouns.add(term);
		}
		br2.close();

		File[] highfiles = new File("high").listFiles();
		File[] lowfiles = new File("low").listFiles();
		int count = 0;

		FileWriter fw = new FileWriter("simpe action units.txt", true);
		for (File arg : highfiles) {
			fw.write(arg.getName() + "\n"); 
			System.out.println("Applying simple heuristic for high file " + count++);
			// option #1: By sentence.
			Set<String> actionUnits = new HashSet<String>();

			DocumentPreprocessor dp = new DocumentPreprocessor("high\\"+ arg.getName());
			String[] tokenizor = { "\n" };
			// dp.setSentenceFinalPuncWords(tokenizor);
			dp.setSentenceDelimiter("\n");
			for (List sentence : dp) {
				Set<String> extractedNoun = new HashSet<String>();
				Set<String> extractedVerb = new HashSet<String>();
				String line = "";
				for (int i = 0; i < sentence.size(); i++) {
					line = line + sentence.get(i).toString().toLowerCase() + " ";
				}
				// System.out.println(line);
				for (String noun : nouns) {
					if (line.contains(noun)) {
						extractedNoun.add(noun);
					}
				}
				for (String verb : verbs) {
					if (line.contains(verb)) {
						extractedVerb.add(verb);
					}
				}
				
				//All combination
				/*for (String verb : extractedVerb) {
					for (String noun : extractedNoun) {
						String actionunit = verb + " " + noun;
						fw.write(actionunit + "\n");
						actionUnits.add(actionunit);
					}
				}*/
				
				//Closest combination
				
				ArrayList<String> Nounlist = new ArrayList<String>(extractedNoun);
				Set<String> biggerNouns = extractedNoun;
				for(int i = 0; i < Nounlist.size(); i++){
					for(int j = 0; j < Nounlist.size(); j++){
						if(i == j) continue;
						if(Nounlist.get(i).contains(Nounlist.get(j))){
							System.out.println(Nounlist.get(j));
							biggerNouns.remove(Nounlist.get(j));
						}
					}
				}
				
				
				for (String noun : biggerNouns) {
					String verb = getClosestVerb(line, noun, extractedVerb);
					if(verb.length() == 0)
						continue;
					String actionunit = verb + " " + noun;
					fw.write(actionunit + "\n");
					actionUnits.add(actionunit);
				}
			}

			allactionunits.put(arg.getName(), actionUnits);

		}
		
		

		count = 0;
		for (File arg : lowfiles) {
			fw.write(arg.getName() + "\n"); 
			System.out.println("Applying simple heuristic for low file " + count++);
			// option #1: By sentence.
			Set<String> actionUnits = new HashSet<String>();

			DocumentPreprocessor dp = new DocumentPreprocessor("low\\" + arg.getName());
			String[] tokenizor = { "\n" };
			// dp.setSentenceFinalPuncWords(tokenizor);
			dp.setSentenceDelimiter("\n");
			for (List sentence : dp) {
				Set<String> extractedNoun = new HashSet<String>();
				Set<String> extractedVerb = new HashSet<String>();
				String line = "";
				for (int i = 0; i < sentence.size(); i++) {
					line = line + sentence.get(i).toString().toLowerCase() + " ";
				}
				// System.out.println(line);
				for (String noun : nouns) {
					if (line.contains(noun)) {
						extractedNoun.add(noun);
					}
				}
				for (String verb : verbs) {
					if (line.contains(verb)) {
						extractedVerb.add(verb);
					}
				}
				
				//All combination
				/*for (String verb : extractedVerb) {
					for (String noun : extractedNoun) {
						String actionunit = verb + " " + noun;
						fw.write(actionunit + "\n");
						actionUnits.add(actionunit);
					}
				}*/
				
				//Closest combination
				/*if(arg.getName().equals("UC-WB5.txt"))
					System.out.println(line);*/
				for (String noun : extractedNoun) {
					String verb = getClosestVerb(line, noun, extractedVerb);
					if(verb.length() == 0)
						continue;
					String actionunit = verb + " " + noun;
					fw.write(actionunit + "\n");
					actionUnits.add(actionunit);
				}

			}
			allactionunits.put(arg.getName(), actionUnits);
		}
		fw.flush();
		fw.close();
		return allactionunits;
	}

	public String getClosestVerb(String sentence, String property, Set<String> verbs) {
		String closestVerb = "";
		List<String> list = Arrays.asList(sentence.split(" "));
		int frequency = Collections.frequency(list, property);
		int pIndex = sentence.indexOf(property);
		if(sentence.indexOf(property) != -1){
			String sub_before = sentence.substring(0, sentence.indexOf(property));
		   
			String[] splitted_before = sub_before.split(" ");
			/*if(splitted_before[0].equals("press"))
				System.out.println(splitted_before[0] + " " + property);*/
			if(property.equals("ctrl+c"))
				System.out.println(splitted_before[0] + " " + property);
		 
			int end = splitted_before.length - 1;
			for (int i = end; i >= 0; i--) {
				if (verbs.contains(splitted_before[i])){
					closestVerb = splitted_before[i];
					break;
				}
			}
			
			if(closestVerb.length() == 0){
				 String sub_after = sentence.substring(sentence.indexOf(property) + property.length());
				   String[] splitted_after = sub_after.split(" ");
				   for (int i = 0; i < splitted_after.length; i++) {
						if (verbs.contains(splitted_after[i])){
							closestVerb = splitted_after[i];
							break;
						}
					}
			}
		}
		
		return closestVerb;
	}
}
