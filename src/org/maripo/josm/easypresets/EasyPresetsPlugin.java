package org.maripo.josm.easypresets;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.CreatePresetAction;
import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.maripo.josm.easypresets.ui.ManagePresetsAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetNameTemplateList;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class EasyPresetsPlugin extends Plugin implements ListDataListener {
	public static final EasyPresets root = new EasyPresets();
	public static final GroupPresetMenu groupMenu = new GroupPresetMenu(root);

	public EasyPresetsPlugin (PluginInformation info) {
		super(info);
        root.load();
        root.setName(tr("Custom Presets"));
		root.addListDataListener(this);
		
        // Add custom presets to "Presets" menu
		JMenu menu = MainApplication.getMenu().presetsMenu;
        menu.add(new JSeparator());
        MainMenu.add(menu, new CreatePresetAction(root));
        MainMenu.add(menu, new ManagePresetsAction(root));
        
        // Group for all custom presets
        menu.add(groupMenu.menu);
		TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		saveAllPresetsTo();
		TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
		saveAllPresetsTo();
		TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
		saveAllPresetsTo();
	}
	
	private void saveAllPresetsTo() {
		File file = new File(root.getXMLPath());
		root.saveTo(file);
	}
}
