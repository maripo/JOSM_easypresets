package org.maripo.josm.easypresets.data;

import javax.swing.Icon;

import org.maripo.josm.easypresets.ui.GroupPresetMenu;

public interface PresetsEntry {

	int getSize();

	Icon getIcon();

	String getName();
	
	String getLocaleName();
	
	String getRawName();

	PresetsEntry copy();
	
	EasyPresets getParent();
	
	void setParent(EasyPresets parent);
	
	void addListDataListener(GroupPresetMenu groupPresetMenu);
}
