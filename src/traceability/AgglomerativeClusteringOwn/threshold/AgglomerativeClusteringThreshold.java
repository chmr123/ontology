package traceability.AgglomerativeClusteringOwn.threshold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AgglomerativeClusteringThreshold {
	static LinkageStrategy linkage = null;
	LinkedHashMap<String, Set<String>> stemLookUp = new LinkedHashMap<String, Set<String>>();

	public static void main(String[] args) throws IOException {
		System.out.println("No break tie");
		String group = null;
		double threshold = 0.0;
		for (int i = 0; i < args.length; i++) {
			if ("-g".equals(args[i])) {
				group = args[i + 1];
				i++;
			} else if ("-t".equals(args[i])) {
				threshold = Double.parseDouble(args[i + 1]);
				i++;
			}
		}

		/*
		 * if(linkageType.equals("single")){ linkage = new SingleLinkStrategy();
		 * } else if(linkageType.equals("complete")){ linkage = new
		 * CompleteLinkStrategy(); } else if(linkageType.equals("average")){
		 * linkage = new AverageLinkStrategy(); } else{
		 * System.out.println("Wrong linkage type entered!!!"); System.exit(0);
		 * }
		 */

		Vector v = new Vector();
		LinkedHashMap<String, double[]> vectors = null;
		if (group.equals("verb"))
			vectors = v.getVerbVectors();
		else if (group.equals("noun"))
			vectors = v.getNounVectors();
		else {
			System.out.println("Please select verb or noun!");
			System.exit(0);
		}

		String[] names = new String[vectors.keySet().size()];
		// String[] names1 = names;

		ArrayList<String> keys = new ArrayList<String>(vectors.keySet());

		int vectorSize = vectors.size();

		double[][] simOrDistMatrix = new double[vectorSize][vectorSize];

		// ArrayList<ArrayList<String>> clusters = new
		// ArrayList<ArrayList<String>>();

		Similarity sim_dist = new Similarity(vectors);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			names[i] = key;
			for (int j = 0; j < keys.size(); j++) {
				double simOrdistance = 0;
				String key1 = keys.get(i);
				String key2 = keys.get(j);

				//simOrdistance = sim_dist.getSimpleSimilarity(vectors.get(key1), vectors.get(key2)); // Calculate simlarity value
				simOrdistance = sim_dist.getCosineSimilarity(vectors.get(key1), vectors.get(key2)); // Calculate simlarity value
				//System.out.println(simOrdistance);

				if (i == j)
					simOrdistance = 0;
				simOrDistMatrix[i][j] = simOrdistance;
			}
		}

		ArrayList<Set<String>> clustered = new ArrayList<Set<String>>();
		ArrayList<Set<String>> clusters = new ArrayList<Set<String>>();

		for (int i = 0; i < keys.size(); i++) {
			Set<String> cluster = new HashSet<String>();
			cluster.add(keys.get(i));
			for (int j = 0; j < keys.size(); j++) {
				if (i != j && simOrDistMatrix[i][j] >= threshold) {
					cluster.add(keys.get(j));
				}
			}
			clusters.add(cluster);
			
		}
	
		System.out.println("Unclustered size: " + clusters.size());
		//int i = 0;
		while(clusters.size() != 0){
			int size = clusters.size();
			Set<String> cluster_current = clusters.get(0);
			clusters.remove(0);
			ArrayList<Integer> indexToBeRemoved = new ArrayList<Integer>();
			for(int i = 0; i < size - 1; i++){
				Set<String> cluster_next = clusters.get(i);
				if(!Collections.disjoint(cluster_current, cluster_next)){
					indexToBeRemoved.add(i);
					cluster_current.addAll(cluster_next);
				}
			}
			clustered.add(cluster_current);
			//System.out.println(cluster_current);
			Collections.sort(indexToBeRemoved, Collections.reverseOrder());
			for(int i : indexToBeRemoved){
				clusters.remove(i);
			}
		}
	
		System.out.println("Clusters in final list " + clustered.size());

		int clusterid = 1;
		FileWriter fw = new FileWriter(group + " clusters " + threshold + ".txt");
		for(Set<String> cluster : clustered){
			String line = "";
			String [] stemBack = null;
			for(String stemmed : cluster){		
				if(group.equals("verb")){
					stemBack = stemBackVerb(stemmed);
					for(String s : stemBack){
						line = line + s + ",";
					}
				}
				if(group.equals("noun")){
					stemBack = stemBackNoun(stemmed);
					for(String s : stemBack){
						line = line + s + ",";
					}
				}
			}
			fw.write("cluster" + clusterid + ":");	
			line = line.substring(0,line.lastIndexOf(","));
			fw.write(line + "\n");
			clusterid++;
		}
		fw.flush();
		fw.close();
	}

	
	private static String[] stemBackVerb(String stemmed) throws IOException {
		// String beforeStem = "";
		File file = new File("verbStemLookUpTable.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		HashMap<String, String[]> stemMap = new HashMap<String, String[]>();
		while ((line = br.readLine()) != null) {
			String afterStem = line.split(":")[0];
			String beforeStem = line.split(":")[1];
			stemMap.put(afterStem, beforeStem.split(","));
		}
		String[] beforeStem = stemMap.get(stemmed);
		return beforeStem;
	}

	private static String[] stemBackNoun(String stemmed) throws IOException {
		// String beforeStem = "";
		File file = new File("nounStemLookUpTable.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		HashMap<String, String[]> stemMap = new HashMap<String, String[]>();
		while ((line = br.readLine()) != null) {
			String afterStem = line.split(":")[0];
			String beforeStem = line.split(":")[1];
			stemMap.put(afterStem, beforeStem.split(","));
		}
		String[] beforeStem = stemMap.get(stemmed);
		return beforeStem;
	}

}
