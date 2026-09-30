package ncb.ui.DataEditor.Editors;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ncb.data.Feature;
import ncb.data.loadables.Selectable;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public class SelectableFeaturesEditPanel extends NoHorizontalScrollPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Selectable sel;

	private JButton add, remove;

	public SelectableFeaturesEditPanel()
	{
		setLayout(new GridBagLayout());

		add = new JButton("Add Selectable Feature");
		add.addActionListener(this);

		remove = new JButton("Remove Last Feature");
		remove.addActionListener(this);
	}

	public void updateFeatures(Selectable sel)
	{
		this.sel = sel;

		GridBagConstraints c = UILib.getStandardGBC();
		c.gridwidth = 2;
		removeAll();
		for (Feature sf : sel.getOriginalFeatures())
		{
			FeatureEditPanel sfep = new FeatureEditPanel(sf, false, null);
			add(sfep, c);
			c.gridy++;
		}
		c.gridwidth = 1;
		add(add, c);
		c.gridx++;
		add(remove, c);
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(add))
		{
			sel.addNewFeature(sel.getName());
			updateFeatures(sel);
		}
		else if (e.getSource().equals(remove))
		{
			sel.removeLastFeature();
			updateFeatures(sel);
		}
	}
}
