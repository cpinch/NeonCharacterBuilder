package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;

import ncb.data.interfaces.Customizable;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;

public class DataBgSelector extends SelectorPanel
{
	private static final long serialVersionUID = 4961059827421084614L;

	public DataBgSelector()
	{
		super("Background");

		display.add(new JLabel("Background saving/loading is TBD"), BorderLayout.CENTER);
	}

	@Override
	protected Customizable createNew(String name)
	{
		return null;
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return null;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Collections.emptyList();
	}
}
