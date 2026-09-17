package nocb.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nocb.data.Skill;
import nocb.main.CharacterSheet;

public class ClassSkillSelector extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -1513890001811947077L;

	private static final int maxSkills = 4;

	private final CharacterSheet sheet;

	private final List<JComboBox<Skill>> selectors = new ArrayList<>();

	private final Runnable skillsUpdatedCallback;

	public ClassSkillSelector(CharacterSheet sheet, Runnable skillsUpdatedCallback)
	{
		this.sheet = sheet;
		this.skillsUpdatedCallback = skillsUpdatedCallback;

		setLayout(new GridBagLayout());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		JLabel l = UILib.addLabel(this, "Skill Proficiencies:", c);
		l.setFont(UILib.boldFont);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		c.gridx++;
		for (int i = 0; i < maxSkills; i++)
		{
			JComboBox<Skill> selector = new JComboBox<Skill>();
			selector.addActionListener(this);
			selector.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
			selector.setBackground(VaporwaveColors.DEEP_VIOLET);
			selector.setForeground(VaporwaveColors.LASER_YELLOW);
			add(selector, c);
			c.gridx++;
			selectors.add(selector);
		}
	}

	public void updateDetails()
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
		skillsUpdatedCallback.run();
	}
}
