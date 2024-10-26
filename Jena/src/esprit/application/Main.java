package esprit.application;
import esprit.tools.JenaEngine;
import org.apache.jena.rdf.model.*;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

public class Main {
    public static void main(String[] args) {
        String NS = "";
        // Read the model from an ontology
        Model model = JenaEngine.readModel("data/projet.owl");
        if (model != null) {
            NS = model.getNsPrefixURI("");

            // Apply inference rules
            Model inferredModel = JenaEngine.readInferencedModelFromRuleFile(model, "data/rules.txt");

            // Execute queries (SELECT)
            System.out.println(JenaEngine.executeQueryFile(inferredModel, "data/query.txt"));
            System.out.println(JenaEngine.executeQueryFile(inferredModel, "data/queryadminemail.txt"));


            // Insertion example using Update API
            String insertQuery = "PREFIX ns: <http://www.semanticweb.org/hafsi/ontologies/2024/9/untitled-ontology-24#> "
                    + "INSERT DATA { "
                    + "ns:admin3 a ns:Admin ; "
                    + "ns:Id 3 ; "
                    + "ns:nom \"Alice Dupont\" ; "
                    + "ns:email \"alice.dupont@example.com\" . "
                    + "}";

            // Create an update request and execute it
            UpdateRequest updateRequest = UpdateFactory.create(insertQuery);
            UpdateAction.execute(updateRequest, inferredModel);

            // Verify insertion by querying
            System.out.println(JenaEngine.executeQueryFile(inferredModel, "data/querytrotinette.txt"));

        } else {
            System.out.println("Error when reading model from ontology");
        }
    }
}