package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nocb.data.Background;
import nocb.data.Feat;
import nocb.main.CharacterSheet;

public class BackgroundFeatPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Feat> feats;
	private final JLabel label = UILib.getLabel("");
	private final SelectablePanel desc;

	public BackgroundFeatPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();

		JPanel selectPanel = new JPanel();
		selectPanel.setOpaque(false);
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));

		UILib.addLabel(selectPanel, "Feat: ");

		selectPanel.add(label);

		feats = new JComboBox<>();
		feats.setBackground(VaporwaveColors.DEEP_VIOLET);
		feats.setForeground(VaporwaveColors.LASER_YELLOW);
		feats.addActionListener(this);
		selectPanel.add(feats);

		add(selectPanel, c);
		c.gridy++;
		c.weighty = 1;

		desc = new SelectablePanel(sheet);
		JScrollPane scroll = new JScrollPane(desc);
		scroll.getViewport().setOpaque(false);
		scroll.setOpaque(false);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, c);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (e.getSource().equals(feats))
			{
				Feat f = (Feat) feats.getSelectedItem();
				if (f != null)
				{
					bg.setFeat(f);
					desc.setSelected(f);
				}
			}
		}
	}

	public void updateDetails()
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (bg.getName().equals("Custom"))
			{
				Feat bgFeat = bg.getFeat();
				feats.removeAllItems();
				Feat.getAllValidFeatsOfType(sheet, "Origin").forEach(f -> feats.addItem(f));
				feats.setVisible(true);
				if (((DefaultComboBoxModel<Feat>) feats.getModel()).getIndexOf(bgFeat) > 0)
				{
					feats.setSelectedItem(bgFeat);
				}
				else if (feats.getItemCount() > 0)
				{
					feats.setSelectedIndex(0);
				}
				label.setVisible(false);
			}
			else
			{
				feats.setVisible(false);
				label.setVisible(true);
				Feat f = bg.getFeat();
				label.setText(f.getName());
				desc.setSelected(f);
			}
		}
	}
}
