package nocb.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UILib
{
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

	public static JTextPane getTextDisplay()
	{
		JTextPane textPane = new JTextPane()
		{
			private static final long serialVersionUID = -5823205338091051232L;

			@Override
			public boolean getScrollableTracksViewportWidth()
			{
				return true;
			}
		};
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		textPane.setBackground(VaporwaveColors.DARK_PURPLE);
		textPane.setForeground(VaporwaveColors.HOT_PINK);
		return textPane;
	}

	public static JTextPane addTextDisplay(JPanel panelToAddTo, String text, GridBagConstraints c)
	{
		JTextPane textPane = getTextDisplay();
		textPane.setOpaque(false);
		textPane.setText(text);
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		panelToAddTo.add(scrollPane, c);
		return textPane;
	}

	public static JLabel getLabel(String label)
	{
		JLabel l = new JLabel(label);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		l.setFont(standardFont);
		l.setBackground(VaporwaveColors.DARK_PURPLE);
		l.setForeground(VaporwaveColors.HOT_PINK);
		return l;
	}

	public static JLabel addLabel(JPanel panelToAddTo, String label)
	{
		JLabel l = getLabel(label);
		panelToAddTo.add(l);
		return l;
	}

	public static JLabel addLabel(JPanel panelToAddTo, String label, GridBagConstraints c)
	{
		JLabel l = getLabel(label);
		panelToAddTo.add(l, c);
		return l;
	}

	public static JLabel addLabeledComponent(JPanel panelToAddTo, String label, Component field, GridBagConstraints c)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JLabel l = getLabel(label);
		panel.setBackground(VaporwaveColors.DARK_PURPLE);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 20));
		l.setForeground(VaporwaveColors.HOT_PINK);
		panel.add(l);
		panel.add(field);
		panelToAddTo.add(panel, c);
		return l;
	}

	public static JLabel addLabeledComponent(JPanel panelToAddTo, String label, Component field)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		JLabel l = getLabel(label);
		panel.setBackground(VaporwaveColors.DARK_PURPLE);
		l.setForeground(VaporwaveColors.HOT_PINK);
		panel.add(l);
		panel.add(field);
		panelToAddTo.add(panel);
		return l;
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

	public static void centerTextInPane(JTextPane pane)
	{
		StyledDocument doc = pane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}
}
