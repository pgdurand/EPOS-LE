package epos.model.tree.io;

import java.io.File;

import epos.model.tree.Tree;

public class NewickExporter implements TreeExporter {	
	private Tree[] trees;
	private File file;

	public void setFile(File file) {
		this.file = file;
	}

	public void setTrees(Tree[] trees) {
		this.trees = trees;
	}

	public void write() throws Exception {
		Newick.trees2File(file, trees);			
	}
	public String toString(){
		return "Newick Tree File";
	}

}
