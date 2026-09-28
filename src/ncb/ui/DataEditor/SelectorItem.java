package ncb.ui.DataEditor;

import ncb.data.interfaces.Customizable;

// This class exists basically purely to show a "*" for customized elements
public class SelectorItem
{
	private final Customizable element;

	public SelectorItem(Customizable element)
	{
		this.element = element;
	}

	public Customizable getElement()
	{
		return element;
	}

	@Override
	public String toString()
	{
		return element.toString() + (element.isCustom() ? "*" : "");
	}
}
