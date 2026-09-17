package nocb.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nocb.data.Background;
import nocb.main.CharacterSheet;

public class BackgroundToolsPanel extends JPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JTextField text = new JTextField();
	private final JLabel label = UILib.getLabel("");

	public BackgroundToolsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 10));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);

		UILib.addLabel(this, "Tool Proficiencies:  ");

		add(label);
		text.setEditable(false); // I don't want to have to deal with creating a whole selectable for tools just
									// for this
		add(text);
	}

	public void updateDetails()
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (bg.getName().equals("Custom"))
			{
				text.setVisible(true);
				text.setText("1 Tool Proficiency");
				label.setVisible(false);
			}
			else
			{
				text.setVisible(false);
				label.setVisible(true);
				label.setText(String.join(", ", bg.getToolProfs()));
			}
		}
	}
}
