package org.maripo.josm.easypresets;

import static org.openstreetmap.josm.tools.I18n.tr;

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

import org.openstreetmap.josm.gui.layer.LayerManager;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;

import java.util.Timer;
import java.util.TimerTask;

public class EasyPresetsPlugin extends Plugin implements ListDataListener, LayerChangeListener {
	public static final EasyPresets root = new EasyPresets();
	public static final GroupPresetMenu groupMenu = new GroupPresetMenu(root);
	
	public EasyPresetsPlugin (PluginInformation info) {
		super(info);
		root.setName(tr("Custom Presets"));
		root.load();
		root.addListDataListener(this);
		
		// Add custom presets to "Presets" menu
		JMenu menu = MainApplication.getMenu().presetsMenu;
		menu.add(new JSeparator());
		MainMenu.add(menu, new CreatePresetAction(root));
		MainMenu.add(menu, new ManagePresetsAction(root));
		
		// Group for all custom presets
		groupMenu.updatePresetListMenu();
		menu.add(groupMenu.menu);
		MainApplication.getLayerManager().addLayerChangeListener(this);
	}
	
	@Override 
	public void layerAdded (LayerAddEvent e) {
		TimerTask task = new TimerTask() {
			public void run() {
				MainApplication.getToolbar().refreshToolbarControl();
			}
		};
		new Timer().schedule(task, 1000);
	}
	@Override public void layerRemoving(LayerRemoveEvent e) {
		
	}
	@Override public void layerOrderChanged(LayerOrderChangeEvent e) {
		
	}
	@Override
	public void contentsChanged(ListDataEvent arg0) {
		TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}
	
	@Override
	public void intervalAdded(ListDataEvent arg0) {
		TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}
	
	@Override
	public void intervalRemoved(ListDataEvent arg0) {
		TaggingPresetNameTemplateList.getInstance().taggingPresetsModified();
	}
	
}
