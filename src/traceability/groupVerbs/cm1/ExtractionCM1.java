package traceability.groupVerbs.cm1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class ExtractionCM1 {

	public static void main(String[] args) throws Exception {
		VerbPhrases vp = new VerbPhrases();
		ArrayList<String[]> pairs_vp = vp.getVerbObjFromVP();

		ObjVerb ov = new ObjVerb();
		VerbObj vo = new VerbObj();
		ov.getObjVerb(pairs_vp);
		vo.getVerbObj(pairs_vp);

		

	}

}
