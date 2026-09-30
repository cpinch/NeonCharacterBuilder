package ncb.ui.DataEditor;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.enums.Skill;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Language;
import ncb.data.loadables.Spell;
import ncb.ui.CollapsablePanel;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public abstract class EditPanel extends NoHorizontalScrollPanel
{
	public abstract void setSelected(Customizable sel);

	protected abstract String getItemName();

	protected abstract void setItemName(String s);

	private static final long serialVersionUID = -3410949620714799628L;

	protected final GridBagConstraints c = UILib.getStandardGBC();
	protected final List<HasLinkedProperty> linkedProperties = new ArrayList<>();
	protected CollapsablePanel collapse;

	public EditPanel()
	{
		setLayout(new GridBagLayout());

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Name: ", c, Color.white, Color.black,
				() -> getItemName(), (s) -> setItemName(s)));
		c.gridy++;
	}

	public EditPanel(boolean startCollapsed)
	{
		setLayout(new GridBagLayout());

		collapse = new CollapsablePanel(startCollapsed);

		linkedProperties.add(UILib.addLabeledLinkedTextField(collapse.headerPanel, "Name: ", c, Color.white,
				Color.black, () -> getItemName(), (s) -> setItemName(s)));

		collapse.bodyPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		add(collapse, c);
	}

	protected void showErrorMessage(List<String> invalid, String type)
	{
		if (!invalid.isEmpty())
		{
			StringBuilder msg = new StringBuilder();
			msg.append(String.join(", ", invalid));
			if (invalid.size() == 1)
			{
				msg.append(" was not a valid ");
			}
			else
			{
				msg.append(" were not valid items of type ");
			}
			msg.append(type);
			msg.append(".");
			JOptionPane.showMessageDialog(null, msg.toString(), "Invalid Entries", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected List<Ability> parseAbilities(String[] strings)
	{
		List<Ability> abilities = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String s : strings)
		{
			if (!s.isBlank())
			{
				try
				{
					abilities.add(Ability.valueOf(s.trim()));
				}
				catch (Exception e)
				{
					invalid.add(s);
				}
			}
		}
		showErrorMessage(invalid, "ability");
		return abilities;
	}

	protected List<Skill> parseSkills(String[] strings)
	{
		List<Skill> skills = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String s : strings)
		{
			if (!s.isBlank())
			{
				try
				{
					skills.add(Skill.skillByName(s.trim()));
				}
				catch (Exception e)
				{
					invalid.add(s);
				}
			}
		}
		showErrorMessage(invalid, "skill");
		return skills;
	}

	protected List<ArmorTraining> parseArmor(String[] strings)
	{
		List<ArmorTraining> armor = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String s : strings)
		{
			if (!s.isBlank())
			{
				try
				{
					armor.add(ArmorTraining.valueOf(s.trim()));
				}
				catch (Exception e)
				{
					invalid.add(s);
				}
			}
		}
		showErrorMessage(invalid, "armor type");
		return armor;
	}

	protected List<Spell> parseSpells(String[] strings)
	{
		List<Spell> spell = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String s : strings)
		{
			if (!s.isBlank())
			{
				try
				{
					spell.add(getSpellFromText(s.trim()));
				}
				catch (Exception e)
				{
					invalid.add(s);
				}
			}
		}
		showErrorMessage(invalid, "spell");
		return spell;
	}

	protected List<Language> parseLanguages(String[] strings)
	{
		List<Language> langs = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String s : strings)
		{
			if (!s.isBlank())
			{
				try
				{
					Language l = Language.getByName(s.trim());
					if (l == null)
					{
						invalid.add(s);
					}
					else
					{
						langs.add(l);
					}
				}
				catch (Exception e)
				{
					invalid.add(s);
				}
			}
		}
		showErrorMessage(invalid, "language");
		return langs;
	}

	protected String spellToText(Spell spell)
	{
		return spell.getName() + (spell.getNotes().isBlank() ? "" : " (" + spell.getNotes() + ")")
				+ (spell.getBaseName().isBlank() ? "" : " [" + spell.getBaseName() + "]");
	}

	private Spell getSpellFromText(String text) throws Exception
	{
		String spellName = text.trim();
		String altName = "";
		String notes = "";
		if (text.contains("("))
		{
			notes = text.substring(text.indexOf('(') + 1, text.indexOf(')')).trim();
			spellName = text.substring(0, text.indexOf('(')).trim();
		}
		if (text.contains("["))
		{
			spellName = text.substring(text.indexOf('[') + 1, text.indexOf(']')).trim();
			altName = text.substring(0, text.indexOf('[')).trim();
		}

		Spell s = null;
		if (altName.isBlank() && notes.isBlank())
		{
			s = Spell.getByName(spellName);
		}
		else if (altName.isBlank() && !notes.isBlank())
		{
			s = Spell.getCopyByName(spellName, notes);
		}
		else if (!altName.isBlank() && notes.isBlank())
		{
			s = Spell.getByAltName(altName, spellName);
		}
		else
		{
			s = Spell.getCopyByAltName(altName, spellName, notes);
		}
		if (s == null)
		{
			throw new Exception("Failed to load spell");
		}
		return s;
	}
}
