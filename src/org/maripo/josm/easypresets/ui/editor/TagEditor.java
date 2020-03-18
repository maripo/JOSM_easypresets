package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.editor.ValuesEditorDialog.ValuesEditorDialogListener;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.items.Check;
import org.openstreetmap.josm.gui.tagging.presets.items.Combo;
import org.openstreetmap.josm.gui.tagging.presets.items.ComboMultiSelect;
import org.openstreetmap.josm.gui.tagging.presets.items.Key;
import org.openstreetmap.josm.gui.tagging.presets.items.KeyedItem;
import org.openstreetmap.josm.gui.tagging.presets.items.MultiSelect;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.openstreetmap.josm.tools.GBC;

public class TagEditor {

	/**
	 * Input field for individual field.
	 * It supports key (fixed value), text, select, combobox, check and multiselect. 
	 * @author maripo
	 *
	 */
	static abstract class KeyField {
		public abstract Component getUI();

		public abstract String getKey() ;
	}
	static class KeyFieldFixed extends KeyField {
		JLabel label;
		private String key;
		public KeyFieldFixed(String key) {
			super();
			this.key = key;
			label = new JLabel(key);
		}

		@Override
		public Component getUI() {
			return label;
		}

		@Override
		public String getKey() {
			return key;
		}
		
	}
	static class KeyFieldEditable extends KeyField {
		private static final int DEFAULT_COLUMNS = 8;
		JTextField textfield;
		public KeyFieldEditable() {
			super();
			textfield = new JTextField(DEFAULT_COLUMNS);
		}
		@Override
		public Component getUI() {
			return textfield;
		}
		@Override
		public String getKey() {
			return textfield.getText();
		}
		
	}
	
	String inputValues[] = new String[]{};
	/**
	 * Value fields
	 */
	abstract static class ValueField {

		public abstract void appendUI(JPanel pane);

		public abstract void setVisibility(boolean isSelected);

		public abstract void populateDefaultValue(String... values);

		public abstract void applyEditedValues();

		public abstract KeyedItem createItem();

		public boolean hasLabel() {
			return true;
		}
	}
	class ValueFieldFixed extends ValueField {
		JTextField textField;
		private static final int DEFAULT_COLUMNS = 10;
		public ValueFieldFixed () {
			super();
			textField = new JTextField(DEFAULT_COLUMNS);
		}
		@Override
		public void appendUI(JPanel pane) {
			pane.add(textField);
			
		}
		@Override
		public boolean hasLabel() {
			return false;
		}
		@Override
		public void setVisibility(boolean visible) {
			textField.setVisible(visible);
			
		}
		@Override
		public void populateDefaultValue(String... values) {
			if (values.length > 0) {
				textField.setText(values[0]);
			}
		}
		@Override
		public void applyEditedValues() {
			if (inputValues.length==0) {
				inputValues = new String[1];
			}
			inputValues[0] = textField.getText();
		}
		@Override
		public KeyedItem createItem() {
			Key item = new Key();
			if (inputValues!=null && inputValues.length>0) {
				item.value = inputValues[0];
			}
			return item;
		}
	}
	class ValueFieldTextbox extends ValueField {
		JTextField textField;
		JLabel labelDefault;
		private static final int DEFAULT_COLUMNS = 10;
		public ValueFieldTextbox () {
			super();
			textField = new JTextField(DEFAULT_COLUMNS);
			labelDefault = new JLabel("("+tr("Default")+")");
		}
		@Override
		public void appendUI(JPanel pane) {
			pane.add(textField);
			pane.add(labelDefault);
		}
		@Override
		public void setVisibility(boolean visible) {
			textField.setVisible(visible);
			labelDefault.setVisible(visible);
		}
		@Override
		public void populateDefaultValue(String... values) {
			if (values.length > 0) {
				textField.setText(values[0]);
			}
		}
		@Override
		public void applyEditedValues() {
			if (inputValues.length==0) {
				inputValues = new String[1];
			}
			inputValues[0] = textField.getText();
		}
		@Override
		public KeyedItem createItem() {
			Text item = new Text();
			if (inputValues!=null && inputValues.length>0) {
				item.default_ = inputValues[0];
			}
			return item;
		}
	}
	class ValueFieldSelection extends ValueField implements ActionListener, ValuesEditorDialogListener {
		JButton button;
		JLabel label;
		String[] values;
		public ValueFieldSelection () {
			super();
			button = new JButton(tr("Edit options")+"...");
			label = new JLabel();
			button.addActionListener(this);
		}
		@Override
		public void appendUI(JPanel pane) {
			pane.add(label);
			pane.add(button);
			
		}
		@Override
		public void setVisibility(boolean visible) {
			button.setVisible(visible);
			label.setVisible(visible);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// Button is clicked.
			new ValuesEditorDialog(baseDialog, values).setListener(this).showDialog(); 
			
		}
		@Override
		public void populateDefaultValue(String... values) {
			this.values = values;
			if (values.length == 1) {
				label.setText(values[0]);
				label.setToolTipText(null);
			} else {
				label.setText(tr("{0} options", values.length));
				label.setToolTipText(String.join(", ", values));
			}
		}
		@Override
		public void applyEditedValues() {
			inputValues = values;
		}
		protected ComboMultiSelect createEmptyItem() {
			return new Combo();
		}
		protected char getDefaultDelimiter() {
			return ',';
		}
		@Override
		public KeyedItem createItem() {
			ComboMultiSelect item = createEmptyItem();
			// set delimiters
			StringBuilder valueString = new StringBuilder();
			item.delimiter = getDefaultDelimiter();
			for (String value: inputValues) {
				if (valueString.length() > 0) {
					valueString.append(getDefaultDelimiter());
				}
				valueString.append(value.replace(",", "\\,"));
			}
			item.values = valueString.toString();
			return item;
		}
		// Callback of ValuesEditorDialog
		@Override
		public void onInput(String[] values) {
			populateDefaultValue(values);
		}
		@Override
		public void onCancel() {
			// Nothing to do
		}
		
	}
	class ValueFieldMultiselect extends ValueFieldSelection {
		@Override
		protected ComboMultiSelect createEmptyItem() {
			return new MultiSelect();
		}
		@Override
		protected char getDefaultDelimiter() {
			return ';';
		}
	}
	static class ValueFieldCheckbox extends ValueField {
		public ValueFieldCheckbox () {
			super();
		}
		@Override
		public void appendUI(JPanel pane) {
		}

		@Override
		public void setVisibility(boolean isSelected) {
		}

		@Override
		public void populateDefaultValue(String... values) {
		}

		@Override
		public void applyEditedValues() {
			// Do nothing
		}
		@Override
		public KeyedItem createItem() {
			Check item = new Check();
			return item;
		}
		
	}

	private static final String TYPE_FIXED;
	private static final String TYPE_TEXTBOX;
	private static final String TYPE_SELECT;
	private static final String TYPE_MULTISELECT;
	private static final String TYPE_CHECKBOX;
	
	private static final String TYPE_DEFAULT;
	private static final String[] TYPE_OPTIONS;
	static {
		TYPE_FIXED = tr("Fixed value");
		TYPE_TEXTBOX = tr("Textbox");
		TYPE_SELECT = tr("Selection");
		TYPE_CHECKBOX = tr("Checkbox");
		TYPE_MULTISELECT = tr("Multiselect");
		TYPE_DEFAULT = TYPE_FIXED;
		TYPE_OPTIONS = new String[]{
				TYPE_FIXED, TYPE_TEXTBOX, TYPE_SELECT, TYPE_CHECKBOX, TYPE_MULTISELECT};
	}

	private JCheckBox uiInclude;
	private JComboBox<String> uiType;
	private KeyField keyField;
	private JPanel valuePanel;
	Map <String, ValueField> fields;
	private ExtendedDialog baseDialog;

	
	public TagEditor(ExtendedDialog baseDialog) {
		uiLabel = new JTextField(8);

		uiLabelNA = new JLabel("("+tr("Unavailable")+")");
		uiLabelNA.setToolTipText(tr("A field of this type doesn't have a label."));
		uiLabelNA.setForeground(Color.GRAY);
		uiLabelWrapper = new JPanel(new GridBagLayout());
		//uiLabelWrapper.setPreferredSize(new Dimension(108,28));
		uiLabelWrapper.add(uiLabelNA);
		uiLabelWrapper.add(uiLabel, GBC.std().insets(0, 0, 0, 0).grid(0, 0));
		this.baseDialog = baseDialog;
		uiInclude = new JCheckBox();
		uiInclude.setSelected(true);
		uiType = new JComboBox<String>();
		valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fields = new HashMap<String, ValueField>();
		fields.put(TYPE_FIXED, new ValueFieldFixed());
		fields.put(TYPE_TEXTBOX, new ValueFieldTextbox());
		fields.put(TYPE_SELECT, new ValueFieldSelection());
		fields.put(TYPE_MULTISELECT, new ValueFieldMultiselect());
		fields.put(TYPE_CHECKBOX, new ValueFieldCheckbox());
		for (String label: TYPE_OPTIONS) {
			uiType.addItem(label);
			fields.get(label).appendUI(valuePanel);
		}
	}

	private String prevSelectedType = null;
	private JPanel parentPane;
	protected void onSelectedTypeChange() {
		String selectedType = (String) uiType.getSelectedItem();
		switchType(selectedType);
	}

	private void switchType(String selectedType) {
		for (String type: TYPE_OPTIONS) {
			boolean isSelected = type.equals(selectedType);
			ValueField field = fields.get(type);
			field.setVisibility(isSelected);
		}
		ValueField selectedValueField = getSelectedValueField();
		if (prevSelectedType!=null) {
			fields.get(prevSelectedType).applyEditedValues();
			selectedValueField.populateDefaultValue(inputValues);
		}
		uiLabel.setVisible(selectedValueField.hasLabel());
		uiLabelNA.setVisible(!selectedValueField.hasLabel());
		if (uiLabel.getParent()!=null) {
			uiLabel.getParent().revalidate();
		}
		prevSelectedType = selectedType;
		if (parentPane!=null) {
			parentPane.revalidate();
		}
	}

	private ValueField getSelectedValueField () {
		return fields.get(uiType.getSelectedItem());
	}
	
	/**
	 * Create empty config (called by clicking "New tag")
	 * @return
	 */
	public static TagEditor create(ExtendedDialog baseDialog) {
		TagEditor instance = new TagEditor(baseDialog);
		instance.keyField = new KeyFieldEditable();
		instance.switchType(TYPE_DEFAULT);
		instance.initUI();
		return instance;
	}
	/**
	 * Init with key and value map
	 * @param key
	 * @param map
	 * @return Created instance
	 */
	public static TagEditor create(ExtendedDialog baseDialog, String key, Map<String, Integer> map) {
		TagEditor instance = new TagEditor(baseDialog);
		instance.keyField = new KeyFieldFixed(key);
		instance.switchType(TYPE_DEFAULT);
		instance.uiType.setSelectedItem(TYPE_DEFAULT);
		if (!map.isEmpty()) {
			String firstKey = map.keySet().iterator().next();
			instance.uiLabel.setText(EasyPresets.getInstance().getLabelFromExistingPresets(key));
			instance.getSelectedValueField().populateDefaultValue(firstKey);
		}
		instance.initUI();
		return instance;
	}

	/**
	 * Init with existing TaggingPresetItem
	 * @param item
	 * @return  Created instance Return null if the preset is not supported)
	 */
	public static TagEditor create(ExtendedDialog baseDialog, TaggingPresetItem item) {
		TagEditor instance = new TagEditor(baseDialog);
		if (!(item instanceof KeyedItem)) {
			return null;
		}
		KeyedItem tag = (KeyedItem)item;
		String type;
		instance.uiLabel.setText(tag.text);
		if (tag instanceof Text) {
			type = TYPE_TEXTBOX;
		} else if (tag instanceof Key) {
			type = TYPE_FIXED;
		} else if (tag instanceof Combo) {
			type = TYPE_SELECT;
		} else if (tag instanceof MultiSelect) {
			type = TYPE_MULTISELECT;
		} else if (tag instanceof Check) {
			type = TYPE_CHECKBOX;
		} else {
			return null;
		}
		instance.uiType.setSelectedItem(type);
		instance.switchType(type);
		instance.getSelectedValueField().populateDefaultValue(tag.getValues().toArray(new String[0]));
		instance.keyField = new KeyFieldFixed(tag.key);
		instance.initUI();
		return instance;
	}

	private void initUI() {
		uiType.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onSelectedTypeChange();
			}
		});
	}


	public void _appendUI(JPanel pane) {
	}

	public TaggingPresetItem getTaggingPresetItem() {
		if (!uiInclude.isSelected() || keyField.getKey().isEmpty()) {
			return null;
		}
		getSelectedValueField().applyEditedValues();
		KeyedItem item = getSelectedValueField().createItem();
		item.key = keyField.getKey();
		if (getSelectedValueField().hasLabel()) {
			if (!uiLabel.getText().isEmpty()) {
				item.text = uiLabel.getText();
			}
		}
		return item;
	}

	/* Getters of UI components */
	public Component getUiInclude() {
		return uiInclude;
	}

	public Component getUiType() {
		return uiType;
	}

	public Component getUiKey() {
		return keyField.getUI();
	}

	public Component getUiValue() {
		return valuePanel;
	}
	JTextField uiLabel; // Input field for label
	JLabel uiLabelNA; // Appears when label is not supported
	JPanel uiLabelWrapper;
	public Component getUiLabel() {
		return uiLabelWrapper;
	}

}
