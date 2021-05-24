package org.maripo.josm.easypresets;

import static org.openstreetmap.josm.tools.I18n.tr;

import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.data.EasyPresets.EasyPresetsListener;
import org.maripo.josm.easypresets.ui.CreatePresetAction;
import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.maripo.josm.easypresets.ui.ManagePresetsAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetNameTemplateList;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

import java.util.Timer;
import java.util.TimerTask;

public class EasyPresetsPlugin extends Plugin implements /*ListDataListener, */EasyPresetsListener {
	
	private static final EasyPresets root = new EasyPresets();
	private GroupPresetMenu groupMenu;
	
	public EasyPresetsPlugin (PluginInformation info) {
		super(info);
		root.setName(tr("Custom Presets"));
		root.load();
		// root.addListDataListener(this);
		root.setListener(this);
		addMenu();
		
		// Group for all custom presets
		/*
		groupMenu = new GroupPresetMenu(root);
		groupMenu.updatePresetListMenu();
		menu.add(groupMenu.menu);
		*/
	}
	private void addMenu () {
		// Add custom presets to "Presets" menu
		JMenu menu = MainApplication.getMenu().presetsMenu;
		menu.add(new JSeparator());
		MainMenu.add(menu, new CreatePresetAction(root));
		MainMenu.add(menu, new ManagePresetsAction(root));
	}
	@Override
	public void onReload () {
		addMenu();
	}
	
}
