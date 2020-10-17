package org.maripo.josm.easypresets.ui;

import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class ParameterPanel extends JPanel implements ParamAction {
    private static final long serialVersionUID = 4629824800747170556L;
    public JTextField argField;
    public JLabel argLabel;

    public ParameterPanel(String label, String text) {
        this();
        this.argLabel.setText(label);
        this.argField.setText(text);
    }

    public ParameterPanel() {
        super();

        argLabel = new JLabel();
        argField = new JTextField();
		
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(1920, 40));
        this.add(argLabel);
        this.add(argField);
    }
    
    public ParameterPanel setLabel(String label) {
        this.argLabel.setText(label);
        return this;
    }
    
    @Override
    public void setText(String text) {
        this.argField.setText(text);
    }
    
    @Override
    public String getText() {
        return this.argField.getText();
    }
}
