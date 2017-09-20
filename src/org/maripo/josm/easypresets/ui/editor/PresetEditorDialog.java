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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	
	private JTextField uiPresetName;

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

		List<TagEditor> tagEditors = new ArrayList<TagEditor>();
        for (final String key: tagMap.keySet()) {
        	TagEditor editor = TagEditor.create(this, key, tagMap.get(key));
        	tagEditors.add(editor);
        }
        name = "";
		initUI(tagEditors);
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
		List<TagEditor> tagEditors = new ArrayList<TagEditor>();
		// Select all
		for (final TaggingPresetItem item: preset.data) {
			TagEditor editor = TagEditor.create(this, item);
			if (editor!=null) {
				tagEditors.add(editor);
			}
		}
		initUI(tagEditors);
	}
	
	TagsPane tagsPane;
	JLabel errorMessageLabel;
	private void initUI(List<TagEditor> tagEditors) {
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
        
		
        tagsPane = new TagsPane(tagEditors, this);
        
        JPanel listWrapper = new JPanel();
        listWrapper.setLayout(new GridBagLayout());
        listWrapper.add(tagsPane, GBC.eol());
        listWrapper.add(Box.createVerticalGlue(), GBC.eol().fill(GBC.VERTICAL));

        JScrollPane scroll = new JScrollPane(listWrapper);
        scroll.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        scroll.setAlignmentY(JScrollPane.TOP_ALIGNMENT);
        scroll.setPreferredSize(new Dimension(640, 300));
        mainPane.add(scroll, GBC.eol().fill(GBC.HORIZONTAL).insets(0, 0, 0, 0));

        JButton addTagButton = new JButton(tr("Add Tag"));
        addTagButton.setIcon(ImageProvider.get("dialogs", "add"));
        addTagButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTag();
			}});
        mainPane.add(addTagButton, GBC.eol().insets(0, 0, 0, 15));

        JButton saveButton = new JButton(tr("Save"));
        saveButton.setIcon(ImageProvider.get("ok"));
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

        JButton cancelButton = new JButton(tr("Cancel"));
        cancelButton.setIcon(ImageProvider.get("cancel"));
        cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

        errorMessageLabel = new JLabel("");
        errorMessageLabel.setForeground(Color.RED);
        errorMessageLabel.setVisible(false);
        mainPane.add(errorMessageLabel, GBC.eol().fill());
        mainPane.add(saveButton, GBC.std());
        mainPane.add(cancelButton, GBC.eol());
        setContent(mainPane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toFront();
            }
        });
	} 

	protected void addTag () {
		tagsPane.addTag();
		repaint();
	}
	
	protected void close() {
		this.dispose();
	}

	void showErrorMessage(String message) {
		if (message==null || message.isEmpty()) {
			errorMessageLabel.setVisible(false);
		} else {
			errorMessageLabel.setVisible(true);
			errorMessageLabel.setText(message);
		}
	}
	protected void save () {
		showErrorMessage("");
		TaggingPreset newPreset = createPreset();
		if (newPreset!=null) {
			if (presetToEdit!=null) {
				// Replace old preset
				EasyPresets.getInstance().replace(presetToEdit, newPreset);
			} else {
				// Add new preset
				EasyPresets.getInstance().add(newPreset);
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
			showErrorMessage(tr("Preset name is empty."));
			return null;
		}
		preset.name = uiPresetName.getText();

		// Add "Label"
		Label label = new Label();
		label.text = uiPresetName.getText();
		preset.data.add(label);
		
		boolean hasItem = false;
		List<TagEditor> tagEditors = tagsPane.getTagEditors();
		for (TagEditor editor : tagEditors) {
			TaggingPresetItem item = editor.getTaggingPresetItem();
			if (item!=null) {
				hasItem = true;
				preset.data.add(item);
			}
		}
		if (!hasItem) {
			showErrorMessage(tr("Tag list is empty."));
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
