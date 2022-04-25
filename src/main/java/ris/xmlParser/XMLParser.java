package ris.xmlParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

import java.util.ArrayDeque;

import java.util.Deque;


public class XMLParser {
    public static final Logger LOGGER = LoggerFactory.getLogger(XMLParser.class);

    private static final String NODE_NAME = "node";
    private static final String TAG_NAME = "tag";

    private final QName userAttr = new QName("user");
    private final QName keyAttr = new QName("k");
    private final QName valueAttr = new QName("v");

    private final Deque<StartElement> parents = new ArrayDeque<>();

    public XMLParsedResult parse(InputStream inputStream) throws XMLStreamException {
        XMLEventReader reader = XMLInputFactory.newDefaultFactory().createXMLEventReader(inputStream);
        XMLParsedResult result = new XMLParsedResult();
        while (reader.hasNext()){
            XMLEvent item = reader.nextEvent();
            if (item.isStartElement()){
                StartElement startElement = item.asStartElement();
                if (isNode(startElement)){
                    processNode(startElement, result);
                }
                else if (isTag(startElement)){
                    processTag(parents.getLast(), startElement, result);
                }
                parents.addLast(startElement);

            }
            else if (item.isEndElement()){
                parents.removeLast();
            }
        }
        return result;
    }

    private void processNode(StartElement element, XMLParsedResult result){
        String user = element.getAttributeByName(userAttr).getValue();
        if (user == null)
            return;

        Integer value = result.userChanges.getOrDefault(user, 1);;
        result.userChanges.put(user, value);
    }

    private void processTag(StartElement parent, StartElement element, XMLParsedResult result){
        if (!isNode(parent)){
            return;
        }
        String k = element.getAttributeByName(keyAttr).getValue();
        if ("name".equals(k)){
            String name = element.getAttributeByName(valueAttr).getValue();
            if (name == null){
                return;
            }
            Integer value = result.nameRepetition.getOrDefault(name, 1);
            result.nameRepetition.put(name, value);
        }
    }
    private boolean isNode(StartElement startElement){
        return startElement != null && startElement.getName().getLocalPart().equals(NODE_NAME);
    }

    private boolean isTag(StartElement startElement){
        return startElement != null && startElement.getName().getLocalPart().equals(TAG_NAME);
    }
}
