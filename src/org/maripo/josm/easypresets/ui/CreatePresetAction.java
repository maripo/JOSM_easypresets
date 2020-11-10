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
import org.maripo.josm.easypresets.ui.editor.PresetEditorDialog;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.tools.Shortcut;

@SuppressWarnings("serial")
public class CreatePresetAction extends JosmAction {
	EasyPresets presets;
	
    /**
     * Constructs a new {@code JosmAction} and installs layer changed and selection changed adapters.
     *
     * Use this super constructor to setup your action.
     *
     * @param name the action's text as displayed on the menu (if it is added to a menu)
     * @param iconName the filename of the icon to use
     * @param tooltip  a longer description of the action that will be displayed in the tooltip. Please note
     *           that html is not supported for menu actions on some platforms.
     * @param shortcut a ready-created shortcut object or null if you don't want a shortcut. But you always
     *            do want a shortcut, remember you can always register it with group=none, so you
     *            won't be assigned a shortcut unless the user configures one. If you pass null here,
     *            the user CANNOT configure a shortcut for your action.
     * @param registerInToolbar register this action for the toolbar preferences?
     */
    protected CreatePresetAction(String name, String iconName, String tooltip, Shortcut shortcut, boolean registerInToolbar) {
        super(name, iconName, tooltip, shortcut, registerInToolbar, null, true);
    }

    public CreatePresetAction (EasyPresets root) {
        this(tr("Create Preset"), "easypresets_add.png",
                tr("Create or edit your custom preset based on tags from the current selection"),
                Shortcut.registerShortcut(
                        "tools:easy_presets_create", tr("Tool: {0}", tr("Create Preset")), 
                        KeyEvent.VK_F3,
                        Shortcut.ALT_CTRL_SHIFT), true);
        this.presets = root;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		OsmDataLayer layer = getLayerManager().getEditLayer();
        if (layer==null || layer.data==null) {
        	showAlertDialog(tr("Layer is null"));
        	return;
        }
        Collection<OsmPrimitive> selected = layer.data.getSelected();
        if (selected==null || selected.isEmpty()) {
        	showAlertDialog(tr("Selection is empty. EasyPresets creates new presets based on selected nodes or ways."));
        	return;
        }
        Map<String, Map<String, Integer>> tagMap = findTagsFromSelection(selected);
        List<TaggingPresetType> targetTypes = findTypesFromSelection(selected);
		PresetEditorDialog dialog = new PresetEditorDialog(tagMap, targetTypes, presets);
		dialog.showDialog();
	}
	
	protected List<TaggingPresetType> findTypesFromSelection(Collection<OsmPrimitive> selected) {
		List<TaggingPresetType> types = new ArrayList<TaggingPresetType>();
		
		for (OsmPrimitive primitive: selected) {
			TaggingPresetType type = TaggingPresetType.forPrimitiveType(primitive.getDisplayType());
        	if (!types.contains(type)) {
        		types.add(type);
        	}
        }
        return types;
	}

	protected Map<String, Map<String, Integer>> findTagsFromSelection(Collection<OsmPrimitive> selected) {
        Map<String, Map<String, Integer>> allTags = new TreeMap<String, Map<String, Integer>>();
        for (OsmPrimitive primitive: selected) {
			// Support both Map<String, List<String>> and <Map<String, String>
			@SuppressWarnings("rawtypes")
			Map tags = primitive.getInterestingTags();
			if (tags==null) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Iterator<String> keyIte = (Iterator<String>)tags.keySet().iterator();
			while (keyIte.hasNext()) {
				String key = keyIte.next();
				Object valueObj = tags.get(key);
				if (valueObj instanceof String) {
					incrementKeyValueCount(allTags, key, (String)valueObj);
				} else {
					@SuppressWarnings("unchecked")
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
			int count = (valueCountMap.containsKey(value) ? valueCountMap.get(value).intValue() : 0);
			valueCountMap.put(value, Integer.valueOf(count+1));
		}
	}

	private void showAlertDialog(String string) {
        final ExtendedDialog dialog = new ExtendedDialog(MainApplication.getMainFrame(), "EasyPresets", "OK")
        		.setContent(string);
        dialog.showDialog();
	}

}
