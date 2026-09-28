package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.loadables.Background;
import ncb.data.loadables.Feat;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class BackgroundFeatPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Feat> feats;
	private final JLabel label;
	private final SelectablePanel desc;

	public BackgroundFeatPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);
		PropertyListener.listenForChanges(PropertyListener.HOMEWORLD, this);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();

		label = UILib.addLabel(this, "Feat: ", VaporwaveColors.HOT_PINK);
		c.gridx++;
		feats = UILib.getComboBox(new Feat[0], this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(feats, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;

		c.weighty = 1;
		desc = new SelectablePanel(sheet);
		UILib.addScrollPaneFor(this, desc, c);

		updateProperty(PropertyListener.BACKGROUND);
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

	@Override
	public void updateProperty(String prop)
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
				label.setText("Feat: ");
			}
			else
			{
				feats.setVisible(false);
				Feat f = bg.getFeat();
				label.setText("Feat: " + f.getName());
				desc.setSelected(f);
			}
		}
	}
}
