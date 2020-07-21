package org.maripo.josm.easypresets;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.CreatePresetAction;
import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.maripo.josm.easypresets.ui.ManagePresetsAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class EasyPresetsPlugin extends Plugin {
	public static EasyPresets presets;
	public static GroupPresetMenu groupMenu;

	public EasyPresetsPlugin (PluginInformation info) {
		super(info);
		
		presets = EasyPresets.getInstance();
        presets.load();

        // Add custom presets to "Presets" menu
		JMenu menu = MainApplication.getMenu().presetsMenu;
        menu.add(new JSeparator());
        MainMenu.add(menu, new CreatePresetAction());
        MainMenu.add(menu, new ManagePresetsAction());
        
        // Group for all custom presets
        groupMenu = new GroupPresetMenu();
        groupMenu.updatePresetListMenu(presets);
        menu.add(groupMenu.menu);
	}
	
}
