/*
 * Created on 24.11.2005
 */
package epos.model.matrix.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import epos.model.matrix.DistanceMatrixModel;

public class DistanceCSVIO implements DistanceIO{
	public static final int ZEROS = 1;
	public static final int COMPLETE = 2;        
    public static final int UPPERLEFT = 4;
    public static final int LOWERLEFT = 8;
    
    public static final String DEFAULT_DELIMITER = "\t";
    public static final int DEFAULT_TYPE = LOWERLEFT;
    
    public static MatrixContainer parseFile(File file, int type){
    		return parseFile(file, type, DEFAULT_DELIMITER);
    }
    public static MatrixContainer parseFile(File file, int type, String delimiter){
    		MatrixContainer c = new MatrixContainer();    		
    		int lineNumbers = countLineNumbers(file);
    		c.setLines(lineNumbers);    		
    		String[] names;
    		
        if(lineNumbers < 0)
            return null;
        
        try {        	
            Number[][] m = new Number[lineNumbers][lineNumbers];
            for (int i = 0; i < m.length; i++) {
                Arrays.fill(m[i], new Double(0));
            }
            names = new String[lineNumbers];
            
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            int lineCounter =0;
            //boolean zero = (type & ZEROS) == 1;
            int zero = -1;
            if((type & UPPERLEFT) == UPPERLEFT){
            		if((type & ZEROS) == ZEROS)
            			zero = -1;
            		else
            			zero = 0;            		
            }
            while(line != null){
                String[] split = line.split(delimiter);
                
                for (int i = 0; i < split.length; i++) {
                    switch (i) {
	                    case 0:
	                    	 	// first match is allways the name
	                        names[lineCounter] = split[i];
	                        break;
	                    default:
	                        if(split[i].length() <=0){
	                            m[lineCounter][i+zero] = new Double(0);
	                        }else{
	                            m[lineCounter][i+zero] = Double.valueOf(split[i]);//Double.parseDouble(split[i]);
	                        }
	                        break;
	                    }
                }
                lineCounter++;
                line = in.readLine();
            }
            in.close();
            c.setMatrix(m);
            c.setNames(names);
            return fillMatrix(type, c);
        } catch (FileNotFoundException e) {
            System.err.println("File not found " + file.getAbsoluteFile());
        } catch (IOException e) {
            System.err.println("Error while reading file " + file.getAbsolutePath());
        }
        return null;
    }
    
    protected static MatrixContainer fillMatrix(int type, MatrixContainer c) {
    		Number[][] m = c.getMatrix();
    		if((type & UPPERLEFT) == UPPERLEFT){
                for (int i = 0; i < m.length; i++) {
                    for (int j = 0; j < m.length; j++) {                    		
                        if(i != j) {
                        	//m[i][j] = m[j][i];
                        	m[j][i] = m[i][j];
                        	//System.out.println("Settin " + i +" " + j+ " "+ m[i][j]);                         	
                        }
                    }
                }    			
    		}else if((type & LOWERLEFT) == LOWERLEFT){
                for (int i = 0; i < m.length; i++) {
                    for (int j = 0; j < m.length; j++) {
                        if(i != j) m[i][j] = m[j][i]; 
                    }
                }    			
    		}
    		c.setMatrix(m);
        return c;
    }

    public static String[] getNames(File file, int lineNumbers){
    		String[] names;
        if(lineNumbers < 0)
            return null;
        
        try {
            names = new String[lineNumbers];
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            int counter = 0;
            while(in != null){
                names[counter++] = line.split("\\t")[0];
                line = in.readLine();
            }
            in.close();
            return names;
        } catch (FileNotFoundException e) {
            System.err.println("File not found " + file.getAbsoluteFile());
        } catch (IOException e) {
            System.err.println("Error while reading file " + file.getAbsoluteFile());
            e.printStackTrace();
        }
        return null;
                
    }

    public static DistanceMatrixModel getDistanceMatrixModel(File file){    		
        return getDistanceMatrixModel(file, DEFAULT_TYPE, DEFAULT_DELIMITER);
    }

    public static DistanceMatrixModel getDistanceMatrixModel(File file, int type){    		
        return getDistanceMatrixModel(file, type, DEFAULT_DELIMITER);
    }
    public static DistanceMatrixModel getDistanceMatrixModel(File file, int type, String delimiter){    		
        MatrixContainer m = parseFile(file, type, delimiter);
        DistanceMatrixModel model = new DistanceMatrixModel(m.toPrimitiveDouble(), m.getNames());        
        return model;
    }

    
    protected static int countLineNumbers(File file){
        int lines = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            while(in.readLine() != null)lines++;
            in.close();
            return lines;
        } catch (FileNotFoundException e) {
            System.err.println("File not found " + file.getAbsoluteFile());
        } catch (IOException e) {
            System.err.println("Error while reading file " + file.getAbsoluteFile());
        }
        return -1;
    }

    public static void writeMatrix(File file, String[] names, double[][] matrix) throws IOException{
		writeMatrix(file, names, matrix, DEFAULT_DELIMITER, DEFAULT_TYPE);
    }

    public static void writeMatrix(File file, String[] names, double[][] matrix, int type) throws IOException{
    		writeMatrix(file, names, matrix, DEFAULT_DELIMITER, type);
    }
    
    public static void writeMatrix(File file, String[] names, double[][] matrix, String delimiter, int type) throws IOException{
    		BufferedWriter out = new BufferedWriter(new FileWriter(file));
    		
    		if((type & COMPLETE) == COMPLETE){
    			for (int i = 0; i < names.length; i++) {
    				out.write(names[i] + delimiter);
    				for (int j = 0; j < matrix[i].length; j++) {
    					out.write(matrix[i][j] + delimiter);
				}
    				out.write("\n");
			}
    		}

    		if((type & LOWERLEFT) == LOWERLEFT){
    			boolean zero = (type & ZEROS) == ZEROS;
    			for (int i = 0; i < names.length; i++) {
    				out.write(names[i] + delimiter);
    				for (int j = 0; j < matrix[i].length; j++) {
    					if(j < i)    					
    						out.write(matrix[i][j] + delimiter);
    					if(j == i && zero)
    						out.write(matrix[i][j] + delimiter);
				}
    				out.write("\n");
			}
    		}
    		
    		if((type & UPPERLEFT) == UPPERLEFT){
    			boolean zero = (type & ZEROS) == ZEROS;
    			for (int i = 0; i < names.length; i++) {
    				out.write(names[i] + delimiter);
    				for (int j = 0; j < matrix[i].length; j++) {
    					if(j <= i){
    						if(j < i)
    							out.write(delimiter);
    						else if( j == i && zero)
    							out.write(matrix[i][j] + delimiter);
    					}
    					else
    						out.write(matrix[i][j] + delimiter);
				}
    				out.write("\n");
			}
    		}
    		out.flush();
    		out.close();

    		
    }
    public static class MatrixContainer{
    		Number[][] matrix;
    		String[] names;
    		int lines;
    		
    		public MatrixContainer(){
    			super();
    		}

			public int getLines() {
				return lines;
			}

			public void setLines(int lines) {
				this.lines = lines;
			}

			public Number[][] getMatrix() {
				return matrix;
			}

			public void setMatrix(Number[][] matrix) {
				this.matrix = matrix;
			}

			public String[] getNames() {
				return names;
			}

			public void setNames(String[] names) {
				this.names = names;
			}
			public double[][] toPrimitiveDouble(){
				double[][] m = new double[matrix.length][];
				for (int i = 0; i < m.length; i++) {
					m[i] = new double[matrix[i].length];
					for (int j = 0; j < m[i].length; j++) {
						m[i][j] = matrix[i][j].doubleValue();
					}
				}
				return m;
			}
    }
    
    protected File file;    
    protected int lineNumbers;    
    protected String[] names;
    protected int type = LOWERLEFT;
    protected String delimiter = "\t";
    
    public DistanceCSVIO(File file){
        super();
        this.file = file;
        lineNumbers = countLineNumbers(file);        
     
    }
    
    public DistanceCSVIO(String fileName){
        this(new File(fileName));
    }
    
    public DistanceCSVIO(File input, int type) {
        this(input);
        this.type = type;
    }
    public DistanceCSVIO(String input, int type) {
        this(input);
        this.type = type;
    }
    public DistanceCSVIO(String input, int type, String delimiter) {
        this(input);
        this.type = type;
        this.delimiter = delimiter;
    }
    public DistanceCSVIO(File input, int type, String delimiter) {
        this(input);
        this.type = type;
        this.delimiter = delimiter;
    }



    public double[][] getMatrix() {
        DistanceMatrixModel m = DistanceCSVIO.getDistanceMatrixModel(file, type, delimiter);
        return m.getDistances();
    }
    public DistanceMatrixModel getDistanceMatrixModel() {
        return DistanceCSVIO.getDistanceMatrixModel(file, type, delimiter);
    }
    public String[] getNames() {
        DistanceMatrixModel m = DistanceCSVIO.getDistanceMatrixModel(file, type, delimiter);
        return m.getNames();        
    }
	public void setFile(File f) {
		this.file = f;
		
	}
}
