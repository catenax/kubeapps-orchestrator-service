package com.poc.kubeappswrapper.kubeappswrapper;

import com.poc.kubeappswrapper.model.CustomerDetails;
import com.poc.kubeappswrapper.model.VaultSecreteRequest;
import com.poc.kubeappswrapper.proxy.vault.VaultAppManageProxy;
import com.poc.kubeappswrapper.service.WorkflowRunner;
import com.poc.kubeappswrapper.utility.Certutil;
import com.poc.kubeappswrapper.workflow.Workflow;
import com.poc.kubeappswrapper.workflow.steps.CertificateStep;
import com.poc.kubeappswrapper.workflow.steps.dapsregisration.DapsRegServiceClient;
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

import java.io.IOException;
import java.net.URI;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    @Value("${vault.url}")
    private String valutURL;


    @Test
    public void workflowTest() throws IOException, ExecutionException, InterruptedException {
        Mockito.when(dapsRegServiceClient.createClient(any(), any())).thenReturn(HttpStatus.CREATED);
        var customerDetails = CustomerDetails.builder()
                 .bpnNumber("BPN123456")
                 .tenantName("Test-Tenant")
                 .build();
        Workflow w = workflowRunner.runWorkflow(customerDetails, "token").completable().get();
        var certificateStep = ((CertificateStep)w.getTasks().get("certificateStep"));

        assertThat(certificateStep).isNotNull();
        assertThat(certificateStep.getCertificateDetails()).isNotNull();
        X509Certificate certificate = certificateStep.getCertificateDetails().certificate();
        assertThat(certificate).isNotNull();
        KeyPair keyPair = certificateStep.getCertificateDetails().keyPair();
        assertThat(keyPair).isNotNull();

        Mockito.verify(dapsRegServiceClient, Mockito.times(1))
                 .createClient(certificate, "token");

        ArgumentCaptor<URI> captor1 = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<VaultSecreteRequest> captor2 = ArgumentCaptor.forClass(VaultSecreteRequest.class);
        Mockito.verify(vaultAppManageProxy, Mockito.times(3))
                 .uploadKeyandValue(captor1.capture(), captor2.capture());
        var vaultUploadPayload = captor2.getAllValues();
        assertThat(vaultUploadPayload.get(0)).isEqualTo(VaultSecreteRequest.builder().data(Map.of("content", Certutil.getAsString(certificate))).build());
        assertThat(vaultUploadPayload.get(1)).isEqualTo(VaultSecreteRequest.builder().data(Map.of("content", Certutil.getAsString(keyPair.getPrivate()))).build());
        assertThat(vaultUploadPayload.get(2)).isEqualTo(VaultSecreteRequest.builder().data(Map.of("content", Certutil.getAsString(keyPair.getPrivate()))).build());
        var vaultUploadUri = captor1.getAllValues();
        assertThat(vaultUploadUri.get(0)).isEqualTo(URI.create(valutURL+ "/v1/secret/data/" + "Test-Tenant" + "daps-cert"));
        assertThat(vaultUploadUri.get(1)).isEqualTo(URI.create(valutURL+ "/v1/secret/data/" + "Test-Tenant" + "certificate-private-key"));
        assertThat(vaultUploadUri.get(2)).isEqualTo(URI.create(valutURL+ "/v1/secret/data/" + "Test-Tenant" + "certificate-private-key-pub"));
    }
}
