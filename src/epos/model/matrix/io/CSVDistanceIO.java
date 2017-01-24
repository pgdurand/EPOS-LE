package epos.model.matrix.io;

import java.io.File;

import org.jdesktop.application.AbstractBean;

import epos.model.matrix.DistanceMatrixModel;

public class CSVDistanceIO extends AbstractBean implements DistanceIO{

	private File                file;
	private int                 type;
	private String              delimiter = "\t";
	private DistanceMatrixModel model;
	
	public DistanceMatrixModel getDistanceMatrixModel() {
		if(model == null){
			getMatrix();
		}
		return model;
	}

	public double[][] getMatrix() {
		model =DistanceCSVIO.getDistanceMatrixModel(file, type, delimiter); 
		return model.getDistances();
	}

	public String[] getNames() {
		if(model == null){
			getMatrix();
		}
		return model.getNames();
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		String old = this.delimiter;
		this.delimiter = delimiter;
		firePropertyChange("delimiter", old, this.delimiter);		
	}

	public void setFile(File f) {
		this.file = f;
	}
	
	public String toString(){
		return "CSV Distance File";
	}

}
