package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.tools.GBC;

/**
 * Dialog to input multiple options to "Selection" tag
 * @author maripo
 *
 */
public class ValuesEditorDialog extends ExtendedDialog {

	/**
	 * Interface to receive edit or cancel result
	 * @author maripo
	 */
	interface ValuesEditorDialogListener {
		public void onInput (String[] values);
		public void onCancel ();
	}
	private static final int DEFAULT_COLUMNS = 20;
	private static final int DEFAULT_ROWS = 8;
	ValuesEditorDialogListener listener;
	public ValuesEditorDialog setListener(ValuesEditorDialogListener listener) {
		this.listener = listener;
		return this;
	}
	JTextArea textarea;
	public ValuesEditorDialog (ExtendedDialog baseDialog, String[] values) {
		super(baseDialog, tr("Options"));
		this.setAlwaysOnTop(true);
		JPanel mainPanel = new JPanel(new GridBagLayout());
		textarea = new JTextArea();
		if (values != null) {
			textarea.setText(String.join("\n", values));
		}
		textarea.setColumns(DEFAULT_COLUMNS);
		textarea.setRows(DEFAULT_ROWS);
		mainPanel.add(textarea, GBC.eol().fill());
		mainPanel.add(new JLabel(tr("Enter one option per line.")), GBC.eol());
		JButton okButton = new JButton(tr("OK"));
		JButton cancelButton = new JButton(tr("Cancel"));
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}});
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}});
		mainPanel.add(okButton);
		mainPanel.add(cancelButton);
		setContent(mainPanel);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                toFront();
            }
        });
	}
	private void ok () {
		if (listener!=null) {
			List<String> validValues = new ArrayList<String>();
			// Remove empty lines
			for (String line: textarea.getText().split("\n")) {
				if (!line.isEmpty()) {
					validValues.add(line);
				}
			}
			listener.onInput(validValues.toArray(new String[0]));
		}
		this.dispose();
		
	}
	private void cancel () {
		if (listener!=null) {
			listener.onCancel();
		}
		this.dispose();
	}
}
