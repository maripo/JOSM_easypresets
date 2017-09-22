package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresets;
import org.openstreetmap.josm.tools.GBC;

public class IconPickerDialog extends ExtendedDialog implements MouseListener {

	public interface IconPickerDialogListener {

		void onSelectIcon(ImageIcon icon, String name);
		void onCancel();
	}
	IconPickerDialogListener listener;
	JPanel mainPane;
	private class Icon {

		private ImageIcon icon;
		private String name;
		private JLabel component;

		public Icon(ImageIcon icon, String iconName) {
			this.icon = icon;
			this.name = iconName;
		}

		public void setConponent(JLabel label) {
			this.component = label;
		}
		
	}
	List<Icon> icons = new ArrayList<Icon>();
	public IconPickerDialog (ExtendedDialog baseDialog) {
		super(baseDialog, tr("Icon"));
		mainPane = new JPanel(new GridBagLayout());
		JPanel iconsPane = new JPanel(new GridBagLayout());

		Collection<TaggingPreset> existingPresets = TaggingPresets.getTaggingPresets();
		List<String> images = new ArrayList<String>();
		for (TaggingPreset preset: existingPresets) {
			if (preset.iconName!=null && !images.contains(preset.iconName)) {
				images.add(preset.iconName);
				icons.add(new Icon(preset.getIcon(), preset.iconName));
			}
		}
		int IMAGES_PER_LINE = 32;
		for (int i=0; i<icons.size(); i++) {
			Icon icon = icons.get(i);
			JLabel label = new JLabel(icon.icon);
			label.setToolTipText(icon.name);
			label.addMouseListener(this);
			icon.setConponent(label);
			iconsPane.add(label, (i%IMAGES_PER_LINE==IMAGES_PER_LINE-1)?GBC.eol():GBC.std());
			
		}
		mainPane.add(iconsPane, GBC.eop());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listener!=null) {
					listener.onCancel();
				}
				dispose();
			}
		});
		mainPane.add(cancelButton, GBC.eol());
		setContent(mainPane);
	}
	public void setListener (IconPickerDialogListener listener) {
		this.listener = listener;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		Object o = e.getSource();
		if (o instanceof JLabel) {
			JLabel label = (JLabel)o;
			mainPane.add(new JLabel(label.getToolTipText()));
			if (listener!=null) {
				for (Icon icon: icons) {
					if (label == icon.component) {
						listener.onSelectIcon(icon.icon, icon.name);
						dispose();
					}
				}
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
}
