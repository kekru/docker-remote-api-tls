package de.kekru.dockerremoteapitls.test;

import static org.assertj.core.api.Assertions.assertThat;

import de.kekru.dockerremoteapitls.test.utils.AbstractIntegrationTest;
import de.kekru.dockerremoteapitls.test.utils.CertUtils;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class CertGenerationTest extends AbstractIntegrationTest {

  @BeforeClass
  public static void init() {
    startRemoteApiContainer(
        "CERT_HOSTNAME=something-else.127.0.0.1.nip.io",
        "CREATE_CERTS_WITH_PW=supersecret123",
        "CERT_EXPIRATION_DAYS=17",
        "CA_EXPIRATION_DAYS=1273"
    );
  }

  @Test
  public void caCertHasCorrectDefaultValues() {

    CertUtils caCert = new CertUtils(new File(certsDir + "/ca-cert.pem"));

    assertThat(caCert.getCert().getSubjectDN().getName())
        .isEqualTo("EMAILADDRESS=test@example.com, CN=example.com, OU=IT, O=ExampleCompany, L=London, ST=London, C=GB");

    assertThat(caCert.getExpiresAt())
        .isEqualTo(LocalDate.now().plusDays(1273));
  }

  @Test
  public void serverCertHasCorrectDefaultValues() {

    CertUtils caCert = new CertUtils(new File(certsDir + "/server-cert.pem"));

    assertThat(caCert.getCert().getSubjectDN().getName())
        .isEqualTo("CN=something-else.127.0.0.1.nip.io");

    assertThat(caCert.getExpiresAt())
        .isEqualTo(LocalDate.now().plusDays(17));
  }

  @Test
  public void clientCertHasCorrectDefaultValues() {

    CertUtils caCert = new CertUtils(new File(certsDirClient + "/cert.pem"));

    assertThat(caCert.getCert().getSubjectDN().getName())
        .isEqualTo("CN=testClient");

    assertThat(caCert.getExpiresAt())
        .isEqualTo(LocalDate.now().plusDays(17));
  }

  @Test
  public void caCertInClientDirIsSameAsInServerDir() throws IOException {

    String ca = FileUtils.readFileToString(new File(certsDir + "/ca-cert.pem"), "UTF-8");
    String caInClientDir = FileUtils.readFileToString(new File(certsDirClient + "/ca.pem"), "UTF-8");

    assertThat(ca).isEqualTo(caInClientDir);
  }

}
