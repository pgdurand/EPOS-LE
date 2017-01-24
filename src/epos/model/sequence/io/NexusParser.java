package epos.model.sequence.io;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import org.biojavax.bio.phylo.io.nexus.DataBlock;
import org.biojavax.bio.phylo.io.nexus.NexusFile;
import org.biojavax.bio.phylo.io.nexus.NexusFileBuilder;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;

/**
 * NexusParser is able to parse nexus files (all sequence types) regarding the sequence information.
 * Main difference to the other parsers: * 
 * The sequence length of all sequences given in the alignment must be EQUAL for one input file, 
 * because Nexus Format only supports this standard.
 * The NexusParser now uses Biojava 1.6.
 *  
 * @author m5muth2
 *
 */
public class NexusParser extends AbstractAlignmentReader {
	
	private int sequenceNumber;	
	
	public NexusParser() {		
	}

	public void read() throws Exception {
		
		// Construct a NexusFileBuilder object
		NexusFileBuilder nexFileBuilder = new NexusFileBuilder();
		NexusFileFormat nexFileFormat = null;
		
		// Parse the nexus file and pass it to the nexus file builder
		nexFileFormat.parseFile(nexFileBuilder, file);
		
		// Get the Nexus file from the nexus file builder
		NexusFile nexFile = nexFileBuilder.getNexusFile();
		
		// Construct the blockIterator
		Iterator blockIter = nexFile.blockIterator();
		
		DataBlock datablock = null;
		CharactersBlock charactersblock = null;
		
		// Iterate over the nexus blocks		
		boolean data = false;
		boolean characters = false;
		
		while(blockIter.hasNext()){			
			Object nexusBlock = blockIter.next();
			// Get the DataBlock which contains the aligments
			if (nexusBlock instanceof DataBlock) {
				datablock = (DataBlock) nexusBlock;
				data = true;
			}
			if (nexusBlock instanceof CharactersBlock) {
				charactersblock = (CharactersBlock) nexusBlock;
				characters = true;
			}
			
		}
		
		// Data block
		if(data){
			sequenceNumber = datablock.getDimensionsNTax();		
			
			names = new String[getSequenceNumber()];		
			sequences = new String[getSequenceNumber()];		
			
			// Get the collection of matrix labels, i.e. the sequence/taxa names
			Collection labelsColl = datablock.getMatrixLabels();	
			Iterator labelIter = labelsColl.iterator();		
			
			// Iterate over the label collection + fill name and sequence array
			int i = 0;		
			while(labelIter.hasNext()){
				String label = (String) labelIter.next();
				names[i] = label;
				
				// Get the list of matrix data, i.e. the sequences		
				List dataList = datablock.getMatrixData(label);
				
				StringBuffer sBuffer = new StringBuffer();
				for(int j = 0; j < dataList.size(); j++){
					if(dataList.get(j) != ""){
						sBuffer.append(dataList.get(j));
					}
				}
				String sequence = sBuffer.toString();
				sequences[i] = sequence;
				i++;
			}
		} else if(characters){
				sequenceNumber = charactersblock.getDimensionsNTax();		
				
				names = new String[getSequenceNumber()];		
				sequences = new String[getSequenceNumber()];		
				
				// Get the collection of matrix labels, i.e. the sequence/taxa names
				Collection labelsColl = charactersblock.getMatrixLabels();	
				Iterator labelIter = labelsColl.iterator();		
				
				// Iterate over the label collection + fill name and sequence array
				int i = 0;		
				while(labelIter.hasNext()){
					String label = (String) labelIter.next();					
					names[i] = label;
					
					// Get the list of matrix data, i.e. the sequences		
					List dataList = charactersblock.getMatrixData(label);
					
					StringBuffer sBuffer = new StringBuffer();
					for(int j = 0; j < dataList.size(); j++){
						if(dataList.get(j) != ""){
							sBuffer.append(dataList.get(j));
						}
					}
					String sequence = sBuffer.toString();
					sequences[i] = sequence;
					i++;
				}
			}
		
	}	
	
	public String toString(){
		return "Nexus File";
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}
