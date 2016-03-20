package traceability.groupVerbs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class GroupTerms {

	public static void main(String[] args) throws Exception {
		ActionUnit a = new ActionUnit();
		LinkedHashMap<String,LinkedHashSet<String>> actionunit = a.actionunits;
		VerbPhrases vp = new VerbPhrases();
		ArrayList<String[]> pairs_vp = vp.getVerbObjFromVP(actionunit);

		ObjVerb ov = new ObjVerb();
		VerbObj vo = new VerbObj();
		ov.getObjVerb(pairs_vp);
		vo.getVerbObj(pairs_vp,actionunit);

		

	}

}
