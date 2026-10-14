package ncb.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ncb.data.SpellChoice;
import ncb.data.loadables.SpellList;
import ncb.io.DocExporter;
import ncb.ui.subpanels.SpellSelectionPanel;

public class CustomSpellsDocDialog extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final JButton export = new JButton("Export"), exportAndReturn = new JButton("Export and Return"),
			addSpell = new JButton("Add Spell"), addCantrip = new JButton("Add Cantrip");

	private final List<SpellChoice> spells = new ArrayList<>();

	private JPanel spellSelectionPanel;

	public CustomSpellsDocDialog()
	{
		setTitle("Custom Spells Doc Generator");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createSpellSelectionPanel(), BorderLayout.CENTER);
		addCantrip.addActionListener(this);
		;
		addSpell.addActionListener(this);
		JPanel buttonPanel1 = new JPanel(new GridLayout(1, 2));
		buttonPanel1.add(addCantrip);
		buttonPanel1.add(addSpell);
		getContentPane().add(buttonPanel1, BorderLayout.NORTH);
		export.addActionListener(this);
		exportAndReturn.addActionListener(this);
		JPanel buttonPanel2 = new JPanel(new GridLayout(1, 2));
		buttonPanel2.add(export);
		buttonPanel2.add(exportAndReturn);
		getContentPane().add(buttonPanel2, BorderLayout.SOUTH);

		pack();
		// Constrain to screen size if unm-aximized
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (getWidth() > screenSize.getWidth())
		{
			this.setSize(new Dimension((int) screenSize.getWidth(), getHeight()));
		}
		if (getHeight() > screenSize.getHeight())
		{
			this.setSize(new Dimension(getWidth(), (int) screenSize.getHeight()));
		}
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JScrollPane createSpellSelectionPanel()
	{
		spellSelectionPanel = new JPanel();
		spellSelectionPanel.setLayout(new BoxLayout(spellSelectionPanel, BoxLayout.Y_AXIS));
		spellSelectionPanel.setOpaque(false);

		JScrollPane scroll = UILib.getScrollPaneFor(spellSelectionPanel);
		scroll.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		add(scroll, BorderLayout.CENTER);
		return scroll;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(export))
		{
			DocExporter.exportSpells("Custom", spells.stream().map(sc -> sc.getSpell()).toList());
		}
		else if (e.getSource().equals(exportAndReturn))
		{
			DocExporter.exportSpells("Custom", spells.stream().map(sc -> sc.getSpell()).toList());
			dispose();
		}
		else if (e.getSource().equals(addSpell))
		{
			SpellChoice sc = new SpellChoice();
			spells.add(sc);
			spellSelectionPanel.add(new SpellSelectionPanel(sc, 9, SpellList.getAllSpellLists(), true));
			revalidate();
		}
		else if (e.getSource().equals(addCantrip))
		{
			SpellChoice sc = new SpellChoice();
			spells.add(sc);
			spellSelectionPanel.add(new SpellSelectionPanel(sc, 0, SpellList.getAllSpellLists(), true));
			revalidate();
		}
	}
}
