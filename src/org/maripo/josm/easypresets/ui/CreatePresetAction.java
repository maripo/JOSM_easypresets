package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.tools.Shortcut;

public class CreatePresetAction extends JosmAction {

	public CreatePresetAction () {
        super(tr("Create Preset"), "easypresets.png",
                tr("Create or edit your custom preset based on tags from the current selection"),
                Shortcut.registerShortcut(
                        "tools:easy_presets", tr("Tool: {0}", tr("Create Preset")), 
                        KeyEvent.VK_F3,
                        Shortcut.ALT_CTRL_SHIFT), true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		OsmDataLayer layer = getLayerManager().getEditLayer();
        if (layer==null || layer.data==null) {
        	debugDialog("Layer is null");
        	return;
        }
        Collection<OsmPrimitive> selected = layer.data.getSelected();
        if (selected==null || selected.isEmpty()) {
        	debugDialog("Selection is null");
        	return;
        }
        Map<String, Map<String, Integer>> tagMap = findTagsFromSelection(selected);
        List<TaggingPresetType> targetTypes = findTypesFromSelection(selected);
        new PresetEditorDialog(tagMap, targetTypes).showDialog();
	}

	private List<TaggingPresetType> findTypesFromSelection(Collection<OsmPrimitive> selected) {
		List<TaggingPresetType> types = new ArrayList<TaggingPresetType>();
        for (OsmPrimitive primitive: selected) {
        	TaggingPresetType type = TaggingPresetType.forPrimitive(primitive);
        	if (!types.contains(type)) {
        		types.add(type);
        	}
        }
        return types;
	}

	private Map<String, Map<String, Integer>> findTagsFromSelection(Collection<OsmPrimitive> selected) {
        Map<String, Map<String, Integer>> allTags = new TreeMap<String, Map<String, Integer>>();
        for (OsmPrimitive primitive: selected) {
			// Support both Map<String, List<String>> and <Map<String, String>
			Map tags = primitive.getInterestingTags();
			if (tags==null) {
				continue;
			}
			Iterator<String> keyIte = (Iterator<String>)tags.keySet().iterator();
			while (keyIte.hasNext()) {
				String key = keyIte.next();
				Object valueObj = tags.get(key);
				if (valueObj instanceof String) {
					incrementKeyValueCount(allTags, key, (String)valueObj);
				} else {
					List<String> values = (List<String>)tags.get(key);
					for (String value: values) {
						incrementKeyValueCount(allTags, key, value);
					}
				}
			}
		}
		return allTags;
	
	}

	private void incrementKeyValueCount(Map<String, Map<String, Integer>> allTags, 
			String key, String value) {
		if (value==null || value.isEmpty()) {
			return;
		}
		if (!allTags.containsKey(key)) {
			allTags.put(key, new HashMap<String, Integer>());
			Map<String, Integer> valueCountMap = allTags.get(key);
			int count = (valueCountMap.containsKey(value)) ? valueCountMap.get(value).intValue() : 0;
			valueCountMap.put(value, new Integer(count+1));
		}
		
	}

	private void debugDialog(String string) {
        final ExtendedDialog dialog = new ExtendedDialog(Main.parent, "Alert", "OK")
        		.setContent(string);
        dialog.showDialog();
	}

}
