package traceability.groupVerbs.cm1;

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
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class VerbPhrasesOld {
	static ArrayList<String> verbs = new ArrayList<String>();
	public ArrayList<String[]> getVerbObjFromVP() throws IOException {
		ArrayList<String[]> verb_obj_vp = new ArrayList<String[]>();
		//NounPhrases nps = new NounPhrases();
		//LinkedHashMap<String, Set<String>> file_np = nps.getNounPhrases();
		
		String parserModel = "englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		
		ArrayList<String> postags = new ArrayList<String>();
		Set<String> allnp = new HashSet<String>();
		Set<String> vp_each = new HashSet<String>();

		ArrayList<String> stopwords = new ArrayList<String>();
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

		ArrayList<String> realverbs = new ArrayList<String>();
		//File verbfile = new File("verbs.txt");
		//BufferedReader br1 = new BufferedReader(new FileReader(verbfile));
		//while((line = br1.readLine()) != null){
			//realverbs.add(line);
		//}
		
		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a
																// PennTreebankLanguagePack
																// for English
		GrammaticalStructureFactory gsf = null;
		if (tlp.supportsGrammaticalStructures()) {
			gsf = tlp.grammaticalStructureFactory();
		}
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		int i = 0;
		File[] highlist = new File("high").listFiles();
		File[] lowlist = new File("low").listFiles();
		ArrayList<File> both = new ArrayList(Arrays.asList(highlist));
		both.addAll(Arrays.asList(lowlist));
		for(File f : both){
			Set<String> actionunits = new HashSet<String>();
			System.out.println("Processing file " + ++i);
			String filepath = f.getPath();
			DocumentPreprocessor dp = new DocumentPreprocessor(filepath);
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
							if((np_subtree.label().value().equals("NP") || np_subtree.label().value().equals("PP"))){
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
										//if(realnouns.contains(goodtermstop))
										/*for(String rn : realnouns){
											if(goodtermstop.contains(rn)){
												uniqueNP.add(rn);
												break;
											}
										}*/
									}
									if(subtree.firstChild().label().value().equals("VB")){
										String term = "";
										//List<Tree> childern = np_subtree.getChildrenAsList();		
										List<Tree> leaves = np_subtree.getLeaves();								
										for(Tree l : leaves) term = term + l.toString() + " ";
						
										
										//System.out.println(term);
										
										String[] pair = new String[2];
										String verb = subtree.firstChild().toString().replaceAll("\\(VB[A-Z]? ", "").replace(")", "");
										pair[0] = verb.toLowerCase();
										pair[1] = term.trim().toLowerCase();
										verb_obj_vp.add(pair);
										//System.out.println(verb + " " + term);
										//System.out.println(verb + " " + goodtermstop);
										break;
									}
								}
							}
								
						}
						
						verbs.clear();
				
						phraseList.add(subtree);
						String np = subtree.toString();
						np = np.replaceAll("\\([A-Z]+ ", "");
						np = np.replaceAll("\\)", "");
						// }

						for (String tag : postags) {
							np = np.replace(tag, "");
						}
						String[] terms = np.split("\\s+");
						if (terms.length < 100 && terms.length > 0) {
							String goodterm = "";
							String goodtermstop = "";
							for (String s : terms) {
								s = s.toLowerCase();
								goodtermstop = goodtermstop + s + " ";
							}
							if(goodtermstop.lastIndexOf(" ") != -1)
								vp_each.add(goodtermstop.substring(0,goodtermstop.lastIndexOf(" ")));
						}
					}
				}
			}
		} // Loop each file to get verb phrases
		return verb_obj_vp;
		//System.out.println();
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
}
