import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

class Ontology {
	private final String SzpitalFile = "owl/szpital2.owl";
	private final String MammoFile = "owl/mammo.owl";
	private final String Szns = "http://pawel/szpital#";
	private final String MammoNS = "http://www.chime.ucl.ac.uk/ontologies/mammo#";
	OntModel OModel, OModel2;
	FileOutputStream FOS;

	Ontology() { // wczytywanie ontologii z pliku
		OModel = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MINI_RULE_INF, null);
		OModel2 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		// OModel.getDocumentManager().addAltEntry("http://www.chime.ucl.ac.uk/ontologies/mammo",
		// MammoFile);
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
		try {
			FileInputStream mammoowl = new FileInputStream(MammoFile);
			OModel2.read(mammoowl, null);
			mammoowl.close();
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
	 * Zwraca listê pacjentów danego lekarza lub wszystkich pacjentów gdy doctor
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
						+ "SELECT ?x ?y ?z ?stan\r\n" + "WHERE { \r\n"
						+ "		?lek foaf:name  \"" + doctor + "\" .\r\n"
						+ "		?x foaf:has_Doctor ?lek .\r\n"
						+ "		?x foaf:name ?y .\r\n"
						+ "		?x foaf:PESEL ?z .\r\n"
						+ "		OPTIONAL { ?x foaf:State ?stan . } \r\n" + "}" + "\r\nORDER BY ?y";
			else
				querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
						+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
						+ "SELECT ?x ?y ?z ?stan WHERE { \r\n"
						+ "		?x rdf:type foaf:Patients.\r\n"
						+ "		?x foaf:name ?y .\r\n"
						+ "		?x foaf:PESEL ?z .\r\n"
						+ "		OPTIONAL { ?x foaf:State ?stan . } \r\n"
						+ "		OPTIONAL {\r\n"
						+ "		?x foaf:has_Doctor ?lek .\r\n"
						+ "		?lek  foaf:name ?imie .\r\n" + "		}\r\n"
						+ "		FILTER (!bound(?imie) || ?imie != \"" + doctor
						+ "\") .\r\n" + "	}" + "\r\nORDER BY ?y";
		} else
			querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
					+ "SELECT ?x ?y ?z ?stan\r\n" + "WHERE { \r\n"
					+ "		?x rdf:type foaf:Patients .\r\n"
					+ "		?x foaf:name ?y .\r\n" + "		?x foaf:PESEL ?z .\r\n"
					+ "		OPTIONAL { ?x foaf:State ?stan . } \r\n" + "}" + "\r\nORDER BY ?y";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, OModel);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			Pacjent p = new Pacjent();
			p.URI = qs.getResource("x").getURI();
			p.nazwa = qs.getLiteral("y").getString();
			p.PESEL = qs.getLiteral("z").getString();
			Literal tmp = qs.getLiteral("stan");
			p.stan = (tmp == null) ? (null) : (tmp.getString());
			pacjenci.add(p);
			p.ostatbad = getLastExaminationDate(p);
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
				+ "SELECT ?x ?y ?s\r\n" + "WHERE { \r\n"
				+ "		?x rdf:type foaf:Patients .\r\n" + "		?x foaf:name ?y ."
				+ "		?x foaf:PESEL \"" + PESEL + "\" .\r\n"
				+ "		OPTIONAL { ?x foaf:State ?s . }\r\n" + "}";
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
			Literal lit = qs.getLiteral("s");
			p.stan = (lit == null) ? null : lit.getString();
			p.ostatbad = getLastExaminationDate(p);
		}
		qe.close();
		return p;
	}
	
	java.util.Date getLastExaminationDate(Pacjent pac) {
		List<Badanie> liba = getExaminationsOfPatient(pac);
		Date dat = null;
		if ((liba != null) && (!liba.isEmpty()))
		dat = liba.get(0).dataBadania;
		return dat;
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
			Individual wor = OModel.getIndividual(p.URI);
			if (wor.hasOntClass(OModel.getOntClass(Szns + "Doctors"))) {
				p.isDoctor = true;
			} else
				p.isDoctor = false;
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
		if (wor.hasOntClass(OModel.getOntClass(Szns + "Doctors"))) {
			patient.addProperty(OModel.getProperty(Szns + "has_Doctor"),
					OModel.getIndividual(workerByName.URI));
		}
		save();

	}

	public void addNewExamination(Badanie bad) {
		OntClass badania = OModel.getOntClass(Szns + "Mammography_Examination");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String nazwa = bad.pacjent.PESEL + "_" + sdf.format(bad.dataBadania);
		Individual badanie = OModel.createIndividual(Szns + nazwa, badania);
		badanie.addProperty(OModel.getProperty(Szns + "of_Patient"),
				OModel.getIndividual(bad.pacjent.URI));
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		badanie.addProperty(OModel.getProperty(Szns + "creat_date"),
				sdf2.format(bad.dataBadania), XSDDatatype.XSDdateTime);
		ListIterator<Zdjecie> li = bad.zdjecia.listIterator();
		while (li.hasNext()) {
			Zdjecie zdj = li.next();
			OntClass zdjecia = OModel.getOntClass(Szns + "Mammography_Images");
			nazwa = bad.pacjent.PESEL + "_" + sdf.format(bad.dataBadania) + "_"
					+ zdj.widok;
			Individual zdjecie = OModel.createIndividual(Szns + nazwa, zdjecia);
			zdjecie.addProperty(OModel.getProperty(Szns + "has_group"), badanie);
			zdjecie.addProperty(OModel.getProperty(Szns + "im_view"), zdj.widok);
			zdjecie.addProperty(OModel.getProperty(Szns + "filename"),
					zdj.nazwapliku);
		}

		save();

	}

	List<Badanie> getExaminationsOfPatient(Pacjent pac) {
		String querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "SELECT ?x ?y ?o WHERE { \r\n"
				+ "		?x rdf:type foaf:Mammography_Examination.\r\n"
				+ "		?x foaf:of_Patient <" + pac.URI + "> .\r\n"
				+ "		?x foaf:creat_date ?y .\r\n"
				+ "		OPTIONAL { ?x foaf:ocena ?o . } \r\n" + "	}" + "\r\nORDER BY DESC(?y)";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, OModel);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		List<Badanie> lizdje = new ArrayList<Badanie>();
		Badanie zdje = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			zdje = new Badanie();
			zdje.pacjent = pac;
			zdje.URI = qs.getResource("x").getURI();
			Literal lit = qs.getLiteral("o");
			zdje.ocena = (lit == null) ? null : lit.getString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Individual examination = OModel.getIndividual(zdje.URI);
			StmtIterator si = examination.listProperties(OModel.getProperty(Szns + "has_mammo_assessment"));
			if (si.hasNext()) zdje.choroby = new ArrayList<Choroby>();
			Statement stmt;
			Choroby cho;
			while (si.hasNext()) {
				stmt = si.next();
				cho = new Choroby();
				cho.URI = stmt.getResource().getURI();
				cho.nazwa = stmt.getResource().getLocalName().replace('_', ' ');
				zdje.choroby.add(cho);
			}
			si = examination.listProperties(OModel.getProperty(Szns + "has_mammo_finding"));
			if (si.hasNext()) zdje.zauwazone = new ArrayList<Zauwazone>();
			Zauwazone zau;
			while (si.hasNext()) {
				stmt = si.next();
				zau = new Zauwazone();
				zau.URI = stmt.getResource().getURI();
				zau.nazwa = stmt.getResource().getLocalName().replace('_', ' ');
				zdje.zauwazone.add(zau);
			}
			
			try {
				zdje.dataBadania = sdf.parse(qs.getLiteral("y").getString());
			} catch (ParseException e) {

				e.printStackTrace();
			}
			lizdje.add(zdje);
		}
		qe.close();
		ListIterator<Badanie> iter = lizdje.listIterator();
		while (iter.hasNext()) {
			zdje = iter.next();
			querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
					+ "SELECT ?zdj ?fnam ?view WHERE { \r\n"
					+ "		?zdj rdf:type foaf:Mammography_Images .\r\n"
					+ "		?zdj foaf:has_group <" + zdje.URI + "> .\r\n"
					+ "		?zdj foaf:im_view ?view .\r\n"
					+ "		?zdj foaf:filename ?fnam .\r\n" + "	}";
			query = QueryFactory.create(querys);
			qe = QueryExecutionFactory.create(query, OModel);
			results = qe.execSelect();
			zdje.zdjecia = new ArrayList<Zdjecie>();
			Zdjecie zdj = null;
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				zdj = new Zdjecie();
				zdj.URI = qs.getResource("zdj").getURI();
				zdj.nazwapliku = qs.getLiteral("fnam").getString();
				zdj.widok = qs.getLiteral("view").getString();

				zdje.zdjecia.add(zdj);
			}
			qe.close();
		}

		return lizdje;

	}

	public void removePatient(Pacjent pac) {
		Individual pacjent = OModel.getIndividual(pac.URI);
		pacjent.remove();
		save();
	}

	public void removeDoctorFromPatient(Worker wUser, Pacjent pac) {
		Individual pacjent = OModel.getIndividual(pac.URI);
		pacjent.removeProperty(OModel.getProperty(Szns + "has_Doctor"),
				OModel.getIndividual(wUser.URI));
		save();
	}

	public void removeExamination(Badanie bad) {
		String querys = "PREFIX foaf: <http://pawel/szpital#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "SELECT ?zdj WHERE { \r\n"
				+ "		?zdj rdf:type foaf:Mammography_Images .\r\n"
				+ "		?zdj foaf:has_group <" + bad.URI + "> .\r\n" + "	}";
		Query query = QueryFactory.create(querys);
		QueryExecution qe = QueryExecutionFactory.create(query, OModel);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		ArrayList<Individual> al = new ArrayList<Individual>();
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			al.add(OModel.getIndividual(qs.getResource("zdj").getURI()));
		}
		qe.close();
		for (Individual in : al)
			in.remove();
		Individual badanie = OModel.getIndividual(bad.URI);
		badanie.remove();
		save();
	}

	public OntoDrzewko getDiagnosisList() {
		OntoDrzewko root = new OntoDrzewko();
		OntClass choroby = OModel2.getOntClass(MammoNS + "Diagnosis");
		root.nazwa = "Diagnosis"; // pewnie bêdzie nieu¿ywane
		ExtendedIterator<OntClass> ei = choroby.listSubClasses(true);
		OntClass pom1 = null;
		OntClass pom2 = null;
		ExtendedIterator<OntClass> ei2;
		OntoDrzewko gal = null;
		OntoDrzewko gal2 = null;
		root.dzieci = new ArrayList<OntoDrzewko>();
		while (ei.hasNext()) {
			pom1 = ei.next();
			gal = new OntoDrzewko();
			gal.URI = pom1.getURI();
			gal.nazwa = pom1.getLocalName().replace('_', ' ');
			gal.wybrane = false;
			gal.wybraneDziecko = false;

			if (!pom1.hasSubClass())
				gal.dzieci = null;
			else {
				ei2 = pom1.listSubClasses(true);
				gal.dzieci = new ArrayList<OntoDrzewko>();
				while (ei2.hasNext()) {
					pom2 = ei2.next();
					gal2 = new OntoDrzewko();
					gal2.dzieci = null;
					gal2.URI = pom2.getURI();
					gal2.nazwa = pom2.getLocalName().replace('_', ' ');
					// System.out.println(gal2.nazwa);
					gal2.wybrane = false;
					gal2.wybraneDziecko = false;
					gal.dzieci.add(gal2);
				}
				Collections.sort(gal.dzieci);
			}
			root.dzieci.add(gal);
		}
		Collections.sort(root.dzieci);
		return root;
	}

	public OntoDrzewko getFindingsList() {
		OntoDrzewko root = new OntoDrzewko();
		OntClass zauwazone = OModel2.getOntClass(MammoNS + "Finding");
		root.nazwa = "Finding"; // pewnie bêdzie nieu¿ywane
		ExtendedIterator<OntClass> ei = zauwazone.listSubClasses(true);
		OntClass pom1 = null;
		OntClass pom2 = null;
		ExtendedIterator<OntClass> ei2;
		OntoDrzewko gal = null;
		OntoDrzewko gal2 = null;
		root.dzieci = new ArrayList<OntoDrzewko>();
		while (ei.hasNext()) {
			pom1 = ei.next();
			gal = new OntoDrzewko();
			gal.URI = pom1.getURI();
			gal.nazwa = pom1.getLocalName().replace('_', ' ');
			gal.wybrane = false;
			gal.wybraneDziecko = false;

			if (!pom1.hasSubClass())
				gal.dzieci = null;
			else {
				ei2 = pom1.listSubClasses(true);
				gal.dzieci = new ArrayList<OntoDrzewko>();
				while (ei2.hasNext()) {
					pom2 = ei2.next();
					gal2 = new OntoDrzewko();
					gal2.dzieci = null;
					gal2.URI = pom2.getURI();
					gal2.nazwa = pom2.getLocalName().replace('_', ' ');
					// System.out.println(gal2.nazwa);
					gal2.wybrane = false;
					gal2.wybraneDziecko = false;
					gal.dzieci.add(gal2);
				}
				Collections.sort(gal.dzieci);
			}
			root.dzieci.add(gal);
		}
		Collections.sort(root.dzieci);
		return root;
	}

	public void updateExamination(Badanie badanie) {
		Individual examination = OModel.getIndividual(badanie.URI);
		examination.removeAll(OModel.getProperty(Szns + "ocena"));
		examination.removeAll(OModel.getProperty(Szns + "has_mammo_finding"));
		examination
				.removeAll(OModel.getProperty(Szns + "has_mammo_assessment"));
		examination.addProperty(OModel.getProperty(Szns + "ocena"),
				badanie.ocena);
		if (badanie.choroby != null)
			for (Choroby cho : badanie.choroby)
				examination.addProperty(
						OModel.getProperty(Szns + "has_mammo_assessment"),
						OModel2.getIndividual(cho.URI));
		if (badanie.zauwazone != null)
			for (Zauwazone zauw : badanie.zauwazone)
				examination.addProperty(
						OModel.getProperty(Szns + "has_mammo_finding"),
						OModel2.getIndividual(zauw.URI));
		Individual patient = OModel.getIndividual(badanie.pacjent.URI);
		patient.removeAll(OModel.getProperty(Szns + "State"));
		patient.addProperty(OModel.getProperty(Szns + "State"),
				badanie.pacjent.stan);
		save();

	}
}