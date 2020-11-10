package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maripo.josm.easypresets.data.EasyPreset;
import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.data.EasySeparator;
import org.maripo.josm.easypresets.data.PresetsEntry;
import org.maripo.josm.easypresets.ui.editor.PresetEditorDialog;
import org.maripo.josm.easypresets.ui.move.MoveFolderDialog;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;

@SuppressWarnings("serial")
public class ManagePresetsDialog extends ExtendedDialog implements ListSelectionListener {
	private JTextField uiGroupName;
	private JButton folderButton;
	private JButton organizeButton;
	private JButton createButton;
	private JButton separatorButton;
	private JButton editButton;
	private JButton copyButton;
	private JButton deleteButton;
	private JButton reorderUpButton;
	private JButton reorderDownButton;
	
	public ManagePresetsDialog (EasyPresets presets) {
		super(MainApplication.getMainFrame(), tr("Manage Custom Presets"));
		this.targetTypes = new ArrayList<TaggingPresetType>();
		this.presets = presets;
		this.tagMap = new TreeMap<String, Map<String, Integer>>();
		this.parent = null;
		this.index = 0;
		initUI();
	}
	
	/**
	 * 
	 * @param tagMap		selected POI Tags
	 * @param presetTypes	selected POI Types
	 * @param presets		EasyPresets
	 * @param parent		uppper class EasyPresets
	 * @param index			index of list
	 */
	public ManagePresetsDialog (
			Map<String,Map<String, Integer>> tagMap, 
			List<TaggingPresetType> presetTypes,
			EasyPresets presets,
			EasyPresets parent, int index)
	{
		super(MainApplication.getMainFrame(), tr("Manage Custom Presets"));
		this.tagMap = ((tagMap == null) ? new TreeMap<String, Map<String, Integer>>() : tagMap);
		this.targetTypes = ((presetTypes == null) ? new ArrayList<TaggingPresetType>() : presetTypes);
		this.presets = presets;
		this.parent = parent;
		this.index = index;
		initUI();
	}
	
	private EasyPresets parent;
	private EasyPresets presets;
	int index;
	JList<PresetsEntry> list;
	Map<String,Map<String, Integer>> tagMap;
	List<TaggingPresetType> targetTypes;			// TypesFromSelection

	private void initUI() {
		list = new JList<PresetsEntry>(this.presets);
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

		JLabel label = new JLabel(tr("Preset Group Name"));

		uiGroupName = new JTextField(16);
		uiGroupName.setText(this.presets.getLocaleName());
		uiGroupName.setEditable(this.parent != null);
		mainPane.add(label, GBC.std().insets(0, 0, 0, 10));
		mainPane.add(uiGroupName, GBC.eol().insets(0, 0, 0, 10));
		
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

		folderButton = new JButton();
		folderButton.setToolTipText(tr("Create a group"));
		ImageProvider img = new ImageProvider("folder_create");
		img.setSize(ImageSizes.LARGEICON);
		folderButton.setIcon(img.get());
		folderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addFolder();
			}
		});
		folderButton.setEnabled(true);
		
		organizeButton = new JButton();
		organizeButton.setToolTipText(tr("Organize"));
		organizeButton.setIcon(ImageProvider.get("folder_move", ImageSizes.LARGEICON));
		organizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					organize();
				} catch (CloneNotSupportedException e1) {
					Logging.error(e1);
				}
			}
		});
		organizeButton.setEnabled(false);
		
		createButton = new JButton();
		createButton.setToolTipText(tr("Create a preset"));
		createButton.setIcon(ImageProvider.get("easypresets_add", ImageSizes.LARGEICON));
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				create();
			}
		});
		createButton.setEnabled(true);
		
		// 'separator'
		separatorButton = new JButton();
		separatorButton.setToolTipText(tr("Create a separator"));
		separatorButton.setIcon(ImageProvider.get("preferences", "separator", ImageSizes.LARGEICON));
		separatorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				separator();
			}
		});
		separatorButton.setEnabled(true);
		
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
		buttons.add(organizeButton, GBC.eol());
		buttons.add(folderButton, GBC.eol());
		buttons.add(createButton, GBC.eol());
		buttons.add(separatorButton, GBC.eol());
		buttons.add(editButton, GBC.eol());
		buttons.add(copyButton, GBC.eol());
		buttons.add(deleteButton, GBC.eol());
		listPane.add(buttons, GBC.eol());
		mainPane.add(listPane, GBC.eol().fill());

		final JButton cancelButton = new JButton(tr("Close"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		mainPane.add(cancelButton, GBC.eol());
		
		setContent(mainPane);
	}

	private void export() {
		new ExportDialog(presets).showDialog();
	}
	
	/*
	 * button action "Folder"
	 */
	protected void addFolder() {
		int i;
		if (!isSelectionValid()) {
			i = presets.getSize();
		}
		else {
			i = list.getSelectedIndex();
		}
		EasyPresets folder = new EasyPresets(presets);
		folder.setName(tr("NewGroup"));
		presets.insertElementAt(folder, i);
		ManagePresetsDialog dialog = new ManagePresetsDialog(this.tagMap, this.targetTypes, folder, presets, i);
		dialog.showDialog();
	}
	
	/*
	 * button action "Organize / Folder_move"
	 */
	protected void organize () throws CloneNotSupportedException {
		if (isSelectionValid()) {
			PresetsEntry preset = getSelectedPreset();
			new MoveFolderDialog(preset).showDialog();
		}
	}

	protected void create() {
		int i;
		if (!isSelectionValid()) {
			i = presets.getSize();
		}
		else {
			i = list.getSelectedIndex();
		}
		EasyPreset preset = new EasyPreset();
		
        preset.types = EnumSet.noneOf(TaggingPresetType.class);
		for (TaggingPresetType type : targetTypes) {
			preset.types.add(type);
		}

		presets.insertElementAt(preset, i);
		PresetEditorDialog dialog = new PresetEditorDialog(preset, tagMap, i, presets);
		dialog.showDialog();
	}

	/**
	 * button action "Create Separator"
	 */
	protected void separator() {
		int i;
		if (!isSelectionValid()) {
			i = presets.getSize();
		}
		else {
			i = list.getSelectedIndex();
		}
		EasySeparator separator = new EasySeparator(parent);
		presets.insertElementAt(separator, i);
	}

	protected void edit() {
		if (isSelectionValid()) {
			int i = list.getSelectedIndex();
			PresetsEntry preset = getSelectedPreset();
			if (preset instanceof EasyPresets) {
				ManagePresetsDialog dialog = new ManagePresetsDialog(this.tagMap, this.targetTypes, (EasyPresets)preset, presets, i);
				dialog.showDialog();
			}
			else if (preset instanceof EasyPreset) {
				PresetEditorDialog dialog = new PresetEditorDialog((EasyPreset)preset, i, presets);
				dialog.showDialog();
			}
		}
	}

	private boolean copy() {
		if (isSelectionValid()) {
			int index = list.getSelectedIndex();
			PresetsEntry copiedPreset = getSelectedPreset().copy();
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
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	
	protected void close() {
		String str = uiGroupName.getText().trim();
		this.presets.setName(str);
		if (this.presets.isRoot()) {
			this.presets.saveTo();
		}
		else {
			this.parent.setElementAt(presets, this.index);
		}
		dispose();
	}

	boolean isSelectionValid () {
		return !(list.getSelectedIndex() < 0 || list.getSelectedIndex() >= presets.getSize()); 
	}
	
	@Override
	public void valueChanged(ListSelectionEvent evt) {
		int i = list.getSelectedIndex();
		reorderUpButton.setEnabled(i > 0);
		reorderDownButton.setEnabled(i < presets.getSize()-1);
		
		if (!isSelectionValid()) {
			folderButton.setEnabled(false);
			createButton.setEnabled(false);
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			organizeButton.setEnabled(false);
			return;
		}
		PresetsEntry obj = getSelectedPreset();
		if (obj instanceof EasySeparator) {
			editButton.setEnabled(false);
			organizeButton.setEnabled(false);
			folderButton.setEnabled(false);
			copyButton.setEnabled(false);
		}
		else {
			editButton.setEnabled(true);
			organizeButton.setEnabled(true);
			folderButton.setEnabled(true);
			copyButton.setEnabled(true);
		}
		createButton.setEnabled(true);
		deleteButton.setEnabled(true);
	}
	
	PresetsEntry getSelectedPreset() {
		if (isSelectionValid()) {
			int i = list.getSelectedIndex();
			return presets.elementAt(i);
		}
		return null;
	}
	
	private void reorderUp () {
		if (!isSelectionValid()) {
			return;
		}
		int i = list.getSelectedIndex();
		presets.moveUp(i);
		list.setSelectedIndex(i-1);
	}
	
	private void reorderDown () {
		if (!isSelectionValid()) {
			return;
		}
		int i = list.getSelectedIndex();
		presets.moveDown(i);
		list.setSelectedIndex(i+1);
	}
}
