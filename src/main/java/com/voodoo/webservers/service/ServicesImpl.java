package com.voodoo.webservers.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

/**
 * @author chamerling
 * 
 */
@WebService(endpointInterface="com.voodoo.webservers.service.Services", serviceName="Services")
public class ServicesImpl implements Services {

    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds057862.mlab.com:57862/prms");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());
    String pwd;
    int is_valid=0;
    boolean patientfound=false;
    boolean datafound;


	@Override
    public String validate_user( String user_name,String password) {

        MongoCollection<org.bson.Document> collection = db.getCollection("credentials");

		try {
            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);

            Element node = doc.createElement("status");


            is_valid=0;
            pwd=null;
            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("user_name", user_name));
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {
                    is_valid=1;
                    pwd = String.valueOf(document.get("password"));
                }

            });



            if(is_valid==1 && pwd.equals(password)){

                node.appendChild(doc.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);

            }
            else
            {
                node.appendChild(doc.createTextNode("false"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);
            }


			/* //output DOM XML to console
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult console = new StreamResult(System.out);
			transformer.transform(source, console);*/

			//System.out.println(convertDocumentToString(doc));
			//System.out.println("\nXML DOM Created Successfully..");
		}catch(Exception e)
		{
           e.printStackTrace();
		}


		return "hello world";
	}

    @Override
    public String register_user(String User_Name, String Password, String Hospital_name, String Type_of_User) {
        MongoCollection<org.bson.Document> collection = db.getCollection("credentials");
        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);



            org.bson.Document doc1;
            if(!Type_of_User.equals("Ambulance Staff")) {
                         doc1 = new org.bson.Document("user_name", User_Name)
                        .append("password", Password)
                        .append("hospital_name", Hospital_name)
                        .append("type_of_user", Type_of_User)
                        .append("ambulance_id", "null");
            }else
            {
                        String temp="amb_"+String.valueOf(gen());
                        doc1 = new org.bson.Document("user_name", User_Name)
                        .append("password", Password)
                        .append("hospital_name", Hospital_name)
                        .append("type_of_user", Type_of_User)
                        .append("ambulance_id", temp);
                Element node = doc.createElement("ambulance_id");
                node.appendChild(doc.createTextNode(temp));
                mainRootElement.appendChild(node);
            }
            collection.insertOne(doc1);
            Element node = doc.createElement("status");

                node.appendChild(doc.createTextNode("true"));
                mainRootElement.appendChild(node);

                return convertDocumentToString(doc);
        }
        catch(Exception e)
        {
           e.printStackTrace();
        }
        return "null";
    }

 /*   @Override
    public String register_ambulance(String User_Name, String Password, String Hospital_name, String Type_of_User, String ambulance_id) {
        MongoCollection<org.bson.Document> collection = db.getCollection("credentials");
        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);

            Element node = doc.createElement("status");

            org.bson.Document doc1 = new org.bson.Document("user_name", User_Name)
                    .append("password", Password)
                    .append("hospital_name", Hospital_name)
                    .append("type_of_user", Type_of_User)
                    .append("ambulance_id", ambulance_id);
            collection.insertOne(doc1);
            node.appendChild(doc.createTextNode("true"));
            mainRootElement.appendChild(node);
            return convertDocumentToString(doc);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "null";
    }*/

    public int gen() {
        Random r = new Random( System.currentTimeMillis() );
        return 10000 + r.nextInt(20000);
    }


    @Override
    public String add_new_patient(String hospital_name, String ambulance_id, String p_name, String gender, String blood_grp, String condition, String problem, String police_case, String is_enabled) {

        MongoCollection<org.bson.Document> collection = db.getCollection("patient_details");

        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();

            Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);

            String p_id="Patient_"+String.valueOf(gen());
            if(p_name==null)
            {
                p_name=p_id;
            }

            org.bson.Document doc1 = new org.bson.Document("hospital_name", hospital_name)
                    .append("ambulance_id", ambulance_id)
                    .append("p_name", p_name)
                    .append("p_id", p_id)
                    .append("gender", gender)
                    .append("blood_grp", blood_grp)
                    .append("condition", condition)
                    .append("problem", problem)
                    .append("police_case", police_case)
                    .append("is_enabled", is_enabled);

            collection.insertOne(doc1);
            System.out.println("1");
            Element node = doc.createElement("P_id");
            System.out.println("2");
            node.appendChild(doc.createTextNode(p_id));
            System.out.println("3");
            mainRootElement.appendChild(node);
            System.out.println("4");
            Element node1 = doc.createElement("status");
            System.out.println("5");
            node1.appendChild(doc.createTextNode("true"));
            System.out.println("6");
            node.appendChild(node1);
            System.out.println("7");
            mainRootElement.appendChild(node1);
            return convertDocumentToString(doc);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return "null";
    }

    @Override
    public String update_patient(String hospital_name, String ambulance_id, String p_name, String p_id, String gender, String blood_grp, String condition, String problem, String police_case, String is_enabled) {

        MongoCollection<org.bson.Document> collection = db.getCollection("patient_details");
        try {
            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);

            Element node = doc.createElement("status");


            UpdateResult ur = collection.updateOne(new org.bson.Document("p_id", p_id).append("hospital_name", hospital_name).append("ambulance_id",ambulance_id), new org.bson.Document("$set", new org.bson.Document("hospital_name", hospital_name).append("ambulance_id", ambulance_id).append("p_name",p_name).append("gender",gender).append("blood_grp",blood_grp).append("condition",condition).append("problem",problem).append("police_case",police_case).append("is_enabled",is_enabled)));


            if (ur.getModifiedCount() != 0) {
                node.appendChild(doc.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);
            }

            node.appendChild(doc.createTextNode("false"));
            mainRootElement.appendChild(node);
            return convertDocumentToString(doc);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        return "null";
    }

    Document doc1;

    @Override
    public String get_patient_details(String hospital_name, String ambulance_id, String p_id) {

        MongoCollection<org.bson.Document> collection = db.getCollection("patient_details");
        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            doc1 = icBuilder.newDocument();
            final Element mainRootElement = doc1.createElement("Response");
            doc1.appendChild(mainRootElement);



            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("hospital_name", hospital_name).append("ambulance_id",ambulance_id).append("p_id",p_id));


            patientfound = false;
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {
                    patientfound = true;

                    Element node = doc1.createElement("hospital_name");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("hospital_name"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("ambulance_id");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("ambulance_id"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("p_name");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("p_name"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("p_id");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("p_id"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("gender");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("gender"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("blood_grp");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("blood_grp"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("condition");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("condition"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("problem");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("problem"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("police_case");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("police_case"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("is_enabled");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("is_enabled"))));
                    mainRootElement.appendChild(node);

                }

            });


            if (patientfound) {
                Element node = doc1.createElement("status");
                node.appendChild(doc1.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc1);
            }

            Element node = doc1.createElement("status");
            node.appendChild(doc1.createTextNode("false"));
            mainRootElement.appendChild(node);
            return convertDocumentToString(doc1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        return "null";
    }

    @Override
    public String update_heartrate(String hospital_name, String ambulance_id, String p_id, String heartrate) {

        MongoCollection<org.bson.Document> collection = db.getCollection("heartrate_details");
        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            final Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);

            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("hospital_name", hospital_name).append("ambulance_id",ambulance_id).append("p_name",p_id));

            datafound=false;
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {
                    datafound=true;
                }
            });


            if(datafound==true)
            {
                UpdateResult ur = collection.updateOne(new org.bson.Document("p_id", p_id).append("hospital_name", hospital_name).append("ambulance_id",ambulance_id), new org.bson.Document("$set", new org.bson.Document("heartrate", heartrate)));
                if (ur.getModifiedCount() != 0) {
                    Element node = doc.createElement("status");
                    node.appendChild(doc.createTextNode("true"));
                    mainRootElement.appendChild(node);
                    return convertDocumentToString(doc);
                }
                Element node = doc.createElement("status");
                node.appendChild(doc.createTextNode("false"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);
            }else
            {
                org.bson.Document doc1 = new org.bson.Document("p_id", p_id)
                        .append("hospital_name", hospital_name)
                        .append("ambulance_id", ambulance_id)
                        .append("heartrate",heartrate);
                collection.insertOne(doc1);
                Element node = doc.createElement("status");
                node.appendChild(doc.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return "null";
    }

    @Override
    public String get_heartrate(String hospital_name, String ambulance_id, String p_id) {

        MongoCollection<org.bson.Document> collection = db.getCollection("heartrate_details");
        try {
            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            doc1 = icBuilder.newDocument();

            final Element mainRootElement = doc1.createElement("Response");
            doc1.appendChild(mainRootElement);

            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("hospital_name", hospital_name).append("ambulance_id", ambulance_id).append("p_id", p_id));

            datafound = false;
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {

                    datafound = true;


                    Element node = doc1.createElement("hospital_name");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("hospital_name"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("ambulance_id");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("ambulance_id"))));
                    mainRootElement.appendChild(node);


                    node = doc1.createElement("p_id");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("p_id"))));
                    mainRootElement.appendChild(node);


                    node = doc1.createElement("heartrate");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("heartrate"))));
                    mainRootElement.appendChild(node);

                }
            });


            if (datafound == true) {
                Element node = doc1.createElement("status");
                node.appendChild(doc1.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc1);
            }

            Element node = doc1.createElement("status");
            node.appendChild(doc1.createTextNode("false"));
            mainRootElement.appendChild(node);
            return convertDocumentToString(doc1);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "null";
    }


        private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }




  /*  private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
