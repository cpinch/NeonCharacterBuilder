package ncb.io;

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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ncb.data.loadables.Background;
import ncb.data.loadables.CharacterClass;
import ncb.data.loadables.Feat;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Language;
import ncb.data.loadables.Selectable;
import ncb.data.loadables.Species;
import ncb.data.loadables.Spell;
import ncb.data.loadables.SpellList;
import ncb.data.loadables.Subclass;
import ncb.main.CharacterSheet;
import ncb.ui.Toast;

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
	private static final String selectableFolder = "selectables";
	private static final String subclassFolder = "subclasses";

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

		Map<String, List<FileData>> allFiles = Map.of(classFolder, new ArrayList<>(), speciesFolder, new ArrayList<>(),
				spellFolder, new ArrayList<>(), spellListFolder, new ArrayList<>(), backgroundFolder, new ArrayList<>(),
				featFolder, new ArrayList<>(), homeworldFolder, new ArrayList<>(), languageFolder, new ArrayList<>(),
				selectableFolder, new ArrayList<>(), subclassFolder, new ArrayList<>());
		for (File lib : libraries)
		{
			try (ZipFile libZip = new ZipFile(lib))
			{
				Enumeration<? extends ZipEntry> entries = libZip.entries();
				boolean custom = libZip.getName().contains("Custom");

				// We want to load things in a specific order that might not be this same order,
				// so iterate through once, mapping the data by name
				while (entries.hasMoreElements())
				{
					ZipEntry entry = entries.nextElement();

					if (!entry.isDirectory())
					{
						try (InputStream in = libZip.getInputStream(entry))
						{
							String folder = entry.getName().substring(0, entry.getName().indexOf('/'));
							allFiles.get(folder).add(new FileData(readAllBytes(in), custom));
						}
						catch (Exception e)
						{
							System.err.println("Error reading data file " + entry.getName() + " in library file "
									+ libZip.getName());
						}
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		allFiles.get(spellFolder).forEach(f -> Spell.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(spellListFolder).forEach(f -> SpellList.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(languageFolder).forEach(f -> Language.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(homeworldFolder).forEach(f -> Homeworld.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(featFolder).forEach(f -> Feat.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(selectableFolder).forEach(f -> Selectable.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(backgroundFolder).forEach(f -> Background.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(speciesFolder).forEach(f -> Species.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(subclassFolder).forEach(f -> Subclass.loadFromFile(readDataFromBytes(f.bytes), f.custom));
		allFiles.get(classFolder).forEach(f -> CharacterClass.loadFromFile(readDataFromBytes(f.bytes), f.custom));

		Spell.sortAll();
		SpellList.sortAll();
		Language.sortAll();
		Homeworld.sortAll();
		Feat.sortAll();
		Selectable.sortAll();
		Background.sortAll();
		Species.sortAll();
		Subclass.sortAll();
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
			System.err.println("Data file " + filename + " does not have a priority #. Defaulting to 0.");
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
			System.err.println("Encountered an error parsing json " + data);
			return null;
		}
		catch (UnsupportedEncodingException e)
		{
			System.err.println("Could not read library file contents.");
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
			System.err.println("Encountered an error reading character save file " + data);
		}
		catch (IOException e)
		{
			System.err.println("Could not read character save file contents.");
		}
		try

		{
			return new JSONObject(data);
		}
		catch (JSONException e)
		{
			System.err.println("Encountered an error parsing json " + f.getName());
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

	public static void loadCharacterStateFromFile(CharacterSheet sheet, File f)
	{
		sheet.loadState(readDataFromFile(f));
	}

	public static void saveCharacterStateToFile(CharacterSheet sheet, File f)
	{
		writeDataToFile(f, sheet.saveState());
	}

	public static void saveAllCustomDataFiles()
	{
		File newCustomFile = new File(dataDirectory, "100_Custom-new.nlib");
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(newCustomFile)))
		{
			for (Spell s : Spell.getAllSpells())
			{
				if (s.isCustom())
				{
					byte[] data = s.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(spellFolder + "/" + s.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (SpellList sl : SpellList.getAllSpellLists())
			{
				if (sl.isCustom())
				{
					byte[] data = sl.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(spellListFolder + "/" + sl.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Language l : Language.getAllLanguages())
			{
				if (l.isCustom())
				{
					byte[] data = l.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(languageFolder + "/" + l.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Homeworld h : Homeworld.getAllHomeworlds())
			{
				if (h.isCustom())
				{
					byte[] data = h.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(homeworldFolder + "/" + h.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Feat f : Feat.getAllLoadedFeats())
			{
				if (f.isCustom())
				{
					byte[] data = f.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(featFolder + "/" + f.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Selectable s : Selectable.getAllSelectables())
			{
				if (s.isCustom())
				{
					byte[] data = s.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					// Selectables are uniquely named by type + name
					ZipEntry ze = new ZipEntry(selectableFolder + "/" + s.getType() + "-" + s.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Background b : Background.getAllBackgrounds())
			{
				if (b.isCustom())
				{
					byte[] data = b.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(backgroundFolder + "/" + b.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Species s : Species.getAllSpecies())
			{
				if (s.isCustom())
				{
					byte[] data = s.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(speciesFolder + "/" + s.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (Subclass s : Subclass.getAllSubclasses())
			{
				if (s.isCustom())
				{
					byte[] data = s.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(subclassFolder + "/" + s.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
			for (CharacterClass c : CharacterClass.getAllClasses())
			{
				if (c.isCustom())
				{
					byte[] data = c.saveConfig().toString(4).getBytes(StandardCharsets.UTF_8);
					ZipEntry ze = new ZipEntry(classFolder + "/" + c.getName() + ".json");
					out.putNextEntry(ze);
					out.write(data, 0, data.length);
				}
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Encountered an error while saving custom data file. Your prior custom file has not been modified. See data/100_Custom-new.nlib for your new changes.",
					"Error Saving Custom Data", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try
		{
			Files.move(newCustomFile.toPath(), new File(dataDirectory, "100_Custom.nlib").toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null,
					"Encountered an error while overriding custom data file. Do you have it open in another program? Your prior custom file has not been modified. See data/100_Custom-new.nlib for your new changes.",
					"Error Saving Custom Data", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static class FileData
	{
		final byte[] bytes;
		final boolean custom;

		public FileData(byte[] bytes, boolean custom)
		{
			this.bytes = bytes;
			this.custom = custom;
		}
	}
}
