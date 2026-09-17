package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.CharacterClass;

public class DataClsSelector extends SelectorPanel
{
	private static final long serialVersionUID = 5331577565314781141L;

	ClassEditPanel cpp = new ClassEditPanel();

	public DataClsSelector()
	{
		super("Class");

		updateClasses();

		display.add(cpp, BorderLayout.CENTER);
	}

	private void updateClasses()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> homeworldNames = new ArrayList<>(CharacterClass.getAllClasses().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		homeworldNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(homeworldNames);
	}

	@Override
	protected void updateSelection()
	{
		cpp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		CharacterClass.addNewClass(name);
		updateClasses();
	}
}
