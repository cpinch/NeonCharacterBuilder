package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import ncb.data.enums.Ability;
import ncb.data.loadables.CharacterClass;
import ncb.ui.CollapsablePanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.PropertyLinkedComboBox;

public class ClassSpellcastingEditPanel extends CollapsablePanel implements ActionListener
{
	private static final long serialVersionUID = 5549755026450721802L;

	private final PropertyLinkedComboBox spellcastingDropdown;
	private final JRadioButton knownSpellsBtn, preparedSpellsBtn, singleSlotsBtn, multiSlotsBtn;
	private final CantripsEditPanel cantripsTable = new CantripsEditPanel();
	private final GainedSpellsEditPanel gainedSpellsTable = new GainedSpellsEditPanel();
	private final PreparedSpellsEditPanel preparedSpellsTable = new PreparedSpellsEditPanel();
	private final SingleSpellSlotsEditPanel singleSlotsTable = new SingleSpellSlotsEditPanel();
	private final FullSpellSlotsEditPanel fullSlotsTable = new FullSpellSlotsEditPanel();

	private CharacterClass cls;

	public ClassSpellcastingEditPanel()
	{
		super(true);

		UILib.addLabel(headerPanel, "Spellcasting Settings", Color.black).setFont(UILib.boldFont);

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		c.gridwidth = 2;
		spellcastingDropdown = UILib.addLabeledLinkedDropdown(bodyPanel,
				List.of("", Ability.Int.toString(), Ability.Wis.toString(), Ability.Cha.toString(),
						Ability.Primary.toString()),
				"Spellcasting Ability: ", c, Color.white, Color.black, () -> getSpellcastingAbility(),
				(s) -> updateSpellcastingAbility(s));
		c.gridy++;

		UILib.addLabel(bodyPanel, "Cantrips per Class Level", c, Color.black);
		c.gridy++;
		bodyPanel.add(cantripsTable, c);
		c.gridy++;

		UILib.addLabel(bodyPanel,
				"Does this class have its own list of known spells or pull from its full spell list when selecting prepared spells?",
				c, Color.black);
		c.gridy++;

		c.gridwidth = 1;
		knownSpellsBtn = UILib.addRadioBtn(bodyPanel, "Personal Spell List", this, c, Color.black);
		c.gridx++;
		preparedSpellsBtn = UILib.addRadioBtn(bodyPanel, "Full Spell List", this, c, Color.black);
		c.gridx = 0;
		c.gridy++;

		ButtonGroup spellListBtns = new ButtonGroup();
		spellListBtns.add(knownSpellsBtn);
		spellListBtns.add(preparedSpellsBtn);

		c.gridwidth = 2;
		bodyPanel.add(gainedSpellsTable, c);
		c.gridy++;
		bodyPanel.add(preparedSpellsTable, c);
		fullSlotsTable.setVisible(false);
		c.gridy++;

		UILib.addLabel(bodyPanel,
				"Does this class have one set of spell slots of the same level or spell slots per spell level?", c,
				Color.black);
		c.gridy++;

		c.gridwidth = 1;
		singleSlotsBtn = UILib.addRadioBtn(bodyPanel, "One set of Slots", this, c, Color.black);
		c.gridx++;
		multiSlotsBtn = UILib.addRadioBtn(bodyPanel, "Spell Slots per Level", this, c, Color.black);
		c.gridx = 0;
		c.gridy++;

		ButtonGroup spellSlotBtns = new ButtonGroup();
		spellSlotBtns.add(singleSlotsBtn);
		spellSlotBtns.add(multiSlotsBtn);

		c.gridwidth = 2;
		bodyPanel.add(singleSlotsTable, c);
		c.gridy++;
		bodyPanel.add(fullSlotsTable, c);
		fullSlotsTable.setVisible(false);

	}

	public void updatePanel(CharacterClass cls)
	{
		this.cls = cls;

		spellcastingDropdown.updateValue();

		cantripsTable.updateClass(cls);
		cantripsTable.updateValues();

		singleSlotsTable.updateClass(cls);
		fullSlotsTable.updateClass(cls);

		gainedSpellsTable.updateClass(cls);
		preparedSpellsTable.updateClass(cls);

		updateSpellsBtn();
		updateSlotBtn();
	}

	private String getSpellcastingAbility()
	{
		return cls.getSpellcastingAbility() == null ? "" : cls.getSpellcastingAbility().toString();
	}

	private void updateSpellcastingAbility(String s)
	{
		if (s.isBlank())
		{
			cls.setSpellcastingAbility(null);
		}
		else
		{
			cls.setSpellcastingAbility(Ability.valueOf(s));
		}
	}

	private void updateSlotBtn()
	{
		if (cls.isFullSlots())
		{
			multiSlotsBtn.setSelected(true);
			singleSlotsTable.setVisible(false);
			fullSlotsTable.setVisible(true);
			fullSlotsTable.updateValues();
		}
		else
		{
			singleSlotsBtn.setSelected(true);
			singleSlotsTable.setVisible(true);
			fullSlotsTable.setVisible(false);
			singleSlotsTable.updateValues();
		}
	}

	private void updateSpellsBtn()
	{
		if (cls.getClassSpells().isGainPerLvl())
		{
			knownSpellsBtn.setSelected(true);
			preparedSpellsTable.setVisible(false);
			gainedSpellsTable.setVisible(true);
			gainedSpellsTable.updateValues();
		}
		else
		{
			preparedSpellsBtn.setSelected(true);
			preparedSpellsTable.setVisible(true);
			gainedSpellsTable.setVisible(false);
			preparedSpellsTable.updateValues();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(knownSpellsBtn))
		{
			cls.getClassSpells().setGainPerLvl(true);
			preparedSpellsTable.setVisible(false);
			gainedSpellsTable.setVisible(true);
			gainedSpellsTable.updateValues();
		}
		else if (e.getSource().equals(preparedSpellsBtn))
		{
			cls.getClassSpells().setGainPerLvl(false);
			preparedSpellsTable.setVisible(true);
			gainedSpellsTable.setVisible(false);
			preparedSpellsTable.updateValues();
		}
		else if (e.getSource().equals(singleSlotsBtn))
		{
			cls.setFullSlots(false);
			singleSlotsTable.setVisible(true);
			fullSlotsTable.setVisible(false);
			singleSlotsTable.updateValues();
		}
		else if (e.getSource().equals(multiSlotsBtn))
		{
			cls.setFullSlots(true);
			singleSlotsTable.setVisible(false);
			fullSlotsTable.setVisible(true);
			fullSlotsTable.updateValues();
		}
	}
}
