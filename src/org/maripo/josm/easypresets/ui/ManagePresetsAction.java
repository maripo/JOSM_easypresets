package org.maripo.josm.easypresets.ui;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.tools.Shortcut;

public class ManagePresetsAction extends JosmAction {

	public ManagePresetsAction () {
        super(tr("Manage custom presets"), "easypresets.png",
                tr("List, Update Or Delete Custom Presets"),
                Shortcut.registerShortcut(
                        "tools:easy_presets", tr("Tool: {0}", tr("Manage Custom Presets")), 
                        KeyEvent.VK_2,
                        Shortcut.ALT_CTRL_SHIFT), true);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		new ManagePresetsDialog().showDialog();
	}

}
