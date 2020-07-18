package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maripo.josm.easypresets.EasyPresetsPlugin;
import org.maripo.josm.easypresets.data.EasyPreset;
import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.editor.PresetEditorDialog;
import org.maripo.josm.easypresets.ui.editor.PresetEditorDialog.PresetEditorDialogListener;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;

@SuppressWarnings("serial")
public class ManagePresetsDialog extends ExtendedDialog implements ListSelectionListener,
	PresetEditorDialogListener {
	private JButton editButton;
	private JButton copyButton;
	private JButton deleteButton;
	private JButton reorderUpButton;
	private JButton reorderDownButton;

	public ManagePresetsDialog () {
		super(MainApplication.getMainFrame(), tr("Manage Custom Presets"));
		presets = EasyPresets.getInstance();
		initUI();
	}
	
	private EasyPresets presets;
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
			setIcon(preset.getIcon());
			setText(preset.getName());
			setOpaque(true);
			setBackground((isSelected)?selectionBackground:textBackground);
			setForeground((isSelected)?selectionForeground:textForeground);
			return this;
		}
	
	}
	private void initUI() {
		list = new JList<TaggingPreset>(EasyPresets.getInstance().getModel());
		list.setCellRenderer(new PresetRenderer());
		final JPanel mainPane = new JPanel(new GridBagLayout());
		
		final JButton exportButton = new JButton(tr("Export"));
		exportButton.setToolTipText(tr("Export custom presets as a local XML file"));
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		mainPane.add(exportButton, GBC.eol().anchor(GridBagConstraints.EAST));

		final JPanel listPane = new JPanel(new GridBagLayout());
		final JPanel buttons = new JPanel(new GridBagLayout());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount()==2) {
					edit();
				}
			}
		});
		
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setPreferredSize(new Dimension(320,420));
		listPane.add(listScroll, GBC.std());
		
		reorderUpButton = new JButton();
		reorderUpButton.setIcon(ImageProvider.get("dialogs", "up", ImageSizes.LARGEICON));
		reorderUpButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				reorderUp();
			}});
		
		reorderDownButton = new JButton();
		reorderDownButton.setIcon(ImageProvider.get("dialogs", "down", ImageSizes.LARGEICON));
		reorderDownButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				reorderDown();
			}});
		
		reorderUpButton.setEnabled(false);
		reorderDownButton.setEnabled(false);
		reorderUpButton.setToolTipText(tr("Move up"));
		reorderDownButton.setToolTipText(tr("Move down"));

		
		editButton = new JButton();
		editButton.setToolTipText(tr("Edit"));
		editButton.setIcon(ImageProvider.get("dialogs", "edit", ImageSizes.LARGEICON));
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				edit();
			}
		});
		editButton.setEnabled(false);
		
		copyButton = new JButton();
		copyButton.setToolTipText(tr("Copy"));
		copyButton.setIcon(ImageProvider.get("copy", ImageSizes.LARGEICON));
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});
		copyButton.setEnabled(false);
		

		deleteButton = new JButton();
		deleteButton.setToolTipText(tr("Delete"));
		deleteButton.setIcon(ImageProvider.get("dialogs", "delete", ImageSizes.LARGEICON));
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (confirmDelete()) {
					delete();
				}
			}
		});
		deleteButton.setEnabled(false);

		buttons.add(reorderUpButton, GBC.eol());
		buttons.add(reorderDownButton, GBC.eol());
		buttons.add(editButton, GBC.eol());
		buttons.add(copyButton, GBC.eol());
		buttons.add(deleteButton, GBC.eol());
		listPane.add(buttons, GBC.eol().fill());
		mainPane.add(listPane, GBC.eol().fill());

		final JButton cancelButton = new JButton(tr("Close"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		mainPane.add(cancelButton, GBC.eol());
		
		setContent(mainPane);
	}

	private void refreshList(String msg) {
		//presets = EasyPresets.getInstance().getPresets().toArray(new TaggingPresEasyPresetsPluginet[0]);
		System.out.println(msg);
		//list.clearSelection();
		//list.setListData(presets);
	}

	private void export() {
		new ExportDialog().showDialog();
	}
			
	protected void edit() {
		// Open 
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			new PresetEditorDialog(getSelectedPreset(), index).showDialog(this);
		}
	}

	private boolean copy() {
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			TaggingPreset copiedPreset = EasyPreset.copy(getSelectedPreset());
			presets.getModel().insertElementAt(copiedPreset, index);
			//presets.isDirty = true;
			refreshList("ManagePresetsDialog->copy()");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean confirmDelete() {
		ExtendedDialog dialog = new ExtendedDialog(
			MainApplication.getMainFrame(),
			tr("Delete"),
			tr("Delete"),
			tr("Cancel")
		);
		dialog.setContent(tr("Are you sure you want to delete \"{0}\"?",getSelectedPreset().getName()));
		dialog.setButtonIcons("ok", "cancel");
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setAlwaysOnTop(true);
		dialog.setupDialog();
		dialog.setVisible(true);
		SwingUtilities.invokeLater(dialog::toFront);
		return dialog.getValue() == 1;
	}
	
	
	private void delete() {
		if (isSelectionValid()) {
			presets.getModel().removeElement(getSelectedPreset());
			presets.save();
			EasyPresetsPlugin.groupMenu.updatePresetListMenu(presets);
			refreshList("ManagePresetsDialog->delete()");
		}
	}

	@Override
	public void dispose() {
		presets.saveIfNeeded();
		super.dispose();
	}
	
	protected void cancel() {
		dispose();
	}

	boolean isSelectionValid () {
		return !(list.getSelectedIndex() < 0 || list.getSelectedIndex() >= presets.getModel().getSize()); 
	}
	
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		int index = list.getSelectedIndex();
		reorderUpButton.setEnabled(index>0);
		reorderDownButton.setEnabled(index < presets.getModel().getSize()-1);
		
		if (!isSelectionValid()) {
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			return;
		}
		editButton.setEnabled(true);
		deleteButton.setEnabled(true);
		copyButton.setEnabled(true);
	}
	
	TaggingPreset getSelectedPreset() {
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			return presets.getModel().elementAt(index);
		}
		return null;
	}
	
	private void reorderUp () {
		if (!isSelectionValid()) {
			return;
		}
		int index = list.getSelectedIndex();
		presets.moveUp(index);
		refreshList("ManagePresetsDialog->reorderUp()");
		list.setSelectedIndex(index-1);
	}
	
	private void reorderDown () {
		if (!isSelectionValid()) {
			return;
		}
		int index = list.getSelectedIndex();
		presets.moveDown(index);
		refreshList("ManagePresetsDialog->reorderDown()");
		list.setSelectedIndex(index+1);
	}

	/* Implementation of ManagePresetsDialogListener */
	@Override
	public void onCancel() {
		// Do nothing
		refreshList("ManagePresetsDialog->onCancel()");
	}

	@Override
	public void onSave() {
		refreshList("ManagePresetsDialog->onSave()");
	}
}
