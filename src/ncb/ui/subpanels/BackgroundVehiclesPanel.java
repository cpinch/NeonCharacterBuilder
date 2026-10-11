package ncb.ui.subpanels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.data.loadables.Background;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;

public class BackgroundVehiclesPanel extends JPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JTextField text;
	private final JLabel label;

	public BackgroundVehiclesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 0));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);

		label = UILib.addLabel(this, "Vehicle Proficiencies: ");
		label.putClientProperty(FlatClientProperties.STYLE, "font: bold");

		text = UILib.getTextField();
		add(text);
		// TODO Future - Vehicle Proficiency Selection
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
				text.setText("2 Vehicle Proficiencies");
				label.setText("Vehicle Proficiencies: ");
			}
			else
			{
				text.setVisible(false);
				label.setText("Vehicle Proficiencies: " + String.join(", ", bg.getVehicleProfs()));
			}
		}
	}
}
