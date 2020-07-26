package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.maripo.josm.easypresets.data.EasyPresets;
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
		setEnabled(model.size()>0);
		menu.removeAll();
		TaggingPreset[] array = (TaggingPreset[]) model.toArray();
        for (int i = 0; i < array.length; i++) {
            JMenuItem mi = new JMenuItem(array[i]);
            mi.setText(array[i].getLocaleName());
            menu.add(mi);
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
