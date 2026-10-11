package ncb.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.function.BooleanConsumer;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.ui.DataEditor.PropertyLinkedCheckbox;
import ncb.ui.DataEditor.PropertyLinkedComboBox;
import ncb.ui.DataEditor.PropertyLinkedSpinner;
import ncb.ui.DataEditor.PropertyLinkedTextArea;
import ncb.ui.DataEditor.PropertyLinkedTextField;

public class UILib
{
	@Deprecated
	public static Font standardFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	public static Font boldFont = standardFont.deriveFont(Font.BOLD);
	public static Font italicFont = standardFont.deriveFont(Font.ITALIC);

	public static GridBagConstraints getStandardGBC()
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		return c;
	}

	public static JLabel addLabel(JComponent parent, String label)
	{
		JLabel l = getLabel(label);
		parent.add(l);
		return l;
	}

	public static JLabel addLabel(JComponent parent, String label, GridBagConstraints c)
	{
		JLabel l = getLabel(label);
		parent.add(l, c);
		return l;
	}

	public static JLabel getLabel(String label)
	{
		JLabel l = new JLabel(label);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		l.setOpaque(false);
		return l;
	}

	public static JScrollPane addScrollPaneFor(JComponent parent, JComponent comp)
	{
		JScrollPane scroll = getScrollPaneFor(comp);
		parent.add(scroll);
		return scroll;
	}

	public static JScrollPane addScrollPaneFor(JComponent parent, JComponent comp, GridBagConstraints c)
	{
		JScrollPane scroll = getScrollPaneFor(comp);
		parent.add(scroll, c);
		return scroll;
	}

	public static JScrollPane getScrollPaneFor(JComponent comp)
	{
		JScrollPane scroll = new JScrollPane(comp);
		scroll.getViewport().setOpaque(false);
		scroll.setOpaque(false);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return scroll;
	}

	public static JTextPane addTextDisplay(JComponent parent, String text, GridBagConstraints c)
	{
		JTextPane textPane = getTextDisplay(text);
		addScrollPaneFor(parent, textPane, c);
		return textPane;
	}

	public static JTextPane getTextDisplay(String text)
	{
		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		textPane.setText(text);
		return textPane;
	}

	public static JTextField addTextField(JComponent parent)
	{
		JTextField textPane = getTextField();
		parent.add(textPane);
		return textPane;
	}

	public static JTextField addTextField(JComponent parent, GridBagConstraints c)
	{
		JTextField textPane = getTextField();
		parent.add(textPane, c);
		return textPane;
	}

	public static JTextField getTextField()
	{
		JTextField textPane = new JTextField(20);
		textPane.setOpaque(false);
		return textPane;
	}

	public static JCheckBox addCheckbox(JComponent parent, String label, ActionListener listener)
	{
		JCheckBox box = getCheckbox(label, listener);
		parent.add(box);
		return box;
	}

	public static JCheckBox getCheckbox(JComponent parent, String label, ActionListener listener, GridBagConstraints c)
	{
		JCheckBox box = getCheckbox(label, listener);
		parent.add(box, c);
		return box;
	}

	public static JCheckBox getCheckbox(String label, ActionListener listener)
	{
		JCheckBox box = new JCheckBox(label);
		box.addActionListener(listener);
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.setOpaque(false);
		return box;
	}

	public static JRadioButton addRadioBtn(JComponent parent, String label, ActionListener listener)
	{
		JRadioButton btn = getRadioBtn(label, listener);
		parent.add(btn);
		return btn;
	}

	public static JRadioButton addRadioBtn(JComponent parent, String label, ActionListener listener,
			GridBagConstraints c)
	{
		JRadioButton btn = getRadioBtn(label, listener);
		parent.add(btn, c);
		return btn;
	}

	public static JRadioButton getRadioBtn(String label, ActionListener listener)
	{
		JRadioButton btn = new JRadioButton(label);
		btn.addActionListener(listener);
		btn.setAlignmentX(Component.LEFT_ALIGNMENT);
		btn.setOpaque(false);
		return btn;
	}

	public static <T> JComboBox<T> getComboBox(T[] items, ActionListener listener)
	{
		JComboBox<T> combo = new JComboBox<T>(items);
		combo.addActionListener(listener);
		return combo;
	}

	public static void selectAllOnFocus(JTextComponent c)
	{
		// We use a focus listener to auto-select all text to make setting these less
		// annoying
		c.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				SwingUtilities.invokeLater(() -> c.selectAll());
			}
		});
	}

	public static JLabel addLabeledComponent(JComponent parent, String label, JComponent comp)
	{
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JLabel l = getLabel(label);
		l.putClientProperty(FlatClientProperties.STYLE, "font: bold");
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 20));
		panel.add(l);
		panel.add(comp);
		parent.add(panel);
		return l;
	}

	public static JLabel addLabeledComponent(JComponent parent, String label, JComponent comp, GridBagConstraints c)
	{
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JLabel l = getLabel(label);
		l.putClientProperty(FlatClientProperties.STYLE, "font: bold");
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		panel.add(l);
		panel.add(comp);
		parent.add(panel, c);
		return l;
	}

	public static PropertyLinkedTextField getLinkedTextField(Supplier<String> getter, Consumer<String> setter)
	{
		PropertyLinkedTextField textPane = new PropertyLinkedTextField(getter, setter);
		return textPane;
	}

	public static PropertyLinkedTextField addLabeledLinkedTextField(JComponent parent, String label,
			GridBagConstraints c, Supplier<String> getter, Consumer<String> setter)
	{
		PropertyLinkedTextField field = getLinkedTextField(getter, setter);
		addLabeledComponent(parent, label, field, c);
		return field;
	}

	public static PropertyLinkedSpinner getLinkedSpinner(SpinnerNumberModel model, IntSupplier getter,
			IntConsumer setter)
	{
		PropertyLinkedSpinner spinner = new PropertyLinkedSpinner(model, getter, setter);
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		return spinner;
	}

	public static PropertyLinkedSpinner addLabeledLinkedSpinner(JComponent parent, String label,
			SpinnerNumberModel model, IntSupplier getter, IntConsumer setter)
	{
		PropertyLinkedSpinner spinner = getLinkedSpinner(model, getter, setter);
		addLabeledComponent(parent, label, spinner);
		return spinner;
	}

	public static PropertyLinkedSpinner addLabeledLinkedSpinner(JComponent parent, String label, GridBagConstraints c,
			SpinnerNumberModel model, IntSupplier getter, IntConsumer setter)
	{
		PropertyLinkedSpinner spinner = getLinkedSpinner(model, getter, setter);
		addLabeledComponent(parent, label, spinner, c);
		return spinner;
	}

	public static PropertyLinkedTextArea getLinkedTextArea(int rows, Supplier<String> getter, Consumer<String> setter)
	{
		PropertyLinkedTextArea textArea = new PropertyLinkedTextArea(rows, getter, setter);
		return textArea;
	}

	public static PropertyLinkedTextArea addLabeledLinkedTextArea(JComponent parent, int rows, String label,
			GridBagConstraints c, Supplier<String> getter, Consumer<String> setter)
	{
		PropertyLinkedTextArea field = getLinkedTextArea(rows, getter, setter);
		addLabeledComponent(parent, label, field, c);
		return field;
	}

	public static PropertyLinkedComboBox getLinkedDropdown(List<String> opts, Supplier<String> getter,
			Consumer<String> setter)
	{
		PropertyLinkedComboBox dropdown = new PropertyLinkedComboBox(opts, getter, setter);
		return dropdown;
	}

	public static PropertyLinkedComboBox addLabeledLinkedDropdown(JComponent parent, List<String> ops, String label,
			GridBagConstraints c, Supplier<String> getter, Consumer<String> setter)
	{
		PropertyLinkedComboBox field = getLinkedDropdown(ops, getter, setter);
		addLabeledComponent(parent, label, field, c);
		return field;
	}

	public static PropertyLinkedCheckbox getLinkedCheckbox(BooleanSupplier getter, BooleanConsumer setter)
	{
		PropertyLinkedCheckbox textArea = new PropertyLinkedCheckbox(getter, setter);
		return textArea;
	}

	public static PropertyLinkedCheckbox addLabeledLinkedCheckbox(JComponent parent, String label, GridBagConstraints c,
			BooleanSupplier getter, BooleanConsumer setter)
	{
		PropertyLinkedCheckbox field = getLinkedCheckbox(getter, setter);
		addLabeledComponent(parent, label, field, c);
		return field;
	}

	public static DocumentListener createDocumentListener(Runnable callOnChange)
	{
		return new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				callOnChange.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				callOnChange.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
			}
		};
	}

	public static FocusListener createFocusListener(Runnable callOnChange)
	{
		return new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				callOnChange.run();
			}
		};
	}

	public static ChangeListener createChangeListener(Runnable callOnChange)
	{
		return new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				callOnChange.run();
			}
		};
	}
}
