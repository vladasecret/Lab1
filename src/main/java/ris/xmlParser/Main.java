package ris.xmlParser;

import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Map;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try{
            CLIOptions cliOptions = new CLIOptions(args);
            String inputPath = cliOptions.getInputPath();

            try(InputStream inputStream = getXMLInput(inputPath)){
                XMLParsedResult result = new XMLParser().parse(inputStream);
                printResult(result);
            }
        }
        catch (ParseException exc){
            LOGGER.error(exc.getMessage(), exc);
            CLIOptions.printHelp();
            System.exit(0);
        }
        catch (XMLStreamException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static InputStream getXMLInput(String inputPath) throws IOException {
            return new BZip2CompressorInputStream(new BufferedInputStream(
                    new FileInputStream(inputPath)));
    }

    public static void printResult(XMLParsedResult result){
        System.out.println("User changes: ");
        printSorted(result.userChanges);

        System.out.println("Name repetition: ");
        printSorted(result.nameRepetition);
    }

    public static void printSorted(Map<String, Integer> map){
        map.entrySet()
                .stream()
                .sorted((i1, i2) -> i2.getValue() - i1.getValue())
                .forEach(item -> System.out.printf("%s:\t%d%n", item.getKey(), item.getValue()));
    }






}