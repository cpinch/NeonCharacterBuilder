package ncb.ui.subpanels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ncb.data.loadables.Background;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class BackgroundToolsPanel extends JPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JTextField text;
	private final JLabel label;

	public BackgroundToolsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 10));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);

		label = UILib.addLabel(this, "Tool Proficiencies:  ", VaporwaveColors.HOT_PINK);
		label.setFont(UILib.boldFont);

		text = UILib.getTextField(VaporwaveColors.HOT_PINK);
		add(text);
		// TODO Future - Tool Proficiency Selection
		text.setEditable(false);

		updateProperty(PropertyListener.BACKGROUND);
	}

	@Override
	public void updateProperty(String prop)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (bg.getName().equals("Custom"))
			{
				text.setVisible(true);
				text.setText("1 Tool Proficiency");
				label.setText("Tool Proficiencies");
			}
			else
			{
				text.setVisible(false);
				label.setText("Tool Proficiencies: " + String.join(", ", bg.getToolProfs()));
			}
		}
	}
}
