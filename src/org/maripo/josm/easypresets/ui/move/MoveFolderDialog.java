package org.maripo.josm.easypresets.ui.move;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

public class MoveFolderDialog extends ExtendedDialog {
	private static final long serialVersionUID = -5956713655196705733L;
	PresetsEntry entry;
	EasyPresets parent;
	
	JLabel alertLabel;
	List<GroupWrapper> wrappers = new ArrayList<>();
		
	public MoveFolderDialog (EasyPresets parent, PresetsEntry entry) {
		super(MainApplication.getMainFrame(), tr("Organize"));
		this.entry = entry;
		this.parent = parent;
		initUI();
	}

	static class GroupWrapper {
		JCheckBox checkbox;
		JLabel  label;
		PresetsEntry entry;
		
		public GroupWrapper(PresetsEntry entry) {
			this.entry = entry;
			checkbox = new JCheckBox();
			label = new JLabel();
			label.setIcon(this.entry.getIcon());
			label.setText(this.entry.getLocaleName());
		}

		public JCheckBox getCheckbox() {
			return checkbox;
		}

		public Component getLabel() {
			return label;
		}
	}

	private void initUI() {
		// mainPane:Grid
		// mainPane:Grid:1 -- JLabel
		// mainPane:Grid:2 -- listPane:Grid
		// mainPane:Grid:3 -- alertLabel
		final JPanel mainPane = getMainPane();

		// buttonPane:Grid
		// buttonPane:Grid:1 -- moveButton
		// buttonPane:Grid:2 -- cancelButton
		final JPanel buttonPane = getButtonPanel();
		
		// basePane:Box
		// basePane:Box:1 -- mainPane
		// basePane:Box:2 -- buttonPane
		JPanel basePanel = new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		basePanel.add(mainPane);
		basePanel.add(buttonPane);
		
		// ExportDialog:1 -- basePane
		setContent(basePanel);
		
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
            public void run() {
                toFront();
            }
        });
	}
	
	/**
	 * 
	 * mainPane:Grid
	 * mainPane:Grid:1 -- JLabel
	 * mainPane:Grid:2 -- listPane
	 * mainPane:Grid:3 -- alertLabel
	 * 
	 * @return mainPane
	 */
	private JPanel getMainPane() {
		JPanel listPane = new JPanel(new GridBagLayout());

		final JPanel list = new JPanel(new GridBagLayout());
		list.setBackground(Color.WHITE);
		
		PresetsEntry[] array = (PresetsEntry[])parent.toArray();
        for (PresetsEntry ent : array) {
        	if (ent instanceof EasyPresets) {
            	GroupWrapper wrapper = new GroupWrapper(ent);
    			list.add(wrapper.getCheckbox());
    			list.add(wrapper.getLabel(), GBC.eol().fill());
    			wrappers.add(wrapper);
        	}
		}
		
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setPreferredSize(new Dimension(320,420));
		listPane.add(listScroll, GBC.std());

		alertLabel = new JLabel(" ");
		alertLabel.setForeground(Color.RED);
		
		final JPanel mainPane = new JPanel(new GridBagLayout());
		mainPane.add(new JLabel(tr("Please select a destination group.")), GBC.eol().fill());
		mainPane.add(listPane, GBC.eol().fill());
		mainPane.add(alertLabel, GBC.eol().fill());
		return mainPane;
	}
	
	/**
	 * 
	 * buttonPane:Grid
	 * buttonPane:Grid:1 -- exportButton
	 * buttonPane:Grid:2 -- cancelButton
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
	
	private void exportSelected() {
		List<PresetsEntry> selectedPresets = new ArrayList<>();
		for (GroupWrapper wrapper: wrappers) {
			if (wrapper.getCheckbox().isSelected()) {
				selectedPresets.add(wrapper.entry);
			}
		}
		
		alertLabel.setText(" ");
		if (selectedPresets.isEmpty()) {
			alertLabel.setText(tr("No presets are selected."));
			return;
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
