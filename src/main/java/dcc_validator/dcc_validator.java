package dcc_validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import gen_dcc.gen_dcc;
import models.Attribute;
import models.DCC;
import models.MinAttribute;
import models.Min_dcc;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class dcc_validator {
    // Verify the signature using the public key and the hashed data
    private static boolean verifySignature(String data, byte[] signatureBytes, String publicKey) throws Exception {
        // Decode the public key (assuming it's Base64 encoded)
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);

        // Reconstruct the public key from bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        // Create the signature object and initialize it with the public key
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(pubKey);

        // Update the signature object with the hashed data
        verifier.update(data.getBytes());

        // Verify the signature
        return verifier.verify(signatureBytes);  // Compare the signature with the provided data
    }

    private static String generateSHA256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private static void verifyCommitmentValues(DCC dcc) {
        try {
            for (Attribute attribute : dcc.getAttributes()) {
                String concatenatedValues = attribute.getLabel() + attribute.getValue() + attribute.getMask();
                String commitmentValue = generateSHA256(concatenatedValues);
                if (commitmentValue.equals(attribute.getCommitment())) {
                    System.out.println("O atributo " + attribute.getLabel() +" é válido.");
                    System.out.println("Gerado: " + commitmentValue);
                    System.out.println("Original: " + attribute.getCommitment());
                } else {
                    System.out.println("O atributo " + attribute.getLabel() +" não é válido.");
                    System.out.println("Gerado: " + commitmentValue);
                    System.out.println("Original: " + attribute.getCommitment());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void verifyMinDccCommitmentValues(Min_dcc minDcc) {
        try {
            int i = 0;
            for (MinAttribute minAttribute : minDcc.getAttributes()) {
                if (minAttribute.getValue() != null) {
                    String concatenatedValues = minAttribute.getName() + minAttribute.getValue()[0] + minAttribute.getValue()[1];
                    String commitmentValue = generateSHA256(concatenatedValues);
                    if (commitmentValue.equals(minDcc.getCommitment_values().get(i))) {
                        System.out.println("O atributo " + minAttribute.getName() + " é válido: " + minAttribute.getValue()[0]);
                        System.out.println("Gerado: " + commitmentValue);
                        System.out.println("Original: " + minDcc.getCommitment_values().get(i));
                    } else {
                        System.out.println("O atributo " + minAttribute.getName() + " não é válido.");
                        System.out.println("Gerado: " + commitmentValue);
                        System.out.println("Original: " + minDcc.getCommitment_values().get(i));
                    }
                } else {
                    System.out.println("Não é possível validar o atributo " + minAttribute.getName() + ".");
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void validateDcc() {
        try {
            // Read the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            DCC dcc = objectMapper.readValue(new File("dcc.json"), DCC.class);

            System.out.println("A validar certificado...");
            if (!validateCertificateChain(dcc.getSignature().getCertificate())) {
                System.out.println("Certificado inválido.");
                return;
            }
            System.out.println("Certificado válido.");

            // Combine all commitments and the public key
            StringBuilder combinedData = new StringBuilder();
            List<Attribute> attributes = dcc.getAttributes();
            for (Attribute attribute : attributes) {
                combinedData.append(attribute.getCommitment());
            }
            combinedData.append(dcc.getPublicKey());  // Append the public key

            // Decode the Base64 signature
            String base64Signature = dcc.getSignature().getValue();
            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);  // Decode Base64 signature

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream certInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(dcc.getSignature().getCertificate()));
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(certInputStream);
            PublicKey publicKey = cert.getPublicKey();

            byte[] publicKeyBytes = publicKey.getEncoded();
            String base64PublicKey = Base64.getEncoder().encodeToString(publicKeyBytes);

            // Verify the signature
            boolean isSignatureValid = verifySignature(combinedData.toString(), signatureBytes, base64PublicKey);
            if (isSignatureValid) {
                System.out.println("A assinatura é válida.");
                verifyCommitmentValues(dcc);
            } else {
                System.out.println("A assinatura não é válida.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void validateMinDcc() {
        try {
            // Read the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            Min_dcc minDcc = objectMapper.readValue(new File("min_dcc.json"), Min_dcc.class);

            System.out.println("A validar certificado...");
            if (!validateCertificateChain(minDcc.getDccSignature().getCertificate())) {
                System.out.println("Certificado inválido.");
                return;
            }
            System.out.println("Certificado válido.");

            // Combine all commitments and the public key
            StringBuilder combinedData = new StringBuilder();
            for (String commitment : minDcc.getCommitment_values()) {
                combinedData.append(commitment);
            }
            combinedData.append(minDcc.getPublicKey());

            // Decode the Base64 signature
            String base64Signature = minDcc.getSignature().getValue();
            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);

            // Verify the signature
            boolean isSignatureValid = verifySignature(combinedData.toString(), signatureBytes, minDcc.getPublicKey());
            if (isSignatureValid) {
                System.out.println("A assinatura é válida.");
                verifyMinDccCommitmentValues(minDcc);
            } else {
                System.out.println("A assinatura não é válida.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean validateCertificateChain(String certificate) throws Exception {
        try {
            // Carregar o certificado raiz (rootCA.crt)
            InputStream inputStream = gen_dcc.class.getClassLoader().getResourceAsStream("rootCA.crt");
            if (inputStream == null) {
                throw new IllegalArgumentException("Não foi possível encontrar o ficheiro signingApp.key.");
            }
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate rootCert = (X509Certificate) certFactory.generateCertificate(inputStream);

            // Carregar o certificado da aplicação de assinatura (signingApp.crt)
            ByteArrayInputStream certInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(certificate));
            X509Certificate appCert = (X509Certificate) certFactory.generateCertificate(certInputStream);

            // Criar um KeyStore para armazenar o certificado da CA
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("rootCA", rootCert);

            // Criar um TrustManagerFactory e configurar com o KeyStore contendo a CA raiz
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Criar um TrustManager a partir do TrustManagerFactory
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            X509TrustManager x509TrustManager = (X509TrustManager) trustManagers[0];

            // Validar o certificado da aplicação de assinatura com a CA raiz
            x509TrustManager.checkServerTrusted(new X509Certificate[]{appCert}, "RSA");

            System.out.println("O certificado foi validado com sucesso.");
            return true;
        } catch (CertificateException e) {
            System.err.println("O certificado não é válido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro ao validar o certificado: " + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nSelecione o tipo de Dcc a validar:\n1 - dcc \n2 - min dcc");
            String input = scanner.nextLine();

            if (Objects.equals(input, "1")) {
                validateDcc();
            } else {
                validateMinDcc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
