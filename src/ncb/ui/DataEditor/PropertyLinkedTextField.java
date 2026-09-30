package ncb.ui.DataEditor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import ncb.ui.UILib;
import ncb.ui.UndoTextField;

public class PropertyLinkedTextField extends UndoTextField implements HasLinkedProperty
{
	private static final long serialVersionUID = 1745018999383177794L;

	private final Supplier<String> getter;
	private final Consumer<String> setter;

	public PropertyLinkedTextField(Supplier<String> getter, Consumer<String> setter)
	{
		super(20);

		this.getter = getter;
		this.setter = setter;
		addFocusListener(UILib.createFocusListener(() -> setValue()));
	}

	@Override
	public void updateValue()
	{
		setText(getter.get());
	}

	private void setValue()
	{
		setter.accept(getText());
	}
}
