package org.maripo.josm.easypresets.ui.move;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.maripo.josm.easypresets.data.PresetsEntry;
import org.maripo.josm.easypresets.ui.PresetRenderer;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.GBC;

public class MoveFolderDialog extends ExtendedDialog implements ListSelectionListener {
	private static final long serialVersionUID = -5956713655196705733L;
	PresetsEntry entry;
	EasyPresets parent;
	
	JLabel alertLabel;
	JList<PresetsEntry> list;
	JButton moveParentButton;
	JButton moveButton;

	public MoveFolderDialog (PresetsEntry entry) throws CloneNotSupportedException {
		super(MainApplication.getMainFrame(), tr("Organize"));
		this.entry = entry;
		this.parent = entry.getParent();
		initUI();
	}

	private void initUI() throws CloneNotSupportedException {
		JPanel basePanel = new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));
		basePanel.add(getMainPane());
		basePanel.add(getButtonPanel());
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
	 * @throws CloneNotSupportedException Clone not supported
	 */
	private JPanel getMainPane() throws CloneNotSupportedException {
		EasyPresets groupList = parent.clone();
		groupList.removeAllElements();
		
		PresetsEntry[] array = (PresetsEntry[])parent.toArray();
		for (PresetsEntry ent : array) {
			if (ent instanceof EasyPresets) {
				groupList.addElement(ent);
			}
		}
		
		list = new JList<PresetsEntry>(groupList);
		list.setCellRenderer(new PresetRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		
		JPanel listPane = new JPanel(new GridBagLayout());
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
	 * buttonPane:Grid:0 -- moveParentButton
	 * buttonPane:Grid:1 -- moveButton
	 * buttonPane:Grid:2 -- cancelButton
	 * 
	 * @return buttonPane
	 */
	private JPanel getButtonPanel() {
		moveParentButton = new JButton(tr("Move to upper group"));
		moveParentButton.setEnabled(false);
		moveParentButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveParent();
			}
		});

		EasyPresets pp = parent.getParent();
		if (pp != null) {
			moveParentButton.setEnabled(true);
		}
		
		moveButton = new JButton(tr("Move to"));
		moveButton.setEnabled(false);
		moveButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				move();
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
		buttonPane.add(moveParentButton, GBC.std());
		buttonPane.add(moveButton, GBC.std());
		buttonPane.add(cancelButton, GBC.eol());
		return buttonPane;
	}
	
	protected void moveParent() {
		EasyPresets pp = parent.getParent();
		if (pp != null) {
			entry.setParent(pp);
			pp.addElement(entry);
			parent.removeElement(entry);
			dispose();
		}
	}
	
	protected void move() {
		EasyPresets destGroup = (EasyPresets)list.getSelectedValue();
		entry.setParent(destGroup);
		destGroup.addElement(entry);
		parent.removeElement(entry);
		dispose();
	}
	
	protected void cancel() {
		dispose();
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		PresetsEntry selected = list.getSelectedValue();
		if (selected == null) {
			moveButton.setEnabled(false);
		}
		else {
			moveButton.setEnabled(true);
		}
		return;
	}

}
