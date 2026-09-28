package ncb.ui.subpanels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class AbilityScorePanel extends JPanel implements ActionListener, PropertyChangeListener
{
	private static final long serialVersionUID = 2221665837843285640L;

	// For consistency across all ability score panels
	private static final Dimension labelDim = new Dimension(100, 15);

	private final JLabel label;
	private final JFormattedTextField input;
	private final JCheckBox bgCheckbox1, bgCheckbox2;
	private final JRadioButton spellRadio;

	private final Consumer<Integer> spinnerCallback;
	private final BiConsumer<Integer, Boolean> checkCallback;
	private final Consumer<Boolean> spellCallback;

	public AbilityScorePanel(String text, Consumer<Integer> spinnerCallback, BiConsumer<Integer, Boolean> checkCallback,
			Consumer<Boolean> spellCallback)
	{
		this.spinnerCallback = spinnerCallback;
		this.checkCallback = checkCallback;
		this.spellCallback = spellCallback;

		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		setOpaque(false);

		label = UILib.addLabel(this, text, VaporwaveColors.HOT_PINK);
		label.setPreferredSize(labelDim);
		label.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		NumberFormatter scoreFormatter = new NumberFormatter(intFormat);
		scoreFormatter.setMinimum(3);
		scoreFormatter.setMaximum(18);
		scoreFormatter.setAllowsInvalid(true);
		scoreFormatter.setCommitsOnValidEdit(true);
		input = new JFormattedTextField(scoreFormatter);
		input.setValue(8);
		input.setColumns(5);
		input.setHorizontalAlignment(JTextField.CENTER);
		input.setFont(UILib.boldFont.deriveFont(20f));
		input.addPropertyChangeListener("value", this);
		UILib.selectAllOnFocus(input);
		add(input);

		bgCheckbox1 = UILib.addCheckbox(this, "+1", this, VaporwaveColors.HOT_PINK);
		bgCheckbox1.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
		bgCheckbox2 = UILib.addCheckbox(this, "+2", this, VaporwaveColors.HOT_PINK);
		ButtonGroup bg = new NoneSelectedButtonGroup();
		bg.add(bgCheckbox1);
		bg.add(bgCheckbox2);

		spellRadio = UILib.addRadioBtn(this, "Spellcasting Ability", this, VaporwaveColors.HOT_PINK);
	}

	public void setScore(int score)
	{
		input.setValue(score);
	}

	public void setLabelBoldState(boolean bold)
	{
		label.setFont(bold ? UILib.boldFont : UILib.standardFont);
	}

	public void clearCheckboxes()
	{
		bgCheckbox1.setSelected(false);
		bgCheckbox1.setVisible(false);
		bgCheckbox2.setSelected(false);
		bgCheckbox2.setVisible(false);
	}

	public void showCheckboxes()
	{
		bgCheckbox1.setVisible(true);
		bgCheckbox2.setVisible(true);
	}

	public void selectCheckbox1()
	{
		bgCheckbox1.setSelected(true);
	}

	public void selectCheckbox2()
	{
		bgCheckbox2.setSelected(true);
	}

	public void unSelectCheckboxes()
	{
		bgCheckbox1.setSelected(false);
		bgCheckbox2.setSelected(false);
	}

	public boolean checkbox1Selected()
	{
		return bgCheckbox1.isSelected();
	}

	public boolean checkbox2Selected()
	{
		return bgCheckbox2.isSelected();
	}

	public JRadioButton getSpellcastingButton()
	{
		return spellRadio;
	}

	public void showSpellcastingButton(boolean show)
	{
		spellRadio.setVisible(show);
	}

	public void selectSpellcasting()
	{
		spellRadio.setSelected(true);
		// Radio buttons are special and don't send action events when programmatically
		// selected
		// We could ItemListener, but this is easier
		spellCallback.accept(spellRadio.isSelected());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(bgCheckbox1))
		{
			checkCallback.accept(1, bgCheckbox1.isSelected());
		}
		else if (e.getSource().equals(bgCheckbox2))
		{
			checkCallback.accept(2, bgCheckbox2.isSelected());
		}
		else if (e.getSource().equals(spellRadio))
		{
			spellCallback.accept(spellRadio.isSelected());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		spinnerCallback.accept((int) evt.getNewValue());
	}

	private class NoneSelectedButtonGroup extends ButtonGroup
	{
		private static final long serialVersionUID = 1108406688567699422L;

		@Override
		public void setSelected(ButtonModel model, boolean selected)
		{
			if (selected)
			{
				super.setSelected(model, selected);
			}
			else
			{
				clearSelection();
			}
		}
	}
}
