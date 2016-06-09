package com.voodoo.webservers.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    String tou,aid,hname;

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




            is_valid=0;
            pwd=null;
            tou=null;
            aid=null;
            hname=null;
            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("user_name", user_name));
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {
                    is_valid=1;
                    pwd = String.valueOf(document.get("password"));
                    tou = String.valueOf(document.get("type_of_user"));
                    aid = String.valueOf(document.get("ambulance_id"));
                    hname = String.valueOf(document.get("hospital_name"));
                }

            });



            if(is_valid==1 && pwd.equals(password)){
                Element node1 = doc.createElement("type_of_user");
                node1.appendChild(doc.createTextNode(tou));
                mainRootElement.appendChild(node1);
                if(tou.equals("Ambulance Staff"))
                {

                    Element node2 = doc.createElement("ambulance_id");
                    node2.appendChild(doc.createTextNode(aid));
                    mainRootElement.appendChild(node2);
                }

                Element node3 =doc.createElement("hospital_name");
                node3.appendChild(doc.createTextNode(hname));
                mainRootElement.appendChild(node3);

                Element node = doc.createElement("status");
                node.appendChild(doc.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);

            }
            else
            {
                Element node = doc.createElement("status");
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


		return "null";
	}

    @Override
    public String register_user(String User_Name, String Password, String Hospital_name, String Type_of_User) {
        MongoCollection<org.bson.Document> collection = db.getCollection("credentials");
        try {
            is_valid=0;
            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("user_name", User_Name));
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {
                    is_valid=1;
                }

            });




            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);


            if(is_valid==1)
            {
                Element node = doc.createElement("status");
                node.appendChild(doc.createTextNode("false"));
                mainRootElement.appendChild(node);

                return convertDocumentToString(doc);
            }

            org.bson.Document doc1;
            if(Type_of_User.equals("Doctor")) {
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
            if(p_name.equals("null"))
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
            Element node = doc.createElement("P_id");
            node.appendChild(doc.createTextNode(p_id));
            mainRootElement.appendChild(node);

            Element node1 = doc.createElement("status");
            node1.appendChild(doc.createTextNode("true"));
            node.appendChild(node1);
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

    String hr;
    String tm;
    int cnt;


    @Override
    public String update_heartrate(String hospital_name, String ambulance_id, String p_id, String heartrate,String timestamp) {

        MongoCollection<org.bson.Document> collection = db.getCollection("heartrate_details");
        String err=null;
        try {

            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            final Element mainRootElement = doc.createElement("Response");
            doc.appendChild(mainRootElement);

            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("hospital_name", hospital_name).append("ambulance_id", ambulance_id).append("p_id",p_id));

            hr="null";
            tm="null";
            cnt=0;
            datafound=false;
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {

                    datafound=true;
                    hr=String.valueOf(document.get("heartrate"));
                    tm=String.valueOf(document.get("timestamp"));
                    cnt=Integer.parseInt(String.valueOf(document.get("count")));
                }
            });



            if(datafound==true)
            {
                if(cnt<10)
                {
                    heartrate=hr+heartrate;
                    timestamp=tm+timestamp;
                    cnt++;
                    UpdateResult ur = collection.updateOne(new org.bson.Document("p_id", p_id).append("hospital_name", hospital_name).append("ambulance_id",ambulance_id), new org.bson.Document("$set", new org.bson.Document("heartrate", heartrate+";").append("timestamp",timestamp+";").append("count",cnt)));
                    if (ur.getModifiedCount() != 0) {
                        Element node = doc.createElement("status");
                        node.appendChild(doc.createTextNode("true"));
                        mainRootElement.appendChild(node);
                        return convertDocumentToString(doc);
                    }
                }
                else
                {
                    int cnt1=cnt;

                    cnt1=cnt1%10;
                    String[] hr1=hr.split(";");
                    String[] tm1=tm.split(";");
                    hr1[cnt1]=heartrate;
                    tm1[cnt1]=timestamp;
                    hr="";
                    tm="";
                    for(int i=0;i<hr1.length;i++)
                    {
                        hr=hr+hr1[i]+";";
                        tm=tm+tm1[i]+";";
                    }
                    cnt++;
                    UpdateResult ur = collection.updateOne(new org.bson.Document("p_id", p_id).append("hospital_name", hospital_name).append("ambulance_id",ambulance_id), new org.bson.Document("$set", new org.bson.Document("heartrate", hr).append("timestamp",tm).append("count", cnt)));
                    if (ur.getModifiedCount() != 0) {
                        Element node = doc.createElement("status");
                        node.appendChild(doc.createTextNode("true"));
                        mainRootElement.appendChild(node);
                        return convertDocumentToString(doc);
                    }
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
                        .append("heartrate",heartrate+";")
                        .append("timestamp",timestamp+";")
                        .append("count","1");
                collection.insertOne(doc1);
                Element node = doc.createElement("status");
                node.appendChild(doc.createTextNode("true"));
                mainRootElement.appendChild(node);
                return convertDocumentToString(doc);
            }

        }
        catch(Exception e)
        {
            err=String.valueOf(e);
        }

        return err;
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

                    node = doc1.createElement("timestamp");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("timestamp"))));
                    mainRootElement.appendChild(node);

                    node = doc1.createElement("count");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("count"))));
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

    int count=0;
    @WebMethod
    public String get_Patient_List(@WebParam(name = "hospital_name") String hospital_name) {

        MongoCollection<org.bson.Document> collection = db.getCollection("patient_details");
        try {
            DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder icBuilder;
            icBuilder = icFactory.newDocumentBuilder();
            doc1 = icBuilder.newDocument();

            final Element mainRootElement = doc1.createElement("Response");
            doc1.appendChild(mainRootElement);

            FindIterable<org.bson.Document> iterable = collection.find(new org.bson.Document("hospital_name", hospital_name).append("is_enabled", "Yes"));

            datafound = false;
            count=0;
            iterable.forEach(new Block<org.bson.Document>() {
                @Override
                public void apply(final org.bson.Document document) {

                    datafound = true;

                    Element PatientElement = doc1.createElement("Patient_"+count);
                    mainRootElement.appendChild(PatientElement);

                    Element node = doc1.createElement("hospital_name");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("hospital_name"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("ambulance_id");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("ambulance_id"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("p_name");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("p_name"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("p_id");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("p_id"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("gender");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("gender"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("blood_grp");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("blood_grp"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("condition");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("condition"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("problem");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("problem"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("police_case");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("police_case"))));
                    PatientElement.appendChild(node);

                    node = doc1.createElement("is_enabled");
                    node.appendChild(doc1.createTextNode(String.valueOf(document.get("is_enabled"))));
                    PatientElement.appendChild(node);
                    count++;

                }
            });


            if (datafound == true) {
                Element node = doc1.createElement("status");
                node.appendChild(doc1.createTextNode("true"));
                mainRootElement.appendChild(node);
                Element node1 = doc1.createElement("count");
                node1.appendChild(doc1.createTextNode(String.valueOf(count)));
                mainRootElement.appendChild(node1);
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

}
