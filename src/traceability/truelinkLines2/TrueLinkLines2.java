package traceability.truelinkLines2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrueLinkLines2 {

	public static void main(String[] args) throws IOException {
		File[] req = new File("req").listFiles();
		File[] uc = new File("uc").listFiles();
		Map<String, String> file_content = new HashMap<String, String>();
		for(File f : req){
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			String content = "";
			while((line = br.readLine()) != null){
				content = content + line + " ";
			}
			
			file_content.put(f.getName(), content);
		}
		
		for(File f : uc){
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			String content = "";
			while((line = br.readLine()) != null){
				content = content + line + " ";
			}
			
			file_content.put(f.getName(), content);
		}
		
		System.out.println("writing to file");
		FileWriter fw = new FileWriter("rationale_before.txt");
		BufferedReader br = new BufferedReader(new FileReader("req_uc.txt"));
		String line;
		while((line = br.readLine()) != null){
			String[] splitted = line.split(" ");
			String high = splitted[0];
			//ArrayList<String> low = new ArrayList<String>();
			if(splitted.length == 1) continue;
			fw.write("%\n");
			
			for(int i = 1; i < splitted.length; i++){
				fw.write(high + ": " + file_content.get(high) + "\n");
				fw.write(splitted[i] + ": " + file_content.get(splitted[i]) + "\n");
			}
			
		}
		fw.flush();
		fw.close();

	}

}
