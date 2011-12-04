import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class DodNowBad extends Dialog {

	protected Badanie result;
	DateTime date;
	DateTime time;
	protected Shell shlD;
	private Table table;
	private List<Zdjecie> zdjecia;
	private Pacjent pacjent;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DodNowBad(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		result = null;
		zdjecia = new ArrayList<Zdjecie>();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Badanie open(Pacjent pac) {
		pacjent = pac;
		createContents();
		shlD.open();
		shlD.layout();
		Display display = getParent().getDisplay();
		while (!shlD.isDisposed()) {
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
		shlD = new Shell(getParent(), getStyle());
		shlD.setSize(450, 300);
		shlD.setText("Dodaj badanie: " + pacjent.nazwa);
		
		Button btnOk = new Button(shlD, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (zdjecia.isEmpty())
				{
					MessageBox mb = new MessageBox(shlD,
							SWT.ICON_ERROR | SWT.OK);
					mb.setMessage("Musi byæ przynajmniej jedno zdjêcie");
					mb.setText("B³¹d");
					mb.open();
					return;
				}
				result = new Badanie();
				result.dataBadania = new Date(date.getYear()-1900, date.getMonth(), date.getDay(), time.getHours(), time.getMinutes());
				result.pacjent = pacjent;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				for (Zdjecie zzzd : zdjecia)
				{
					int dot = zzzd.nazwapliku.lastIndexOf('.');
					String fname = result.pacjent.PESEL + "_" + sdf.format(result.dataBadania) + "_" + zzzd.widok + zzzd.nazwapliku.substring(dot);
					java.nio.file.Path source = java.nio.file.Paths.get(zzzd.nazwapliku);
					java.nio.file.Path dest = java.nio.file.Paths.get(mammografia.lokalizacja + fname);
					try {
						java.nio.file.Files.copy(source, dest, java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					zzzd.nazwapliku = fname;
				}
				result.zdjecia = zdjecia;
				shlD.dispose();
			}
		});
		btnOk.setBounds(10, 242, 68, 23);
		btnOk.setText("OK");
		
		Button btnAnuluj = new Button(shlD, SWT.NONE);
		btnAnuluj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlD.dispose();
			}
		});
		btnAnuluj.setBounds(366, 242, 68, 23);
		btnAnuluj.setText("Anuluj");
		
		Button btnDodajZdjecie = new Button(shlD, SWT.NONE);
		btnDodajZdjecie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DodNowZdj okno = new DodNowZdj(shlD, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
				Zdjecie zdj = okno.open();
				if (zdj == null) return;
				zdjecia.add(zdj);
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setText(new String[] { zdj.nazwapliku, zdj.widok });
			}
		});
		btnDodajZdjecie.setBounds(10, 160, 89, 23);
		btnDodajZdjecie.setText("Dodaj zdj\u0119cie");
		
		date = new DateTime(shlD, SWT.BORDER);
		date.setBounds(208, 160, 90, 23);
		
		time = new DateTime(shlD, SWT.BORDER | SWT.TIME | SWT.SHORT);
		time.setBounds(304, 160, 77, 23);
		
		table = new Table(shlD, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 424, 132);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNazwaPliku = new TableColumn(table, SWT.NONE);
		tblclmnNazwaPliku.setWidth(337);
		tblclmnNazwaPliku.setText("Nazwa pliku");
		
		TableColumn tblclmnWidok = new TableColumn(table, SWT.NONE);
		tblclmnWidok.setWidth(83);
		tblclmnWidok.setText("Widok");
		
		Label lblDataBadania = new Label(shlD, SWT.NONE);
		lblDataBadania.setBounds(134, 165, 68, 13);
		lblDataBadania.setText("Data badania:");

	}
}
