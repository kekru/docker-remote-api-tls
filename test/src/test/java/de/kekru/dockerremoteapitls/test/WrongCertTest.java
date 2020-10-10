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

public class WrongCertTest extends AbstractIntegrationTest {

  @BeforeClass
  public static void init() {
    startRemoteApiContainer(
        "CERT_HOSTNAME=abc.127.0.0.1.nip.io",
        "CREATE_CERTS_WITH_PW=supersecret"
    );
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
