package ncb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;
import ncb.data.loadables.CharacterClass;
import ncb.data.loadables.Homeworld;
import ncb.main.CharacterSheet;

public class Prereq implements HasConfig
{
	public static final String clsLvl = "Class Level", chrLvl = "Character Level", pSel = "Prior Selection",
			hTrait = "Homeworld Trait", ablScore = "Ability Score Minimum", armor = "Armor Training",
			spellcast = "Spellcaster";
	public static final List<String> prereqTypes = List.of(clsLvl, chrLvl, pSel, hTrait, ablScore, armor, spellcast);

	private static int nId = 1;
	private final int id;
	private Customizable parent;

	public Prereq()
	{
		this.id = nId++;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public Customizable getParent()
	{
		return parent;
	}

	@Override
	public void setParent(Customizable p)
	{
		this.parent = p;
	}

	public boolean met(CharacterSheet sheet)
	{
		switch (type)
		{
			case hTrait:
				Homeworld h = sheet.getBackground().getHomeworld();
				if (h != null)
				{
					for (String req : reqHwTrait)
					{
						if (h.hasTrait(req))
						{
							return true;
						}
					}
				}
				return false;
			case clsLvl:
				List<CharacterClass> classes = List.of(sheet.getCharClass()); // TODO Multiclass

				return classes.stream().anyMatch(c -> c.getName().equals(reqClassName) && c.getLevel() >= reqVal);
			case pSel:
				for (Feature cf : sheet.getCharClass().getClassFeatures())
				{
					if (cf.getName().equals(reqFeatureName)
							|| (cf.getSelected() != null && cf.getSelected().getName().equals(reqFeatureName))
							|| cf.getFeat() != null && cf.getFeat().getName().equals(reqFeatureName))
					{
						return true;
					}
				}
				return false;
			case chrLvl:
				return sheet.getLevel() >= reqVal;
			case ablScore:
				for (Ability a : reqAbls)
				{
					if (sheet.getTotalAbilityScore(a) >= reqVal)
					{
						return true;
					}
				}
				return false;
			case armor:
				return sheet.getAllArmorProfs().contains(reqArmor);
			case spellcast:
				return sheet.isSpellcaster();
			default:
				System.err.println("Unknown Prereq Type " + type);
				return false;
		}
	}

	// Configuration
	private String type = clsLvl;
	private final List<String> reqHwTrait = new ArrayList<>();
	private String reqClassName = "", reqFeatureName = "";
	private final List<Ability> reqAbls = new ArrayList<>();
	private ArmorTraining reqArmor = null;
	private int reqVal = 0;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		updateConfig(this.type, type, (v) ->
		{
			this.type = v;
		});
	}

	public List<String> getReqHwTraits()
	{
		return reqHwTrait;
	}

	public void setReqHwTraits(List<String> traits)
	{
		updateConfig(reqHwTrait, traits, (v) ->
		{
			reqHwTrait.clear();
			reqHwTrait.addAll(traits);
		});
	}

	public String getReqClassName()
	{
		return reqClassName;
	}

	public void setReqClassName(String name)
	{
		updateConfig(reqClassName, name, (v) ->
		{
			reqClassName = v;
		});
	}

	public String getReqFeatureName()
	{
		return reqFeatureName;
	}

	public void setReqFeatureName(String name)
	{
		updateConfig(reqFeatureName, name, (v) ->
		{
			reqFeatureName = v;
		});
	}

	public List<Ability> getReqAbilities()
	{
		return reqAbls;
	}

	public void setReqAbilities(List<Ability> abls)
	{
		updateConfig(reqAbls, abls, (v) ->
		{
			reqAbls.clear();
			reqAbls.addAll(v);
		});
	}

	public ArmorTraining getReqArmor()
	{
		return reqArmor;
	}

	public void setReqArmor(ArmorTraining at)
	{
		updateConfig(reqArmor, at, (v) ->
		{
			reqArmor = v;
		});
	}

	public int getReqVal()
	{
		return reqVal;
	}

	public void setReqVal(int val)
	{
		updateConfig(reqVal, val, (v) ->
		{
			reqVal = v;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "type", type);

		switch (type)
		{
			case hTrait:
				json = putList(json, "hwTraits", reqHwTrait);
			break;
			case pSel:
				json = putStr(json, "feature", reqFeatureName);
			break;
			case clsLvl:
				json = putStr(json, "class", reqClassName);
			break;
			case ablScore:
				json = putList(json, "abilities", reqAbls.stream().map(a -> a.toString()).toList());
			break;
			case armor:
				json = putStr(json, "armor", reqArmor.toString());
			break;
		}
		json = putInt(json, "val", reqVal);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		type = data.optString("type", "");

		reqHwTrait.clear();
		reqHwTrait.addAll(getList(data, "hwTraits"));
		reqClassName = data.optString("class", "");
		reqFeatureName = data.optString("feature", "");
		reqAbls.clear();
		getList(data, "abilities").forEach(a -> reqAbls.add(Ability.valueOf(a)));
		reqVal = data.optInt("val", 0);
		String atName = data.optString("armor", "");
		if (!atName.isBlank())
		{
			reqArmor = ArmorTraining.valueOf(atName);
		}
	}

	@Override
	public String toString()
	{
		switch (type)
		{
			case hTrait:
				return "Requires Homeworld with trait in (" + String.join(", ", reqHwTrait) + ")";
			case clsLvl:
				return "Requires " + reqClassName + " level " + reqVal;
			case pSel:
				return "Requires " + reqFeatureName;
			case chrLvl:
				return "Requires Character Level " + reqVal;
			case ablScore:
				return "Requires one of " + reqAbls + " " + reqVal;
			case armor:
				return "Required Armor Training " + reqArmor;
			case spellcast:
				return "Requires spellcasting ability";
			default:
				return "Unknown Prereq Type " + type;
		}
	}
}
