package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Language;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.LanguageEditPanel;

public class DataLgSelector extends SelectorPanel
{
	private static final long serialVersionUID = 6901896521242199526L;

	LanguageEditPanel lwp = new LanguageEditPanel();

	public DataLgSelector()
	{
		super("Language");

		display.add(lwp, BorderLayout.CENTER);
	}

	@Override
	protected Customizable createNew(String name)
	{
		return Language.addNewLanguage(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return lwp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Language.getAllLanguages();
	}
}
