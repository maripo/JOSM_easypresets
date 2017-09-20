package org.maripo.josm.easypresets.ui.editor;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.josm.tools.GBC;

public class TagsPane extends JPanel {
	private List<TagEditor> tagEditors;

	List<Line> lines;
	public TagsPane (List<TagEditor> tagEditors) {
		super(new GridBagLayout());
		this.tagEditors = tagEditors;
		lines = new ArrayList<Line>();
		
        createHeader();
        
        for (TagEditor editor: tagEditors) {
    		appendEditorUI(editor);
        }
	}

	// Container of data line
	class Line implements ActionListener {

		private int index;
		JButton upButton, downButton;
		private JPanel containerInclude, containerType, containerKey, containerValue;
		public Line(TagEditor editor, int index) {
			this.index = index;

			containerInclude = new JPanel();
			containerType = new JPanel();
			containerKey = new JPanel();
			containerValue = new JPanel();

			containerType.setBackground(Color.PINK);
			containerValue.setBackground(Color.CYAN);

			renderEditor(editor);

			add(containerInclude, GBC.std().anchor(GridBagConstraints.WEST));
			add(containerType, GBC.std().anchor(GridBagConstraints.WEST));
			add(containerKey, GBC.std().anchor(GridBagConstraints.WEST));
			add(containerValue, GBC.std());
			
			JPanel orderButtonsPanel = new JPanel();
			upButton = new JButton("up");
			downButton = new JButton("down");
			orderButtonsPanel.add(upButton);
			upButton.addActionListener(this);
			downButton.addActionListener(this);
			orderButtonsPanel.add(downButton);
			add(orderButtonsPanel, GBC.eol().fill(GBC.HORIZONTAL));
			
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
			containerKey.remove(0);
			containerValue.remove(0);
		}
		public void renderEditor(TagEditor editor) {

			containerInclude.add(editor.getUiInclude());
			containerType.add(editor.getUiType());
			containerKey.add(editor.getUiKey());
			containerValue.add(editor.getUiValue());
			
		}
	}
	void reorder (int index, int direction) {
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
		// swap UI
		repaint();
		
	}
	private void appendEditorUI(TagEditor editor) {
		Line line = new Line(editor, lines.size());
		lines.add(line);
		setPreferredSize(new Dimension(620, lines.size() * 35 + 22));
		invalidate();
		
	}

	private void createHeader() {
		// Header
        this.add(new JLabel(tr("Use")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        this.add(new JLabel(tr("Type")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        this.add(new JLabel(tr("Key")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        this.add(new JLabel(tr("Value")), GBC.std().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST));
        this.add(new JLabel(tr("Order")), GBC.eol().insets(5, 0, 0, 0).anchor(GridBagConstraints.NORTHWEST).fill(GBC.HORIZONTAL));
	}

	public void addTag(PresetEditorDialog baseDialog) {
		TagEditor editor = TagEditor.create(baseDialog);
		tagEditors.add(editor);
		appendEditorUI(editor);
		revalidate();
	}

	public List<TagEditor> getTagEditors() {
		return tagEditors;
	}

}
