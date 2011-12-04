import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;


public class SelectDiagnosis extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SelectDiagnosis(Shell parent, int style) {
		super(parent, style);
		setText("Wybór diagnozy");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(270, 485);
		shell.setText(getText());
		
		Tree tree = new Tree(shell, SWT.BORDER | SWT.CHECK | SWT.MULTI);
		tree.setBounds(10, 10, 244, 389);
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(10, 424, 68, 23);
		btnOk.setText("OK");
		
		Button btnAnuluj = new Button(shell, SWT.NONE);
		btnAnuluj.setBounds(186, 424, 68, 23);
		btnAnuluj.setText("Anuluj");
		
		for (int i=0;i<10;i++) {
			TreeItem treeItem= new TreeItem(tree, SWT.NONE);
			treeItem.setText("aaaa:" + Integer.toString(i));
			
		}

	}
}
