package ris.xmlParser.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ris.generated.osmSchema.Node;
import ris.generated.osmSchema.Tag;
import ris.xmlParser.NodeReader;
import ris.xmlParser.db.ConnectionManager;
import ris.xmlParser.db.DatabaseInitializer;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class XMLParser {
    public static final Logger LOGGER = LoggerFactory.getLogger(XMLParser.class);

    //private final XMLInputFactory xmlFactory = XMLInputFactory.newDefaultFactory();

    private Connection connection;

    public XMLParsedResult parse(NodeReader nodeReader) throws XMLStreamException, JAXBException, SQLException {
        XMLParsedResult result = new XMLParsedResult();
        try{
            connection = ConnectionManager.getConnection();
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.dropTables();
            initializer.createTables();
            connection.commit();
        } catch (SQLException  e) {
            if (connection != null && !connection.isClosed()){
                connection.rollback();
                connection.close();
            }
            LOGGER.error(e. getMessage());
            throw e;
        }

        while (nodeReader.hasNext()){
            Node node = nodeReader.nextNode();
            processNode(node, result);
        }
        return result;
    }

    private void processNode(Node node, XMLParsedResult result){
        String user = node.getUser();
        if (user == null)
            return;
        Integer value = result.userChanges.getOrDefault(user, 0) ;
        result.userChanges.put(user, value + 1);
        for (Tag tag: node.getTag()) {
            processTag(tag, result);
        }
    }

    private void processTag(Tag tag, XMLParsedResult result){
        if ("name".equals(tag.getK())){
            String name = tag.getV();
            if (name == null){
                return;
            }
            Integer value = result.nameRepetition.getOrDefault(name, 0) ;
            result.nameRepetition.put(name, value + 1);
        }
    }
}
