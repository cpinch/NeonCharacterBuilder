package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.Species;

public class DataSpeSelector extends SelectorPanel
{
	private static final long serialVersionUID = -2703988813733746384L;

	SpeciesEditPanel spp = new SpeciesEditPanel();

	public DataSpeSelector()
	{
		super("Species");

		updateSpecies();

		display.add(spp, BorderLayout.CENTER);
	}

	private void updateSpecies()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> homeworldNames = new ArrayList<>(Species.getAllSpecies().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		homeworldNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(homeworldNames);
	}

	@Override
	protected void updateSelection()
	{
		spp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		Species.addNewSpecies(name);
		updateSpecies();
	}
}
