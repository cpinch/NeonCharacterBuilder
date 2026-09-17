package nocb.io;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nocb.data.Background;
import nocb.data.CharacterClass;
import nocb.data.Feat;
import nocb.data.Homeworld;
import nocb.data.Language;
import nocb.data.Species;
import nocb.data.Spell;
import nocb.data.SpellList;
import nocb.main.CharacterSheet;
import nocb.ui.Toast;

public class JsonDataLoader
{
	private static final File dataDirectory = new File("./data");
	private static final String classFolder = "classes";
	private static final String speciesFolder = "species";
	private static final String spellFolder = "spells";
	private static final String spellListFolder = "spellLists";
	private static final String backgroundFolder = "backgrounds";
	private static final String featFolder = "feats";
	private static final String homeworldFolder = "homeworlds";
	private static final String languageFolder = "languages";

	public static void loadAllFiles()
	{
		// Get all data libraries in the dataDirectory
		List<File> libraries = new ArrayList<>();
		for (File f : dataDirectory.listFiles())
		{
			if (f.getName().endsWith(".nlib"))
			{
				libraries.add(f);
			}
		}
		// Sort in descending order so the first things of each category we load come
		// from the highest priority files
		// This way, any duplicates between libraries we ignore the lower priority
		// content as expected
		libraries.sort((a, b) -> Integer.compare(getPriorityNum(b.getName()), getPriorityNum(a.getName())));

		List<byte[]> allClasses = new ArrayList<>();
		List<byte[]> allSpecies = new ArrayList<>();
		List<byte[]> allSpells = new ArrayList<>();
		List<byte[]> allSpellLists = new ArrayList<>();
		List<byte[]> allBackgrounds = new ArrayList<>();
		List<byte[]> allFeats = new ArrayList<>();
		List<byte[]> allHomeworlds = new ArrayList<>();
		List<byte[]> allLanguages = new ArrayList<>();
		for (File lib : libraries)
		{
			try (ZipFile libZip = new ZipFile(lib))
			{
				Enumeration<? extends ZipEntry> entries = libZip.entries();

				// We want to load things in a specific order that might not be this same order,
				// so iterate through once, mapping the data by name
				while (entries.hasMoreElements())
				{
					ZipEntry entry = entries.nextElement();

					if (!entry.isDirectory())
					{
						try (InputStream in = libZip.getInputStream(entry))
						{
							if (entry.getName().startsWith(classFolder))
							{
								allClasses.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(speciesFolder))
							{
								allSpecies.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(spellFolder))
							{
								allSpells.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(spellListFolder))
							{
								allSpellLists.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(backgroundFolder))
							{
								allBackgrounds.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(featFolder))
							{
								allFeats.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(homeworldFolder))
							{
								allHomeworlds.add(readAllBytes(in));
							}
							else if (entry.getName().startsWith(languageFolder))
							{
								allLanguages.add(readAllBytes(in));
							}
							else
							{
								System.out.println("Unknown data file " + entry.getName() + " in library file "
										+ libZip.getName());
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		// Load all spells
		for (byte[] f : allSpells)
		{
			Spell.loadSpell(readDataFromBytes(f));
		}
		Spell.sortAll();

		// Load all spell lists
		for (byte[] f : allSpellLists)
		{
			SpellList.loadSpellList(readDataFromBytes(f));
		}
		SpellList.sortAll();

		// Load all languages
		for (byte[] f : allLanguages)
		{
			Language.loadLanguages(readDataFromBytes(f));
		}
		Language.sortAll();

		// Load all homeworlds
		for (byte[] f : allHomeworlds)
		{
			Homeworld.loadHomeworld(readDataFromBytes(f));
		}
		Homeworld.sortAll();

		// Load all feats
		for (byte[] f : allFeats)
		{
			Feat.loadFeat(readDataFromBytes(f));
		}
		Feat.sortAll();

		// Load all backgrounds
		for (byte[] f : allBackgrounds)
		{
			Background.loadBackground(readDataFromBytes(f));
		}
		Background.sortAll();

		// Load all species
		for (byte[] f : allSpecies)
		{
			Species.loadSpecies(readDataFromBytes(f));
		}
		Species.sortAll();

		// Load all classes
		for (byte[] f : allClasses)
		{
			CharacterClass.loadClass(readDataFromBytes(f));
		}
		CharacterClass.sortAll();
	}

	private static int getPriorityNum(String filename)
	{
		String priorityNum = filename.substring(0, filename.indexOf("_"));
		try
		{
			return Integer.parseInt(priorityNum);
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	private static byte[] readAllBytes(InputStream is) throws IOException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = is.read(data, 0, data.length)) != -1)
		{
			buffer.write(data, 0, nRead);
		}
		return buffer.toByteArray();
	}

	private static JSONObject readDataFromBytes(byte[] f)
	{
		String data = "";
		try
		{
			data = new String(f, "UTF-8");
			return new JSONObject(data);
		}
		catch (JSONException e)
		{
			System.out.println("Encountered an error parsing json " + data);
			e.printStackTrace();
			return null;
		}
		catch (UnsupportedEncodingException e)
		{
			System.out.println("Could not read library file contents.");
			e.printStackTrace();
			return null;
		}
	}

	private static JSONObject readDataFromFile(File f)
	{
		String data = "";
		try (FileInputStream fis = new FileInputStream(f))
		{
			byte[] buffer = new byte[(int) f.length()];
			new DataInputStream(fis).readFully(buffer);
			data = new String(buffer, "UTF-8");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			return new JSONObject(data);
		}
		catch (JSONException e)
		{
			System.out.println("Encountered an error parting file " + f.getName());
			e.printStackTrace();
			return null;
		}
	}

	private static void writeDataToFile(File f, JSONObject data)
	{
		try (FileWriter out = new FileWriter(f))
		{
			out.write(data.toString(4));
			out.flush();
			new Toast("Saved character succcessfully", -30);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Failed to save character. Most likely the nchar file is open in another application.",
					"Character save failed.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static final List<String> jsonArrayToStringArray(JSONArray in)
	{
		List<String> out = new ArrayList<>();
		if (in != null)
		{
			for (int i = 0; i < in.length(); i++)
			{
				out.add(in.getString(i));
			}
		}
		return out;
	}

	public static final List<JSONObject> jsonArrayToObjectArray(JSONArray in)
	{
		List<JSONObject> out = new ArrayList<>();
		if (in != null)
		{
			for (int i = 0; i < in.length(); i++)
			{
				out.add(in.getJSONObject(i));
			}
		}
		return out;
	}

	public static boolean loadCharacterStateFromFile(CharacterSheet sheet, File f)
	{
		return sheet.loadState(readDataFromFile(f));
	}

	public static void saveCharacterStateToFile(CharacterSheet sheet, File f)
	{
		writeDataToFile(f, sheet.saveState());
	}

	public static void saveAllCustomDataFiles()
	{
		// TODO post1.0 - backup current custom just in case? delete backup at end if
		// all went
		// well

		try (ZipOutputStream out = new ZipOutputStream(
				new FileOutputStream(new File(dataDirectory, "100_Custom.nlib"))))
		{
			for (Spell s : Spell.getAllSpells())
			{
				if (s.isCustom())
				{
					byte[] data = s.saveSpell().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(spellFolder + "/" + s.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (SpellList sl : SpellList.getAllSpellLists())
			{
				if (sl.isCustom())
				{
					byte[] data = sl.saveSpellList().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(spellListFolder + "/" + sl.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Language l : Language.getAllLanguages())
			{
				if (l.isCustom())
				{
					byte[] data = l.saveLanguage().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(languageFolder + "/" + l.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Homeworld h : Homeworld.getAllHomeworlds())
			{
				if (h.isCustom())
				{
					byte[] data = h.saveHomeworld().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(homeworldFolder + "/" + h.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Feat f : Feat.getAllLoadedFeats())
			{
				if (f.isCustom())
				{
					byte[] data = f.saveFeat().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(featFolder + "/" + f.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Background b : Background.getAllBackgrounds())
			{
				if (b.isCustom())
				{
					byte[] data = b.saveBackground().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(backgroundFolder + "/" + b.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Species s : Species.getAllSpecies())
			{
				if (s.isCustom())
				{
					byte[] data = s.saveSpecies().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(speciesFolder + "/" + s.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (CharacterClass c : CharacterClass.getAllClasses())
			{
				if (c.isCustom())
				{
					byte[] data = c.saveClass().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(classFolder + "/" + c.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Encountered an error while saving custom data file",
					"Error Saving Custom Data", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
