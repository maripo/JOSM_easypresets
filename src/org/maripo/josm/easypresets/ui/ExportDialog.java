package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maripo.josm.easypresets.data.EasyPreset;
import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.data.PresetsEntry;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
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
		TaggingPreset preset;
		public PresetWrapper(TaggingPreset preset) {
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
	private void initUI() {
		JPanel listPane = new JPanel(new GridBagLayout());
		final JPanel mainPane = new JPanel(new GridBagLayout());
		mainPane.add(new JLabel(tr("Please check presets you want to export.")), GBC.eol().fill());

		final JPanel list = new JPanel(new GridBagLayout());
		list.setBackground(Color.WHITE);
		EasyPreset[] array = (EasyPreset[]) presets.toArray();
        for (int i = 0; i < array.length; i++) {
			PresetWrapper wrapper = new PresetWrapper(array[i]);
			list.add(wrapper.getCheckbox());
			list.add(wrapper.getLabel(), GBC.eol().fill());
			wrappers.add(wrapper);
		}
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setPreferredSize(new Dimension(320,420));
		
		listPane.add(listScroll, GBC.std());
		mainPane.add(listPane, GBC.eol().fill());
		
		final JButton selectAllButton = new JButton(tr("Check all"));
		final JButton deselectAllButton = new JButton(tr("Uncheck all"));
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll(true);
			}
		});
		deselectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAll(false);
			}
		});
		
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


		mainPane.add(selectAllButton, GBC.std());
		mainPane.add(deselectAllButton, GBC.eol());
		alertLabel = new JLabel(" ");
		alertLabel.setForeground(Color.RED);
		mainPane.add(alertLabel, GBC.eol().fill());

		mainPane.add(exportButton, GBC.std());
		mainPane.add(cancelButton, GBC.eol());
		
		setContent(mainPane);
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
            public void run() {
                toFront();
            }
        });
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
				selectedPresets.add(new EasyPreset(wrapper.preset));
			}
		}
		
		if (selectedPresets.isEmpty()) {
			alertLabel.setText(tr("No presets are selected."));
			return;
		} else {
			alertLabel.setText(" ");
		}
		
		EasyPresets root = new EasyPresets();
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
