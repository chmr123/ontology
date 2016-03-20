package traceability.AgglomerativeClusteringOwn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleLinkStrategy implements LinkageStrategy {
	public double calculateDistance(Cluster c1, Cluster c2, double[][] distances, ArrayList<String> keys, String linkageType){
		List<String> memberSet1 = c1.getMembers();
		List<String> memberSet2 = c2.getMembers();
		ArrayList<Double> allDistances = new ArrayList<Double>();
		for(String member1 : memberSet1){
			for(String member2 : memberSet2){
				int index1 = keys.indexOf(member1);
				int index2 = keys.indexOf(member2);
				double distance = distances[index1][index2];
				allDistances.add(distance);
			}
		}
		if(linkageType.equals("sim"))
			return Collections.max(allDistances);
		else
			return Collections.min(allDistances);
	}
}
