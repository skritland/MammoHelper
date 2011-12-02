import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author skritland
 * 
 */
public class mammografia {

	private String User;
	private Worker WUser;
	private Ontology Onto;
	protected Shell shell;
	private Label ClblUytkownika;
	private Text text;
	private Table table;
	private Text text_1;
	TabFolder tabFolder;
	private java.util.List<Zdjecia> zdjeciaWyswZdjecia; //wyświetlane zdjęcia pacjenta

	public Combo admins;
	private Table lista_zdjec;

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
		WUser = Onto.getWorkerByName(User);
		startInUserMode(); // uruchamia program dla odpowiedniego u¿ytkownika

		if (User == null)
			shell.dispose();
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
		TabItem browsePatients = new TabItem(tabFolder, SWT.NONE);
		browsePatients.setText("Przegl\u0105danie pacjent\u00F3w");

		Composite compPacjenci = new Composite(tabFolder, SWT.NONE);
		browsePatients.setControl(compPacjenci);
		tabFolder.setSelection(2);

		table = new Table(compPacjenci, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pacjent pac = Onto.getPatientByPESEL(table.getSelection()[0]
						.getText(1));
				fillImagesTable(Onto.getImagesOfPatient(pac));

			}
		});
		table.setBounds(10, 10, 340, 312);
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

		fillPatientsTable();

		// przycisk dodaj pacjenta z bazy*************************
		Button dodipac = new Button(compPacjenci, SWT.NONE);
		dodipac.setText("Dodaj pacjenta z bazy");
		dodipac.setBounds(15, 340, 130, 30);
		dodipac.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DodIstPac okno = new DodIstPac(shell);
				okno.open(Onto, User);
				fillPatientsTable();
			}
		});
		// przycisk dodaj pacjenta do bazy************************
		Button dodnpac = new Button(compPacjenci, SWT.NONE);
		dodnpac.setText("Dodaj nowego pacjenta");
		dodnpac.setBounds(215, 340, 130, 30);

		Composite zdjecia = new Composite(compPacjenci, SWT.NONE);
		zdjecia.setBounds(363, 10, 194, 424);

		lista_zdjec = new Table(zdjecia, SWT.BORDER | SWT.FULL_SELECTION);
		lista_zdjec.setBounds(0, 0, 190, 312);
		lista_zdjec.setHeaderVisible(true);
		lista_zdjec.setLinesVisible(true);

		TableColumn tblclmnDataWykonania = new TableColumn(lista_zdjec,
				SWT.NONE);
		tblclmnDataWykonania.setWidth(90);
		tblclmnDataWykonania.setText("Data wykonania");

		TableColumn tblclmnOcena = new TableColumn(lista_zdjec, SWT.NONE);
		tblclmnOcena.setWidth(94);
		tblclmnOcena.setText("Ocena");

		Button btnDodajZdjcie_1 = new Button(zdjecia, SWT.NONE);
		btnDodajZdjcie_1.setBounds(0, 334, 76, 23);
		btnDodajZdjcie_1.setText("Dodaj zdjęcie");

		Button btnUsuZdjcie = new Button(zdjecia, SWT.NONE);
		btnUsuZdjcie.setBounds(136, 334, 68, 23);
		btnUsuZdjcie.setText("Usuń zdjęcie");

		Button btnUsuPacjenta = new Button(compPacjenci, SWT.NONE);
		btnUsuPacjenta.setBounds(215, 388, 130, 30);
		btnUsuPacjenta.setText("Usuń pacjenta");

		Button btnZrezygnujZPacjenta = new Button(compPacjenci, SWT.NONE);
		btnZrezygnujZPacjenta.setBounds(10, 388, 135, 30);
		btnZrezygnujZPacjenta.setText("Zrezygnuj z pacjenta");
		dodnpac.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new DodNowPac(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,
						Onto, User).open();
				fillPatientsTable();
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

				sh.dispose();
			}
		});
		sh.pack();
		sh.open();
		while (!sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}

		}
		ClblUytkownika.setText("Pracuje: " + User);
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

		// admins = new Combo(shell, SWT.NONE);
		// admins.setBounds(118, 10, 472, 22);

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 38, 780, 470);

		TabItem newPhoto = new TabItem(tabFolder, SWT.NONE);
		newPhoto.setText("Dodawanie zdj\u0119cia");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		newPhoto.setControl(composite);
		composite.setLayout(null);

		Label lblPodajSciekePliku = new Label(composite, SWT.NONE);
		lblPodajSciekePliku.setBounds(22, 34, 132, 21);
		lblPodajSciekePliku.setAlignment(SWT.RIGHT);
		lblPodajSciekePliku.setText("Podaj scie\u017Cke pliku");

		Label lblWybierzPacjenta = new Label(composite, SWT.NONE);
		lblWybierzPacjenta.setBounds(32, 60, 122, 14);
		lblWybierzPacjenta.setAlignment(SWT.RIGHT);
		lblWybierzPacjenta.setText("Wybierz Pacjenta");

		Label lblDataWykonania = new Label(composite, SWT.NONE);
		lblDataWykonania.setBounds(32, 86, 122, 14);
		lblDataWykonania.setText("Data wykonania");
		lblDataWykonania.setAlignment(SWT.RIGHT);

		DateTime dateTime = new DateTime(composite, SWT.BORDER);
		dateTime.setBounds(169, 86, 92, 27);

		DateTime dateTime_1 = new DateTime(composite, SWT.BORDER | SWT.TIME
				| SWT.SHORT);
		dateTime_1.setBounds(267, 86, 66, 27);

		Combo combo_1 = new Combo(composite, SWT.NONE);
		combo_1.setBounds(169, 58, 364, 21);

		Button btnDodajZdjcie = new Button(composite, SWT.NONE);
		btnDodajZdjcie.setBounds(167, 131, 113, 28);
		btnDodajZdjcie.setText("Dodaj zdj\u0119cie");

		text = new Text(composite, SWT.BORDER);
		text.setBounds(169, 31, 264, 19);
		text.setEnabled(false);

		final Label photo = new Label(composite, SWT.BORDER);
		photo.setBounds(300, 131, 233, 177);
		photo.setAlignment(SWT.CENTER);

		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setBounds(439, 27, 94, 28);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);

				String[] filterNames = new String[] { "Image Files" };
				String[] filterExtensions = new String[] { "*.gif;*.png;*.xpm;*.jpg;*.jpeg;*.tiff" };
				String filterPath = "/";
				String platform = SWT.getPlatform();

				if (platform.equals("win32") || platform.equals("wpf")) {
					filterPath = "c:\\";
				}

				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(filterPath);

				String path = dialog.open();
				text.setText(path);

				ImageData ideaImage = new ImageData(path);
				Image im = new Image(Display.getDefault(), ideaImage);

				int width = im.getBounds().width;
				int height = im.getBounds().height;

				double ratio = (double) 266 / (double) width;
				height = (int) (height * ratio);
				width = 266;

				Image scaled = new Image(Display.getDefault(), im
						.getImageData().scaledTo(width, height));

				photo.setImage(scaled);
			}
		});
		btnNewButton.setText("wybierz...");
		photo.setText("Podgl\u0105d:");

		TabItem describePhoto = new TabItem(tabFolder, SWT.NONE);
		describePhoto.setText("Analiza zdj\u0119cia");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		describePhoto.setControl(composite_1);

		Label lblWybierzPacjenta_1 = new Label(composite_1, SWT.NONE);
		lblWybierzPacjenta_1.setText("Wybierz pacjenta");
		lblWybierzPacjenta_1.setAlignment(SWT.RIGHT);
		lblWybierzPacjenta_1.setBounds(10, 14, 115, 21);

		Label lblWybierzZdjcie = new Label(composite_1, SWT.NONE);
		lblWybierzZdjcie.setText("Wybierz zdj\u0119cie");
		lblWybierzZdjcie.setAlignment(SWT.RIGHT);
		lblWybierzZdjcie.setBounds(22, 42, 103, 24);

		Button btnZapiszAnalize = new Button(composite_1, SWT.NONE);
		btnZapiszAnalize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnZapiszAnalize.setText("Zapisz analize");
		btnZapiszAnalize.setBounds(133, 294, 165, 28);

		Label label_3 = new Label(composite_1, SWT.BORDER);
		label_3.setText("Podgl\u0105d:");
		label_3.setAlignment(SWT.CENTER);
		label_3.setBounds(315, 10, 235, 301);

		Combo combo_2 = new Combo(composite_1, SWT.NONE);
		combo_2.setBounds(133, 10, 165, 22);

		Combo combo_3 = new Combo(composite_1, SWT.NONE);
		combo_3.setBounds(133, 38, 165, 22);

		List list = new List(composite_1, SWT.BORDER);
		list.setBounds(10, 87, 288, 123);

		Label lblWybierzSchorzenia = new Label(composite_1, SWT.NONE);
		lblWybierzSchorzenia.setText("Wybierz schorzenia");
		lblWybierzSchorzenia.setAlignment(SWT.RIGHT);
		lblWybierzSchorzenia.setBounds(160, 66, 134, 24);

		Label lblKomentarz = new Label(composite_1, SWT.NONE);
		lblKomentarz.setText("Komentarz");
		lblKomentarz.setAlignment(SWT.RIGHT);
		lblKomentarz.setBounds(160, 216, 134, 24);

		text_1 = new Text(composite_1, SWT.BORDER | SWT.MULTI);
		text_1.setBounds(10, 233, 200, 55);

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

	private void fillImagesTable(java.util.List<Zdjecia> zdjecia) {
		WyswZdjecia = zdjecia;
		lista_zdjec.removeAll();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.ListIterator<Zdjecia> iter = zdjecia.listIterator();

		while (iter.hasNext()) {
			Zdjecia zdje = iter.next();
			TableItem tableItem = new TableItem(lista_zdjec, SWT.NONE);

			tableItem.setText(new String[] { sdf.format(zdje.dataBadania),
					(zdje.ocena == null) ? "Nie diagnozowano" : zdje.ocena, });
		}

	}
}