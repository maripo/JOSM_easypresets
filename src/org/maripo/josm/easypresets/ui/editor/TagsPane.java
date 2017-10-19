package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;

public class TagsPane extends JPanel {
	private List<TagEditor> tagEditors;
	List<Line> lines;

	private PresetEditorDialog baseDialog;
	public TagsPane (List<TagEditor> tagEditors, PresetEditorDialog baseDialog) {
		super(new GridBagLayout());
		this.baseDialog = baseDialog;
		this.tagEditors = tagEditors;
		lines = new ArrayList<Line>();
		
        createHeader();
        
        for (TagEditor editor: tagEditors) {
    		appendEditorUI(editor);
        }
        
        repaintAll();
	}

	private void createHeader() {
		// Header
        add(new JLabel(tr("Use")), GBC.std().insets(5, 0, 5, 0).anchor(GridBagConstraints.NORTHWEST));
        add(new JLabel(tr("Type")), GBC.std().insets(5, 0, 5, 0).anchor(GridBagConstraints.NORTHWEST));
        add(new JLabel(tr("Key")), GBC.std().insets(5, 0, 5, 0).anchor(GridBagConstraints.NORTHWEST));
        add(new JLabel(tr("Label" + "(" + tr("Optional") +")")), GBC.std().insets(5, 0, 5, 0).anchor(GridBagConstraints.NORTHWEST));
        add(new JLabel(tr("Value")), GBC.std().insets(5, 0, 5, 0).anchor(GridBagConstraints.NORTHWEST));
        add(new JLabel(tr("Order")), GBC.eol().insets(5, 0, 5, 0).anchor(GridBagConstraints.NORTHWEST).fill(GBC.HORIZONTAL));
	}

	// Container of data line
	class Line implements ActionListener {

		private int index;
		JButton upButton, downButton;
		private JPanel containerInclude, containerType, containerKey, containerLabel, containerValue;
		private TagEditor editor;
		public Line(TagEditor editor, int index) {
			this.index = index;
			this.editor = editor;

			containerInclude = new JPanel(new GridBagLayout());
			containerType = new JPanel(new GridBagLayout());
			containerKey = new JPanel(new GridBagLayout());
			containerLabel = new JPanel(new GridBagLayout());
			containerValue = new JPanel(new GridBagLayout());

			renderEditor(editor);

			add(containerInclude, GBC.std().insets(0).anchor(GBC.WEST));
			add(containerType, GBC.std().insets(0).anchor(GBC.WEST));
			add(containerKey, GBC.std().insets(0).anchor(GBC.WEST));
			add(containerLabel, GBC.std().insets(0).anchor(GBC.WEST));
			add(containerValue, GBC.std().insets(0).anchor(GBC.WEST));
			
			JPanel orderButtonsPanel = new JPanel(new GridLayout(1, 2));
			upButton = new JButton();
			upButton.setIcon(ImageProvider.get("dialogs", "up"));
			downButton = new JButton();
			downButton.setIcon(ImageProvider.get("dialogs", "down"));
			upButton.addActionListener(this);
			downButton.addActionListener(this);
			orderButtonsPanel.add(upButton);
			orderButtonsPanel.add(downButton);
			if (index==0) {
				upButton.setVisible(false);
			}
			add(orderButtonsPanel, GBC.eol().insets(0).fill(GBC.HORIZONTAL));
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==upButton) {
				reorder(index, -1);
			} if (e.getSource()==downButton) {
				reorder(index, 1);
			} 
		}
		public void removeUI() {
			containerInclude.remove(0);
			containerType.remove(0);
			containerLabel.remove(0);
			containerKey.remove(0);
			containerValue.remove(0);
		}
		public void renderEditor(TagEditor editor) {
			this.editor = editor;
			containerInclude.add(editor.getUiInclude(), GBC.eol().insets(0));
			containerType.add(editor.getUiType(), GBC.eol().insets(0));
			containerKey.add(editor.getUiKey(), GBC.eol().insets(0));
			containerLabel.add(editor.getUiLabel(), GBC.eol().insets(0));
			containerValue.add(editor.getUiValue(), GBC.eol().insets(0));
		}
		public void revalidateComponents() {
			containerInclude.revalidate();
			containerType.revalidate();
			containerLabel.revalidate();
			containerKey.revalidate();
			containerValue.revalidate();
		}
	}
	
	private void reorder (int index, int direction) {
		int fromIndex, toIndex;
		if (direction > 0) {
			fromIndex = index;
			toIndex = index + direction;
		} else {
			fromIndex = index + direction;
			toIndex = index;
		}
		if (fromIndex < 0 || toIndex >= lines.size()) {
			// Out of index
			return;
		}
		// swap list
		TagEditor fromEditor = tagEditors.get(fromIndex);
		TagEditor toEditor = tagEditors.get(toIndex);
		tagEditors.remove(toEditor);
		tagEditors.add(fromIndex, toEditor);
		lines.get(fromIndex).removeUI();
		lines.get(toIndex).removeUI();
		lines.get(fromIndex).renderEditor(toEditor);
		lines.get(toIndex).renderEditor(fromEditor);
        repaintAll();
	}
	
	private void repaintAll () {
		for (Line line: lines) {
			line.downButton.setVisible(line.index<lines.size()-1);
			line.revalidateComponents();
		}
		repaint();
		baseDialog.repaint();
	}
	
	private void appendEditorUI(TagEditor editor) {
		Line line = new Line(editor, lines.size());
		lines.add(line);
		invalidate();
	}

	public void addTag() {
		TagEditor editor = TagEditor.create(baseDialog);
		tagEditors.add(editor);
		appendEditorUI(editor);
		repaintAll();
	}

	public List<TagEditor> getTagEditors() {
		return tagEditors;
	}

}
