package gen_min_dcc;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Attribute;
import models.DCC;
import models.MinAttribute;
import models.Min_dcc;
import pt.gov.cartaodecidadao.*;

import java.io.*;
import java.security.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

public class gen_min_dcc {
    static {
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. \n" + e);
            System.exit(1);
        }
    }

    private static Set<Integer> parseUserInput(String input) {
        Set<Integer> selectedIndexes = new HashSet<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                int index = Integer.parseInt(part.trim()) - 1;
                selectedIndexes.add(index);
            } catch (NumberFormatException e) {
                System.err.println("Valor inválido: " + part.trim());
            }
        }
        return selectedIndexes;
    }

    private static byte[] generateHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    private static void signMinDcc(Min_dcc min_dcc) {
        StringBuilder itemsToSign = new StringBuilder();
        for (String commitment : min_dcc.getCommitment_values()) {
            itemsToSign.append(commitment);
        }
        itemsToSign.append(min_dcc.getPublicKey());
        try {
            PTEID_ReaderSet.initSDK();
            PTEID_EIDCard card;
            PTEID_ReaderContext context;
            PTEID_ReaderSet readerSet;
            readerSet = PTEID_ReaderSet.instance();

            System.out.println("Items para assinar: " + itemsToSign);

            for( int i = 0; i < readerSet.readerCount(); i++) {
                context = readerSet.getReaderByNum(i);
                System.out.println("Obtendo dados...");
                if (context.isCardPresent()) {
                    card = context.getEIDCard();
                    System.out.println("Assinando...");
                    PTEID_ByteArray output = card.Sign(new PTEID_ByteArray(generateHash(itemsToSign.toString().getBytes()), generateHash(itemsToSign.toString().getBytes()).length), true);

                    String signatureBase64 = Base64.getEncoder().encodeToString(output.GetBytes());
                    min_dcc.setSignature(new models.Signature());
                    min_dcc.getSignature().setValue(signatureBase64);
                    min_dcc.getSignature().setTimestamp(Instant.now().toString());
                    min_dcc.getSignature().setDescription("RSA-SHA256 é um algoritmo de assinatura digital que combina o sistema de criptografia RSA com a função de hash SHA-256. Primeiro, o algoritmo aplica o SHA-256 à mensagem para gerar um resumo (hash) da mesma. Em seguida, a assinatura digital é criada ao cifrar esse hash com a chave privada RSA. A assinatura gerada pode ser validada usando a chave pública correspondente, garantindo a autenticidade e integridade da mensagem. Esse método é amplamente utilizado em processos de autenticação e segurança de dados.");
                    System.out.println("Assinado");
                }
            }
            PTEID_ReaderSet.releaseSDK();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Min_dcc minDcc = new Min_dcc();

            // Read the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            DCC dcc = objectMapper.readValue(new File("dcc.json"), DCC.class);

            // Step 2: Display the values to the user
            System.out.println("Estes são os campos do ficheiro JSON:");
            List<String> commitmentValues = new ArrayList<>();
            List<MinAttribute> minAttributes = new ArrayList<>();
            int i = 1;
            for (Attribute attribute : dcc.getAttributes()) {
                System.out.println(i + " - " + attribute.getLabel());
                i++;
                commitmentValues.add(attribute.getCommitment());
                MinAttribute minAttribute = new MinAttribute();
                minAttribute.setName(attribute.getLabel());
                String[] v = {attribute.getValue(), attribute.getMask()};
                minAttribute.setValue(v);
                minAttributes.add(minAttribute);
            }

            minDcc.setCommitment_values(commitmentValues);
            minDcc.setAttributes(minAttributes);
            minDcc.setPublicKey(dcc.getPublicKey());
            minDcc.setDescription(dcc.getDescription());
            minDcc.setDccSignature(dcc.getSignature());

            // Step 3: Prompt the user to select which values to keep
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nIntroduza os números dos campos que pretende manter no min_dcc separados por ',' (ex: 1,2,3): ");
            String input = scanner.nextLine();

            // Step 4: Process the user input and filter the values
            Set<Integer> selectedIndexes = parseUserInput(input);
            System.out.println(selectedIndexes);

            // Iterate through the objects list and set the value to null for the others
            for (int j = 0; j < minDcc.getAttributes().size(); j++) {
                if (!selectedIndexes.contains(j)) {
                    minDcc.getAttributes().get(j).setValue(null);
                }
            }

            // Step 5: Display the selected values
            System.out.println("\nOs valores selecionados para manter foram:");
            for (MinAttribute attribute : minDcc.getAttributes()) {
                System.out.println(attribute.getName());
                System.out.println(Arrays.toString(attribute.getValue()));
            }

            signMinDcc(minDcc);

            // Write to a JSON file
            objectMapper.writeValue(new File("min_dcc.json"), minDcc);
            System.out.println("Os dados do Min_DCC foram exportados para o ficheiro min_dcc.json.");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
