package nocb.ui.DataEditor;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DataTypeSelector extends JPanel implements ListSelectionListener
{
	private static final long serialVersionUID = 7705589923724594447L;

	public static final String bgStr = "Background", clsStr = "Class", ftStr = "Feat", hwStr = "Homeworld",
			lgStr = "Language", selStr = "Selectable", speStr = "Species", slStr = "SpellList", spStr = "Spell";

	private final JList<String> type = new JList<>(new String[]
	{ bgStr, clsStr, ftStr, hwStr, lgStr, selStr, speStr, slStr, spStr });
	private final DataObjectSelector dos = new DataObjectSelector();

	public DataTypeSelector()
	{
		setLayout(new BorderLayout());
		type.addListSelectionListener(this);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, type, dos);
		split.setContinuousLayout(true);
		add(split, BorderLayout.CENTER);
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			dos.setType(type.getSelectedValue());
		}
	}
}
