package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetMenu;

@SuppressWarnings("serial")
public class GroupPresetMenu extends TaggingPresetMenu {

	/**
	 * Create a preset group holding all custom presets
	 * @return created group
	 */
	public GroupPresetMenu() {
		super();
		name = tr("Custom Presets");
		setIcon("easypresets.png");
		menu = new JMenu(name);
		setDisplayName();
	}

	public void updatePresetListMenu(EasyPresets presets) {
		setEnabled(presets.size()>0);
		menu.removeAll();
        for (TaggingPreset preset: presets) {
            JMenuItem mi = new JMenuItem(preset);
            mi.setText(preset.getLocaleName());
            menu.add(mi);
        }
	}
	
}
