// SimpleAlignment.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.alignment;

import pal.io.*;
import pal.datatype.*;
import pal.misc.*;

import java.io.*;


/**
 * An alignment class that can be efficiently constructed 
 * from an array of strings.
 *
 * @version $Id: SimpleAlignment.java,v 1.11 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Alexei Drummond
 */
public class SimpleAlignment extends AbstractAlignment
{
	//
	// Public stuff
	//

	/** The sequences */
	String[] sequences;

	/**
	 * parameterless constructor.
	 */
	public SimpleAlignment() {}


	/**
	 * Clone constructor.
	 */
	public SimpleAlignment(Alignment a) {
		
		String[] sequences = new String[a.getIdCount()];
		for (int i = 0; i < sequences.length; i++) {
			sequences[i] = a.getAlignedSequenceString(i);
		}
		String gaps = a.GAP + "";

		init(new SimpleIdGroup(a), sequences, gaps);
	}

	public SimpleAlignment(Identifier[] ids, String[] sequences,  
				   String gaps) {
		
		init(new SimpleIdGroup(ids), sequences, gaps);	
	}

	public SimpleAlignment(IdGroup group, String[] sequences) {
	
		init(group, sequences, null);
	}

	public SimpleAlignment(IdGroup group, String[] sequences,  
				   String gaps) {
		
		init(group, sequences, gaps);
	}

	private void init(IdGroup group, String[] sequences, String gaps) {
		
		numSeqs = sequences.length;
		numSites = sequences[0].length();

		this.sequences = sequences;
		idGroup = group;

		if (gaps != null) {
			convertGaps(gaps);
		}
		
		AlignmentUtils.estimateFrequencies(this);	
	}

	/** 
	 * Constructor taking single identifier and sequence.
	 */
	public SimpleAlignment(Identifier id, String sequence, DataType dataType_) {
		this(id, sequence, dataType_ ,true);
	}

	/** 
	 * Constructor taking single identifier and sequence.
	 */
	public SimpleAlignment(Identifier id, String sequence, DataType dataType_ ,boolean estFreqs) {

		this.dataType = dataType_;
		numSeqs = 1;
		numSites = sequence.length();

		sequences = new String[1];
		sequences[0] = sequence;

		Identifier[] ids = new Identifier[1];
		ids[0] = id;
		idGroup = new SimpleIdGroup(ids);

		if (estFreqs) {
			AlignmentUtils.estimateFrequencies(this);
		}
	}

	/**
	 * This constructor combines to alignments given two guide strings.
	 */
	public SimpleAlignment(Alignment a, Alignment b, 
		String guide1, String guide2, char gap) {

		sequences = new String[a.getSequenceCount() + b.getSequenceCount()];	
		numSeqs = sequences.length;
		
		for (int i = 0; i < a.getSequenceCount(); i++) {
	    		sequences[i] = getAlignedString(a.getAlignedSequenceString(i), guide1, gap, GAP);
	    	}
		for (int i = 0; i < b.getSequenceCount(); i++) {
	    		sequences[i + a.getSequenceCount()] = 
				getAlignedString(b.getAlignedSequenceString(i), guide2, gap, GAP);
		}

		numSites = sequences[0].length();
		idGroup = new SimpleIdGroup(a, b);
		AlignmentUtils.estimateFrequencies(this);
	}

	// Abstract method

	/** sequence alignment at (sequence, site) */
	public char getData(int seq, int site) {
		return sequences[seq].charAt(site);
	}

	/**
	 * Returns a string representing a single sequence (including gaps)
	 * from this alignment.
	 */
	public String getAlignedSequenceString(int seq) {
		return sequences[seq];
	}

	// PRIVATE STUFF

	private String getAlignedString(String original, String guide, 
		char guideChar, char gapChar) {
		
		StringBuffer buf = new StringBuffer(guide.length());
		int seqcounter = 0;
		for (int j = 0; j < guide.length(); j++) {
			if (guide.charAt(j) != guideChar) {
				buf.append(original.charAt(seqcounter));
				seqcounter += 1;
			} else {
				buf.append(gapChar);
			}
		}
		return new String(buf);
	}

	/**
	 * Converts all gap characters to Alignment.GAP
	 */
	private void convertGaps(String gaps) {
		for (int i = 0; i < sequences.length; i++) {
			for (int j = 0; j < gaps.length(); j++) {
				sequences[i] = sequences[i].replace(gaps.charAt(j), Alignment.GAP);
			}
		}
	}
}

