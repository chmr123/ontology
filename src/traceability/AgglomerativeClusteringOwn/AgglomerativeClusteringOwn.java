package traceability.AgglomerativeClusteringOwn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AgglomerativeClusteringOwn {
	static LinkageStrategy linkage = null;
	public static void main(String[] args) throws IOException {
		System.out.println("With break tie");
		String group = null;
		String function = null;
		String linkageType = null;
		int clusterNum = 10;
		for(int i = 0;i < args.length;i++) {	
		       if ("-g".equals(args[i])) {
		    	   group = args[i+1];
		          i++;
		      }else if ("-f".equals(args[i])) {
		    	  function = args[i+1];
		          i++;
		      }
		      else if ("-l".equals(args[i])) {
		    	  linkageType = args[i+1];
		          i++;
		      }else if ("-n".equals(args[i])) {
		    	  clusterNum = Integer.parseInt(args[i+1]);
		          i++;
		      }
		    }	
		
		if(linkageType.equals("single")){
			linkage = new SingleLinkStrategy();
		}
		else if(linkageType.equals("complete")){
			linkage = new CompleteLinkStrategy();
		}
		else if(linkageType.equals("average")){
			linkage = new AverageLinkStrategy();
		}
		else{
			System.out.println("Wrong linkage type entered!!!");
			System.exit(0);
		}
		
	
		Vector v = new Vector();
		Frequency f = new Frequency();
		LinkedHashMap<String, double[]> vectors = null;
		LinkedHashMap<String, double[]> vectorsFreq = null;
		if (group.equals("verb")){
			vectors = v.getVerbVectors();
			vectorsFreq = f.getVerbVectorsFreq();
		}
		else if (group.equals("noun")){
			vectors = v.getNounVectors();
			vectorsFreq = f.getNounVectorsFreq();
		}
		else {
			System.out.println("Please select verb or noun!");
			System.exit(0);
		}

		String[] names = new String[vectors.keySet().size()];
		// String[] names1 = names;
		List<String> names1 = Arrays.asList(names);

		ArrayList<String> keys = new ArrayList<String>(vectors.keySet());

		int vectorSize = vectors.size();

		double[][] simOrDistMatrix = new double[vectorSize][vectorSize];
		double[][] simOrDistMatrixFreq = new double[vectorSize][vectorSize];

		// ArrayList<ArrayList<String>> clusters = new
		// ArrayList<ArrayList<String>>();

		Similarity sim_dist = new Similarity(vectors);
		
		//Calculate distance based on binary vectors
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			names[i] = key;
			for (int j = 0; j < keys.size(); j++) {
				double simOrdistance = 0;
				String key1 = keys.get(i);
				String key2 = keys.get(j);
				if(function.equals("sim")){
					simOrdistance = sim_dist.getSimpleSimilarity(vectors.get(key1), vectors.get(key2));
				}
				else{
					simOrdistance = sim_dist.getEuclideanDistance(vectors.get(key1), vectors.get(key2));
				}
				if (i == j) simOrdistance = 0;
				simOrDistMatrix[i][j] = simOrdistance;
			}
		}
		
		//Calculate distance based on frequency vectors
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			names[i] = key;
			for (int j = 0; j < keys.size(); j++) {
				double simOrdistance = 0;
				String key1 = keys.get(i);
				String key2 = keys.get(j);
				
				simOrdistance = sim_dist.getFrequencySimilarity(vectorsFreq.get(key1), vectorsFreq.get(key2));
				
	
				if (i == j) simOrdistance = 0;
				simOrDistMatrixFreq[i][j] = simOrdistance;
			}
		}

		List<Cluster> clusters = new ArrayList<Cluster>();
		List<Cluster> mergedClusters = new ArrayList<Cluster>();
		int id = 0;
		for (String clusterName : keys) {
			Cluster cluster = new Cluster();
			cluster.addMember(clusterName);
			cluster.setClusterID(id);
			clusters.add(cluster);
			id++;
		}

		while (!(clusters.size() == clusterNum)) {
			LinkedHashMap<Cluster[], Double> clusterDistance = new LinkedHashMap<Cluster[], Double>();
			ArrayList<Double> allPairDistances = new ArrayList<Double>();
			for (int i = 0; i < clusters.size(); i++) {
				for (int j = i + 1; j < clusters.size(); j++) {
					Cluster[] clusterPair = new Cluster[2];
					clusterPair[0] = clusters.get(i);
					clusterPair[1] = clusters.get(j);
					double dist = linkage.calculateDistance(clusterPair[0], clusterPair[1], simOrDistMatrix, keys, function);
					allPairDistances.add(dist);
					clusterDistance.put(clusterPair, dist);
				}
			}

			double max = Collections.max(allPairDistances);
			List<Cluster[]> pairsWithMax = new ArrayList<Cluster[]>();
			
			for (Cluster[] key : clusterDistance.keySet()) {
				double value = clusterDistance.get(key);
				if (value == max) {
					//pairToBeMerged = key;
					//break;
					pairsWithMax.add(key);
				}
			}
			
			LinkedHashMap<Cluster[], Double> mergeCandidatesClusterDistance = new LinkedHashMap<Cluster[], Double>();
			ArrayList<Double> mergePairDistances = new ArrayList<Double>();
			for(Cluster[] pair : pairsWithMax){
				double distance = FrequencyLinkage.calculateDistance(pair[0], pair[1], simOrDistMatrixFreq, keys);
				mergePairDistances.add(distance);
				mergeCandidatesClusterDistance.put(pair, distance);
			}
			double max1 = Collections.max(mergePairDistances);
			Cluster[] pairToBeMerged = new Cluster[2];
			for (Cluster[] key : mergeCandidatesClusterDistance.keySet()) {
				double value = mergeCandidatesClusterDistance.get(key);
				if (value == max1) {
					pairToBeMerged = key;
					break;
				}
			}
			//把所有有最大值的Pair都拿出来然后算频率 哪个最大就merge哪个
			
			
			Cluster mergedCluster = mergeCluster(pairToBeMerged[0], pairToBeMerged[1]);
			int oldIndex1 = pairToBeMerged[0].getClusterID();
			int oldIndex2 = pairToBeMerged[1].getClusterID();
			
			List<Cluster> clusterToBeRemoved = new ArrayList<Cluster>();
			for (int i = 0; i < clusters.size(); i++) {
				if (clusters.get(i).getClusterID() == oldIndex1)
					clusterToBeRemoved.add(clusters.get(i));
				if (clusters.get(i).getClusterID() == oldIndex2)
					clusterToBeRemoved.add(clusters.get(i));
			}
			
			clusters.removeAll(clusterToBeRemoved);
			
			
			clusters.add(mergedCluster);
			mergedClusters.add(mergedCluster);
		}
		output(clusters, group, function, linkageType);
		/*for(Cluster c : mergedClusters){
			System.out.println("[" + c.toString() + "]");
		}
		System.out.println(mergedClusters.size());*/
	}

	private static void output(List<Cluster> clusters, String group, String function, String linkageType) throws IOException{
		FileWriter fw = new FileWriter("simple_BT_" + group + "_" + function + "_" + linkageType + "_"+ clusters.size() + ".txt");
		for(Cluster cluster : clusters){
			fw.write(cluster.toString() + "\n");
		}
		fw.flush();
		fw.close();
	}
	
	private static Cluster mergeCluster(Cluster c1, Cluster c2) {
		int id1 = c1.getClusterID();
		int id2 = c2.getClusterID();
		Cluster cluster = new Cluster();
		ArrayList<String> oldCluster1Member = c1.getMembers();
		ArrayList<String> oldCluster2Member = c2.getMembers();
		for (String member : oldCluster1Member)
			cluster.addMember(member);
		for (String member : oldCluster2Member)
			cluster.addMember(member);
		int newID = Math.min(id1, id2);
		cluster.setClusterID(newID);
		return cluster;
	}

}
