package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Feat;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.FeatEditPanel;

public class DataFtSelector extends SelectorPanel
{
	private static final long serialVersionUID = 7716882898063441557L;

	FeatEditPanel ftp = new FeatEditPanel();

	public DataFtSelector()
	{
		super("Feat");

		display.add(ftp, BorderLayout.CENTER);
	}

	@Override
	protected void createNew(String name)
	{
		Feat.addNewFeat(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return ftp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Feat.getAllLoadedFeats();
	}
}
