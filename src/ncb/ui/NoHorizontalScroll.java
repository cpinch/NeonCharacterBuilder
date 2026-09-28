package ncb.ui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Scrollable;

public interface NoHorizontalScroll extends Scrollable
{
	public abstract Dimension getPreferredSize();

	@Override
	default Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}

	@Override
	default int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 16;
	}

	@Override
	default int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
	{
		return 32;
	}

	@Override
	default boolean getScrollableTracksViewportWidth()
	{
		return true;
	}

	@Override
	default boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
}
