package ncb.ui.DataEditor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import ncb.ui.UILib;
import ncb.ui.UndoTextArea;

public class PropertyLinkedTextArea extends UndoTextArea implements HasLinkedProperty
{
	private static final long serialVersionUID = 1745018999383177794L;

	private final Supplier<String> getter;
	private final Consumer<String> setter;

	public PropertyLinkedTextArea(int rows, Supplier<String> getter, Consumer<String> setter)
	{
		super(rows, 20);

		this.getter = getter;
		this.setter = setter;
		setWrapStyleWord(true);
		setLineWrap(true);
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
