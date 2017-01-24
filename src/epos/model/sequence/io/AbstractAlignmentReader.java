package epos.model.sequence.io;

import java.io.File;

import org.jdesktop.application.AbstractBean;

import epos.model.sequence.SequenceQNode.Type;

public abstract class AbstractAlignmentReader extends AbstractBean implements AlignmentReader{
	protected String[] names;
	protected String[] sequences;
	protected File file;
	protected Type type;
	
	public String[] getNames() {
		return names;
	}

	public String[] getSequences() {
		return sequences;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public abstract void read() throws Exception;
}
