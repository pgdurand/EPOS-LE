package epos.model.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * QNode that holds a list of alignments as children.
 * 
 * @author Thasso Griebel (thasso@minet.uni-jena.de)
 *
 */
public class AlignmentListQNode {
	private boolean original;
	private String name;
	/**
	 * Create new alignemnt list
	 */
	public AlignmentListQNode() {
		super();
	}
	/**
	 * Create new alignment list with given namen
	 * 
	 * @param name name
	 */
	public AlignmentListQNode(String name) {
		this();
		this.name = name;
	}
	/**
	 * 
	 * @return true if this list holds original imports
	 */
	public boolean isOriginal() {
		return original;
	}
	/**
	 * @param original set to true if the list holds original imports 
	 */
	public void setOriginal(boolean original) {
		this.original = original;
	}
	/**
	 * Collects all {@link AlignmentQNode}s that are children of this node.
	 * 
	 * @return array of {@link AlignmentQNode}s
	 */
	/*public AlignmentQNode[] getMatrices() {
        List<AlignmentQNode> alignments = new ArrayList<AlignmentQNode>();
        for (QNode c : children) {
            if(c instanceof AlignmentQNode)
                alignments.add((AlignmentQNode) c);
        } 
        AlignmentQNode[] t = new AlignmentQNode[alignments.size()];
        alignments.toArray(t);
        return t;

	}*/

}
