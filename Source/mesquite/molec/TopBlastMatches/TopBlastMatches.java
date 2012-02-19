/* Mesquite source code.  Copyright 1997-2011 W. Maddison and D. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.molec.TopBlastMatches; import java.awt.*;import java.awt.event.*;import java.net.*;import java.io.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;import mesquite.molec.lib.*;/* ======================================================================== */public class TopBlastMatches extends CategDataSearcher implements ItemListener { 	MesquiteTable table;	CharacterData data;	StringBuffer results;	String[] accessionNumbers;	boolean importTopMatches = false;	boolean saveResultsToFile = true;	int maxHits = 1;	double  minimumBitScore = 0.0;	boolean preferencesSet = false;	boolean fetchTaxonomy = false;	boolean blastx = false;	int maxTime = 300;	static int upperMaxHits = 30;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		loadPreferences();		results = new StringBuffer();		results.append("Match\tAccession\tScore\tE Value\n");		return true;	}	/*.................................................................................................................*/	public void processSingleXMLPreference (String tag, String content) {		if ("fetchTaxonomy".equalsIgnoreCase(tag))			fetchTaxonomy = MesquiteBoolean.fromTrueFalseString(content);		else if ("saveResultsToFile".equalsIgnoreCase(tag))			saveResultsToFile = MesquiteBoolean.fromTrueFalseString(content);		else if ("importTopMatches".equalsIgnoreCase(tag))			importTopMatches = MesquiteBoolean.fromTrueFalseString(content);		else if ("blastx".equalsIgnoreCase(tag))			blastx = MesquiteBoolean.fromTrueFalseString(content);		else if ("maxTime".equalsIgnoreCase(tag))			maxTime = MesquiteInteger.fromString(content);		else if ("maxHits".equalsIgnoreCase(tag))			maxHits = MesquiteInteger.fromString(content);				preferencesSet = true;	}	/*.................................................................................................................*/	public String preparePreferencesForXML () {		StringBuffer buffer = new StringBuffer(60);			StringUtil.appendXMLTag(buffer, 2, "fetchTaxonomy", fetchTaxonomy);  		StringUtil.appendXMLTag(buffer, 2, "maxTime", maxTime);  		StringUtil.appendXMLTag(buffer, 2, "importTopMatches", importTopMatches);  		StringUtil.appendXMLTag(buffer, 2, "blastx", blastx);  		StringUtil.appendXMLTag(buffer, 2, "maxHits", maxHits);  		StringUtil.appendXMLTag(buffer, 2, "saveResultsToFile", saveResultsToFile);  		preferencesSet = true;		return buffer.toString();	}	Checkbox blastXCheckBox ;	Checkbox saveFileCheckBox ;	Checkbox fetchTaxonomyCheckBox;	Checkbox importCheckBox;	/*.................................................................................................................*/	private void checkEnabling(){		importCheckBox.setEnabled(!blastXCheckBox.getState());		fetchTaxonomyCheckBox.setEnabled(!blastXCheckBox.getState() && saveFileCheckBox.getState());	}	/*.................................................................................................................*/	public void itemStateChanged(ItemEvent e) {		checkEnabling();	}	/*.................................................................................................................*/	public boolean queryOptions() {		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog dialog = new ExtensibleDialog(containerOfModule(), "Top Blast Matches",buttonPressed);  //MesquiteTrunk.mesquiteTrunk.containerOfModule()		dialog.addLabel("Options for Top Blast Matches");		IntegerField maxHitsField = dialog.addIntegerField("Maximum number of matches:",  maxHits,5,1,upperMaxHits);		blastXCheckBox = dialog.addCheckBox("use blastx for nucleotides",blastx);		saveFileCheckBox = dialog.addCheckBox("save report of results to file",saveResultsToFile);		fetchTaxonomyCheckBox = dialog.addCheckBox("fetch taxonomic lineage",fetchTaxonomy);		importCheckBox = dialog.addCheckBox("import top matches into matrix",importTopMatches);		IntegerField maxTimeField = dialog.addIntegerField("Maximum time for BLAST response (seconds):",  maxTime,5);		blastXCheckBox.addItemListener(this);		saveFileCheckBox.addItemListener(this);		checkEnabling();		dialog.completeAndShowDialog(true);		if (buttonPressed.getValue()==0)  {			maxHits = maxHitsField.getValue();			saveResultsToFile = saveFileCheckBox.getState();			fetchTaxonomy = fetchTaxonomyCheckBox.getState();			blastx = blastXCheckBox.getState();			importTopMatches = importCheckBox.getState();			maxTime=maxTimeField.getValue();			storePreferences();		}		dialog.dispose();		return (buttonPressed.getValue()==0);	}	/*.................................................................................................................*/	public  void actionPerformed(ActionEvent e) {		if (e.getActionCommand().equalsIgnoreCase("defaults")) {		}	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return 110;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	/** Called to search on the data in selected cells.  Returns true if data searched*/	public boolean searchData(CharacterData data, MesquiteTable table){		this.data = data;		if (!(data instanceof DNAData || data instanceof ProteinData)){			discreetAlert( "Only DNA or protein data can be searched using this module.");			return false;		} 		else {			if (!queryOptions())				return false;			boolean searchOK = searchSelectedTaxa(data,table);			logln("\nSearch results: \n"+ results.toString());			if (importTopMatches && (data instanceof ProteinData || !blastx)) {				logln("About to import top matches.", true);				StringBuffer report = new StringBuffer();				NCBIUtil.fetchGenBankSequencesFromAccessions(accessionNumbers, data,  this, true, report);					data.notifyListeners(this, new Notification(MesquiteListener.PARTS_ADDED));				data.getTaxa().notifyListeners(this, new Notification(MesquiteListener.PARTS_ADDED));				logln(report.toString());			}			if (saveResultsToFile)				saveResults( results);			return searchOK;		}	}	/*.................................................................................................................*/	public boolean isNucleotides(CharacterData data){		return data instanceof DNAData;	}	/*.................................................................................................................*/	public boolean acceptableHit(int hitCount, double bitScore, double eValue) {		return hitCount<=maxHits;	}	/*.................................................................................................................*/	protected URL getESearchAddress(String s)	throws MalformedURLException {		return new URL(s);	}	/*.................................................................................................................*/	public void saveResults( StringBuffer results) {		String path;		path = MesquiteFile.saveFileAsDialog("Save Top Matches report", new StringBuffer("blastMatches.txt"));		if (!StringUtil.blank(path)) {			MesquiteFile.putFileContents(path, results.toString(), false);		}	}	/*.................................................................................................................*/	public void searchOneTaxon(CharacterData data, int it, int icStart, int icEnd){		if (data==null)			return;		String sequenceName = data.getTaxa().getTaxonName(it);		StringBuffer sequence = new StringBuffer(data.getNumChars());		for (int ic = icStart; ic<=icEnd; ic++) {			data.statesIntoStringBuffer(ic, it, sequence, false, false, false);		}				StringBuffer response = new StringBuffer();		if (data instanceof ProteinData)			NCBIUtil.blastForMatches("blastp", sequenceName, sequence.toString(), true, maxHits, 300, response);		else if (blastx)			NCBIUtil.blastForMatches("blastx", sequenceName, sequence.toString(), true, maxHits, 300, response);		else			NCBIUtil.blastForMatches("blastn", sequenceName, sequence.toString(), true, maxHits, 300, response);		BLASTResults blastResults = new BLASTResults(maxHits);		blastResults.processResultsFromBLAST(response.toString(), false);		for (int i=0; i<maxHits; i++) {			results.append(blastResults.getDefinition(i)+"\t");			results.append(blastResults.getAccession(i)+"\t");			results.append(blastResults.getBitScore(i)+"\t");			results.append(blastResults.geteValue(i));			String tax = "";			if (fetchTaxonomy) {				tax = NCBIUtil.fetchTaxonomyList(blastResults.getAccession(i), data instanceof DNAData, true, null);				results.append("\t"+tax);			}			results.append("\n");		}		accessionNumbers = blastResults.getAccessions();	}	/*.................................................................................................................*/	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyMolecularData();	}	/*.................................................................................................................*/	public String getNameForMenuItem() {		return "Top BLAST Matches...";	}	/*.................................................................................................................*/	public String getName() {		return "Top BLAST Matches";	}	/*.................................................................................................................*/	public boolean showCitation() {		return false;	}	/*.................................................................................................................*/	public String getExplanation() {		return "Does a BLAST search against GenBank on selected data and returns the top BLAST matches for each sequence selected.";	}}/*Search string into Genbank Entrez:http://www.ncbi.nlm.nih.gov:80/entrez/query.fcgi?cmd=Search&db=nucleotide&dopt=GenBank&term=Bembidionhttp://www.ncbi.nlm.nih.gov/blast/Blast.cgi?DATABASE=nr&FORMAT_TYPE=HTML&PROGRAM=blastn&CLIENT=web&SERVICE=plain&PAGE=Nucleotides&CMD=Put&QUERY= http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?DATABASE=nr&HITLIST_SIZE=10&FILTER=L&EXPECT=10&FORMAT_TYPE=HTML&PROGRAM=blastn&CLIENT=web&SERVICE=plain&NCBI_GI=on&PAGE=Nucleotides&CMD=Put&QUERY= http://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=put&PAGE=Nucleotides&program=blast&QUERY_FILE=fasta&query="CCAAGTCCTTCTTGAAGGGGGCCATTTACCCATAGAGGGTGCCAGGCCCGTAGTGACCATTTATATATTTGGGTGAGTTTCTCCTTAGAGTCGGGTTGCTTGAGAGTGCAGCTCTAAGTGGGTGGTAAACTCCATCTAAGGCTAAATATGACTGCGAAACCGATAGCGAACAAGTACCGTGAGGGAAAGTTGAAAAGAACTTTGAAGAGAGAGTTCAAGAGTACGTGAAACTGTTCAGGGGTAAACCTGTGGTGCCCGAAAGTTCGAAGGGGGAGATTC" */