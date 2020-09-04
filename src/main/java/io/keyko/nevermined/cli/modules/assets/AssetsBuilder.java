package io.keyko.nevermined.cli.modules.assets;

import io.keyko.nevermined.models.DDO;
import io.keyko.nevermined.models.asset.AssetMetadata;
import io.keyko.nevermined.models.service.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

}
