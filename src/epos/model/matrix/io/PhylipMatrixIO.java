package epos.model.matrix.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;

import de.unijena.bioinf.commons.plugin.annotations.ClassExtension;
import epos.model.matrix.DistanceMatrixModel;

/**
 * Reads and writes phylip matrix files/streams.
 * 
 *
 * @author Thasso Griebel (thasso@minet.uni-jena.de)
 *
 */
@ClassExtension("epos.model.matrix@distanceio")
public class PhylipMatrixIO implements DistanceIO{
	
	private ArrayList<String> data;
	private String[] names;
	private double[][] matrix;	
	private boolean writeTriangle = false;
	private boolean phylipNameLimitEnabled = true;
	
	public PhylipMatrixIO(){
		super();
	}
	
	public void setData(String data) throws IOException{
		setData(new StringReader(data));
	}	
	
	public void setData(File file) throws IOException{
		setData(new FileInputStream(file));
	}
	
	public void setData(InputStream in) throws IOException{
		setData(new InputStreamReader(in));
	}
	
	public void setData(Reader in) throws IOException {
		BufferedReader r = new BufferedReader(in);
		this.data = new ArrayList<String>();
		String line = r.readLine();
		while(line != null){
			//line = line.trim();
			this.data.add(line);
			line = r.readLine();
		}				
	}

	public double[][] getMatrix() {
		if(matrix == null){
			readData();
		}
		return matrix;
	}
	public String[] getNames(){
		if(names == null) readData();
		return names;
	}
	
	public DistanceMatrixModel getDistanceMatrixModel() {
		if(data == null) return null;
		readData();
		DistanceMatrixModel m = new DistanceMatrixModel(matrix, names);
		return m;
	}
	
	public void write(Writer w, String[] names, double[][] mat) throws IOException{
		BufferedWriter out = new BufferedWriter(w);
		out.write("\t" + names.length + "\n");
		for (int i = 0; i < names.length; i++) {
			String n = names[i];		
			if(n.length() > 10){
				n = n.substring(0,9);
			}
			while(n.length()<9){
				n+=" ";
			}
			out.write(n);
			// write at least one space
			out.write(" ");
			if(!writeTriangle){
				for (int j = 0; j < mat[i].length; j++) {
					out.write(mat[i][j] + (j+1==mat[i].length ? (i+1==names.length ? "" : "\n") : "  "));
				}
			}else{
				if(i > 0){
					for (int j = 0; j < i; j++) {
						out.write(mat[i][j] + (j+1==i ? (i+1==names.length ? "" : "\n") : "  "));
					}					
				}else{
					out.write("\n");
				}
			}
		}
		out.close();
	}
	
	public void write(File f, String[] names, double[][] matrix) throws IOException{
		write(new FileWriter(f), names, matrix);
	}

	protected void readData() {
		if(data == null) return;
		
		String first = data.get(0);
		first = first.trim();
		int size = Integer.parseInt(first);
		
		names = new String[size];
		matrix = new double[size][size];
		
		int lc = 1;
		Boolean triangular = null;
		for(int i=0; i<size; i++){
			String line = data.get(lc++);
			while(line == null || line.length() == 0){
				line = data.get(lc++);
				//line = line.trim();
			}
			String[] line_split = line.split("\\s+");
			
			if(triangular ==null){
//				if(!strictMode)
//					triangular = (i == 0 && line_split.length == 1 || line.length() <=10);
//				else{
					triangular = (i == 0 && line.length() ==10);
//				}
			}
			String name = "";
//			if(strictMode){
				name = line.substring(0, 9);
				name = name.trim();
				if(line.length() > 10){
					line = line.substring(10);
					line = line.trim();
					line_split = line.split("\\s+");				
				}else{
					names[i] = name;
					continue;
				}
//			}else{			
//				name = line_split[0];
//				name = name.trim();
//				line = line.trim();
//				line_split = line.split("\\s+");				
//				line_split[0] = null;
//			}			
			names[i] = name;//line_split[0];

			int lc_index = 0;
			int len = 0;
			int it_length = 0;
			matrix[i][i] = 0;
			int offset = 0;
			//for (int j = 0; j < names.length; j++) {
			int j = 0;
			while(triangular ? (j < i) : (j < names.length)){	
				if((lc_index < line_split.length) && line_split[lc_index] == null){
					lc_index++;
					offset++;
					j++;
					continue;
				}
				if(j >= it_length+line_split.length){
					it_length += line_split.length;
					line = data.get(lc++);		
					line = line.trim();
					line_split = line.split("\\s+");
					lc_index = 0;
					offset = 0;
				}
				if(!triangular){
					matrix[i][j-offset] = Double.parseDouble(line_split[lc_index++]);									
				}else{
					matrix[i][j-offset] = Double.parseDouble(line_split[lc_index++]);
					matrix[j-offset][i] = matrix[i][j-offset];
				}				
				len++;
				j++;
			}
			if(len != (triangular ?  i : size)){
				throw new RuntimeException("Not enough elements in matrix. Check the matrix! Only "+len + " found but "+ size + " expected");
			}
		}		
	}

	public void setWriteTriangle(boolean writeTriangle) {
		this.writeTriangle = writeTriangle;
	}
	
	public boolean isWriteTriangle(){
		return this.writeTriangle;
	}

	public boolean isPhylipNameLimitEnabled() {
		return phylipNameLimitEnabled;
	}

	public void setPhylipNameLimitEnabled(boolean phylipNameLimitEnabled) {
		this.phylipNameLimitEnabled = phylipNameLimitEnabled;
	}

	public void setFile(File f) {
		try {
			setData(f);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public String toString(){
		return "Phylip Matrix";
	}
	
}
