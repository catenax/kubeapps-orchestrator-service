package com.poc.kubeappswrapper.kubeappswrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.kubeappswrapper.config.EmailConfig;
import com.poc.kubeappswrapper.factory.AppFactory;
import com.poc.kubeappswrapper.kubeapp.model.CreateInstalledPackageRequest;
import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.model.VaultSecreteRequest;
import com.poc.kubeappswrapper.proxy.kubeapps.KubeAppManageProxy;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;
import com.poc.kubeappswrapper.repository.AppRepository;
import com.poc.kubeappswrapper.service.WorkflowRunner;
import com.poc.kubeappswrapper.utility.Certutil;
import com.poc.kubeappswrapper.workflow.Workflow;
import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.dapsregisration.DapsRegServiceClient;
import com.poc.kubeappswrapper.workflow.steps.vaultupload.VaultUploadStep;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.poc.kubeappswrapper.constant.AppNameConstant.DFT_BACKEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
public class WorkflowTest {

    @Autowired
    WorkflowRunner workflowRunner;

    @MockBean
    DapsRegServiceClient dapsRegServiceClient;

    @MockBean
    VaultAppManageProxy vaultAppManageProxy;

    @MockBean
    KubeAppManageProxy kubeAppManageProxy;

    @MockBean
    AppRepository appRepository;

    @Value("${vault.url}")
    private String valutURL;
    @Value("${vault.token}")
    private String vaulttoken;
    @Value("${vault.timeout}")
    private String vaulttimeout;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void workflowTest() throws IOException, ExecutionException, InterruptedException {
        String TENANT_NAME = "Test-Tenant";
        String BPN_NUMBER = "BPN123456";


        Mockito.when(dapsRegServiceClient.createClient(any(), any())).thenReturn(HttpStatus.CREATED);
        Mockito.when(appRepository.findById("POSTGRES_DB")).thenReturn(Optional.of(PostgresInitializer.getAppDetails()));
        Mockito.when(appRepository.findById("EDC_DATAPLANE")).thenReturn(Optional.of(EDCDataPlaneInitializer.getAppDetails()));
        Mockito.when(appRepository.findById("EDC_CONTROLPLANE")).thenReturn(Optional.of(EDCControlPlaneInitializer.getAppDetails()));
        Mockito.when(appRepository.findById("DFT_BACKEND")).thenReturn(Optional.of(DFTBackendInitializer.getAppDetails()));

        var customerDetails = CustomerDetails.builder()
                 .bpnNumber(BPN_NUMBER)
                 .tenantName(TENANT_NAME)
                 .build();
        Workflow w = workflowRunner.runWorkflow(customerDetails, "token").completable().get();

        // Certificate Step Check
        var certificateStep = ((CertificateStep)w.getTasks().get("certificateStep"));
        {
            assertThat(certificateStep).isNotNull();
            assertThat(certificateStep.getCertificateDetails()).isNotNull();
            var certificate = certificateStep.getCertificateDetails().certificate();
            assertThat(certificate).isNotNull();
            KeyPair keyPair = certificateStep.getCertificateDetails().keyPair();
            assertThat(keyPair).isNotNull();
        }
        // DAPS Step Check
        {
            var certificate = certificateStep.getCertificateDetails().certificate();
            Mockito.verify(dapsRegServiceClient, Mockito.times(1))
                    .createClient(certificate, "token");
        }
        // Vault Step check
        {
            var certificate = certificateStep.getCertificateDetails().certificate();
            var keyPair = certificateStep.getCertificateDetails().keyPair();

            ArgumentCaptor<URI> captor1 = ArgumentCaptor.forClass(URI.class);
            ArgumentCaptor<VaultSecreteRequest> captor2 = ArgumentCaptor.forClass(VaultSecreteRequest.class);
            Mockito.verify(vaultAppManageProxy, Mockito.times(3))
                    .uploadKeyandValue(captor1.capture(), captor2.capture());
            var vaultUploadPayload = captor2.getAllValues();
            assertThat(vaultUploadPayload.get(0)).isEqualTo(VaultSecreteRequest.builder().data(Map.of("content", Certutil.getAsString(certificate))).build());
            assertThat(vaultUploadPayload.get(1)).isEqualTo(VaultSecreteRequest.builder().data(Map.of("content", Certutil.getAsString(keyPair.getPrivate()))).build());
            assertThat(vaultUploadPayload.get(2)).isEqualTo(VaultSecreteRequest.builder().data(Map.of("content", Certutil.getAsString(keyPair.getPrivate()))).build());
            var vaultUploadUri = captor1.getAllValues();
            assertThat(vaultUploadUri.get(0)).isEqualTo(URI.create(valutURL + "/v1/secret/data/" + TENANT_NAME + "daps-cert"));
            assertThat(vaultUploadUri.get(1)).isEqualTo(URI.create(valutURL + "/v1/secret/data/" + TENANT_NAME + "certificate-private-key"));
            assertThat(vaultUploadUri.get(2)).isEqualTo(URI.create(valutURL + "/v1/secret/data/" + TENANT_NAME + "certificate-private-key-pub"));
            var vaultStep = ((VaultUploadStep) w.getTasks().get("vaultUploadStep"));
            assertThat(vaultStep.getConfigParams())
                    .isEqualTo(
                            Map.of(
                                    "daps-cert", TENANT_NAME + "daps-cert",
                                    "certificate-private-key", TENANT_NAME + "certificate-private-key",
                                    "vaulturl", valutURL,
                                    "vaulttoken", vaulttoken,
                                    "vaulttimeout", vaulttimeout
                            )
                    );
        }

        // EDC Postgres Check
        {
            ArgumentCaptor<CreateInstalledPackageRequest> kubAppsProxyCaptor = ArgumentCaptor.forClass(CreateInstalledPackageRequest.class);
            Mockito.verify(kubeAppManageProxy, Mockito.times(5)).createPackage(kubAppsProxyCaptor.capture());
            var createInstalledPackageRequest = kubAppsProxyCaptor.getValue();
            assertThat(createInstalledPackageRequest).isNotNull();
            var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(createInstalledPackageRequest);
        }


    }
}
