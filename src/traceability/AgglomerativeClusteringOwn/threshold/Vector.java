package traceability.AgglomerativeClusteringOwn.threshold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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
			
			/*System.out.print(key + " ");
			
			for(int i : vector){
				System.out.print(i + ",");
			}
			System.out.println();*/
			
		}
		br.close();
		return nounVectors;
	}
}
