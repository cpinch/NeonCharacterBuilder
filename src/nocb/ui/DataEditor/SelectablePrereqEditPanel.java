package nocb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import nocb.data.SelectablePrereq;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class SelectablePrereqEditPanel extends NoHorizontalScrollPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private final SelectablePrereq sp;

	private final JComboBox<String> typeSel = new JComboBox<>();
	private final JTextField req = new JTextField(20);

	public SelectablePrereqEditPanel(SelectablePrereq sp)
	{
		this.sp = sp;

		setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();
		c.weightx = 0;

		SelectablePrereq.prereqTypes.forEach(pt -> typeSel.addItem(pt));
		typeSel.addActionListener(this);
		if (!sp.getType().isBlank())
		{
			typeSel.setSelectedItem(sp.getType());
		}
		else
		{
			typeSel.setSelectedIndex(0);
		}
		add(typeSel, c);
		c.gridx++;
		c.weightx = 1;
		req.setText(String.join(", ", sp.getRequired()));
		req.addFocusListener(UILib.createFocusListener(() -> updateRequired()));
		add(req, c);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(typeSel))
		{
			sp.setType((String) typeSel.getSelectedItem());
		}
	}

	private void updateRequired()
	{
		sp.setRequred(Arrays.asList(req.getText().split(",")).stream().map(r -> r.trim()).toList());
	}
}
