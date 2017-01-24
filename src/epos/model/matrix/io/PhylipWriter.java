package epos.model.matrix.io;

import java.io.FileWriter;

public class PhylipWriter extends AbstractMatrixWriter{
	private boolean writeTriangle = true;
	private boolean phylipNameLimit = true;
	
	@Override
	public void write() throws Exception {
		PhylipMatrixIO m = new PhylipMatrixIO();
		m.write(new FileWriter(file), names, matrix);
	}

	public boolean isWriteTriangle() {
		return writeTriangle;
	}

	public void setWriteTriangle(boolean writeTriangle) {
		boolean old = this.writeTriangle;
		this.writeTriangle = writeTriangle;
		firePropertyChange("writeTriangle", old, this.writeTriangle);
	}

	public boolean isPhylipNameLimit() {
		return phylipNameLimit;
	}

	public void setPhylipNameLimit(boolean phylipNameLimit) {
		boolean old = this.phylipNameLimit;
		this.phylipNameLimit = phylipNameLimit;
		firePropertyChange("phylipNameLimit", old, this.phylipNameLimit);
	}
	
	@Override
	public String toString() {
		return "Phylip matrix";
	}
	
	

}
