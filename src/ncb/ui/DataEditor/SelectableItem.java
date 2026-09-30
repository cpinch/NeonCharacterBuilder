package ncb.ui.DataEditor;

import ncb.data.loadables.Selectable;

// Special implementation to show type and name from selectables
public class SelectableItem extends SelectorItem
{
	public SelectableItem(Selectable element)
	{
		super(element);
	}

	@Override
	public String toString()
	{
		Selectable element = (Selectable) getElement();
		return element.getType() + "-" + element.getName() + (element.isCustom() ? "*" : "");
	}
}
