package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.data.PresetsEntry;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.GBC;

public class ExportDialog extends ExtendedDialog {
	private static final long serialVersionUID = -1147760276640641360L;
	EasyPresets presets;
	
	public ExportDialog (EasyPresets presets) {
		super(MainApplication.getMainFrame(), tr("Export"));
		this.presets = presets;
		initUI();
	}

	JLabel alertLabel;
	static class PresetWrapper {
		JCheckBox checkbox;
		JLabel  label;
		PresetsEntry preset;
		public PresetWrapper(PresetsEntry preset) {
			this.preset = preset;
			checkbox = new JCheckBox();
			label = new JLabel();
			label.setIcon(preset.getIcon());
			label.setText(preset.getLocaleName());
		}

		public JCheckBox getCheckbox() {
			return checkbox;
		}

		public Component getLabel() {
			return label;
		}
	}

	List<PresetWrapper> wrappers = new ArrayList<PresetWrapper>();
	ParameterPanelName presetsNamePane = new ParameterPanelName(tr("Presets name"), "", "Custom Presets");
	
	private void initUI() {
		// headerPane:Box
		// headerPane:Box:1 <-- presetsNamePane:Flow:320x30
		final JPanel headerPane = getHeaderPane();
		
		// mainPane:Grid
		// mainPane:Grid:1 <-- JLabel
		// mainPane:Grid:2 <-- listPane:Grid
		// mainPane:Grid:3 <-- alertLabel
		// mainPane:Grid:4 <-- selectAllButton
		// mainPane:Grid:5 <-- deselectAllButton
		final JPanel mainPane = getMainPane();

		// buttonPane:Grid
		// buttonPane:Grid:1 <-- exportButton
		// buttonPane:Grid:2 <-- cancelButton
		final JPanel buttonPane = getButtonPanel();
		
		// basePane:Box
		// basePane:Box:1 <-- headerPane
		// basePane:Box:2 <-- mainPane
		// basePane:Box:3 <-- buttonPane
		JPanel basePanel = new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		basePanel.add(headerPane);
		basePanel.add(mainPane);
		basePanel.add(buttonPane);
		
		// ExportDialog:1 <-- basePane
		setContent(basePanel);
		
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
            public void run() {
                toFront();
            }
        });
	}
	
	/**
	 * panel:Box
	 * panel:Box:1 <-- presetsNamePane:Flow:320x30
	 * 
	 * @return panel 
	 */
	private JPanel getHeaderPane() {
		presetsNamePane.setPreferredSize(new Dimension(320,30));
		String rootName = this.presets.getLocaleName();
		if (!rootName.equals("Custom Presets")) {
			presetsNamePane.setText(rootName);
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(presetsNamePane);
		return panel;
	}
	
	/**
	 * 
	 * mainPane:Grid
	 * mainPane:Grid:1 <-- JLabel
	 * mainPane:Grid:2 <-- listPane
	 * mainPane:Grid:3 <-- alertLabel
	 * mainPane:Grid:4 <-- selectAllButton
	 * mainPane:Grid:5 <-- deselectAllButton
	 * 
	 * @return mainPane
	 */
	private JPanel getMainPane() {
		// mainPane:Grid:2 <-- listPane:Grid
		JPanel listPane = new JPanel(new GridBagLayout());

		// mainPane:Grid:2 <-- listPane:Grid <-- listScroll::320x420
		// mainPane:Grid:2 <-- listPane:Grid <-- listScroll:1 <-- list:Grid
		final JPanel list = new JPanel(new GridBagLayout());
		list.setBackground(Color.WHITE);
		
		PresetsEntry[] array = (PresetsEntry[]) presets.toArray();
        for (int i = 0; i < array.length; i++) {
			PresetWrapper wrapper = new PresetWrapper(array[i]);
			list.add(wrapper.getCheckbox());
			list.add(wrapper.getLabel(), GBC.eol().fill());
			wrappers.add(wrapper);
		}
		
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setPreferredSize(new Dimension(320,420));
		listPane.add(listScroll, GBC.std());

		alertLabel = new JLabel(" ");
		alertLabel.setForeground(Color.RED);
		
		final JButton selectAllButton = new JButton(tr("Check all"));
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll(true);
			}
		});
		
		final JButton deselectAllButton = new JButton(tr("Uncheck all"));
		deselectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll(false);
			}
		});
				
		final JPanel mainPane = new JPanel(new GridBagLayout());
		mainPane.add(new JLabel(tr("Please check presets you want to export.")), GBC.eol().fill());
		mainPane.add(listPane, GBC.eol().fill());
		mainPane.add(alertLabel, GBC.eol().fill());
		mainPane.add(selectAllButton, GBC.std());
		mainPane.add(deselectAllButton, GBC.eol());
		return mainPane;
	}
	
	/**
	 * 
	 * buttonPane:Grid
	 * buttonPane:Grid:1 <-- exportButton
	 * buttonPane:Grid:2 <-- cancelButton
	 * 
	 * @return buttonPane
	 */
	private JPanel getButtonPanel() {
		final JButton exportButton = new JButton(tr("Export"));
		exportButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportSelected();
			}
		});

		final JButton cancelButton = new JButton(tr("Close"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		
		final JPanel buttonPane = new JPanel(new GridBagLayout());
		buttonPane.add(exportButton, GBC.std());
		buttonPane.add(cancelButton, GBC.eol());
		return buttonPane;
	}
	
	
	protected void selectAll(boolean selected) {
		for (PresetWrapper wrapper: wrappers) {
			wrapper.checkbox.setSelected(selected);
		}
		
	}
	private void exportSelected() {
		List<PresetsEntry> selectedPresets = new ArrayList<>();
		for (PresetWrapper wrapper: wrappers) {
			if (wrapper.getCheckbox().isSelected()) {
				selectedPresets.add(wrapper.preset);
			}
		}
		
		alertLabel.setText(" ");
		if (!presetsNamePane.isEnabled()) {
			alertLabel.setText(tr("Illegal presets name."));
			return;
		}

		if (selectedPresets.isEmpty()) {
			alertLabel.setText(tr("No presets are selected."));
			return;
		}
		
		EasyPresets root = new EasyPresets();
		root.setLocaleName(presetsNamePane.getText());
		for (PresetsEntry preset : selectedPresets) {
			root.addElement(preset);
		}
		
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(tr("Save Presets"));
        chooser.setFileFilter(new FileNameExtensionFilter("XML File", "xml"));
        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	root.saveTo(chooser.getSelectedFile());
        }
	}
	
	protected void cancel() {
		dispose();
	}

}
