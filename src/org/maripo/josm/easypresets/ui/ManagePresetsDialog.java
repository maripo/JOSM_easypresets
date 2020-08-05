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
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

import org.maripo.josm.easypresets.data.EasyPreset;
import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.ui.editor.PresetEditorDialog;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;

@SuppressWarnings("serial")
public class ManagePresetsDialog extends ExtendedDialog implements ListSelectionListener {
	private JButton createButton;
	private JButton editButton;
	private JButton copyButton;
	private JButton deleteButton;
	private JButton reorderUpButton;
	private JButton reorderDownButton;
	
	public ManagePresetsDialog (EasyPresets presets) {
		super(MainApplication.getMainFrame(), tr("Manage Custom Presets"));
		this.presets = presets;
		initUI();
	}
	
	/**
	 * 
	 * @param tagMap		selected POI Tags
	 * @param presetTypes	selected POI Types
	 * @param presets		EasyPresets
	 */
	public ManagePresetsDialog (
			Map<String,Map<String, Integer>> tagMap, 
			List<TaggingPresetType> presetTypes,
			EasyPresets presets)
	{
		super(MainApplication.getMainFrame(), tr("Manage Custom Presets"));
		this.defaultTypes = presetTypes;
		this.presets = presets;
		this.tagMap = tagMap;
		initUI();
	}
	
	private EasyPresets presets;
	JList<EasyPreset> list;
	Map<String,Map<String, Integer>> tagMap;
	List<TaggingPresetType> targetTypes = null;			// TypesFromSelection
	protected Collection<TaggingPresetType> defaultTypes;

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
			setBackground(isSelected?selectionBackground:textBackground);
			setForeground(isSelected?selectionForeground:textForeground);
			return this;
		}
	
	}
	private void initUI() {
		list = new JList<EasyPreset>(presets);
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

		createButton = new JButton();
		createButton.setToolTipText(tr("Create Preset"));
		createButton.setIcon(ImageProvider.get("dialogs", "add", ImageSizes.LARGEICON));
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				create();
			}
		});
		createButton.setEnabled(true);
		
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
		buttons.add(createButton, GBC.eol());
		buttons.add(editButton, GBC.eol());
		buttons.add(copyButton, GBC.eol());
		buttons.add(deleteButton, GBC.eol());
		listPane.add(buttons, GBC.eol());
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

	private void export() {
		new ExportDialog(presets).showDialog();
	}
			
	protected void create() {
		int index;
		if (!isSelectionValid()) {
			index = presets.getSize();
		}
		else {
			index = list.getSelectedIndex();
		}
		EasyPreset preset = new EasyPreset();
		presets.insertElementAt(preset, index);
		PresetEditorDialog dialog = new PresetEditorDialog(tagMap, targetTypes, presets);
		dialog.showDialog();
	}

	protected void edit() {
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			EasyPreset preset = getSelectedPreset();
			PresetEditorDialog dialog = new PresetEditorDialog(preset, index, presets);
			dialog.showDialog();
		}
	}

	private boolean copy() {
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			EasyPreset copiedPreset = EasyPreset.copy(getSelectedPreset());
			presets.insertElementAt(copiedPreset, index);
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
			presets.removeElement(getSelectedPreset());
			// TODO : presets.save();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	protected void cancel() {
		dispose();
	}

	boolean isSelectionValid () {
		return !(list.getSelectedIndex() < 0 || list.getSelectedIndex() >= presets.getSize()); 
	}
	
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		int index = list.getSelectedIndex();
		reorderUpButton.setEnabled(index>0);
		reorderDownButton.setEnabled(index < presets.getSize()-1);
		
		if (!isSelectionValid()) {
			createButton.setEnabled(false);
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			return;
		}
		createButton.setEnabled(true);
		editButton.setEnabled(true);
		deleteButton.setEnabled(true);
		copyButton.setEnabled(true);
	}
	
	EasyPreset getSelectedPreset() {
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			return presets.elementAt(index);
		}
		return null;
	}
	
	private void reorderUp () {
		if (!isSelectionValid()) {
			return;
		}
		int index = list.getSelectedIndex();
		presets.moveUp(index);
		list.setSelectedIndex(index-1);
	}
	
	private void reorderDown () {
		if (!isSelectionValid()) {
			return;
		}
		int index = list.getSelectedIndex();
		presets.moveDown(index);
		list.setSelectedIndex(index+1);
	}
}
