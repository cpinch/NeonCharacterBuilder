package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.util.Arrays;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Language;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class LanguageEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Language lang;

	@Override
	protected String getItemName()
	{
		return lang.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		lang.setName(s);
	}

	public LanguageEditPanel()
	{
		super();

		UILib.addLabeledLinkedTextField(this, "Spoken Locations: ", c, Color.white, Color.black, () -> getSpokenAt(),
				(s) -> updateSpokenAt(s));

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		lang = (Language) sel;

		linkedProperties.forEach(p -> p.updateValue());

		setVisible(true);
	}

	private String getSpokenAt()
	{
		return String.join(", ", lang.getSpokenAt());
	}

	private void updateSpokenAt(String s)
	{
		lang.setSpokeAt(Arrays.asList(s.split(",")).stream().map(t -> t.trim()).toList());
	}
}
