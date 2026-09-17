package nocb.ui.DataEditor;

public class SelectorItem implements Comparable<SelectorItem>
{
	private int id;
	private String text;

	public SelectorItem(int id, String text)
	{
		this.id = id;
		this.text = text;
	}

	public int getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return text;
	}

	@Override
	public int compareTo(SelectorItem o)
	{
		return this.text.compareTo(o.text);
	}
}
