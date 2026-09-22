package nocb.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;
import nocb.main.PropertyListener;

/**
 * This class is a baseline for all sorts of "things that you can pick that
 * affect your character" and just provides standardized getters/setters for
 * them so each class can have a minimal implementation.
 */
public class Feature extends AlertsChanges
{
	private static int nId = 1;
	private final int id;

	// Basic
	protected String name = "";
	protected String text = "";
	protected int speedMod = 0;
	protected String sheetNotes = "";
	protected int extraHPLvl1 = 0;
	protected int extraHPPerLevel = 0;
	protected boolean givesHalfProfAll = false;
	protected boolean givesProfToInit = false;

	// List
	protected final List<Ability> saveProfs = new ArrayList<>();
	protected final List<String> weaponProfs = new ArrayList<>();
	protected final List<String> toolProfs = new ArrayList<>();
	protected final List<ArmorProf> armorProfs = new ArrayList<>();
	protected final List<Skill> skillsGranted = new ArrayList<>();
	protected final List<Spell> spellsGranted = new ArrayList<>();
	protected final List<Language> languagesGranted = new ArrayList<>();
	protected final List<String> resistancesGranted = new ArrayList<>();
	protected final List<Ability> abilitiesToAC = new ArrayList<>();

	// Selections
	protected final List<String> resistanceOptions = new ArrayList<>();
	protected String resistanceSelected = "";
	protected final List<Skill> skillOptions = new ArrayList<>();
	protected int skillSelectCount = 0;
	protected final List<Skill> skillsSelected = new ArrayList<>();
	protected final List<Skill> skillExpertOptions = new ArrayList<>();
	protected int skillExpertCount = 0;
	protected final List<Skill> skillExpertSelected = new ArrayList<>();
	protected final List<String> languageOptionNames = new ArrayList<>();
	protected int langSelectCount = 0;
	protected final List<Language> languagesSelected = new ArrayList<>();
	protected String selectableName = "";
	protected Selectable selected;
	protected String featTraitName = "";
	protected Feat feat;
	protected boolean featIgnoresPrereqs = false;
	protected final List<Spell> specificSpellChoices = new ArrayList<>();
	protected Spell chosenSpell;

	// Complex
	protected final Map<String, String> resistancesByHomeworld = new HashMap<>();
	protected final Map<Integer, List<SpellChoice>> spellChoices = new HashMap<>();
	protected final Map<Skill, Ability> abilitiesAddToSkills = new HashMap<>();

	protected boolean custom = false;

	public Feature()
	{
		this.id = nId++;
	}

	protected void loadFromData(JSONObject data)
	{
		// Basic
		this.name = data.getString("name");
		this.text = data.optString("text", "");
		this.speedMod = data.optInt("speedMod", 0);
		this.sheetNotes = data.optString("sheetNotes", "");
		this.extraHPPerLevel = data.optInt("extraHPPerLevel", 0);
		this.extraHPLvl1 = data.optInt("extraHPLvl1", 0);
		this.givesHalfProfAll = data.optBoolean("halfProfAll", false);
		this.givesProfToInit = data.optBoolean("profInit", false);

		// List
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("saves"))
				.forEach(save -> this.saveProfs.add(Ability.valueOf(save)));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("weapons")).forEach(wep -> this.weaponProfs.add(wep));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("tools")).forEach(tool -> this.toolProfs.add(tool));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("armor"))
				.forEach(armor -> this.armorProfs.add(ArmorProf.valueOf(armor)));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("skills"))
				.forEach(s -> skillsGranted.add(Skill.skillByName(s)));
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("grantsSpells"))
				.forEach(sp -> this.spellsGranted.add(Spell.getFromJSONObject(sp)));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("langs"))
				.forEach(l -> this.languagesGranted.add(Language.getByName(l)));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("res")).forEach(r -> resistancesGranted.add(r));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("acAbilities"))
				.forEach(a -> abilitiesToAC.add(Ability.valueOf(a)));

		// Selections
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("resOptions"))
				.forEach(res -> this.resistanceOptions.add(res));
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("skillOptions"))
				.forEach(so -> this.skillOptions.add(Skill.skillByName(so)));
		this.skillSelectCount = data.optInt("skillCount", 0);
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("skillExpertOptions"))
				.forEach(so -> this.skillExpertOptions.add(Skill.skillByName(so)));
		this.skillExpertCount = data.optInt("skillExpertCount", 0);
		JsonDataLoader.jsonArrayToStringArray(data.optJSONArray("langOptions"))
				.forEach(l -> this.languageOptionNames.add(l));
		this.langSelectCount = data.optInt("langSelectCount", 0);
		this.selectableName = data.optString("selectable", "");
		this.featTraitName = data.optString("featTrait", "");
		this.featIgnoresPrereqs = data.optBoolean("featIgnores", false);
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("specificSpells"))
				.forEach(s -> this.specificSpellChoices.add(Spell.getFromJSONObject(s)));

		// Complex
		JSONArray resHomes = data.optJSONArray("resHome");
		if (resHomes != null)
		{
			for (int i = 0; i < resHomes.length(); i++)
			{
				JSONObject resH = resHomes.getJSONObject(i);
				this.resistancesByHomeworld.put(resH.getString("trait"), resH.getString("res"));
			}
		}
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("spellChoices")).forEach(sp ->
		{
			List<SpellChoice> sc = new ArrayList<>();
			int count = sp.getInt("count");
			SpellList spellList = SpellList.getForClass(sp.optString("spellList", name));
			for (int i = 0; i < count; i++)
			{
				sc.add(new SpellChoice(spellList, i));
			}
			int spL = sp.getInt("level");
			this.spellChoices.put(spL, sc);
		});
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("addAbilitesToSkills"))
				.forEach(as -> abilitiesAddToSkills.put(Skill.skillByName(as.getString("skill")),
						Ability.valueOf(as.getString("ability"))));

		this.custom = data.optBoolean("custom", false);
	}

	public JSONObject saveFeature()
	{
		JSONObject data = new JSONObject();

		// Basic
		data.put("name", name);
		if (!text.isBlank())
		{
			data.put("text", text);
		}
		if (speedMod > 0)
		{
			data.put("speedMod", speedMod);
		}
		if (!sheetNotes.isBlank())
		{
			data.put("sheetNotes", sheetNotes);
		}
		if (extraHPPerLevel > 0)
		{
			data.put("extraHPPerLevel", extraHPPerLevel);
		}
		if (extraHPLvl1 > 0)
		{
			data.put("extraHPLvl1", extraHPLvl1);
		}
		if (givesHalfProfAll)
		{
			data.put("halfProfAll", true);
		}
		if (givesProfToInit)
		{
			data.put("profInit", true);
		}

		// List
		if (!saveProfs.isEmpty())
		{
			JSONArray saves = new JSONArray();
			saveProfs.forEach(save -> saves.put(save.toString()));
			data.put("saves", saves);
		}
		if (!weaponProfs.isEmpty())
		{
			JSONArray weapons = new JSONArray();
			weaponProfs.forEach(wep -> weapons.put(wep));
			data.put("weapons", weapons);
		}
		if (!toolProfs.isEmpty())
		{
			JSONArray tools = new JSONArray();
			toolProfs.forEach(tool -> tools.put(tool));
			data.put("tools", tools);
		}
		if (!armorProfs.isEmpty())
		{
			JSONArray armor = new JSONArray();
			armorProfs.forEach(ap -> armor.put(ap.toString()));
			data.put("armor", armor);
		}
		if (!skillsGranted.isEmpty())
		{
			JSONArray skills = new JSONArray();
			skillsGranted.forEach(skill -> skills.put(skill.toString()));
			data.put("skills", skills);
		}
		if (!spellsGranted.isEmpty())
		{
			JSONArray spells = new JSONArray();
			spellsGranted.forEach(spell -> spells.put(spell.toJSONObject()));
			data.put("grantsSpells", spells);
		}
		if (!languagesGranted.isEmpty())
		{
			JSONArray langs = new JSONArray();
			languagesGranted.forEach(lang -> langs.put(lang.getName()));
			data.put("langs", langs);
		}
		if (!resistancesGranted.isEmpty())
		{
			JSONArray res = new JSONArray();
			resistancesGranted.forEach(r -> res.put(r));
			data.put("res", res);
		}
		if (!abilitiesToAC.isEmpty())
		{
			JSONArray abls = new JSONArray();
			abilitiesToAC.forEach(a -> abls.put(a.toString()));
			data.put("acAbilities", abls);
		}

		// Selections
		if (!resistanceOptions.isEmpty())
		{
			JSONArray resOptions = new JSONArray();
			resistanceOptions.forEach(r -> resOptions.put(r));
			data.put("resOptions", resOptions);
		}
		if (!skillOptions.isEmpty())
		{
			JSONArray so = new JSONArray();
			skillOptions.forEach(s -> so.put(s.toString()));
			data.put("skillOptions", so);
		}
		if (skillSelectCount > 0)
		{
			data.put("skillCount", skillSelectCount);
		}
		if (!skillExpertOptions.isEmpty())
		{
			JSONArray seo = new JSONArray();
			skillExpertOptions.forEach(s -> seo.put(s.toString()));
			data.put("skillExpertOptions", seo);
		}
		if (skillExpertCount > 0)
		{
			data.put("skillExpertCount", skillExpertCount);
		}
		if (!languageOptionNames.isEmpty())
		{
			JSONArray langOptions = new JSONArray();
			languageOptionNames.forEach(l -> langOptions.put(l));
			data.put("langOptions", langOptions);
		}
		if (langSelectCount > 0)
		{
			data.put("langSelectCount", langSelectCount);
		}
		if (!selectableName.isBlank())
		{
			data.put("selectable", selectableName);
		}
		if (!featTraitName.isBlank())
		{
			data.put("featTrait", featTraitName);
			if (featIgnoresPrereqs)
			{
				data.put("featIgnores", true);
			}
		}
		if (!specificSpellChoices.isEmpty())
		{
			JSONArray spellOpts = new JSONArray();
			specificSpellChoices.forEach(spell ->
			{
				JSONObject sp = new JSONObject();
				sp.put("spell", spell.getName());
				if (!spell.getNotes().isBlank())
				{
					sp.put("note", spell.getNotes());
				}
				if (!spell.getBaseName().isBlank())
				{
					sp.put("baseName", spell.getBaseName());
				}
				spellOpts.put(sp);
			});
			data.put("specificSpells", spellOpts);
		}

		// Complex
		if (!resistancesByHomeworld.isEmpty())
		{
			JSONArray resHomes = new JSONArray();
			for (Map.Entry<String, String> resH : resistancesByHomeworld.entrySet())
			{
				JSONObject rH = new JSONObject();
				rH.put("trait", resH.getKey());
				rH.put("res", resH.getValue());
				resHomes.put(rH);
			}
			data.put("resHome", resHomes);
		}
		if (!spellChoices.isEmpty())
		{
			JSONArray sc = new JSONArray();
			for (Map.Entry<Integer, List<SpellChoice>> sce : spellChoices.entrySet())
			{
				JSONObject spC = new JSONObject();
				spC.put("level", sce.getKey());
				spC.put("count", sce.getValue().size());
				spC.put("spellList", sce.getValue().get(0).getSpellList().getName());
				sc.put(spC);
			}
			data.put("spellChoices", sc);
		}
		if (!abilitiesAddToSkills.isEmpty())
		{
			JSONArray aats = new JSONArray();
			for (Map.Entry<Skill, Ability> sta : abilitiesAddToSkills.entrySet())
			{
				JSONObject skillAndAbility = new JSONObject();
				skillAndAbility.put("skill", sta.getKey().toString());
				skillAndAbility.put("ability", sta.getValue().toString());
				aats.put(skillAndAbility);
			}
			data.put("addAbilitiesToSkills", aats);
		}

		if (isCustom())
		{
			data.put("custom", true);
		}

		return data;
	}

	public int getId()
	{
		return id;
	}

	// Standard getters/setters
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (!this.name.equals(name))
		{
			this.name = name;
			setCustom(true);
		}
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		if (!this.text.equals(text))
		{
			this.text = text;
			setCustom(true);
		}
	}

	public String getSheetNotes()
	{
		return sheetNotes;
	}

	public void setSheetNotes(String notes)
	{
		if (!this.sheetNotes.equals(notes))
		{
			this.sheetNotes = notes;
			setCustom(true);
		}
	}

	public List<Skill> getSkillsGranted()
	{
		return skillsGranted;
	}

	public void setSkillsGranted(List<Skill> skills)
	{
		if (!this.skillsGranted.equals(skills))
		{
			skillsGranted.clear();
			skillsGranted.addAll(skills);
			setCustom(true);
		}
	}

	public List<Ability> getSaveProfs()
	{
		return saveProfs;
	}

	public void setSaveProfs(List<Ability> saves)
	{
		if (!this.saveProfs.equals(saves))
		{
			saveProfs.clear();
			saveProfs.addAll(saves);
			setCustom(true);
		}
	}

	public List<String> getWeaponProfs()
	{
		return weaponProfs;
	}

	public void setWeaponProfs(List<String> weapons)
	{
		if (!this.weaponProfs.equals(weapons))
		{
			weaponProfs.clear();
			weaponProfs.addAll(weapons);
			setCustom(true);
		}
	}

	public List<String> getToolProfs()
	{
		return toolProfs;
	}

	public void setToolProfs(List<String> tools)
	{
		if (!this.toolProfs.equals(tools))
		{
			toolProfs.clear();
			toolProfs.addAll(tools);
			setCustom(true);
		}
	}

	public List<ArmorProf> getArmorProfs()
	{
		return armorProfs;
	}

	public void setArmorProfs(List<ArmorProf> armors)
	{
		if (!this.armorProfs.equals(armors))
		{
			armorProfs.clear();
			armorProfs.addAll(armors);
			setCustom(true);
		}
	}

	public List<Ability> getAbilitiesToAC()
	{
		return abilitiesToAC;
	}

	public void setAbilitiesToAC(List<Ability> abilities)
	{
		if (abilitiesToAC == null || (abilities != null && !abilitiesToAC.equals(abilities)))
		{
			abilitiesToAC.clear();
			abilitiesToAC.addAll(abilities);
			setCustom(true);
		}
	}

	public List<Spell> getSpellsGranted()
	{
		return spellsGranted;
	}

	public void setSpellsGranted(List<Spell> spells)
	{
		if (!spellsGranted.equals(spells))
		{
			spellsGranted.clear();
			spellsGranted.addAll(spells);
			setCustom(true);
		}
	}

	public int getSpeedMod()
	{
		return speedMod;
	}

	public void setSpeedMod(int speed)
	{
		if (speedMod != speed)
		{
			speedMod = speed;
			setCustom(true);
		}
	}

	public int getExtraHPPerLevel()
	{
		return extraHPPerLevel;
	}

	public void setExtraHPPerLevel(int hp)
	{
		if (extraHPPerLevel != hp)
		{
			extraHPPerLevel = hp;
			setCustom(true);
		}
	}

	public int getExtraHPLvl1()
	{
		return extraHPLvl1;
	}

	public void setExtraHPLvl1(int hp)
	{
		if (extraHPLvl1 != hp)
		{
			extraHPLvl1 = hp;
			setCustom(true);
		}
	}

	public List<String> getResistancesGranted()
	{
		return resistancesGranted;
	}

	public void setResistancesGranted(List<String> res)
	{
		if (!resistancesGranted.equals(res))
		{
			resistancesGranted.clear();
			resistancesGranted.addAll(res);
			setCustom(true);
		}
	}

	public List<String> getResistanceOptions()
	{
		return resistanceOptions;
	}

	public void setResistanceOptions(List<String> res)
	{
		if (!resistanceOptions.equals(res))
		{
			resistanceOptions.clear();
			resistanceOptions.addAll(res);
			setCustom(true);
		}
	}

	public String getResistanceSelected()
	{
		return resistanceSelected;
	}

	public void setResistanceSelected(String res)
	{
		this.resistanceSelected = res;
	}

	public String getResistanceByHomeworld(Homeworld h)
	{
		for (String trait : h.getTraits())
		{
			if (resistancesByHomeworld.containsKey(trait))
			{
				return resistancesByHomeworld.get(trait);
			}
		}
		return "";
	}

	public Map<String, String> getAllResistancesByHomeworld()
	{
		return resistancesByHomeworld;
	}

	public void setResistancesByHomeworld(Map<String, String> traitToRes)
	{
		if (!resistancesByHomeworld.equals(traitToRes))
		{
			this.resistancesByHomeworld.clear();
			this.resistancesByHomeworld.putAll(traitToRes);
			setCustom(true);
		}
	}

	public List<Skill> getSkillSelectionOptions()
	{
		return skillOptions;
	}

	public void setSkillSelectionOptions(List<Skill> skills)
	{
		if (!skillOptions.equals(skills))
		{
			skillOptions.clear();
			skillOptions.addAll(skills);
			setCustom(true);
		}
	}

	public int getSkillSelectionCount()
	{
		return skillSelectCount;
	}

	public void setSkillSelectionCount(int count)
	{
		if (skillSelectCount != count)
		{
			skillSelectCount = count;
			setCustom(true);
		}
	}

	public List<Skill> getSkillsSelected()
	{
		return skillsSelected;
	}

	public void setSkillsSelected(List<Skill> skills)
	{
		if (!skillsSelected.equals(skills))
		{
			List<Skill> oldSkills = new ArrayList<>(skillsSelected);
			this.skillsSelected.clear();
			this.skillsSelected.addAll(skills);
			pcs.firePropertyChange(PropertyListener.SKILLS, oldSkills, skills);
		}
	}

	public List<Skill> getSkillExpertOptions()
	{
		return skillExpertOptions;
	}

	public void setSkillExpertOptions(List<Skill> skills)
	{
		if (!skillExpertOptions.equals(skills))
		{
			skillExpertOptions.clear();
			skillExpertOptions.addAll(skills);
			setCustom(true);
		}
	}

	public int getSkillExpertCount()
	{
		return skillExpertCount;
	}

	public void setSkillExpertCount(int count)
	{
		if (skillExpertCount != count)
		{
			skillExpertCount = count;
			setCustom(true);
		}
	}

	public List<Skill> getSkillsExpert()
	{
		return skillExpertSelected;
	}

	public void setSkillsExpert(List<Skill> s)
	{
		this.skillExpertSelected.clear();
		this.skillExpertSelected.addAll(s);
	}

	public List<Language> getLanguagesGranted()
	{
		return languagesGranted;
	}

	public void setLanguagesGranted(List<Language> langs)
	{
		if (!languagesGranted.equals(langs))
		{
			languagesGranted.clear();
			languagesGranted.addAll(langs);
			setCustom(true);
		}
	}

	public List<Language> getLanguageSelectionOptions()
	{
		if (languageOptionNames.contains("Any"))
		{
			return Language.getAllLanguages();
		}
		else
		{
			return languageOptionNames.stream().map(l -> Language.getByName(l)).toList();
		}
	}

	public void setLanguageOptionNames(List<String> langs)
	{
		if (!languageOptionNames.equals(langs))
		{
			languageOptionNames.clear();
			languageOptionNames.addAll(langs);
			setCustom(true);
		}
	}

	public int getLanguageSelectionCount()
	{
		return langSelectCount;
	}

	public void setLanguageSelectionCount(int count)
	{
		if (langSelectCount != count)
		{
			langSelectCount = count;
			setCustom(true);
		}
	}

	public List<Language> getLanguagesSelected()
	{
		return languagesSelected;
	}

	public void setLanguagesSelected(List<Language> langs)
	{
		this.languagesSelected.clear();
		this.languagesSelected.addAll(langs);
	}

	public String getSelectableName()
	{
		return selectableName;
	}

	public void setSelectableName(String name)
	{
		if (!selectableName.equals(name))
		{
			selectableName = name;
			setCustom(true);
		}
	}

	public Selectable getSelected()
	{
		return selected;
	}

	public void setSelection(Selectable s)
	{
		if (selected == null || (s != null && !selected.getName().equals(s.getName())))
		{
			Selectable old = selected;
			this.selected = s;
			// Only one of feat/selectable can be selected at a time for a feature
			this.feat = null;
			pcs.firePropertyChange(PropertyListener.SELECTED, old, s);
		}
	}

	public String getFeatTraitName()
	{
		return featTraitName;
	}

	public void setFeatTraitName(String ft)
	{
		if (!featTraitName.equals(ft))
		{
			featTraitName = ft;
			setCustom(true);
		}
	}

	public boolean featIgnoresPrereqs()
	{
		return featIgnoresPrereqs;
	}

	public void setFeatIgnoresPrereqs(boolean ignore)
	{
		if (featIgnoresPrereqs != ignore)
		{
			this.featIgnoresPrereqs = ignore;
			setCustom(true);
		}
	}

	public Feat getFeat()
	{
		return feat;
	}

	public void setFeat(Feat f)
	{
		if (feat == null || (f != null && !feat.getName().equals(f.getName())))
		{
			Feat old = feat;
			this.feat = f;
			// Only one of feat/selectable can be selected at a time for a feature
			this.selected = null;
			pcs.firePropertyChange(PropertyListener.SELECTED, old, f);
		}
	}

	public List<Spell> getSpecificSpellChoices()
	{
		return specificSpellChoices;
	}

	public void setSpecificSpellChoices(List<Spell> spells)
	{
		if (!specificSpellChoices.equals(spells))
		{
			this.specificSpellChoices.clear();
			this.specificSpellChoices.addAll(spells);
			setCustom(true);
		}
	}

	public Spell getSelectedSpecificSpell()
	{
		return chosenSpell;
	}

	public void chooseSpecificSpell(Spell s)
	{
		this.chosenSpell = s;
	}

	public List<SpellChoice> getSpellChoicesByLevel(int spellLevel)
	{
		if (spellChoices.containsKey(spellLevel))
		{
			return spellChoices.get(spellLevel);
		}
		return Collections.emptyList();
	}

	public Map<Integer, List<SpellChoice>> getSpellChoices()
	{
		return spellChoices;
	}

	public void setSpellChoices(Map<Integer, List<SpellChoice>> spellChoices)
	{
		if (!this.spellChoices.equals(spellChoices))
		{
			this.spellChoices.clear();
			this.spellChoices.putAll(spellChoices);
			setCustom(true);
		}
	}

	public List<Spell> getSpellsSelected()
	{
		List<Spell> all = new ArrayList<>();

		for (Map.Entry<Integer, List<SpellChoice>> scL : spellChoices.entrySet())
		{
			all.addAll(scL.getValue().stream().map(sc -> sc.getSpell()).toList());
		}

		return all;
	}

	public Map<Skill, Ability> getAbilitiesAddToSkills()
	{
		return abilitiesAddToSkills;
	}

	public void setAbilitiesAddToSkills(Map<Skill, Ability> aForS)
	{
		if (!abilitiesAddToSkills.equals(aForS))
		{
			this.abilitiesAddToSkills.clear();
			this.abilitiesAddToSkills.putAll(aForS);
			setCustom(true);
		}
	}

	public boolean givesHalfProfAll()
	{
		return givesHalfProfAll;
	}

	public void setHalfProfAll(boolean b)
	{
		if (givesHalfProfAll != b)
		{
			this.givesHalfProfAll = b;
			setCustom(true);
		}
	}

	public boolean givesProfToInit()
	{
		return givesProfToInit;
	}

	public void setProfToInit(boolean b)
	{
		if (givesProfToInit != b)
		{
			this.givesProfToInit = b;
			setCustom(true);
		}
	}

	public boolean isCustom()
	{
		// For anything we are customizing, to be able to customize one of its children,
		// we have to add that child to it, which marks it as custom, so don't need to
		// check if any children are custom.
		return custom;
	}

	protected void setCustom(boolean custom)
	{
		this.custom = custom;
	}

	public void clearCustom()
	{
		setCustom(false);
	}

	// Combined getters
	public List<String> getAllText()
	{
		return getAllStrings((f) -> f.getText());
	}

	public List<String> getAllSheetNotes()
	{
		return getAllStrings((f) -> f.getSheetNotes());
	}

	public List<Ability> getAllSaveProfs()
	{
		return getAll(Feature::getSaveProfs);
	}

	public List<String> getAllWeaponProfs()
	{
		return getAll(Feature::getWeaponProfs);
	}

	public List<String> getAllToolProfs()
	{
		return getAll(Feature::getToolProfs);
	}

	public List<ArmorProf> getAllArmorProfs()
	{
		return getAll(Feature::getArmorProfs);
	}

	public List<Spell> getAllSpellsGranted()
	{
		return getAll(Feature::getSpellsGranted);
	}

	public List<Spell> getAllSpellsSelected()
	{
		return getAll(Feature::getSpellsSelected);
	}

	public int getTotalSpeedMod()
	{
		int total = 0;
		total += this.getSpeedMod();
		for (Feature child : getChildFeatures())
		{
			total += child.getTotalSpeedMod();
		}
		return total;
	}

	public int getTotalExtraHPPerLevel()
	{
		int total = 0;
		total += this.getExtraHPPerLevel();
		for (Feature child : getChildFeatures())
		{
			total += child.getTotalExtraHPLvl1();
		}
		return total;
	}

	public int getTotalExtraHPLvl1()
	{

		int total = 0;
		total += this.getExtraHPLvl1();
		for (Feature child : getChildFeatures())
		{
			total += child.getTotalExtraHPLvl1();
		}
		return total;
	}

	public List<String> getAllResistancesGranted(Homeworld h)
	{
		List<String> all = new ArrayList<>();
		// Granted resistances
		all.addAll(getResistancesGranted());

		// Homeworld based resistances
		if (h != null)
		{
			String home = getResistanceByHomeworld(h);
			if (!home.isBlank())
			{
				all.add(home);
			}
		}

		// Children
		getChildFeatures().forEach(f -> all.addAll(f.getAllResistancesGranted(h)));

		return all;
	}

	public List<String> getAllResistances(Homeworld h)
	{
		List<String> all = new ArrayList<>();

		// Granted resistances
		all.addAll(getResistancesGranted());

		// Selected resistances
		String selected = getResistanceSelected();
		if (!selected.isBlank())
		{
			all.add(selected);
		}

		// Homeworld based resistances
		String home = getResistanceByHomeworld(h);
		if (!home.isBlank())
		{
			all.add(home);
		}

		// Children
		getChildFeatures().forEach(f -> all.addAll(f.getAllResistances(h)));

		return all;
	}

	public List<Skill> getAllSkills()
	{
		List<Skill> all = new ArrayList<>();

		// Granted skills
		all.addAll(getAll(Feature::getSkillsGranted));

		// Selected skills
		all.addAll(getAll(Feature::getSkillsSelected));

		return all;
	}

	public List<Skill> getAllSkillsExpert()
	{
		return getAll(Feature::getSkillsExpert);
	}

	public List<Language> getAllLanguages()
	{
		List<Language> all = new ArrayList<>();

		// Granted langs
		all.addAll(getAll(Feature::getLanguagesGranted));

		// Selected langs
		all.addAll(getAll(Feature::getLanguagesSelected));

		return all;
	}

	public List<Spell> getAllSpells()
	{
		List<Spell> spells = new ArrayList<>();

		spells.addAll(getAllSpellsGranted());
		if (chosenSpell != null)
		{
			spells.add(chosenSpell);
		}
		spells.addAll(getAllSpellsSelected());

		return spells;
	}

	public List<SpellChoice> getAllSpellChoicesByLevel(int spellLevel)
	{
		List<SpellChoice> all = new ArrayList<>();

		all.addAll(getSpellChoicesByLevel(spellLevel));
		getChildFeatures().forEach(f -> all.addAll(f.getAllSpellChoicesByLevel(spellLevel)));

		return all;
	}

	public List<List<Ability>> getAllAbilitiesToAC()
	{
		List<List<Ability>> abls = new ArrayList<>();

		if (!getAbilitiesToAC().isEmpty())
		{
			abls.add(getAbilitiesToAC());
		}
		getChildFeatures().forEach(ab -> abls.addAll(ab.getAllAbilitiesToAC()));

		return abls;
	}

	public Map<Skill, Ability> getAllAbilitiesAddToSkills()
	{
		Map<Skill, Ability> all = new HashMap<>();

		// TODO post1.0 - This assumes no overlap, ie we'll never have a combination of
		// features that add Dex and Wis to a single skill
		// If that's possible, this needs to be overhauled
		all.putAll(getAbilitiesAddToSkills());
		getChildFeatures().forEach(f -> all.putAll(f.getAllAbilitiesAddToSkills()));

		return all;
	}

	public List<Feat> getAllFeats()
	{
		List<Feat> all = new ArrayList<>();

		if (getFeat() != null)
		{
			all.add(getFeat());
		}
		getChildFeatures().forEach(f -> all.addAll(f.getAllFeats()));

		return all;
	}

	public List<String> getAllFeatureStrings()
	{
		List<String> fs = new ArrayList<>();

		if (!getText().isBlank())
		{
			fs.add(getName() + " - " + getText());
		}
		getChildFeatures().forEach(f -> fs.addAll(f.getAllFeatureStrings()));

		return fs;
	}

	public boolean getAnyGivesHalfProfAll()
	{
		if (givesHalfProfAll)
		{
			return true;
		}
		else
		{
			if (getChildFeatures().stream().anyMatch(f -> f.getAnyGivesHalfProfAll()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean getAnyGivesProfToInit()
	{
		if (givesProfToInit)
		{
			return true;
		}
		else
		{
			if (getChildFeatures().stream().anyMatch(f -> f.getAnyGivesProfToInit()))
			{
				return true;
			}
		}
		return false;
	}

	// Utility

	/*
	 * Separated out to handle single string return methods
	 */
	protected List<String> getAllStrings(Function<Feature, String> method)
	{
		List<String> all = new ArrayList<>();

		String thisText = method.apply(this);
		if (thisText != null && !thisText.isBlank())
		{
			all.add(thisText);
		}
		getChildFeatures().forEach(f -> all.addAll(f.getAllStrings(method)));

		return all;
	}

	protected <T> List<T> getAll(Function<Feature, List<T>> method)
	{
		List<T> all = new ArrayList<>();

		all.addAll(method.apply(this));
		getChildFeatures().forEach(f -> all.addAll(f.getAll(method)));

		return all;
	}

	/**
	 * This should be overridden by anything that has child features other than
	 * selectables or feats
	 */
	protected List<? extends Feature> getChildFeatures()
	{
		List<Feature> children = new ArrayList<>();
		if (selected != null)
		{
			children.add(selected);
		}
		if (feat != null)
		{
			children.add(feat);
		}
		return children;
	}

	/**
	 * This is the same as the above except for class and species, which have
	 * leveled features that aren't returned by the above but are needed for some
	 * data checks
	 */
	protected List<? extends Feature> getAllChildFeatures()
	{
		return getChildFeatures();
	}

	@Override
	public String toString()
	{
		return name;
	}

	public JSONObject saveState()
	{
		// We only want to save stuff the user can change, plus name for reference
		JSONObject json = new JSONObject();

		json.put("name", name);

		if (!resistanceSelected.isBlank())
		{
			json.put("resistanceSelected", resistanceSelected);
		}
		if (!skillsSelected.isEmpty())
		{
			json.put("skillsSelected", new JSONArray(skillsSelected.stream().map(s -> s.toString()).toList()));
		}
		if (!skillExpertSelected.isEmpty())
		{
			json.put("skillExpertSelected",
					new JSONArray(skillExpertSelected.stream().map(s -> s.toString()).toList()));
		}
		if (!languagesSelected.isEmpty())
		{
			json.put("languagesSelected", new JSONArray(languagesSelected.stream().map(s -> s.toString()).toList()));
		}
		if (selected != null)
		{
			json.put("selected", selected.saveState());
		}
		if (feat != null)
		{
			json.put("feat", feat.saveState());
		}
		if (chosenSpell != null)
		{
			JSONObject spellData = new JSONObject();
			spellData.put("spell", chosenSpell.getName());
			if (!chosenSpell.getBaseName().isBlank())
			{
				spellData.put("baseName", chosenSpell.getBaseName());
			}
			if (!chosenSpell.getNotes().isBlank())
			{
				spellData.put("notes", chosenSpell.getNotes());
			}
			json.put("chosenSpell", spellData);
		}
		for (Map.Entry<Integer, List<SpellChoice>> scl : spellChoices.entrySet())
		{
			json.put("spellChoices" + scl.getKey(),
					new JSONArray(scl.getValue().stream().map(sc -> sc.saveState()).toList()));
		}

		return json;
	}

	public boolean loadState(JSONObject data)
	{
		boolean success = true;
		try
		{
			this.resistanceSelected = data.optString("resistanceSelected", "");
			JSONArray skillArr = data.optJSONArray("skillsSelected");
			if (skillArr != null)
			{
				skillsSelected.clear();
				for (int i = 0; i < skillArr.length(); i++)
				{
					skillsSelected.add(Skill.skillByName(skillArr.getString(i)));
				}
			}
			JSONArray skillExp = data.optJSONArray("skillExpertSelected");
			if (skillExp != null)
			{
				skillExpertSelected.clear();
				for (int i = 0; i < skillExp.length(); i++)
				{
					skillExpertSelected.add(Skill.skillByName(skillExp.getString(i)));
				}
			}
			JSONArray langs = data.optJSONArray("languagesSelected");
			if (langs != null)
			{
				languagesSelected.clear();
				for (int i = 0; i < langs.length(); i++)
				{
					languagesSelected.add(Language.getByName(langs.getString(i)));
				}
			}
			JSONObject selectedData = data.optJSONObject("selected");
			if (selectedData != null)
			{
				String selectedName = selectedData.getString("name");
				String selectedType = selectedData.getString("type");
				selected = Selectable.getSelectableByTypeAndName(selectedType, selectedName);
				selected.loadState(selectedData);
			}
			JSONObject featData = data.optJSONObject("feat");
			if (featData != null)
			{
				String featName = featData.getString("name");
				feat = Feat.getByName(featName);
				feat.loadState(featData);
			}
			JSONObject csData = data.optJSONObject("chosenSpell");
			if (csData != null)
			{
				chosenSpell = Spell.getFromJSONObject(csData);
			}
			for (int i = 0; i <= 9; i++)
			{
				JSONArray sc = data.optJSONArray("spellChoices" + i);
				if (sc != null)
				{
					List<SpellChoice> scs = new ArrayList<>();
					for (int v = 0; v < sc.length(); v++)
					{
						SpellChoice s = new SpellChoice();
						s.loadState(sc.getJSONObject(v));
						scs.add(s);
					}
					spellChoices.put(i, scs);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			success = false;
		}
		return success;

	}
}
