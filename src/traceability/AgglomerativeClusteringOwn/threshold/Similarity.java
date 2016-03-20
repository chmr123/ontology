package traceability.AgglomerativeClusteringOwn.threshold;

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
	
	public double getCosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += (double)vectorA[i] * (double)vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}
