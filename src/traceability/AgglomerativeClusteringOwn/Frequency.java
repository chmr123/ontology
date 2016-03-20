package traceability.AgglomerativeClusteringOwn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Frequency {
	LinkedHashMap<String, double[]> verbVectorsFreq;
	LinkedHashMap<String, double[]> nounVectorsFreq;
	ArrayList<String[]> allpairs;
	public Frequency() throws IOException{
		verbVectorsFreq = new LinkedHashMap<String, double[]>();
		nounVectorsFreq = new LinkedHashMap<String, double[]>();
		allpairs = new ArrayList<String[]>();
		File file = new File("allpairs.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			String[] pair = new String[2];
			String p1 = line.substring(0,line.indexOf(" "));
			String p2 = line.substring(line.indexOf(" ") + 1);
			pair[0] = p1;
			pair[1] = p2;
			allpairs.add(pair);
		}
	}
	
	public LinkedHashMap<String, double[]> getVerbVectorsFreq() throws IOException{
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
				if(Arrays.asList(nouns).contains(objectArray.get(i))){
					int freq = 0;
					for(String[] pair : allpairs){
						//System.out.println(pair[0] + " " + pair[1]);
						if(pair[0].equals(key) && pair[1].equals(objectArray.get(i))) freq++;
					}
					vector[i] = freq;
					if(freq != 0)
						System.out.println(freq);
				}
			}
			verbVectorsFreq.put(key, vector);
			
		}
		br.close();
		return verbVectorsFreq;
	}
	
	public LinkedHashMap<String, double[]> getNounVectorsFreq() throws IOException{
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
				if(Arrays.asList(verbs).contains(verbArray.get(i))){
					int freq = 0;
					for(String[] pair : allpairs){
						if(pair[1].equals(key) && pair[0].equals(verbArray.get(i))) freq++;
					}
					vector[i] = freq;
				}
			}
			nounVectorsFreq.put(key, vector);
			
			/*System.out.print(key + " ");
			
			for(int i : vector){
				System.out.print(i + ",");
			}
			System.out.println();*/
			
		}
		br.close();
		return nounVectorsFreq;
	}
}
