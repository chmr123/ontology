package traceability.ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class ActionUnits {
	public LinkedHashMap<String, Set<String>> getActionUnits() throws IOException{
		File file = new File("action units.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		Set<String> actionunits = new HashSet<String>();
		String line;
		String filename = "";
		boolean newfile = false;
		LinkedHashMap<String, Set<String>> allActionUnits = new LinkedHashMap<String, Set<String>>();
		boolean first = true;
		while((line = br.readLine()) != null){
			if(line.contains(".txt") && first == true) {
				filename = line;
				first = false;
				continue;
			}
			if(line.contains(".txt") && first == false) {
				allActionUnits.put(filename, actionunits);
				filename = line;
				newfile = true;
				actionunits.clear();
				continue;
			}
			if(newfile == false){
				actionunits.add(line);
			}
		}
		
		return allActionUnits;
	}
}
