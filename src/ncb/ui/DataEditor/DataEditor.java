package ncb.ui.DataEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ncb.io.JsonDataLoader;

public class DataEditor extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -9143003430483273009L;

	private final JButton save = new JButton("Save"), saveAndReturn = new JButton("Save and Return");

	public DataEditor()
	{
		setTitle("Data Editor");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new DataTypeSelector(), BorderLayout.CENTER);
		save.addActionListener(this);
		saveAndReturn.addActionListener(this);
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
		buttonPanel.add(save);
		buttonPanel.add(saveAndReturn);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(save))
		{
			JsonDataLoader.saveAllCustomDataFiles();
		}
		else if (e.getSource().equals(saveAndReturn))
		{
			JsonDataLoader.saveAllCustomDataFiles();
			dispose();
		}
	}
}
