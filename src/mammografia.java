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
	private Ontology Onto;
	protected Shell shell;
	private Label ClblUytkownika;
	private Text text;
	private Table table;
	private Text text_1;
	TabFolder tabFolder;

	public Combo admins;

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
		tabFolder.setSelection(2);
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

		java.util.List<Pacjent> pacli = Onto.getPatients(User);
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
	}

	/**
	 *  Wybiera u¿ytkownika programu
	 */
	private void selectUser() {
		Display display = Display.getDefault();
		final Shell sh = new Shell(shell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		sh.setLayout(new FillLayout(SWT.VERTICAL));
		Label lblUytkownika = new Label(sh, SWT.NONE);
		lblUytkownika.setAlignment(SWT.CENTER);
		lblUytkownika.setText("U¿ytkownik: ");
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
		shell.setSize(600, 440);
		shell.setText("Mammografia");
		shell.setLayout(null);

		// admins = new Combo(shell, SWT.NONE);
		// admins.setBounds(118, 10, 472, 22);

		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 38, 580, 370);

		TabItem newPhoto = new TabItem(tabFolder, SWT.NONE);
		newPhoto.setText("Dodawanie zdj\u0119cia");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		newPhoto.setControl(composite);

		Label lblPodajSciekePliku = new Label(composite, SWT.NONE);
		lblPodajSciekePliku.setAlignment(SWT.RIGHT);
		lblPodajSciekePliku.setBounds(22, 34, 132, 21);
		lblPodajSciekePliku.setText("Podaj scie\u017Cke pliku");

		Label lblWybierzPacjenta = new Label(composite, SWT.NONE);
		lblWybierzPacjenta.setAlignment(SWT.RIGHT);
		lblWybierzPacjenta.setBounds(32, 60, 122, 14);
		lblWybierzPacjenta.setText("Wybierz Pacjenta");

		Label lblDataWykonania = new Label(composite, SWT.NONE);
		lblDataWykonania.setText("Data wykonania");
		lblDataWykonania.setAlignment(SWT.RIGHT);
		lblDataWykonania.setBounds(32, 86, 122, 14);

		DateTime dateTime = new DateTime(composite, SWT.BORDER);
		dateTime.setBounds(169, 86, 92, 27);

		DateTime dateTime_1 = new DateTime(composite, SWT.BORDER | SWT.TIME
				| SWT.SHORT);
		dateTime_1.setBounds(267, 86, 66, 27);

		Combo combo_1 = new Combo(composite, SWT.NONE);
		combo_1.setBounds(169, 58, 364, 22);

		Button btnDodajZdjcie = new Button(composite, SWT.NONE);
		btnDodajZdjcie.setBounds(167, 131, 113, 28);
		btnDodajZdjcie.setText("Dodaj zdj\u0119cie");

		text = new Text(composite, SWT.BORDER);
		text.setBounds(169, 31, 264, 19);
		text.setEnabled(false);

		final Label photo = new Label(composite, SWT.BORDER);
		photo.setAlignment(SWT.CENTER);

		Button btnNewButton = new Button(composite, SWT.NONE);
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
		btnNewButton.setBounds(439, 27, 94, 28);
		btnNewButton.setText("wybierz...");

		photo.setBounds(300, 131, 233, 177);
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

		TabItem browsePatients = new TabItem(tabFolder, SWT.NONE);
		browsePatients.setText("Przegl\u0105danie pacjent\u00F3w");

		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		browsePatients.setControl(composite_2);

		table = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 540, 312);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		ClblUytkownika = new Label(shell, SWT.NONE);
		ClblUytkownika.setEnabled(true);
		ClblUytkownika.setAlignment(SWT.CENTER);
		ClblUytkownika.setBounds(10, 14, 102, 14);
		ClblUytkownika.setText("Pracuje: ");

	}

}
