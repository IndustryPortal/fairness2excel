import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import converters.FairExcelConverter;
import converters.QuestionsExcelConverter;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {

    public static final String SERVICE_URL = "http://services.stageportal.lirmm.fr/fairness/v2/";

    public static void main(String[] args) {


        final Options options = getOptions();

        try {
            CommandLineParser clp = new BasicParser();
            CommandLine call = clp.parse(options, args);

            String portal = call.getOptionValue("r");
            String ontologies =  call.getOptionValue("o");
            String fileName = call.hasOption("-f") ? call.getOptionValue("f") : "";


            if (portal != null && !portal.trim().isEmpty()) {
                portal = portal.trim();

                if (ontologies != null && !ontologies.trim().isEmpty()) {
                        ontologies = ontologies.toLowerCase(Locale.ROOT).equals("all") ? "all" : ontologies.toUpperCase(Locale.ROOT).trim();
                }


                if(fileName == null || fileName.trim().isEmpty()){
                    fileName = "Results/FAIRnessResults-" + portal + ".xlsx";
                }

                String uri = String.format(SERVICE_URL+"?portal=%s&ontologies=%s", portal , ontologies);
                JsonObject response = getJsonFromFairService(uri);


                System.out.println("Get Fair score of : " + ontologies);
                System.out.println("Use portal : " + portal);
                System.out.println("HTTP call : " + uri);

                if (!isResponseOK(response)){
                    System.out.println("Error in fairness service : " + getErrorMessage(response));
                }else if( ontologies == null || ontologies.isEmpty()) {
                    System.out.println("Error in option 'ontologies' : it's empty" );
                }else if (portal == null || portal.isEmpty()) {
                    System.out.println("Error in option 'portal' : it's empty");
                }else {
                    FairExcelConverter excelConverter = new FairExcelConverter(response);
                    excelConverter.toExcel( fileName, "FAIRness evaluation"  , true);
                    QuestionsExcelConverter questionsExcelConverter = new QuestionsExcelConverter(response);
                    questionsExcelConverter.toExcel(fileName , "Fairness questions evaluation ");
                    System.out.println("The file " +  fileName +  " was created ");
                }
            }
            else {
                System.out.println("portal error");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("ERROR : " + e.getMessage());
        }

    }

        private static String getErrorMessage(JsonObject response) {
        return response.getAsJsonObject("status").get("message").getAsString();
    }

    private static boolean isResponseOK(JsonObject response) {
        return response!=null && response.getAsJsonObject("status").get("success").getAsBoolean();
    }

    private static JsonObject getJsonFromFairService(String URI) throws IOException {
        JsonObject out ;
        URL url = new URL(URI);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        Reader reader = new InputStreamReader(is);
        out = new GsonBuilder().create().fromJson(reader , JsonObject.class);
        return out;
    }

    private static Options getOptions(){
        final Options options = new Options();

        Option portalInstanceNameOpt = new Option("r", "repository-name", true,
                "Name of the ontology repository (agroportal, bioportal, stageportal).");
        portalInstanceNameOpt.setRequired(true);
        Option ontologyAcronymOpt = new Option("o", "ontology-acronyms", true,
                "Acronyms of the ontologies to evaluate (comma separated) or all.");
        ontologyAcronymOpt.setRequired(true);
        ontologyAcronymOpt.setValueSeparator(',');

        Option output = new Option("f", "output-file", true, "File output path");
        output.setRequired(false);

        options.addOption(portalInstanceNameOpt);
        options.addOption(ontologyAcronymOpt);
        options.addOption(output);
        return options;
    }
}
