package traceability.AgglomerativeClusteringNew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class Similarity {
	LinkedHashMap<String, int[]> vectors = new LinkedHashMap<String, int[]>();
	public Similarity(LinkedHashMap<String, int[]> vectors){
		this.vectors = vectors;
	}
	public double getSimpleSimilarity(int[] vector1, int[] vector2){
		int sim = 0;
		for(int i = 0; i < vector1.length; i++){
			if(vector1[i] == vector2[i] && vector2[i] == 1)
				sim++;
		}
		return sim;
	}
	
	public double getEuclideanDistance(int[] vector1, int[] vector2){
		double Sum = 0.0;
        for(int i=0;i<vector1.length;i++) {
           Sum = Sum + Math.pow((vector1[i]-vector2[i]),2.0);
        }
        return Math.sqrt(Sum); 
	}
}
