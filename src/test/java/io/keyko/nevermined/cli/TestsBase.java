package io.keyko.nevermined.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.nevermined.exceptions.DIDFormatException;
import io.keyko.nevermined.models.DID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TestsBase {

    protected static final Logger logger = LogManager.getLogger(TestsBase.class);
    protected static final Config config = ConfigFactory.load();

    public static final String TESTS_CONFIG_FOLDER = "src/test/resources/";
    public static final String TESTS_MAIN_CONFIG_FILE = "src/test/resources/application.conf";
    public static final String TESTS_NETWORK_FOLDER = "src/test/resources/";

    final String[] PUBLISH_DATASET_ARGS = {"assets", "publish-dataset",
            "--service", "access",
            "--title", "you can access to this",
            "--dateCreated", "2012-10-10T17:00:000Z",
            "--author", "aitor",
            "--license", "CC-BY",
            "--contentType", "text/txt",
            "--price", "5",
            "--urls", "https://raw.githubusercontent.com/nevermined-io/docs/master/docs/README.md"};

    final String[] PUBLISH_ALGORITHM_ARGS = {"assets", "publish-algorithm",
            "--title", "word count",
            "--dateCreated", "2012-10-10T17:00:000Z",
            "--author", "aitor",
            "--contentType", "text/text",
            "--language", "python",
            "--entrypoint", "python word_count.py",
            "--container", "python:3.8-alpine",
            "--url", "https://raw.githubusercontent.com/nevermined-io/sdk-py/examples/word_count.py"};

    String[] PUBLISH_WORKFLOW_ARGS = {};
    {
        try {
            PUBLISH_WORKFLOW_ARGS = new String[]{"assets", "publish-workflow",
                    "--title", "word count workflow",
                    "--dateCreated", "2012-11-11T17:00:000Z",
                    "--author", "aitor",
                    "--container", "python:3.8-alpine",
                    "--inputs", DID.builder().getDid() + ":" + DID.builder().getDid(),
                    "--transformation", DID.builder().getDid()
            };
        } catch (DIDFormatException e) {}
    }

    String[] PUBLISH_COMPUTE_ARGS = {"assets", "publish-dataset",
            "--service", "compute",
            "--title", "you can compute this",
            "--dateCreated", "2012-10-10T17:00:000Z",
            "--author", "aitor",
            "--license", "CC-BY",
            "--contentType", "text/txt",
            "--price", "5",
            "--urls", "https://github.com/robots.txt"
    };

}
