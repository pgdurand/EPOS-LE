package epos.model.sequence.io;

import java.io.File;

import epos.model.sequence.SequenceQNode;

public interface AlignmentReader {	
	public String[] getNames();
	public String[] getSequences(); 
	public void setFile(File file);
	public void setType(SequenceQNode.Type type);
	public void read() throws Exception;
}
