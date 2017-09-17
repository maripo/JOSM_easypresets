package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.gui.tagging.presets.items.Label;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
/**
 * Editor dialog to create or edit a custom preset
 * @author maripo
 *
 */
public class PresetEditorDialog extends ExtendedDialog {
	
	private List<TagEditor> tagEditors;
	private JTextField uiPresetName;
	private JTextArea uiXML;

	List<TargetType> targetTypes = new ArrayList<TargetType>();
	String name;
	private TaggingPreset presetToEdit;
	protected Collection<TaggingPresetType> defaultTypes;
	
	/**
	 * Create new preset (Initialize with tags and types extracted from selection)
	 * @param tagMap
	 * @param presetTypes 
	 */
	public PresetEditorDialog (Map<String, Map<String, Integer>> tagMap, 
			List<TaggingPresetType> presetTypes) {
		super(Main.parent, tr("Preset Editor"));
		this.defaultTypes = presetTypes;

		tagEditors = new ArrayList<TagEditor>();
        for (final String key: tagMap.keySet()) {
        	TagEditor editor = TagEditor.create(this, key, tagMap.get(key));
        	tagEditors.add(editor);
        }
        name = "";
		initUI();
	}

	/**
	 * Edit existing preset (Initialize with existing TaggingPreset object)
	 * @param selectedPreset
	 */
	public PresetEditorDialog(TaggingPreset preset) {
		super(Main.parent, tr("Preset Editor"));
		name = preset.name;
		this.presetToEdit = preset;
		defaultTypes = preset.types;
		tagEditors = new ArrayList<TagEditor>();
		// Select all
		for (final TaggingPresetItem item: preset.data) {
			TagEditor editor = TagEditor.create(this, item);
			if (editor!=null) {
				tagEditors.add(editor);
			}
		}
		initUI();
	}
	
	JPanel listPane;
	JLabel errorMessageLabel;
	private void initUI() {
		targetTypes.add(new TargetType(TaggingPresetType.NODE));
		targetTypes.add(new TargetType(TaggingPresetType.WAY));
		targetTypes.add(new TargetType(TaggingPresetType.CLOSEDWAY));
		targetTypes.add(new TargetType(TaggingPresetType.RELATION));
		targetTypes.add(new TargetType(TaggingPresetType.MULTIPOLYGON));
		
		final JPanel mainPane = new JPanel(new GridBagLayout());
		mainPane.add(new JLabel(tr("Preset Name")));
		uiPresetName = new JTextField();
		uiPresetName.setText(name);
		mainPane.add(uiPresetName, GBC.eol().fill(GBC.HORIZONTAL));
		
		// Types pane
		final JPanel typesPane = new JPanel(new GridBagLayout());
		for (final TargetType type: targetTypes) {
			typesPane.add(type.createCheckbox());
			typesPane.add(new JLabel(ImageProvider.get(type.type.getIconName())));
			typesPane.add(new JLabel(tr(type.type.getName())), GBC.eol().fill(GBC.HORIZONTAL));
		}
        mainPane.add(typesPane, GBC.eol().fill(GBC.HORIZONTAL).insets(0, 15, 0, 15));
        
		
        listPane = new JPanel(new GridBagLayout());
        // Header
        listPane.add(new JLabel(tr("Use")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        listPane.add(new JLabel(tr("Type")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        listPane.add(new JLabel(tr("Key")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        listPane.add(new JLabel(tr("Value")), GBC.eol().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST).fill(GBC.HORIZONTAL));
        
        for (TagEditor editor: tagEditors) {
        	editor.appendUI(listPane);
        }
        JScrollPane scroll = new JScrollPane(listPane);
        scroll.setPreferredSize(new Dimension(640, 300));
        mainPane.add(scroll, GBC.eol().fill(GBC.HORIZONTAL).insets(0, 0, 0, 15));
        uiXML = new JTextArea();
        uiXML.setRows(tagEditors.size() + 1);

        JButton addTagButton = new JButton(tr("Add Tag"));
        addTagButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTag();
			}});
        mainPane.add(addTagButton, GBC.eol());

        JButton saveButton = new JButton(tr("Save"));
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

        JButton cancelButton = new JButton(tr("Cancel"));
        cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

        errorMessageLabel = new JLabel();
        errorMessageLabel.setForeground(Color.RED);
        mainPane.add(errorMessageLabel, GBC.eol().fill());
        mainPane.add(saveButton);
        mainPane.add(cancelButton, GBC.eol());
        setContent(mainPane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toFront();
            }
        });
	} 

	protected void addTag () {
		TagEditor editor = TagEditor.create(this);
		tagEditors.add(editor);
		editor.appendUI(listPane);
		revalidate();
		repaint();
	}
	
	protected void close() {
		this.dispose();
	}

	protected void save () {
		errorMessageLabel.setText("");
		TaggingPreset newPreset = createPreset();
		if (newPreset!=null) {
			EasyPresets.getInstance().add(newPreset);
			if (presetToEdit!=null) {
				EasyPresets.getInstance().remove(presetToEdit);
			}
			EasyPresets.getInstance().save();
			close();
		}
	}

	private class TargetType {

		private TaggingPresetType type;
		private boolean checked;
		private JCheckBox checkbox;

		public TargetType(TaggingPresetType type) {
			this.type = type;
			boolean checkedDefault = (defaultTypes!=null && defaultTypes.contains(type));
			this.checked = checkedDefault;
		}

		public JCheckBox createCheckbox() {
			checkbox = new JCheckBox();
			checkbox.setSelected(checked);
			return this.checkbox;
		}

		public boolean isChecked() {
			return checkbox.isSelected();
		}
		
	}
	/**
	 * Generate new TaggingPreset
	 * @return
	 */
	private TaggingPreset createPreset () {
		TaggingPreset preset = new TaggingPreset();
		if (uiPresetName.getText().isEmpty()) {
			errorMessageLabel.setText(tr("Preset name is empty."));
			return null;
		}
		preset.name = uiPresetName.getText();

		// Add "Label"
		Label label = new Label();
		label.text = uiPresetName.getText();
		preset.data.add(label);
		
		boolean hasItem = false;
		for (TagEditor editor : tagEditors) {
			TaggingPresetItem item = editor.getTaggingPresetItem();
			if (item!=null) {
				hasItem = true;
				preset.data.add(item);
			}
		}
		if (!hasItem) {
			errorMessageLabel.setText(tr("Tag list is empty."));
			return null;
		}
		preset.setDisplayName();
		if (preset.types==null) {
	        preset.types = EnumSet.noneOf(TaggingPresetType.class);
		}
		preset.types = getSelectedTypes();
		return preset;
	}

	private Set<TaggingPresetType> getSelectedTypes() {
		Set<TaggingPresetType> types = EnumSet.noneOf(TaggingPresetType.class);
		for (TargetType type: targetTypes) {
			if (type.isChecked()) {
				types.add(type.type);
			}
		}
		return types;
	}
}
