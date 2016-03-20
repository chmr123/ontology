package traceability.ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Ontology {

	public static void main(String[] args) throws IOException {		
		LinkedHashMap<String, ArrayList<String>> properties = new LinkedHashMap<String, ArrayList<String>>();
		File termlist = new File("term list.txt");
		BufferedReader br = new BufferedReader(new FileReader(termlist));
		String line;
		while((line = br.readLine()) != null){
			String type = line.substring(0, line.indexOf(":") - 1);
			String nouns = line.substring(line.indexOf(":") + 1);
			String[] splitted = nouns.split(",");
			ArrayList<String> group = new ArrayList<String>();
			for(String s : splitted){
				group.add(s);
			}
			properties.put(type, group);
		}

	}

}
