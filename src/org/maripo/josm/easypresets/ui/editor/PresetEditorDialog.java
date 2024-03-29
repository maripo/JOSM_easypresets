package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.maripo.josm.easypresets.data.EasyPreset;
import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.editor.IconPickerDialog.IconPickerDialogListener;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.gui.tagging.presets.items.Label;
import org.openstreetmap.josm.gui.tagging.presets.items.Link;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;

/**
 * Editor dialog to create or edit a custom preset
 * @author maripo, hayashi
 *
 */
@SuppressWarnings("serial")
public class PresetEditorDialog extends ExtendedDialog {
	private JTextField uiPresetName;
	private JTextField uiURL;
	private JCheckBox uiIncludeName; // Check to include name label
	private Icon icon;
	private String iconPath;

	private List<TargetType> targetTypes = new ArrayList<TargetType>();
	private String name;
	private String referenceURL;
	private EasyPreset presetToEdit;
	protected Collection<TaggingPresetType> defaultTypes;
	private int index = -1;
	private EasyPresets parentPresets;
	
	/*
	 * Create new preset (Initialize with tags and types extracted from selection)
	 * 
	 * @param tagMap		Tag map
	 * @param presetTypes	POI types
	 * @param presets		EasyPresets
	 */
	public PresetEditorDialog (
			Map<String,Map<String, Integer>> tagMap, 
			List<TaggingPresetType> presetTypes,
			EasyPresets presets)
	{
		super(MainApplication.getMainFrame(), tr("Preset Editor"));
		this.defaultTypes = presetTypes;
		this.parentPresets = presets;

		List<TagEditor> tagEditors = new ArrayList<TagEditor>();
        for (final String key: tagMap.keySet()) {
        	TagEditor editor = TagEditor.create(this, key, tagMap.get(key), presets);
        	tagEditors.add(editor);
        }
        name = "";
		initUI(tagEditors);
	}

	/*
	 * Edit existing preset (Initialize with existing TaggingPreset object)
	 * 
	 * @param preset		new EasyPreset
	 * @param index			insert index
	 * @param parentPresets	EasyPresets
	 */
	public PresetEditorDialog(EasyPreset preset, int index, final EasyPresets parentPresets) {
		this(preset, null, index, parentPresets);
	}
	
	/*
	 * Edit existing preset (Initialize with existing TaggingPreset object)
	 * 
	 * @param preset		new EasyPreset
	 * @param tagMap		Tag map
	 * @param index			insert index
	 * @param parentPresets	EasyPresets
	 */
	public PresetEditorDialog (EasyPreset preset, 
			Map<String,Map<String, Integer>> tagMap, 
			int index, 
			final EasyPresets parentPresets) 
	{
		super(MainApplication.getMainFrame(), tr("Preset Editor"));
		this.index = index;
		this.parentPresets = parentPresets;
		this.name = preset.getLocaleName();
		this.referenceURL = findURL(preset);
		this.icon = preset.getIcon();
		this.iconPath = preset.iconName;
		this.presetToEdit = preset.clone();
		this.defaultTypes = preset.types;
		List<TagEditor> tagEditors = new ArrayList<TagEditor>();
		if (tagMap != null) {
	        for (final String key: tagMap.keySet()) {
	        	TagEditor editor = TagEditor.create(this, key, tagMap.get(key), parentPresets);
	        	tagEditors.add(editor);
	        }
		}
		
		// Select all
		for (final TaggingPresetItem item: preset.data) {
			TagEditor editor = TagEditor.create(this, item);
			if (editor!=null) {
				tagEditors.add(editor);
			}
		}
		initUI(tagEditors);
		boolean containsLabel = false;
		for (TaggingPresetItem field : preset.data) {
			if (field instanceof Label) {
				containsLabel = true;
			}
		}
		uiIncludeName.setSelected(containsLabel);
	}

	private String findURL(TaggingPreset preset) {
		if (preset.data!=null) {
			for (TaggingPresetItem item: preset.data) {
				if (item instanceof Link) {
					return ((Link)item).href;
				}
			}
		}
		return null;
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
		
		mainPane.add(new JLabel(tr("Preset Name") + ":"),  GBC.std().insets(0, 0, 0, 10).anchor(GBC.WEST));
		uiPresetName = new JTextField(16);
		uiPresetName.setText(name);
		
		uiIncludeName = new JCheckBox();
		uiIncludeName.setSelected(true);
		
		JLabel label = new JLabel(tr("Show the name on the dialog"));
		label.setLabelFor(uiIncludeName);

		mainPane.add(uiPresetName, GBC.std().insets(0, 0, 0, 10));
		mainPane.add(uiIncludeName, GBC.std().insets(0, 0, 0, 10));
		mainPane.add(label, GBC.eol().insets(0, 0, 0, 10));
		
		// Types pane
		final JPanel typesPane = new JPanel(new GridBagLayout());
		int index = 0;
		for (final TargetType type: targetTypes) {
			typesPane.add(type.createCheckbox());
			typesPane.add(new JLabel(ImageProvider.get(type.type.getIconName())));
			GBC constraints = (index%3==2)?GBC.eol():GBC.std();
			typesPane.add(new JLabel(tr(type.type.getName())), constraints);
			index++;
		}

		mainPane.add(new JLabel(tr("Icon") + ":"), GBC.std().anchor(GBC.NORTHWEST));
		JPanel iconPane = new JPanel(new GridBagLayout());
		JLabel iconLabel = new JLabel();
		JLabel iconPathLabel = new JLabel();
		if (icon!=null) {
			iconLabel.setIcon(icon);
		}
		if (iconPath!=null && !iconPath.isEmpty()) {
			iconPathLabel.setText(iconPath);
		}
        JButton iconPickerButton = new JButton(tr("Select icon") + "...");
        ExtendedDialog dialog = this;
        iconPickerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IconPickerDialog iconPickerDialog = new IconPickerDialog(dialog);
				iconPickerDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				iconPickerDialog.setAlwaysOnTop(true);
				iconPickerDialog.setupDialog();
				iconPickerDialog.setListener(new IconPickerDialogListener() {
					
					@Override
					public void onSelectIcon(ImageIcon _icon, String name) {
						icon = _icon;
						iconLabel.setIcon(icon);
						iconPathLabel.setText(name);
						iconLabel.revalidate();
						iconPathLabel.revalidate();
						iconPath = name;
					}
					
					@Override
					public void onCancel() {
						
					}
				});
				iconPickerDialog.showDialog();
			}
		});
        iconPane.add(iconLabel, GBC.std().insets(0));
        iconPane.add(iconPathLabel, GBC.std().insets(0));
        iconPane.add(iconPickerButton, GBC.std().insets(0));
        mainPane.add(iconPane, GBC.eol());
        mainPane.add(iconPane, GBC.eol().insets(0, 0, 0, 5).anchor(GBC.NORTHWEST));
        
		mainPane.add(new JLabel(tr("Applies to") + ":"), GBC.std().anchor(GBC.NORTHWEST));
        mainPane.add(typesPane, GBC.eol().insets(0, 0, 0, 5).anchor(GBC.NORTHWEST));

		mainPane.add(new JLabel(tr("Reference URL") + ":"), GBC.std().anchor(GBC.NORTHWEST));
		uiURL = new JTextField();
		uiURL.setText(referenceURL);
		mainPane.add(uiURL, GBC.eol().fill());
		mainPane.add(new JLabel(tr("Tags") + ":"), GBC.eol().anchor(GBC.NORTHWEST));
		
        tagsPane = new TagsPane(tagEditors, this);
        if (tagEditors.isEmpty()) {
        	tagsPane.addTag();
        }
        
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
        addTagButton.setIcon(ImageProvider.get("dialogs", "add", ImageSizes.LARGEICON));
        addTagButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTag();
			}});
        mainPane.add(addTagButton, GBC.eol().insets(0, 0, 0, 15));

        JButton saveButton = new JButton(tr("Save"));
        saveButton.setIcon(ImageProvider.get("ok", ImageSizes.LARGEICON));
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

        JButton cancelButton = new JButton(tr("Cancel"));
        cancelButton.setIcon(ImageProvider.get("cancel", ImageSizes.LARGEICON));
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
        	@Override
            public void run() {
                toFront();
            }
        });
	} 

	protected void addTag() {
		tagsPane.addTag();
		repaint();
	}
	
	protected void close() {
		this.dispose();
	}
	
	@Override
	public void dispose() {
		super.dispose();
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
		List<String> errors = validateInput();
		if (!errors.isEmpty()) {
			showErrorMessage(errors.get(0));
			// TODO support multiple errors
			return;
		}
		
		if (presetToEdit != null) {
			String str = uiPresetName.getText().trim();
			if ((str == null) || (str.length() < 1)) {
				str = presetToEdit.getName();
				uiPresetName.setText(str);
			}
			presetToEdit.name = str;
			this.parentPresets.setElementAt(applyToPreset(presetToEdit), index);
		} else {
			// New preset
			this.parentPresets.addElement(applyToPreset(new EasyPreset()));
		}
		close();
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
	
	private List<String> validateInput () {
		List<String> errors = new ArrayList<String>();
		if (uiPresetName.getText().isEmpty()) {
			errors.add(tr("Preset name is empty."));
		}
		boolean hasItem = false;
		List<TagEditor> tagEditors = tagsPane.getTagEditors();
		for (TagEditor editor : tagEditors) {
			if (editor.getTaggingPresetItem()!=null) {
				hasItem = true;
			}
		}
		if (!hasItem) {
			errors.add(tr("Tag list is empty."));
		}
		return errors;
	}
	
	/*
	 * Generate new TaggingPreset
	 */
	private EasyPreset applyToPreset(final EasyPreset src) {
		EasyPreset preset = src.clone();
		preset.name = uiPresetName.getText();
		preset.data.clear();
		if (iconPath!=null) {
			preset.setIcon(iconPath);
		}
		// Add "Label"
		if (uiIncludeName.isSelected()) {
			Label label = new Label();
			label.text = uiPresetName.getText();
			preset.data.add(label);
		}
		List<TagEditor> tagEditors = tagsPane.getTagEditors();
		for (TagEditor editor : tagEditors) {
			TaggingPresetItem item = editor.getTaggingPresetItem();
			if (item!=null) {
				preset.data.add(item);
			}
		}
		// Add link
		if (uiURL.getText()!=null && !uiURL.getText().isEmpty()) {
			Link link = new Link();
			link.href = uiURL.getText();
			preset.data.add(link);
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
