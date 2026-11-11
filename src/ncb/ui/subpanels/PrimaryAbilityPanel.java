package ncb.ui.subpanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.data.enums.Ability;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.HasAssociatedClass;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;

public class PrimaryAbilityPanel extends JPanel implements ActionListener, ListensForChanges, HasAssociatedClass
{
	private static final long serialVersionUID = -1513890001811947077L;

	private final CharacterSheet sheet;
	private final int classIndex;

	private final JComboBox<Ability> selector;
	private final JLabel label;

	public PrimaryAbilityPanel(CharacterSheet sheet, int multiclassIndex)
	{
		this.sheet = sheet;
		this.classIndex = multiclassIndex;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);

		label = UILib.addLabel(this, "Primary Ability:");
		label.putClientProperty(FlatClientProperties.STYLE, "font: bold");

		selector = UILib.getComboBox(new Ability[0], this);
		add(selector);
	}

	@Override
	public void updateProperty(String prop)
	{
		if (getAssociatedClass() != null)
		{
			if (!getAssociatedClass().getPrimaryAbilityOptions().isEmpty())
			{
				selector.setVisible(true);
				label.setText("Primary Ability: ");
				selector.removeAllItems();
				getAssociatedClass().getPrimaryAbilityOptions().forEach(a -> selector.addItem(a));
			}
			else
			{
				selector.setVisible(false);
				label.setText("Primary Ability: " + String.join(" and ",
						getAssociatedClass().getPrimaryAbilities().stream().map(a -> a.name()).toList()));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(selector))
		{
			Ability selected = (Ability) selector.getSelectedItem();
			if (selected != null)
			{
				getAssociatedClass().setSelectedPrimary(selected);
			}
		}
	}

	@Override
	public int getClassIndex()
	{
		return classIndex;
	}

	@Override
	public CharacterSheet getSheet()
	{
		return sheet;
	}
}
