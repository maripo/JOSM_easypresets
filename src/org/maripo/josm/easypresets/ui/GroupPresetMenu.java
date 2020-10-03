package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.data.PresetsEntry;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetMenu;

@SuppressWarnings("serial")
public class GroupPresetMenu extends TaggingPresetMenu implements ListDataListener {
	EasyPresets model;
	
	/*
	* Create a preset group holding all custom presets
	*/
	public GroupPresetMenu(EasyPresets presets) {
		super();
		name = tr("Custom Presets");
		setIcon("easypresets.png");
		menu = new JMenu(name);
		setDisplayName();
		model = presets;
		model.addListDataListener(this);
	}
	
	public void updatePresetListMenu() {
		setEnabled(model.getSize() > 0);
		menu.removeAll();
		List<PresetsEntry> lentry = model.getEntry();
		for (PresetsEntry entry : lentry) {
			if (entry instanceof TaggingPreset) {
				JMenuItem mi = new JMenuItem((TaggingPreset)entry);
				mi.setText(((TaggingPreset)entry).getName());
				mi.setEnabled(true);
				menu.add(mi);
			}
			else if (entry instanceof EasyPresets) {
				menu.add(((EasyPresets) entry).getMenu());
			}
		}
	}
	
	@Override
	public void contentsChanged(ListDataEvent evt) {
		updatePresetListMenu();
	}
	
	@Override
	public void intervalAdded(ListDataEvent evt) {
		updatePresetListMenu();
		
	}
	
	@Override
	public void intervalRemoved(ListDataEvent evt) {
		updatePresetListMenu();
	}
	
}
