package org.maripo.josm.easypresets;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.CreatePresetAction;
import org.maripo.josm.easypresets.ui.ManagePresetsAction;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class EasyPresetsPlugin extends Plugin {

	@SuppressWarnings("deprecation")
	public EasyPresetsPlugin (PluginInformation info) {
		super(info);
		EasyPresets.getInstance().load();

        // Add custom presets to "Presets" menu
        JMenu menu = Main.main.menu.presetsMenu;
        menu.add(new JSeparator());
        MainMenu.add(menu, new CreatePresetAction());
        MainMenu.add(menu, new ManagePresetsAction());
        // Group for all custom presets
        JMenu customPresetMenu = EasyPresets.getInstance().createPresetListMenu();
        menu.add(customPresetMenu);
	}
}
