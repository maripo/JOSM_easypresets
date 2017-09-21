# JOSM EasyPresets Plugin

EasyPresets plugin helps you create and use custom presets.
You can easily create presets according to selected objects. 
Some functionalities will be added to "Preset" menu.

Your custom presets can be exported to local XML files.

![Preset editor](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/preset_editor.png)

## Create Presets
* Select nodes or ways and click "Presets > Create Preset" menu. It shows a dialog containing tags extracted from the selection.
* If you want to exclude some of listed tags, please uncheck "Use" checkboxes.
* This plugin supports three types of tags.  
	* Select "Fixed value" to create a fixed key-value pair.
	* Select "Textbox" if you want to make the value editable. You can set default values.
	* Select "Selection" to create dropdown field with multiple options. ![Editing options](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/options.png)
* You can select target types such as nodes, ways and multipolygons.![types](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/target_types.png)

## Use Presets
* You can use custom preset in the same manner as normal presets.
* Your custom presets can be found in "Presets > Custom Presets" menu in the toolbar.
* Custom presets can also be found by "Search presets" (F3) dialog.

## Manage Presets
* You can edit, delete and export data by "Presets > Manage custom presets" menu.
* Exported XML files are compatible with JOSM preset files. If you want to share your cool presets with other users, please refer to official document because only a few tags and attributes are included. https://josm.openstreetmap.de/wiki/TaggingPresets

![プリセットの管理](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/manager.png) 

## Misc
* This plugin stores the data to "EasyPresets.xml" in your JOSM user directory.
* It is now under development and has only basic functionalities. I am plannning to implement more functionalities such as grouping, sorting and support of various preset types. 

## TODO
* Grouping
* Sorting
* Localization

## Developer
Maripo GODA <goda.mariko@gmail.com>
* Twitter @MaripoGoda
* Blog http://blog.maripo.org
* OpenStreetMap maripogoda (Mapping around Akihabara)