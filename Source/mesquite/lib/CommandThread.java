/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import mesquite.lib.duties.*;import java.io.*;public class CommandThread extends MesquiteThread implements SpecialListName, Explainable {	MesquiteCommand command;	String arguments;	String uiCallInformation;	boolean logCommand;	static int waitCursorDepth =0;	boolean showWaitCursor;	boolean running = false;	boolean crashed = false;	CommandRecord record;	public CommandThread (MesquiteCommand command, String arguments, String uiCallInformation, boolean showWaitCursor, boolean logCommand) {		super();		this.command = command;		this.arguments = arguments;		this.uiCallInformation = uiCallInformation;		this.logCommand = logCommand;		this.showWaitCursor = showWaitCursor;		setCurrent(1);		if (showWaitCursor)			waitCursorDepth++;	}	public MesquiteCommand getCommand(){		return command;	}		public boolean hasCrashed(){		return crashed;	}	public String getListName(){		String s;		if (command.getOwner() instanceof Listable)			s= "Command: " + command.getName() + " to " + ((Listable)command.getOwner()).getName();		else			s= "Command: " + command.getName() + " to unknown object";		if (running)			return "(IN PROGRESS) " + s;		return s;	}	/**/	public String getExplanation(){		if (running)			return "(IN PROGRESS) " + uiCallInformation;		return uiCallInformation;	}	public CommandRecord getCommandRecord(){		return record;	}	public void setCommandRecord(CommandRecord c){		 record = c;	}	public String getCurrentCommandName(){		if (command !=null)			return command.getName();		return null;	}	public String getCurrentCommandExplanation(){		if (command !=null)			return "?";		return null;	}	/** DOCUMENT */	public void run() {		try {			int count = 0;			while (!(MesquiteCommand.currentThreads.size()==0 || MesquiteCommand.currentThreads.elementAt(0) == this)) { 				Thread.sleep(10);				count ++;			}			//execute command here			if (command.disposed) {				finishUp();				return;			}			if (command.ownerObject == null) {				MesquiteMessage.warnProgrammer("Warning: Command given to null object (" + command.commandName + "  " + arguments + ") CommandThread");				finishUp();				return;			}			if (logCommand){				String logString = " > " + command.commandName;				if (arguments!=null && !arguments.equals(""))					logString +=  " \"" + arguments + "\"";				if (command.ownerObject instanceof Listable)					logString = ((Listable)command.ownerObject).getName() + logString;				else					logString = "[" + command.ownerObject.getClass().getName() + "]"  + logString;				if (command.ownerObject instanceof Logger)					((Logger)command.ownerObject).logln(logString);				else					MesquiteModule.mesquiteTrunk.logln(logString);			}			running = true;			record = new CommandRecord(this, false);			CommandRecord previous = MesquiteThread.getCurrentCommandRecord();			MesquiteThread.setCurrentCommandRecord(record);			record.addListener(command);			Object returned = command.ownerObject.doCommand(command.commandName, arguments, record, CommandChecker.defaultChecker);  //if a command is executed in this way, assumed that not scripting			MesquiteThread.setCurrentCommandRecord(previous);			finishUp();		}		catch (InterruptedException e){			Thread.currentThread().interrupt();		}		catch (Exception e){				crashed = true;				e.printStackTrace();				PrintWriter pw = MesquiteFile.getLogWriter();				if (pw!=null)					e.printStackTrace(pw);				MesquiteTrunk.mesquiteTrunk.alert("A command could not be completed because an exception or error occurred (i.e. a crash; " + e.getClass() + "). If you save any files, you might best use Save As... in case file saving doesn't work properly.  To report this as a bug, PLEASE send along the Mesquite_Log file from Mesquite_Support_Files.");				//TODO: should have flag in modules and windows that successfully completed startup; if not, then zap them				finishUp();					}		catch (Error e){			if (!(e instanceof ThreadDeath)){				crashed = true;				e.printStackTrace();				PrintWriter pw = MesquiteFile.getLogWriter();				if (pw!=null)					e.printStackTrace(pw);				MesquiteTrunk.mesquiteTrunk.alert("A command could not be completed because an exception or error occurred (i.e. a crash; " + e.getClass() + " " + MesquiteException.lastLocMessage() + "). If you save any files, you might best use Save As... in case file saving doesn't work properly.  To report this as a bug, PLEASE send along the Mesquite_Log file from Mesquite_Support_Files.");				//TODO: should have flag in modules and windows that successfully completed startup; if not, then zap them				finishUp();			}   	 		throw e;		}	}		private void finishUp(){		running = false;		if (command.ownerObject instanceof FileDirtier)			((FileDirtier)command.ownerObject).fileDirtiedByCommand(command);				MesquiteCommand.currentThreads.removeElement(this, false);		if (showWaitCursor) {			--waitCursorDepth;			if (waitCursorDepth<1) //MesquiteCommand.currentThreads.size()==1 && 				MesquiteWindow.restoreAllCursors();		}		interrupt();	}}