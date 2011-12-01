import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.widgets.Combo;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class LoadPeople {
	private Combo m;

	public LoadPeople(Combo mm) {
		m = mm;
	}

	public void run() {
		// String source =
		// "http://www.w3.org/TR/2003/CR-owl-guide-20030818/wine";
		String source = "owl/szpital2.owl";
		OntModel mm = ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MINI_RULE_INF, null);

		FileInputStream szpitalowl = null;
		try {
			szpitalowl = new FileInputStream(source);
			mm.read(szpitalowl, null);
		} catch (FileNotFoundException e) {
			System.out.println("Nie znaleziono pliku: " + e.getMessage());
		}

		String ns = "http://pawel/szpital#";
		OntClass workers = mm.getOntClass(ns + "Workers");
		ExtendedIterator<? extends OntResource> iter = workers.listInstances();
		for (; iter.hasNext();) {
			OntResource wine = iter.next();
			if (wine.getPropertyValue(mm.getProperty(ns + "name")) != null) {
				// System.out.println(wine.getPropertyValue(mm.getProperty(ns+"name")));
				m.add(wine.getPropertyValue(mm.getProperty(ns + "name"))
						.asLiteral().getString());
			}
		}

		/*
		 * String source =
		 * "http://www.w3.org/TR/2003/CR-owl-guide-20030818/wine";
		 * 
		 * OntModel m = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM,
		 * null );
		 * 
		 * m.getDocumentManager().addAltEntry(
		 * "http://www.w3.org/TR/2003/CR-owl-guide-20030818/wine",
		 * "file:wine.owl" ); m.getDocumentManager().addAltEntry(
		 * "http://www.w3.org/TR/2003/CR-owl-guide-20030818/food",
		 * "file:food.owl" );
		 * 
		 * 
		 * m.read( source );
		 * 
		 * 
		 * DescribeClass dc = new DescribeClass();
		 * 
		 * for (ResIterator i = m.listSubjects(); i.hasNext(); ) { Resource wine
		 * = i.nextResource(); //OntClass c = (OntClass) i.next();
		 * 
		 * //System.out.println( c.getLocalName().toString() );
		 * 
		 * if( wine.getLocalName() != null &&
		 * wine.getNameSpace().contains("wine") ){ System.out.println(
		 * wine.getLocalName() ); }
		 * 
		 * // now list the classes //dc.describeClass( System.out, (OntClass)
		 * i.next() ); }
		 */

		System.out.println("test");

	}
}
