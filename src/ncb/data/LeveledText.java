package ncb.data;

public class LeveledText
{
	private final int lvl;
	private final String name, text;

	public LeveledText(int lvl, String name, String text)
	{
		this.lvl = lvl;
		this.name = name;
		this.text = text;
	}

	public int getLvl()
	{
		return lvl;
	}

	public String getName()
	{
		return name;
	}

	public String getText()
	{
		return text;
	}
}
