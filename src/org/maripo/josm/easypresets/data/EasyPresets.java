package org.maripo.josm.easypresets.data;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openstreetmap.josm.io.UTFInputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetReader;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresets;
import org.openstreetmap.josm.gui.tagging.presets.items.Check;
import org.openstreetmap.josm.gui.tagging.presets.items.Combo;
import org.openstreetmap.josm.gui.tagging.presets.items.ComboMultiSelect;
import org.openstreetmap.josm.gui.tagging.presets.items.Key;
import org.openstreetmap.josm.gui.tagging.presets.items.KeyedItem;
import org.openstreetmap.josm.gui.tagging.presets.items.Label;
import org.openstreetmap.josm.gui.tagging.presets.items.Link;
import org.openstreetmap.josm.gui.tagging.presets.items.MultiSelect;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Container of custom presets
 * @author maripo
 *
 */
@SuppressWarnings("serial")
public class EasyPresets extends DefaultListModel<EasyPreset> implements PropertyChangeListener {
	private static final String FILE_NAME = "EasyPresets.xml";
	private static final String[] PRESET_FORMAT_URLS = {
			"https://josm.openstreetmap.de/wiki/TaggingPresets",
			"https://wiki.openstreetmap.org/wiki/Customising_JOSM_Presets"
			};
	public static final String PLUGIN_HELP_URL = "https://github.com/maripo/JOSM_easypresets/blob/master/README.md";

	/**
	 * Get file path of custom preset data file
	 * @return Full path of preset data file
	 */
	public String getXMLPath() {
		return Config.getDirs().getUserDataDirectory(true) + "/" + FILE_NAME;
	}

	public EasyPresets() {
		super();
	}

	public List<EasyPreset> getPresets() {
		List<EasyPreset> list = new ArrayList<EasyPreset>();
		for (int i = 0; i < getSize(); i++) {
			EasyPreset e = getElementAt(i);
			list.add(e);
		}
		return list;
	}
	
	@Override
	public EasyPreset[] toArray() {
		List<EasyPreset> list = getPresets();
		return (EasyPreset[])list.toArray(new EasyPreset[list.size()]);
	}

	/**
	 * Load custom presets from local XML (if exists)
	 */
	public void load() {
		final File file = new File(this.getXMLPath());
		if (file.exists() && file.canRead()) {
			try (Reader reader = UTFInputStreamReader.create(new FileInputStream(file))) {
				final Collection<TaggingPreset> readResult = TaggingPresetReader.readAll(reader, true);
				if (readResult != null) {
					addAll(readResult);
				}
			} catch (FileNotFoundException e) {
				Logging.debug("File not found: " + file.getAbsolutePath());
				return;
			} catch (SAXException | IOException e) {
				Logging.warn(e);
			}
		}
	}
	
	@Override
	public void addAll(@SuppressWarnings("rawtypes") Collection c) {
		if (c != null) {
			for (Object preset : c) {
				super.addElement((EasyPreset)preset);
			}
		}
	}
	
	/*
	 * Add new tagging preset
	 */
	@Override
	public void addElement(EasyPreset preset) {
		super.addElement(preset);
	}
	
	@Override
	public void setElementAt(EasyPreset element, int index) {
		super.setElementAt(element, index);
	}
	
	/*
	 * Save all TaggingPresets to specified file
	 */
	public void saveTo(List<TaggingPreset> list, File file) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("presets");
			rootElement.setAttribute("xmlns", "http://josm.openstreetmap.de/tagging-preset-1.0");
			rootElement.setAttribute("author", "");
			rootElement.setAttribute("version", "");
			rootElement.setAttribute("description", "");
			rootElement.setAttribute("shortdescription", "");
			doc.appendChild(rootElement);
			rootElement.appendChild(doc.createComment(getComment()));
			for (TaggingPreset preset: list) {
				Element presetElement = createpresetElement(doc, preset);
				rootElement.appendChild(presetElement);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			// Write to local XML
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private String getComment() {
		StringBuilder comment = new StringBuilder();
		comment.append("\n");
		comment.append(tr("This file is generated by EasyPresets plugin.\n"));
		comment.append(PLUGIN_HELP_URL);
		comment.append("\n");
		comment.append(tr("It supports just a few tags and attributes.\n"));
		comment.append(tr("If you want to share your custom tags with other users, please look through the official document.\n"));
		for (String url : PRESET_FORMAT_URLS) {
			comment.append(" * ");
			comment.append(url);
			comment.append("\n");
		}
		return comment.toString();
	}

	private Element createpresetElement(Document doc, TaggingPreset obj) {
		Element presetElement = doc.createElement("item");
		presetElement.setAttribute("name", obj.name);
		if (obj.iconName!=null && !obj.iconName.isEmpty()) {
			presetElement.setAttribute("icon", obj.iconName);
		}
		if (obj.types!=null && obj.types.size()>0) {
			List<String> typeNames = new ArrayList<String>();
			for (TaggingPresetType type: obj.types) {
				typeNames.add(type.getName());
			}
			presetElement.setAttribute("type", String.join(",", typeNames));
		}
		for (TaggingPresetItem item : obj.data) {
			if (item instanceof Label) {
				Label label = (Label)item;
				Element labelElement = doc.createElement("label");
				labelElement.setAttribute("text", label.text);
				presetElement.appendChild(labelElement);
			}
			else if (item instanceof Key) {
				Key key = (Key) item;
				Element keyElement = doc.createElement("key");
				keyElement.setAttribute("key", key.key);
				keyElement.setAttribute("value", key.value);
				presetElement.appendChild(keyElement);
			}
			else if (item instanceof Text) {
				Text text = (Text)item;
				Element textElement = doc.createElement("text");
				textElement.setAttribute("text", text.text);
				textElement.setAttribute("key", text.key);
				textElement.setAttribute("default", text.default_);
				presetElement.appendChild(textElement);
			}
			else if (item instanceof Combo) {
				Combo combo = (Combo)item;
				Element comboElement = doc.createElement("combo");
				comboElement.setAttribute("text", combo.text);
				comboElement.setAttribute("key", combo.key);
				comboElement.setAttribute("values", combo.values);
				presetElement.appendChild(comboElement);
			}
			else if (item instanceof MultiSelect) {
				MultiSelect multiselect = (MultiSelect)item;
				Element multiselectElement = doc.createElement("multiselect");
				multiselectElement.setAttribute("text", multiselect.text);
				multiselectElement.setAttribute("key", multiselect.key);
				multiselectElement.setAttribute("values", multiselect.values);
				presetElement.appendChild(multiselectElement);
			}
			else if (item instanceof Check) {
				Check key = (Check) item;
				Element keyElement = doc.createElement("check");
				keyElement.setAttribute("text", key.text);
				keyElement.setAttribute("key", key.key);
				presetElement.appendChild(keyElement);
			}
			else if (item instanceof Link) {
				Link link = (Link)item;
				Element linkItem = doc.createElement("link");
				linkItem.setAttribute("href", link.href);
				presetElement.appendChild(linkItem);
			}
		}
		return presetElement;
	}

	/*
	 * Reorder presets
	 */
	public void moveDown(int index) {
		if (index >= getSize() - 1) {
			return;
		}
		EasyPreset presetToMove = remove(index);
		add(index+1, presetToMove);
	}

	/*
	 * Reorder presets
	 */
	public void moveUp(int index) {
		if (index <= 0) {
			return;
		}
		EasyPreset presetToMove = this.remove(index);
		this.add(index-1, presetToMove);
	}
	
	public String getLabelFromExistingPresets (String key) {
		Collection<TaggingPreset> existingPresets = TaggingPresets.getTaggingPresets();
		Map<String, Integer> labelCountMap = new HashMap<String, Integer>();
		for (TaggingPreset preset: existingPresets) {
			for (TaggingPresetItem _item: preset.data) {
				if (_item instanceof KeyedItem) {
					KeyedItem item = (KeyedItem)_item;
					if (key.equals(item.key)) {
						String label = getLocaleLabel(item);
						if (label!=null && !label.isEmpty() && !label.equals(key)) {
							int count;
							if (labelCountMap.containsKey(label)) {
								count = labelCountMap.get(label).intValue() + 1;
							} else {
								count = 1;
							}
							labelCountMap.put(label, count);
						}
					}
				}
			}
		}
		int maxCount = 0;
		String mostFrequentLabel = "";
		for (String label: labelCountMap.keySet()) {
			if (labelCountMap.get(label) > maxCount) {
				mostFrequentLabel = label;
				maxCount = labelCountMap.get(label); 
			}
		}
		return mostFrequentLabel;
	}

	static class DummyPresetClass extends Text {
		public static String getLocaleText(String text, String textContext){
			return getLocaleText(text, textContext, null);
		}
	}
	private String getLocaleLabel(KeyedItem _item) {
		if (_item instanceof Text) {
			Text item = (Text)_item;
			return (item.locale_text!=null)?
					item.locale_text:DummyPresetClass.getLocaleText(item.text, item.text_context);
		}
		if (_item instanceof ComboMultiSelect) {
			ComboMultiSelect item = (ComboMultiSelect)_item;
			return (item.locale_text!=null)?
					item.locale_text:DummyPresetClass.getLocaleText(item.text, item.text_context);
		}
		if (_item instanceof Check) {
			Check item = (Check)_item;
			return (item.locale_text!=null)?
					item.locale_text:DummyPresetClass.getLocaleText(item.text, item.text_context);
		}
		return null;
	}
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
        this.pcs.firePropertyChange(EasyPresets.class.getName(), null, this);
	}
}
