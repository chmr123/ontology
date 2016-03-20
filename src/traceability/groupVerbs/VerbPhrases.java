package traceability.groupVerbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class VerbPhrases {
	static ArrayList<String> verbs = new ArrayList<String>();
	public ArrayList<String[]> getVerbObjFromVP(LinkedHashMap<String,LinkedHashSet<String>> allactionunit) throws IOException {
		//String filename = args[0];
		//NounPhrases nps = new NounPhrases();
		//LinkedHashMap<String, Set<String>> file_np = nps.getNounPhrases();
		
		ArrayList<String[]> allpairs = new ArrayList<String[]>();
		String parserModel = "englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		ArrayList<String> stopwords = new ArrayList<String>();
		ArrayList<String> postags = new ArrayList<String>();
		Set<String> allnp = new HashSet<String>();
		Set<String> vp_each = new HashSet<String>();

		//ArrayList<String> realnouns = getRealNouns();
		File file = new File("stopwords.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			stopwords.add(line);
		}
		br.close();

		file = new File("pos tags.txt");
		br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			postags.add(line);
		}

		/*ArrayList<String> realverbs = new ArrayList<String>();
		File verbfile = new File("verbs.txt");
		BufferedReader br1 = new BufferedReader(new FileReader(verbfile));
		while((line = br1.readLine()) != null){
			realverbs.add(line);
		}*/
		
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); 
		GrammaticalStructureFactory gsf = null;
		if (tlp.supportsGrammaticalStructures()) {
			gsf = tlp.grammaticalStructureFactory();
		}
		
		Filter filter = new Filter();
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		int i = 0;
		File[] highlist = new File("high").listFiles();
		File[] lowlist = new File("low").listFiles();
		ArrayList<File> both = new ArrayList(Arrays.asList(highlist));
		both.addAll(Arrays.asList(lowlist));
		for(File f : both){
			ArrayList<String> verbObj = new ArrayList<String>();
			System.out.println("Processing file " + ++i + " " + f.getName());
			String filepath = f.getPath();
			//String filepath = filename;
			DocumentPreprocessor dp = new DocumentPreprocessor(filepath);
			// String[] deliminators = {".","\n"};
			// dp.setSentenceFinalPuncWords(deliminators);
			dp.setSentenceDelimiter("\n");
			for (List<HasWord> sentence : dp) {
				Tree parse = lp.apply(sentence);
				// parse.pennPrint();
				// System.out.println();

				if (gsf != null) {
					GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
					Collection tdl = gs.typedDependenciesCCprocessed();
					// System.out.println(tdl);
					// System.out.println();
				}

				List<Tree> phraseList = new ArrayList<Tree>();
				Set<String> nounphrases = new HashSet<String>();
				for (Tree subtree : parse) {
					if (subtree.label().value().equals("VP")) {
						//System.out.println(subtree.toString());
						//System.out.println();
						Set<String> uniqueNP = new HashSet<String>();
						for(Tree np_subtree : subtree){
							if(np_subtree.label().value().contains("NP")){
								//System.out.println("aaa");
								List<Tree> children = np_subtree.getChildrenAsList();
								String verb = "";
								String phrase = "";
								for(Tree c : children){
									//System.out.println(c.toString());
								
									if(c.label().value().equals("NP") && !c.isLeaf()){
										String np = c.toString();
										np = np.replaceAll("\\([A-Z]+ ", "");
										np = np.replaceAll("\\)", "");
										//System.out.println(np);
										for (String tag : postags) {
											np = np.replace(tag, "");
										}
										String[] terms = np.split("\\s+");
										if (terms.length < 100 && terms.length > 0) {
											String goodtermstop = "";
											for (String s : terms) {
												s = s.toLowerCase();
												if(stopwords.contains(s)) continue;
												goodtermstop = goodtermstop + s + " ";
											}
											if(goodtermstop.lastIndexOf(" ") != -1){
												goodtermstop = goodtermstop.substring(0,goodtermstop.lastIndexOf(" "));
										//System.out.println(goodtermstop);
										//if(realnouns.contains(goodtermstop))
										/*for(String rn : realnouns){
											if(goodtermstop.contains(rn)){
												uniqueNP.add(rn);
												break;
											}
										}*/
											}
											if(subtree.firstChild().label().value().contains("VB")){
												verb = subtree.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", "");
												verbObj.add(verb + " " + goodtermstop);
												//System.out.println(verb + " " + goodtermstop);
												break;
											}
										}
									}
									
									
									//if(c.label().value().contains("NN"))
										phrase = phrase + c.toString().replaceAll("\\([A-Z]+ ", "").replace(")", "") + " ";
									
									
								}
								if(subtree.firstChild().label().value().contains("VB")){
									verb = subtree.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", "");
									if(phrase.length() != 0){
										verbObj.add(verb + " " + phrase);
										//System.out.println(verb + " " + phrase);
									}
								}
								
								break;
							}	
							/*
							if(np_subtree.label().value().contains("NP")){
								//System.out.println("aaa");
								String np = np_subtree.toString();
								np = np.replaceAll("\\([A-Z]+ ", "");
								np = np.replaceAll("\\)", "");
								//System.out.println(np);
								for (String tag : postags) {
									np = np.replace(tag, "");
								}
								String[] terms = np.split("\\s+");
								if (terms.length < 100 && terms.length > 0) {
									String goodtermstop = "";
									for (String s : terms) {
										s = s.toLowerCase();
										if(stopwords.contains(s)) continue;
										goodtermstop = goodtermstop + s + " ";
									}
									if(goodtermstop.lastIndexOf(" ") != -1){
										goodtermstop = goodtermstop.substring(0,goodtermstop.lastIndexOf(" "));
								//System.out.println(goodtermstop);
								//if(realnouns.contains(goodtermstop))
								for(String rn : realnouns){
									if(goodtermstop.contains(rn)){
										uniqueNP.add(rn);
										break;
									}
								}
									}
									if(subtree.firstChild().label().value().contains("VB")){
										System.out.println(subtree.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", "") + " " + goodtermstop);
										break;
									}
								}
							
							}*/
						}
						/*getParentVB(subtree,parse);
						if(uniqueNP.size() != 0){
							for(String n : uniqueNP){
								for(String v : verbs){
									//System.out.println(v + " ");
									if(realverbs.contains(v)){
										actionunits.add(v + " " + n);
										System.out.println(v + " " + n);
										//break;
									}
								}
							}
						}*/
						/*for(String s : uniqueNP){
							System.out.println(s);
						}*/
						
						verbs.clear();
					
						
						phraseList.add(subtree);
						String np = subtree.toString();

						// for(String tag : postags){
						// System.out.println(tag);
						np = np.replaceAll("\\([A-Z]+ ", "");
						np = np.replaceAll("\\)", "");
						// }

						for (String tag : postags) {
							np = np.replace(tag, "");
						}
						String[] terms = np.split("\\s+");
						if (terms.length < 100 && terms.length > 0) {
							//String goodterm = "";
							String goodtermstop = "";
							for (String s : terms) {
								s = s.toLowerCase();
								goodtermstop = goodtermstop + s + " ";
							}
							if(goodtermstop.lastIndexOf(" ") != -1)
								vp_each.add(goodtermstop.substring(0,goodtermstop.lastIndexOf(" ")));
							//if(goodterm.lastIndexOf(" ") != -1)
								//allnp.add(goodterm.substring(0,goodterm.lastIndexOf(" ")));
						}
					}
				}
				//System.out.println(allnp);
			}
			
			Set<String> excludedNouns = new HashSet<String>();
			Set<String> excludedVerbs = new HashSet<String>();
			LinkedHashSet<String> actionunit = new LinkedHashSet<String>();
			
			String filename = f.getName();
			allactionunit.put(filename, actionunit);
			if(verbObj.size() > 0){
				for(int j = 0; j < verbObj.size()-1; j++){
					String current = verbObj.get(j).substring(verbObj.get(j).indexOf(" ")+1);
					String next = verbObj.get(j+1).substring(verbObj.get(j+1).indexOf(" ")+1);
					if(!current.equals(next)){
						String[] pair = new String[2];
						pair[0] = verbObj.get(j).substring(0, verbObj.get(j).indexOf(" ")).toLowerCase().trim();
						pair[1] = verbObj.get(j).substring(verbObj.get(j).indexOf(" ")+1).toLowerCase().trim();
						if(filter.isFiltered(pair[0])) {
							excludedVerbs.add(pair[0]);
							continue;
						}
						if(filter.isFiltered(pair[1])) {
							excludedNouns.add(pair[1]);
							continue;
						}
						if(pair[1].split(" ").length < 5){
							//System.out.println(pair[0] + " " + pair[1]);
							allpairs.add(pair);
							actionunit.add(pair[0] + " " + pair[1]);
						}
					}
				
					//System.out.println(verbObj.get(j));
				}
				
			
				allactionunit.put(filename, actionunit);
				
				String[] pair = new String[2];
				pair[0] = verbObj.get(verbObj.size() - 1).substring(0, verbObj.get(verbObj.size() - 1).indexOf(" ")).toLowerCase().trim();
				pair[1] = verbObj.get(verbObj.size() - 1).substring(verbObj.get(verbObj.size() - 1).indexOf(" ")+1).toLowerCase().trim();
				if(filter.isFiltered(pair[0])) {
					excludedVerbs.add(pair[0]);
					continue;
				}
				if(filter.isFiltered(pair[1])) {
					excludedNouns.add(pair[1]);
					continue;
				}
				if(pair[1].split(" ").length < 5){
					//System.out.println(pair[0] + " " + pair[1]);
					allpairs.add(pair);
					actionunit.add(pair[0] + " " + pair[1]);
				}
				allactionunit.put(filename, actionunit);
			}
			
			
			//System.out.println(filename);
			
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
			
			//System.out.println(verbObj.get(verbObj.size() - 1));
		}
			/*for(String au : actionunits){
				System.out.println(au);
			}*/
			//Set<String> np_each = file_np.get(f.getName());
			//actionUnitAnalysis(f.getName(), vp_each, np_each);
		//} // Loop each file to get verb phrases
		
		//System.out.println();
		return allpairs;
	}
	
	public static void getParentVB(Tree parent, Tree root){
		if(!parent.equals(root)){
			Tree parentOfParent = parent.parent(root);
			if(parentOfParent.label().value().equals("VP") && parentOfParent.firstChild().label().value().contains("VB")){
				//System.out.println(parentOfParent.firstChild());
				System.out.println(parentOfParent.toString());
				verbs.add(parentOfParent.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", ""));
			}
			getParentVB(parentOfParent, root);
		}
	}
	
	public static void actionUnitAnalysis(String filename, Set<String> vp, Set<String> np) throws IOException{
		
		ArrayList<String> verbs = new ArrayList<String>();
		File verbfile = new File("verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(verbfile));
		String line;
		while((line = br.readLine()) != null){
			verbs.add(line);
		}
		
		System.out.println("Analyzing file " + filename);
		FileWriter fw = new FileWriter("action unit.txt", true);
		fw.write(filename + "\n");
		for(String s : vp){
			String[] splitted = s.split(" ");
			String verb = splitted[0];
			ArrayList<String> others = new ArrayList<String>();
			for(int i = 1; i < splitted.length; i++){
				others.add(splitted[i]);
			}
			for(String n : np){
				if(others.contains(n))
					fw.write(verb + " " + n + "\n");
			}
		}
		fw.flush();
		fw.close();
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
}
