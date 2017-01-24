package epos.model.tree.io;

import java.io.File;

import epos.model.tree.Tree;

public interface TreeImporter {
	public void setFile(File file);
	public Tree[] getTrees();
}
