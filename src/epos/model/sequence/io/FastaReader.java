package epos.model.sequence.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.biojava.bio.BioException;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.biojavax.bio.seq.RichSequence.IOTools;

import epos.model.sequence.SequenceQNode.Type;

public class FastaReader extends AbstractAlignmentReader {
	private Type type;
	private File file;
	private String[] names;
	private String[] seqs;

	public void read() throws Exception {
		FileReader input;
		input = new FileReader((File) file);
		RichSequenceIterator iterator = null;
		switch (type) {
		case DNA:
			iterator = IOTools.readFastaDNA(new BufferedReader((Reader) input),
					null);
			break;
		case RNA:
			iterator = IOTools.readFastaRNA(new BufferedReader((Reader) input),
					null);
			break;
		case PROTEIN:
			iterator = IOTools.readFastaProtein(new BufferedReader(
					(Reader) input), null);
			break;
		}
		ArrayList<String> seqs = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		while (iterator.hasNext()) {
			RichSequence s;
			try {
				s = iterator.nextRichSequence();
				names.add(s.getName());
				seqs.add(s.seqString());
			} catch (NoSuchElementException e) {
				Logger.getLogger(getClass()).error(e);
			} catch (BioException e) {
				Logger.getLogger(getClass()).error(e);
			}
		}
		this.names = new String[names.size()];
		this.names = names.toArray(this.names);
		this.seqs = new String[seqs.size()];
		this.seqs = seqs.toArray(this.seqs);
	}

	public String toString() {
		return "FASTA File";
	}
}
