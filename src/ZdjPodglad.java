import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

class ZdjPodglad {
	private List<Zdjecie> zdjecia;
	private int ile;
	private int ktore;

	public ZdjPodglad(List<Zdjecie> zdjeciaa) {
		zdjecia = zdjeciaa;
		ile = zdjecia.size();
		ktore = 0;
	}

	public Image getImage(int x, int y) {
		ImageData imd = new ImageData(mammografia.lokalizacja
				+ zdjecia.get(ktore).nazwapliku);
		System.out.println(Integer.toString(x) + " " + Integer.toString(y));
		System.out.println(Integer.toString(imd.width) + " " + Integer.toString(imd.height));
		int max = (x>y)?x:y;
		int mino = (imd.height<imd.width)?imd.height:imd.width;
		double scale = (double)max/(double)mino;
		ImageData imds = imd.scaledTo((int)(scale*imd.width), (int)(scale*imd.height));
		System.out.println(Integer.toString(imds.width) + " " + Integer.toString(imds.height));
		System.out.println("***");
		return new Image(Display.getDefault(), imds);
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
