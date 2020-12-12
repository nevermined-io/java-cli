package io.keyko.nevermined.cli;

import io.keyko.nevermined.NeverminedCLI;
import io.keyko.nevermined.models.contracts.ProvenanceEntry;
import io.keyko.nevermined.models.contracts.ProvenanceEvent;
import picocli.CommandLine;

import java.io.PrintWriter;

import static io.keyko.nevermined.cli.helpers.CommandLineHelper.isValidSignature;

public class NeverminedBaseCommand {

    public static final String COLOR_PREFIX = "@|";
    public static final String COLOR_SUFFIX = "|@ ";
    public static final String ERROR_PREFIX = COLOR_PREFIX + "bold,red Error:" + COLOR_SUFFIX + "\n";
    public static final String SUCCESS_INDICATOR = COLOR_PREFIX + "bold,green [✔] " + COLOR_SUFFIX;
    public static final String ERROR_INDICATOR = COLOR_PREFIX + "bold,red [✘] " + COLOR_SUFFIX;
    public static final String QUESTION_INDICATOR = COLOR_PREFIX + "bold,yellow [?] " + COLOR_SUFFIX;
    public static final String SUCCESS_MESSAGE = COLOR_PREFIX + "bold,green Success [✔] " + COLOR_SUFFIX;
    public static final String ERROR_MESSAGE = COLOR_PREFIX + "bold,red Error [✘] " + COLOR_SUFFIX;
    public static final String HEADER_PREFIX = COLOR_PREFIX + "bold,blue ";
    public static final String SUBHEADER_PREFIX = COLOR_PREFIX + "bold,yellow ";
    public static final String ITEM_PREFIX = COLOR_PREFIX + "yellow ";


    @CommandLine.ParentCommand
    public NeverminedCLI cli;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    public void print(String line)   {
        if (!line.contains(COLOR_PREFIX))
            getOut().print(line);
        else
            getOut().print(
                CommandLine.Help.Ansi.AUTO.string(line)
            );
        getOut().flush();
    }

    public void println(String line) {
        print(line + "\n");
    }

    public void println() {
        print("\n");
    }

    public void printHeader(String header)  {
        println("\n" + HEADER_PREFIX + header + COLOR_SUFFIX + "\n");
    }

    public void printSubHeader(String text)  {
        println("\t" + SUBHEADER_PREFIX + text + COLOR_SUFFIX + "\n");
    }

    public void printItem(String text)  {
        println(getItem(text));
    }

    public void printKeyValue(String key, String value)  {
        println(getItem(key) + ":" + value);
    }

    public String getItem(String text)  {
        return ITEM_PREFIX + text + COLOR_SUFFIX;
    }

    public void printError(String line)   {
        getErr().println(
                CommandLine.Help.Ansi.AUTO.string(ERROR_PREFIX + line)
        );
    }

    public void printSuccess()  {
        println(SUCCESS_MESSAGE);
    }

    public void printStatusRunning(String text) {
        print(COLOR_PREFIX + "yellow  " + text + COLOR_SUFFIX);
    }

    public void printStatusSucceeded(String text) {
        print(COLOR_PREFIX + "green  " + text + COLOR_SUFFIX);
    }

    public void printStatusFailed(String text) {
        print(COLOR_PREFIX + "red  " + text + COLOR_SUFFIX);
    }

    public void print(ProvenanceEvent event)    {

        println("\tW3C Method:      \t" + event.method.name());
        println("\tProvenance ID:   \t" + event.provId);
        println("\tDID:             \t" + event.did.getDid());
        println("\tRelated DID:     \t" + event.relatedDid.getDid());
        println("\tAgent:           \t" + event.agentId);
        println("\tActivity:        \t" + event.activityId);
        println("\tAgent Involved:  \t" + event.agentInvolvedId);
        println("\tBlock number:    \t" + event.blockNumberUpdated.toString());
        println("\tAttributes:      \t" + event.attributes);
    }

    public void print(String provenanceId, ProvenanceEntry entry, boolean skipSignatureValidation)    {

        println("\tProvenance ID:   \t" + provenanceId);
        println("\tDID:             \t" + entry.did.getDid());
        println("\tRelated DID:     \t" + entry.relatedDid.getDid());
        println("\tCreated by:      \t" + entry.createdBy);
        println("\tAgent:           \t" + entry.agentId);
        println("\tAgent Involved:  \t" + entry.agentInvolvedId);
        println("\tActivity:        \t" + entry.activityId);
        println("\tW3C PROV Method: \t" + entry.method.name());

        if (!skipSignatureValidation) {
            final boolean isValidSignature = isValidSignature(provenanceId, entry.signature, entry.agentId);
            printSignatureValidation(isValidSignature, entry.signature);
        }
        println("\tBlock number:    \t" + entry.blockNumberUpdated.toString());
    }

    public void printSignatureValidation(boolean isValidSignature, String signature)    {
        if (null == signature || signature.length() <=128) {
            print("\tSignature:       \t" + NeverminedBaseCommand.QUESTION_INDICATOR);
            println("No signature provided");
        } else if (!isValidSignature) {
            print("\tSignature:       \t" + NeverminedBaseCommand.ERROR_INDICATOR);
            println("The signature couldn't be validated");
        } else {
            print("\tSignature:       \t" + NeverminedBaseCommand.SUCCESS_INDICATOR);
            println("Validated - " + signature);
        }
    }

    public PrintWriter getOut() {
        return spec.commandLine().getOut();
    }

    public PrintWriter getErr() {
        return spec.commandLine().getErr();
    }

}
