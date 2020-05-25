package io.keyko.nevermined.cli.modules.utils;

import io.keyko.nevermined.cli.UtilsCommand;
import io.keyko.nevermined.cli.helpers.CommandLineHelper;
import io.keyko.nevermined.cli.models.CommandResult;
import io.keyko.nevermined.cli.models.exceptions.CLIException;
import io.keyko.nevermined.models.asset.AssetMetadata;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.json.JSONObject;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "info",
        description = "Retrieve metadata of an external resource")
public class UtilsInfo implements Callable {

    @CommandLine.ParentCommand
    UtilsCommand command;

    @CommandLine.Mixin
    io.keyko.nevermined.cli.helpers.Logger logger;

    @CommandLine.Parameters(index = "0")
    String url= null;

    CommandResult info() {

        AssetMetadata.File file;

        try {
            command.printHeader("Querying resource:");
            command.printSubHeader(url);

            command.cli.progressBar.start();

            Header[] responseHeaders= getHttpHeadHeaders(url);

            if (responseHeaders.length == 0)
                throw new CLIException("Unable to retrieve information from URL " + url);

            file= convertResponseHeadersToAssetsMetadataFile(responseHeaders, url);

            command.println();
            command.println(CommandLineHelper.prettyJson(metadataFileToJson(file)));


        } catch (Exception e) {
            command.printError("Unable to retrieve information from URL " + url);
            logger.debug(e.getMessage());
            return CommandResult.errorResult();
        } finally {
            command.cli.progressBar.doStop();
        }

        return CommandResult.successResult();

    }

    public static AssetMetadata.File convertResponseHeadersToAssetsMetadataFile(Header[] headers, String url)   {
        AssetMetadata.File file= new AssetMetadata.File();
        file.index= 0;
        file.url= url;

        if (null != getHeaderIfExists(headers, "Content-Type"))
            file.contentType= getHeaderIfExists(headers, "Content-Type");

        if (null != getHeaderIfExists(headers, "Content-Length"))
            file.contentLength= getHeaderIfExists(headers, "Content-Length");

        if (null != getHeaderIfExists(headers, "etag"))
            file.contentType= getHeaderIfExists(headers, "etag").replace("\"", "");


        return file;
    }

    private static String getHeaderIfExists(Header[] headers, String name) {
        for (Header h: headers) {
            if (h.getName().equalsIgnoreCase(name))
                return h.getValue();
        }
        return "";
    }

    public static Header[] getHttpHeadHeaders(String url)  {
        Header[] responseHeaders= {};
        HttpClient client= new HttpClient();

        try {
            HeadMethod headMethod = new HeadMethod(url);
            headMethod.setRequestHeader(new Header("User-Agent", "curl/7.58.0"));
            headMethod.setRequestHeader(new Header("Accept", "*/*"));
            headMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
            client.executeMethod(headMethod);
            responseHeaders = headMethod.getResponseHeaders();
        } catch (IOException ex)  {
        }
        return responseHeaders;

    }

    public static String metadataFileToJson(AssetMetadata.File file)    {
        JSONObject json= new JSONObject();
        json.put("index", file.index);
        json.put("url", file.url);
        json.put("contentLength", file.contentLength);

        if (null != file.contentType)
            json.put("contentType", file.contentType);
        if (null != file.checksum)
            json.put("checksum", file.checksum);

        return json.toString();
    }

    @Override
    public Object call() throws CLIException {
        return info();
    }
}
