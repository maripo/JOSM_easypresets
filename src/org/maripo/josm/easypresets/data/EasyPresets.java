package org.maripo.josm.easypresets.data;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
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

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.JosmUserIdentityManager;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetReader;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetType;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresets;
import org.openstreetmap.josm.gui.tagging.presets.items.Combo;
import org.openstreetmap.josm.gui.tagging.presets.items.Key;
import org.openstreetmap.josm.gui.tagging.presets.items.Label;
import org.openstreetmap.josm.gui.tagging.presets.items.Link;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.openstreetmap.josm.tools.ImageProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import jdk.internal.util.xml.impl.ReaderUTF8;

/**
 * Container of custom presets
 * @author maripo
 *
 */
public class EasyPresets {
	private static final String FILE_NAME = "EasyPresets.xml";
	private static final String[] PRESET_FORMAT_URLS = {
			"https://josm.openstreetmap.de/wiki/TaggingPresets",
			"https://wiki.openstreetmap.org/wiki/Customising_JOSM_Presets"
			};
	public static final String PLUGIN_HELP_URL = "https://github.com/maripo/JOSM_easypresets/blob/master/README.md";

	boolean isDirty = false;
	/**
	 * Get file path of custom preset data file
	 * @return Full path of preset data file
	 */
	public String getXMLPath() {
		return Main.pref.getUserDataDirectory() + "/" + FILE_NAME;
	}

	private static EasyPresets instance;

	private EasyPresets() {
		super();
	}

	public static EasyPresets getInstance() {
		if (instance == null) {
			instance = new EasyPresets();
		}
		return instance;
	}

	List<TaggingPreset> presets = new ArrayList<TaggingPreset>();

	/**
	 * Load custom presets from local XML (if exists)
	 */
	public void load() {
		ReaderUTF8 reader;
		String path = EasyPresets.getInstance().getXMLPath();
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		try {
			reader = new ReaderUTF8(new FileInputStream(path));
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			return;
		}
		presets = new ArrayList<TaggingPreset>();
		try {
			Collection<TaggingPreset> readResult = TaggingPresetReader.readAll(reader, true);
			if (readResult!=null) {
				presets.addAll(readResult);
			}
			TaggingPresets.addTaggingPresets(readResult);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		updatePresetListMenu();
	}

	/**
	 * Add new tagging preset
	 * @param preset
	 */
	public void add (TaggingPreset preset) {
		presets.add(preset);
		Collection<TaggingPreset> toAdd = new ArrayList<TaggingPreset>();
		toAdd.add(preset);
		// New preset will be able to find F3 menu
		TaggingPresets.addTaggingPresets(toAdd);
	}

	/**
	 * Save all custom presets to specified file
	 * @param file
	 */
	public void saveTo(File file) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("presets");
			rootElement.setAttribute("xmlns", "http://josm.openstreetmap.de/tagging-preset-1.0");
			rootElement.setAttribute("author", JosmUserIdentityManager.getInstance().getUserName());
			rootElement.setAttribute("version", "");
			rootElement.setAttribute("description", "");
			rootElement.setAttribute("shortdescription", "");
			doc.appendChild(rootElement);
			rootElement.appendChild(doc.createComment(getComment()));
			for (TaggingPreset preset: presets) {
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
		updatePresetListMenu();
		
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

	public void saveIfNeeded() {
		if (isDirty) {
			save();
		}
	}
	public void save() {
		saveTo(new File(EasyPresets.getInstance().getXMLPath()));
		isDirty = false;
	}
	
	private void updatePresetListMenu() {
		group.setEnabled(presets.size()>0);
        for (TaggingPreset preset: presets) {
            JMenuItem mi = new JMenuItem(preset);
            mi.setText(preset.getLocaleName());
            group.menu.add(mi);
        }
	}

	private TaggingPresetMenu group;
	/**
	 * Create a preset group holding all custom presets
	 * @return created group
	 */
	public TaggingPresetMenu createGroupMenu() {
		if (group==null) {
			group = new TaggingPresetMenu();
			group.name = tr("Custom Presets");
			group.setIcon("easypresets.png");
			JMenu menu = new JMenu(group);
			group.menu = menu;
			group.setDisplayName();
		}
		return group;
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
			if (item instanceof Key) {
				Key key = (Key) item;
				Element keyElement = doc.createElement("key");
				keyElement.setAttribute("key", key.key);
				keyElement.setAttribute("value", key.value);
				presetElement.appendChild(keyElement);
			}
			else if (item instanceof Label) {
				Label label = (Label)item;
				Element labelElement = doc.createElement("label");
				labelElement.setAttribute("text", label.text);
				presetElement.appendChild(labelElement);
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
			else if (item instanceof Link) {
				Link link = (Link)item;
				Element linkItem = doc.createElement("link");
				linkItem.setAttribute("href", link.href);
				presetElement.appendChild(linkItem);
			}
		}
		return presetElement;
	}

	public TaggingPreset getLastItem() {
		if (presets.size()==0) {
			return null;
		}
		Object[] objs = presets.toArray();
		return (TaggingPreset)objs[objs.length-1];
	}

	public Collection<TaggingPreset> getPresets() {
		return presets;
	}

	public void remove(TaggingPreset presetToRemove) {
		presets.remove(presetToRemove);
		
	}

	public void delete(TaggingPreset presetToDelete) {
		remove(presetToDelete);
		save();
		updatePresetListMenu();
	}


	/**
	 * Reorder presets
	 * @param index
	 */
	public void moveDown(int index) {
		if (index >= presets.size()-1) {
			return;
		}
		TaggingPreset presetToMove = presets.remove(index);
		presets.add(index+1, presetToMove);
		isDirty = true;
	}

	/**
	 * Reorder presets
	 * @param index
	 */
	public void moveUp(int index) {
		if (index <= 0) {
			return;
		}
		TaggingPreset presetToMove = presets.remove(index);
		presets.add(index-1, presetToMove);
		isDirty = true;
		
	}

	/**
	 * Replace existing preset with another preset
	 * @param oldPreset
	 * @param newPreset
	 */
	public void replace(TaggingPreset oldPreset, TaggingPreset newPreset) {
		int index = presets.indexOf(oldPreset);
		if (index < -1) {
			// Not found.
			presets.add(newPreset);
			return;
		}
		presets.remove(oldPreset);
		presets.add(index, newPreset);
	}
}
