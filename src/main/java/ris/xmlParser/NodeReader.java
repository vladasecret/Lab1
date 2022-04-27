package ris.xmlParser;

import ris.generated.osmSchema.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Closeable;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class NodeReader implements Closeable {
    private static final String NODE_NAME = "node";

    private final XMLEventReader reader;
    private final Unmarshaller unmarshaller;

    public NodeReader(InputStream inputStream) throws XMLStreamException, JAXBException {
        reader = XMLInputFactory.newDefaultFactory().createXMLEventReader(inputStream);
        unmarshaller = JAXBContext.newInstance(Node.class).createUnmarshaller();
    }

    public boolean hasNext() throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent item = reader.peek();
            if (item.isStartElement()) {
                StartElement startElement = item.asStartElement();
                if (isNode(startElement))
                    return true;
            }
            reader.nextEvent();
        }
        return false;
    }

    public Node nextNode() throws XMLStreamException, JAXBException {
        if (!hasNext()){
            throw new NoSuchElementException();
        }
        Node node = (Node) unmarshaller.unmarshal(reader);
        reader.nextEvent();
        return node;
    }

    private boolean isNode(StartElement startElement){
        return startElement != null && startElement.getName().getLocalPart().equals(NODE_NAME);
    }


    @Override
    public void close() {
        try {
            reader.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
