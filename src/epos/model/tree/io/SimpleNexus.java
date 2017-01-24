package epos.model.tree.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import epos.model.graph.methods.DFS;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;

//class to parse trees out of a nexus file
public class SimpleNexus {

	private static Map<String, String> initTaxLabels(String foreststring, String foreststring_copy) {

		int startpos = foreststring_copy.indexOf("BEGIN TAXA;");
		String[] taxLabels = null;
		if (startpos == -1){
			
		} else {
			int endpos = foreststring_copy.indexOf("END;");
			String taxlabels = foreststring.substring(startpos, endpos - 1);
			int startPosTaxLabels = foreststring_copy.indexOf("TAXLABELS");
			taxlabels = foreststring.substring(startPosTaxLabels, endpos - 1);
			taxlabels = taxlabels.substring(9, taxlabels.length());
			taxlabels = taxlabels.trim();
			taxLabels = taxlabels.split("\\s+");
		}
		Map<String, String> intMap = new HashMap<String, String>();
		if(taxLabels != null){
			for(int i = 0; i < taxLabels.length; i++){
				Integer t = Integer.valueOf(i);
				String key = t.toString();
				intMap.put(key, taxLabels[i]);
			}
		}
		return intMap;
	}
	
	private static Map<String, String> initTranslation(String foreststring, String foreststring_copy){
		int startpos = foreststring_copy.indexOf("TRANSLATE");
	
		if (startpos == -1){
			return null;
		} else {
			int stopPos = foreststring_copy.indexOf("TREE ");
		
			String translate = foreststring.substring(startpos, stopPos); 
			translate = translate.trim();
	  		translate = translate.substring(9, translate.length());
	  		translate = translate.trim();
	  		String[] test = translate.split("\\s+"); 
			
			Map<String, String> translateMap = new HashMap<String, String>();
			
			for (int x = 0; x < (test.length - 1); x += 2){
				String key = test[x];
			    for (int r = x + 1; r < test.length; r++){
				 String value = test[r];
				 // cut of ; behind the names
				 value = value.substring(1, value.length() - 1 );
				 translateMap.put(key, value);
				 break;
				}
			}
			return translateMap;
		}
		
//		for ( String elem : translateMap.keySet() ) 
//			  System.out.println( elem + " --> "+ translateMap.get(elem));
		
	}

	private static ArrayList<String> parseForest(String foreststring, String foreststring_copy) {
		ArrayList<String> l = new ArrayList<String>();
		// look for the preamble
		int pos = foreststring.indexOf('#');
		if (pos == -1)
			return l;
		if (!foreststring.substring(pos, pos + 6).equalsIgnoreCase("#nexus"))
			return l;
		pos = foreststring_copy.indexOf("BEGIN TREES;", pos);
		if (pos == -1)
			return l;

		// look for the first tree
		int starttreepos = foreststring_copy.indexOf("TREE", pos + 12);
		int endtreepos = foreststring_copy.indexOf(';', starttreepos);

		if (starttreepos == -1 || endtreepos == -1)
			return l;

		// read all the trees
		boolean moretrees = true;
		while (moretrees) {
			String treestring = foreststring.substring(starttreepos,
					endtreepos + 1);
			l.add(treestring);
			starttreepos = foreststring_copy.indexOf("TREE", endtreepos);
			endtreepos = foreststring_copy.indexOf(';', starttreepos);
			if (starttreepos == -1)
				moretrees = false;
		}
		return l;
	}

     
    // returns the trees without their names at the beginning
	private static List<String> prepareTrees(ArrayList<String> parsedForest){
		String tree;
		String treeName;
		List<String> parsedForest_names = new ArrayList<String>();
		//get the name of the trees 
		for(int i = 0; i < parsedForest.size(); i++) {
         	tree = parsedForest.get(i);
		    treeName = tree.substring(0, (tree.indexOf("=")));
		    treeName.trim();
		    treeName = treeName.substring(5, treeName.length());
		    treeName.trim();
			parsedForest_names.add(treeName);
			tree = tree.substring(tree.indexOf("("), tree.length());
			parsedForest.remove(i);
			parsedForest.add(i, tree);
		}
		return parsedForest_names;
	}

	
	/*
	 * Removes all comments within square brackets [...] from a string.
	 * 
	 * @param s
	 *            the string from which the comments are to be removed.
	 * @return s without comments.
	 */
	private static String removeComments(String s) {
		String ret = s;
		int pos1 = s.indexOf('[');
		int pos2 = s.indexOf(']', pos1);
		while (pos1 != -1 && pos2 != -1) {
			ret = ret.substring(0, pos1) + ret.substring(pos2 + 1);
			pos1 = ret.indexOf('[');
			pos2 = ret.indexOf(']', pos1);
		}
		return ret;
	}
    
	
	/**
	 * This method returns the trees from a NEXUS string. 
	 * @return Tree[]
	 */
	public static Tree[] getTreesFromString(String input) {
		String foreststring = removeComments(input);
		String foreststring_copy = foreststring.toUpperCase();
		Map<String, String> intMap = initTaxLabels(foreststring, foreststring_copy);
		Map<String, String> translateMap = initTranslation(foreststring, foreststring_copy);
	    ArrayList<String> parsedForest = parseForest(foreststring, foreststring_copy);
	    List<String> parsedForest_names = prepareTrees(parsedForest);

		Tree[] trees = new Tree[parsedForest.size()];
        
		for(int i = 0; i < parsedForest.size() ; i++){
		   Tree t = Newick.getTreeFromString(parsedForest.get(i));
		   t.setName(parsedForest_names.get(i));
		   //System.out.println("setze name fuer tree : "+parsedForest_names.get(i) );
		   trees[i] = t;
		}
		
		//replace intStrings in the tree and translate names
		
		for (Tree tree : trees) {
			DFS dfs = new DFS(tree);
			TreeNode root = tree.getRoot();
			
			
			String label;
			
			Iterator depth_iter = dfs.iterator(root);
			while(depth_iter.hasNext()){
				TreeNode n = (TreeNode) depth_iter.next();
				if(n.isLeaf() && n.getLabel() != null && (translateMap != null || intMap != null)){
					label = n.getLabel();
					if(translateMap != null && translateMap.get(label) != null){
			          n.setLabel(translateMap.get(label));      	
			        }else if(intMap != null && intMap.get(label) != null){
				      n.setLabel(intMap.get(label));      	
				    }
				}
			}
		}
		return trees;
	}
	
	public static Tree[] getTreesFromFile(File f) throws IOException{
		FileReader reader;
		StringBuffer foreststring = new StringBuffer();
		reader = new FileReader(f);
		BufferedReader buffer = new BufferedReader(reader);
		while (buffer.ready()) {
			foreststring.append(buffer.readLine());
		}
		buffer.close();
		return getTreesFromString(foreststring.toString());
	}

	public static Tree[] getTreesFromFile(String file) throws IOException {
		return getTreesFromFile(new File(file));
	}
}