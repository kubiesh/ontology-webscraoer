import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Node;

import javax.ws.rs.NotAuthorizedException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OntologyXMLParser {

    public final static String[] countries = {"pl", "en"};

    private static String ontologyFilePath = "D:\\Informatyka\\Politechnika\\Oculus\\OculusOnt-v8.owl";
    private static Element rootNode;
    private static boolean isInitialized;
    private static Document document;

    public static void initialize() {
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(ontologyFilePath);

        document = null;
        try {
            document = (Document) builder.build(xmlFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootNode = document.getRootElement();
        isInitialized = true;
    }

    public static void scratchPad() {



    }

    public static List<OWLClassLabel> getAllOWLClassLabels() {
        if (!isInitialized) {
            throw new NotAuthorizedException("Not initialized");
        }
        List<OWLClassLabel> classLabels = new LinkedList<OWLClassLabel>();

        List<Element> classes = getAllOWLClasses();

        for (Element child : classes) {
            System.out.println(child.getName());
            List<Element> classProperties = child.getChildren();
            classProperties = classProperties.stream().filter(p -> p.getName().equals("label")).collect(Collectors.toList());
            for (Element property : classProperties) {
                String country = property.getAttributes().iterator().next().getValue();
                String entry = property.getValue();
                if (country != null && entry != null) {
                    OWLClassLabel label = new OWLClassLabel(country, entry);
                    classLabels.add(label);
                    System.out.println("Added");
                } else {
                    System.out.println("RDFS:LABEL country or label null");
                }
                System.out.println("\t" +   property.getAttributes().iterator().next().getValue() + ":"+ property.getValue());
            }
        }
        return classLabels;
    }

    public static List<Element> getAllOWLClasses() {
        if (!isInitialized) {
            throw new NotAuthorizedException("Not initialized");
        }

        List<Element> childrenList = rootNode.getChildren();
        List<Element> classes = childrenList.stream().filter(c -> c.getName().equals("Class")).collect(Collectors.toList());
        return classes;
    }

    public static List<OWLClassLabel> getOWLClassLabels(Element owlClass) {
        List<OWLClassLabel> classLabels = new LinkedList<OWLClassLabel>();

        List<Element> classProperties = owlClass.getChildren();
        classProperties = classProperties.stream().filter(p -> p.getName().equals("label")).collect(Collectors.toList());
        for (Element property : classProperties) {
            //first attribute is always language
            String country = property.getAttributes().iterator().next().getValue();
            String entry = property.getValue();
            if (country != null && entry != null) {
                OWLClassLabel label = new OWLClassLabel(country, entry);
                classLabels.add(label);
            } else {
                System.out.println("RDFS:LABEL country or label null");
            }
            System.out.println("\t" +   property.getAttributes().iterator().next().getValue() + ":"+ property.getValue());
        }
        return classLabels;
    }

    public static String getClassName(Element owlClass) {
        //First attribute is always rdf:about
        String fullURI = owlClass.getAttributes().iterator().next().getValue();
        Pattern compiledPattern = Pattern.compile("#\\w+");
        Matcher matcher = compiledPattern.matcher(fullURI);

        if(matcher.find()) {
            //Skip '#'(on index 0) when returning class name
            return matcher.group().substring(1);
        }
        return "";
    }

    public static String getClassLabel(String country, List<OWLClassLabel> classLabels) {
        for (OWLClassLabel classLabel : classLabels) {
            if (classLabel.getCountry().equals(country)) {
                return classLabel.getEntry();
            }
        }
        return "";
    }

    public static void setNewValue(Element owlClass, String country, String value) {
        List<Element> classProperties = owlClass.getChildren();
        classProperties = classProperties.stream().filter(p -> p.getName().equals("label")).collect(Collectors.toList());

        for (Element property : classProperties) {
            //first attribute is always language
            if(property.getAttributes().iterator().next().getValue().equals(country)) {
                String entry = property.getValue();
                property.setText(value);
                saveFile();
                return;
            }
        }

        owlClass.addContent(new Element("label", Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#")).setAttribute(new Attribute("lang",country,Namespace.getNamespace("xml","http://www.w3.org/XML/1998/namespace"))).addContent(value));


        XMLOutputter xo = new XMLOutputter();
        try {
            xo.output(document, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveFile();

    }
    private static void saveFile() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(ontologyFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        XMLOutputter outputter = new XMLOutputter();

        try {
            outputter.output(document, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writer.close(); // close writer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
