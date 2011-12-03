import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author skritland
 * 
 */
public class mammografia {

	final static String lokalizacja = "./imdb/"; // folder z plikami

	private String User;
	private Worker WUser;
	private Ontology Onto;
	protected Shell shell;
	private Label ClblUytkownika;
	private Table table;
	TabFolder tabFolder;
	private Badania WyswBadanie; // wyświetlane zdjęcia pacjenta
	private ZdjPodglad podgladZdjec;
	Button btnPodglad;
	public Combo admins;
	private Table lista_badan;
	private Pacjent wyswPacjent;

	public mammografia() {
		Onto = new Ontology();
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			mammografia window = new mammografia();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		// initTasks();
		selectUser();
		if (User == null) {
			shell.dispose();
			return;
		}
		startInUserMode(); // uruchamia program dla odpowiedniego u¿ytkownika

		shell.open();
		shell.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void startInUserMode() {
		// tworzenie tabeli pacjentów ***************************************
		Composite compPacjenci = new Composite(shell, SWT.NONE);
		compPacjenci.setLocation(0, 40);
		compPacjenci.setSize(780, 500);



		// *******************************pacjenci*****************************

		Group grpPacjenci = new Group(compPacjenci, SWT.NONE);
		grpPacjenci.setText("Pacjenci");
		grpPacjenci.setBounds(10, 0, 340, 440);

		table = new Table(grpPacjenci, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLocation(5, 20);
		table.setSize(340, 312);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pacjent pac = Onto.getPatientByPESEL(table.getSelection()[0]
						.getText(1));
				wyswPacjent = pac;
				fillImagesTable(Onto.getImagesOfPatient(wyswPacjent));
				setImagesPreview(null);
				WyswBadanie = null;

			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tcNazwa = new TableColumn(table, SWT.NONE);
		tcNazwa.setWidth(100);
		tcNazwa.setText("Imię i nazwisko");
		TableColumn tcPESEL = new TableColumn(table, SWT.NONE);
		tcPESEL.setWidth(78);
		tcPESEL.setText("PESEL");

		TableColumn tcStan = new TableColumn(table, SWT.NONE);
		tcStan.setWidth(56);
		tcStan.setText("Stan");

		TableColumn tcOstZdj = new TableColumn(table, SWT.NONE);
		tcOstZdj.setWidth(100);
		tcOstZdj.setText("Ostatnie zdj\u0119cie");

		// przycisk dodaj pacjenta z bazy*************************
		Button dodipac = new Button(grpPacjenci, SWT.NONE);
		dodipac.setLocation(10, 340);
		dodipac.setSize(130, 30);
		dodipac.setText("Dodaj pacjenta z bazy");
		if (!WUser.isDoctor)
			dodipac.setEnabled(false);
		dodipac.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DodIstPac okno = new DodIstPac(shell);
				okno.open(Onto, User);
				fillPatientsTable();
				wyswPacjent = null;
			}
		});

		// przycisk dodaj pacjenta do bazy************************
		Button dodnpac = new Button(grpPacjenci, SWT.NONE);
		dodnpac.setLocation(200, 340);
		dodnpac.setSize(130, 30);
		dodnpac.setText("Dodaj nowego pacjenta");
		dodnpac.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new DodNowPac(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
						Onto, User).open();
				fillPatientsTable();
				wyswPacjent = null;
			}
		});

		Button btnUsuPacjenta = new Button(grpPacjenci, SWT.NONE);
		btnUsuPacjenta.setLocation(200, 390);
		btnUsuPacjenta.setSize(130, 30);
		btnUsuPacjenta.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pacjent pac = Onto.getPatientByPESEL(table.getSelection()[0]
						.getText(1));
				Onto.removePatient(pac);
				wyswPacjent = null;
				fillPatientsTable();
				fillImagesTable(null);
			}
		});
		btnUsuPacjenta.setText("Usuń pacjenta");

		Button btnZrezygnujZPacjenta = new Button(grpPacjenci, SWT.NONE);
		btnZrezygnujZPacjenta.setLocation(10, 390);
		btnZrezygnujZPacjenta.setSize(130, 30);
		if (!WUser.isDoctor)
			btnZrezygnujZPacjenta.setEnabled(false);
		btnZrezygnujZPacjenta.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pacjent pac = Onto.getPatientByPESEL(table.getSelection()[0]
						.getText(1));
				Onto.removeDoctorFromPatient(WUser, pac);
				fillPatientsTable();
				fillImagesTable(null);
				wyswPacjent = null;

			}
		});
		btnZrezygnujZPacjenta.setText("Zrezygnuj z pacjenta");

		fillPatientsTable();

		// *********************badania********************************
		Group badania = new Group(compPacjenci, SWT.NONE);
		badania.setText("Badania");
		badania.setBounds(360, 0, 200, 440);

		lista_badan = new Table(badania, SWT.BORDER | SWT.FULL_SELECTION);
		lista_badan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WyswBadanie = (Badania) lista_badan.getSelection()[0].getData();
				setImagesPreview(new ZdjPodglad(WyswBadanie.zdjecia));
			}
		});
		lista_badan.setBounds(5, 20, 190, 350);
		lista_badan.setHeaderVisible(true);
		lista_badan.setLinesVisible(true);

		TableColumn tblclmnDataWykonania = new TableColumn(lista_badan,
				SWT.NONE);
		tblclmnDataWykonania.setWidth(90);
		tblclmnDataWykonania.setText("Data wykonania");

		TableColumn tblclmnOcena = new TableColumn(lista_badan, SWT.NONE);
		tblclmnOcena.setWidth(94);
		tblclmnOcena.setText("Ocena");

		Button btnDodajZdjcie_1 = new Button(badania, SWT.NONE);
		btnDodajZdjcie_1.setBounds(10, 390, 76, 23);
		btnDodajZdjcie_1.setText("Dodaj badanie");
		btnDodajZdjcie_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (wyswPacjent == null)
					return;
				DodNowBad okno = new DodNowBad(shell, SWT.DIALOG_TRIM
						| SWT.PRIMARY_MODAL);
				Badania bad = okno.open(wyswPacjent);
				if (bad == null)
					return;
				Onto.addNewExamination(bad);
				WyswBadanie = bad;
				Pacjent pac = Onto.getPatientByPESEL(table.getSelection()[0]
						.getText(1));
				fillImagesTable(Onto.getImagesOfPatient(pac));
				setImagesPreview(new ZdjPodglad(WyswBadanie.zdjecia));
			}
		});

		Button btnUsuZdjcie = new Button(badania, SWT.NONE);
		btnUsuZdjcie.setBounds(116, 390, 76, 23);
		btnUsuZdjcie.setText("Usuń badanie");
		btnUsuZdjcie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (WyswBadanie == null)
					return;
				Onto.removeExamination(WyswBadanie);
				WyswBadanie = null;
				Pacjent pac = Onto.getPatientByPESEL(table.getSelection()[0]
						.getText(1));
				fillImagesTable(Onto.getImagesOfPatient(pac));
				setImagesPreview(null);
			}
		});
		// ********************************************podgląd*************************************************************
		Group grpPodgld = new Group(compPacjenci, SWT.NONE);
		grpPodgld.setText("Podgląd");
		grpPodgld.setBounds(570, 0, 200, 440);

		btnPodglad = new Button(grpPodgld, SWT.FLAT);
		btnPodglad.setBounds(10, 47, 180, 180);
		btnPodglad.setText("Podglad");
		btnPodglad.setAlignment(SWT.CENTER);

		Button btnPrev = new Button(grpPodgld, SWT.ARROW | SWT.LEFT);
		btnPrev.setBounds(10, 233, 40, 23);
		btnPrev.setText("Previous");
		btnPrev.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (podgladZdjec == null)
					return;
				podgladZdjec.previous();
				setImagesPreview(podgladZdjec);
			}
		});

		Button btnNext = new Button(grpPodgld, SWT.ARROW | SWT.RIGHT);
		btnNext.setBounds(149, 233, 40, 23);
		btnNext.setText("Next");
		btnNext.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (podgladZdjec == null)
					return;
				podgladZdjec.next();
				setImagesPreview(podgladZdjec);
			}
		});

	}

	/**
	 * Wybiera użytkownika programu
	 */
	private void selectUser() {
		Display display = Display.getDefault();
		final Shell sh = new Shell(shell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		sh.setLayout(new FillLayout(SWT.VERTICAL));
		Label lblUytkownika = new Label(sh, SWT.NONE);
		lblUytkownika.setAlignment(SWT.CENTER);
		lblUytkownika.setText("Użytkownik: ");
		admins = new Combo(sh, SWT.READ_ONLY);
		new LoadPeople(admins).run();
		admins.select(0);

		Button ok = new Button(sh, SWT.NONE);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setUser(admins.getText());
				WUser = Onto.getWorkerByName(User);
				sh.dispose();
			}
		});
		sh.pack();
		sh.setLocation(
				display.getBounds().width / 2 - sh.getBounds().width / 2,
				display.getBounds().height / 3 - sh.getBounds().height / 2);
		sh.open();
		while (!sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}

		}
		if (User == null)
			return;
		ClblUytkownika.setText("Pracuje: " + User + " "
				+ ((WUser.isDoctor) ? ("(LEKARZ)") : ("(LABORANT)")));
		ClblUytkownika.pack();
	}

	private void setUser(String us) {
		User = us;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(800, 540);
		shell.setText("Mammografia");
		shell.setLayout(null);		
		
		ClblUytkownika = new Label(shell, SWT.NONE);
		ClblUytkownika.setEnabled(true);
		ClblUytkownika.setAlignment(SWT.CENTER);
		ClblUytkownika.setBounds(10, 14, 102, 14);
		ClblUytkownika.setText("Pracuje: ");

	}

	private void fillPatientsTable() {
		table.removeAll();
		java.util.List<Pacjent> pacli;
		if (WUser.isDoctor == true)
			pacli = Onto.getPatients(User, true);
		else
			pacli = Onto.getPatients(null, true);
		java.util.ListIterator<Pacjent> iter = pacli.listIterator();

		while (iter.hasNext()) {
			Pacjent pac = iter.next();
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem
					.setText(new String[] {
							pac.nazwa,
							pac.PESEL,
							(pac.stan == null) ? "Nieznany" : pac.stan,
							(pac.ostatbad == null) ? "Nigdy" : pac.ostatbad
									.toString() });
		}

	}

	private void fillImagesTable(java.util.List<Badania> zdjecia) {
		lista_badan.removeAll();
		if (zdjecia == null)
			return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.ListIterator<Badania> iter = zdjecia.listIterator();

		while (iter.hasNext()) {
			Badania zdje = iter.next();
			TableItem tableItem = new TableItem(lista_badan, SWT.NONE);

			tableItem.setText(new String[] { sdf.format(zdje.dataBadania),
					(zdje.ocena == null) ? "Brak diagnozy" : zdje.ocena, });
			tableItem.setData(zdje);
		}

	}

	private void setImagesPreview(ZdjPodglad pz) {
		podgladZdjec = pz;
		if (pz == null) {
			btnPodglad.setImage(null);
		} else {
			btnPodglad.setImage(pz.getImage());
		}
	}
}