package com.voodoo.webservers.service;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author chamerling
 * 
 */
public class ServicesImpl implements Services {

    MongoClientURI connectionString = new MongoClientURI("mongodb://voodoo:722446@ds057862.mlab.com:57862/prms");
    MongoClient mongoClient = new MongoClient(connectionString);
    MongoDatabase db = mongoClient.getDatabase(connectionString.getDatabase());

    int is_valid=0;
    String pwd;



	@Override
    public String validate_user(String user_name,String password) {

        MongoCollection<org.bson.Document> collection = db.getCollection("credentials");

		try {
            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
           // Element mainRootElement = doc.createElement("Response");
            //doc.appendChild(mainRootElement);

            Element node = doc.createElement("message");

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
                doc.appendChild(node);
                return convertDocumentToString(doc);

            }
            else
            {
                node.appendChild(doc.createTextNode("false"));
                doc.appendChild(node);
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


		return "null";
	}

    @Override
    public String register_user(String User_Name, String Password, String Hospital_name, String Type_of_User) {
        MongoCollection<org.bson.Document> collection = db.getCollection("credentials");
        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element node = doc.createElement("message");



            org.bson.Document doc1 = new org.bson.Document("user_name", User_Name)
                    .append("password", Password)
                    .append("hospital_name", Hospital_name)
                    .append("type_of_user", Type_of_User)
                    .append("ambulance_id", "null");
            collection.insertOne(doc1);
            node.appendChild(doc.createTextNode("true"));
            doc.appendChild(node);
            return convertDocumentToString(doc);
        }
        catch(Exception e)
        {
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

    private static Document convertStringToDocument(String xmlStr) {
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
    }

}
