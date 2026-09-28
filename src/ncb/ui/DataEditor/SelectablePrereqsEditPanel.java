package ncb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import ncb.data.Prereq;
import ncb.data.loadables.Selectable;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public class SelectablePrereqsEditPanel extends NoHorizontalScrollPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Selectable sel;

	private JButton add, remove;

	public SelectablePrereqsEditPanel()
	{
		setLayout(new GridBagLayout());

		add = new JButton("Add Selectable Prereq");
		add.addActionListener(this);

		remove = new JButton("Remove Last Prereq");
		remove.addActionListener(this);
	}

	public void updatePrereqs(Selectable sel)
	{
		this.sel = sel;

		GridBagConstraints c = UILib.getStandardGBC();
		c.gridwidth = 2;
		removeAll();
		for (Prereq sp : sel.getPrereqs())
		{
			add(new SelectablePrereqEditPanel(sp), c);
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
			sel.addNewPrereq();
			updatePrereqs(sel);
		}
		else if (e.getSource().equals(remove))
		{
			sel.removeLastPrereq();
			updatePrereqs(sel);
		}
	}
}
