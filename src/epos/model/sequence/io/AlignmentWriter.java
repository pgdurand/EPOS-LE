package epos.model.sequence.io;

import java.io.File;

import epos.model.sequence.SequenceQNode;

public interface AlignmentWriter {
	public void setFile(File file);
	public void setSequences(String[] seqs);
	public void setNames(String[]  names);
	public void setType(SequenceQNode.Type type);
	public void write() throws Exception;
}
