package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import org.openstreetmap.josm.gui.tagging.presets.items.Key;
import org.openstreetmap.josm.gui.tagging.presets.items.Label;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;

import javafx.geometry.Insets;
/**
 * Editor dialog to create or edit a custom preset
 * @author maripo
 *
 */
public class PresetEditorDialog extends ExtendedDialog {
	
	private List<AbstractTagConf> tagConfs;
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
		super(Main.parent, tr("New Preset"));
		this.defaultTypes = presetTypes;

		tagConfs = new ArrayList<AbstractTagConf>();
        for (final String key: tagMap.keySet()) {
        	TagConf conf = new TagConf(key, tagMap.get(key));
        	tagConfs.add(conf);
        }
        name = "";
		initUI();
	}

	/**
	 * Edit existing preset (Initialize with existing TaggingPreset object)
	 * @param selectedPreset
	 */
	public PresetEditorDialog(TaggingPreset preset) {
		super(Main.parent, tr("Edit Preset"));
		name = preset.name;
		this.presetToEdit = preset;
		defaultTypes = preset.types;
		tagConfs = new ArrayList<AbstractTagConf>();
		// Select all
		for (final TaggingPresetItem item: preset.data) {
			TagConf conf = TagConf.create(item);
			if (conf!=null) {
				tagConfs.add(conf);
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
		mainPane.add(new JLabel("Preset Name"));
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
        listPane.add(new JLabel(tr("Use")));
        listPane.add(new JLabel(tr("Type")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.CENTER));
        listPane.add(new JLabel(tr("Key")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.CENTER));
        listPane.add(new JLabel(tr("Value")), GBC.eol().insets(5, 0, 0, 0).fill(GBC.HORIZONTAL));
        
        for (AbstractTagConf conf: tagConfs) {
        	conf.appendUI(listPane);
        }
        
        mainPane.add(new JScrollPane(listPane), GBC.eol().fill(GBC.HORIZONTAL).insets(0, 0, 0, 15));
        uiXML = new JTextArea();
        uiXML.setRows(tagConfs.size() + 1);

        JButton addTagButton = new JButton("Add Tag");
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
	private static final String TYPE_FIXED = "Fixed value";
	private static final String TYPE_TEXTBOX = "Textbox";
	private static final String TYPE_SELECTION = "Selection";
	private static final String[] TYPE_OPTIONS = {TYPE_FIXED, TYPE_TEXTBOX/*, TYPE_SELECTION*/}; 
	static abstract class AbstractTagConf {
		protected JCheckBox uiInclude;
		protected JTextField uiValue;
		// Dropbox to select types (Fixed value, Textbox, Selection)
		protected JComboBox<String> uiType;

		public AbstractTagConf() {
			uiInclude = new JCheckBox();
			uiType = new JComboBox<String>();
			for (String label: TYPE_OPTIONS) {
				uiType.addItem(label);
			}
			uiValue = new JTextField();
		}
		public void appendUI(JPanel pane) {
			pane.add(uiInclude,GBC.std().anchor(GridBagConstraints.CENTER));
			pane.add(uiType, GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.CENTER));
			pane.add(getUIKey(), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.CENTER));
			pane.add(uiValue, GBC.eol().insets(5, 0, 0, 0).fill(GBC.HORIZONTAL));
		}

		public TaggingPresetItem getTaggingPresetItem() {
			if (!uiInclude.isSelected() || getKey().isEmpty()) {
				return null;
			}
			if (TYPE_TEXTBOX.equals(uiType.getSelectedItem())) {
				return createTextItem();
			}
			else {
			// else if (TYPE_FIXED.equals(uiType.getSelectedItem())) {
				return createKeyItem();
			}
		}
		protected abstract JComponent getUIKey();
		protected abstract String getKey();

		/**
		 * Generate editable text field
		 * @return "Key" type item 
		 */
		private TaggingPresetItem createTextItem() {
			Text item = new Text();
			item.key = getKey();
			item.default_ = uiValue.getText();
			item.text = getKey();
			return item;
		}
		/**
		 * Generate fixed key-value
		 * @return "Key" type item
		 */
		private TaggingPresetItem createKeyItem() {
			Key item = new Key();
			item.key = getKey();
			item.text = getKey();
			item.value = uiValue.getText();
			return item;
		}
	}
	static class TagConfWithEditableKey extends AbstractTagConf {

		protected JTextField uiKeyInput;
		public TagConfWithEditableKey () {
			uiKeyInput = new JTextField(8);
		}
		@Override
		protected JComponent getUIKey() {
			return uiKeyInput;
		}
		@Override
		protected String getKey() {
			return uiKeyInput.getText();
		}
		
	}
	static class TagConf extends AbstractTagConf {
		protected JLabel uiKeyFixed;
		protected String key;

		public TagConf (String key, Map<String, Integer> map) {
			this();
			this.key = key;
			uiInclude.setSelected(true);
			uiKeyFixed.setText(key);
			
			if (map.size() > 0) {
				Iterator<String> ite = map.keySet().iterator();
				this.uiValue.setText(ite.next());
			}
		}
		public static TagConf create(TaggingPresetItem item) {
			if (item instanceof Key || item instanceof Text) {
				return new TagConf(item);
			}
			return null;
		}
		
		private TagConf (TaggingPresetItem item) {
			this();
			uiInclude.setSelected(true);
			if (item instanceof Key) {
				// Fixed field
				Key keyItem = (Key)item;
				key = ((Key) item).key;
				uiValue.setText(keyItem.value);
				uiType.setSelectedItem(TYPE_FIXED);
			} else if (item instanceof Text) {
				// Editable field
				Text text = (Text)item;
				key = text.key;
				uiValue.setText(text.default_);
				uiType.setSelectedItem(TYPE_TEXTBOX);
			}
			// TODO combobox
			uiKeyFixed.setText(key);
		}
		public TagConf() {
			super();
			uiKeyFixed = new JLabel();
		}
		@Override
		protected JComponent getUIKey() {
			return uiKeyFixed;
		}
		@Override
		protected String getKey() {
			return key;
		}

	}


	protected void addTag () {
		TagConfWithEditableKey conf = new TagConfWithEditableKey();
		tagConfs.add(conf);
		conf.appendUI(listPane);
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
		for (AbstractTagConf conf : tagConfs) {
			TaggingPresetItem item = conf.getTaggingPresetItem();
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
