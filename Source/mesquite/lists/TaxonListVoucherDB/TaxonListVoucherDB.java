/* Mesquite Chromaseq source code.  Copyright 2005-2011 David Maddison and Wayne Maddison.Version 1.0   December 2011Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lists.TaxonListVoucherDB;/*~~  */import mesquite.lists.lib.*;import mesquite.lib.*;import mesquite.lib.table.*;/* ======================================================================== */public class TaxonListVoucherDB extends TaxonListAssistant {	Taxa taxa;	MesquiteTable table=null;	MesquiteMenuItemSpec msbrowseDB;	MesquiteMenuItemSpec msSetDB;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		return true;  	 } 	/*.................................................................................................................*/	public void setTableAndTaxa(MesquiteTable table, Taxa taxa){		if (this.taxa != null)			this.taxa.removeListener(this);		this.taxa = taxa;		if (this.taxa != null)			this.taxa.addListener(this);		this.table = table;		deleteMenuItem(msbrowseDB);		msbrowseDB = addMenuItem("Browse for OTU code database...", makeCommand("browseDB", this));		deleteMenuItem(msSetDB);		msSetDB = addMenuItem("Set OTU code database...", makeCommand("setDB", this));	}	/*.................................................................................................................*/	public void dispose() {		super.dispose();		if (taxa!=null)			taxa.removeListener(this);	}	/*.................................................................................................................*/	private void setDBURL(String arguments){		if (table !=null && taxa!=null) {			boolean changed=false;			String path = arguments; //parser.getFirstToken(arguments);			if (StringUtil.blank(path))				return;			if (employer!=null && employer instanceof ListModule) {				int c = ((ListModule)employer).getMyColumn(this);				for (int i=0; i<taxa.getNumTaxa(); i++) {					if (table.isCellSelectedAnyWay(c, i)) {						taxa.setAssociatedObject(VoucherInfo.voucherDBRef, i, path);						if (!changed)							outputInvalid();						changed = true;					}				}			}//			if (changed)//				data.notifyListeners(this, new Notification(MesquiteListener.NAMES_CHANGED)); //TODO: bogus! should notify via specs not data???			outputInvalid();			parametersChanged();		}	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the OTU code database to the one you choose by browsing", null, commandName, "browseDB")) {			MesquiteString directoryName = new MesquiteString();			MesquiteString fileName = new MesquiteString();			String urlString = MesquiteFile.openFileDialog("Choose OTU code database", directoryName, fileName);			if (StringUtil.blank(urlString))				return null;			setDBURL("tdt:"+ urlString);			}		else if (checker.compare(this.getClass(), "Sets the OTU code database to the one you enter", null, commandName, "setDB")) {			if (table!=null){				String urlString = MesquiteString.queryString(containerOfModule(), "VoucherDB", "URL of VoucherDB:", "");				if (StringUtil.blank(urlString))					return null;				setDBURL(urlString);				}		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	public void changed(Object caller, Object obj, Notification notification){		outputInvalid();		parametersChanged(notification);	}	public String getTitle() {		return "Voucher DB";	}	public String getStringForTaxon(int ic){		if (taxa!=null) {			Object n = (String)taxa.getAssociatedObject(VoucherInfo.voucherDBRef, ic);			if (n !=null)				return ((String)n);		}		return "-";	}	/*...............................................................................................................*/	/** returns whether or not a cell of table is editable.*/	public boolean isCellEditable(int row){		return true;	}	/*...............................................................................................................*/	/** for those permitting editing, indicates user has edited to incoming string.*/	public void setString(int row, String s){		if (taxa!=null) {			taxa.setAssociatedObject(VoucherInfo.voucherDBRef, row, s);		}			}	public boolean useString(int ic){		return true;	}		public String getWidestString(){		return "88888888888888888  ";	}	/*.................................................................................................................*/    	 public String getName() {		return "OTU Code Database";   	 }	/*.................................................................................................................*/   	public boolean isPrerelease(){   		return false;     	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return -1;  	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}   	 	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Lists the OTU code database for a taxon." ;   	 }}