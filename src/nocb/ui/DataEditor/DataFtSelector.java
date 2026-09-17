package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.Feat;

public class DataFtSelector extends SelectorPanel
{
	private static final long serialVersionUID = 7716882898063441557L;

	FeatEditPanel ftp = new FeatEditPanel();

	public DataFtSelector()
	{
		super("Feat");

		updateFeats();

		display.add(ftp, BorderLayout.CENTER);
	}

	private void updateFeats()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> featNames = new ArrayList<>(Feat.getAllLoadedFeats().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		featNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(featNames);
	}

	@Override
	protected void updateSelection()
	{
		ftp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		Feat.addNewFeat(name);
		updateFeats();
	}
}
