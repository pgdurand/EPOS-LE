package epos.model.sequence.io;

import iubio.readseq.BioseqFormats;
import iubio.readseq.BioseqRecord;
import iubio.readseq.Readseq;

import java.io.FileReader;
import java.util.ArrayList;

public abstract class ReadSeqReader extends AbstractAlignmentReader{
	
	public abstract String getFormat();
	
	@Override
	public void read() throws Exception {	
		Object inputObject= new FileReader(file);
	    Readseq rd= new Readseq(BioseqFormats.formatFromName(getFormat()));
	    String seqname= rd.setInputObject( inputObject );
	    ArrayList<String> names = new ArrayList<String>();
	    ArrayList<String> seqs = new ArrayList<String>();

	    if ( rd.isKnownFormat() && rd.readInit() )  {
	      while (rd.readNext()) {
	    	  BioseqRecord seqrec= new BioseqRecord(rd.nextSeq());
	    	  if(seqrec.getseq() == null){
	    		  //System.err.println("No sequence found for " + seqrec.getID());
	    		  // This solve a problem with the phylip importer
	    		  // the importer continues to read empty sequences
	    		  break;
	    	  }else{
	    		  seqs.add(seqrec.getseq().toString());
	    	  }
	    	  names.add(seqrec.getID());

	      }
	    }
	    rd.close();
	    
	    this.sequences = new String[seqs.size()];
	    this.names = new String[seqs.size()];
	    this.sequences = seqs.toArray(this.sequences);
	    this.names = names.toArray(this.names);
	}
	
	@Override
	public String toString() {	
		return getFormat();
	}
	
	
	public static class PhylipReader extends ReadSeqReader{
		@Override
		public String getFormat() {
			return "Phylip";
		}
	}

	
	public static class GenBankReader extends ReadSeqReader{
		@Override
		public String getFormat() {
			return "GenBank";
		}
	}
	
	public static class EMBLReader extends ReadSeqReader{
		@Override
		public String getFormat() {
			return "EMBL";
		}
	}
	
	public static class NexusReader extends ReadSeqReader{
		@Override
		public String getFormat() {
			return "NEXUS";
		}
	}

	
//	@ClassExtension("epos.model.sequence@AlignmentReader")
//	public static class GCGReader extends ReadSeqReader{
//		@Override
//		public String getFormat() {
//			return "GCG";
//		}
//	}
		
//	@ClassExtension("epos.model.sequence@AlignmentReader")
//	public static class ASNReader extends ReadSeqReader{
//		@Override
//		public String getFormat() {
//			return "ASN.1";
//		}
//	}
	
//	@ClassExtension("epos.model.sequence@AlignmentReader")
//	public static class GFFReader extends ReadSeqReader{
//		@Override
//		public String getFormat() {
//			return "GFF";
//		}
//	}


	public static class FastaReader extends ReadSeqReader{
		@Override
		public String getFormat() {
			return "Fasta";
		}
	}

}
