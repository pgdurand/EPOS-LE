package epos.model.sequence.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * ClustalParser is able to parse clustalW alignment files of all types: DNA, RNA and PROTEIN
 * 
 * @author m5muth2
 *
 */
public class ClustalParser extends AbstractAlignmentReader {

	public void read()  throws Exception{
		BufferedReader inFile = null;
		try {
			if (file != null) {
				inFile = new BufferedReader(new FileReader(file));
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		boolean readSeq = false;
		Vector<String> namesVector = new Vector<String>();
		Map<String, StringBuffer> seqmap = new HashMap<String, StringBuffer>();

		String line, key;
		StringTokenizer str;
		StringBuffer seqbuffer;

		try {
			line = inFile.readLine();

			while (line != null) {

				if (line.indexOf(" ") != 0) {
					str = new StringTokenizer(line, " ");

					if (str.hasMoreTokens()) {
						key = str.nextToken();
						if (key.equalsIgnoreCase("CLUSTAL")) {							
							readSeq = true;
						} else if (readSeq) {							
							if (seqmap.containsKey(key)) {								
								seqbuffer = new StringBuffer(seqmap.get(key));								
							} else {
								seqbuffer = new StringBuffer();
								seqmap.put(key, seqbuffer);
							}

							if (!(namesVector.contains(key))) {
								namesVector.addElement(key);
							}

							if (str.hasMoreTokens()) {
								seqbuffer.append(str.nextToken());
							}
							seqmap.put(key, seqbuffer);
						} else {
							readSeq = false;
						}	
					}					
				}							
				line = inFile.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		if (readSeq) {
			names = new String[namesVector.size()];
			sequences = new String[namesVector.size()];
			for (int i = 0; i < namesVector.size(); i++) {
				if (seqmap.get(namesVector.get(i)) != null) {
					String seqID = namesVector.elementAt(i).toString();
					names[i] = seqID;					
					String seqString = seqmap.get(seqID).toString();					
					sequences[i] = seqString;					
				}
			}
		} else {
			// if no Clustal String is found in the clustal file
			throw new RuntimeException("Could not read the Clustal-inputfile...");
		}
	}

	public String toString() {
		return "Clustal File";
	}
}
