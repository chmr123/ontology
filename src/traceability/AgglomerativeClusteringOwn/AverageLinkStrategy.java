package traceability.AgglomerativeClusteringOwn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AverageLinkStrategy implements LinkageStrategy {
	public double calculateDistance(Cluster c1, Cluster c2, double[][] distances, ArrayList<String> keys, String function){
		List<String> memberSet1 = c1.getMembers();
		List<String> memberSet2 = c2.getMembers();
		double sum = 0.0;
		for(String member1 : memberSet1){
			for(String member2 : memberSet2){
				int index1 = keys.indexOf(member1);
				int index2 = keys.indexOf(member2);
				double distance = distances[index1][index2];
				sum = sum + distance;
			}
		}
		return sum / (memberSet1.size() + memberSet2.size());
	}
}
