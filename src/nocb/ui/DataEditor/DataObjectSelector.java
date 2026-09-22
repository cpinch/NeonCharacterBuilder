package nocb.ui.DataEditor;

import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import nocb.ui.UILib;

public class DataObjectSelector extends JPanel
{
	private static final long serialVersionUID = 4159386163855374790L;

	private final JTextPane instructions = UILib.getTextDisplay();
	private final DataBgSelector bgSel = new DataBgSelector();
	private final DataClsSelector clsSel = new DataClsSelector();
	private final DataFtSelector ftSel = new DataFtSelector();
	private final DataHwSelector hwSel = new DataHwSelector();
	private final DataLgSelector lgSel = new DataLgSelector();
	private final DataSelSelector selSel = new DataSelSelector();
	private final DataSpeSelector speSel = new DataSpeSelector();
	private final DataSlSelector slSel = new DataSlSelector();
	private final DataSpSelector spSel = new DataSpSelector();
	private final DataSubClsSelector subSel = new DataSubClsSelector();

	CardLayout layout = new CardLayout();

	public DataObjectSelector()
	{
		setLayout(layout);

		instructions.setText(
				"<html>Welcome to the Data Editor. This tool is designed to facility entering the data from the Neon Odyssey books in order to get the most value out of the character builder.<br><br>To the left you will find the various types of objects that can be created/updated. Once you select one you will see a list of all objects of that type that exist if you wish to update them and a New button to add more.<br><br>See the DATA EDITOR README doc for specifics on how each object's data is configured.<br><br>Don't forget to Save your changes with the button at the bottom. Any changes you make will persist for the current session but they will only be retained for the future if you save them to a custom nlib file.");
		instructions.setForeground(Color.black);

		add(instructions, "Instruction");
		add(bgSel, DataTypeSelector.bgStr);
		add(clsSel, DataTypeSelector.clsStr);
		add(ftSel, DataTypeSelector.ftStr);
		add(hwSel, DataTypeSelector.hwStr);
		add(lgSel, DataTypeSelector.lgStr);
		add(selSel, DataTypeSelector.selStr);
		add(speSel, DataTypeSelector.speStr);
		add(slSel, DataTypeSelector.slStr);
		add(spSel, DataTypeSelector.spStr);
		add(subSel, DataTypeSelector.subStr);
	}

	public void setType(String type)
	{
		layout.show(this, type);
	}
}
