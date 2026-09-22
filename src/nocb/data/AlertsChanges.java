package nocb.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AlertsChanges
{
	protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		pcs.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		pcs.removePropertyChangeListener(l);
	}
}
