/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.distance.TaxonDistance;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.distance.lib.*;public class TaxonDistance extends NumberFor2Taxa {	MesquiteNumber nt;	TaxaDistanceSource distanceTask;	TaxaDistance taxaDistance;	MesquiteString distanceTaskName;	MesquiteCommand atC;	MesquiteSubmenuSpec mss;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		distanceTask = (TaxaDistanceSource)hireEmployee(commandRec, TaxaDistanceSource.class, "Source of distance");		if (distanceTask == null) { 			return sorry(commandRec, "Can't start TaxonDistance because no distance source found"); 		}		nt= new MesquiteNumber();		atC = makeCommand("setDistanceSource",  this);		distanceTask.setHiringCommand(atC);		distanceTaskName = new MesquiteString(distanceTask.getName());		if (numModulesAvailable(TaxaDistanceSource.class)>1){			mss = addSubmenu(null, "Source of Distance", atC, TaxaDistanceSource.class);			mss.setSelected(distanceTaskName);		}  		return true;  	 }  	public void employeeQuit(MesquiteModule m){  		iQuit();  	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine( "setDistanceSource " , distanceTask);  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets the module supplying distances", "[name of module]", commandName, "setDistanceSource")) {    	 		TaxaDistanceSource temp =  (TaxaDistanceSource)replaceEmployee(commandRec, TaxaDistanceSource.class, arguments, "Source of distances", distanceTask); 			if (temp!=null) {	    	 	distanceTask=  temp;	    	 	taxaDistance = null;		 		distanceTask.setHiringCommand(atC);				distanceTaskName = new MesquiteString(distanceTask.getName());				if (mss!=null){					mss.setSelected(distanceTaskName);				}				parametersChanged(null, commandRec); 			} 			return temp;    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);   	 }   	public void initialize(Taxon t1, Taxon t2, CommandRecord commandRec){   	}   		public void calculateNumber(Taxon t1, Taxon t2, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		if (result==null || t1==null || t2==null)			return;		result.setToUnassigned();		Taxa taxa = t1.getTaxa();		if (taxaDistance==null) //need to recalculate only if change in distance calculator			taxaDistance = distanceTask.getTaxaDistance(taxa, commandRec);		result.setValue(taxaDistance.getDistance(t1.getTaxa().whichTaxonNumber(t1),t2.getTaxa().whichTaxonNumber(t2)));			}			/*.................................................................................................................*/   	 public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {   	 		taxaDistance=null; //to force recalculation 			super.employeeParametersChanged(this, source, notification, commandRec);   	 }	/*.................................................................................................................*/    	 public String getNameAndParameters() {		return distanceTask.getNameAndParameters();   	 }    		/*.................................................................................................................*/    	 public String getParameters() {		return "Distance used: " + distanceTask.getName();   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Distance between taxa";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Counts the difference between two taxa." ;   	 }   	 public boolean isPrerelease(){   	 	return false;   	 }   	 }