package epos.model.tree.io;

import java.io.File;

import epos.model.tree.Tree;

public interface TreeExporter {
	public void setFile(File file);
	public void setTrees(Tree[] trees);
	public void write() throws Exception;
}
