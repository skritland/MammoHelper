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

public class SelectDiagnosis extends Dialog {

	protected OntoDrzewko root;
	protected List<Choroby> result;
	protected Shell shell;
	protected Tree tree;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public SelectDiagnosis(Shell parent, int style, OntoDrzewko ro,
			List<Choroby> res) {
		super(parent, style);
		setText("Wyb�r diagnozy");
		result = res;
		root = ro;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public List<Choroby> open() {
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
				List<Choroby> res = new ArrayList<Choroby>();
				TreeItem[] table = tree.getItems();
				Choroby cho;
				for (TreeItem ti : table) {
					if (ti.getChecked()) {
						cho = new Choroby();
						cho.nazwa = ti.getText();
						cho.URI = (String) ti.getData();
						res.add(cho);
					}
					TreeItem[] tb2 = ti.getItems();
					for (TreeItem ti2 : tb2) {
						if (ti2.getChecked()) {
							cho = new Choroby();
							cho.nazwa = ti2.getText();
							cho.URI = (String) ti2.getData();
							res.add(cho);
						}
					}

				}
				// for (Choroby ccc : res) System.out.println(ccc.nazwa +
				// "URI: " + ccc.URI);
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
		Choroby cho = null;

		while (li1.hasNext()) {
			pom1 = li1.next();
			ti = new TreeItem(tree, SWT.NONE);
			cho = new Choroby();
			cho.URI = pom1.URI;
			cho.nazwa = pom1.nazwa;
			if ((result != null) && (result.contains(cho)))
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
					cho = new Choroby();
					cho.URI = pom2.URI;
					cho.nazwa = pom2.nazwa;
					if ((result != null) && (result.contains(cho)))
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
