package org.maripo.josm.easypresets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.maripo.josm.easypresets.data.EasyPreset;
import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.CreatePresetAction;
import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.maripo.josm.easypresets.ui.ManagePresetsAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class EasyPresetsPlugin extends Plugin implements ListDataListener {
	public static EasyPresets root;
	public static GroupPresetMenu groupMenu;

	public EasyPresetsPlugin (PluginInformation info) {
		super(info);
		root = new EasyPresets();
        root.load();
		root.addListDataListener(this);
		
        // Add custom presets to "Presets" menu
		JMenu menu = MainApplication.getMenu().presetsMenu;
        menu.add(new JSeparator());
        MainMenu.add(menu, new CreatePresetAction(root));
        MainMenu.add(menu, new ManagePresetsAction(root));
        
        // Group for all custom presets
        groupMenu = new GroupPresetMenu(root);
        menu.add(groupMenu.menu);
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		saveAllPresetsTo();
		// TODO TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
		saveAllPresetsTo();
		// TODO TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
		saveAllPresetsTo();
	}
	
	private void saveAllPresetsTo() {
		File file = new File(root.getXMLPath());
		List<EasyPreset> list = root.getPresets();
		List<TaggingPreset> tags = new ArrayList<TaggingPreset>();
		for (EasyPreset preset : list) {
			if (preset instanceof TaggingPreset) {
				tags.add(preset);
			}
		}
		root.saveTo(tags, file);
	}
	
}
