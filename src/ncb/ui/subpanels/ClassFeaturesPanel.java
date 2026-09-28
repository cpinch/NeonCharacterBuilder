package ncb.ui.subpanels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;

import ncb.data.Feature;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.NoHorizontalScrollPanel;

public class ClassFeaturesPanel extends NoHorizontalScrollPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final List<FeaturePanel> featurePanels = new ArrayList<>();

	public ClassFeaturesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	public void updateProperty(String prop)
	{
		if (sheet.getCharClass() != null)
		{
			removeAll();
			featurePanels.forEach(fp -> PropertyListener.stopListening(fp)); // Dropping these UI elements
			featurePanels.clear();
			for (Feature feature : sheet.getCharClass().getClassOnlyFeatures())
			{
				FeaturePanel fp = new FeaturePanel(sheet, feature,
						feature.getLevel() < sheet.getCharClass().getLevel());
				featurePanels.add(fp);
				add(fp);
				add(Box.createVerticalStrut(5));
			}
		}
	}
}
