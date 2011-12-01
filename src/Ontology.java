import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.ModelFactory;

class Ontology {
	private final String SzpitalFile = "owl/szpital2.owl";
	// private final String MammoFile = "owl/mammo.owl";
	// private final String Szns = "http://pawel/szpital#";
	OntModel Model;

	Ontology() { // wczytywanie ontologii z pliku
		Model = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MINI_RULE_INF, null);
		try {
			FileInputStream szpitalowl = new FileInputStream(SzpitalFile);
			Model.read(szpitalowl, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Zwraca listê pacjentów danego lekarza lub wszystkich pacjentów gdy doctor
	 * = null
	 * 
	 * @param doctor
	 * @return
	 */
	List<Pacjent> getPatients(String doctor) {
		List<Pacjent> pacjenci = new ArrayList<Pacjent>();
		String querys;
		if (doctor != null)
			querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
					+ "SELECT ?y ?z\r\n" + "WHERE { \r\n"
					+ "		?lek  foaf:name  \"" + doctor + "\" .\r\n"
					+ "		?x foaf:has_Doctor ?lek .\r\n"
					+ "		?x foaf:name ?y .\r\n" + "		?x foaf:PESEL ?z .\r\n"
					+ "}";
		else
			querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
					+ "SELECT ?y ?z\r\n" + "WHERE { \r\n"
					+ "		?x rdf:type foaf:Patients .\r\n"
					+ "		?x foaf:name ?y .\r\n" + "		?x foaf:PESEL ?z .\r\n"
					+ "}";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, Model);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			Pacjent p = new Pacjent();
			p.nazwa = qs.getLiteral("y").getString();
			p.PESEL = qs.getLiteral("z").getString();
			pacjenci.add(p);
			// p.stan = qs.getLiteral("s").getString();
		}
		qe.close();
		return pacjenci;
	}

	/**
	 * Zwraca pacjenta po numerze PESEL
	 * 
	 * @param PESEL
	 * @return
	 */
	Pacjent getPatientByPESEL(String PESEL) {
		String querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "SELECT ?x\r\n" + "WHERE { \r\n"
				+ "		?x rdf:type foaf:Patients .\r\n" + "		?x foaf:PESEL \""
				+ PESEL + "\" .\r\n" + "}";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, Model);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		Pacjent p = null;
		if (results.hasNext()) {
			QuerySolution qs = results.next();
			p = new Pacjent();
			p.nazwa = qs.getLiteral("y").getString();
			p.PESEL = qs.getLiteral("z").getString();
			// p.stan = qs.getLiteral("s").getString();
		}
		qe.close();
		return p;
	}

}