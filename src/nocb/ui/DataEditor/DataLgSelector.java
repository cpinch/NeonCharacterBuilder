package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.Language;

public class DataLgSelector extends SelectorPanel
{
	private static final long serialVersionUID = 6901896521242199526L;

	LanguageEditPanel lwp = new LanguageEditPanel();

	public DataLgSelector()
	{
		super("Language");

		updateLanguages();

		display.add(lwp, BorderLayout.CENTER);
	}

	private void updateLanguages()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> languageNames = new ArrayList<>(Language.getAllLanguages().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		languageNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(languageNames);
	}

	@Override
	protected void updateSelection()
	{
		lwp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		Language.addNewLanguage(name);
		updateLanguages();
	}
}
