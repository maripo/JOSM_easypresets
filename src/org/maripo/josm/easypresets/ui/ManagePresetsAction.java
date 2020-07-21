package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;
import java.awt.event.ActionEvent;
import org.openstreetmap.josm.actions.JosmAction;

@SuppressWarnings("serial")
public class ManagePresetsAction extends JosmAction {

	public ManagePresetsAction () {
        super(tr("Manage custom presets"), "easypresets.png",
                tr("List, Update Or Delete Custom Presets"),
                null, true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ManagePresetsDialog dialog = new ManagePresetsDialog();
		dialog.showDialog();
	}
}
