package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
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

import org.maripo.josm.easypresets.ui.editor.ValuesEditorDialog.ValuesEditorDialogListener;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.items.Combo;
import org.openstreetmap.josm.gui.tagging.presets.items.Key;
import org.openstreetmap.josm.gui.tagging.presets.items.KeyedItem;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.openstreetmap.josm.tools.GBC;

public class TagEditor {

	/**
	 * Key fields
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
	
	/**
	 * Value fields
	 */
	abstract class ValueField {

		public abstract void appendUI(JPanel pane);

		public abstract void setVisibility(boolean isSelected);

		public abstract void setDefaultValue(String... values);

		public abstract String[] getValues();
	}
	class ValueFieldFixed extends ValueField {
		JTextField textField;
		private static final int DEFAULT_COLUMNS = 12;
		public ValueFieldFixed () {
			super();
			textField = new JTextField(DEFAULT_COLUMNS);
		}
		@Override
		public void appendUI(JPanel pane) {
			pane.add(textField);
			
		}
		@Override
		public void setVisibility(boolean visible) {
			textField.setVisible(visible);
			
		}
		@Override
		public void setDefaultValue(String... values) {
			if (values.length > 0) {
				textField.setText(values[0]);
			}
		}
		@Override
		public String[] getValues() {
			return new String[]{textField.getText()};
		}
	}
	class ValueFieldTextbox extends ValueField {
		JTextField textField;
		JLabel labelDefault;
		private static final int DEFAULT_COLUMNS = 12;
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
		public void setDefaultValue(String... values) {
			if (values.length > 0) {
				textField.setText(values[0]);
			}
		}
		@Override
		public String[] getValues() {
			return new String[]{textField.getText()};
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
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// Button is clicked.
			new ValuesEditorDialog(baseDialog, values).setListener(this).showDialog(); 
			
		}
		@Override
		public void setDefaultValue(String... values) {
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
		public String[] getValues() {
			return values;
		}
		// Callback of ValuesEditorDialog
		@Override
		public void onInput(String[] values) {
			setDefaultValue(values);
		}
		@Override
		public void onCancel() {
			// Nothing to do
		}
	}

	private static final String TYPE_FIXED;
	private static final String TYPE_TEXTBOX;
	private static final String TYPE_SELECTION;
	private static final String TYPE_DEFAULT;
	private static final String[] TYPE_OPTIONS;
	static {
		TYPE_FIXED = tr("Fixed value");
		TYPE_TEXTBOX = tr("Textbox");
		TYPE_SELECTION = tr("Selection");
		TYPE_DEFAULT = TYPE_FIXED;
		TYPE_OPTIONS = new String[]{TYPE_FIXED, TYPE_TEXTBOX, TYPE_SELECTION};
	}

	private JCheckBox uiInclude;
	private KeyField keyField;
	private JComboBox<String> uiType;
	Map <String, ValueField> fields;
	private JPanel valuePanel;
	private ExtendedDialog baseDialog;

	
	public TagEditor(ExtendedDialog baseDialog) {
		this.baseDialog = baseDialog;
		uiInclude = new JCheckBox();
		uiInclude.setSelected(true);
		uiType = new JComboBox<String>();
		valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fields = new HashMap<String, ValueField>();
		fields.put(TYPE_FIXED, new ValueFieldFixed());
		fields.put(TYPE_TEXTBOX, new ValueFieldTextbox());
		fields.put(TYPE_SELECTION, new ValueFieldSelection());
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
		if (prevSelectedType!=null) {
			String[] prevValues = fields.get(prevSelectedType).getValues();
			getSelectedValueField().setDefaultValue(prevValues);
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
			instance.getSelectedValueField().setDefaultValue(firstKey);
		}
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
		if (tag instanceof Text) {
			type = TYPE_TEXTBOX;
		} else if (tag instanceof Key) {
			type = TYPE_FIXED;
		} else if (tag instanceof Combo) {
			type = TYPE_SELECTION;
		} else {
			return null;
		}
		instance.uiType.setSelectedItem(type);
		instance.switchType(type);
		instance.getSelectedValueField().setDefaultValue(tag.getValues().toArray(new String[0]));
		instance.keyField = new KeyFieldFixed(tag.key);
		return instance;
	}

	/**
	 * Create empty config (called by clicking "New tag")
	 * @return
	 */
	public static TagEditor create(ExtendedDialog baseDialog) {
		TagEditor instance = new TagEditor(baseDialog);
		instance.keyField = new KeyFieldEditable();
		instance.switchType(TYPE_DEFAULT);
		return instance;
	}

	public void appendUI(JPanel pane) {
		pane.add(uiInclude,GBC.std().anchor(GridBagConstraints.NORTHWEST));
		pane.add(uiType, GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.WEST));
		pane.add(keyField.getUI(), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.WEST));
		pane.add(valuePanel, GBC.eol().fill(GBC.HORIZONTAL));
		int rowsCount = 1+(pane.getComponentCount() / 4);
		pane.setPreferredSize(new Dimension(620, rowsCount * 32));
		pane.invalidate();
		uiType.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				onSelectedTypeChange();
			}
			
		});
		this.parentPane = pane;
	}

	/**
	 * Generate editable text field
	 * @return "Key" type item 
	 */
	private TaggingPresetItem createTextItem() {
		Text item = new Text();
		item.key = keyField.getKey();
		item.text = keyField.getKey();
		String[] values = getSelectedValueField().getValues();
		if (values!=null && values.length>0) {
			item.default_ = values[0];
		}
		return item;
	}
	/**
	 * Generate fixed key-value
	 * @return "Key" type item
	 */
	private TaggingPresetItem createKeyItem() {
		Key item = new Key();
		item.key = keyField.getKey();
		item.text = keyField.getKey();
		String[] values = getSelectedValueField().getValues();
		if (values!=null && values.length>0) {
			item.value = values[0];
		}
		return item;
	}
	/**
	 * Generate selection type with comma separated values
	 * @return
	 */
	private TaggingPresetItem createSelectionItem() {
		Combo item = new Combo();
		item.key = keyField.getKey();
		item.text = keyField.getKey();
		// set delimiters
		StringBuilder valueString = new StringBuilder();
		item.delimiter = ",";
		for (String value: getSelectedValueField().getValues()) {
			if (valueString.length() > 0) {
				valueString.append(",");
			}
			valueString.append(value.replace(",", "\\,"));
		}
		item.values = valueString.toString();
		return item;
	}

	public TaggingPresetItem getTaggingPresetItem() {
		if (!uiInclude.isSelected() || keyField.getKey().isEmpty()) {
			return null;
		}
		if (TYPE_TEXTBOX.equals(uiType.getSelectedItem())) {
			return createTextItem();
		}
		else if (TYPE_FIXED.equals(uiType.getSelectedItem())) {
			return createKeyItem();
		}
		else if (TYPE_SELECTION.equals(uiType.getSelectedItem())) {
			return createSelectionItem();
		}
		return null;
	}


}
