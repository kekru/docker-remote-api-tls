package de.kekru.dockerremoteapitls.test;

import de.kekru.dockerremoteapitls.test.utils.ShellExecutor;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTest {

  private static final Logger LOG_REMOTE_API = LoggerFactory.getLogger("Remote Api Container");
  private static final Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);
  private static final String REMOTE_API_CONNECTION_STRING = "tcp://abc.127.0.0.1.nip.io:30129";


  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  private ShellExecutor shellExecutor;


  @Before
  public void init() throws IOException {
    shellExecutor = new ShellExecutor();
    shellExecutor.execute("docker-compose down");

    File certsDir = new File("certs-integr-test");
    if (certsDir.exists()) {
      FileUtils.deleteDirectory(certsDir);
    }
  }

  @Test
  public void test() throws Exception {

    Map<String, String> env = new HashMap<>();
    env.put("COMPOSE_DOCKER_CLI_BUILD", "1");
    env.put("DOCKER_BUILDKIT", "1");
    //shellExecutor.execute("docker-compose build --progress=plain remote-api", env);
    shellExecutor.execute("docker-compose up -d remote-api", env);

    Thread.sleep(5000);

    env = new HashMap<>();
    env.put("DOCKER_HOST", REMOTE_API_CONNECTION_STRING);
    env.put("DOCKER_TLS_VERIFY", "1");
    env.put("DOCKER_CERT_PATH", new File("certs-integr-test/client").getAbsolutePath());
    shellExecutor.execute("docker ps", env);
  }

}
