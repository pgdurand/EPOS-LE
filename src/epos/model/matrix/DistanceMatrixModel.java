package epos.model.matrix;

import java.io.Serializable;

import epos.model.sequence.SequenceQNode;

public class DistanceMatrixModel implements Serializable {
	/**
	 * Array storing the dissimilarity (<i>D(x,y)</i>) or distance values (<i>d(x,y)</i>)
	 * of the matrix.
	 * 
	 * @since 1.0 rc1
	 */
	protected double[][] distances;

	protected String[] names;
	

	public DistanceMatrixModel(double[][] distances, String[] names) {
		this.distances = distances;
		this.names = names;
	}

	public double[][] getDistancesPercent() {

		double[][] dist = new double[distances.length][distances[0].length];

		for (int i = 0; i < distances.length; ++i)
			for (int j = 0; j < distances[i].length; ++j)
				dist[i][j] = distances[i][j] * 100d;

		return dist;
	}

	public String[] getNames() {
		return names;
	}

	protected boolean protein = false;
	protected int correction = 0;
	protected double baseB = -1d; // Tajima-Nei factor for complete layout
	public static final int CORR_KIM = 1;
	public static final int CORR_JC = 2;
	public static final int CORR_F81 = 4;
	public static final int CORR_F84 = 8;

	public static final int CORR_FREQ = 16;
	public static final int DISS_STRICT = 32;
	public static final int EXCLUDE_INT_PW = 64;
	public static final int EXCLUDE_TERM = 128;
	public static final int EXCLUDE_INT_CD = 256;
	public static final int DEFAULT_SETTING = DISS_STRICT | CORR_KIM
			| EXCLUDE_INT_CD | EXCLUDE_TERM;

	public static boolean IS_EXCLUDE_INT_CD(int query) {
		return ((query & EXCLUDE_INT_CD) == EXCLUDE_INT_CD);
	}

	public static boolean IS_EXCLUDE_TERM(int query) {
		return ((query & EXCLUDE_TERM) == EXCLUDE_TERM);
	}

	public static boolean IS_EXCLUDE_INT_PW(int query) {
		return ((query & EXCLUDE_INT_PW) == EXCLUDE_INT_PW);
	}

	public static boolean IS_DISS_STRICT(int query) {
		return ((query & DISS_STRICT) == DISS_STRICT);
	}

	public static boolean IS_CORR(int query) {
		return ((query & 15) != 0);
	}

	public static boolean IS_CORR_KIM(int query) {
		return ((query & CORR_KIM) == CORR_KIM);
	}

	public static boolean IS_CORR_JC(int query) {
		return ((query & CORR_JC) == CORR_JC);
	}

	public static boolean IS_CORR_F81(int query) {
		return ((query & CORR_F81) == CORR_F81);
	}

	public static boolean IS_CORR_F84(int query) {
		return ((query & CORR_F84) == CORR_F84);
	}

	public static boolean IS_CORR_FREQ(int query) {
		return ((query & CORR_FREQ) == CORR_FREQ);
	}

	/**
	 * [Hillis, p.453] suggests to differentially treat cysteine.
	 */
	public static final String AA_POLAR = SequenceQNode.AA_HYDROXIC
			+ SequenceQNode.AA_AMIDIC + "M";

	/**
	 * [Hillis, p.453] suggests to differentially treat cysteine.
	 */
	public static final String AA_CYSTEINE = "C";
	public static final String A_WILDCARD = "*";

	/**
	 * (see CLUSTAL W, DAYHOFF.H)
	 * 
	 * Table of estimated PAMS (actual no. of substitutions per 100 residues)
	 * for a range of observed amino acid distances from 75.0% (the first entry
	 * in the array), in 0.1% increments, up to 93.0%.<br>
	 * <br>
	 * These values are used to correct for multiple hits in protein alignments.
	 * The values below are for observed distances above 74.9%. For values above
	 * 93%, an arbitrary value of 1000 PAMS (1000% substitution) is used.<br>
	 * <br>
	 * These values are derived from a Dayhoff model (1978) of amino acid
	 * substitution and assume average amino acid composition and that amino
	 * acids replace each other at the same rate as in the original Dayhoff
	 * model.<br>
	 * <br>
	 * Up to 75% observed distance, use Kimura's emprical formula to derive the
	 * correction. For 75% or greater, use this table. Kimura's formula is
	 * accurate up to about 75% and fails completely above 85%.<br>
	 */

	public static int[] DAYHOFF_PAMS = {
			195, // 75.0% observed d; 195 PAMs estimated = 195% estimated d
			196, // 75.1% observed d; 196 PAMs estimated
			197, 198, 199, 200, 200, 201, 202, 203, 204, 205, 206, 207, 208,
			209, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220,
			221, 222, 223, 224, 226, 227, 228, 229, 230, 231, 232, 233, 234,
			236, 237, 238, 239,
			240,
			241,
			243,
			244,
			245,
			246,
			248,
			249,
			250, // 250 PAMs = 80.3% observed d
			252, 253, 254, 255, 257, 258, 260, 261, 262, 264, 265, 267, 268,
			270, 271, 273, 274, 276, 277, 279, 281, 282, 284, 285, 287, 289,
			291, 292, 294, 296, 298, 299, 301, 303, 305, 307, 309, 311, 313,
			315, 317, 319, 321, 323, 325, 328, 330, 332, 335, 337, 339, 342,
			344, 347, 349, 352, 354, 357, 360, 362, 365, 368, 371, 374, 377,
			380, 383, 386, 389, 393, 396, 399, 403, 407, 410, 414, 418, 422,
			426, 430, 434, 438, 442, 447, 451, 456, 461, 466, 471, 476, 482,
			487, 493, 498, 504, 511, 517, 524, 531, 538, 545, 553, 560, 569,
			577, 586, 595, 605, 615, 626, 637, 649, 661, 675, 688, 703, 719,
			736, 754, 775, 796, 819, 845, 874, 907, 945,
			// 92.9% observed; 945 PAMs
			988 // 93.0% observed; 988 PAMs
	};

	/**
	 * The weight of the gap positions for calculating the effective length and
	 * the number of mismatching positions.
	 * 
	 * @since 1.0 rc1
	 */
	protected float omega;

	/**
	 * The correction factor for turning fractional dissimilarities (<i>D(x,y)</i>)
	 * into evolutionary distances (<i>d(x,y)</i>). <br>
	 * The factor is depending on frequencies of the nucleotides in the layout.
	 * 
	 * @since 1.0 rc1
	 */
	protected double b;


	/**
	 * The multiple alignment layout on which a distance table is upset.
	 * 
	 * @since 1.0 rc1
	 */
	protected java.lang.String[] layout;


	public DistanceMatrixModel(String[] sequenceList, String[] names, float newOmega, boolean newProtein, int newCorrection) {
		this.names = names;
		this.layout = sequenceList;// new String[sequenceList.length];
		this.protein = newProtein;
		this.correction = newCorrection; // do not filter high values
		this.omega = newOmega;
		if (!IS_EXCLUDE_INT_PW(correction) && !IS_EXCLUDE_INT_CD(correction))
			this.omega = 1f;
	}


	public void init() {

		// delete internals FIRST (otherwise it will skip terminal gaps)
		// if (IS_EXCLUDE_INT_CD(correction))
		// this.layout= qnodes.stripInternalGaps(this.layout);
		// this.layout=
		// sequenceList.getSequences()//qnodes.stripInternalGaps(this.layout);

		// if (IS_EXCLUDE_TERM(correction))

		// this.layout= MultipleAlignmentModel.stripTerminalGaps(this.layout);
//		if (layout == null && sequenceList != null) {
//			this.layout = new String[sequenceList.size()];
//			this.layout = sequenceList.getSequences(
//					IS_EXCLUDE_INT_CD(correction), IS_EXCLUDE_TERM(correction),
//					sequenceList.getLongestSequenceLength());
//		}
		// this.distances= computeDistances(layout);
	}

	/**
	 * Computes the correction factor for turning fractional dissimilarities (<i>D(x,y)</i>)
	 * into evolutionary distances (<i>d(x,y)</i>). <br>
	 * The calculation is based on the frequencies of the nucleotides in the
	 * layout [Tajima and Nei, 1984] and uses the assumptions:
	 * <ul>
	 * <li> The nucleotide base composition is not shifting over time.
	 * <li> The rate of change to each residue type is proportional to the
	 * residue's equilibrium abundance but independent of the identity of the
	 * starting residue.
	 * </ul>
	 * 
	 * @return the correction factor.
	 * @param layout
	 *            the layout for which the correction factor is to be
	 *            calculated.
	 * @since 1.0 rc1
	 */

	public double computeCorrectionFactor(String[] layout) {

		int sumNucleotides = 0; // counts all nucleotides
		int sumA = 0; // counts adenines
		int sumG = 0; // counts guanines
		int sumC = 0; // counts cytosines
		int sumT = 0; // counts thymines
		// --> adapt to ambiguities here !

		for (int i = 0; i < layout.length; ++i) {
			for (int j = 0; j < layout[i].length(); ++j) {
				if (layout[i].charAt(j) != '-') {
					++sumNucleotides; // count overall characters
					switch (layout[i].charAt(j)) {
					case 'A': { // count occurrence of each nucleotide
						++sumA; // --> adapt to ambiguities here !
						break;
					}
					case 'G': {
						++sumG;
						break;
					}
					case 'C': {
						++sumG;
						break;
					}
					case 'T': {
						++sumG;
						break;
					}
					}
				}
			}
		}

		double sumSqrFrequencies = 0d; // the sum of squares of all nucleotides
		sumSqrFrequencies += Math.pow(
				(double) ((double) sumA / (double) sumNucleotides), 2d);
		sumSqrFrequencies += Math.pow(
				(double) ((double) sumG / (double) sumNucleotides), 2d);
		sumSqrFrequencies += Math.pow(
				(double) ((double) sumC / (double) sumNucleotides), 2d);
		sumSqrFrequencies += Math.pow(
				(double) ((double) sumT / (double) sumNucleotides), 2d);

		double newB = 1d - sumSqrFrequencies; // the correction factor

		return newB;
	}

	/**
	 * Fills the distance matrix by calling
	 * <code>computeEvolutionaryDistance</code> for each pair of sequences.
	 * 
	 * @param newLayout
	 *            the layout for which the distance table is to be calculated.
	 * @return the distance matrix (percentage values).
	 * @see #computeEvolutionaryDistance
	 * @since 1.0 rc1
	 */
	public double[][] computeDistances(String[] newLayout) {

		double[][] tempDS = new double[newLayout.length][newLayout.length];
		for (int i = 0; i < newLayout.length; ++i)
			for (int j = (i + 1); j < newLayout.length; ++j)
				switch (correction & 15) {

				case 0:
					tempDS[i][j] = tempDS[j][i] = computeFractionalDissimilarity(
							newLayout[i], newLayout[j], false);
					continue;
				case CORR_KIM:
					tempDS[i][j] = tempDS[j][i] = computeKimura(newLayout[i],
							newLayout[j]);
					continue;
				case CORR_JC:
					tempDS[i][j] = tempDS[j][i] = computeJC(newLayout[i],
							newLayout[j]);
					continue;
				case CORR_F81:
					tempDS[i][j] = tempDS[j][i] = computeF81(newLayout[i],
							newLayout[j]);
					continue;
				case CORR_F84:
					tempDS[i][j] = tempDS[j][i] = computeF84(newLayout[i],
							newLayout[j]);
					continue;
				}

		// 4) (last hope): set to twice the maximum,
		// if observed distance D exceeds maximum of evolutionary model
		if (IS_CORR(correction)) {

			// find maximum
			int a = 0, b = 0;
			double max = 0d;
			for (int i = 0; i < tempDS.length; ++i)
				for (int j = 0; j < i; ++j)
					if (!Double.isNaN(tempDS[i][j])
							&& !Double.isInfinite(tempDS[i][j])
							&& tempDS[i][j] > max) {
						max = tempDS[i][j];
						a = i;
						b = j;
					}
			double maxD = computeFractionalDissimilarity(newLayout[a],
					newLayout[b], false);

			// find failing corrections
			for (int i = 0; i < tempDS.length; ++i)
				for (int j = 0; j < i; ++j)
					if ((tempDS[i][j] < 0d) || Double.isNaN(tempDS[i][j])
							|| Double.isInfinite(tempDS[i][j]))
						switch (correction & 15) {

						case CORR_KIM:
							tempDS[i][j] = tempDS[j][i] = 2 * max;
							continue;
						case CORR_JC:
							tempDS[i][j] = tempDS[j][i] = correctF81_JC(getB(),
									maxD);
							if ((tempDS[i][j] < 0d)
									|| Double.isNaN(tempDS[i][j])
									|| Double.isInfinite(tempDS[i][j]))
								tempDS[i][j] = tempDS[j][i] = 2 * max;
							continue;
						case CORR_F81:
							tempDS[i][j] = tempDS[j][i] = correctF81_JC(getB(
									newLayout[a], newLayout[b]), maxD);
							if ((tempDS[i][j] < 0d)
									|| Double.isNaN(tempDS[i][j])
									|| Double.isInfinite(tempDS[i][j]))
								tempDS[i][j] = tempDS[j][i] = 2 * max;
							continue;
						case CORR_F84:
							tempDS[i][j] = tempDS[j][i] = 2 * max;
							continue;
						}
		} // end if

		return tempDS;
	}

	/**
	 * Computes correction for Felsenstein [Felsenstein, 1984] model (see
	 * [Hillis, p456]).
	 */
	public double computeF84(String seq1, String seq2) {

		String[] seqBase = new String[2];
		seqBase[0] = seq1;
		seqBase[1] = seq2;
		int[] res = countResidues(seqBase);

		// the residues regarded in the comparison
		double N = getN(seqBase, res);

		// determine frequencies
		double[] freqs = new double[res.length];
		for (int i = 0; i < freqs.length; ++i)
			freqs[i] = (double) res[i] / (double) N;

		char tuSwitch = 'T';
		if (freqs[SequenceQNode.NA_CHARS.indexOf("T")] == 0d)
			tuSwitch = 'U';

		double yFreq = SequenceQNode.NA_CHARS.indexOf('C')
				+ SequenceQNode.NA_CHARS.indexOf('T');
		double rFreq = SequenceQNode.NA_CHARS.indexOf('A')
				+ SequenceQNode.NA_CHARS.indexOf('G');
		double cMtFreq = SequenceQNode.NA_CHARS.indexOf('C')
				* SequenceQNode.NA_CHARS.indexOf(tuSwitch);
		double aMgFreq = SequenceQNode.NA_CHARS.indexOf('A')
				* SequenceQNode.NA_CHARS.indexOf('G');
		double A = cMtFreq / yFreq + aMgFreq / rFreq;
		double B = cMtFreq + aMgFreq;
		double C = rFreq * yFreq;

		// get transitions, transversions
		double[] t = computeTrans(seq1, seq2);
		double P = t[0];
		double Q = t[1];

		// perform F84
		double ln1 = Math.log(1d - P / (2d * A) - ((A - B) * Q) / (2d * A * C));
		double ln2 = Math.log(1d - Q / (2d * C));
		double d = (-2d) * A * ln1 + 2d * (A - B - C) * ln2;

		return d;
	}

	protected double getN(String[] seqs, int[] res) {

		int N = 0;
		for (int i = 0; i < res.length; ++i)
			N += res[i];

		int gapPositions = 0, tmpGap = 0;
		boolean hasOnlyGap = false;
		for (int i = 0; i < seqs[0].length(); ++i) {

			hasOnlyGap = true;
			tmpGap = 0;

			// look for gap in column
			for (int j = 0; j < seqs.length; ++j) {
				if (SequenceQNode.GAP_CHARS.indexOf(seqs[j].charAt(i)) != (-1)) {
					++tmpGap;
					hasOnlyGap &= true;
				} else
					hasOnlyGap &= false;
			}
			// skip only gap columns anyway
			if (!hasOnlyGap)
				gapPositions += tmpGap; // a gap in one of the seqs
		}

		return ((double) N + (omega * (double) gapPositions));
	}

	/**
	 * Fills the distance matrix by calling
	 * <code>computeKimuraNCorrection<code> for each
	 * pair of sequences. 
	 * 
	 * @param newLayout the layout for which the distance table is
	 * to be calculated.
	 * @return the distance matrix  (percentage values).
	 * @see #computeEvolutionaryDistance
	 * @since 1.0 rc1
	 */
	public double[][] computeKimuraNMatrix(String[] newLayout) {

		int seqNr = newLayout.length; // consensus sequence is already
										// excluded
		double[][] tempDS = new double[seqNr][seqNr];
		for (int i = 0; i < seqNr; ++i) {
			tempDS[i][i] = 0d;
			for (int j = (i + 1); j < seqNr; ++j) {
				tempDS[i][j] = tempDS[j][i] = computeK2P(newLayout[i],
						newLayout[j]); // no offset for consensus
				// computeEvolutionaryDistance(newLayout[i],newLayout[j]); // no
				// offset for consensus
				// System.err.println(tempDS[i][j]);
				if (Double.isNaN(tempDS[i][j])) {
					tempDS[i][j] = tempDS[j][i] = computeDivergence(
							newLayout[i], newLayout[j]); // no offset for
															// consensus
					// System.err.println("-"+tempDS[i][j]);
				}
			}
		}

		return tempDS;
	}

	/**
	 * Naive calculation of dissimilarity of two strings. <br>
	 * The divergence is taken as the ratio mismatches over the aligned length,
	 * without regarding gap characters.
	 * 
	 * @param seq1
	 *            the first aligned sequence.
	 * @param seq2
	 *            the second aligned sequence.
	 * @return the naive divergence of both strings (percentage value).
	 * @see #computeEvolutionaryDistance
	 * @see #computeFractionalDissimilarity
	 * @since 1.0 rc1
	 * @deprecated The method is not used due to more sophisticated
	 *             possibilities to derive distance values of sequences.
	 */

	public double computeDivergence(String seq1, String seq2) {

		// count mismatches
		int sumMismatch = 0;
		for (int i = 0; i < seq1.length(); ++i)
			if (seq1.charAt(i) != seq2.charAt(i))
				++sumMismatch;

		// calculate divergence
		double div = (double) sumMismatch / (double) seq1.length();

		// return percentage values
		return 100d * div;
	}

	/**
	 * Computes the evolutionary distance according to [Tajima and Nei, 1984]
	 * for two strings. <br>
	 * The distance is calculated as a Taylor-Series [Tajima, 1993], because the
	 * logarithm used may have an undefined input. Gap character mismatches are
	 * contributed by the <code>omega</code> weight factor and the correction
	 * factor as calculated by <code>computeCorrectionFactor</code> is used.
	 * 
	 * @param seq1
	 *            the first aligned sequence.
	 * @param seq2
	 *            the second aligned sequence.
	 * @return the evolutionary distance of both strings (percentage value).
	 * @see #computeDivergence
	 * @see #computeFractionalDissimilarity
	 * @since 1.0 rc1
	 */

	public double computeEvolutionaryDistance(String seq1, String seq2) {

		// count all kinds of mismatches
		int sumMatch = 0; // counter synonymous positions (M(x,y))
		int sumMismatch = 0; // counter different positions (U(x,y))
		int gapPositions = 0; // counts positions with gaps in either sequence
								// (G(x,y))
		for (int i = 0; i < seq1.length(); ++i) {
			if ((seq1.charAt(i) == '-') || (seq2.charAt(i) == '-'))
				++gapPositions; // a gap in one of the seqs
			else if (seq1.charAt(i) != seq2.charAt(i))
				++sumMismatch; // mismatch
			else
				++sumMatch; // or match
		}

		// compute the effective length
		double effLength = (double) ((double) sumMatch + (double) sumMismatch + (omega * (double) gapPositions));

		// compute the evolutionary distance
		double dhat = 0;
		for (int i = 1; i <= sumMismatch; ++i)
			dhat += (double) ((double) (Math.pow((double) sumMismatch
					+ (double) omega * (double) gapPositions, (double) i)) / (double) ((double) i
					* Math.pow(b, (double) (i - 1)) * Math.pow(effLength,
					(double) i)));

		// return percentage values
		return dhat * 100;

	}

	/**
	 * Computes the evolutionary distance according to [Kimura, 1980] for two
	 * strings. <br>
	 * The transitions (type I substitutions) and transversions (type II
	 * substitutions) are counted and the according probabilities P (transition)
	 * and Q (transversion) are calculated respectively: P= n1 / n (n1, n2=
	 * residues with observed transitions, transversions) Q= n2 / n (n= total
	 * number of sites compared w/o residues containing a gap in any sequence)
	 * 
	 * The evolutionary distance K then is estimated by: K= 0.5*ln(a)+
	 * 0.25*ln(b) with a= 1/ (1- 2*P- Q) b= 1/ (1- 2*Q)
	 * 
	 * @param seq1
	 *            the first aligned sequence.
	 * @param seq2
	 *            the second aligned sequence.
	 * @return Kimura's evolutionary distance of both strings (percentage
	 *         value).
	 * @see #computeKimuraDivergence
	 * @since 1.0 rc1
	 */

	public double computeK2P(String seq1, String seq2) {

		double[] t = computeTrans(seq1, seq2);
		double P = t[0];
		double Q = t[1];

		// compute the evolutionary distance
		double a = 1d / (1d - 2 * P - Q);
		double b = 1d / (1d - 2 * Q);
		// System.out.println(P+", "+Q+" : "+a+","+b);

		// fail correction control
		double K = 0.5d * Math.log(a) + 0.25d * Math.log(b);
		return K;
	}

	/**
	 * Only available pairwise [Hillis, p.456].
	 */
	public double[] computeTrans(String seq1, String seq2) {

		// count all kinds of mismatches
		int sumMatch = 0; // counter synonymous positions (M(x,y))
		int n1 = 0; // counter different positions (type I, transition)
		int n2 = 0; // counter different positions (type II, transversion)
		int gapPositions = 0; // counts positions with gaps in either sequence
								// (G(x,y))

		for (int i = 0; i < seq1.length(); ++i) {
			if ((SequenceQNode.GAP_CHARS.indexOf(seq1.charAt(i)) != (-1))
					&& (SequenceQNode.GAP_CHARS.indexOf(seq2.charAt(i)) != (-1)))
				continue; // gaps in both, skip
			else if ((SequenceQNode.GAP_CHARS.indexOf(seq1.charAt(i)) != (-1))
					|| (SequenceQNode.GAP_CHARS.indexOf(seq2.charAt(i)) != (-1)))
				++gapPositions; // a gap in one of the seqs
			else if (compare(seq1.charAt(i), seq2.charAt(i)))
				++sumMatch; // or match
			else if (isTransition(seq1.charAt(i), seq2.charAt(i)))
				++n1; // type I substituition, transition
			else if (isTransversion(seq1.charAt(i), seq2.charAt(i))) // need
																		// check
																		// (ambiguities
																		// !)
				++n2; // type II substitution, transversion
		}

		// compute probabilities of substitutions
		double n = (double) (sumMatch + n1 + n2) + (omega * gapPositions); // effective
																			// length
																			// (residues
																			// watched)
		double P = ((double) n1) / n; // type I, transition
		double Q = ((double) n2) / n; // type II, transversion
		// System.out.println(n1+","+n2+","+n+": "+P+","+Q);

		double[] res = new double[2];
		res[0] = P;
		res[1] = Q;
		return res;
	}

	public double computeKimura(String seq1, String seq2) {

		if (isProtein())
			return computeK83(computeFractionalDissimilarity(seq1, seq2, false));

		else
			return computeK2P(seq1, seq2);
	}

	/**
	 * Gets observed distance <code>D</code> and calls for correction.
	 * 
	 * @param seq1
	 *            the first aligned sequence.
	 * @param seq2
	 *            the second aligned sequence.
	 * @param B
	 *            frequency base (proportional or poisson model)
	 * @return Felsenstein's/Jukes-Cantor's evolutionary distance of both
	 *         strings (percentage value).
	 */

	public double computeF81_JC(String seq1, String seq2, double B) {

		// the uncorrected dissimilarity
		double D = computeFractionalDissimilarity(seq1, seq2, false);

		// undefined values [Hillis, p.458]
		// 1) skipped: suggested exclusion of the troublesome sequences
		if (!isProtein() && (D >= B)) // 2) try to exclude transitions from
										// distance computation
			D = computeFractionalDissimilarity(seq1, seq2, true);
		// 3) skippped: suggested max. likelihood distance
		// with high transition:transversion ratio
		// 4) take twice the maximum observed distance delegated for lateron
		// see computeDistances()

		return correctF81_JC(B, D);
	}

	/**
	 * Computes the evolutionary distance according to [Felsenstein, 1981] for
	 * two strings. However, if setting the freqBase to <code>3/4</code> (for
	 * nucleotides) or <code>19/20</code> (proteins), the Jukes-Cantor model
	 * can be computed respectively. <br>
	 * d= -B * ln(1-D/B) where D= observed distance B= poisson model
	 * [Jukes-Cantor] or proportional model [Tajima and Nei, 1982]
	 * 
	 * @param seq1
	 *            the first aligned sequence.
	 * @param seq2
	 *            the second aligned sequence.
	 * @param B
	 *            frequency base (proportional or poisson model)
	 * @param D
	 *            observed (replaced for non defined states) distance
	 * @return Felsenstein's/Jukes-Cantor's evolutionary distance of both
	 *         strings (percentage value).
	 */
	public double correctF81_JC(double B, double D) {

		// correct
		// System.out.println(D+" "+B+": log("+(1d- D/B)+")= "+Math.log((1d- D)/
		// B)+" * "+((-1d)* B)+" = "+ ((-1d)* B)* Math.log((1d- D)/ B));
		return ((-1d) * B) * Math.log(1d - D / B);
	}

	/**
	 * Computes correction for Jukes-Cantor model (see [Hillis, p422]).
	 */
	public double computeJC(String seq1, String seq2) {

		double B = getB();
		return computeF81_JC(seq1, seq2, B);
	}

	protected double getB() {

		if (isProtein())
			return (19d / 20d);
		else
			return (3d / 4d);
	}

	/**
	 * Computes correction for Felsenstein [Felsenstein, 1981] model (see
	 * [Hillis, p455]).
	 */
	public double computeF81(String seq1, String seq2) {

		double B = getB(seq1, seq2);

		return computeF81_JC(seq1, seq2, B);
	}

	protected double getB(String seq1, String seq2) {

		if (IS_CORR_FREQ(correction)) {
			String[] seqBase = new String[2];
			seqBase[0] = seq1;
			seqBase[1] = seq2;
			return computeTNPropCorrection(seqBase);
		} else
			return getCompleteB();
	}

	protected double getCompleteB() {

		if (baseB < 0)
			baseB = computeTNPropCorrection(layout);

		return baseB;
	}

	public double computeTNPropCorrection(String[] seqBase) {

		int[] res = countResidues(seqBase);

		// the residues regarded in the comparison
		double N = getN(seqBase, res);

		// determine residue frequencies
		double B = 0d, pi = 0d;
		for (int i = 0; i < res.length; ++i) {
			pi = (double) res[i] / N;
			B += pi * pi;
		}

		return (1d - B);
	}

	public int[] countResidues(String[] seqBase) {

		String comp = null;
		if (isProtein())
			comp = SequenceQNode.AA_CHARS;
		else
			comp = SequenceQNode.NA_CHARS; // ambiguities not included

		int[] res = new int[comp.length()];
		int[] tmpRes = new int[res.length];
		for (int i = 0; i < seqBase[0].length(); ++i) { // for all columns

			// reset temporary result
			for (int j = 0; j < tmpRes.length; ++j)
				tmpRes[j] = 0;

			int k = 0;
			for (int j = 0; j < seqBase.length; ++j) { // all chars in a column
				for (k = 0; k < comp.length(); ++k)
					if (Character.toUpperCase(seqBase[j].charAt(i)) == comp
							.charAt(k)) {
						++tmpRes[k];
						break;
					}

				if (k == comp.length())
					break;
			}
			// gap char assumed, skip column
			if ((IS_EXCLUDE_INT_PW(correction)) && (k == comp.length()))
				continue;

			// add temp result
			for (int j = 0; j < tmpRes.length; ++j)
				// else
				res[j] += tmpRes[j];
		}

		return res;
	}

	public boolean compare(char c, char d) {

		// force uppercase
		c = Character.toUpperCase(c);
		d = Character.toUpperCase(d);

		// really strict character comparison
		if (IS_DISS_STRICT(correction))
			return (c == d);

		if (isProtein()) // else
			// report whether both amino acids share a common group
			return (((SequenceQNode.AA_ACIC.indexOf(c) != (-1)) && (SequenceQNode.AA_ACIC
					.indexOf(d) != (-1)))
					|| ((SequenceQNode.AA_AROMATIC.indexOf(c) != (-1)) && (SequenceQNode.AA_AROMATIC
							.indexOf(d) != (-1)))
					|| ((SequenceQNode.AA_BASIC.indexOf(c) != (-1)) && (SequenceQNode.AA_BASIC
							.indexOf(d) != (-1)))
					|| ((SequenceQNode.AA_ALIPHATIC.indexOf(c) != (-1)) && (SequenceQNode.AA_ALIPHATIC
							.indexOf(d) != (-1)))
					|| ((AA_CYSTEINE.indexOf(c) != (-1)) && (AA_CYSTEINE
							.indexOf(d) != (-1))) || ((AA_POLAR.indexOf(c) != (-1)) && (AA_POLAR
					.indexOf(d) != (-1))));

		// else nucleotide ambiguity comparison
		// (relaxed, ambiguity sets are checked for at least one common member)
		String cs = SequenceQNode.NA_IUPAC[c - 'A'];
		String ds = SequenceQNode.NA_IUPAC[d - 'A'];
		for (int i = 0; i < cs.length(); ++i)
			for (int j = 0; j < ds.length(); ++j)
				if (cs.charAt(i) == ds.charAt(j))
					return true;
		return false;
	}

	/**
	 * Kimura's correction for protein sequences. (1983, "The neutral Theory of
	 * Molecular Evolution", Camb. Univ. Press).
	 * 
	 * in clustalV:
	 * 
	 * K= -ln(1.0 - K - (K* k/5.0))
	 * 
	 * ...later (clustalW):
	 * 
	 * K= -ln(1 - D - (D.D)/5)
	 * 
	 * ... I guess they mean:
	 * 
	 * K= -ln(1- D - (D*D)/5)
	 * 
	 * @param observed
	 *            distance [in percent]
	 * @return corrected distance [in percent]
	 */
	public double computeK83(double D) {

		// for values above 93%, an arbitrary value of 1000PAMs (1000%
		// substitution) is set
		if (D > 0.93d) {
			// System.out.println("*1* "+D+"->"+1000d);
			return 10d;
		}

		// for values [75.0% .. 93.0%] set value from table of estimated PAMs
		if (D >= 0.75d) {
			// System.out.println("* "+D+"->"+((D- 75d)*
			// 10d)+"->"+Math.round((float) ((D- 75d)*
			// 10d))+"->"+DAYHOFF_PAMS[Math.round((float) ((D- 75d)* 10d))]);
			D = (D - 0.75d) * 1000d;
			return (DAYHOFF_PAMS[Math.round((float) D)] / 100d);
		}

		// rest: compute Kimura
		// System.out.println("*3*
		// "+D+"->"+(D*D)+"->"+(D*D/5d)+"->"+Math.log(1d- D- D*D/5d));
		return (-1d) * Math.log(1d - D - D * D / 5d);
	}

	/**
	 * Determines whether a->b substitution is a transition. Only exact
	 * transitions, no ambiguities (frequencies in formula).
	 */
	public static boolean isTransition(char a, char b) {

		a = Character.toUpperCase(a);
		b = Character.toUpperCase(b);
		if (a == b)
			return false;
		boolean transition = (((SequenceQNode.NA_PURINES.indexOf(a) != (-1)) && (SequenceQNode.NA_PURINES
				.indexOf(b) != (-1))) || ((SequenceQNode.NA_PYRIMIDINES
				.indexOf(a) != (-1)) && (SequenceQNode.NA_PYRIMIDINES
				.indexOf(b) != (-1))));
		return transition;
	}

	/**
	 * Determines whether a->b substitution is a transversion. Only exact
	 * transversions, no ambiguities (frequencies in formula).
	 */
	public static boolean isTransversion(char a, char b) {

		a = Character.toUpperCase(a);
		b = Character.toUpperCase(b);
		if (a == b)
			return false;
		boolean transversion = (((SequenceQNode.NA_PURINES.indexOf(a) != (-1)) && (SequenceQNode.NA_PYRIMIDINES
				.indexOf(b) != (-1))) || ((SequenceQNode.NA_PYRIMIDINES
				.indexOf(a) != (-1)) && (SequenceQNode.NA_PURINES.indexOf(b) != (-1))));
		return transversion;
	}

	/**
	 * Calculation of the fractional dissimilarity with regard to mismatches
	 * produced by gap insertion. <br>
	 * The mismatches provoked by gaps are weighted by factor <code>omega</code>.
	 * 
	 * @param seq1
	 *            the first aligned sequence.
	 * @param seq2
	 *            the second aligned sequence.
	 * @return the fractional dissimilarity of both strings (percentage value).
	 * @see #computeDivergence
	 * @see #computeEvolutionaryDistance
	 * @since 1.0 rc1
	 * @deprecated The method is not used due to more sophisticated
	 *             possibilities to derive distance values of sequences.
	 */

	public double computeFractionalDissimilarity(String seq1, String seq2,
			boolean justTransversions) {

		// count all kinds of mismatches
		int sumMatch = 0; // counter synonymous positions (M(x,y))
		int sumMismatch = 0; // counter different positions (U(x,y))
		int gapPositions = 0; // counts positions with gaps in either sequence
								// (G(x,y))
		int transversions = 0;
		// String DNA="ACGT";
		for (int i = 0; i < seq1.length(); ++i) {
			if(i >= seq2.length()){
				// assume gap ??
				if((SequenceQNode.GAP_CHARS.indexOf(seq1.charAt(i)) != (-1))){
					// gap in both - seq2 is not long enough -> assume gap anyway
					continue;
				}else{
					// gap assumtion in seq2 not in seq1
					++gapPositions;	
				}
			}else{
				if ((SequenceQNode.GAP_CHARS.indexOf(seq1.charAt(i)) != (-1))
						&& (SequenceQNode.GAP_CHARS.indexOf(seq2.charAt(i)) != (-1)))
					continue; // gaps in both, skip
				else if ((SequenceQNode.GAP_CHARS.indexOf(seq1.charAt(i)) != (-1))
						|| (SequenceQNode.GAP_CHARS.indexOf(seq2.charAt(i)) != (-1)))
					++gapPositions; // a gap in one of the seqs
				else if (compare(seq1.charAt(i), seq2.charAt(i)))
					++sumMatch; // or match
				else {
					++sumMismatch; // mismatch !!! correct for ambiguities !!!
					if (justTransversions
							&& isTransversion(seq1.charAt(i), seq2.charAt(i)))
						++transversions;
				}
			}

			/*
			 * if ( ( (DNA.indexOf(seq1.charAt(i))== (-1)) &&
			 * (MultipleAlignmentModel.getGAP_CHARS().indexOf(seq1.charAt(i))==
			 * (-1)) ) || ( (DNA.indexOf(seq2.charAt(i))== (-1)) &&
			 * (MultipleAlignmentModel.getGAP_CHARS().indexOf(seq2.charAt(i))==
			 * (-1))) ) System.err.println(seq1.charAt(i)+","+seq2.charAt(i));
			 */
		}

		// System.out.println(seq1.length()+","+seq2.length()+":"+sumMatch+"+"+sumMismatch+"+"+gapPositions);
		// compute the effective length (of the aligned strings)
		// System.out.println(omega);
		double effLength = (double) ((double) sumMatch + (double) sumMismatch + (omega * (double) gapPositions));

		// compute the fractional similarity
		// double fractSim= (double) ((double) sumMatch/ effLength);

		// compute the fractional dissimilarity
		double fractDis = 0d;
		if (justTransversions)
			fractDis = ((double) transversions) / effLength;
		else
			fractDis = ((double) sumMismatch + omega * ((double) gapPositions))
					/ effLength;

		return fractDis;
	}

	public double[][] computedFD(String[] seqs) {

		double p, q, e, k;
		double[][] tmat = new double[seqs.length][seqs.length];
		char res1, res2;
		String gaps = SequenceQNode.GAP_CHARS;

		for (int m = 1; m < seqs.length; ++m)
			/* for every pair of sequence */
			for (int n = m + 1; n < seqs.length; ++n) {

				p = q = e = 0.0;
				tmat[m][n] = tmat[n][m] = 0.0;
				for (int i = 1; i < seqs[0].length(); ++i) {

					/*
					 * j = boot_positions[i]; if(tossgaps && (tree_gaps[j] > 0) )
					 * goto skip; /* gap position
					 */

					res1 = seqs[m].charAt(i);
					res2 = seqs[n].charAt(i);

					if ((gaps.indexOf(res1) != (-1))
							|| (gaps.indexOf(res2) != (-1)))
						continue;
					// goto skip; /* gap in a seq*/

					/*
					 * if(!use_ambiguities) if( is_ambiguity(res1) ||
					 * is_ambiguity(res2)) goto skip; /* ambiguity code in a seq
					 */

					e = e + 1.0;
					if (res1 != res2) {
						if (isTransition(res1, res2))
							p = p + 1.0;
						else
							q = q + 1.0;
					}
					skip:
					;
				}

				/* Kimura's 2 parameter correction for multiple substitutions */

				if (true) {
					if (e == 0) {
						// fprintf(stdout,"\n WARNING: sequences %d and %d are
						// non-overlapping\n",m,n);
						k = 0.0;
						p = 0.0;
						q = 0.0;
					} else {
						k = (p + q) / e;
						if (p > 0.0)
							p = p / e;
						else
							p = 0.0;
						if (q > 0.0)
							q = q / e;
						else
							q = 0.0;
					}
					tmat[m][n] = tmat[n][m] = k;
					// if(verbose) /* if screen output */
					// fprintf(tree,
					// "%4d vs.%4d: DIST = %7.4f; p = %6.4f; q = %6.4f; length =
					// %6.0f\n"
					// ,(pint)m,(pint)n,k,p,q,e);
				}
			}

		return tmat;
	}

	/**
	 * Returns the distance matrix.
	 * 
	 * @return the distance matrix (percentage values).
	 * @since 1.0 rc1
	 */

	public double[][] getDistances() {
		if (distances == null) {
			init();
			distances = computeDistances(layout);
		}
		return distances;
	}

	/**
	 * Returns the correction.
	 * 
	 * @return boolean
	 */
	public int getCorrection() {
		return correction;
	}

	/**
	 * Sets the correction.
	 * 
	 * @param correction
	 *            The correction to set
	 */
	public void setCorrection(int correction) {
		this.correction = correction;
	}

	/**
	 * Returns the protein.
	 * 
	 * @return boolean
	 */
	public boolean isProtein() {
		return protein;
	}

	/**
	 * Sets the protein.
	 * 
	 * @param protein
	 *            The protein to set
	 */
	public void setProtein(boolean protein) {
		this.protein = protein;
	}
}
