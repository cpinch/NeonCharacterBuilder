package nocb.ui.DataEditor;

import java.awt.CardLayout;

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

	CardLayout layout = new CardLayout();

	public DataObjectSelector()
	{
		setLayout(layout);

		instructions.setText("Instructions");

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
	}

	public void setType(String type)
	{
		layout.show(this, type);
	}
}
