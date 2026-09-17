package nocb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import nocb.data.Selectable;
import nocb.data.SelectablePrereq;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class SelectablePrereqsEditPanel extends NoHorizontalScrollPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Selectable sel;

	private JButton add;

	public SelectablePrereqsEditPanel()
	{
		setLayout(new GridBagLayout());

		add = new JButton("Add Selectable Prereq");
		add.addActionListener(this);
	}

	public void updatePrereqs(Selectable sel)
	{
		GridBagConstraints c = UILib.getStandardGBC();
		removeAll();
		for (SelectablePrereq sp : sel.getPrereqs())
		{
			add(new SelectablePrereqEditPanel(sp), c);
			c.gridy++;
		}
		add(add, c);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(add))
		{
			sel.addNewPrereq();
			updatePrereqs(sel);
		}
	}
}
