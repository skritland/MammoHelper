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

	protected Object result;
	protected Shell shell;
	

	private Pacjent pacjent;
	private Badania badanie;
	private ZdjPodglad podglad;
	private Label view;
	private Label lblZdjcie;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DescribePhotos(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @param wyswPacjent 
	 * @param wyswBadanie 
	 * @return the result
	 */
	public Object open(Pacjent wyswPacjent, Badania wyswBadanie) {
		pacjent = wyswPacjent;
		badanie = wyswBadanie;
		
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
		return result;
	}

	private void refresh() {
		Point size = view.getSize();
		view.setImage(podglad.getImage(size.x, size.y));
		lblZdjcie.setText("Zdj\u0119cie #"+(podglad.num()+1));
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(534, 441);
		shell.setText("Pacjent: " + pacjent.nazwa + " / Badanie: " + badanie.dataBadania );
		shell.setLayout(null);
		
		Group group = new Group(shell, SWT.NONE);
		group.setBounds(10, 10, 248, 359);
		
		view = new Label(group, SWT.FLAT);
		view.setBounds(10, 10, 224, 335);
		view.setText("View");
		view.setAlignment(SWT.CENTER);
		
		Point size = view.getSize();
		view.setImage(podglad.getImage(size.x, size.y));
		
		
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
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(264, 10, 59, 14);
		lblNewLabel.setText("Choroby:");
		
		List chList = new List(shell, SWT.BORDER);
		chList.setBounds(264, 30, 260, 151);
		
		Button chUsun = new Button(shell, SWT.NONE);
		chUsun.setBounds(450, 187, 74, 28);
		chUsun.setText("Usu\u0144");
		
		Button chDodaj = new Button(shell, SWT.NONE);
		chDodaj.setText("Dodaj");
		chDodaj.setBounds(375, 187, 74, 28);
		
		List komList = new List(shell, SWT.BORDER);
		komList.setBounds(264, 241, 260, 94);
		
		Button komDodaj = new Button(shell, SWT.NONE);
		komDodaj.setText("Dodaj");
		komDodaj.setBounds(375, 341, 74, 28);
		
		Label lblKomentarze = new Label(shell, SWT.NONE);
		lblKomentarze.setText("Komentarze:");
		lblKomentarze.setBounds(264, 221, 98, 14);
		
		Button komUsun = new Button(shell, SWT.NONE);
		komUsun.setText("Usu\u0144");
		komUsun.setBounds(450, 341, 74, 28);
		
		Label lblStandPacjenta = new Label(shell, SWT.NONE);
		lblStandPacjenta.setText("Stan pacjenta:");
		lblStandPacjenta.setBounds(264, 367, 98, 14);
		
		Combo combo = new Combo(shell, SWT.NONE);
		combo.setBounds(264, 387, 260, 22);

	}
}
