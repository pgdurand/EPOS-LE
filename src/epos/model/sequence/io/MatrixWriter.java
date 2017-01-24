package epos.model.sequence.io;

import java.io.File;

public interface MatrixWriter {
	public void setFile(File f);
	public void setMatrix(String[] names, double[][] matrix);
	public void write() throws Exception;
}
