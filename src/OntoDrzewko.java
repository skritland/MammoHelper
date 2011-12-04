import java.util.List;


class OntoDrzewko implements Comparable<OntoDrzewko> {
	public String nazwa;
	public String URI;
	public List<OntoDrzewko> dzieci;
	public boolean wybrane;
	public boolean wybraneDziecko;
	@Override
	public int compareTo(OntoDrzewko o) {
		return nazwa.compareToIgnoreCase(o.nazwa);
	}
	
}
