package epos.model.matrix.io;


public class CSVWriter extends AbstractMatrixWriter{
	private String delimiterSelection = "\t";
	private String typeSelection = "Complete";
	private String delimiter = "\t";
	private int    type = DistanceCSVIO.COMPLETE;

	@Override
	public void write() throws Exception {
		DistanceCSVIO.writeMatrix(file, names, matrix,delimiter, type);
	}
	public String getDelimiterSelection() {
		return delimiterSelection;
	}
	public void setDelimiterSelection(String delimiterSelection) {
		String old = this.delimiterSelection;
		this.delimiterSelection = delimiterSelection;
		firePropertyChange("delimiterSelection", old, this.delimiterSelection);
		
		if(delimiterSelection.equals("TAB")){
			delimiter = "\t";
		}else if(delimiterSelection.equals("SPACE")){
			delimiter = " ";
		}else{
			delimiter = this.delimiterSelection;
		}
	}
	
	public String getTypeSelection() {
		return typeSelection;
	}
	
	public void setTypeSelection(String typeSelection) {
		String old = this.typeSelection;
		this.typeSelection = typeSelection;
		firePropertyChange("typeSelection", old, this.typeSelection);
		
		if(typeSelection.equals("Complete")){
			this.type = DistanceCSVIO.COMPLETE;
		}else{
			this.type = DistanceCSVIO.LOWERLEFT;
		}
	}
	
	@Override
	public String toString() {
		return "CSV Export";
	}

}
