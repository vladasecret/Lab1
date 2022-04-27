package ris.xmlParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ris.generated.osmSchema.Node;
import ris.generated.osmSchema.Tag;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public class XMLParser {
    public static final Logger LOGGER = LoggerFactory.getLogger(XMLParser.class);

    private final XMLInputFactory xmlFactory = XMLInputFactory.newDefaultFactory();

    public XMLParsedResult parse(NodeReader nodeReader) throws XMLStreamException, JAXBException {
        XMLParsedResult result = new XMLParsedResult();
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
