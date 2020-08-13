package org.maripo.josm.easypresets.data;

import javax.swing.Icon;

import org.maripo.josm.easypresets.ui.GroupPresetMenu;

public interface PresetsEntry {

	int getSize();

	Icon getIcon();

	String getName();
	
	String getLocaleName();
	
	PresetsEntry copy();
	
	void addListDataListener(GroupPresetMenu groupPresetMenu);
}
