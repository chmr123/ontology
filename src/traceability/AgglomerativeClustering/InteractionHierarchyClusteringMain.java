package traceability.AgglomerativeClustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;




public class InteractionHierarchyClusteringMain {

	public static void main(String[] args) throws IOException {
		/*String[] names = new String[] { "O1", "O2", "O3", "O4", "O5", "O6" };
		double[][] distances = new double[][] { 
		    { 0, 1, 9, 7, 11, 14 },
		    { 1, 0, 4, 3, 8, 10 }, 
		    { 9, 4, 0, 9, 2, 8 },
		    { 7, 3, 9, 0, 6, 13 }, 
		    { 11, 8, 2, 6, 0, 10 },
		    { 14, 10, 8, 13, 10, 0 }};*/
		
		String option = args[0];
		Vector v = new Vector();
		LinkedHashMap<String, int[]> vectors = null;
		if(option.equals("verb"))
			vectors = v.getVerbVectors();
		else if(option.equals("noun"))
			vectors = v.getNounVectors();
		else
		{
			System.out.println("Please select verb or noun!");
			System.exit(0);
		}
		
		String[] names = new String[vectors.keySet().size()];
		//String[] names1 = names;
		List<String> names1 = Arrays.asList(names);
		
		//LinkedHashMap<String, int[]> nounVectors = v.getNounVectors();

		List<String> keys = new ArrayList<String>(vectors.keySet());
		//List<String> nounKey = new ArrayList<String>(nounVectors.keySet());
		
		int vectorSize = vectors.size();
		
	    double[][] distances = new double[vectorSize][vectorSize];
		
		//ArrayList<ArrayList<String>> clusters = new ArrayList<ArrayList<String>>();
		
		Similarity similarity = new Similarity(vectors);
		for(int i = 0; i < keys.size(); i++){
			String key = keys.get(i);
			names[i] = key;
			for(int j = 0; j < keys.size(); j++){
				String key1 = keys.get(i);
				String key2 = keys.get(j);
				double similarityValue = similarity.getSimpleSimilarity(vectors.get(key1), vectors.get(key2));
				int[] vector1 = vectors.get(key1);
				int[] vector2 = vectors.get(key2);
				//double similarityValue = similarity.getEuclideanDistance(vector1, vector2);
				if(i == j) similarityValue = 0;
				distances[i][j] = similarityValue;	
			}
		}
		
		Set<Double> uniqueSim = new HashSet<Double>();
		for(int i = 0; i < keys.size(); i++){
			for(int j = i + 1; j < keys.size(); j++){
				if(distances[i][j] == 6){
					System.out.println(names[i] + " " + names[j]);
				}
			}
		}
		
		System.out.println("Max is " + Collections.max(uniqueSim));
		
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		alg.performClustering1(distances, names, new AverageLinkageStrategy());
		//Cluster cluster = alg.performClustering(distances, names, new SingleLinkageStrategy());
		//List<Cluster> clusters = cluster.getChildren();
		
		
	}

}
