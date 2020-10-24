package org.maripo.josm.easypresets.ui;

public interface ParamAction {
    boolean isEnabled();
    
    void setText(String text);
    
    String getText();
}
