package traceability.AgglomerativeClusteringOwn.originalCopy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Similarity {
	LinkedHashMap<String, double[]> vectors = new LinkedHashMap<String, double[]>();
	public Similarity(LinkedHashMap<String, double[]> vectors){
		this.vectors = vectors;
	}
	public double getSimpleSimilarity(double[] vector1, double[] vector2){
		int sim = 0;
		for(int i = 0; i < vector1.length; i++){
			if(vector1[i] == vector2[i] && vector2[i] == 1)
				sim = sim+1;
		}
		return sim;
	}
	
	public double getEuclideanDistance(double[] vector1, double[] vector2){
		double Sum = 0.0;
        for(int i=0;i<vector1.length;i++) {
           Sum = Sum + Math.pow((vector1[i]-vector2[i]),2.0);
        }
        return Math.sqrt(Sum); 
	}
}
