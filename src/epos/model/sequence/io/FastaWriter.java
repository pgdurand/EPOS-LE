package epos.model.sequence.io;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class FastaWriter extends AbstractAlignmentWriter{

	@Override
	public void write() throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < names.length; i++) {
			out.write(">"+names[i] +"\n");
			out.write(sequences[i] +"\n");			
		}
		out.close();
	}
	
	@Override
	public String toString() {
		return "Fasta File";
	}
}
