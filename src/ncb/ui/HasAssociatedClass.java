package ncb.ui;

import ncb.data.loadables.CharacterClass;
import ncb.main.CharacterSheet;

public interface HasAssociatedClass
{
	public abstract int getClassIndex();

	public abstract CharacterSheet getSheet();

	default CharacterClass getAssociatedClass()
	{
		if (getClassIndex() == CharacterSheet.baseClassIndex)
		{
			return getSheet().getCharClass();
		}
		else
		{
			return getSheet().getMulticlasses().get(getClassIndex());
		}
	}
}
