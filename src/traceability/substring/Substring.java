package traceability.substring;

import java.util.ArrayList;

public class Substring {

	public static void main(String[] args) {
		ArrayList<String> a = new ArrayList<String>();
		a.add("a");
		a.add("a");
		a.add("a");
		a.add("d");
		a.add("e");
		for(int i = 0; i < a.size(); i++){
			if(a.get(i).equals("a")){
				a.remove(i);
				System.out.println(a.size());
			}
		}
		System.out.println(a);
	}

}
