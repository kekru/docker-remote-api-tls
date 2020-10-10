package de.kekru.dockerremoteapitls.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import de.kekru.dockerremoteapitls.test.utils.AbstractIntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicConnectionTest extends AbstractIntegrationTest {

  @BeforeClass
  public static void init() {
    startRemoteApiContainer("CREATE_CERTS_WITH_PW=supersecret");
  }

  @Test
  public void canConnectOverRemoteApi() {
    // When
    String output = runOverRemoteApi("docker ps");

    // Then
    assertThat(output).contains("kekru/docker-remote-api-tls:temp-for-unittests");
  }

  @Test
  public void failsOnWrongHost() {
    // When
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        runOverRemoteApi("docker ps",
            "DOCKER_HOST=tcp://wrong-host-" + REMOTE_API_HOST + ":" + REMOTE_API_PORT)
    );

    // Then
    assertThat(exception)
        .hasMessageContaining("error during connect:");
    assertThat(exception)
        .hasMessageContaining("x509: certificate is valid for abc.127.0.0.1.nip.io, "
            + "not wrong-host-abc.127.0.0.1.nip.io");
  }

  @Test
  public void failsOnNoTls() {
    // When
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        runOverRemoteApi("docker ps",
            "DOCKER_TLS_VERIFY=",
            "DOCKER_CERT_PATH="
            )
    );

    // Then
    assertThat(exception)
        .hasMessageContaining("error during connect: Get http://abc.127.0.0.1.nip.io:30129/v1.40/containers/json: EOF");
  }

}
