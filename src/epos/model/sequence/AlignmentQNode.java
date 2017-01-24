package epos.model.sequence;

import java.util.ArrayList;

import epos.model.sequence.SequenceQNode.Type;
public class AlignmentQNode{
	
	private ArrayList<SequenceQNode> sequences;
	private transient SequenceQNode.Type type;
	
	public AlignmentQNode(){
		super();
	}

	public AlignmentQNode(ArrayList<SequenceQNode> sequences){
		super();
		setSequences(sequences);
	}

	public ArrayList<SequenceQNode> getSequences() {
		return sequences;
	}

	public void setSequences(ArrayList<SequenceQNode> sequences) {
		this.sequences = sequences;
	}
	
	/**
	 * @return length of longest sequence in alignment
	 */
	public int getLength(){
		int l = -1;
		if(getSequences() !=  null){
			for (SequenceQNode s : getSequences()) {
				if(s.getSequence().length() > l) l = s.getSequence().length();
			}
		}
		return l;
	}

	public String[] getSequenceNames() {
		String[] result = new String[sequences.size()];
		for(int i=0; i<sequences.size(); i++){
			result[i] = sequences.get(i).getName();
		}
		return result;
	}


	public String[] getSequenceStrings() {
		String[] seqs = new String[getSequences().size()];
		for (int i = 0; i < seqs.length; i++) {
			seqs[i] = getSequences().get(i).getSequence();
		}
		return seqs;
	}
	
	public Type getType(){
		if(type == null){
			for (SequenceQNode n : getSequences()) {
				if(type == null) type = n.getType();
				else if(type != n.getType()){
					throw new RuntimeException("Alingment with multiple sequence types can not be handled!");
				}
			}
		}
		return type;		
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	

}
