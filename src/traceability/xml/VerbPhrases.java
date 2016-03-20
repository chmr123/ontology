package traceability.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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

public class VerbPhrases { 
	public LinkedHashMap<String, Set<String>> getVerbPhrasesHigh() throws IOException{
		//String filename = args[0];
		LinkedHashMap<String, Set<String>> file_np = new LinkedHashMap<String, Set<String>>();
		String parserModel = "englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		ArrayList<String> stopwords = new ArrayList<String>();
		ArrayList<String> postags = new ArrayList<String>();
		Set<String> allnp = new HashSet<String>();
		

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
		ArrayList<File> both = new ArrayList(Arrays.asList(highlist));
		for(File f : highlist){
			Set<String> allnpwithStopWords = new HashSet<String>();
			System.out.println("Processing file for verb phrases " + ++i);
			String filepath = f.getPath();
			DocumentPreprocessor dp = new DocumentPreprocessor(filepath);
			// String[] deliminators = {".","\n"};
			// dp.setSentenceFinalPuncWords(deliminators);
			dp.setSentenceDelimiter("\n");
			for (List<HasWord> sentence : dp) {
				Tree parse = lp.apply(sentence);
				 //parse.pennPrint();
				// System.out.println();

				if (gsf != null) {
					GrammaticalStructure gs = gsf
							.newGrammaticalStructure(parse);
					Collection tdl = gs.typedDependenciesCCprocessed();
					// System.out.println(tdl);
					// System.out.println();
				}

				List<Tree> phraseList = new ArrayList<Tree>();
				Set<String> nounphrases = new HashSet<String>();
				for (Tree subtree : parse) {
					if (subtree.label().value().equals("VP")) {
						
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
							String goodterm = "";
							String goodtermstop = "";
							for (String s : terms) {
								s = s.toLowerCase();
								if(stopwords.contains(s)) continue;
								goodtermstop = goodtermstop + s + " ";
							}
							if(goodtermstop.lastIndexOf(" ") != -1)
								allnpwithStopWords.add(goodtermstop.substring(0,goodtermstop.lastIndexOf(" ")));
							//if(goodterm.lastIndexOf(" ") != -1)
								//allnp.add(goodterm.substring(0,goodterm.lastIndexOf(" ")));
						}
					}
				}
			}	
			file_np.put(f.getName(), allnpwithStopWords);
		}
		return file_np;
	}
	
	public LinkedHashMap<String, Set<String>> getVerbPhrasesLow() throws IOException{
		//String filename = args[0];
		LinkedHashMap<String, Set<String>> file_np = new LinkedHashMap<String, Set<String>>();
		String parserModel = "englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		ArrayList<String> stopwords = new ArrayList<String>();
		ArrayList<String> postags = new ArrayList<String>();
		Set<String> allnp = new HashSet<String>();
		

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
		File[] lowlist = new File("low").listFiles();
		//ArrayList<File> both = new ArrayList(Arrays.asList(highlist));
		//both.addAll(Arrays.asList(lowlist));
		for(File f : lowlist){
			Set<String> allnpwithStopWords = new HashSet<String>();
			System.out.println("Processing file for verb phrases" + ++i);
			String filepath = f.getPath();
			DocumentPreprocessor dp = new DocumentPreprocessor(filepath);
			// String[] deliminators = {".","\n"};
			// dp.setSentenceFinalPuncWords(deliminators);
			dp.setSentenceDelimiter("\n");
			for (List<HasWord> sentence : dp) {
				Tree parse = lp.apply(sentence);
				 //parse.pennPrint();
				// System.out.println();

				if (gsf != null) {
					GrammaticalStructure gs = gsf
							.newGrammaticalStructure(parse);
					Collection tdl = gs.typedDependenciesCCprocessed();
					// System.out.println(tdl);
					// System.out.println();
				}

				List<Tree> phraseList = new ArrayList<Tree>();
				Set<String> nounphrases = new HashSet<String>();
				for (Tree subtree : parse) {
					if (subtree.label().value().equals("VP")) {

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
							String goodterm = "";
							String goodtermstop = "";
							for (String s : terms) {
								s = s.toLowerCase();
								if(stopwords.contains(s)) continue;
								goodtermstop = goodtermstop + s + " ";
							}
							if(goodtermstop.lastIndexOf(" ") != -1)
								allnpwithStopWords.add(goodtermstop.substring(0,goodtermstop.lastIndexOf(" ")));
							//if(goodterm.lastIndexOf(" ") != -1)
								//allnp.add(goodterm.substring(0,goodterm.lastIndexOf(" ")));
						}
					}
				}
			}	
			file_np.put(f.getName(), allnpwithStopWords);
		}
		return file_np;
	}
}
