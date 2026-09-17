package nocb.ui.DataEditor;

import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import nocb.data.Language;
import nocb.ui.UILib;

public class LanguageEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Language lang;

	private final JTextField spokenAt = new JTextField(20);

	public LanguageEditPanel()
	{
		super();

		spokenAt.addFocusListener(UILib.createFocusListener(() -> updateSpokenAt()));
		spokenAt.setBorder(BorderFactory.createEtchedBorder());
		UILib.addLabeledComponent(this, "Spoken Locations: ", spokenAt, c);

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		lang = Language.getById(id);

		nameField.setText(lang.getName());
		spokenAt.setText(String.join(", ", lang.getSpokenAt()));

		setVisible(true);
	}

	@Override
	protected void updateName()
	{
		lang.setName(nameField.getText());
	}

	private void updateSpokenAt()
	{
		lang.setSpokeAt(Arrays.asList(spokenAt.getText().split(",")).stream().map(t -> t.trim()).toList());
		lang.setCustom(true);
	}
}
