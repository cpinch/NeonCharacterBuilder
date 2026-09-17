package nocb.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;

import nocb.data.Selectable;
import nocb.data.SelectableFeature;
import nocb.data.SelectablePrereq;
import nocb.main.CharacterSheet;

public class SelectablePanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JLabel prereqs = UILib.getLabel("");
	private final List<FeaturePanel> featurePanels = new ArrayList<>();

	public SelectablePanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		add(prereqs);
	}

	public void setSelected(Selectable f)
	{
		List<SelectablePrereq> pres = f.getPrereqs();
		if (pres.isEmpty())
		{
			prereqs.setVisible(false);
		}
		else
		{
			prereqs.setVisible(true);
			prereqs.setText(
					"<html><i>" + String.join(", ", pres.stream().map(p -> p.toString()).toList()) + "</i></html>");
		}

		for (FeaturePanel ffp : featurePanels)
		{
			this.remove(ffp);
		}
		featurePanels.clear();
		for (SelectableFeature ff : f.getFeatures())
		{
			FeaturePanel ffp = new FeaturePanel(sheet, ff, false);
			add(ffp);
			featurePanels.add(ffp);
		}
	}
}
