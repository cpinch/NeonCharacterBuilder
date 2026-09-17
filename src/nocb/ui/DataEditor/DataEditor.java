package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import nocb.io.JsonDataLoader;

public class DataEditor extends JFrame implements ActionListener
{
	private static final long serialVersionUID = -9143003430483273009L;

	private static JButton save = new JButton("Save and Return");

	public DataEditor()
	{
		setTitle("Data Editor");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new DataTypeSelector(), BorderLayout.CENTER);
		save.addActionListener(this);
		getContentPane().add(save, BorderLayout.SOUTH);

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
			dispose();
		}
	}
}
