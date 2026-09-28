package ncb.ui.DataEditor;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ncb.ui.UILib;

public class PropertyLinkedSpinner extends JSpinner implements HasLinkedProperty
{
	private static final long serialVersionUID = 1745018999383177794L;

	private final IntSupplier getter;
	private final IntConsumer setter;

	public PropertyLinkedSpinner(SpinnerNumberModel model, IntSupplier getter, IntConsumer setter)
	{
		super(model);
		this.getter = getter;
		this.setter = setter;
		addChangeListener(UILib.createChangeListener(() -> setValue()));
	}

	@Override
	public void updateValue()
	{
		setValue(getter.getAsInt());
	}

	private void setValue()
	{
		setter.accept((int) getValue());
	}
}
