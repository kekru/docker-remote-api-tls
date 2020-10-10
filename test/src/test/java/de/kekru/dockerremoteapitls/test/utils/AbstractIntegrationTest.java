package de.kekru.dockerremoteapitls.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractIntegrationTest {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractIntegrationTest.class);
  protected static final String REMOTE_API_HOST = "abc.127.0.0.1.nip.io";
  protected static final int REMOTE_API_PORT = 30129;

  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();
  protected static ShellExecutor shellExecutor = new ShellExecutor();
  protected static File certsDir;
  protected static File certsDirClient;
  protected static File remoteApiEnvFile;


  @BeforeClass
  public static void initTests() throws IOException {
    remoteApiEnvFile = new File("target/integr-test/remote-api.env");
    certsDir = new File("target/integr-test/certs");
    certsDirClient = new File(certsDir + "/client");
    if (certsDir.exists()) {
      FileUtils.cleanDirectory(certsDir);
    }
    certsDir.mkdirs();

    runDockerCompose("stop remote-api");
  }

  protected static void startRemoteApiContainer(String... envEntries) {
    writeEnvFile(Arrays.asList(envEntries));
    runDockerCompose("up -d --force-recreate remote-api");
    waitForHealthy();
    copyGeneratedClientCertsToLocal();
  }

  private static void writeEnvFile(List<String> envEntries) {
    if (remoteApiEnvFile.exists()) {
      remoteApiEnvFile.delete();
    }

    try (PrintWriter printWriter = new PrintWriter(new FileWriter(remoteApiEnvFile))) {
      envEntries.forEach(printWriter::println);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected static String runDockerCompose(String composeCommand) {

    Map<String, String> env = new HashMap<>();
    env.put("COMPOSE_DOCKER_CLI_BUILD", "1");
    env.put("DOCKER_BUILDKIT", "1");
    env.put("COMPOSE_PROJECT_NAME", "test");
    //shellExecutor.execute("docker-compose build --progress=plain remote-api", env);
    return shellExecutor.execute("docker-compose " + composeCommand, env);
  }

  protected static void waitForHealthy() {
    final int timeoutSeconds = 30;
    final long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000);

    try {
      while (System.currentTimeMillis() < endTime) {

        LOG.info("Waiting for remote-api to become healthy");
        Thread.sleep(1000);

        String output = runDockerCompose("ps remote-api");

        if (output.contains("Up (healthy)")) {
          return;
        }
      }

      throw new RuntimeException("remote-api not healthy after" + timeoutSeconds + " seconds");

    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Sleep interrupted", e);
    }
  }

  protected static void copyGeneratedClientCertsToLocal() {
    String remoteApiContainerId = runDockerCompose("ps -q remote-api");
    shellExecutor.execute("docker cp " + remoteApiContainerId + ":/data/certs/. "
        + withForwardSlashes(certsDir));

    assertThat(new File(certsDir + "/ca-cert.pem")).exists();
    assertThat(new File(certsDir + "/ca-key.pem")).exists();
    assertThat(new File(certsDir + "/server-cert.pem")).exists();
    assertThat(new File(certsDir + "/server-key.pem")).exists();
    assertThat(new File(certsDirClient + "/ca.pem")).exists();
    assertThat(new File(certsDirClient + "/cert.pem")).exists();
    assertThat(new File(certsDirClient + "/key.pem")).exists();
  }

  protected static String runOverRemoteApi(String command, String... moreEnvs) {
    Map<String, String> env = new HashMap<>();
    env.put("DOCKER_HOST", "tcp://" + REMOTE_API_HOST + ":" + REMOTE_API_PORT);
    env.put("DOCKER_TLS_VERIFY", "1");
    env.put("DOCKER_CERT_PATH", withForwardSlashes(certsDirClient));

    for (String entry : moreEnvs) {
      String key = StringUtils.substringBefore(entry, "=");
      String value = StringUtils.substringAfter(entry, "=");
      env.put(key, value);

      if (StringUtils.isBlank(value)) {
        env.remove(key);
      }
    }

    return shellExecutor.execute(command, env);
  }

  private static String withForwardSlashes(File file) {
    return file.getAbsolutePath().replace("\\", "/");
  }
}
