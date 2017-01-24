package epos.model.sequence.io;

import java.io.File;

import org.jdesktop.application.AbstractBean;

import epos.model.sequence.SequenceQNode.Type;

public abstract class AbstractAlignmentWriter extends AbstractBean implements AlignmentWriter{
	protected String[] names;
	protected String[] sequences;
	protected Type	 type;
	protected File file;
	
	public void setNames(String[] names) {
		this.names = names;
	}

	public void setSequences(String[] seqs) {
		this.sequences = seqs;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public void setFile(File file) {
		this.file = file;
	}

	public abstract  void write() throws Exception;
}
