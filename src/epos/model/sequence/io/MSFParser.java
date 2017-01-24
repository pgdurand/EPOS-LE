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
 * MSFParser is able to parse msf alignment files of all types: DNA, RNA and PROTEIN
 * 
 * @author m5muth2
 *
 */
public class MSFParser extends AbstractAlignmentReader {

	public void read() throws Exception {

		BufferedReader inFile = null;
		try {
			if (file != null) {
				inFile = new BufferedReader(new FileReader(file));
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		boolean readSeq = false;
		String keyelement = new String();
		Vector<String> namesVector = new Vector<String>();
		Map<String, StringBuffer> seqmap = new HashMap<String, StringBuffer>();		
		
		String line;
		try {
			line = inFile.readLine();

			while (line != null) {
				StringTokenizer str = new StringTokenizer(line);

				while (str.hasMoreTokens()) {
					String inStr = str.nextToken();

					// Store the name of the file in the headers-vector
					if (inStr.indexOf("Name:") != -1) {
						keyelement = str.nextToken();
						namesVector.addElement(keyelement);
					}

					// If // exists, the sequences begin
					if (inStr.indexOf("//") != -1) {
						readSeq = true;
					}

					// Read the lines
					if ((inStr.indexOf("//") == -1) && (readSeq == true)) {

						// First comes the sequence id
						keyelement = inStr;

						// Initialize a string buffer
						StringBuffer seqbuffer;

						//Get sequence from hash if it exists
						if (seqmap.containsKey(keyelement)) {
							seqbuffer = new StringBuffer(seqmap.get(keyelement));
						} else {
							seqbuffer = new StringBuffer(); // Create an empty strinbuffer
							seqmap.put(keyelement, seqbuffer);
						}

						// Go through the rest of the sequence "words"
						while (str.hasMoreTokens()) {
							// Append each sequence "word" to the buffer
							seqbuffer.append(str.nextToken());							

						}	
						seqmap.put(keyelement, seqbuffer);
					}
				}
				line = inFile.readLine();
			}
		} catch (IOException e) {			
			e.printStackTrace();						
		}
		// If there's no element in the vector --> throw Exception
		if(namesVector.size() == 0){
			throw new RuntimeException("Could not read the MSF-inputfile...");
		} else {
			names = new String[namesVector.size()];
			sequences = new String[namesVector.size()];
			for (int i = 0; i < namesVector.size(); i++) {
				if (seqmap.get(namesVector.get(i)) != null) {
					String seqID = namesVector.elementAt(i).toString();
					names[i] = seqID;
					
					String seqString = seqmap.get(seqID).toString();
					 
					// Replace the "." by the gap sign: "-"
					seqString = seqString.replace('.', '-');				
					sequences[i] = seqString;
				}
			}
		}
	}
	
	public String toString(){
		return "MSF File";
	}
}
