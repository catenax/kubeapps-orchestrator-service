package com.poc.kubeappswrapper.workflow.steps;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import simplewfms.Task;
import simplewfms.Workflow;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import static com.poc.kubeappswrapper.utility.Certutil.generateSelfSignedCertificateSecret;

public class CertificateStep extends Task<CertificateStep> {

    private final Supplier<String> tenantNameSup = registerExternalParameter("InputDataStep", "TENANT_NAME");
    private final Supplier<String> bpnSup = registerExternalParameter("InputDataStep", "BPN_NUMBER");

    public CertificateStep() {
        super();
        name = "Certificate";
   }

    @Override
    public void runThrows() throws Exception {
        var name = String.format("O=%s, OU=%s, C=DE", tenantNameSup.get(), bpnSup.get());
        setOutput(
                "CERTIFICATE",
                generateSelfSignedCertificateSecret(name, null, null)
        );
    }
}
