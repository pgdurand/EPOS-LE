package epos.model.matrix.io;

import java.io.File;

import org.jdesktop.application.AbstractBean;

import epos.model.sequence.io.MatrixWriter;

public abstract class AbstractMatrixWriter extends AbstractBean implements MatrixWriter{

	protected File file;
	protected String[] names;
	protected double[][] matrix;

	public void setFile(File f) {
		this.file = f;		
	}

	public void setMatrix(String[] names, double[][] matrix) {
		this.names = names;
		this.matrix = matrix;
	}

	public abstract  void write() throws Exception;
}
