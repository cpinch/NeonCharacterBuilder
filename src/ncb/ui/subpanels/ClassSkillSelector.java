package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import ncb.data.enums.Skill;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class ClassSkillSelector extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -1513890001811947077L;

	private static final int maxSkills = 4;

	private final CharacterSheet sheet;

	private final List<JComboBox<Skill>> selectors = new ArrayList<>();

	public ClassSkillSelector(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);

		setLayout(new GridBagLayout());
		setOpaque(false);

		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(0, 0, 0, 0);

		UILib.addLabel(this, "Skill Proficiencies: ", c, VaporwaveColors.HOT_PINK).setFont(UILib.boldFont);
		c.gridx++;
		for (int i = 0; i < maxSkills; i++)
		{
			JComboBox<Skill> selector = UILib.getComboBox(new Skill[0], this, VaporwaveColors.DEEP_VIOLET,
					VaporwaveColors.LASER_YELLOW);
			add(selector, c);
			c.gridx++;
			selectors.add(selector);
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		if (sheet.getCharClass() != null)
		{
			List<Skill> options = sheet.getCharClass().getSkillSelectionOptions();
			List<Skill> charSkills = new ArrayList<>(sheet.getCharClass().getSkillsSelected());
			int count = sheet.getCharClass().getSkillSelectionCount();
			for (int i = maxSkills - 1; i >= 0; i--)
			{
				JComboBox<Skill> selector = selectors.get(i);
				if (i >= count)
				{
					selector.setVisible(false);
				}
				else
				{
					selector.setVisible(true);
					selector.removeAllItems();
					if (options.contains(Skill.Any))
					{
						for (Skill s : Skill.realValues())
						{
							selector.addItem(s);
						}
					}
					else
					{
						options.forEach(s -> selector.addItem(s));
					}
					if (charSkills.size() > i)
					{
						selector.setSelectedItem(charSkills.get(i));
					}
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Skill> selectedSkills = new ArrayList<>();
		for (JComboBox<Skill> sel : selectors)
		{
			if (sel.isVisible())
			{
				Skill s = (Skill) sel.getSelectedItem();
				if (s == null)
				{
					// We are in the middle of an update, we've done the removeAll but haven't added
					// skills yet, this update is worthless
					return;
				}
				selectedSkills.add(s);
			}
		}
		sheet.getCharClass().setSkillsSelected(selectedSkills);
	}
}
