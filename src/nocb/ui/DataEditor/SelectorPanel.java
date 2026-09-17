package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public abstract class SelectorPanel extends JPanel implements ActionListener, ListSelectionListener
{
	private static final long serialVersionUID = 7574108347231064490L;

	protected final JList<SelectorItem> selector = new JList<>(new DefaultListModel<>());
	protected final JPanel display = new NoHorizontalScrollPanel();

	private JButton newBtn = new JButton("New");
	private final String typeName;

	public SelectorPanel(String typeName)
	{
		this.typeName = typeName;

		display.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
		newBtn.addActionListener(this);
		selector.addListSelectionListener(this);
		JScrollPane selScroll = new JScrollPane(selector);
		selScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane disScroll = new JScrollPane(display);
		disScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel selectorPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		selectorPanel.add(selScroll, c);
		c.weighty = 0;
		c.gridy++;
		selectorPanel.add(newBtn, c);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectorPanel, disScroll);
		split.setContinuousLayout(true);
		add(split, BorderLayout.CENTER);
	}

	protected abstract void updateSelection();

	protected abstract void createNew(String name);

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			updateSelection();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(newBtn))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New " + this.typeName,
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank())
			{
				createNew(name);
			}
		}
	}
}
