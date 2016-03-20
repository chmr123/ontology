package traceability.actionunit.latest;

import java.io.IOException;

public class VerbPhraseMain {

	public static void main(String[] args) throws IOException {
		VerbPhrases vp = new VerbPhrases();
		vp.getVP("req");
		vp.getVP("UC");

	}

}
