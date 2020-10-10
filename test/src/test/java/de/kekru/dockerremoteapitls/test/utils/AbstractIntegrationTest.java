package de.kekru.dockerremoteapitls.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractIntegrationTest {

  protected final Logger LOG = LoggerFactory.getLogger(getClass());
  public static final String REMOTE_API_CONNECTION_STRING = "tcp://abc.127.0.0.1.nip.io:30129";

  @Rule
  public TemporaryFolder folder= new TemporaryFolder();
  protected ShellExecutor shellExecutor;
  protected File certsDir;
  protected File certsDirClient;


  @Before
  public void initTests() throws IOException {
    shellExecutor = new ShellExecutor();
    certsDir = new File("target/certs-integr-test");
    certsDirClient = new File(certsDir + "/client");
    if (certsDir.exists()) {
      FileUtils.cleanDirectory(certsDir);
    }
  }

  protected String runDockerCompose(String composeCommand) {

    Map<String, String> env = new HashMap<>();
    env.put("COMPOSE_DOCKER_CLI_BUILD", "1");
    env.put("DOCKER_BUILDKIT", "1");
    env.put("COMPOSE_PROJECT_NAME","remote-api-integr-test");
    //shellExecutor.execute("docker-compose build --progress=plain remote-api", env);
    return shellExecutor.execute("docker-compose " + composeCommand, env);
  }

  protected void copyGeneratedClientCertsToLocal() {
    String remoteApiContainerId = runDockerCompose("ps -q remote-api");
    shellExecutor.execute("docker cp " + remoteApiContainerId + ":/data/certs/ "
        + certsDir.getAbsolutePath() + "/");

    assertThat(new File(certsDir + "/ca-cert.pem")).exists();
    assertThat(new File(certsDir + "/ca-key.pem")).exists();
    assertThat(new File(certsDir + "/server-cert.pem")).exists();
    assertThat(new File(certsDir + "/server-key.pem")).exists();
    assertThat(new File(certsDirClient + "/ca.pem")).exists();
    assertThat(new File(certsDirClient + "/cert.pem")).exists();
    assertThat(new File(certsDirClient + "/key.pem")).exists();
  }
}
