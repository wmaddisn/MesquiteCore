// IUPACNucleotides.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.datatype;


/**
 * implements DataType for nucleotides with ambiguous characters
 *
 * @version $Id: IUPACNucleotides.java,v 1.6 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Alexei Drummond
 */
public class IUPACNucleotides implements DataType
{
	/**
	 * Get number of states.
	 */
	public int getNumStates()
	{
		return 15;
	}

	/**
	 * @retrun true if this state is an unknown state
	 */
	public boolean isUnknownState(int state) {
		return(state>=getNumStates());
	}

	/**
	 * returns the number of true non-ambiguous states.
	 */
	public int getNumSimpleStates() {
		return 4;
	}


	// Get state corresponding to character c
	public final int getState(char c) {
	
		switch (c) {
			case 'A': return  0;
			case 'C': return  1;
			case 'G': return  2;
			case 'T': return  3;
			case 'U': return  3;
			case 'K': return  4;
			case 'M': return  5;
			case 'R': return  6;
			case 'S': return  7;
			case 'W': return  8;
			case 'Y': return  9;
			case 'B': return 10;
			case 'D': return 11;
			case 'H': return 12;
			case 'V': return 13;
			case 'N': return 14;
			default: return  15;
		}
	}

	// Get character corresponding to a given state
	public final char getChar(int c) {
		
		switch (c) {
			case  0: return 'A';
			case  1: return 'C';
			case  2: return 'G';
			case  3: return 'U';
			case  4: return 'K';
			case  5: return 'M';
			case  6: return 'R';
			case  7: return 'S';
			case  8: return 'W';
			case  9: return 'Y';
			case 10: return 'B';
			case 11: return 'D';
			case 12: return 'H';
			case 13: return 'V';
			case 14: return 'N';
			default: return '?';
		}
	}

	// String describing the data type
	public String getDescription()
	{
		return "IUPACNucleotide";
	}
	
	// Get numerical code describing the data type
	public int getTypeID()
	{
		return DataType.IUPACNUCLEOTIDES;
	}

	/**
	 * returns true if this state is an ambiguous state.
	 */
	public boolean isAmbiguousState(int state) {
		if ((state > 3) && (state < getNumStates())) {
			return true;
		}
		return false;
	}
	
	/**
	 * returns an array containing the non-ambiguous states 
	 * that this state represents.
	 */
	public int[] getSimpleStates(int state) {
		
		String stateString = "";
		
		switch (state) {
			case 0: stateString = "A"; break;
			case 1: stateString = "C"; break;
			case 2: stateString = "G"; break;
			case 3: stateString = "T"; break;
			case 4: stateString = "GT"; break;
			case 5: stateString = "AC"; break;
			case 6: stateString = "AG"; break;
			case 7: stateString = "CG"; break;
			case 8: stateString = "AT"; break;
			case 9: stateString = "CT"; break;
			case 10: stateString = "CGT"; break;
			case 11: stateString = "AGT"; break;
			case 12: stateString = "ACT"; break;
			case 13: stateString = "ACG"; break;
			case 14: stateString = "ACGT"; break;
			default: stateString = "ACGT"; break;
		}
		
		int[] states = new int[stateString.length()];
		for (int i = 0; i < stateString.length(); i++) {
			states[i] = getState(stateString.charAt(i));
		}
		
		return states;
	}
}

