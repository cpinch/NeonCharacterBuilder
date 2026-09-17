package nocb.ui.DataEditor;

import java.awt.BorderLayout;

import javax.swing.JLabel;

public class DataBgSelector extends SelectorPanel
{
	private static final long serialVersionUID = 4961059827421084614L;

	public DataBgSelector()
	{
		super("Background");

		display.add(new JLabel("Background saving/loading is TBD"), BorderLayout.CENTER);
	}

	@Override
	protected void updateSelection()
	{
	}

	@Override
	protected void createNew(String name)
	{
	}
}
