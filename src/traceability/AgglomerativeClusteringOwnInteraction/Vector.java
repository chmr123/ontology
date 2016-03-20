package traceability.AgglomerativeClusteringOwnInteraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class Vector {
	LinkedHashMap<String, double[]> verbVectors;
	LinkedHashMap<String, double[]> nounVectors;
	public Vector(){
		verbVectors = new LinkedHashMap<String, double[]>();
		nounVectors = new LinkedHashMap<String, double[]>();
	}
	
	public LinkedHashMap<String, double[]> getVerbVectors() throws IOException{
		LinkedHashSet<String> objects = new LinkedHashSet<String>();
		LinkedHashMap<String, String[]> instances = new LinkedHashMap<String, String[]>();
		File file = new File("verb_objs.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			String[] splitted = line.split(":");
			String verb = splitted[0];
			String objectGroup = splitted[1].replace("[", "").replace("]", "");
			String[] nouns = objectGroup.split(", ");
			instances.put(verb, nouns);
			for(String n : nouns){
				objects.add(n.trim());
			}
		}
		
		ArrayList<String> objectArray = new ArrayList<String>(objects);
		for(String key : instances.keySet()){
			String[] nouns = instances.get(key);
			double[] vector = new double[objects.size()];
			for(int i = 0; i < objects.size(); i++){
				if(Arrays.asList(nouns).contains(objectArray.get(i)))
					vector[i] = 1;
			}
			verbVectors.put(key, vector);
			
		}
		br.close();
		return verbVectors;
	}
	
	public LinkedHashMap<String, double[]> updateVerbVectors(List<Cluster> mergedClusters) throws IOException{
		
		List<ArrayList<String>> mergedMembers = new ArrayList<ArrayList<String>>();
		for(Cluster c : mergedClusters){
			ArrayList<String> memberNames = c.getMembers();
			mergedMembers.add(memberNames);
		}
		
		
		LinkedHashSet<String> objects = new LinkedHashSet<String>();
		LinkedHashMap<String, String[]> instances = new LinkedHashMap<String, String[]>();
		File file = new File("verb_objs.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			String[] splitted = line.split(":");
			String verb = splitted[0];
			String objectGroup = splitted[1].replace("[", "").replace("]", "");
			String[] initialNouns = objectGroup.split(", ");
			String[] updatedFeature = updateFeature(initialNouns, mergedClusters);
	
			instances.put(verb, updatedFeature);
			for(String n : updatedFeature){
				objects.add(n.trim());
			}
		}
		
		ArrayList<String> objectArray = new ArrayList<String>(objects);
		for(String key : instances.keySet()){
			String[] nouns = instances.get(key);
			double[] vector = new double[objects.size()];
			for(int i = 0; i < objects.size(); i++){
				if(Arrays.asList(nouns).contains(objectArray.get(i)))
					vector[i] = 1;
			}
			verbVectors.put(key, vector);		
		}
		br.close();
		return verbVectors;
	}
	
	public LinkedHashMap<String, double[]> updateNounVectors(List<Cluster> mergedClusters) throws IOException{
		
		List<ArrayList<String>> mergedMembers = new ArrayList<ArrayList<String>>();
		for(Cluster c : mergedClusters){
			ArrayList<String> memberNames = c.getMembers();
			mergedMembers.add(memberNames);
		}
				
		LinkedHashSet<String> actions = new LinkedHashSet<String>();
		LinkedHashMap<String, String[]> instances = new LinkedHashMap<String, String[]>();
		File file = new File("obj_verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			String[] splitted = line.split(":");
			String noun = splitted[0];
			String verbGroup = splitted[1].replace("[", "").replace("]", "");
			String[] initialVerbs = verbGroup.split(", ");
			String[] updatedFeature = updateFeature(initialVerbs, mergedClusters);
	
			instances.put(noun, updatedFeature);
			for(String n : updatedFeature){
				actions.add(n.trim());
			}
		}
		
		ArrayList<String> verbArray = new ArrayList<String>(actions);
		for(String key : instances.keySet()){
			String[] verbs = instances.get(key);
			double[] vector = new double[actions.size()];
			for(int i = 0; i < actions.size(); i++){
				if(Arrays.asList(verbs).contains(verbArray.get(i)))
					vector[i] = 1;
			}
			nounVectors.put(key, vector);		
		}
		br.close();
		return nounVectors;
	}
	
	
	public LinkedHashMap<String, double[]> getNounVectors() throws IOException{
		LinkedHashSet<String> actions = new LinkedHashSet<String>();
		LinkedHashMap<String, String[]> instances = new LinkedHashMap<String, String[]>();
		File file = new File("obj_verbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			String[] splitted = line.split(":");
			String noun = splitted[0];
			String verbGroup = splitted[1].replace("[", "").replace("]", "");
			String[] verbs = verbGroup.split(", ");
			instances.put(noun, verbs);
			for(String v : verbs){
				actions.add(v.trim());
			}
		}
		
		ArrayList<String> verbArray = new ArrayList<String>(actions);
		for(String key : instances.keySet()){
			String[] verbs = instances.get(key);
			double[] vector = new double[actions.size()];
			for(int i = 0; i < actions.size(); i++){
				if(Arrays.asList(verbs).contains(verbArray.get(i)))
					vector[i] = 1;
			}
			nounVectors.put(key, vector);		
		}
		br.close();
		return nounVectors;
	}
	
	private String[] updateFeature(String[] initialFeature, List<Cluster> mergedClusters){
		List<String> featureArray = Arrays.asList(initialFeature);
		String[] updatedFeature = new String[featureArray.size()];
		for(Cluster c : mergedClusters){
			int clusterID = c.getClusterID();
			ArrayList<String> members = c.getMembers();
			for(String feature : featureArray){
				if(members.contains(feature)){
					int index = featureArray.indexOf(feature);
					featureArray.set(index, "cluster" + clusterID);
				}
			}	
		}
		
		updatedFeature = featureArray.toArray(updatedFeature);
		return updatedFeature;
	}
	
}
