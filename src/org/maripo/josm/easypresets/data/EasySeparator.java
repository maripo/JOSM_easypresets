package org.maripo.josm.easypresets.data;

import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetSeparator;
import org.openstreetmap.josm.tools.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EasySeparator extends TaggingPresetSeparator implements PresetsEntry, Cloneable {
	private static final long serialVersionUID = -945138620106720559L;
	private EasyPresets parent = null;

	public EasySeparator() {
		this(null);
	}
	
	public EasySeparator(EasyPresets parent) {
		super();
		this.parent = parent;
		this.name = "---";
		this.setDisplayName();	// for JOSM menu [presets]-[find preset... F3]
	}
	
	@Override
	public EasySeparator copy() {
		return this.clone();
	}

	@Override
	public EasySeparator clone() {
		EasySeparator obj = null;
		try {
			obj = new EasySeparator(this.parent);
		}
		catch (Exception e) {
            Logging.error(e);
		}
		return obj;
	}
	
	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public EasyPresets getParent() {
		return this.parent;
	}

	@Override
	public void setParent(EasyPresets parent) {
		this.parent = parent;
	}
	
	@Override
	public String getName() {
		return "---";
	}
	
	@Override
	public String getLocaleName() {
		return getName();
	}
	
	@Override
	public String getRawName() {
		return parent.getRawName() + GroupStack.SEPA + getLocaleName();
	}

	@Override
	public void addListDataListener(GroupPresetMenu groupPresetMenu) {
		// TODO Auto-generated method stub
		System.out.println("EasySeparator.addListDataListener(GroupPresetMenu)");
	}
	
	Element getItemElement(Document doc) {
		return doc.createElement("separator");
	}
}
