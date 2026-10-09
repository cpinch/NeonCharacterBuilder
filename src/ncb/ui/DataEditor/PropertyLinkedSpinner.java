package ncb.ui.DataEditor;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import ncb.ui.UILib;

public class PropertyLinkedSpinner extends JSpinner implements HasLinkedProperty
{
	private static final long serialVersionUID = 1745018999383177794L;

	private final IntSupplier getter;
	private final IntConsumer setter;
	private final ChangeListener listener = UILib.createChangeListener(() -> setValue());

	public PropertyLinkedSpinner(SpinnerNumberModel model, IntSupplier getter, IntConsumer setter)
	{
		super(model);
		this.getter = getter;
		this.setter = setter;
		addChangeListener(listener);
	}

	@Override
	public void updateValue()
	{
		removeChangeListener(listener);
		setValue(getter.getAsInt());
		addChangeListener(listener);
	}

	private void setValue()
	{
		setter.accept((int) getValue());
	}
}
