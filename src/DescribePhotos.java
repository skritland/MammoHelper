import java.text.SimpleDateFormat;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Combo;

public class DescribePhotos extends Dialog {

	private final String[] STANYPACJENTA = new String[] { "brak", "zdrowy",
			"obserwacja", "niez³oœliwy", "z³oœliwy" };
	private final String[] OCENYBADANIA = new String[] { "brak oceny", "OK",
			"mo¿liwy niez³oœliwy", "niez³oœliwy", "mo¿liwy z³oœliwy",
			"z³oœliwy" };

	protected Shell shell;

	private SimpleDateFormat sdf;
	private String data;
	private Badanie badanie;
	private ZdjPodglad podglad;
	private Label view;
	private Label lblZdjcie;
	List liChoroby;
	List liZauw;
	Combo cmbStanPacjenta;
	Combo cmbOcenaZdjecia;
	private Ontology onto;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public DescribePhotos(Shell parent, int style, Ontology o) {
		super(parent, style);
		setText("SWT Dialog");
		onto = o;
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}

	/**
	 * Open the dialog.
	 * 
	 * @param wyswPacjent
	 * @param wyswBadanie
	 * @return the result
	 */
	public Badanie open(Badanie wyswBadanie) {
		badanie = wyswBadanie;
		data = sdf.format(badanie.dataBadania);

		podglad = new ZdjPodglad(badanie.zdjecia);

		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return badanie;
	}

	private void refresh() {
		Point size = view.getSize();
		view.setImage(podglad.getImage(size.x, size.y));
		lblZdjcie.setText("Zdj\u0119cie #" + (podglad.num() + 1) + "/"
				+ podglad.ile());
	}

	private void refreshChoroby() {
		liChoroby.removeAll();
		if (badanie.choroby != null)
			for (Choroby cho : badanie.choroby)
				liChoroby.add(cho.nazwa);

	}

	private void refreshZauw() {
		liZauw.removeAll();
		if (badanie.choroby != null)
			for (Zauwazone zau : badanie.zauwazone)
				liZauw.add(zau.nazwa);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(863, 443);
		shell.setText("Pacjent: " + badanie.pacjent.nazwa + " / Badanie: "
				+ data);
		shell.setLayout(null);

		Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 10, 248, 359);

		view = new Label(group, SWT.FLAT);
		view.setBounds(10, 10, 224, 335);
		view.setText("View");
		view.setAlignment(SWT.CENTER);

		Button btnNewButton = new Button(shell, SWT.ARROW | SWT.RIGHT);
		btnNewButton.setBounds(225, 381, 33, 28);
		btnNewButton.setText("next");

		btnNewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				podglad.next();
				refresh();
			}
		});

		Button button = new Button(shell, SWT.ARROW | SWT.LEFT);
		button.setText("prev");
		button.setBounds(10, 381, 33, 28);

		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				podglad.previous();
				refresh();
			}
		});

		lblZdjcie = new Label(shell, SWT.NONE);
		lblZdjcie.setAlignment(SWT.CENTER);
		lblZdjcie.setBounds(49, 380, 170, 27);
		lblZdjcie.setText("Zdj\u0119cie #1");

		Label lblChoroby = new Label(shell, SWT.NONE);
		lblChoroby.setBounds(264, 10, 59, 14);
		lblChoroby.setText("Choroby:");

		liChoroby = new List(shell, SWT.BORDER);
		liChoroby.setBounds(264, 30, 260, 210);

		Button btEdytujChoroby = new Button(shell, SWT.NONE);
		btEdytujChoroby.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectDiagnosis sd = new SelectDiagnosis(shell, SWT.DIALOG_TRIM
						| SWT.PRIMARY_MODAL, onto.getDiagnosisList(),
						badanie.choroby);
				badanie.choroby = sd.open();
				refreshChoroby();
			}

		});
		btEdytujChoroby.setText("Edytuj");
		btEdytujChoroby.setBounds(450, 246, 74, 28);

		liZauw = new List(shell, SWT.BORDER);
		liZauw.setBounds(538, 30, 260, 210);

		Button btnEdytujZauw = new Button(shell, SWT.NONE);
		btnEdytujZauw.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectFinding sf = new SelectFinding(shell, SWT.DIALOG_TRIM
						| SWT.PRIMARY_MODAL, onto.getFindingsList(),
						badanie.zauwazone);
				badanie.zauwazone = sf.open();
				refreshZauw();
			}
		});
		btnEdytujZauw.setText("Edytuj");
		btnEdytujZauw.setBounds(724, 246, 74, 28);

		Label lblZauw = new Label(shell, SWT.NONE);
		lblZauw.setText("Zauwa\u017Cone istotne cechy");
		lblZauw.setBounds(538, 10, 129, 14);

		Label lblStanPacjenta = new Label(shell, SWT.NONE);
		lblStanPacjenta.setText("Stan pacjenta:");
		lblStanPacjenta.setBounds(264, 367, 98, 14);

		cmbStanPacjenta = new Combo(shell, SWT.NONE);
		cmbStanPacjenta.setBounds(264, 387, 260, 21);
		for (String s : STANYPACJENTA) {
			cmbStanPacjenta.add(s);
		}
		if (badanie.pacjent.stan == null)
			cmbStanPacjenta.setText(STANYPACJENTA[0]);
		else
			cmbStanPacjenta.setText(badanie.pacjent.stan);

		Label lblOcenaZdjecia = new Label(shell, SWT.NONE);
		lblOcenaZdjecia.setBounds(264, 317, 74, 13);
		lblOcenaZdjecia.setText("Ocena zdj\u0119cia:");
		cmbOcenaZdjecia = new Combo(shell, SWT.NONE);
		cmbOcenaZdjecia.setBounds(264, 336, 260, 21);
		for (String s : OCENYBADANIA) {
			cmbOcenaZdjecia.add(s);
		}
		if (badanie.ocena == null)
			cmbOcenaZdjecia.setText(OCENYBADANIA[0]);
		else
			cmbOcenaZdjecia.setText(badanie.ocena);

		Label lblDataBadania = new Label(shell, SWT.NONE);
		lblDataBadania.setBounds(538, 336, 234, 13);
		lblDataBadania.setText("Data badania: " + data);

		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				badanie.ocena = cmbOcenaZdjecia.getText();
				badanie.pacjent.stan = cmbStanPacjenta.getText();
				shell.dispose();
			}
		});
		btnOk.setBounds(624, 387, 68, 23);
		btnOk.setText("OK");

		Button btnAnuluj = new Button(shell, SWT.NONE);
		btnAnuluj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnAnuluj.setBounds(730, 386, 68, 23);
		btnAnuluj.setText("Anuluj");

		refreshChoroby();
		refreshZauw();
		refresh();

	}
}
