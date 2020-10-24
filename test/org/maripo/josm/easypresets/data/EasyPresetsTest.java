package org.maripo.josm.easypresets.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.preferences.JosmBaseDirectories;
import org.openstreetmap.josm.data.preferences.JosmUrls;
import org.openstreetmap.josm.spi.preferences.Config;

/**
 * Use JUnit5
 * @author hayashi
 *
 */
class EasyPresetsTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
        Preferences prefs = Preferences.main();
        Config.setPreferencesInstance(prefs);
        Config.setBaseDirectoriesProvider(JosmBaseDirectories.getInstance());
        Config.setUrlsProvider(JosmUrls.getInstance());
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * ~/.josm/EasyPresets.xml
	 */
	@Test
	void testLoad_000() {
		try {
			File file = new File("test/resources/CustomPresets.xml");
			EasyPresets root = new EasyPresets();
			root.setLocaleName(tr("Custom Presets"));
			root.load(file);
			
			// check
			assertEquals("Custom Presets", root.getName());
			assertEquals(1, root.size());

			PresetsEntry group1 = root.get(0);
			assertTrue(group1 instanceof EasyPresets);
			{
				EasyPresets g = (EasyPresets)group1;
				assertEquals("カスタムプリセット", g.getName());
				assertEquals(2, g.size());
				
				PresetsEntry item1 = g.get(0);
				assertTrue(item1 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item1;
					assertEquals("カスタムトイレ", i.getName());
				}
				
				PresetsEntry item2 = g.get(1);
				assertTrue(item2 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item2;
					assertEquals("あづま屋", i.getName());
				}
			}
		}
		catch(Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * `EasyPresets-001.xml`
	 * トップグループが"Custom Presets"以外の階層化XML
	 */
	@Test
	void testLoad_001() {
		try {
			File file = new File("test/resources/EasyPresets-001.xml");
			EasyPresets root = new EasyPresets();
			root.setLocaleName(tr("Custom Presets"));
			root.load(file);
			
			// check
			assertEquals("Custom Presets", root.getName());
			assertEquals(3, root.size());
			
			PresetsEntry group1 = root.get(0);
			assertTrue(group1 instanceof EasyPresets);
			{
				EasyPresets g = (EasyPresets)group1;
				assertEquals("街道", g.getName());
				assertEquals(5, g.size());
				
				PresetsEntry item1 = g.get(0);
				assertTrue(item1 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item1;
					assertEquals("石柱 一里塚", i.getName());
				}
				
				PresetsEntry item5 = g.get(4);
				assertTrue(item5 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item5;
					assertEquals("中北道", i.getName());
				}
			}
			
			PresetsEntry i2 = root.get(1);
			assertTrue(i2 instanceof EasyPreset);
			{
				EasyPreset i = (EasyPreset)i2;
				assertEquals("あづま屋", i.getName());
			}

			PresetsEntry i3 = root.get(2);
			assertTrue(i3 instanceof EasyPreset);
			{
				EasyPreset i = (EasyPreset)i3;
				assertEquals("カスタムトイレ", i.getName());
			}
		}
		catch(Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * `EasyPresets-002.xml`
	 * トップグループが"Custom Presets"以外の階層化XML
	 */
	@Test
	void testLoad_002() {
		try {
			File file = new File("test/resources/EasyPresets-002.xml");
			EasyPresets root = new EasyPresets();
			root.setLocaleName(tr("Custom Presets"));
			root.load(file);
			
			// check
			assertEquals("Custom Presets", root.getName());
			assertEquals(1, root.size());
			
			PresetsEntry group1 = root.get(0);
			assertTrue(group1 instanceof EasyPresets);
			{
				EasyPresets g = (EasyPresets)group1;
				assertEquals("階層", g.getName());
				assertEquals(4, g.size());
				
				PresetsEntry group1_1 = g.get(0);
				assertTrue(group1_1 instanceof EasyPresets);
				{
					EasyPresets g1 = (EasyPresets)group1_1;
					assertEquals("街道", g1.getName());
					assertEquals(6, g1.size());
					
					PresetsEntry item1 = g1.get(0);
					assertTrue(item1 instanceof EasyPreset);
					{
						EasyPreset i = (EasyPreset)item1;
						assertEquals("石柱 一里塚", i.getName());
					}
					
					PresetsEntry group1_1_5 = g1.get(4);
					assertTrue(group1_1_5 instanceof EasyPresets);
					{
						EasyPresets g1_5 = (EasyPresets)group1_1_5;
						assertEquals("cyclelane", g1_5.getName());
						assertEquals(1, g1_5.size());
						
						PresetsEntry item1_5_1 = g1_5.get(0);
						assertTrue(item1_5_1 instanceof EasyPreset);
						{
							EasyPreset i = (EasyPreset)item1_5_1;
							assertEquals("cyclelane", i.getName());
						}
					}

					PresetsEntry item5 = g1.get(5);
					assertTrue(item5 instanceof EasyPreset);
					{
						EasyPreset i = (EasyPreset)item5;
						assertEquals("中北道", i.getName());
					}
				}
				
				PresetsEntry i2 = g.get(1);
				assertTrue(i2 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)i2;
					assertEquals("あづま屋", i.getName());
				}

				PresetsEntry e3 = g.get(2);
				assertTrue(e3 instanceof EasyPresets);
				{
					EasyPresets g3 = (EasyPresets)e3;
					assertEquals("トイレ", g3.getName());
					assertEquals(1, g3.size());
					
					PresetsEntry i1 = g3.get(0);
					assertTrue(i1 instanceof EasyPreset);
					{
						EasyPreset i = (EasyPreset)i1;
						assertEquals("公衆トイレ", i.getName());
					}
				}

				PresetsEntry i4 = g.get(3);
				assertTrue(i4 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)i4;
					assertEquals("カスタムトイレ", i.getName());
				}
			}
		}
		catch(Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * `EasyPresets-003.xml`
	 * トップグループが"Custom Presets"の階層化XML
	 */
	@Test
	void testLoad_003() {
		try {
			File file = new File("test/resources/EasyPresets-003.xml");
			EasyPresets root = new EasyPresets();
			root.setLocaleName(tr("Custom Presets"));
			root.load(file);
			
			// check
			assertEquals("Custom Presets", root.getName());
			assertEquals(4, root.size());
			
			PresetsEntry e1 = root.get(0);
			assertTrue(e1 instanceof EasyPresets);
			{
				EasyPresets g1 = (EasyPresets)e1;
				assertEquals("街道", g1.getName());
				assertEquals(6, g1.size());
				{
					PresetsEntry item1 = g1.get(0);
					assertTrue(item1 instanceof EasyPreset);
					{
						EasyPreset i = (EasyPreset)item1;
						assertEquals("石柱 一里塚", i.getName());
					}
					
					PresetsEntry group1_1_5 = g1.get(4);
					assertTrue(group1_1_5 instanceof EasyPresets);
					{
						EasyPresets g1_5 = (EasyPresets)group1_1_5;
						assertEquals("cyclelane", g1_5.getName());
						assertEquals(1, g1_5.size());
						
						PresetsEntry item1_5_1 = g1_5.get(0);
						assertTrue(item1_5_1 instanceof EasyPreset);
						{
							EasyPreset i = (EasyPreset)item1_5_1;
							assertEquals("cyclelane", i.getName());
						}
					}
		
					PresetsEntry item5 = g1.get(5);
					assertTrue(item5 instanceof EasyPreset);
					{
						EasyPreset i = (EasyPreset)item5;
						assertEquals("中北道", i.getName());
					}
				}
			}
			
			PresetsEntry i2 = root.get(1);
			assertTrue(i2 instanceof EasyPreset);
			{
				EasyPreset i = (EasyPreset)i2;
				assertEquals("あづま屋", i.getName());
			}
	
			PresetsEntry e3 = root.get(2);
			assertTrue(e3 instanceof EasyPresets);
			{
				EasyPresets g3 = (EasyPresets)e3;
				assertEquals("トイレ", g3.getName());
				assertEquals(1, g3.size());
				
				PresetsEntry i1 = g3.get(0);
				assertTrue(i1 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)i1;
					assertEquals("公衆トイレ", i.getName());
				}
			}
	
			PresetsEntry i4 = root.get(3);
			assertTrue(i4 instanceof EasyPreset);
			{
				EasyPreset i = (EasyPreset)i4;
				assertEquals("カスタムトイレ", i.getName());
			}
		}
		catch(Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * `EasyPresets-004.xml`
	 * トップグループなし
	 */
	@Test
	void testLoad_004() {
		try {
			File file = new File("test/resources/EasyPresets-004.xml");
			EasyPresets root = new EasyPresets();
			root.setLocaleName(tr("Custom Presets"));
			root.load(file);
			
			// check
			assertEquals("Custom Presets", root.getName());
			assertEquals(4, root.size());
			{
				PresetsEntry item1 = root.get(0);
				assertTrue(item1 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item1;
					assertEquals("石柱 一里塚", i.getName());
				}
				
				PresetsEntry item2 = root.get(1);
				assertTrue(item2 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item2;
					assertEquals("marler中山道", i.getName());
				}
	
				PresetsEntry item4 = root.get(3);
				assertTrue(item4 instanceof EasyPreset);
				{
					EasyPreset i = (EasyPreset)item4;
					assertEquals("馬籠の宿場", i.getName());
				}
			}
		}
		catch(Exception e) {
			fail(e.toString());
		}
	}
}
