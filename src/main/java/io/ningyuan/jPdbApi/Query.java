package io.ningyuan.jPdbApi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Query {
    public static final int KEYWORD_QUERY = 0;
    private static final String API_URL = "http://www.rcsb.org/pdb/rest/search";
    private int type;
    private String keywords;
    private Document xml;

    public Query(int type, String keywords) throws ParserConfigurationException {
        this.type = type;
        this.keywords = keywords;

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        this.xml = documentBuilder.newDocument();

        Element root = xml.createElement("orgPdbQuery");
        xml.appendChild(root);

        switch (this.type) {
            case KEYWORD_QUERY:
                buildAdvancedKeywordQuery();
                break;
        }
    }

    public List<String> execute() throws IOException {
        String xmlEncoded = URLEncoder.encode(this.toString(), "UTF-8");
        InputStream inputStream = executePost(xmlEncoded);
        List<String> resultIds = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            resultIds.add(line);
        }
        reader.close();

        return resultIds;
    }

    public String toString() {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(this.xml), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            return null;
        }
    }

    private void buildAdvancedKeywordQuery() {
        Element queryType = xml.createElement("queryType");
        Text queryTypeText = this.xml.createTextNode("org.pdb.query.simple.AdvancedKeywordQuery");
        queryType.appendChild(queryTypeText);

        Element keywords = xml.createElement("keywords");
        Text keywordsText = this.xml.createTextNode(this.keywords);
        keywords.appendChild(keywordsText);

        this.xml.getFirstChild().appendChild(queryType);
        this.xml.getFirstChild().appendChild(keywords);
    }

    private static InputStream executePost(String data) throws IOException {
        URL url = new URL(API_URL);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

        writer.write(data);
        writer.flush();

        return connection.getInputStream();
    }
}