package de.kekru.dockerremoteapitls.test;

import static org.assertj.core.api.Assertions.assertThat;

import de.kekru.dockerremoteapitls.test.utils.AbstractIntegrationTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class IntegrationTest extends AbstractIntegrationTest {

  @Test
  public void test() throws Exception {

    runDockerCompose("build --progress=plain");
    runDockerCompose("up -d --force-recreate remote-api");

    Thread.sleep(5000);
    copyGeneratedClientCertsToLocal();

    Map<String, String> env = new HashMap<>();
    env.put("DOCKER_HOST", REMOTE_API_CONNECTION_STRING);
    env.put("DOCKER_TLS_VERIFY", "1");
    env.put("DOCKER_CERT_PATH", certsDirClient.getAbsolutePath());
    shellExecutor.execute("docker ps", env);

    assertThat("").isEqualTo("");
  }

}
