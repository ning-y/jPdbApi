package io.ningyuan.jPdbApi;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Pdb {
    private static final String PDB_DESCRIPTION_API_URL = "https://www.rcsb.org/pdb/rest/describePDB?structureId=%s";
    private static final String PDB_DOWNLOAD_URL = "https://files.rcsb.org/download/%s.pdb";

    private String structureId;
    private String title;

    public Pdb(String structureId) {
        this.structureId = structureId;
    }

    public void load() throws IOException {
        URL url = new URL(String.format(PDB_DESCRIPTION_API_URL, this.structureId));
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        StringBuffer response = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        InputStream inputStream = new ByteArrayInputStream(response.toString().getBytes());

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Node root = document.getFirstChild();
            NodeList children = root.getChildNodes();

            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    this.title = child.getAttributes().getNamedItem("title").getTextContent();
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException();
        }
    }

    public InputStream getInputStream() throws IOException {
        URL url = new URL(String.format(PDB_DOWNLOAD_URL, this.structureId));
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    public String getStructureId() {
        return this.structureId;
    }

    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return String.format("[%s] %s", this.structureId, this.title);
    }
}
