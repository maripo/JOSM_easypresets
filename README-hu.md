# JOSM EasyPresets Plugin

[English](https://github.com/maripo/JOSM_easypresets/blob/master/README-en.md)
[日本語](https://github.com/maripo/JOSM_easypresets/blob/master/README-ja.md)

Az EasyPresets bővítménnyel egyéni címkesablonokat készíthetsz és használhatsz.
Könnyen hozhatsz létre címkesablonokat a kiválasztott objektumok alapján.
Egyes funkciók a „Címkesablonok” menü alá fognak kerülni.

Az egyéni sablonok helyi XML-fájlokba exportálhatók.

![Címkesablon-szerkesztő](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/preset_editor.png)

## Címkesablon létrehozása
* Válassz ki pontokat vagy vonalakat és kattints a „Címkesablonok > Címkesablon létrehozása” menüelemre. Egy párbeszédablakot fog megjeleníteni, amely a kiválasztásból kinyert címkéket tartalmazza.
* Ha ki akarod hagyni valamelyik felsorolt címkét, akkor vedd ki a pipát a „Használat” jelölőmező elől.
* A bővítmény a következő címketípusokat támogatja:
	* „Rögzített érték”: A rögzített kulcs–érték pár automatikusan hozzárendelésre kerül. Az ilyen mezők nem fognak explicit megjelenni a címkesablon párbeszédablakon.
	* „Szövegmező”: Mező tetszőleges szöveg szerkesztéséhez.
	* „Kiválasztás”: Legördülő menü több lehetőséggel. Szerkesztheted az értékkészletet.
	![Szerkesztési beállítások](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/options.png)
	* „Többszörös kiválasztás”: Értéklista egy vagy több lehetőség kiválasztásához. 
	* „Jelölőmező”: Jelölőmező „yes” vagy „no” megadásához.
* Célponttípusokat is megadhatsz, mint pontokat, vonalakat és multipoligonokat.
![Típusok](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/target_types.png)
* Az ikonok tesztreszabhatók. Kattints az „Ikon kiválasztása…" gombra.
![Ikonválasztó párbeszédablak](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/icon_picker.png)

## Címkesablon használata
* A normál címkesablonokhoz hasonlóan használhatod az egyéni címkesablonokat.
* Az egyéni címkesablonok a „Címkesablonok > Egyéni címkesablonok” menüben találhatók az eszköztáron.
* Az egyéni címkesablonok a „Címkesablonok keresése” (F3) párbeszédablakkal is megtalálhatók.

## Címkesablon szerkesztése
* A címkesablonok a „Címkesablonok > Egyéni címkesablonok kezelése” menüben szerkeszthetők, törölhetők és exportálhatók.
* Az exportált XML-fájlok kompatibilisek a JOSM címkesablon-fájlokkal. Ha szeretnéd megosztani a címkesablonokat más felhasználókkal, akkor nézd meg a hivatalos dokumentumot, mert csak néhány címke és attribútum támogatott. https://josm.openstreetmap.de/wiki/TaggingPresets

![Címkesablon-kezelő](https://github.com/maripo/JOSM_easypresets/blob/master/doc/img/en/manager.png) 

## Egyebek
* A bővítmény a JOSM felhasználói könyvtárban található „EasyPresets.xml” fájlban tárolja az adatokat.
* Jelenleg fejlesztés alatt áll, és csak alapvető funkciókkal rendelkezik. Azt tervezem, hogy további funkciókat valósítok meg, mint a csoportosítás, rendezés és a külöböző sablontípusok támogatása.

## TODO
* Csoportosítási funkció
* Speciális funkciók címkesablon-fejlesztőknek

## Fejlesztő
Maripo GODA <goda.mariko@gmail.com>
* Twitter: @MaripoGoda
* Blog: http://blog.maripo.org
* OpenStreetMap: maripogoda (Térképezés Akihabara körül)

