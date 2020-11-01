package org.maripo.josm.easypresets.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.maripo.josm.easypresets.data.PresetsEntry;

public class PresetRenderer extends JLabel implements ListCellRenderer<PresetsEntry> {
	private static final long serialVersionUID = -6052079733496895567L;
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
	public Component getListCellRendererComponent(
			JList<? extends PresetsEntry> list, 
			PresetsEntry preset,
			int index, 
			boolean isSelected, 
			boolean cellHasFocus) 
	{
		setIcon(preset.getIcon());
		setText(preset.getName());
		setOpaque(true);
		setBackground(isSelected?selectionBackground:textBackground);
		setForeground(isSelected?selectionForeground:textForeground);
		return this;
	}
}
