# Pinata SDK

Unofficial Java SDK for [Pinata](https://pinata.cloud).

## Overview

This Pinata Java SDK enables interaction with the [Pinata API](https://pinata.cloud/documentation#GettingStarted).
Pinata is a pinning service that allows you to upload and manage files on IPFS.

## Install

### Maven

For Maven, add the following sections to your pom.xml (replacing $LATEST_VERSION):
```
  <dependencies>
    <dependency>
      <groupId>io.github.zinebfadili</groupId>
      <artifactId>pinata-java-sdk</artifactId>
      <version>$LATEST_VERSION</version>
    </dependency>
  </dependencies>
```

## Setup

Create a Pinata instance without API keys:
```Java
  Pinata pinata = new Pinata();
```
Or, with Pinata API keys:
```Java
  Pinata pinata = new Pinata("yourPinataApiKey", "yourPinataSecretApiKey");
```
Test that you can connect to the API with:
```Java
  // If you created a Pinata instance with keys
  try {
    PinataResponse authResponse = pinata.testAuthentication();
    // If a PinataException hasn't been been thrown, it means that the status is 200  
    System.out.println(authResponse.getStatus()); // 200
  } catch (PinataException e) {
    // The status returned is not 200
  } catch (IOException e) {
    // Unable to send request
  }
  
  // If you created a Pinata instance without keys
  try {
    PinataResponse authResponse = pinata.testAuthentication("yourPinataApiKey", "yourPinataSecretApiKey");
    // If a PinataException hasn't been been thrown, it means that the status is 200  
    System.out.println(authResponse.getStatus()); // 200
  } catch (PinataException e) {
    // The status returned is not 200
  } catch (IOException e) {
    // Unable to send request
  }
```
## Usage

The available operations are:

* Pinning
  * hashMetadata
  * hashPinPolicy
  * pinByHash
  * pinFileToIPFS
  * pinFromFs
  * pinJobs
  * pinJSONToIPFS
  * unpin
  * userPinPolicy

* Data
  * testAuthentication
  * pinList
  * userPinnedDataTotal

Please refer to the [Pinata-SDK documentation](https://github.com/PinataCloud/Pinata-SDK/blob/master/README.md) for the explanation of the purpose of these methods. The method names are the same but in camel case.

If you have created a Pinata instance using your keys, you don't need to specify them again when calling the methods.

As an example, here is a call to pin by hash:
```Java
  // If you created a Pinata instance with keys
  try {
    PinataResponse pinResponse = pinata.pinByHash("yourHash");
    // If a PinataException hasn't been been thrown, it means that the status is 200  
    System.out.println(pinResponse.getStatus()); // 200
  } catch (PinataException e) {
    // The status returned is not 200
  } catch (IOException e) {
    // Unable to send request
  }
  
  // If you created a Pinata instance without keys
  try {
    PinataResponse pinResponse = pinata.pinByHash("yourPinataApiKey", "yourPinataSecretApiKey", "yourHash");
    // If a PinataException hasn't been been thrown, it means that the status is 200  
    System.out.println(pinResponse.getStatus()); // 200
  } catch (PinataException e) {
    // The status returned is not 200
  } catch (IOException e) {
    // Unable to send request
  }
```

The return value contains two attributes:
 * `status` : an Integer containing the response status
 * `body` : a String containing the response body

Methods with optional parameters (for example `pinByHash` and its `options` parameter) are overloaded so that they can be invoked without.

## Remarks

Please note that this version only supports IPFS peer addresses protocols and formats that are handled in the [Multiformats Java Implementation](https://github.com/multiformats/java-multiaddr).
This means, for example, that addresses containing *p2p-webrtc-start* or in *Base1* are not supported.

## License

[MIT](LICENSE)
