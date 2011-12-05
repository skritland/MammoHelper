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

		//int max = (x>y)?x:y;
		boolean isBiggerWidth = (imd.height<imd.width)?true:false;
		double scale;
		if (isBiggerWidth)
		{
			//if (((double)x)/((double)y) > ((double)imd.width)/((double)imd.height))
			scale = (double)x/(double)imd.width;
		}
		else
		{
			scale = (double)y/(double)imd.height;
		}
		ImageData imds = imd.scaledTo((int)(scale*imd.width), (int)(scale*imd.height));
		//System.out.println(Integer.toString(imds.width) + " " + Integer.toString(imds.height));
		//System.out.println("***");
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
	
	public int num(){
		return ktore;
	}
	
	public int ile(){
		return ile;
	}
	
	public String view() {
		return zdjecia.get(ktore).widok;
	}
}
