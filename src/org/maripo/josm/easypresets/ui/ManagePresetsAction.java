package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.event.ActionEvent;

import org.maripo.josm.easypresets.data.EasyPresets;
import org.openstreetmap.josm.actions.JosmAction;

@SuppressWarnings("serial")
public class ManagePresetsAction extends JosmAction {
	EasyPresets root;

	public ManagePresetsAction (EasyPresets root) {
        super(tr("Manage custom presets"), "easypresets.png",
                tr("List, Update Or Delete Custom Presets"),
                null, true);
		this.root = root;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ManagePresetsDialog dialog = new ManagePresetsDialog(this.root);
		dialog.showDialog();
	}
}
