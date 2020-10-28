package org.maripo.josm.easypresets.data;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.gui.tagging.presets.items.Check;
import org.openstreetmap.josm.gui.tagging.presets.items.Combo;
import org.openstreetmap.josm.gui.tagging.presets.items.Key;
import org.openstreetmap.josm.gui.tagging.presets.items.Label;
import org.openstreetmap.josm.gui.tagging.presets.items.Link;
import org.openstreetmap.josm.gui.tagging.presets.items.MultiSelect;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EasyPreset extends TaggingPreset implements PresetsEntry {
	private static final long serialVersionUID = -7626914563011340418L;
	private EasyPresets parent = null;

	public EasyPreset() {
		this(null, null);
	}
	
	public EasyPreset(TaggingPreset src, EasyPresets parent) {
		super();
		this.parent = parent;
		if (src == null) {
			this.name = tr("New Preset");
		}
		else {
			this.name = src.name;
			this.setIcon(src.iconName);
			for (TaggingPresetItem fromItem: src.data) {
				TaggingPresetItem item = clonePresetTag(fromItem);
				if (item != null) {
					this.data.add(item);
				}
			}
			this.types = EnumSet.noneOf(TaggingPresetType.class);
			if (src.types != null) {
				this.types.addAll(src.types);
			}
		}
		this.setDisplayName();	// for JOSM menu [presets]-[find preset... F3]
	}
	
	public EasyPreset copy() {
		EasyPreset preset = EasyPreset.clone(this, this.parent);
		preset.name = "Copy of "+ this.getRawName();
		preset.locale_name = tr("Copy of {0}", this.getRawName());
		preset.setDisplayName();
		return preset;
	}

	@Override
	public EasyPreset clone() {
		return EasyPreset.clone(this, this.parent);
	}
	
	public static EasyPreset clone(TaggingPreset src, EasyPresets parent) {
		return new EasyPreset(src, parent);
	}
	
	private static TaggingPresetItem clonePresetTag(TaggingPresetItem itemFrom) {
		if (itemFrom instanceof Label) {
			Label itemTo = new Label();
			itemTo.text = ((Label) itemFrom).text;
			return itemTo;
		}
		else if (itemFrom instanceof Key) {
			Key key = (Key) itemFrom;
			Key itemTo = new Key();
			itemTo.key = key.key;
			itemTo.value = key.value;
			return itemTo;
		}
		else if (itemFrom instanceof Text) {
			Text text = (Text)itemFrom;
			Text itemTo = new Text();
			itemTo.text = text.text;
			itemTo.key = text.key;
			itemTo.default_ = text.default_;
			return itemTo;
		}
		else if (itemFrom instanceof Combo) {
			Combo combo = (Combo)itemFrom;
			Combo itemTo = new Combo();
			itemTo.text = combo.text;
			itemTo.key = combo.key;
			itemTo.values = combo.values;
			return itemTo;
		}
		else if (itemFrom instanceof MultiSelect) {
			MultiSelect multiselect = (MultiSelect)itemFrom;
			MultiSelect itemTo = new MultiSelect();
			itemTo.text = multiselect.text;
			itemTo.key = multiselect.key;
			itemTo.values = multiselect.values;
			return itemTo;
		}
		else if (itemFrom instanceof Check) {
			Check key = (Check) itemFrom;
			Check itemTo = new Check();
			itemTo.text = key.text;
			itemTo.key = key.key;
			return itemTo;
		}
		else if (itemFrom instanceof Link) {
			Link link = (Link)itemFrom;
			Link itemTo = new Link();
			itemTo.href = link.href;
			return itemTo;
		}
		return null;
	}
	
	Element getItemElement(Document doc) {
		Element itemElement = doc.createElement("item");
		itemElement.setAttribute("name", this.name);
		if (this.iconName!=null && !this.iconName.isEmpty()) {
			itemElement.setAttribute("icon", this.iconName);
		}
		if (this.types!=null && this.types.size()>0) {
			List<String> typeNames = new ArrayList<String>();
			for (TaggingPresetType type: this.types) {
				typeNames.add(type.getName());
			}
			itemElement.setAttribute("type", String.join(",", typeNames));
		}
		for (TaggingPresetItem item : this.data) {
			if (item instanceof Label) {
				Label label = (Label)item;
				Element labelElement = doc.createElement("label");
				labelElement.setAttribute("text", label.text);
				itemElement.appendChild(labelElement);
			}
			else if (item instanceof Key) {
				Key key = (Key) item;
				Element keyElement = doc.createElement("key");
				keyElement.setAttribute("key", key.key);
				keyElement.setAttribute("value", key.value);
				itemElement.appendChild(keyElement);
			}
			else if (item instanceof Text) {
				Text text = (Text)item;
				Element textElement = doc.createElement("text");
				textElement.setAttribute("text", text.text);
				textElement.setAttribute("key", text.key);
				textElement.setAttribute("default", text.default_);
				itemElement.appendChild(textElement);
			}
			else if (item instanceof Combo) {
				Combo combo = (Combo)item;
				Element comboElement = doc.createElement("combo");
				comboElement.setAttribute("text", combo.text);
				comboElement.setAttribute("key", combo.key);
				comboElement.setAttribute("values", combo.values);
				itemElement.appendChild(comboElement);
			}
			else if (item instanceof MultiSelect) {
				MultiSelect multiselect = (MultiSelect)item;
				Element multiselectElement = doc.createElement("multiselect");
				multiselectElement.setAttribute("text", multiselect.text);
				multiselectElement.setAttribute("key", multiselect.key);
				multiselectElement.setAttribute("values", multiselect.values);
				itemElement.appendChild(multiselectElement);
			}
			else if (item instanceof Check) {
				Check key = (Check) item;
				Element keyElement = doc.createElement("check");
				keyElement.setAttribute("text", key.text);
				keyElement.setAttribute("key", key.key);
				itemElement.appendChild(keyElement);
			}
			else if (item instanceof Link) {
				Link link = (Link)item;
				Element linkItem = doc.createElement("link");
				linkItem.setAttribute("href", link.href);
				itemElement.appendChild(linkItem);
			}
		}
		return itemElement;
	}

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	@Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void addListDataListener(GroupPresetMenu groupPresetMenu) {
		// TODO Auto-generated method stub
		System.out.println("EasyPreset.addListDataListener(...)");
	}
}
