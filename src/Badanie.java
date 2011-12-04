import java.util.Date;
import java.util.List;

class Badanie {
	public String URI;
	public Pacjent pacjent;
	public Date dataBadania;
	public List<Zdjecie> zdjecia;
	public String ocena;
	public List<Choroby> choroby; // zdiagnozowane choroby
	public List<Zauwazone> zauwazone; // to co zosta³o zauwa¿one na zdjêciach (zgrubienia, itp.)
}
