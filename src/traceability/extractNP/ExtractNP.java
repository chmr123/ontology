package traceability.extractNP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;

public class ExtractNP {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//String filename = args[0];
		String parserModel = "englishPCFG.ser.gz";
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		ArrayList<String> stopwords = new ArrayList<String>();
		ArrayList<String> postags = new ArrayList<String>();
		Set<String> allnp = new HashSet<String>();
		Set<String> allnpwithStopWords = new HashSet<String>();

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
		File[] highlist = new File("Req").listFiles();
		File[] lowlist = new File("UC").listFiles();
		ArrayList<File> both = new ArrayList(Arrays.asList(highlist));
		both.addAll(Arrays.asList(lowlist));
		for (File f : both) {
			System.out.println("Processing file " + ++i);
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
								goodtermstop = goodtermstop + s + " ";
								if(stopwords.contains(s)) continue;
									goodterm = goodterm + s + " ";
							}
							if(goodtermstop.lastIndexOf(" ") != -1)
								allnpwithStopWords.add(goodtermstop.substring(0,goodtermstop.lastIndexOf(" ")));
							if(goodterm.lastIndexOf(" ") != -1)
								allnp.add(goodterm.substring(0,goodterm.lastIndexOf(" ")));
						}
					}
				}
				//System.out.println(allnp);
			}
		}
		ArrayList<String> actionUnits = getActionUnits();
		filter(actionUnits, allnp,allnpwithStopWords);
	}

	public static void filter(ArrayList<String> actionUnits, Set<String> nounPhrases, Set<String> nounPhrasesWithStopwords) {
		//for(double t = 0.1; t <=1; t = t + 0.1){
		double t = 0.4;
		Set<String> multipleterm = new HashSet<String>();
		//Set<String> np_copy = nounPhrasesWithStopwords;
		nounPhrases.retainAll(actionUnits);
		actionUnits.removeAll(nounPhrases);
		/*for(String n : nounPhrasesWithStopwords){
			String[] x = n.split("\\s+");
			for(String a : actionUnits){
				String[] y = a.split(" ");
				if(similarity(x,y) > t) multipleterm.add(a);
			}
		}*/
		//actionUnits.removeAll(multipleterm);
		//System.out.println("Threshold " + t);
		System.out.println("Left noun phrases: " + nounPhrases.size());
		//System.out.println("Left multiple noun phrases: " + multipleterm.size());
		System.out.println("Actual action units: " + actionUnits.size());
		for(String s : actionUnits){
			//if(s.contains("file"))
				System.out.println(s);
		}
		System.out.println();
		//}
		
	}

	public static ArrayList<String> getActionUnits() throws IOException {
		ArrayList<String> actionUnits = new ArrayList<String>();
		File file = new File("action units 1.txt");
		//File file = new File("shortcutkey.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while ((line = br.readLine()) != null) {
			actionUnits.add(line.toLowerCase());
		}
		return actionUnits;
	}
	
	public static double similarity(String[] x, String[] y) {
        double sim=0.0d;
        if ( (x!=null && y!=null) && (x.length>0 || y.length>0)) {
                        sim = similarity(Arrays.asList(x), Arrays.asList(y)); 
        } else {
                throw new IllegalArgumentException("The arguments x and y must be not NULL and either x or y must be non-empty.");
        }
        return sim;
    }
    
    private static double similarity(List<String> x, List<String> y) {
        
        if( x.size() == 0 || y.size() == 0 ) {
            return 0.0;
        }
        
        Set<String> unionXY = new HashSet<String>(x);
        unionXY.addAll(y);
        
        Set<String> intersectionXY = new HashSet<String>(x);
        intersectionXY.retainAll(y);

        return (double) intersectionXY.size() / (double) unionXY.size(); 
    }
}
