package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ncb.data.Feature;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Subclass;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class SubclassEditPanel extends EditPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Subclass sc;

	@Override
	protected String getItemName()
	{
		return sc.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		sc.setName(s);
	}

	private final JButton addFeature = new JButton("Add Subclass Feature");
	private final JPanel featuresPanel = new NoHorizontalScrollPanel();

	public SubclassEditPanel()
	{
		super();

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Associated Class: ", c, Color.white, Color.black,
				() -> sc.getAssociatedClassName(), (s) -> sc.setAssociatedClass(s)));
		c.gridy++;

		c.weighty = 1;
		linkedProperties.add(UILib.addLabeledLinkedTextArea(this, 2, "Desc: ", c, Color.white, Color.black,
				() -> sc.getDesc(), (s) -> sc.setDesc(s)));
		c.gridy++;
		c.weighty = 0;

		addFeature.addActionListener(this);
		add(addFeature, c);
		c.gridy++;
		featuresPanel.setLayout(new GridBagLayout());
		add(featuresPanel, c);
		c.gridy++;

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		sc = (Subclass) sel;

		linkedProperties.forEach(l -> l.updateValue());

		updateFeaturePanels();

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(addFeature))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New Subclass Feature",
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank())
			{
				sc.addSubclassFeature(name);
				updateFeaturePanels();
			}
		}
		else if (e.getActionCommand().startsWith("Delete"))
		{
			int fId = Integer.parseInt(e.getActionCommand().split(" ")[1].trim());
			sc.removeSubclassFeature(fId);
			updateFeaturePanels();
		}
	}

	private void updateFeaturePanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);
		featuresPanel.removeAll();
		for (Feature cf : sc.getAllSubclassFeatures())
		{
			FeatureEditPanel cfep = new FeatureEditPanel(cf, true, this);
			featuresPanel.add(cfep, c);
			c.gridy++;
		}
		featuresPanel.revalidate();
	}
}
