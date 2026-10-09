package ncb.ui.DataEditor;

import java.awt.AWTKeyStroke;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.KeyStroke;

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

		// Make tab go to next component and shift-tab go back to last component
		Set<AWTKeyStroke> forwardKeys = new HashSet<>(
				getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.add(KeyStroke.getKeyStroke("TAB"));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

		Set<AWTKeyStroke> backwardKeys = new HashSet<>(
				getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.add(KeyStroke.getKeyStroke("shift TAB"));
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

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
