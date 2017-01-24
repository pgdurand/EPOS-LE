package epos.model.tree.io;

import java.io.File;

import org.biojavax.bio.phylo.io.nexus.NexusFile;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;

public class NexusExporter {	
	private Tree[] trees;
	private File file;
	
	private boolean createTranslationTable = true;

	public boolean isCreateTranslationTable() {
		return createTranslationTable;
	}

	public void setCreateTranslationTable(boolean createTranslationTable) {
		boolean old = this.createTranslationTable;
		this.createTranslationTable = createTranslationTable;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setTrees(Tree[] trees) {
		this.trees = trees;
	}

	public void write() throws Exception {		
		NexusTreeBlock block = new NexusTreeBlock();		
		for (Tree t : trees) {
			if(createTranslationTable){
				Tree t2 = t.getSubtree(t.getRoot());
				t2.setName(t.getName());
				int counter = 0;
				for (TreeNode n : t2.getRoot().depthFirstIterator()) {
					if(n.getLabel() != null){
						String label = n.getLabel();
						n.setLabel(""+counter);
						block.addTranslation(""+counter, label);
						counter++;
					}
				}
				block.addTree(t2);
			}else{
				block.addTree(t);	
			}			
		}
		NexusFile nf = new NexusFile();
		nf.addObject(block);
		NexusFileFormat.writeFile(file, nf);
	}
	
	public String toString(){
		return "Nexus Tree File";
	}
}



