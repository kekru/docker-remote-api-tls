package de.kekru.dockerremoteapitls.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import de.kekru.dockerremoteapitls.test.utils.AbstractIntegrationTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntegrationTest extends AbstractIntegrationTest {

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

  @Test
  public void failsOnModifiedClientCert() throws Exception {
    // Given
    copyResourceToFile("/some-client-cert.pem",
        new File(certsDirClient + "/cert.pem"));
    copyResourceToFile("/some-client-key.pem",
         new File(certsDirClient + "/key.pem"));

    // When
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        runOverRemoteApi("docker ps")
    );

    // Then
    assertThat(exception)
        .hasMessageContaining("error during connect:");
    assertThat(exception)
        .hasMessageContaining("failed to retrieve context tls info: tls: private key does not match public key");
  }

  private void copyResourceToFile(String resourceFile, File clientCert) throws IOException {
    try (InputStream in = getClass().getResourceAsStream(resourceFile);
        OutputStream out = new FileOutputStream(clientCert)) {

      Objects.requireNonNull(in, "no resourcefile found for " + resourceFile);
      IOUtils.copy(in, out);
    }
  }

}
