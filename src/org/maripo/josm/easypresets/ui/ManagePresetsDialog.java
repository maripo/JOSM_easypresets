package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.editor.PresetEditorDialog;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.tools.GBC;

public class ManagePresetsDialog extends ExtendedDialog implements ListSelectionListener {
	private JButton deleteButton;
	private JButton editButton;

	public ManagePresetsDialog () {
		super(Main.parent, tr("Manage Custom Presets"));
		initUI();
	}
	TaggingPreset[] presets;
	private TaggingPreset selectedPreset;
	JList<TaggingPreset> list;
	private static class PresetRenderer extends JLabel implements ListCellRenderer<TaggingPreset> {
	    private final static Color selectionForeground;
	    private final static Color selectionBackground;
	    private final static Color textForeground;
	    private final static Color textBackground;
	    static {
	        selectionForeground = UIManager.getColor("Tree.selectionForeground");
	        selectionBackground = UIManager.getColor("Tree.selectionBackground");
	        textForeground = UIManager.getColor("Tree.textForeground");
	        textBackground = UIManager.getColor("Tree.textBackground");
	    }

		@Override
		public Component getListCellRendererComponent(JList<? extends TaggingPreset> list, TaggingPreset preset,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText(preset.getName());
			setOpaque(true);
			setBackground((isSelected)?selectionBackground:textBackground);
			setForeground((isSelected)?selectionForeground:textForeground);
			return this;
		}
	
	}
	private void initUI() {
		presets = EasyPresets.getInstance().getPresets().toArray(new TaggingPreset[0]);
		list = new JList(EasyPresets.getInstance().getPresets().toArray());
		list.setCellRenderer(new PresetRenderer());
		final JPanel mainPane = new JPanel(new GridBagLayout());
		


		final JButton exportButton = new JButton(tr("Export"));
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		mainPane.add(exportButton, GBC.eol().anchor(GridBagConstraints.EAST));
		
		mainPane.add(list, GBC.eol().fill(GBC.HORIZONTAL));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		
		editButton = new JButton(tr("Edit"));
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		});
		editButton.setEnabled(false);

		deleteButton = new JButton(tr("Delete"));
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		deleteButton.setEnabled(false);

		final JButton cancelButton = new JButton(tr("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		mainPane.add(editButton);
		mainPane.add(deleteButton);
		mainPane.add(cancelButton);
		setContent(mainPane);
	}

	private void export() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(tr("Save Presets"));
        chooser.setFileFilter(new FileNameExtensionFilter("XML File", "xml"));
        int returnVal = chooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	EasyPresets.getInstance().saveTo(chooser.getSelectedFile());
        }
	}
			
	protected void edit() {
		// Open 
		if (selectedPreset!=null) {
			new PresetEditorDialog(selectedPreset).showDialog();
			dispose();
		}
	}
	protected void delete() {
		if (selectedPreset!=null) {
			EasyPresets.getInstance().delete(selectedPreset);
			dispose();
		}
		
	}
	protected void cancel() {
		dispose();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getFirstIndex() < 0) {
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			return;
		}
		editButton.setEnabled(true);
		deleteButton.setEnabled(true);
		select(presets[e.getFirstIndex()]);
	}

	private void select(TaggingPreset preset) {
		this.selectedPreset = preset;
		
	}
}
