package nocb.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import nocb.io.JsonDataLoader;
import nocb.ui.AbilityPanel;
import nocb.ui.BackgroundPanel;
import nocb.ui.ClassPanel;
import nocb.ui.SpeciesPanel;
import nocb.ui.SpellsPanel;
import nocb.ui.SummaryPanel;
import nocb.ui.Toast;
import nocb.ui.UILib;
import nocb.ui.VaporwaveColors;
import nocb.ui.DataEditor.DataEditor;

public class BaseCharacterWindow extends JFrame implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = -2792167287995448499L;

	private CharacterSheet sheet = new CharacterSheet();

	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final JTextField nameF = new JTextField(45);
	private final ClassPanel classP = new ClassPanel(sheet);
	private final SpeciesPanel speciesP = new SpeciesPanel(sheet);
	private final AbilityPanel abilityP = new AbilityPanel(sheet);
	private final BackgroundPanel backP = new BackgroundPanel(sheet);
	private final SpellsPanel spellP = new SpellsPanel(sheet);
	private final SummaryPanel summaryP = new SummaryPanel(sheet);

	private static final String backS = "<- Back", dataES = "Data Editor";
	private final JButton back = new JButton();
	private final JButton next = new JButton("Next ->");
	private final JButton save = new JButton("Save");
	private final JButton load = new JButton("Load");

	public BaseCharacterWindow()
	{
		tabbedPane.setOpaque(false);
		tabbedPane.setBackground(VaporwaveColors.DEEP_VIOLET);
		tabbedPane.setForeground(VaporwaveColors.HOT_PINK);
		tabbedPane.addTab("Details", createCharacterDetailsTab());
		tabbedPane.addTab("Class", classP);
		tabbedPane.addTab("Species", speciesP);
		tabbedPane.addTab("Background", backP);
		tabbedPane.addTab("Ability Scores", abilityP);
		tabbedPane.addTab("Spells", spellP);
		tabbedPane.addTab("Summary/Export", summaryP);
		tabbedPane.addChangeListener(this);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel navBar = new JPanel(new GridLayout(1, 3));
		back.addActionListener(this);
		// We start on the details tab, no back from there
		// but we repurpose this button for the data editor here
		back.setEnabled(true);
		back.setText(dataES);
		back.setBackground(VaporwaveColors.NEON_BLUE);
		navBar.add(back);
		save.addActionListener(this);
		save.setEnabled(false); // Save is disabled until a character name is entered
		save.setBackground(VaporwaveColors.NEON_BLUE);
		navBar.add(save);
		next.addActionListener(this);
		next.setBackground(VaporwaveColors.NEON_BLUE);
		navBar.add(next);

		getContentPane().add(navBar, BorderLayout.SOUTH);
		getContentPane().setBackground(VaporwaveColors.DARK_PURPLE);

		setTitle("Neon Character Builder");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

	private JPanel createCharacterDetailsTab()
	{
		JPanel panel = new JPanel();
		panel.setBackground(VaporwaveColors.DARK_PURPLE);
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		JTextPane instructions = UILib.getTextDisplay();
		instructions.setText("<html><h1>Welcome to the Neon Character Builder.</h1><br>"
				+ "This tool is designed to guide you through the character creation process"
				+ " for the Neon Odyssey rpg using the standard 5.5E process.<br>"
				+ "Please navigate through each tab, in order, and follow the instructions on that tab.<br>"
				+ "At the end you will be able to export a form-fillable pdf which will be pre-filled with your character's details.<br><br>"
				+ "<b><i>To begin, please choose a name for your character (this will also be the name of the exported pdf).</b><i/>");
		panel.add(instructions, c);
		c.gridy++;

		nameF.getDocument().addDocumentListener(UILib.createDocumentListener(() ->
		{
			sheet.setName(nameF.getText());
			save.setEnabled(!sheet.getName().isBlank());
		}));
		UILib.addLabeledComponent(panel, "Character Name: ", nameF, c);
		c.gridy++;

		JPanel filler = new JPanel();
		filler.setBackground(VaporwaveColors.DARK_PURPLE);
		c.weighty = 1;
		panel.add(filler, c);
		c.gridy++;

		c.weighty = 0;
		load.addActionListener(this);
		load.setBackground(VaporwaveColors.NEON_BLUE);
		panel.add(load, c);

		return panel;
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		// Reload each tab on load to ensure it's consistent with the sheet data
		// We could try to be more specific, but this is safer
		if (e.getSource().equals(tabbedPane))
		{
			if (tabbedPane.getSelectedComponent().equals(classP))
			{
				classP.updateDetails();
			}
			else if (tabbedPane.getSelectedComponent().equals(speciesP))
			{
				speciesP.updateDetails();
			}
			else if (tabbedPane.getSelectedComponent().equals(backP))
			{
				backP.updateDetails();
			}
			else if (tabbedPane.getSelectedComponent().equals(abilityP))
			{
				abilityP.updateDetails();
			}
			else if (tabbedPane.getSelectedComponent().equals(spellP))
			{
				spellP.updateDetails();
			}
			else if (tabbedPane.getSelectedComponent().equals(summaryP))
			{
				summaryP.updateDetails();
			}
			back.setText(tabbedPane.getSelectedIndex() > 0 ? backS : dataES);
			next.setEnabled(tabbedPane.getSelectedIndex() < tabbedPane.getTabCount() - 1);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(back))
		{
			if (tabbedPane.getSelectedIndex() == 0)
			{
				new DataEditor();
			}
			else
			{
				tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
			}
		}
		else if (e.getSource().equals(next))
		{
			tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
		}
		else if (e.getSource().equals(save))
		{
			if (sheet.getName().isBlank())
			{
				JOptionPane.showMessageDialog(null, "Cannot save character with blank name", "Cannot save",
						JOptionPane.ERROR_MESSAGE);
			}
			JsonDataLoader.saveCharacterStateToFile(sheet, new File(sheet.getName() + ".nchar"));
		}
		else if (e.getSource().equals(load))
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("nchar files (*.nchar)", "nchar");
			fileChooser.setFileFilter(filter);
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
			{
				boolean success = JsonDataLoader.loadCharacterStateFromFile(sheet, fileChooser.getSelectedFile());
				if (success)
				{
					new Toast("Successfully loaded character sheet.", 0);
				}
				else
				{
					JOptionPane.showMessageDialog(null,
							"Encountered an error while loading character. Most likely the data set of the character builder this character was created on is different than this character builder's dataset. As much of the character was loaded as possible but some fields may have changed or been missed.",
							"Character Load Error", JOptionPane.ERROR_MESSAGE);
				}
				nameF.setText(sheet.getName());
			}
		}
		back.setText(tabbedPane.getSelectedIndex() > 0 ? backS : dataES);
		next.setEnabled(tabbedPane.getSelectedIndex() < tabbedPane.getTabCount() - 1);
	}

	public static void main(String[] args)
	{
		// TODO post1.0 - figure out a way to only do this on larger screens, checking
		// screensize with toolkit "locks in" the scaling, so that doesn't work.
		System.setProperty("sun.java2d.uiScale", "2.0");

		JsonDataLoader.loadAllFiles();

		new BaseCharacterWindow();
	}
}
