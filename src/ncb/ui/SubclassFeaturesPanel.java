package ncb.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import ncb.data.Feature;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;

public class SubclassFeaturesPanel extends NoHorizontalScrollPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final List<FeaturePanel> featurePanels = new ArrayList<>();

	public SubclassFeaturesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.SUBCLASS, this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(VaporwaveColors.DARK_PURPLE);
	}

	@Override
	public void updateProperty(String prop)
	{
		if (sheet.getCharClass() != null && sheet.getCharClass().getSubclass() != null)
		{
			removeAll();
			featurePanels.forEach(fp -> PropertyListener.stopListening(fp));
			featurePanels.clear();
			for (Feature feature : sheet.getCharClass().getSubclass().getSubclassFeatures())
			{
				FeaturePanel fp = new FeaturePanel(sheet, feature, false);
				featurePanels.add(fp);
				add(fp);
				add(Box.createVerticalStrut(5));
			}
			revalidate();
		}
	}
}
