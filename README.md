# Selective Identity Disclosure (SID) Project

## Overview
This project implements a secure and privacy-preserving Digital Citizen Card (DCC) system for Selective Identity Disclosure (SID). The system allows users to:
- Disclose only selected identity attributes.
- Prove ownership of those attributes.
- Ensure the correctness of the disclosed attributes.

The project includes four applications:
1. **`req_dcc`**: Request a DCC.
2. **`gen_dcc`**: Generate a signed DCC.
3. **`gen_min_dcc`**: Produce a minimalistic DCC with selected attributes.
4. **`check_dcc`**: Validate a signed DCC and/or a minimalistic DCC.

## Technologies Used
- **Language**: Java
- **Build Tool**: Maven
- **Cryptography**: Java Cryptography Architecture (JCA), Bouncy Castle
- **JSON Handling**: Jackson Library

## Setup
### Prerequisites
1. Install Java (JDK 11 or higher).
2. Install Maven.
3. Install oficial SDK and add to project (https://amagovpt.github.io/docs.autenticacao.gov/manual_sdk.html#instala%C3%A7%C3%A3o-do-sdk)

### Clone the Repository
```bash
git clone https://github.com/Hugofos/CA_TP2
cd CA_TP2
```

### Dependencies
The project uses the following Maven dependencies:
- [Jackson](https://github.com/FasterXML/jackson) for JSON serialization/deserialization.
- [Bouncy Castle](https://www.bouncycastle.org/java.html) for cryptographic operations.

## Applications
### 1. `req_dcc`
Generates a json file with the user data and masks for the dcc.

#### Workflow
1. Request user data to Citizen Card.
2. Generate mask for each attribute.

#### Usage
Run the `req_dcc` application.

### 2. `gen_dcc`
Generates a signed DCC for a user.

#### Workflow
1. Generate commitments for each attribute.
2. Sign the DCC using the issuer's private key.

#### Usage
Run the `gen_dcc` application.

### 3. `gen_min_dcc`
Produces a minimalistic DCC with selected attributes and signs it.

#### Workflow
1. Asks user for witch attributes wants to share.
2. Removes unwanted attributes.
3. Signs data just like gen_dcc but using user's citizen card private key.

#### Usage
Run the `gen_min_dcc` application.

### 4. `dcc_validator`
Validates a DCC or a minimalistic DCC.

#### Workflow
1. Asks what type of DCC to validate.
2. Validates the certificate chain.
3. Validates the signatures.
4. Validates the commitment values.

#### Usage
Run the `dcc_validator` application.

### 5. `UI`
Shows the data in a friendly user interface.

#### Workflow
1. Asks what type of JSON file want to load.
2. Opens a window with the data displayed.

#### Usage
Run the `UI` application.

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   ├── dcc_validator/
│   │   │   └── dcc_validator.java
│   │   ├── gen_dcc/
│   │   │   └── gen_dcc.java
│   │   ├── gen_min_dcc/
│   │   │   └── gen_min_dcc.java
│   │   ├── models/
│   │   │   ├── Attribute.java
│   │   │   ├── DCC.java
│   │   │   ├── Min_dcc.java
│   │   │   ├── MinAttribute.java
│   │   │   ├── Signature.java
│   │   │   └── Wrapper.java
│   │   ├── req_dcc/
│   │   │   └── req_dcc.java
│   │   └── UI.java
│   └── resources/
│       ├── rootCA.crt
│       ├── rootCA.key
│       ├── rootCA.srl
│       ├── signingApp.crt
│       ├── signingApp.csr
│       └── signingApp.key
```

## Future Work
- Join everything into one single app with UI.
- Improve apps feedback (ex: colors in validation).

## License
This project is licensed under the MIT License. See the LICENSE file for details.
