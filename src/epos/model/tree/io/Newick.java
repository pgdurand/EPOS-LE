/*
 * Created on Jun 29, 2006
 */
package epos.model.tree.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import epos.model.tree.Tree;
import epos.model.tree.TreeNode;

public class Newick {
//    public static final Map<String, Property> nhx2prop = new HashMap<String, Property>();
//    public static final Map<Property, String> prop2nhx = new HashMap<Property, String>();
    private static HashMap parser;    
//    static{
//        nhx2prop.put("B", TreeNodeProperties.PROPERTY_BOOTSTRAP);
//        nhx2prop.put("S", TreeNodeProperties.PROPERTY_SPECIESNAME);
//        nhx2prop.put("T", TreeNodeProperties.PROPERTY_NCBIID);
//        nhx2prop.put("E", TreeNodeProperties.PROPERTY_ECNUMBER);
//        nhx2prop.put("D", TreeNodeProperties.PROPERTY_DUBLICATIONEVENT);
//        nhx2prop.put("O", TreeNodeProperties.PROPERTY_ORTHOLOGOUS);
//        nhx2prop.put("SO", TreeNodeProperties.PROPERTY_SUPERORTHOLOGOUS);
//        nhx2prop.put("L", TreeNodeProperties.PROPERTY_LOGLIKELIHOOD);
//        nhx2prop.put("Sw", TreeNodeProperties.PROPERTY_PLACESUBTREE);
//        nhx2prop.put("Co", TreeNodeProperties.PROPERTY_COLLAPSED);
//         
//        prop2nhx.put(TreeNodeProperties.PROPERTY_BOOTSTRAP, "B");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_SPECIESNAME, "S");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_NCBIID, "T");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_ECNUMBER, "E");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_DUBLICATIONEVENT, "D");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_ORTHOLOGOUS, "O");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_SUPERORTHOLOGOUS, "SO");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_LOGLIKELIHOOD, "L");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_PLACESUBTREE, "Sw");
//        prop2nhx.put(TreeNodeProperties.PROPERTY_COLLAPSED, "Co");
//    }

    public static Tree getTreeFromString(String line) {
    	// since 0.9 
    	// ensure that string is losed with a ;
    	if(!line.endsWith(";")){
    		line += ";";
    	}
        StringReader r = new StringReader(line);        
        return getTree(r);
    }
    
    
    public static Tree[] getAllTreesFromString(String line) {
        StringReader r = new StringReader(line);
        Tree[] t = getAllTrees(r);
        r.close();
        return t;
    }
    
    
    /**
     * Reads a newick String from the given file("name") and returns the parsed tree.
     * 
     * @param file the source file.
     * @return newly created Tree.
     */
    public static Tree[] getTreeFromFile(String file){
        Tree[] t;
        try {
            t = getAllTrees(new FileReader(file));
            return t;
        } catch (FileNotFoundException e) {
        }
        return null;
    }
    /**
     * Reads a newick String from the given file and returns the parsed tree.
     * 
     * @param file the source file.
     * @return a newly created tree.
     */
    public static Tree[] getTreeFromFile(File file){
        Tree[] t;
        try {
            t = getAllTrees(new FileReader(file));
            return t;
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static Tree[] getAllTrees(Reader r){        
        List<Tree> trees = new ArrayList<Tree>();
        Tree t = getTree(r);
        boolean e = false;
        while(t != null || e){
        	try {
        		if(!e && t != null) {
        			trees.add(t);
        		}else {
        			e = false;
        		}
        		t = getTree(r);
        	}catch(Exception e1) {
        		e = true;
        		t = null;
        	}
        }
        if(trees.size() == 0) return null;
        Tree[] ta = new Tree[trees.size()];
        trees.toArray(ta);
        return ta;
    }
    
    protected static Tree getTree(Reader r) {
        NewickStreamTokenizer st = new NewickStreamTokenizer(r);
        Tree t = new Tree();
        StringBuffer label = new StringBuffer();
        StringBuffer nhx = new StringBuffer();
        TreeNode n = new TreeNode();        
        TreeNode x = new TreeNode();
        t.addVertex(x);
        t.addVertex(n);
        t.addEdge(x,n);
        //x.addChild(n);
        t.setRoot(x);
        TreeNode c = null;

        
        double d = 1.0;
        boolean read = true;
        boolean distance = false;
        boolean addNhx = false;
        boolean escaped = false;
        boolean somethingRead = false;
        boolean finished  = false;
        while (read) {
            try {
                int next = st.nextToken();                
                if(addNhx && next != ']'){                    
                    if(st.sval == null){                        
                        nhx.append((char)next);
                    }
                    if(st.sval != null){
                        nhx.append(st.sval);
                    }
                    continue;
                }           
                
                if(escaped){
                	if(st.sval != null){
                		label.append(st.sval);
                	}else{
                		label.append(Character.toString((char)next));
                	}
                	escaped = false;
                	continue;
                }
                
                switch (next) {                 
                case StreamTokenizer.TT_WORD:
                    if(distance){
                        d = Double.parseDouble(st.sval);
                        distance = false;
                    }else{                        
                        label.append(st.sval);
                    }
                    break;
                case StreamTokenizer.TT_EOF:
                    read = false;
                    break;
                default:                	
                    if(st.ttype != ';')
                        somethingRead = true;
                    switch (st.ttype) {
                    case '(':                        
                        c = new TreeNode();      
                        t.addVertex(c);
                        t.addEdge(n,c);
                        //n.addChild(c);
                        n = c;
                        break;
                    case ')':
                        setLabel(n, label, nhx);
                        if(d != 1.0){
                            n.getEdgeToParent().setWeight(d);
                            d = 1.0;
                        }                        
                        label = new StringBuffer();
                        nhx = new StringBuffer();
                        n = n.getParent();
                        break;
                    case ',':
                        setLabel(n, label, nhx);
                        if(d != 1.0){
                            n.getEdgeToParent().setWeight(d);
                            d = 1.0;
                        }                        

                        label = new StringBuffer();
                        nhx = new StringBuffer();
                        c = new TreeNode();
                        t.addVertex(c);
                        t.addEdge(n.getParent(), c);
                        //n.getParent().addChild(c);
                        n = c;
                        break;
                    case ':':
                        distance = true;                                               
                        break;
                    case '[':
                        int peek = st.nextToken();
                        if(peek == ']'){
                            addNhx = true;
                        }else{
                            System.out.println("no ! not nhx");
                            label.append("[");
                        }
                        st.pushBack();
                                                                       
                        break;
                    case ']':
                        if(addNhx){
                            addNhx = false;
                        }else{
                            label.append("]");
                        }
                        break;

                    case ';':
                        setLabel(n, label, nhx);
                        if(d != 1.0){
                            if(n.getEdgeToParent() != null){
                                n.getEdgeToParent().setWeight(d);
                                d = 1.0;
                            }
                        }
                        label = new StringBuffer();
                        nhx = new StringBuffer();
                        read = false;
                        finished = true;
                        break;
                    case '\'':
                    case '"':                    	
                        label.append(st.sval);
                        break;
                    case '\\':                    	
                        escaped = true;
                        break;
                    }

                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(((TreeNode)t.getRoot()).childCount() == 1){
            TreeNode child = ((TreeNode)t.getRoot()).getChildAt(0);
            t.removeVertex(t.getRoot());
            t.setRoot(child);
        }        
        if(!finished && somethingRead) {
        	throw new RuntimeException("Tree definition was not closed with a semicolon ; !");
        }
        return somethingRead ? t:null;
    }
    
    private static void setLabel(TreeNode n, StringBuffer label, StringBuffer nhxB){
        if(nhxB.length() > 0){
            String nhx = nhxB.substring(6);
            String[] tags = nhx.split(":");
            HashMap<String, String> extLabel = new HashMap<String, String>();                
            for (int k = 0; k < tags.length; k++) {                    
                String[] nameValue = tags[k].split("=");
                extLabel.put(nameValue[0], nameValue[1]);
            }
            createTaxonInfo(n, extLabel);
        }
        if(label.length() > 0){
            String l = label.toString();
            l = l.trim();
            n.setLabel(l);
        }
    }
    private static void createTaxonInfo(TreeNode node, Map<String,String> extLabel) {
//        for (String s : extLabel.keySet()) {
//            Property p = nhx2prop.get(s);
//            if(p != null){
//                node.setProperty(p, parseData(extLabel.get(s), p.getType()));
//            }
//        }
    }
    private static Object parseData(String s, Class type){        
        return null;//Property.getDataParser(type).parse(s);
    }

    
    public static String getStringFromTree(Tree t) {
    	return getStringFromTree(t, true);
    }
    
    public static String getStringFromTree(Tree t, boolean addSemicolon) {
        StringBuffer treeString = new StringBuffer();
        TreeNode root = (TreeNode)t.getRoot();
        appendNode(root, treeString);
        return treeString.toString() + (addSemicolon ? ";" : "");    	
    }
    
    
    /**
     * This is an internal method to parse the given String buffer.
     * The method converts the given node to newick string and appends 
     * the result to the given StringBuffer.
     * 
     * @param node
     * @param treeString
     */
    private static void appendNode(TreeNode node, StringBuffer s) {
        if(node == null)
            return;
        
        if(!node.isLeaf()){
            s.append("(");
            boolean added = false;
            for (TreeNode n : node.children()) {
                appendNode(n, s);
                s.append(',');
                added = true;
            }
            if(added)
                s.deleteCharAt(s.length()-1);
            s.append(")");
        }
        if(node.getLabel() != null){
        	/*
        	 * Escape characters in the label
        	 * 
        	 * Escape all : as \:
        	 * Escape all " " (space) as "\ "
        	 */
        	
        	String label = node.getLabel();
        	if(label.indexOf(":") >=0){
        		Pattern p = Pattern.compile(":");
        		Matcher matcher = p.matcher(label);;
        		label = matcher.replaceAll("\\\\$0");        		
        	}
    		Pattern spacePattern = Pattern.compile("\\s");
    		Matcher matcher = spacePattern.matcher(label);;
    		label = matcher.replaceAll("\\\\$0");        		

            s.append(label);
        }        
        if(node.getDistanceToParent() != 1.0 && node.getDistanceToParent() >= 0){
            s.append(":" + node.getDistanceToParent());
        }
        writeNHXExtensiont(node, s);              
    }


    private static void writeNHXExtensiont(TreeNode node, StringBuffer s) {
//        PropertySet ps = node.getPropertySet();
//        StringBuffer buf = new StringBuffer();
//        if(ps != null){
//	        for (Property p : ps.getProperties()) {
//	            String nhx = prop2nhx.get(p);
//	            if(nhx != null){
//	                buf.append(":"+nhx+"="+Property.getDataParser(p.getType()).getString(node.getProperty(p.getName())));
//	            }
//	        }
//        }
//        if(buf.length() > 0){
//            s.append("[&&NHX");
//            s.append(buf);
//            s.append("]");
//        }
    }
    
    public static boolean tree2File(File outFile, Tree tree){
        String theString = getStringFromTree(tree);     
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            out.write(theString);
            out.flush();            
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    
    public static boolean trees2File(File outFile, Tree[] trees){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            for (int i = 0; i < trees.length; i++) {            
                String theString = getStringFromTree(trees[i]);     
                    out.write(theString);
                    out.write("\r\n");
                    out.flush();            
            }
            out.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}

class NewickStreamTokenizer extends StreamTokenizer {

    public NewickStreamTokenizer(Reader r) {
        super(r);
        resetSyntax();
        wordChars(0, 255);
        whitespaceChars(0, '\n');

        ordinaryChar(';');
        ordinaryChar(',');
        ordinaryChar(')');
        ordinaryChar('(');
        ordinaryChar('[');
        ordinaryChar(']');
        ordinaryChar(':');
        ordinaryChar('\\');
        //commentChar('/');
        quoteChar('"');
        quoteChar('\'');
    }
}
