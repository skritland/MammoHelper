import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
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
	private final String Szns = "http://pawel/szpital#";
	OntModel OModel;
	FileOutputStream FOS;

	Ontology() { // wczytywanie ontologii z pliku
		OModel = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MINI_RULE_INF, null);
		try {
			FileInputStream szpitalowl = new FileInputStream(SzpitalFile);
			OModel.read(szpitalowl, null);
			szpitalowl.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void save() {
		try {
			FOS = new FileOutputStream(SzpitalFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OModel.write(FOS);
	}

	/**
	 * Zwraca list� pacjent�w danego lekarza lub wszystkich pacjent�w gdy doctor
	 * = null
	 * 
	 * @param doctor
	 * @param doktora
	 * @return
	 */
	List<Pacjent> getPatients(String doctor, Boolean doktora) {
		List<Pacjent> pacjenci = new ArrayList<Pacjent>();
		String querys;
		if (doctor != null) {
			if (doktora == true)
				querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
						+ "SELECT ?y ?z\r\n" + "WHERE { \r\n"
						+ "		?lek foaf:name  \"" + doctor + "\" .\r\n"
						+ "		?x foaf:has_Doctor ?lek .\r\n"
						+ "		?x foaf:name ?y .\r\n"
						+ "		?x foaf:PESEL ?z .\r\n" + "}";
			else
				querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
						+ "SELECT ?y ?z WHERE { \r\n"
						+ "		?x rdf:type foaf:Patients.\r\n"
						+ "		?x foaf:name ?y .\r\n"
						+ "		?x foaf:PESEL ?z .\r\n" + "		OPTIONAL {\r\n"
						+ "		?x foaf:has_Doctor ?lek .\r\n"
						+ "		?lek  foaf:name ?imie .\r\n" + "		}\r\n"
						+ "		FILTER (!bound(?imie) || ?imie != \"" + doctor
						+ "\") .\r\n" + "	}";
		} else
			querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
					+ "SELECT ?y ?z\r\n" + "WHERE { \r\n"
					+ "		?x rdf:type foaf:Patients .\r\n"
					+ "		?x foaf:name ?y .\r\n" + "		?x foaf:PESEL ?z .\r\n"
					+ "}";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, OModel);
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
				+ "SELECT ?x ?y\r\n" + "WHERE { \r\n"
				+ "		?x rdf:type foaf:Patients .\r\n" + "		?x foaf:name ?y ."
				+ "		?x foaf:PESEL \"" + PESEL + "\" .\r\n" + "}";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, OModel);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		Pacjent p = null;
		if (results.hasNext()) {
			QuerySolution qs = results.next();
			p = new Pacjent();
			p.nazwa = qs.getLiteral("y").getString();
			p.PESEL = PESEL;
			p.URI = qs.getResource("x").getURI();
			// p.stan = qs.getLiteral("s").getString();
		}
		qe.close();
		return p;
	}

	Worker getWorkerByName(String worname) {
		String querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "SELECT ?x ?z\r\n" + "WHERE { \r\n"
				+ "		?x rdf:type foaf:Workers .\r\n" + "		?x foaf:name \""
				+ worname + "\" .\r\n" + "		?x foaf:PESEL ?z .\r\n" + "}";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, OModel);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		Worker p = null;
		if (results.hasNext()) {
			QuerySolution qs = results.next();
			p = new Worker();
			p.nazwa = worname;
			p.PESEL = qs.getLiteral("z").getString();
			p.URI = qs.getResource("x").getURI();
			// p.stan = qs.getLiteral("s").getString();
		}
		qe.close();
		return p;
	}

	void addDoctorToPatient(String docuri, String pacuri) {
		Individual pac = OModel.getIndividual(pacuri);
		// System.out.println(pac);
		pac.addProperty(OModel.getProperty(Szns + "has_Doctor"),
				OModel.getIndividual(docuri));
		save();
	}

	public void addNewPatient(Pacjent pac, Worker workerByName) {
		OntClass patients = OModel.getOntClass(Szns + "Patients");
		String pom = pac.nazwa.replace(' ', '_');
		Individual patient = OModel.createIndividual(Szns + pom, patients);
		patient.addProperty(OModel.getProperty(Szns + "name"), pac.nazwa);
		patient.addProperty(OModel.getProperty(Szns + "PESEL"), pac.PESEL);
		Individual wor = OModel.getIndividual(workerByName.URI);
		if (wor.getOntClass().getURI() == OModel.getOntClass(Szns + "Doctors")
				.getURI()) {
			patient.addProperty(OModel.getProperty(Szns + "has_Doctor"),
					OModel.getIndividual(workerByName.URI));
		}
		save();

	}

}