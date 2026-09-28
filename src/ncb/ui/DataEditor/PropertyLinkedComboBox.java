package ncb.ui.DataEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComboBox;

public class PropertyLinkedComboBox extends JComboBox<String> implements HasLinkedProperty
{
	private static final long serialVersionUID = 1745018999383177794L;

	private final Supplier<String> getter;
	private final Consumer<String> setter;

	public PropertyLinkedComboBox(List<String> opts, Supplier<String> getter, Consumer<String> setter)
	{
		super(opts.toArray(new String[0]));

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
		setSelectedItem(getter.get());
	}

	private void setValue()
	{
		setter.accept((String) getSelectedItem());
	}
}
