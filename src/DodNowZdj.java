import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class DodNowZdj extends Dialog {

	protected Zdjecie result;
	protected Shell shlDodajZdjecie;
	private Text text;
	private String fn;
	private Label lblPlik;
	private Label lblPodglad;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public DodNowZdj(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		result = null;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Zdjecie open() {
		createContents();
		shlDodajZdjecie.open();
		shlDodajZdjecie.layout();
		Display display = getParent().getDisplay();
		while (!shlDodajZdjecie.isDisposed()) {
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
		shlDodajZdjecie = new Shell(getParent(), getStyle());
		shlDodajZdjecie.setSize(215, 372);
		shlDodajZdjecie.setText("Dodaj zdj\u0119cie");

		Button btnWybierzPlik = new Button(shlDodajZdjecie, SWT.NONE);
		btnWybierzPlik.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shlDodajZdjecie, SWT.OPEN);

				String[] filterNames = new String[] { "Obrazki" };
				String[] filterExtensions = new String[] { "*.gif;*.png;*.xpm;*.jpg;*.jpeg;*.tiff" };
				String filterPath = "/";
				String platform = SWT.getPlatform();

				if (platform.equals("win32") || platform.equals("wpf")) {
					filterPath = "c:\\";
				}

				dialog.setFilterNames(filterNames);
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(filterPath);

				fn = dialog.open();
				if (fn != null)
				{
					lblPlik.setText(fn);
					ImageData imd = new ImageData(fn);
					
					boolean isBiggerWidth = (imd.height<imd.width)?true:false;
					double scale;
					Point pp = lblPodglad.getSize();
					if (isBiggerWidth)
					{
						//if (((double)x)/((double)y) > ((double)imd.width)/((double)imd.height))
						scale = (double)pp.x/(double)imd.width;
					}
					else
					{
						scale = (double)pp.y/(double)imd.height;
					}
					ImageData imds = imd.scaledTo((int)(scale*imd.width), (int)(scale*imd.height));
					
					lblPodglad.setImage(new Image(Display.getDefault(), imds));
				}
				else {
					lblPlik.setText("");
					lblPodglad.setImage(null);
				}
			}
		});
		btnWybierzPlik.setBounds(10, 259, 68, 23);
		btnWybierzPlik.setText("Wybierz plik");

		Button btnOk = new Button(shlDodajZdjecie, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ((text.getCharCount() == 0) || (fn == null)) {
					MessageBox mb = new MessageBox(shlDodajZdjecie,
							SWT.ICON_ERROR | SWT.OK);
					mb.setMessage("Wszystkie pola musz¹ byæ wype³nione i wybrany plik!");
					mb.setText("B³¹d");
					mb.open();
					return;
				}
				result = new Zdjecie();
				result.nazwapliku = fn;
				result.widok = text.getText();
				shlDodajZdjecie.dispose();
			}
		});
		btnOk.setBounds(10, 306, 68, 23);
		btnOk.setText("OK");

		Button btnAnuluj = new Button(shlDodajZdjecie, SWT.NONE);
		btnAnuluj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlDodajZdjecie.dispose();
			}
		});
		btnAnuluj.setBounds(100, 306, 68, 23);
		btnAnuluj.setText("Anuluj");

		lblPodglad = new Label(shlDodajZdjecie, SWT.NONE);
		lblPodglad.setBounds(10, 29, 180, 180);

		lblPlik = new Label(shlDodajZdjecie, SWT.NONE);
		lblPlik.setBounds(10, 10, 183, 13);

		Label lblWidok = new Label(shlDodajZdjecie, SWT.NONE);
		lblWidok.setAlignment(SWT.RIGHT);
		lblWidok.setBounds(10, 229, 33, 13);
		lblWidok.setText("Widok:");

		text = new Text(shlDodajZdjecie, SWT.BORDER);
		text.setBounds(49, 226, 141, 19);

	}
}
