package klt.ui;

public interface SelectableNode {
	public boolean requestSelection(boolean select);
	
	public void notifySelection(boolean select);
}