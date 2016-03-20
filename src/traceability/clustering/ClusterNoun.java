package traceability.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class ClusterNoun {
	//static int clusterNum = 20;
	public void clusterNoun(int clusterNum) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("objVerb.arff"));
		//BufferedReader reader = new BufferedReader(new FileReader("objVerbFreq.arff"));
		Instances data = new Instances(reader);
		String[] options = new String[3];
		options[0] = "-N"; // max. iterations
		options[1] = String.valueOf(clusterNum);
		options[2] = "-O";
		SimpleKMeans kmeans = new SimpleKMeans(); // new instance of clusterer
		kmeans.setOptions(options); // set the options
		kmeans.buildClusterer(data); // build the clusterer
		int[] clusterAssignment = kmeans.getAssignments();
		/*for(int i : clusterAssignment){
			System.out.println(i);
		}*/
		
		LinkedHashMap<Integer, ArrayList<String>> nounGroups = new LinkedHashMap<Integer, ArrayList<String>>();
		ArrayList<String> nouns = new ArrayList<String>();
		File extractedNouns = new File("allnouns.txt");
		BufferedReader br = new BufferedReader(new FileReader(extractedNouns));
		String line;
		while((line = br.readLine()) != null){
			nouns.add(line);
		}
		
		for(int clusterid = 0; clusterid < clusterNum; clusterid++){
			ArrayList<String> group = new ArrayList<String>();
			for(int i = 0; i < clusterAssignment.length; i++){
				if(clusterAssignment[i] == clusterid){
					group.add(nouns.get(i));
				}
			}
			nounGroups.put(clusterid, group);
		}
		
		FileWriter fw = new FileWriter("noun groups.txt");
		//FileWriter fw = new FileWriter("noun groups freq.txt");
		for(int key : nounGroups.keySet()){
			ArrayList<String> noungroup = nounGroups.get(key);
			for(String noun : noungroup){
				fw.write(noun + ",");
			}
			fw.write("\n");
		}
		fw.flush();
		fw.close();
	}

}
