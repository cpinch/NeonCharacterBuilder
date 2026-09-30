package ncb.ui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UndoTextArea extends JTextArea
{
	private static final long serialVersionUID = 7273605470048139266L;
	private static final KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
	private static final KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);

	public UndoTextArea(int rows, int cols)
	{
		super(rows, cols);

		UndoManager undoManager = new UndoManager();

		getDocument().addUndoableEditListener(new UndoableEditListener()
		{
			@Override
			public void undoableEditHappened(UndoableEditEvent e)
			{
				undoManager.addEdit(e.getEdit());
			}
		});

		getInputMap().put(undoKeyStroke, "undoKeyStroke");
		getActionMap().put("undoKeyStroke", new AbstractAction()
		{
			private static final long serialVersionUID = -4769667669600547871L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					undoManager.undo();
				}
				catch (CannotUndoException cue)
				{
				}
			}
		});

		getInputMap().put(redoKeyStroke, "redoKeyStroke");
		getActionMap().put("redoKeyStroke", new AbstractAction()
		{
			private static final long serialVersionUID = -8283380189148649648L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					undoManager.redo();
				}
				catch (CannotRedoException cre)
				{
				}
			}
		});
	}
}
