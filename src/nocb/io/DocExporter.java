package nocb.io;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;

import nocb.data.Spell;
import nocb.main.CharacterSheet;
import nocb.ui.Toast;

public class DocExporter
{
	public static boolean exportSpells(CharacterSheet c)
	{
		if (c.getName().isBlank())
		{
			System.out.println("Cannot export character with a blank name.");
			return false;
		}
		try
		{
			List<Spell> allSpells = c.getAllSpells();
			Collections.sort(allSpells, (a, b) -> a.getName().compareTo(b.getName()));

			XWPFDocument out = new XWPFDocument();

			for (int lvl = 0; lvl <= 9; lvl++)
			{
				final int spellLvl = lvl;
				List<Spell> spellsAtLevel = allSpells.stream().filter(s -> s.getLevel() == spellLvl).toList();

				if (!spellsAtLevel.isEmpty())
				{
					for (Spell s : spellsAtLevel)
					{
						writeSpell(out, s);
					}

					XWPFParagraph lineParagraph = out.createParagraph();
					lineParagraph.setBorderBottom(Borders.DOUBLE);

					XWPFParagraph spacerParagraph = out.createParagraph();
					spacerParagraph.createRun().addCarriageReturn();
				}
			}

			out.write(new FileOutputStream(new File(c.getName() + " Spells.docx")));
			out.close();

			new Toast("Successfully Exported Spells Doc", 50);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null,
					"Failed to export spells odt file. Most likely it is open in another application.",
					"Export Doc Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	private static void writeSpell(XWPFDocument out, Spell s) throws Exception
	{
		XWPFParagraph spellTitleParagraph = out.createParagraph();
		writeTitle(spellTitleParagraph, s);

		XWPFParagraph spellParagraph = out.createParagraph();
		writeLevelAndSchool(spellParagraph, s);
		writeCastingTime(spellParagraph, s);
		writeRange(spellParagraph, s);
		writeComponents(spellParagraph, s);
		writeDuration(spellParagraph, s);
		writeText(spellParagraph, s);
		if (!s.getNotes().isBlank())
		{
			writeNote(spellParagraph, s);
		}
	}

	private static void writeTitle(XWPFParagraph p, Spell s)
	{
		XWPFRun name = getBoldRun(p, s.getName());

		name.setColor("FFFFFF"); // Red text
		// Access low-level OpenXML elements to apply shading
		CTPPr pPr = p.getCTP().getPPr();
		if (pPr == null)
		{
			pPr = p.getCTP().addNewPPr();
		}
		CTShd shading = pPr.isSetShd() ? pPr.getShd() : pPr.addNewShd();
		shading.setVal(STShd.CLEAR);
		shading.setColor("auto");
		shading.setFill("EC008C");
		name.addCarriageReturn();
	}

	private static void writeLevelAndSchool(XWPFParagraph p, Spell s)
	{
		getItalicRun(p,
				(s.getLevel() == 0 ? s.getSchool() + " cantrip" : "Level " + s.getLevel() + " " + s.getSchool()))
						.addCarriageReturn();
	}

	private static void writeCastingTime(XWPFParagraph p, Spell s)
	{
		getBoldRun(p, "Casting Time: ");
		String text = s.getCastTime();
		if (text.equals("A"))
		{
			text = "Action";
		}
		else if (text.equals("B"))
		{
			text = "Bonus Action";
		}
		else if (text.equals("R"))
		{
			text = "Reaction";
		}
		if (s.isRitual())
		{
			text += " (Ritual)";
		}
		XWPFRun time = getRun(p, text);

		String triggerText = (s.getTrigger().isBlank() ? "" : " (" + s.getTrigger() + ")");
		if (triggerText.isBlank())
		{
			time.addCarriageReturn();
		}
		else
		{
			getItalicRun(p, triggerText).addCarriageReturn();
		}
	}

	private static void writeRange(XWPFParagraph p, Spell s)
	{
		getBoldRun(p, "Range: ");
		getRun(p, s.getRange()).addCarriageReturn();
	}

	private static void writeComponents(XWPFParagraph p, Spell s)
	{
		getBoldRun(p, "Components: ");
		XWPFRun components = getRun(p, s.getComponents());
		if (s.getMaterials().isBlank())
		{
			components.addCarriageReturn();
		}
		else
		{
			getItalicRun(p, " (" + s.getMaterials() + ")").addCarriageReturn();
		}
	}

	private static void writeDuration(XWPFParagraph p, Spell s)
	{
		getBoldRun(p, "Duration: ");
		getRun(p, s.getDuration()).addCarriageReturn();
	}

	private static void writeText(XWPFParagraph p, Spell s)
	{
		String[] split = s.getText().split("<br>");
		XWPFRun textRun = p.createRun();
		for (String s2 : split)
		{
			textRun.setText(s2);
			textRun.addBreak();
		}
	}

	private static void writeNote(XWPFParagraph p, Spell s)
	{
		getBoldRun(p, "Note: ");
		getItalicRun(p, s.getNotes()).addCarriageReturn();
	}

	private static XWPFRun getRun(XWPFParagraph p, String text)
	{
		XWPFRun run = p.createRun();
		run.setText(text);
		return run;
	}

	private static XWPFRun getBoldRun(XWPFParagraph p, String text)
	{
		XWPFRun run = p.createRun();
		run.setText(text);
		run.setBold(true);
		return run;
	}

	private static XWPFRun getItalicRun(XWPFParagraph p, String text)
	{
		XWPFRun run = p.createRun();
		run.setText(text);
		run.setItalic(true);
		return run;
	}
}
