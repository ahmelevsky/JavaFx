package my;
import org.eclipse.swt.widgets.*;

public class Test {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Hello world!");
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				 display.sleep();
		display.dispose();
	}

}
