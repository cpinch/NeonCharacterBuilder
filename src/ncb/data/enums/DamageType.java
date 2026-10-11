package ncb.data.enums;

public enum DamageType
{
	Acid, Bludgeoning, Cold, Fire, Force, Lightning, Necrotic, Piercing, Poison, Psychic, Radiant, Slashing, Thunder, Falling_damage;

	public static DamageType typeFromStr(String s)
	{
		return valueOf(s.replace(' ', '_'));
	}

	@Override
	public String toString()
	{
		return name().replace('_', ' ');
	}
}
