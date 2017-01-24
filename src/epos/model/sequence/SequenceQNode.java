package epos.model.sequence;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple seqnece representation.
 * 
 * @author thasso
 * 
 */
public class SequenceQNode {
	public static enum Type {
		DNA, RNA, PROTEIN
	};

	/**
	 * Nucleotide bases of different nature
	 */
	public final static String NA_PURINES = "AG";

	public final static String NA_PYRIMIDINES = "CTU";

	public final static String NA_AMBIGUITIES = "RYSWKMBDHVN";

	// types for typetest
	public final static String NA_DNA = "ACGT";

	public final static String NA_RNA = "ACGU";

	/**
	 * IUPAC Nucleotide Ambiguities
	 */
	public static final String[] NA_IUPAC = { "A", "CGT", "C", "AGT", null,
			null, "G", "ACT", null, null, // A- J
			"GT", null, "AC", "AGCT", null, null, null, "AG", "GC", "T", // K- T
			"U", "ACG", "AT", "AGCT", "CT", null // U- Z
	};

	public final static String NA_CHARS = NA_PURINES + NA_PYRIMIDINES; // add
	// ambiguities
	// ?

	/**
	 * Nucleotide bases of different nature
	 */
	public final static String AA_ALIPHATIC = "GAVLIP";

	public final static String AA_AROMATIC = "FYW";

	public final static String AA_BASIC = "KRH";

	public final static String AA_ACIC = "DE";

	public final static String AA_AMIDIC = "NQ";

	public final static String AA_SULFURIC = "MC";

	public final static String AA_HYDROXIC = "ST";

	public final static String AA_POLAR = AA_AMIDIC + AA_HYDROXIC + AA_SULFURIC;

	public final static String AA_CHARS = AA_ALIPHATIC + AA_AROMATIC + AA_BASIC
			+ AA_ACIC + AA_POLAR;

	// compare to clustal
	public static float DNA_RNA_THRESHOLD = 0.85f;

	public static final String GAP_CHARS = "-~.";
	
	public static Map<String, String> one2ThreeLetterCode = new HashMap<String, String>();
	static{
		one2ThreeLetterCode.put("A", "Ala");
		one2ThreeLetterCode.put("R", "Arg");
		one2ThreeLetterCode.put("N", "Asn");
		one2ThreeLetterCode.put("D", "Asp");
		one2ThreeLetterCode.put("C", "Cys");
		one2ThreeLetterCode.put("Q", "Gln");
		one2ThreeLetterCode.put("E", "Glu");
		one2ThreeLetterCode.put("G", "Gly");
		one2ThreeLetterCode.put("H", "His");
		one2ThreeLetterCode.put("I", "Ile");
		one2ThreeLetterCode.put("L", "Leu");
		one2ThreeLetterCode.put("K", "Lys");
		one2ThreeLetterCode.put("M", "Met");
		one2ThreeLetterCode.put("F", "Phe");
		one2ThreeLetterCode.put("P", "Pro");
		one2ThreeLetterCode.put("S", "Ser");
		one2ThreeLetterCode.put("T", "Thr");
		one2ThreeLetterCode.put("W", "Trp");
		one2ThreeLetterCode.put("Y", "Tyr");
		one2ThreeLetterCode.put("V", "Val");
		one2ThreeLetterCode.put("B", "Asx");
		one2ThreeLetterCode.put("Z", "Glx");		
	}

	private String sequence;
	private String name;
	private Type type = Type.DNA;

	public SequenceQNode() {
		super();
	}

	public SequenceQNode(String s) {
		super();
		setSequence(s);
	}
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public char[] getSequence(boolean stripInternalGaps, int length) {
		char[] seq = null;
		char[] seq_ext;
		if(length <=0 ) length =  getSequence().length();
		if (stripInternalGaps) {
			seq = stripInternalGaps(getSequence().toCharArray());
		} else
			seq = getSequence().toCharArray();
		// should we extend terminal gaps to the end of the array ?
		if (length > seq.length) {
			seq_ext = new char[length];
			System.arraycopy(seq, 0, seq_ext, 0, seq.length);
			Arrays.fill(seq_ext, seq.length, seq_ext.length, GAP_CHARS
					.charAt(0));
			return seq_ext;
		}
		return seq;
	}

	protected char[] stripInternalGaps(char[] input) {
		int gapCount = 0;
		char[] result = new char[input.length];
		for (int i = 0; i < input.length; i++) {
			if (isGap(input[i])) {
				gapCount++;
			} else {
				result[i - gapCount] = input[i];
			}
		}
		char[] ret = new char[input.length - gapCount];
		System.arraycopy(result, 0, ret, 0, ret.length);
		return ret;
	}

	public static boolean isGap(char c) {		
		return GAP_CHARS.contains(Character.toString(c));
	}

}
