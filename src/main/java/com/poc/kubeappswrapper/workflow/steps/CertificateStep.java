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

public class CertificateStep extends Task<CertificateStep> {

    public CertificateStep(Workflow workflow) {
        super();
        workflow.registerTask(this, "Certificate");
        registerExternalParameter("InputDataStep", "TENANT_NAME", "tenant");
        registerExternalParameter("InputDataStep", "BPN_NUMBER", "bpn");
    }

    @Override
    public void runThrows() throws Exception {
        String bpn = (String) getParameter("bpn");
        String tenant = (String) getParameter("tenant");
        var name = String.format("O=%s, OU=%s, C=DE", tenant, bpn);
        var certificateData = generateSelfSignedCertificateSecret(name, null, null);
        setOutput("CERTIFICATE", certificateData);
    }

    public static AbstractMap.SimpleImmutableEntry<X509Certificate, KeyPair> generateSelfSignedCertificateSecret(String name, Integer days, Integer bits) throws GeneralSecurityException, OperatorCreationException, CertIOException {
        var subject = new X500Principal(name);
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(Optional.ofNullable(bits).orElse(2048), new SecureRandom());
        var keyPair = keyPairGenerator.generateKeyPair();
        var notBefore = System.currentTimeMillis();
        var notAfter = notBefore + (1000L * 3600L * 24 * Optional.ofNullable(days).orElse(365));
        var certBuilder = new JcaX509v3CertificateBuilder(
                subject, // signed by
                BigInteger.ONE,
                new Date(notBefore),
                new Date(notAfter),
                subject,
                keyPair.getPublic()
        );
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
        certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature + KeyUsage.keyEncipherment));
        var spki = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        var ski = new BcX509ExtensionUtils().createSubjectKeyIdentifier(spki);
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);
        var aki = new BcX509ExtensionUtils().createAuthorityKeyIdentifier(spki);
        certBuilder.addExtension(Extension.authorityKeyIdentifier, false, aki);
        var signer = new JcaContentSignerBuilder(("SHA256withRSA")).build(keyPair.getPrivate());
        var certHolder = certBuilder.build(signer);
        return new AbstractMap.SimpleImmutableEntry<>(new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder), keyPair);
    }
}
