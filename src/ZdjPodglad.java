import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

class ZdjPodglad {
	private List<Zdjecie> zdjecia;
	private int ile;
	private int ktore;
	private final String lokalizacja = "./imdb/"; // folder z plikami

	public ZdjPodglad(List<Zdjecie> zdjeciaa) {
		zdjecia = zdjeciaa;
		ile = zdjecia.size();
		ktore = 0;
	}

	public Image getImage() {
		ImageData imd = new ImageData(lokalizacja
				+ zdjecia.get(ktore).nazwapliku);
		return new Image(Display.getDefault(), imd);
	}

	public void next() {
		if (ktore < (ile - 1))
			ktore++;
		else
			ktore = 0;
	}

	public void previous() {
		if (ktore > 0)
			ktore--;
		else
			ktore = ile - 1;
	}
}
