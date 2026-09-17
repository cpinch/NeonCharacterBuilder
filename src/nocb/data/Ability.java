package nocb.data;

public enum Ability
{
	Str, Dex, Con, Int, Wis, Cha, Primary;

	public String getFullName()
	{
		switch (this)
		{
			case Str:
				return "Strength";
			case Dex:
				return "Dexterity";
			case Con:
				return "Constitution";
			case Int:
				return "Intelligence";
			case Wis:
				return "Wisdom";
			case Cha:
				return "Charisma";
			default:
				return "";
		}
	}

	public static Ability[] realValues()
	{
		return new Ability[]
		{ Str, Dex, Con, Int, Wis, Cha };
	}
}
