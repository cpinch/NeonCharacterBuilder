package ncb.ui.DataEditor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ncb.data.interfaces.Customizable;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public abstract class SelectorPanel extends JPanel implements ActionListener, ListSelectionListener
{
	private static final long serialVersionUID = 7574108347231064490L;

	protected final JList<SelectorItem> selector = new JList<>(new DefaultListModel<>());
	protected final JPanel display = new NoHorizontalScrollPanel();

	private JButton newBtn = new JButton("New");
	private JButton clrBtn = new JButton("Clear");
	private final String typeName;

	public SelectorPanel(String typeName)
	{
		this.typeName = typeName;
		setLayout(new BorderLayout());
		display.setLayout(new BorderLayout());

		selector.addListSelectionListener(this);
		JPanel selectorPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		UILib.addScrollPaneFor(selectorPanel, selector, c);
		c.weighty = 0;
		c.gridy++;
		newBtn.addActionListener(this);
		selectorPanel.add(newBtn, c);
		c.gridy++;
		clrBtn.addActionListener(this);
		selectorPanel.add(clrBtn, c);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selectorPanel, UILib.getScrollPaneFor(display));
		split.setContinuousLayout(true);

		add(split, BorderLayout.CENTER);

		updateItems();
	}

	protected abstract EditPanel getEditPanel();

	protected abstract List<? extends Customizable> getItemsList();

	protected abstract void createNew(String name);

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (selector.getSelectedValue() != null)
			{
				getEditPanel().setSelected(selector.getSelectedValue().getElement());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(newBtn))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New " + this.typeName,
					JOptionPane.QUESTION_MESSAGE);

			if (name != null && !name.isBlank())
			{
				createNew(name);
				updateItems();
			}
		}
		else if (e.getSource().equals(clrBtn))
		{
			Customizable c = selector.getSelectedValue().getElement();
			if (c != null)
			{
				c.setCustom(false);
			}
		}
	}

	public void updateItems()
	{
		selector.removeListSelectionListener(this);

		SelectorItem selected = selector.getSelectedValue();
		DefaultListModel<SelectorItem> model = ((DefaultListModel<SelectorItem>) selector.getModel());
		model.removeAllElements();
		model.addAll(getItemsList().stream().map(e -> new SelectorItem(e)).toList());
		selector.setSelectedValue(selected, true);

		selector.addListSelectionListener(this);
	}
}
