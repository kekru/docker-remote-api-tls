package de.kekru.dockerremoteapitls.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import de.kekru.dockerremoteapitls.test.utils.AbstractIntegrationTest;
import de.kekru.dockerremoteapitls.test.utils.CertUtils;
import java.io.File;
import java.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicConnectionTest extends AbstractIntegrationTest {

  @BeforeClass
  public static void init() {
    startRemoteApiContainer(
        "CERT_HOSTNAME=abc.127.0.0.1.nip.io",
        "CREATE_CERTS_WITH_PW=supersecret"
    );
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
        .hasMessageContaining("error during connect: Get \"http://abc.127.0.0.1.nip.io:30129/v1.47/containers/json\"");
  }

  @Test
  public void caCertHasCorrectDefaultValues() {

    CertUtils caCert = new CertUtils(new File(certsDir + "/ca-cert.pem"));

    assertThat(caCert.getCert().getSubjectDN().getName())
        .isEqualTo("EMAILADDRESS=test@example.com, CN=example.com, OU=IT, O=ExampleCompany, L=London, ST=London, C=GB");

    assertThat(caCert.getExpiresAt())
        .isEqualTo(LocalDate.now().plusDays(900));
  }

  @Test
  public void serverCertHasCorrectDefaultValues() {

    CertUtils caCert = new CertUtils(new File(certsDir + "/server-cert.pem"));

    assertThat(caCert.getCert().getSubjectDN().getName())
        .isEqualTo("CN=abc.127.0.0.1.nip.io");

    assertThat(caCert.getExpiresAt())
        .isEqualTo(LocalDate.now().plusDays(365));
  }

  @Test
  public void clientCertHasCorrectDefaultValues() {

    CertUtils caCert = new CertUtils(new File(certsDirClient + "/cert.pem"));

    assertThat(caCert.getCert().getSubjectDN().getName())
        .isEqualTo("CN=testClient");

    assertThat(caCert.getExpiresAt())
        .isEqualTo(LocalDate.now().plusDays(365));
  }

}
