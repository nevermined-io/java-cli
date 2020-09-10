package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.DID;
import io.keyko.nevermined.models.asset.AssetMetadata;
import io.keyko.nevermined.models.service.Service;
import io.keyko.nevermined.models.service.metadata.Algorithm;
import io.keyko.nevermined.models.service.metadata.Workflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class AssetsBuilder {

    AssetMetadata assetMetadataBuilder(String title, String dateCreated, String author, String license, String price, List<String> urls, String contentType) throws ParseException {
        AssetMetadata metadata= AssetMetadata.builder();

        metadata.attributes.main.name= title;
        metadata.attributes.main.type= Service.AssetTypes.DATASET.toString();
        metadata.attributes.main.dateCreated= new SimpleDateFormat(DDO.DATE_PATTERN, Locale.ENGLISH).parse(dateCreated);
        metadata.attributes.main.author= author;
        metadata.attributes.main.license= license;
        metadata.attributes.main.price= price;
        ArrayList<AssetMetadata.File> files= new ArrayList<>();

        AtomicInteger fileIndex = new AtomicInteger(0);

        urls.stream().forEach( url -> {
            AssetMetadata.File file = new AssetMetadata.File();
            file.index= fileIndex.getAndIncrement();
            file.url= url;
            file.contentType= contentType;
            files.add(file);
        });
        metadata.attributes.main.files = files;

        return metadata;
    }

    AssetMetadata workflowMetadataBuilder(AssetMetadata metadata, String inputs, String transformation, String container) throws DIDFormatException {
        metadata.attributes.main.type= Service.AssetTypes.WORKFLOW.toString();
        metadata.attributes.main.workflow = new Workflow();

        Workflow.Stage stage = new Workflow.Stage();
        stage.index = 0;
        stage.stageType = "Transformation";
        stage.input = Workflow.Stage.parseInputs(inputs);
        stage.transformation = new Workflow.Transformation();
        stage.transformation.id = new DID(transformation);

        stage.requirements = new Workflow.Requirements();
        stage.requirements.container = Workflow.Container.parseString(container);
        metadata.attributes.main.workflow.stages = Arrays.asList(stage);

        return metadata;
    }


    AssetMetadata algorithmMetadataBuilder(AssetMetadata metadata, String language, String entrypoint, String container)    {
        metadata.attributes.main.type= Service.AssetTypes.ALGORITHM.toString();
        metadata.attributes.main.algorithm = new Algorithm();
        metadata.attributes.main.algorithm.language = language;
        metadata.attributes.main.algorithm.entrypoint = entrypoint;
        metadata.attributes.main.algorithm.requirements = new Algorithm.Requirements();
        metadata.attributes.main.algorithm.requirements.container = Workflow.Container.parseString(container);

        return metadata;
    }

}
