package ncb.ui.DataEditor;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.SpinnerNumberModel;

import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public class LabeledSpinnersPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = 1221308345285329701L;

	private final List<PropertyLinkedSpinner> spinners = new ArrayList<>();

	public LabeledSpinnersPanel(String label, SpinnerNumberModel[] models, BiConsumer<Integer, Integer> callback)
	{
		setLayout(new GridLayout(1, models.length + 1));

		UILib.addLabel(this, label, Color.black);

		for (int i = 0; i < models.length; i++)
		{
			final int index = i;
			PropertyLinkedSpinner s = UILib.getLinkedSpinner(models[i], Color.white, Color.black, () -> getVal(index),
					(v) -> callback.accept(index, v));
			spinners.add(s);
			add(s);
		}
	}

	private Integer[] vals;

	private int getVal(int index)
	{
		if (vals.length > index)
		{
			return vals[index];
		}
		else
		{
			return 0;
		}
	}

	public void updateValues(Integer[] newVals)
	{
		vals = newVals;
		spinners.forEach(s -> s.updateValue());
	}
}
