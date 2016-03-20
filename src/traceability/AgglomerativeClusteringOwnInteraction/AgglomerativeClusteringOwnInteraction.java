package traceability.AgglomerativeClusteringOwnInteraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class AgglomerativeClusteringOwnInteraction {
	static List<Cluster> verbClusters = new ArrayList<Cluster>();
	
	static List<Cluster> verbMergedClusters = new ArrayList<Cluster>();
	static ArrayList<String> verbKeys = null;
	static double[][] simOrDistMatrixVerb;
	
	static List<Cluster> nounClusters = new ArrayList<Cluster>();
	static List<Cluster> nounClustersFinal = new ArrayList<Cluster>();
	static List<Cluster> nounMergedClusters = new ArrayList<Cluster>();
	static ArrayList<String> nounKeys = null;
	static double[][] simOrDistMatrixNoun;
	
	static LinkageStrategy linkage = null;
	static String function = null;
	static String linkageType = null;
	static String display = null;
	static int verbClusterNum = 10;
	static int nounClusterNum = 10;
	public static void main(String[] args) throws IOException {
		 for(int i = 0;i < args.length;i++) {	
		       if ("-f".equals(args[i])) {
		    	  function = args[i+1];
		          i++;
		      }else if ("-l".equals(args[i])) {
		    	  linkageType = args[i+1];
		          i++;
		      }else if ("-d".equals(args[i])) {
		    	  display = args[i+1];
		          i++;
		      }else if ("-v".equals(args[i])) {
		    	  verbClusterNum = Integer.parseInt(args[i+1]);
		          i++;
		      }else if ("-n".equals(args[i])) {
		    	  nounClusterNum = Integer.parseInt(args[i+1]);
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
		
		
		initiateVerbProcess();
		initiateNounProcess();
		
		while(!(verbClusters.size() == verbClusterNum)){
			clusterVerb();
			updateVerbMatrix();
			clusterNoun();
			updateNounMatrix();
		}
		
		List<Cluster> verbClustersFinal = new ArrayList<Cluster>();
		for(Cluster c : verbClusters){
			verbClustersFinal.add(c);
		}
		
		while(!(nounClusters.size() == nounClusterNum)){
			if(verbClusters.size() == 1) break;
			clusterVerb();
			updateVerbMatrix();
			clusterNoun();
			updateNounMatrix();
		}
		
		while(!(nounClusters.size() == nounClusterNum)){
			clusterNoun();
		}
		
		//List<Cluster> nounClustersFinal = nounClusters;
		
		outputVerb(verbClustersFinal, function, linkageType);
		outputNoun(nounClusters, function, linkageType);
	}
	
	private static void outputVerb(List<Cluster> clusters, String function, String linkageType) throws IOException{
		FileWriter fw = new FileWriter("interaction_verb"  + "_" + function + "_" + linkageType + "_"+ clusters.size() + ".txt");
		int clusterID = 1;
		for(Cluster cluster : clusters){
			String member = cluster.toString();
			String[] splitted = member.split(",");
			String memberBeforeStem = "";
			for(String s : splitted){
				String beforeStem = "";			
				String[] beforeStemList = stemBackVerb(s);
				for(String ss : beforeStemList){
					beforeStem = beforeStem + ss + ",";
				}
				beforeStem = beforeStem.substring(0,beforeStem.lastIndexOf(","));
				memberBeforeStem = memberBeforeStem + beforeStem + ",";
			}
			fw.write("Cluster" + clusterID++ + ":" + memberBeforeStem.substring(0,memberBeforeStem.lastIndexOf(",")) + "\n");
		}
		fw.flush();
		fw.close();
	}
	
	
	
	private static void outputNoun(List<Cluster> clusters, String function, String linkageType) throws IOException{
		FileWriter fw = new FileWriter("interaction_noun"  + "_" + function + "_" + linkageType + "_"+ clusters.size() + ".txt");
		int clusterID = 1;
		for(Cluster cluster : clusters){
			String member = cluster.toString();
			String[] splitted = member.split(",");
			String memberBeforeStem = "";
			for(String s : splitted){
				String beforeStem = "";			
				String[] beforeStemList = stemBackNoun(s);
				for(String ss : beforeStemList){
					beforeStem = beforeStem + ss + ",";
				}
				beforeStem = beforeStem.substring(0,beforeStem.lastIndexOf(","));
				memberBeforeStem = memberBeforeStem + beforeStem + ",";
			}
			fw.write("Cluster" + clusterID++ + ":" + memberBeforeStem.substring(0,memberBeforeStem.lastIndexOf(",")) + "\n");
		}
		fw.flush();
		fw.close();
	}
	
	private static String[] stemBackVerb(String stemmed) throws IOException{
		//String beforeStem = "";
		File file = new File("verbStemLookUpTable.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		HashMap<String, String[]> stemMap = new HashMap<String, String[]>();
		while((line = br.readLine()) != null){
			String afterStem = line.split(":")[0];
			String beforeStem = line.split(":")[1];
			stemMap.put(afterStem, beforeStem.split(","));
		}
		String[] beforeStem = stemMap.get(stemmed);
		return beforeStem;
	}
	
	private static String[] stemBackNoun(String stemmed) throws IOException{
		//String beforeStem = "";
		File file = new File("nounStemLookUpTable.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		HashMap<String, String[]> stemMap = new HashMap<String, String[]>();
		while((line = br.readLine()) != null){
			String afterStem = line.split(":")[0];
			String beforeStem = line.split(":")[1];
			stemMap.put(afterStem, beforeStem.split(","));
		}
		String[] beforeStem = stemMap.get(stemmed);
		return beforeStem;
	}
	
	
	private static void clusterVerb(){
		LinkedHashMap<Cluster[], Double> clusterDistance = new LinkedHashMap<Cluster[], Double>();
		ArrayList<Double> allPairDistances = new ArrayList<Double>();
		for (int i = 0; i < verbClusters.size(); i++) {
			for (int j = i + 1; j < verbClusters.size(); j++) {
				Cluster[] clusterPair = new Cluster[2];
				clusterPair[0] = verbClusters.get(i);
				clusterPair[1] = verbClusters.get(j);
				double dist = linkage.calculateDistance(clusterPair[0], clusterPair[1], simOrDistMatrixVerb, verbKeys, function);
				allPairDistances.add(dist);
				clusterDistance.put(clusterPair, dist);
			}
		}

		double max = Collections.max(allPairDistances);
		Cluster[] pairToBeMerged = new Cluster[2];
		for (Cluster[] key : clusterDistance.keySet()) {
			double value = clusterDistance.get(key);
			if (value == max) {
				pairToBeMerged = key;
				break;
			}
		}
		
		
		//把所有有最大值的Pair都拿出来然后算频率 哪个最大就merge哪个
		
		
		Cluster mergedCluster = mergeCluster(pairToBeMerged[0], pairToBeMerged[1]);
		int oldIndex1 = pairToBeMerged[0].getClusterID();
		int oldIndex2 = pairToBeMerged[1].getClusterID();
		
		List<Cluster> clusterToBeRemoved = new ArrayList<Cluster>();
		for (int i = 0; i < verbClusters.size(); i++) {
			if (verbClusters.get(i).getClusterID() == oldIndex1)
				clusterToBeRemoved.add(verbClusters.get(i));
			if (verbClusters.get(i).getClusterID() == oldIndex2)
				clusterToBeRemoved.add(verbClusters.get(i));
		}
		
		verbClusters.removeAll(clusterToBeRemoved);	
		verbClusters.add(mergedCluster);
		verbMergedClusters.add(mergedCluster);
	}
	
	private static void clusterNoun(){
		LinkedHashMap<Cluster[], Double> clusterDistance = new LinkedHashMap<Cluster[], Double>();
		ArrayList<Double> allPairDistances = new ArrayList<Double>();
		for (int i = 0; i < nounClusters.size(); i++) {
			for (int j = i + 1; j < nounClusters.size(); j++) {
				Cluster[] clusterPair = new Cluster[2];
				clusterPair[0] = nounClusters.get(i);
				clusterPair[1] = nounClusters.get(j);
				double dist = linkage.calculateDistance(clusterPair[0], clusterPair[1], simOrDistMatrixNoun, nounKeys, function);
				allPairDistances.add(dist);
				clusterDistance.put(clusterPair, dist);
			}
		}

		double max = Collections.max(allPairDistances);
		Cluster[] pairToBeMerged = new Cluster[2];
		for (Cluster[] key : clusterDistance.keySet()) {
			double value = clusterDistance.get(key);
			if (value == max) {
				pairToBeMerged = key;
				break;
			}
		}
		
		
		//把所有有最大值的Pair都拿出来然后算频率 哪个最大就merge哪个
		
		
		Cluster mergedCluster = mergeCluster(pairToBeMerged[0], pairToBeMerged[1]);
		int oldIndex1 = pairToBeMerged[0].getClusterID();
		int oldIndex2 = pairToBeMerged[1].getClusterID();
		
		List<Cluster> clusterToBeRemoved = new ArrayList<Cluster>();
		for (int i = 0; i < nounClusters.size(); i++) {
			if (nounClusters.get(i).getClusterID() == oldIndex1)
				clusterToBeRemoved.add(nounClusters.get(i));
			if (nounClusters.get(i).getClusterID() == oldIndex2)
				clusterToBeRemoved.add(nounClusters.get(i));
		}
		
		nounClusters.removeAll(clusterToBeRemoved);	
		nounClusters.add(mergedCluster);
		nounMergedClusters.add(mergedCluster);
	}

	private static void initiateVerbProcess() throws IOException{
		Vector v = new Vector();
		LinkedHashMap<String, double[]> verbVectors = v.getVerbVectors();
		String[] verbNames = new String[verbVectors.keySet().size()];
		verbKeys = new ArrayList<String>(verbVectors.keySet());
		int vectorSize = verbKeys.size();
		simOrDistMatrixVerb = new double[vectorSize][vectorSize];
		Similarity sim_dist = new Similarity(verbVectors);
		for (int i = 0; i < verbKeys.size(); i++) {
			String key = verbKeys.get(i);
			verbNames[i] = key;
			for (int j = 0; j < verbKeys.size(); j++) {
				double simOrdistance = 0;
				String key1 = verbKeys.get(i);
				String key2 = verbKeys.get(j);
				if(function.equals("sim")){
					simOrdistance = sim_dist.getSimpleSimilarity(verbVectors.get(key1), verbVectors.get(key2));
				}
				else{
					simOrdistance = sim_dist.getEuclideanDistance(verbVectors.get(key1), verbVectors.get(key2));
				}
				if (i == j) simOrdistance = 0;
				simOrDistMatrixVerb[i][j] = simOrdistance;
			}
		}
		
		int id = 0;
		for (String clusterName : verbKeys) {
			Cluster cluster = new Cluster();
			cluster.addMember(clusterName);
			cluster.setClusterID(id);
			verbClusters.add(cluster);
			id++;
		}
	}
	
	private static void initiateNounProcess() throws IOException{
		Vector v = new Vector();
		LinkedHashMap<String, double[]> nounVectors = v.getNounVectors();
		String[] nounNames = new String[nounVectors.keySet().size()];
		nounKeys = new ArrayList<String>(nounVectors.keySet());
		int vectorSize = nounKeys.size();
		simOrDistMatrixNoun = new double[vectorSize][vectorSize];
		Similarity sim_dist = new Similarity(nounVectors);
		for (int i = 0; i < nounKeys.size(); i++) {
			String key = nounKeys.get(i);
			nounNames[i] = key;
			for (int j = 0; j < nounKeys.size(); j++) {
				double simOrdistance = 0;
				String key1 = nounKeys.get(i);
				String key2 = nounKeys.get(j);
				if(function.equals("sim")){
					simOrdistance = sim_dist.getSimpleSimilarity(nounVectors.get(key1), nounVectors.get(key2));
				}
				else{
					simOrdistance = sim_dist.getEuclideanDistance(nounVectors.get(key1), nounVectors.get(key2));
				}
				if (i == j) simOrdistance = 0;
				simOrDistMatrixNoun[i][j] = simOrdistance;
			}
		}
		
		int id = 0;
		for (String clusterName : nounKeys) {
			Cluster cluster = new Cluster();
			cluster.addMember(clusterName);
			cluster.setClusterID(id);
			nounClusters.add(cluster);
			id++;
		}
	}
	
	
	private static void updateVerbMatrix() throws IOException{
		Vector v = new Vector();
		LinkedHashMap<String, double[]> verbVectors = v.updateVerbVectors(nounMergedClusters);
		String[] verbNames = new String[verbVectors.keySet().size()];
		verbKeys = new ArrayList<String>(verbVectors.keySet());
		int vectorSize = verbKeys.size();
		simOrDistMatrixVerb = new double[vectorSize][vectorSize];
		Similarity sim_dist = new Similarity(verbVectors);
		for (int i = 0; i < verbKeys.size(); i++) {
			String key = verbKeys.get(i);
			verbNames[i] = key;
			for (int j = 0; j < verbKeys.size(); j++) {
				double simOrdistance = 0;
				String key1 = verbKeys.get(i);
				String key2 = verbKeys.get(j);
				if(function.equals("sim")){
					simOrdistance = sim_dist.getSimpleSimilarity(verbVectors.get(key1), verbVectors.get(key2));
				}
				else{
					simOrdistance = sim_dist.getEuclideanDistance(verbVectors.get(key1), verbVectors.get(key2));
				}
				if (i == j) simOrdistance = 0;
				simOrDistMatrixVerb[i][j] = simOrdistance;
			}
		}
	}
	
	private static void updateNounMatrix() throws IOException{
		Vector v = new Vector();
		LinkedHashMap<String, double[]> nounVectors = v.updateNounVectors(verbMergedClusters);
		String[] nounNames = new String[nounVectors.keySet().size()];
		nounKeys = new ArrayList<String>(nounVectors.keySet());
		int vectorSize = nounKeys.size();
		simOrDistMatrixNoun = new double[vectorSize][vectorSize];
		Similarity sim_dist = new Similarity(nounVectors);
		for (int i = 0; i < nounKeys.size(); i++) {
			String key = nounKeys.get(i);
			nounNames[i] = key;
			for (int j = 0; j < nounKeys.size(); j++) {
				double simOrdistance = 0;
				String key1 = nounKeys.get(i);
				String key2 = nounKeys.get(j);
				if(function.equals("sim")){
					simOrdistance = sim_dist.getSimpleSimilarity(nounVectors.get(key1), nounVectors.get(key2));
				}
				else{
					simOrdistance = sim_dist.getEuclideanDistance(nounVectors.get(key1), nounVectors.get(key2));
				}
				if (i == j) simOrdistance = 0;
				simOrDistMatrixNoun[i][j] = simOrdistance;
			}
		}
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
