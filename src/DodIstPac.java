import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


class DodIstPac extends Dialog {

	private Table table;
	private Ontology ontmodel;
	private String user;
	
	public DodIstPac(Shell parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public DodIstPac(Shell parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	public void open(Ontology ontmodell, String userr) {
		ontmodel = ontmodell;
		user = userr;
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 340, 312);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tcNazwa = new TableColumn(table, SWT.NONE);
		tcNazwa.setWidth(100);
		tcNazwa.setText("Imiê i nazwisko");
		TableColumn tcPESEL = new TableColumn(table, SWT.NONE);
		tcPESEL.setWidth(70);
		tcPESEL.setText("PESEL");

		TableColumn tcStan = new TableColumn(table, SWT.NONE);
		tcStan.setWidth(70);
		tcStan.setText("Stan");

		TableColumn tcOstZdj = new TableColumn(table, SWT.NONE);
		tcOstZdj.setWidth(100);
		tcOstZdj.setText("Ostatnie zdj\u0119cie");
		
		java.util.List<Pacjent> pacli = ontmodel.getPatients(user, false);
		java.util.ListIterator<Pacjent> iter = pacli.listIterator();

		while (iter.hasNext()) {
			Pacjent pac = iter.next();
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem
					.setText(new String[] {
							pac.nazwa,
							pac.PESEL,
							(pac.stan == null) ? "Nieznany" : "pac.stan",
							(pac.ostatbad == null) ? "Nigdy" : pac.ostatbad
									.toString() });
		}

		tcPESEL.pack();
		tcStan.pack();
		// przycisk dodaj pacjenta do lekarza*************************
		Button dodipac = new Button(shell, SWT.NONE);
		dodipac.setText("Dodaj pacjenta");
		dodipac.setBounds(15, 340, 130, 30);
		dodipac.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO gdy nic nie zaznaczy
				Pacjent pac = ontmodel.getPatientByPESEL(table.getSelection()[0].getText(1));
				Worker doktor = ontmodel.getWorkerByName(user);
				ontmodel.addDoctorToPatient(doktor.URI, pac.URI);
				shell.dispose();
			}
		});
		Button anuluj = new Button(shell, SWT.NONE);
		anuluj.setText("Anuluj");
		anuluj.setBounds(150, 340, 130, 30);
		anuluj.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

}
