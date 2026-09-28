package ncb.data.interfaces;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public interface AlertsChanges
{
	public abstract PropertyChangeSupport getPCS();

	default void addPropertyChangeListener(PropertyChangeListener l)
	{
		getPCS().addPropertyChangeListener(l);
	}

	default void removePropertyChangeListener(PropertyChangeListener l)
	{
		getPCS().removePropertyChangeListener(l);
	}
}
