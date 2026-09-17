package nocb.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import nocb.data.SpeciesTrait;
import nocb.main.CharacterSheet;

public class SpeciesFeaturesPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final List<FeaturePanel> featurePanels = new ArrayList<>();

	public SpeciesFeaturesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(VaporwaveColors.DARK_PURPLE);
	}

	public void updateDetails()
	{
		if (sheet.getSpecies() != null)
		{
			removeAll();
			featurePanels.clear();
			for (SpeciesTrait feature : sheet.getSpecies().getTraits())
			{
				FeaturePanel fp = new FeaturePanel(sheet, feature, false);
				featurePanels.add(fp);
				add(fp);
				add(Box.createVerticalStrut(5));
			}
		}
	}

	public void updateHomeworld()
	{
		for (FeaturePanel fp : featurePanels)
		{
			fp.updateHomeworld();
		}
	}
}
