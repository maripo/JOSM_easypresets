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
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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

import org.maripo.josm.easypresets.ui.GroupPresetMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetItem;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetReader;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresets;
import org.openstreetmap.josm.gui.tagging.presets.items.Check;
import org.openstreetmap.josm.gui.tagging.presets.items.ComboMultiSelect;
import org.openstreetmap.josm.gui.tagging.presets.items.KeyedItem;
import org.openstreetmap.josm.gui.tagging.presets.items.Text;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.tools.ImageProvider;
import org.openstreetmap.josm.tools.Logging;
import org.openstreetmap.josm.tools.XmlParsingException;
import org.openstreetmap.josm.tools.ImageProvider.ImageSizes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Container of custom presets
 * @author maripo
 *
 */
@SuppressWarnings("serial")
public class EasyPresets extends DefaultListModel<PresetsEntry> implements PropertyChangeListener, PresetsEntry, Cloneable {
	private static final String FILE_NAME = "EasyPresets.xml";
	private static final String[] PRESET_FORMAT_URLS = {
			"https://josm.openstreetmap.de/wiki/TaggingPresets",
			"https://wiki.openstreetmap.org/wiki/Customising_JOSM_Presets"
			};
	public static final String PLUGIN_HELP_URL = "https://github.com/maripo/JOSM_easypresets/blob/master/README.md";

    /**
     * A cache for the local name. Should never be accessed directly.
     * @see #getLocaleName()
     */
    private String name = "";
    EasyPresets parent = null;

    /**
	 * Get file path of custom preset data file
	 * @return Full path of preset data file
	 */
	public String getXMLPath() {
		return Config.getDirs().getUserDataDirectory(true) + "/" + FILE_NAME;
	}

	public EasyPresets() {
		this(null);
	}

	public EasyPresets(EasyPresets parent) {
		super();
		this.parent = parent;
	}
	
	public boolean isRoot() {
		return (this.parent == null);
	}

	public List<PresetsEntry> getEntry() {
		List<PresetsEntry> list = new ArrayList<PresetsEntry>();
		for (int i = 0; i < getSize(); i++) {
			PresetsEntry e = getElementAt(i);
			list.add(e);
		}
		return list;
	}
	
	public JMenu getMenu() {
		JMenu menu = new JMenu(this.getLocaleName());
		List<PresetsEntry> lentry = this.getEntry();
        for (PresetsEntry entry : lentry) {
        	if (entry instanceof TaggingPreset) {
                JMenuItem mi = new JMenuItem((TaggingPreset)entry);
                mi.setText(((TaggingPreset)entry).getName());
                mi.setEnabled(true);
                menu.add(mi);
        	}
        	else if (entry instanceof EasyPresets) {
                menu.add(((EasyPresets) entry).getMenu());
        	}
        }
        return menu;
	}
	
	@Override
	public PresetsEntry[] toArray() {
		List<PresetsEntry> list = getEntry();
		return (PresetsEntry[])list.toArray(new PresetsEntry[list.size()]);
	}

	/**
	 * Load custom presets from local XML (if exists)
	 */
	public void load() {
		final File file = new File(this.getXMLPath());
		load(file);
	}
	
	void load(File file) {
		if (file.exists() && file.canRead()) {
			try (Reader reader = UTFInputStreamReader.create(new FileInputStream(file))) {
				final Collection<TaggingPreset> readResult = TaggingPresetReader.readAll(reader, true);
				if (readResult != null) {
					GroupStack stack = new GroupStack(this);
					for (TaggingPreset preset : readResult) {
						String locale = preset.getLocaleName();
						String raw = preset.getRawName();
						String path = raw.substring(0, raw.length() - locale.length());
						if (!path.startsWith(this.name)) {
							path = this.name + GroupStack.SEPA + path;
						}
						EasyPresets pp = stack.pop(path);
						if (pp == null) {
							pp = this;
						}
						if (preset instanceof TaggingPresetMenu) {
							if (!locale.contentEquals(this.name)) {
								EasyPresets group = new EasyPresets(pp);
								group.setName(locale);
								stack.push(group);
								pp.addElement(group);
							}
						}
						else {
							EasyPreset tags = new EasyPreset((TaggingPreset)preset, pp);
							pp.addElement(tags);
						}
					}
				}
			} catch (FileNotFoundException e) {
				Logging.debug("File not found: " + file.getAbsolutePath());
				return;
			} catch (XmlParsingException e) {
				Logging.info(e.toString());
			} catch (SAXException | IOException e) {
				Logging.warn(e);
			}
		}
	}
	
	/*
	 * Add new tagging preset
	 */
	@Override
	public void addElement(PresetsEntry preset) {
		super.addElement(preset);
	}
	
	@Override
	public void setElementAt(PresetsEntry element, int index) {
		super.setElementAt(element, index);
	}
	
	public void saveTo() {
		final File file = new File(this.getXMLPath());
		saveTo(file);
	}

	/*
	 * Save all TaggingPresets to specified file
	 */
	public void saveTo(File file) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			// XML top element <presets>
			Element presetsElement = getPresetsElement(doc);
			presetsElement.appendChild(doc.createComment(getComment()));
			doc.appendChild(presetsElement);
			
			// XML element <presets><group>
			Element groupElement = getGroupElement(doc);
			
			if (groupElement.hasChildNodes()) {
				presetsElement.appendChild(groupElement);
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

	Element getPresetsElement(Document doc) {
		Element presetsElement = doc.createElement("presets");
		presetsElement.setAttribute("xmlns", "http://josm.openstreetmap.de/tagging-preset-1.0");
		presetsElement.setAttribute("author", "");
		presetsElement.setAttribute("version", "");
		presetsElement.setAttribute("description", "");
		presetsElement.setAttribute("shortdescription", "");
		return presetsElement;
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
	
	Element getGroupElement(Document doc) {
		String name = this.getLocaleName();
		return getGroupElement(doc, name);
	}

	Element getGroupElement(Document doc, String name) {
		Element groupElement = doc.createElement("group");
		if (name != null) {
			groupElement.setAttribute("name", name);
		}
		else {
			groupElement.setAttribute("name", tr("Custom Presets"));
		}
		List<PresetsEntry> list = this.getEntry();
		for (PresetsEntry preset: list) {
			if (preset instanceof EasyPreset) {
				Element itemElement = ((EasyPreset)preset).getItemElement(doc);
				groupElement.appendChild(itemElement);
			}
			else if (preset instanceof EasyPresets) {
				if (!((EasyPresets)preset).isEmpty()) {
					Element itemElement = ((EasyPresets)preset).getGroupElement(doc);
					groupElement.appendChild(itemElement);
				}
			}
		}
		return groupElement;
	}

	/*
	 * Reorder presets
	 */
	public void moveDown(int index) {
		if (index >= getSize() - 1) {
			return;
		}
		PresetsEntry presetToMove = remove(index);
		add(index+1, presetToMove);
	}

	/*
	 * Reorder presets
	 */
	public void moveUp(int index) {
		if (index <= 0) {
			return;
		}
		PresetsEntry presetToMove = this.remove(index);
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

	@Override
	public Icon getIcon() {
		ImageProvider img = new ImageProvider("open");
		img.setSize(ImageSizes.SMALLICON);
		return img.get();
	}

    /**
     * Returns the translated name of this preset, prefixed with the group names it belongs to.
     * @return the translated name of this preset, prefixed with the group names it belongs to
     */
	@Override
	public String getLocaleName() {
        return tr(this.name);
	}
	
	@Override
	public String getName() {
        return this.name;
	}
	
	public String getRawName() {
		String locale = "";
		if (this.parent != null) {
			locale += this.parent.getRawName();
		}
		if (!locale.isEmpty()) {
			locale += "/";
		}
		locale += this.getLocaleName();
		return locale;
	}

    /**
     * Returns the translated name of this preset, prefixed with the group names it belongs to.
     * @return the translated name of this preset, prefixed with the group names it belongs to
     */
	public void setName(String name) {
        this.name = name;
	}

	@Override
	public PresetsEntry copy() {
		try {
			EasyPresets preset = this.clone();
			preset.name = tr("Copy of {0}", this.name);
			return preset;
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public EasyPresets clone() throws CloneNotSupportedException {
		EasyPresets obj = new EasyPresets(this.parent);
		obj.setName(getName());
		obj.parent = this.parent;
		List<PresetsEntry> entries = this.getEntry();
		for (PresetsEntry entry : entries) {
			if (entry instanceof EasyPreset) {
				obj.addElement(((EasyPreset) entry).clone());
			}
			else if (entry instanceof EasyPresets) {
				obj.addElement(((EasyPresets) entry).clone());
			}
		}
		return obj;
	}
	/*
	static EasyPresets clone(EasyPresets src) {
		return new EasyPresets();
	}*/

	@Override
	public void addListDataListener(GroupPresetMenu groupPresetMenu) {
		super.addListDataListener(groupPresetMenu);
	}
}
