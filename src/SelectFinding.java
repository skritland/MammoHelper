import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SelectFinding extends Dialog {

	protected OntoDrzewko root;
	protected List<Zauwazone> result;
	protected Shell shell;
	protected Tree tree;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SelectFinding(Shell parent, int style, OntoDrzewko ro,
			List<Zauwazone> res) {
		super(parent, style);
		setText("Wybór istotnych znalezisk z badania");
		result = res;
		root = ro;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public List<Zauwazone> open() {
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

		tree = new Tree(shell, SWT.BORDER | SWT.CHECK | SWT.MULTI);
		tree.setBounds(10, 10, 244, 389);

		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Zauwazone> res = new ArrayList<Zauwazone>();
				TreeItem[] table = tree.getItems();
				Zauwazone zauw;
				for (TreeItem ti : table) {
					if (ti.getChecked()) {
						zauw = new Zauwazone();
						zauw.nazwa = ti.getText();
						zauw.URI = (String) ti.getData();
						res.add(zauw);
					}
					TreeItem[] tb2 = ti.getItems();
					for (TreeItem ti2 : tb2) {
						if (ti2.getChecked()) {
							zauw = new Zauwazone();
							zauw.nazwa = ti2.getText();
							zauw.URI = (String) ti2.getData();
							res.add(zauw);
						}
					}

				}

				result = res;
				shell.dispose();
			}
		});
		btnOk.setBounds(10, 424, 68, 23);
		btnOk.setText("OK");

		Button btnAnuluj = new Button(shell, SWT.NONE);
		btnAnuluj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnAnuluj.setBounds(186, 424, 68, 23);
		btnAnuluj.setText("Anuluj");

		ListIterator<OntoDrzewko> li1 = root.dzieci.listIterator();
		OntoDrzewko pom1, pom2;
		ListIterator<OntoDrzewko> li2;
		TreeItem ti, ti2;
		Zauwazone zauw = null;

		while (li1.hasNext()) {
			pom1 = li1.next();
			ti = new TreeItem(tree, SWT.NONE);
			zauw = new Zauwazone();
			zauw.URI = pom1.URI;
			zauw.nazwa = pom1.nazwa;
			if ((result != null) && (result.contains(zauw)))
				ti.setChecked(true);
			else
				ti.setChecked(false);
			ti.setText(pom1.nazwa);
			ti.setData(pom1.URI);
			if (pom1.dzieci != null) {
				li2 = pom1.dzieci.listIterator();
				while (li2.hasNext()) {
					pom2 = li2.next();
					ti2 = new TreeItem(ti, SWT.NONE);
					zauw = new Zauwazone();
					zauw.URI = pom2.URI;
					zauw.nazwa = pom2.nazwa;
					if ((result != null) && (result.contains(zauw)))
						ti2.setChecked(true);
					else
						ti2.setChecked(false);
					ti2.setText(pom2.nazwa);
					ti2.setData(pom2.URI);
				}
			}
		}

	}
}
