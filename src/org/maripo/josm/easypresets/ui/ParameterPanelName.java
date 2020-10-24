package org.maripo.josm.easypresets.ui;

public class ParameterPanelName extends ParameterPanel {
	
	private static final long serialVersionUID = 1L;
	String ngword = "";

	public ParameterPanelName(String labelText, String v, String ngword) {
		super(labelText, v);
		this.ngword = ngword;
	}

    @Override
    public boolean isEnabled() {
        String text = this.argField.getText();
        if (text == null) {
            return false;
        }
        if (text.trim().length() > 0) {
        	if ((ngword != null) && text.contentEquals(ngword)) {
        		return false;
        	}
        	else {
        		return true;
        	}
        }
        else {
            return false;
        }
    }
}
