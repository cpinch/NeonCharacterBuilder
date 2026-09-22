package nocb.ui.DataEditor;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nocb.data.ClassFeature;
import nocb.data.Subclass;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class SubclassEditPanel extends EditPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Subclass sc;

	private final JTextField assocCls = new JTextField(20);
	private final JTextArea desc = new JTextArea(2, 20);

	private final JButton addFeature = new JButton("Add Subclass Feature");
	private final JPanel featuresPanel = new NoHorizontalScrollPanel();

	public SubclassEditPanel()
	{
		super();

		assocCls.addFocusListener(UILib.createFocusListener(() -> updateAssociatedClass()));
		UILib.addLabeledComponent(this, "Associated Class: ", assocCls, c).setForeground(Color.black);
		c.gridy++;

		c.weighty = 1;
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.addFocusListener(UILib.createFocusListener(() -> updateDesc()));
		UILib.addLabeledComponent(this, "Desc: ", desc, c).setForeground(Color.black);
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
	protected void updateSelection()
	{
		sc = Subclass.getById(id);

		nameField.setText(sc.getName());
		assocCls.setText(sc.getAssociatedClassName());
		desc.setText(sc.getDesc());

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
	}

	@Override
	protected void clearSelectedCustom()
	{
		if (sc != null)
		{
			sc.clearCustom();
		}
	}

	private void updateFeaturePanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.ipady = 20;
		featuresPanel.removeAll();
		for (ClassFeature cf : sc.getAllSubclassFeatures())
		{
			ClassFeatureEditPanel cfep = new ClassFeatureEditPanel(cf);
			cfep.updateSelection();
			featuresPanel.add(cfep, c);
			c.gridy++;
		}
		featuresPanel.revalidate();
	}

	@Override
	protected void updateName()
	{
		sc.setName(nameField.getText());
	}

	private void updateDesc()
	{
		sc.setDesc(desc.getText());
	}

	private void updateAssociatedClass()
	{
		sc.setAssociatedClass(assocCls.getText());
	}
}
