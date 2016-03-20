package traceability.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class ClusterVerb {
	//static int clusterNum = 30;
	public void clusterVerb(int clusterNum) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("verbObj.arff"));
		//BufferedReader reader = new BufferedReader(new FileReader("verbObjFreq.arff"));
		Instances data = new Instances(reader);
		String[] options = new String[3];
		
		options[0] = "-N"; // max. iterations
		options[1] = String.valueOf(clusterNum);
		options[2] = "-O";
		SimpleKMeans clusterer = new SimpleKMeans(); // new instance of clusterer
		
		
		
		clusterer.setOptions(options); // set the options
		clusterer.buildClusterer(data); // build the clusterer
		int[] clusterAssignment = clusterer.getAssignments();
		/*for(int i : clusterAssignment){
			System.out.println(i);
		}*/
		
		LinkedHashMap<Integer, ArrayList<String>> verbGroups = new LinkedHashMap<Integer, ArrayList<String>>();
		ArrayList<String> verbs = new ArrayList<String>();
		File extractedVerbs = new File("allverbs.txt");
		BufferedReader br = new BufferedReader(new FileReader(extractedVerbs));
		String line;
		while((line = br.readLine()) != null){
			verbs.add(line);
		}
		
		for(int clusterid = 0; clusterid < clusterNum; clusterid++){
			ArrayList<String> group = new ArrayList<String>();
			for(int i = 0; i < clusterAssignment.length; i++){
				if(clusterAssignment[i] == clusterid){
					group.add(verbs.get(i));
				}
			}
			verbGroups.put(clusterid, group);
		}
		
		FileWriter fw = new FileWriter("verb groups.txt");
		//FileWriter fw = new FileWriter("verb groups freq.txt");
		for(int key : verbGroups.keySet()){
			ArrayList<String> verbgroup = verbGroups.get(key);
			for(String verb : verbgroup){
				fw.write(verb + ",");
			}
			fw.write("\n");
		}
		fw.flush();
		fw.close();
	}

}
