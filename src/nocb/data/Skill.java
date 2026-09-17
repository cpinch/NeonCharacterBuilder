package nocb.data;

import static nocb.data.Ability.Cha;
import static nocb.data.Ability.Dex;
import static nocb.data.Ability.Int;
import static nocb.data.Ability.Str;
import static nocb.data.Ability.Wis;

import java.util.Arrays;

public enum Skill
{
	Acrobatics(Dex), Animal_Handling(Cha), Arcana(Int), Athletics(Str), Computers(Int), Deception(Cha), History(
			Int), Insight(Wis), Intimidation(Cha), Investigation(Int), Medicine(Wis), Nature(Int), Perception(
					Wis), Performance(Cha), Persuasion(Cha), Religion(
							Int), Sleight_of_Hand(Dex), Stealth(Dex), Survival(Wis), Technology(Int), Any(Int);

	private final Ability associatedAbility;

	private Skill(Ability aa)
	{
		this.associatedAbility = aa;
	}

	public Ability getAbility()
	{
		return associatedAbility;
	}

	public static Skill skillByName(String s)
	{
		return valueOf(s.replace(' ', '_'));
	}

	public static Skill[] realValues()
	{
		// Filters out "Any"
		return Arrays.asList(values()).stream().filter(s -> !s.equals(Skill.Any)).toList().toArray(new Skill[0]);
	}

	@Override
	public String toString()
	{
		return name().replace('_', ' ');
	}
}
