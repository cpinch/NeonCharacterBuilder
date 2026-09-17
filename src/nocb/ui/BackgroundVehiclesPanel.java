package nocb.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nocb.data.Background;
import nocb.main.CharacterSheet;

public class BackgroundVehiclesPanel extends JPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JTextField text = new JTextField();
	private final JLabel label = UILib.getLabel("");

	public BackgroundVehiclesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 0));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);

		UILib.addLabel(this, "Vehicle Proficiencies:  ");

		add(label);
		text.setEditable(false); // I don't want to have to deal with creating a whole selectable for vehicles
									// just
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
				text.setText("2 Vehicle Proficiencies");
				label.setVisible(false);
			}
			else
			{
				text.setVisible(false);
				label.setVisible(true);
				label.setText(bg.getVehicleProfs());
			}
		}
	}
}
