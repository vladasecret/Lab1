package ris.xmlParser;

import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ris.xmlParser.parser.XMLParsedResult;
import ris.xmlParser.parser.XMLParser;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.sql.SQLException;
import java.util.Map;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try{
            CLIOptions cliOptions = new CLIOptions(args);
            String inputPath = cliOptions.getInputPath();

            try(NodeReader reader = new NodeReader(getXMLInput(inputPath))){
                XMLParsedResult result = new XMLParser().parse(reader);
                printResult(result);
            }
        }
        catch (ParseException exc){
            LOGGER.error(exc.getMessage(), exc);
            CLIOptions.printHelp();
        }
        catch (XMLStreamException | IOException | JAXBException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static InputStream getXMLInput(String inputPath) throws IOException {
            return new BZip2CompressorInputStream(new BufferedInputStream(
                    new FileInputStream(inputPath)));
    }

    public static void printResult(XMLParsedResult result){
        System.out.println("-------------------User changes-------------------");
        printSorted(result.userChanges);

        System.out.println("-------------------Name repetition-------------------");
        printSorted(result.nameRepetition);
    }

    public static void printSorted(Map<String, Integer> map){
        map.entrySet()
                .stream()
                .sorted((i1, i2) -> i2.getValue() - i1.getValue())
                .forEach(item -> System.out.printf("%s:\t%d%n", item.getKey(), item.getValue()));
    }






}