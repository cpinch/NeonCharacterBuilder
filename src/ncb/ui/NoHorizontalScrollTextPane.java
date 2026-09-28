package ncb.ui;

import javax.swing.JTextPane;

public class NoHorizontalScrollTextPane extends JTextPane implements NoHorizontalScroll
{
	private static final long serialVersionUID = 8706508971448212456L;

	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return true;
	}
}
