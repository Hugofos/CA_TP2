package gen_dcc;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Attribute;
import models.DCC;
import models.Wrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

public class gen_dcc {

    public static void generateCommitmentValues(List<Attribute> attributes, DCC dcc) {
        try {
            for (Attribute attribute : attributes) {
                String concatenatedValues = attribute.getLabel() + attribute.getValue() + attribute.getMask();
                String commitmentValue = generateSHA256(concatenatedValues);
                attribute.setCommitment(commitmentValue);
                System.out.println("Valor do commitment: " + attribute.getCommitment());
            }
            dcc.setAttributes(attributes);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static void signDcc(DCC dcc) {
        StringBuilder itemsToSign = new StringBuilder();
        for (Attribute attribute : dcc.getAttributes()) {
            itemsToSign.append(attribute.getCommitment());
        }
        itemsToSign.append(dcc.getPublicKey());
        try {
            // Load the private key from signingApp.key
            PrivateKey privateKey = loadPrivateKey();
            System.out.println("Items para assinar: " + itemsToSign);

            // Data to be signed
            byte[] dataBytes = itemsToSign.toString().getBytes();

            // Sign the data
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(dataBytes);
            byte[] digitalSignature = signature.sign();

            // Output the signature in Base64 format
            String signatureBase64 = Base64.getEncoder().encodeToString(digitalSignature);
            dcc.setSignature(new models.Signature());
            dcc.getSignature().setValue(signatureBase64);

            byte[] certificate = loadCertificate();
            dcc.getSignature().setCertificate(Base64.getEncoder().encodeToString(certificate));
            dcc.getSignature().setTimestamp(Instant.now().toString());
            dcc.getSignature().setDescription("Certificados gerados utilizando o algoritmo RSA, garantindo autenticação e integridade dos dados através de uma cadeia de confiança estabelecida pelo certificado raiz (CA).");
            dcc.setDescription("SHA-256 (Algoritmo de Hash Seguro 256-bit) é uma função de hash criptográfica que recebe uma entrada de qualquer tamanho e gera uma saída fixa de 256 bits (32 bytes). É amplamente utilizada para garantir a integridade e segurança dos dados, assegurando que até uma pequena alteração na entrada resulte num hash completamente diferente. O SHA-256 é considerado seguro e faz parte da família SHA-2 de algoritmos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey loadPrivateKey() throws Exception {
        // Load private key from resources
        InputStream inputStream = gen_dcc.class.getClassLoader().getResourceAsStream("signingApp.key");
        if (inputStream == null) {
            throw new IllegalArgumentException("Ficheiro signingApp.key não encontrado.");
        }

        // Read and decode the private key
        StringBuilder keyContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("-----")) {
                    keyContent.append(line);
                }
            }
        }

        byte[] keyBytes = Base64.getDecoder().decode(keyContent.toString());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private static byte[] loadCertificate() throws Exception {
        // Load certificate from resources
        InputStream inputStream = gen_dcc.class.getClassLoader().getResourceAsStream("signingApp.crt");
        if (inputStream == null) {
            throw new IllegalArgumentException("Ficheiro signingApp.crt não encontrado.");
        }

        // Read and decode the certificate
        StringBuilder keyContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("-----")) {
                    keyContent.append(line);
                }
            }
        }

        return Base64.getDecoder().decode(keyContent.toString());
    }

    public static void main(String[] args) {
        try {
            // Read the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            Wrapper wrapper = objectMapper.readValue(new File("req_dcc.json"), Wrapper.class);

            DCC dcc = new DCC();

            dcc.setPublicKey(wrapper.getPublicKey());

            generateCommitmentValues(wrapper.getAttributes(), dcc);
            signDcc(dcc);

            // Write to a JSON file
            objectMapper.writeValue(new File("dcc.json"), dcc);
            System.out.println("Os dados do DCC foram exportados para o ficheiro dcc.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
