package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;

@SuppressWarnings("serial")
public class ManagePresetsAction extends CreatePresetAction {
	EasyPresets root;

	public ManagePresetsAction (EasyPresets root) {
        super(tr("Manage custom presets"), "easypresets.png",
                tr("List, Update Or Delete Custom Presets"),
                null, true);
		this.root = root;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		OsmDataLayer layer = getLayerManager().getEditLayer();
        if (layer!=null && layer.data!=null) {
            Collection<OsmPrimitive> selected = layer.data.getSelected();
            if (selected!=null && !selected.isEmpty()) {
            	Map<String, Map<String, Integer>> tagMap = findTagsFromSelection(selected);
            	List<TaggingPresetType> targetTypes = findTypesFromSelection(selected);
        		ManagePresetsDialog dialog = new ManagePresetsDialog(tagMap, targetTypes, this.root, null);
        		dialog.showDialog();
        		return;
            }
        }
		
		ManagePresetsDialog dialog = new ManagePresetsDialog(this.root);
		dialog.showDialog();
	}
}
