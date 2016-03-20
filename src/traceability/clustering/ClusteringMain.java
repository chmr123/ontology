package traceability.clustering;

public class ClusteringMain {

	public static void main(String[] args) throws Exception {
		
		int verbNum = Integer.valueOf(args[0]);
		int nounNum = Integer.valueOf(args[1]);
		
		ClusterVerb cv = new ClusterVerb();
		cv.clusterVerb(verbNum);
		
		ClusterNoun cn = new ClusterNoun();
		cn.clusterNoun(nounNum);

	}

}
