/* Mesquite source code.  Copyright 1997 and onward, W. Maddison and D. Maddison. 


Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
*/
package mesquite.lib;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.*;

/* ======================================================================== */
/** A menu bar attached to a window.*/
public class MesquiteMenuBar extends JMenuBar {
	MesquiteWindow ownerWindow;
	public static long totalCreated = 0;
	public static long totalFinalized = 0;
	public MesquiteMenuBar(MesquiteWindow ownerWindow){
		super();
		this.ownerWindow=ownerWindow;
		totalCreated++;
	}
	
	public MesquiteWindow getOwnerWindow(){
		return ownerWindow;
	}
	boolean menuWithSameLabelExists(String label){
		for (int i=0; i<getMenuCount(); i++)
			if (getMenu(i).getLabel().equals(label))
				return true;
		return false;
	}
	public JMenu add(JMenu menu) {
		if (menu==null)
			return null;
		while (menuWithSameLabelExists(menu.getLabel()))
			menu.setLabel(menu.getLabel()+".");
		return super.add(menu);
	}
	/*-------------------------------------------------------*/
	public void finalize() throws Throwable {
		MesquiteMenuBar.totalFinalized++;
		super.finalize();
	}
	private void removeItems(JMenu menu){
			for (int j=0; j<menu.getItemCount(); j++) {
				JMenuItem item = menu.getItem(j);
				if (item instanceof JMenu)
					removeItems((JMenu)item);
				if (item instanceof ActionListener){
					((JMenuItem)item).removeActionListener((ActionListener)item);
				}
				if (item instanceof JCheckBoxMenuItem){
					((JCheckBoxMenuItem)item).removeItemListener((ItemListener)item);
				}
			}
			menu.removeAll();
	}
	public void disconnect() {
	//	Debugg.println("@@@@@@@@@@@@@ disconnect menu bar");
		ownerWindow = null;
		for (int i=0; i<getMenuCount(); i++){
			JMenu menu = getMenu(i);
			removeItems(menu);
		}
		for (int i=getMenuCount()-1; i>=0; i--){
			JMenu menu = getMenu(i);
			remove(menu);
		}
		
	//	removeAll();
	/*
		for (int i=0; i<getMenuCount(); i++){
			Menu menu = getMenu(i);
			if (menu instanceof MesquiteMenu)
				((MesquiteMenu)menu).recycle = true; //setting recycle flag so can be recycled
			for (int j=0; j<menu.getItemCount(); j++) {
				MenuItem item = menu.getItem(i);
				if (item instanceof MesquiteMenu)
					((MesquiteMenu)item).recycle = true; //setting recycle flag so can be recycled
			}
		}
		for (int i=getMenuCount()-1; i>=0; i--){
			Menu menu = getMenu(i);
			menu.removeAll();
			remove(menu);
		}
	//	removeAll();
	*/
	}
}


