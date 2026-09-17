package nocb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import nocb.data.Selectable;
import nocb.data.SelectableFeature;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class SelectableFeaturesEditPanel extends NoHorizontalScrollPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Selectable sel;

	private JButton add;

	public SelectableFeaturesEditPanel()
	{
		setLayout(new GridBagLayout());

		add = new JButton("Add Selectable Feature");
		add.addActionListener(this);
	}

	public void updateFeatures(Selectable sel)
	{
		this.sel = sel;

		GridBagConstraints c = UILib.getStandardGBC();
		removeAll();
		for (SelectableFeature sf : sel.getFeatures())
		{
			SelectableFeatureEditPanel sfep = new SelectableFeatureEditPanel(sf);
			sfep.updateSelection();
			add(sfep, c);
			c.gridy++;
		}
		add(add, c);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(add))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New Selectable Feature",
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank())
			{
				sel.addNewFeature(name);
				updateFeatures(sel);
			}
		}
	}
}
