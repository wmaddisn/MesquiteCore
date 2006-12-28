/* Mesquite source code.  Copyright 1997-2002 W. Maddison & D. Maddison. Version 0.992.  September 2002.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.diverse.CategCharSpecExt;import java.awt.Label;import mesquite.lib.*;import mesquite.categ.lib.*;import mesquite.diverse.lib.*;/** ======================================================================== */public class CategCharSpecExt extends TreeCharSimulate {	RandomBetween rng;	double rateCharStateChange0 = 0.01; //01	double rateCharStateChange1 = 0.01; //01	double spnForState0 = 0.1;	double spnForState1 = 0.1;	double extForState0 = 0.03;	double extForState1 = 0.03;	double increment = 0.001; //	double increment = 0.0001;	double prior1AtRoot = 0.5; //TODO: have toggle to set what is used	boolean keepAllExtinct = false; //TODO: have toggle to set what is used		//IF BOOLEAN SET THEN increment spnForState0 up by 0.01 or some such each new tree	double spnPerIncrement0 = spnForState0*increment;	double spnPerIncrement1 = spnForState1*increment;	double extPerIncrement0 = extForState0*increment;	double extPerIncrement1 = extForState1*increment;	double rateChangePerIncrement0 = rateCharStateChange0*increment;	double rateChangePerIncrement1 = rateCharStateChange1*increment;	double logSpnForState0 = Math.log(spnPerIncrement0);  	double logSpnForState1 = Math.log(spnPerIncrement1);	double logExtForState0 = Math.log(extPerIncrement0);  	double logExtForState1 = Math.log(extPerIncrement1);	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		rng= new RandomBetween(1);		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExtensibleDialog dlog = new ExtensibleDialog(containerOfModule(), "Simulation Parameters",  buttonPressed);		DoubleField[] value = new DoubleField[6];		value[0] = dlog.addDoubleField("Rate 0 -> 1: ", rateCharStateChange0, 6, 0, 100);		value[1] = dlog.addDoubleField("Rate 1 -> 0: ", rateCharStateChange1, 6, 0, 100);		value[2] = dlog.addDoubleField("Rate Speciation with 0: ", spnForState0, 6, 0, 100);		value[3] = dlog.addDoubleField("Rate Speciation with 1 :", spnForState1, 6, 0, 100);		value[4] = dlog.addDoubleField("Rate Extinction with 0: ", extForState0, 6, 0, 100);		value[5] = dlog.addDoubleField("Rate Extinction with 1: ", extForState1, 6, 0, 100);		dlog.completeAndShowDialog(true);		boolean ok = (dlog.query()==0);		boolean success = false;		if (ok) {			success = true;			for (int i=0; i<6; i++)				if (!MesquiteDouble.isCombinable(value[i].getValue()))					success = false;			if (!success){				dlog.dispose();				return false;			}			rateCharStateChange0 = value[0].getValue();			rateCharStateChange1 = value[1].getValue();			spnForState0 = value[2].getValue();			spnForState1 = value[3].getValue();			extForState0 = value[4].getValue();			extForState1 = value[5].getValue();		}		else			return false;		dlog.dispose();		spnPerIncrement0 = spnForState0*increment;		spnPerIncrement1 = spnForState1*increment;		logSpnForState0 = Math.log(spnPerIncrement0);		logSpnForState1 = Math.log(spnPerIncrement1);		extPerIncrement0 = extForState0*increment;		extPerIncrement1 = extForState1*increment;		logExtForState0 = Math.log(extPerIncrement0);		logExtForState1 = Math.log(extPerIncrement1);		rateChangePerIncrement0 = rateCharStateChange0*increment;		rateChangePerIncrement1 = rateCharStateChange1*increment;		prior1AtRoot =  1.0 - stationaryFreq0();		addMenuItem("Rate of Evolution of Speciation Controlling Character...", makeCommand("setRate",  this));		addMenuItem("Rate of Speciation if state 0...", makeCommand("setSRate0",  this));		addMenuItem("Rate of Speciation if state 1...", makeCommand("setSRate1",  this));		addMenuItem("Rate of Extinction if state 0...", makeCommand("setERate0",  this));		addMenuItem("Rate of Extinction if state 1...", makeCommand("setERate1",  this));		addMenuItem("Prior on state at root...", makeCommand("setPrior",  this));		return true;	}	/*.................................................................................................................*/	public double stationaryFreq0() {  		double d = spnForState0-spnForState1+extForState1-extForState0;		double r01 = rateCharStateChange0;		double r10 = rateCharStateChange1;		if (Math.abs(d ) < 1e-100){			if (r01 + r10 == 0)				return 0.5;			return r10/(r01+r10);		}		double part = d - r01 - r10;		part = part*part + 4*d*r10;		if (part >=0)			part = Math.sqrt(part);		else			return MesquiteDouble.unassigned;		double plus = (r01 + r10 - d + part) / (-2*d);		double minus = (r01 + r10 - d - part) / (-2*d);		if (minus < 0 || minus >1)			return plus;		else if (plus < 0 || plus >1)			return minus;		else			return MesquiteDouble.unassigned;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return false;  	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		temp.addLine("setCRate0 " + rateCharStateChange0);		temp.addLine("setCRate1 " + rateCharStateChange1);		temp.addLine("setSRate0 " + spnForState0);		temp.addLine("setSRate1 " + spnForState1);		temp.addLine("setERate0 " + extForState0);		temp.addLine("setERate1 " + extForState1);		temp.addLine("setPrior " + prior1AtRoot);		return temp;	}	MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the rate of change of the categorical character", "[number]", commandName, "setCRate0")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Evolution of Speciation Controlling Character 0 -> 1", rateCharStateChange0);			if (MesquiteDouble.isCombinable(s)) {				rateCharStateChange0 = s;				rateChangePerIncrement0 = s * increment;				prior1AtRoot =  1.0 - stationaryFreq0();				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of change of the categorical character", "[number]", commandName, "setCRate1")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Evolution of Speciation Controlling Character 1 -> 0", rateCharStateChange1);			if (MesquiteDouble.isCombinable(s)) {				rateCharStateChange1 = s;				rateChangePerIncrement1 = s * increment;				prior1AtRoot =  1.0 - stationaryFreq0();				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of speciation if 0", "[number]", commandName, "setSRate0")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Speciation if 0", spnForState0);			if (MesquiteDouble.isCombinable(s)) {				spnForState0 = s;				spnPerIncrement0 = spnForState0*increment;				logSpnForState0 = Math.log(spnPerIncrement0);				prior1AtRoot =  1.0 - stationaryFreq0();				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of speciation if 1", "[number]", commandName, "setSRate1")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Speciation if 1", spnForState1);			if (MesquiteDouble.isCombinable(s)) {				spnForState1 = s;				spnPerIncrement1 = spnForState1*increment;				logSpnForState1 = Math.log(spnPerIncrement1);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of extinction if 0", "[number]", commandName, "setERate0")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Extinction if 0", extForState0);			if (MesquiteDouble.isCombinable(s)) {				extForState0 = s;				extPerIncrement0 = extForState0*increment;				prior1AtRoot =  1.0 - stationaryFreq0();				logExtForState0 = Math.log(extPerIncrement0);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of extinction if 1", "[number]", commandName, "setERate1")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Extinction if 1", extForState1);			if (MesquiteDouble.isCombinable(s)) {				extForState1 = s;				extPerIncrement1 = extForState1*increment;				prior1AtRoot =  1.0 - stationaryFreq0();				logExtForState1 = Math.log(extPerIncrement1);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the prior probability of state 1 at root", "[number]", commandName, "setPrior")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Root state", "Probability of state 1 at root", prior1AtRoot);			if (MesquiteDouble.isCombinable(s)) {				prior1AtRoot = s;				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	private double getSRate(long state){		if (state == 1) //state 0			return logSpnForState0;		else			return logSpnForState1;	}	private double getERate(long state){		if (state == 1) //state 0			return logExtForState0;		else			return logExtForState1;	}	long countSpeciations = 0;	long countExtinctions = 0;	/*.................................................................................................................*/	private boolean goExtinctIfUnlucky(Taxa taxa, boolean[] taxaInTree, MesquiteTree tree, CategoricalHistory states, MesquiteInteger countOfSpecies, CommandRecord commandRec){		for (int it = 0; it<taxa.getNumTaxa(); it++){			if (taxaInTree[it]){				int node = tree.nodeOfTaxonNumber(it);				long statesAtNode = states.getState(node);				double rate = getERate(statesAtNode);				double probability = Math.exp(-Math.abs(rate));				if (rng.nextDouble()<probability) {					if (!keepAllExtinct){						tree.deleteClade(node, false);						countOfSpecies.decrement();					}					taxaInTree[it] = false;					countExtinctions++;						commandRec.tick("Went extinct at node " + node + " ; total number of species " + countOfSpecies + "; total speciations: " + countSpeciations  + "; total extinctions: " + countExtinctions );					if (countOfSpecies.getValue() == 0 || tree.numberOfTerminalsInClade(tree.getRoot()) == 0)						return true;				}			}		}		return false;	}	/*.................................................................................................................*/	private void speciateIfLucky(MesquiteTree tree, int node, boolean[] taxaInTree, CategoricalHistory states, MesquiteInteger countOfSpecies, int numTaxa, CommandRecord commandRec){		if (tree.nodeIsTerminal(node) && MesquiteDouble.isCombinable(tree.getBranchLength(node))) {  //length will be uncombinable if this was just a daughter species			if (countOfSpecies.getValue()<numTaxa) {				int taxon = tree.taxonNumberOfNode(node);				if (!keepAllExtinct || taxaInTree[taxon]){ //not extinct				long statesAtNode = states.getState(node);				double rate = getSRate(statesAtNode);  //negative absolute value of rate				double probability = Math.exp(-Math.abs(rate));				if (rng.nextDouble()<probability) {					tree.splitTerminal(taxon, -1, false);					countSpeciations++;					int firstD = tree.firstDaughterOfNode(node);					int lastD = tree.lastDaughterOfNode(node);					states.setState(firstD, statesAtNode);					states.setState(lastD, statesAtNode);					taxaInTree[tree.taxonNumberOfNode(firstD)] = true;					taxaInTree[tree.taxonNumberOfNode(lastD)] = true;					countOfSpecies.increment();					commandRec.tick("Speciated at node " + node + " ; total number of species " + countOfSpecies + "; total speciations: " + countSpeciations  + "; total extinctions: " + countExtinctions );				}				}			}		}		else			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {				if (countOfSpecies.getValue()<numTaxa)					speciateIfLucky(tree, d, taxaInTree, states, countOfSpecies, numTaxa, commandRec);			}	}	private void flipState(CategoricalHistory states, int index){		if (states.getState(index) == 1L)			states.setState(index, 2L); //state 0 to 1		else			states.setState(index, 1L);//state 1 to 0	}	long numFlips;	/*.................................................................................................................*/	private void evolveRates(MesquiteTree tree, int node, boolean[] taxaInTree, CategoricalHistory states){		if (tree.nodeIsTerminal(node)) {			int taxon = tree.taxonNumberOfNode(node);			if (!keepAllExtinct || taxaInTree[taxon]){ //not extinct			if ((states.getState(node) == 1L && rng.nextDouble()< rateChangePerIncrement0) || (states.getState(node) == 2L && rng.nextDouble()< rateChangePerIncrement1)) {				flipState(states, node);				numFlips++;			}			}		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {			evolveRates(tree, d, taxaInTree, states);		}	}	/*.................................................................................................................*/	private void addLengthToAllTerminals(MesquiteTree tree, int node, double increment, boolean[] taxaInTree){		if (tree.nodeIsTerminal(node)) {			int taxon = tree.taxonNumberOfNode(node);			if (!keepAllExtinct || taxaInTree[taxon]){ //not extinct			double current = tree.getBranchLength(node, MesquiteDouble.unassigned);			if (MesquiteDouble.isCombinable(current))				tree.setBranchLength(node, current + increment, false);			else				tree.setBranchLength(node, increment, false);  	 				}		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {			addLengthToAllTerminals(tree, d, increment, taxaInTree);		}	}	/*.................................................................................................................*/	public String getDataType(){		return "Standard Categorical Data";	}	/*.................................................................................................................*/	public  void doSimulation(Taxa taxa, int replicateNumber, ObjectContainer treeContainer, ObjectContainer characterHistoryContainer, MesquiteLong seed, CommandRecord commandRec){		//save random seed used to make tree under tree.seed for use in recovering later		rng.setSeed(seed.getValue());		MesquiteTree tree = null;		CategoricalHistory charHistory = null;		Object t = treeContainer.getObject();		if (t == null || !(t instanceof MesquiteTree))			tree = new MesquiteTree(taxa);		else			tree = (MesquiteTree)t;		Object c = characterHistoryContainer.getObject();		if (c == null || !(c instanceof CategoricalHistory))			charHistory = new CategoricalHistory(taxa);		else  			charHistory = (CategoricalHistory)c;		charHistory = (CategoricalHistory)charHistory.adjustSize(tree);		int numTaxa = taxa.getNumTaxa();		int attempts = 0;		boolean done = false;		boolean wentExtinct = false;		int patience = 100; //TODO: make this user settable		boolean[] taxaInTree = new boolean[taxa.getNumTaxa()];		for (int i=0; i< taxaInTree.length; i++)			taxaInTree[i] = false;		long state = 1L; //state 0	Debugg.println("prior 1 at root " + prior1AtRoot);	String hitsString = "";	long minGen500 = 0;	long maxGen500 = 0;		while (attempts < patience && !done){			countSpeciations = 0;			countExtinctions = 0;			wentExtinct = false;			state = 1L; //state 0			if (rng.nextDouble()<prior1AtRoot)				state = 2L; //state 1			for (int i=0; i<tree.getNumNodeSpaces(); i++){  //state 0				charHistory.setState(i, state);			}			tree.setToDefaultBush(2, false);			taxaInTree[0] = true;			taxaInTree[1] = true;			tree.setAllBranchLengths(0, false);			MesquiteInteger countOfSpecies = new MesquiteInteger(2);			long generations = 0;			//logln("sim using  logSpnForState0 " + logSpnForState0 + " logSpnForState1 " + logSpnForState1 + " rateChangePerIncrement " + rateChangePerIncrement);			commandRec.tick("Attempt " + (attempts+1) + " to simulate tree ");			addLengthToAllTerminals(tree, tree.getRoot(), increment, taxaInTree);			numFlips =0;			hitsString = "";			while (countOfSpecies.getValue()<numTaxa && !wentExtinct){				generations++;				evolveRates(tree, tree.getRoot(), taxaInTree, charHistory);				boolean anyExtinctions = goExtinctIfUnlucky(taxa, taxaInTree, tree, charHistory, countOfSpecies, commandRec);				if (anyExtinctions){					wentExtinct = true;					commandRec.tick("Extinction event (species in tree currently:  " + countOfSpecies.getValue() + ") [attempt: "+ (attempts+1) + "] ");				}				else {					speciateIfLucky(tree, tree.getRoot(), taxaInTree, charHistory, countOfSpecies, numTaxa, commandRec);					if (countOfSpecies.getValue() == 500){						if (minGen500 == 0)							minGen500 = generations;						if (generations>maxGen500)							maxGen500 = generations;						hitsString = hitsString + "\t" + generations;					}					addLengthToAllTerminals(tree, tree.getRoot(), increment, taxaInTree);					commandRec.tick("Speciation event (species in tree currently:  " + countOfSpecies.getValue()  + ") [attempt: "+ (attempts+1) + "] ");				}			}			if (!wentExtinct)				done = true;			attempts++;		}		if (!done){			tree.setToDefaultBush(2, false);			tree.setAllBranchLengths(0, false);			charHistory.deassignStates();			MesquiteMessage.warnUser("Attempt to simulate speciation/extinction failed because clade went entirely extinct " + patience + " times");		}		else  {			hitsString = "\t" + minGen500 + "\t" + maxGen500 + hitsString;			MesquiteMessage.println("Tree and character " + (replicateNumber +1) + " successfully evolved.");			tree.setName("Sim. sp/ext with char. " + (replicateNumber +1) + " [#ext. " + countExtinctions + " #st.chg. " + numFlips + " root " + CategoricalState.toString(state) + "]" + hitsString);		}		//	MesquiteMessage.println("number of character changes: " + numFlips);		treeContainer.setObject(tree);		characterHistoryContainer.setObject(charHistory);		seed.setValue(rng.nextLong());  //see for next time	}	public void initialize(Taxa taxa, CommandRecord commandRec){	}	/*.................................................................................................................*/	/*.................................................................................................................*/	public String getName() {		return "Evolving Binary Speciation/Extinction Character";	}	/*.................................................................................................................*/	public String getVersion() {		return null;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Generates tree by a speciation/extinction model in which a character controlling rates of speciation/extinction ." ;	}	/*.................................................................................................................*/	public String getParameters() {		return "Rates: " + MesquiteDouble.toString(rateCharStateChange0, 4) + "," + MesquiteDouble.toString(rateCharStateChange1, 4) + "/" + MesquiteDouble.toString(spnForState0, 4) + "," +MesquiteDouble.toString(spnForState1, 4) + "/" +MesquiteDouble.toString(extForState0, 4) + "," +MesquiteDouble.toString(extForState1, 4);	}	/*.................................................................................................................*/}