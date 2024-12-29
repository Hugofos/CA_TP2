import com.fasterxml.jackson.databind.ObjectMapper;
import models.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;

public class UI {

    private static JLabel photoLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UI::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("Citizen Card - Custom Layout");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 720);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        String name = "";
        String surName = "";
        String photo = "";
        String gender = "";
        String height = "";
        String nationality = "";
        String birthDate = "";
        String nrCC = "";
        String valEndDate = "";
        String nif = "";
        String niss = "";
        String healthNumber = "";
        String docVersion = "";
        String valBeginDate = "";
        String issuerEntity = "";
        String docType = "";
        String requestLocal = "";
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
        String father = "";
        String mother = "";

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nSelecione o tipo de Dcc para visualizar:\n1 - req_dcc\n2 - dcc \n3 - min dcc");
            String input = scanner.nextLine();
            ObjectMapper objectMapper = new ObjectMapper();
            Object object;

            if (Objects.equals(input, "1")) {
                object = objectMapper.readValue(new File("req_dcc.json"), Wrapper.class);
            } else {
                if (Objects.equals(input, "2")) {
                    object = objectMapper.readValue(new File("dcc.json"), DCC.class);
                } else {
                    object = objectMapper.readValue(new File("min_dcc.json"), Min_dcc.class);
                }
            }
            name = getValueByLabel(object, "name");
            surName = getValueByLabel(object, "surName");
            photo = getValueByLabel(object, "png");
            gender = getValueByLabel(object, "gender");
            height = getValueByLabel(object, "height");
            nationality = getValueByLabel(object, "nationality");
            birthDate = getValueByLabel(object, "birthDate");
            nrCC = getValueByLabel(object, "nrCC");
            valEndDate = getValueByLabel(object, "valEndDate");
            father = getValueByLabel(object, "father");
            mother = getValueByLabel(object, "mother");
            nif = getValueByLabel(object, "nif");
            niss = getValueByLabel(object, "niss");
            healthNumber = getValueByLabel(object, "healthNumber");
            docVersion = getValueByLabel(object, "docVersion");
            valBeginDate = getValueByLabel(object, "valBeginDate");
            issuerEntity = getValueByLabel(object, "issuerEntity");
            docType = getValueByLabel(object, "docType");
            requestLocal = getValueByLabel(object, "requestLocal");
            country = getValueByLabel(object, "country");
            district = getValueByLabel(object, "district");
            municipality = getValueByLabel(object, "municipality");
            civilParish = getValueByLabel(object, "civilParish");
            streetType = getValueByLabel(object, "streetType");
            streetName = getValueByLabel(object, "streetName");
            doorNumber = getValueByLabel(object, "doorNumber");
            floor = getValueByLabel(object, "floor");
            side = getValueByLabel(object, "side");
            place = getValueByLabel(object, "place");
            locality = getValueByLabel(object, "locality");
            zip4 = getValueByLabel(object, "zip4");
            zip3 = getValueByLabel(object, "zip3");
            postalLocality = getValueByLabel(object, "postalLocality");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel row1 = new JPanel(new BorderLayout());

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(createLabeledField("Nome(s):", name));
        namePanel.add(createLabeledField("Apelido(s):", surName));
        row1.add(namePanel, BorderLayout.CENTER);

        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(180, 40));
        photoLabel.setOpaque(true);
        photoLabel.setBackground(Color.LIGHT_GRAY);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (Objects.equals(photo, "null")) {
            System.out.println("PHOTO IS NULL");
        } else {
            setPhotoFromBase64(photo);
        }

        row1.add(photoLabel, BorderLayout.EAST);

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row1);

        JPanel row2 = new JPanel();
        row2.setLayout(new GridLayout(1, 4, 10, 0));
        row2.add(createLabeledField("Sexo:", gender));
        row2.add(createLabeledField("Altura:", height));
        row2.add(createLabeledField("Nacionalidade:", nationality));
        row2.add(createLabeledField("Data de Nascimento:", birthDate));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row2);

        JPanel row3 = new JPanel();
        row3.setLayout(new GridLayout(1, 2, 10, 0));
        row3.add(createLabeledField("Número de Documento:", nrCC));
        row3.add(createLabeledField("Data de Validade:", valEndDate));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row3);

        // Row 4: 2 columns (Pai, Mae)
        JPanel row4 = new JPanel();
        row4.setLayout(new GridLayout(1, 2, 10, 0));
        row4.add(createLabeledField("Pai:", father));
        row4.add(createLabeledField("Mãe:", mother));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row4);

        JPanel row5 = new JPanel();
        row5.setLayout(new GridLayout(1, 1, 10, 0));
        row5.add(createLabeledField("Indicações Eventuais:", ""));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row5);

        JPanel row6 = new JPanel();
        row6.setLayout(new GridLayout(1, 3, 10, 0));
        row6.add(createLabeledField("Nº Indetificação Fiscal:", nif));
        row6.add(createLabeledField("Nº Segurança Social:", niss));
        row6.add(createLabeledField("Nº Utente de Saúde:", healthNumber));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row6);

        JPanel row7 = new JPanel();
        row7.setLayout(new GridLayout(1, 2, 10, 0));
        row7.add(createLabeledField("Entidade Emissora:", issuerEntity));
        row7.add(createLabeledField("Data de Emissão:", valBeginDate));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row7);

        JPanel row8 = new JPanel();
        row8.setLayout(new GridLayout(1, 2, 10, 0));
        row8.add(createLabeledField("Tipo de Documento:", docType));
        row8.add(createLabeledField("Local do Pedido:", requestLocal));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row8);

        JPanel row9 = new JPanel();
        row9.setLayout(new GridLayout(1, 1, 10, 0));
        row9.add(createLabeledField("Versão do Cartão:", docVersion));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row9);

        JPanel row10 = new JPanel();
        row10.setLayout(new GridLayout(1, 2, 10, 0));
        row10.add(createLabeledField("País:", country));
        row10.add(createLabeledField("Distrito Nacional:", district));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row10);

        JPanel row11 = new JPanel();
        row11.setLayout(new GridLayout(1, 2, 10, 0));
        row11.add(createLabeledField("Concelho:", municipality));
        row11.add(createLabeledField("Freguesia:", civilParish));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row11);

        JPanel row12 = new JPanel();
        row12.setLayout(new GridLayout(1, 2, 10, 0));
        row12.add(createLabeledField("Tipo de Via:", streetType));
        row12.add(createLabeledField("Nome da Via:", streetName));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row12);

        JPanel row13 = new JPanel();
        row13.setLayout(new GridLayout(1, 3, 10, 0));
        row13.add(createLabeledField("Nº da Porta:", doorNumber));
        row13.add(createLabeledField("Andar:", floor));
        row13.add(createLabeledField("Lado:", side));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row13);

        JPanel row14 = new JPanel();
        row14.setLayout(new GridLayout(1, 3, 10, 0));
        row14.add(createLabeledField("Lugar:", place));
        row14.add(createLabeledField("Localidade:", locality));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row14);

        JPanel row15 = new JPanel();
        row15.setLayout(new GridLayout(1, 3, 10, 0));
        row15.add(createLabeledField("Código Postal:", zip4 + "-" + zip3));
        row15.add(createLabeledField("Localidade Postal:", postalLocality));

        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(row15);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void setPhotoFromBase64(String base64Image) {
        try {
            // Decodificar a string Base64 em bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Criar a imagem a partir dos bytes
            InputStream is = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(is);

            // Obter as dimensões originais da imagem
            int originalWidth = bufferedImage.getWidth();
            int originalHeight = bufferedImage.getHeight();

            // Calcular as novas dimensões (0.5 vezes)
            int newWidth = (int) (originalWidth * 0.5);
            int newHeight = (int) (originalHeight * 0.5);

            // Redimensionar a imagem
            Image scaledImage = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            // Criar um ImageIcon a partir da imagem redimensionada
            ImageIcon imageIcon = new ImageIcon(scaledImage);

            // Atualizar o JLabel para mostrar a imagem redimensionada
            photoLabel.setIcon(imageIcon);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao carregar a imagem Base64.");
        }
    }

    private static String getValueByLabel(Object object, String label) {
        if (object instanceof Wrapper wrapper) {
            for (Attribute attribute : wrapper.getAttributes()) {
                if (attribute.getLabel().equals(label)) {
                    return attribute.getValue();
                }
            }
        }
        if (object instanceof DCC dcc) {
            for (Attribute attribute : dcc.getAttributes()) {
                if (attribute.getLabel().equals(label)) {
                    return attribute.getValue();
                }
            }
        }
        if (object instanceof Min_dcc minDcc) {
            for (MinAttribute attribute : minDcc.getAttributes()) {
                if (attribute.getName().equals(label)) {
                    if (attribute.getValue() == null) {
                        return "null";
                    } else {
                        return attribute.getValue()[0];
                    }
                }
            }
        }
        return "null";
    }

    private static JPanel createLabeledField(String labelText, String fieldValue) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField(fieldValue);
        textField.setEditable(false);
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        return panel;
    }
}
