package epos.model.sequence.io;

import iubio.bioseq.Bioseq;
import iubio.readseq.BasicBioseqDoc;
import iubio.readseq.BioseqFormats;
import iubio.readseq.BioseqRecord;
import iubio.readseq.BioseqWriterIface;

import java.io.FileWriter;


public abstract class ReadSeqWriter extends AbstractAlignmentWriter{

	public abstract String getFormat();

	@Override
	public void write() throws Exception {
	     BioseqWriterIface writer= BioseqFormats.newWriter(BioseqFormats.formatFromName(getFormat()));
	     writer.setOutput( new FileWriter(file)) ;
	     writer.writeHeader();
	     for (int i = 0; i < names.length; i++) {
			 // your data goes here
		     Bioseq seq = new Bioseq(sequences[i]);		     
		     BasicBioseqDoc seqdoc= new BasicBioseqDoc(names[i]);
		     BioseqRecord  seqrec= new BioseqRecord( seq, seqdoc);		   
		       // now write to a file
		     if (writer.setSeq( seqrec)) writer.writeSeqRecord();
		     else throw new RuntimeException("Error while writing sequence");		
	     }
	     writer.writeTrailer();
	     writer.close();		
	}
	
	public String toString(){
		return  getFormat();
	}
	
	public static class PhylipWriter extends ReadSeqWriter{
		@Override
		public String getFormat() {
			return "Phylip";
		}
	}

	
	public static class GenBankWriter extends ReadSeqWriter{
		@Override
		public String getFormat() {
			return "GenBank";
		}
	}
	
	public static class EMBLWriter extends ReadSeqWriter{
		@Override
		public String getFormat() {
			return "EMBL";
		}
	}
	
//	@ClassExtension("epos.model.sequence@AlignmentWriter")
//	public static class GCGWriter extends ReadSeqWriter{
//		@Override
//		public String getFormat() {
//			return "GCG";
//		}
//	}
	
	public static class MSFWriter extends ReadSeqWriter{
		@Override
		public String getFormat() {
			return "MSF";
		}
	}
	
//	@ClassExtension("epos.model.sequence@AlignmentWriter")
//	public static class ASNWriter extends ReadSeqWriter{
//		@Override
//		public String getFormat() {
//			return "ASN.1";
//		}
//	}
	
//	@ClassExtension("epos.model.sequence@AlignmentWriter")
//	public static class GFFWriter extends ReadSeqWriter{
//		@Override
//		public String getFormat() {
//			return "GFF";
//		}
//	}
}
