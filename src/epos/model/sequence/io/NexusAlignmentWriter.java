package epos.model.sequence.io;
import org.biojavax.bio.phylo.io.nexus.CharactersBlock;
import org.biojavax.bio.phylo.io.nexus.NexusFile;
import org.biojavax.bio.phylo.io.nexus.NexusFileFormat;

import epos.model.sequence.SequenceQNode;

public class NexusAlignmentWriter extends AbstractAlignmentWriter{

	@Override
	public void write() throws Exception {
		CharactersBlock bb = new CharactersBlock();

		for (int i = 0; i < names.length; i++) {
			bb.addMatrixEntry(names[i]);
			bb.appendMatrixData(names[i], sequences[i]);
			for (int j = 0; j < sequences[i].length(); j++) {
				String s = Character.toString(sequences[i].charAt(j));
				if(SequenceQNode.GAP_CHARS.contains(s)){
					continue;
				}
				bb.addSymbol(s);
			}
		}
		
		bb.setDataType(type.toString());
		bb.setDimensionsNTax(names.length);
		bb.setDimensionsNChar(sequences[0].length());
		bb.setGap("-");
				
		NexusFile nf = new NexusFile();
		nf.addObject(bb);		
		NexusFileFormat.writeFile(file, nf);
	}
	
	@Override
	public String toString() {
		return "Nexus File";
	}

}
