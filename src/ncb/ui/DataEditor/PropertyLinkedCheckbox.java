package ncb.ui.DataEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BooleanSupplier;

import javax.swing.JCheckBox;

import org.apache.commons.lang3.function.BooleanConsumer;

public class PropertyLinkedCheckbox extends JCheckBox implements HasLinkedProperty
{
	private static final long serialVersionUID = 1745018999383177794L;

	private final BooleanSupplier getter;
	private final BooleanConsumer setter;

	public PropertyLinkedCheckbox(BooleanSupplier getter, BooleanConsumer setter)
	{
		this.getter = getter;
		this.setter = setter;
		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setValue();
			}
		});
	}

	@Override
	public void updateValue()
	{
		setSelected(getter.getAsBoolean());
	}

	private void setValue()
	{
		setter.accept(isSelected());
	}
}
