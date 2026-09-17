package nocb.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import nocb.data.ClassFeature;
import nocb.main.CharacterSheet;

public class ClassFeaturesPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final List<FeaturePanel> featurePanels = new ArrayList<>();

	public ClassFeaturesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		// setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(VaporwaveColors.DARK_PURPLE);
	}

	public void updateDetails()
	{
		if (sheet.getCharClass() != null)
		{
			removeAll();
			featurePanels.clear();
			for (ClassFeature feature : sheet.getCharClass().getClassFeatures())
			{
				FeaturePanel fp = new FeaturePanel(sheet, feature, false);
				featurePanels.add(fp);
				add(fp);
				add(Box.createVerticalStrut(5));
			}
		}
	}

	public void skillsUpdated()
	{
		for (FeaturePanel fp : featurePanels)
		{
			fp.updateSkillExpertiseLists();
		}
	}
}
