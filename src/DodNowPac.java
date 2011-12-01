import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class DodNowPac extends Dialog {

	protected Object result;
	protected Shell shlDodajNowegoPacjenta;
	private Text txtImieinazwisko;
	private Text txtPesel;
	private Ontology ontMod;
	private String user;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DodNowPac(Shell parent, int style, Ontology o, String u) {
		super(parent, style);
		ontMod = o;
		user = u;
		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlDodajNowegoPacjenta.open();
		shlDodajNowegoPacjenta.layout();
		Display display = getParent().getDisplay();
		while (!shlDodajNowegoPacjenta.isDisposed()) {
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
		shlDodajNowegoPacjenta = new Shell(getParent(), getStyle());
		shlDodajNowegoPacjenta.setSize(238, 181);
		shlDodajNowegoPacjenta.setText("Dodaj nowego pacjenta");
		
		Label lblImienazwisko = new Label(shlDodajNowegoPacjenta, SWT.NONE);
		lblImienazwisko.setAlignment(SWT.RIGHT);
		lblImienazwisko.setBounds(10, 30, 80, 15);
		lblImienazwisko.setText("Imi\u0119 i nazwisko:");
		
		Label lblPesel = new Label(shlDodajNowegoPacjenta, SWT.NONE);
		lblPesel.setAlignment(SWT.RIGHT);
		lblPesel.setBounds(41, 54, 49, 13);
		lblPesel.setText("PESEL:");
		
		txtImieinazwisko = new Text(shlDodajNowegoPacjenta, SWT.BORDER);
		txtImieinazwisko.setBounds(96, 26, 76, 19);
		
		txtPesel = new Text(shlDodajNowegoPacjenta, SWT.BORDER);
		txtPesel.setBounds(96, 51, 76, 19);
		
		Button btnDodaj = new Button(shlDodajNowegoPacjenta, SWT.NONE);
		btnDodaj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pacjent pac = new Pacjent();
				if ((txtImieinazwisko.getCharCount() == 0) || (txtPesel.getCharCount() == 0))
				{
					MessageBox mb = new MessageBox(shlDodajNowegoPacjenta, SWT.ICON_ERROR | SWT.OK);
					mb.setMessage("Obydwa pola musz¹ byæ wype³nione!");
					mb.setText("B³¹d");
					mb.open();
					return;
				}
				pac.nazwa = txtImieinazwisko.getText();
				pac.PESEL = txtPesel.getText();
				
				ontMod.addNewPatient(pac, ontMod.getWorkerByName(user));
			}
		});
		btnDodaj.setBounds(10, 105, 68, 23);
		btnDodaj.setText("Dodaj");
		
		Button btnAnuluj = new Button(shlDodajNowegoPacjenta, SWT.NONE);
		btnAnuluj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlDodajNowegoPacjenta.dispose();
			}
		});
		btnAnuluj.setBounds(154, 105, 68, 23);
		btnAnuluj.setText("Anuluj");

	}
}
