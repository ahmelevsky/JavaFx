package klt.ui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import klt.Main;

public class SelectionHandler {
	
	public static Main app;
	private EventHandler<MouseEvent> mousePressedEventHandler;
	
	public SelectionHandler(final Parent root) {
		this.mousePressedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				SelectionHandler.this.doOnMousePressed(root, event);
				event.consume();
			}
		};
	}
	
	public EventHandler<MouseEvent> getMousePressedEventHandler() {
		return mousePressedEventHandler;
	}
	
	private void doOnMousePressed(Parent root, MouseEvent event) {
		Node target = (Node) event.getTarget();
		//if(target.equals(root))
			//clipboard.unselectAll();
		if(root.getChildrenUnmodifiable().contains(target) && target instanceof SelectableNode) {
			SelectableBorderPane selectableTarget = (SelectableBorderPane) target;
			if(app.mainController.getSelectedItems().contains(selectableTarget))
				app.mainController.select(selectableTarget, false);
			else 
				app.mainController.select(selectableTarget, true);
		}
		app.mainController.gotoSearchTab();
	}
	
		
}