/*
 * Created on 20.04.2006
 */
package epos.model.matrix.io;

import java.io.File;

import epos.model.matrix.DistanceMatrixModel;

public interface DistanceIO {
	public void setFile(File f);
    public double[][] getMatrix();
    public DistanceMatrixModel getDistanceMatrixModel();
    public String[] getNames();    
}
