package ris.xmlParser;

import org.apache.commons.cli.*;

public class CLIOptions {

    private static String INPUT_FILE_OPTION = "input";
    private static String INPUT_FILE_SHORT_OPTION = "i";


    private final CommandLine commandLine;
    private static Options buildOptions(){
        var inputOption = Option.builder(INPUT_FILE_SHORT_OPTION)
                .hasArg(true)
                .longOpt(INPUT_FILE_OPTION)
                .desc("input xml file in bzip2 format archive")
                .required();
        Options options = new Options();
        options.addOption(inputOption.build());
        return options;
    }

    public CLIOptions(String[] args) throws ParseException {
        Options options = buildOptions();
        DefaultParser parser = new DefaultParser();
        commandLine = parser.parse(options, args, true);
    }

    public String getInputPath(){
        return commandLine.getOptionValue(INPUT_FILE_OPTION);
    }

    public static void printHelp(){
        new HelpFormatter().printHelp("Extract Open Street Map data and compute stats", buildOptions());
    }
}
