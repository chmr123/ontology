package traceability.AgglomerativeClusteringNew;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AnalyzeCluster {
	public void analyzeCluster() throws IOException{
		File file = new File("hiararchy.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		while((line = br.readLine()) != null){
			ArrayList<String> clusteredMembers = new ArrayList<String>();
			String clusterResults = line.split(": ")[1].replace("[", "").replace("]", "");
			String[] allclusters = clusterResults.split(", ");
			for(String c : allclusters){
				if(c.contains("&"))
					clusteredMembers.add(c);
			}
		}
	}
}
