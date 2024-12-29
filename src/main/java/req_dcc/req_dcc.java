package req_dcc;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Attribute;
import models.Wrapper;
import pt.gov.cartaodecidadao.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import java.security.cert.X509Certificate;

public class req_dcc {
    static {
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. \n" + e);
            System.exit(1);
        }
    }

    private static X509Certificate getX509Certificate(byte[] byteArray) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray)) {
            return (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String exportRSAPublicKeyPem(RSAPublicKey publicKey) {
        return java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Method to create a pseudo-random mask based on password and all input strings
    public static String[] generateMasks(String password, String[] inputStrings) throws NoSuchAlgorithmException {
        // Create an array to hold the masks for each input string
        String[] masks = new String[inputStrings.length];

        // Iterate over the input strings
        for (int i = 0; i < inputStrings.length; i++) {
            String input = inputStrings[i];
            // Concatenate password and the input string
            String combined = password + input;

            // Hash the combined string using SHA-256 to create a pseudo-random key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes());

            // Convert the hash to a hexadecimal string (mask)
            StringBuilder hexMask = new StringBuilder();
            for (byte b : hash) {
                hexMask.append(String.format("%02x", b));  // Convert each byte to a 2-digit hex string
            }

            // Store the mask in the array (each mask will be a string)
            masks[i] = hexMask.toString();
        }

        // Return the array of masks
        return masks;
    }

    public static void main(String[] args) {
        try {
            PTEID_ReaderSet.initSDK();
            PTEID_EIDCard card;
            PTEID_ReaderContext context;
            PTEID_ReaderSet readerSet;
            readerSet = PTEID_ReaderSet.instance();
            List<Attribute> attributes = new ArrayList<>();
            String[] labels = new String[0];
            String[] values = new String[0];
            String[] masks = new String[0];
            String pubKey = "";
            for( int i = 0; i < readerSet.readerCount(); i++) {
                context = readerSet.getReaderByNum(i);
                System.out.println("Obtendo dados...");
                if (context.isCardPresent()){
                    card = context.getEIDCard();
                    PTEID_EId eid = card.getID();
                    String name = eid.getGivenName();
                    String surName = eid.getSurname();
                    String gender = eid.getGender();
                    String height = eid.getHeight();
                    String nationality = eid.getNationality();
                    String birthDate = eid.getDateOfBirth();
                    String nrCC = eid.getDocumentNumber();
                    String valEndDate = eid.getValidityEndDate();
                    String father = eid.getGivenNameFather() + " " + eid.getSurnameFather();
                    String mother = eid.getGivenNameMother() + " " + eid.getSurnameMother();
                    String nif = eid.getTaxNo();
                    String niss = eid.getSocialSecurityNumber();
                    String healthNumber = eid.getHealthNumber();
                    String docVersion = eid.getDocumentVersion();
                    String valBeginDate = eid.getValidityBeginDate();
                    String issuerEntity = eid.getIssuingEntity();
                    String docType = eid.getDocumentType();
                    String requestLocal = eid.getLocalofRequest();

                    PTEID_Photo photoObj = eid.getPhotoObj();
                    PTEID_ByteArray ppng = photoObj.getphoto();

                    PTEID_ulwrapper triesLeft = new PTEID_ulwrapper(-1);
                    PTEID_Pins pins = card.getPins();
                    PTEID_Pin pin = pins.getPinByPinRef(PTEID_Pin.ADDR_PIN);

                    String country = "";
                    String district = "";
                    String municipality = "";
                    String civilParish = "";
                    String streetType = "";
                    String streetName = "";
                    String doorNumber = "";
                    String floor = "";
                    String side = "";
                    String place = "";
                    String locality = "";
                    String zip4 = "";
                    String zip3 = "";
                    String postalLocality = "";

                    try {
                        if (pin.verifyPin("", triesLeft, true)) {
                            PTEID_Address address = card.getAddr();

                            country = address.getCountryCode();
                            district = address.getDistrict();
                            municipality = address.getMunicipality();
                            civilParish = address.getCivilParish();
                            streetType = address.getStreetType();
                            streetName = address.getStreetName();
                            doorNumber = address.getDoorNo();
                            floor = address.getFloor();
                            side = address.getSide();
                            place = address.getPlace();
                            locality = address.getLocality();
                            zip4 = address.getZip4();
                            zip3 = address.getZip3();
                            postalLocality = address.getPostalLocality();
                        }
                    } catch (Exception et) {
                        System.out.println("Não foi possível obter morada");
                    }

                    labels = new String[] {"name", "surName", "png", "gender", "height", "nationality", "birthDate", "nrCC", "valEndDate", "father", "mother", "nif", "niss", "healthNumber", "docVersion", "valBeginDate", "issuerEntity", "docType", "requestLocal", "country", "district", "municipality", "civilParish", "streetType", "streetName", "doorNumber", "floor", "side", "place", "locality", "zip4", "zip3", "postalLocality"};
                    values = new String[] {name, surName, java.util.Base64.getEncoder().encodeToString(ppng.GetBytes()), gender, height, nationality, birthDate, nrCC, valEndDate, father, mother, nif, niss, healthNumber, docVersion, valBeginDate, issuerEntity, docType, requestLocal, country, district, municipality, civilParish, streetType, streetName, doorNumber, floor, side, place, locality, zip4, zip3, postalLocality};

                    PTEID_Certificate t = card.getCertificates().getSignature();
                    byte[] byte_array = t.getCertData().GetBytes();

                    X509Certificate x509Cert = getX509Certificate(byte_array);

                    // Extract the public key
                    assert x509Cert != null;
                    RSAPublicKey publicKey = (RSAPublicKey) x509Cert.getPublicKey();
                    if (publicKey != null) {
                        pubKey = exportRSAPublicKeyPem(publicKey);
                    }

                    // Generate the masks for all the input strings
                    masks = generateMasks(pubKey, labels);

                    // Output all the generated masks
                    for (int j = 0; j < masks.length; j++) {
                        System.out.println("Máscara para o valor '" + labels[j] + "': " + masks[j]);
                    }

                    System.out.println("Nome(s): " + name);
                    System.out.println("Apelido(s): " + surName);
                    System.out.println("Sexo: " + gender);
                    System.out.println("Altura: " + height);
                    System.out.println("Nacionalidade: " + nationality);
                    System.out.println("Data de nascimento: " + birthDate);
                    System.out.println("Nº do CC: " + nrCC);
                    System.out.println("Válido até: " + valEndDate);
                    System.out.println("Pai: " + father);
                    System.out.println("Mãe: " + mother);
                    System.out.println("NIF: " + nif);
                    System.out.println("NISS: " + niss);
                    System.out.println("Nº de Utente: " + healthNumber);
                    System.out.println("Versão do documento: " + docVersion);
                    System.out.println("Data de emissão: " + valBeginDate);
                    System.out.println("Entidade emissora: " + issuerEntity);
                    System.out.println("Tipo de documento: " + docType);
                    System.out.println("Local do pedido: " + requestLocal);
                    System.out.println("País: " + country);
                    System.out.println("Distrito: " + district);
                    System.out.println("Município: " + municipality);
                    System.out.println("Freguesia: " + civilParish);
                    System.out.println("Tipo de via: " + streetType);
                    System.out.println("Nome da via: " + streetName);
                    System.out.println("Nº da porta: " + doorNumber);
                    System.out.println("Andar: " + floor);
                    System.out.println("Lado: " + side);
                    System.out.println("Lugar: " + place);
                    System.out.println("Localidade: " + locality);
                    System.out.println("Código postal: " + zip4 + " - " + zip3);
                    System.out.println("Localidade postal: " + postalLocality);
                    System.out.println("Chave pública: " + pubKey);
                }
            }

            // Loop through and create Field objects dynamically
            for (int i = 0; i < labels.length; i++) {
                Attribute attribute = new Attribute();
                attribute.setLabel(labels[i]);
                attribute.setValue(values[i]);
                attribute.setMask(masks[i]);

                // Add the created Field object to the list
                attributes.add(attribute);
            }
            PTEID_ReaderSet.releaseSDK();

            Wrapper wrapper = new Wrapper();
            wrapper.setAttributes(attributes);

            wrapper.setPublicKey(pubKey);

            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Write to a JSON file
            objectMapper.writeValue(new File("req_dcc.json"), wrapper);
            System.out.println("Os dados do Req_DCC foram exportados para o ficheiro req_dcc.json.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
