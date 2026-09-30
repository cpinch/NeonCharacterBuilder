package ncb.data.interfaces;

public interface Customizable
{
	public abstract Customizable getParent();

	public abstract void setParent(Customizable p);

	public abstract int getId();

	default boolean isCustom()
	{
		return false;
	}

	default void setCustom(boolean b)
	{
		if (getParent() != null)
		{
			getParent().setCustom(b);
		}
	}
}
