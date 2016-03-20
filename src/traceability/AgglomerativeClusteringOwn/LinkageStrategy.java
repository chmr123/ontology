package traceability.AgglomerativeClusteringOwn;

import java.util.ArrayList;


public interface LinkageStrategy {
	public double calculateDistance(Cluster c1, Cluster c2, double[][] distances, ArrayList<String> keys, String function);
}
