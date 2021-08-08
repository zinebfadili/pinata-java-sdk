package pinata;

import static util.RequestSender.getRequest;
import static util.RequestSender.postOrPutRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import util.IsIpfs;
import util.Validator;
import util.querybuilder.PinJobsQueryBuilder;
import util.querybuilder.PinListQueryBuilder;
import util.querybuilder.QueryBuilder;

/**
 * Pinata methods.
 */
public class Pinata {

  private String pinataApiKey;
  private String pinataSecretApiKey;
  private static final String BASE_URL = "https://api.pinata.cloud";

  public Pinata(String pinataApiKey, String pinataSecretApiKey) {
    this.pinataApiKey = pinataApiKey;
    this.pinataSecretApiKey = pinataSecretApiKey;
  }

  public Pinata() {
  }

  public PinataResponse pinByHash(String hashToPin) throws PinataException, IOException {
    return pinByHash(pinataApiKey, pinataSecretApiKey, hashToPin, null);
  }

  public PinataResponse pinByHash(String pinataApiKey, String pinataSecretApiKey, String hashToPin)
      throws PinataException, IOException {
    return pinByHash(pinataApiKey, pinataSecretApiKey, hashToPin, null);
  }

  public PinataResponse pinByHash(String hashToPin, JSONObject options)
      throws PinataException, IOException {
    return pinByHash(pinataApiKey, pinataSecretApiKey, hashToPin, options);
  }

  /**
   * Add hash to Pinata's pin queue.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param hashToPin Hash to be pinned
   * @param options Pinning options
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or hash provided
   * @throws IOException when failure to send request
   */
  public PinataResponse pinByHash(String pinataApiKey, String pinataSecretApiKey, String hashToPin,
      JSONObject options) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    final String endpoint = BASE_URL + "/pinning/pinByHash";
    if (StringUtils.isBlank(hashToPin)) {
      throw new PinataException("hashToPin value is required for pinning by hash");
    }

    if (!IsIpfs.isCid(hashToPin)) {
      throw new PinataException("hashToPin value is an invalid IPFS CID");
    }


    JSONObject bodyContent = new JSONObject();
    bodyContent.put("hashToPin", hashToPin);
    bodyContent.put("pinataOptions", new JSONObject());

    if (options != null) {
      if (options.has("pinataOptions")) {
        bodyContent.put("pinataOptions", options.getJSONObject("pinataOptions"));
      }
      if (options.has("pinataMetadata")) {
        Validator.validateMetadata(options.getJSONObject("pinataMetadata"));
        bodyContent.put("pinataMetadata", options.getJSONObject("pinataMetadata"));
      }
    }

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(bodyContent.toString(), mediaType);
    return postOrPutRequest("POST", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse hashMetaData(String ipfsPinHash, JSONObject metadata)
      throws PinataException, IOException {
    return hashMetaData(pinataApiKey, pinataSecretApiKey, ipfsPinHash, metadata);
  }

  /**
   * Change name and keyvalues of content pinned to Pinata.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param ipfsPinHash Content hash
   * @param metadata Pinata Metadata
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or hash provided
   * @throws IOException when failure to send request
   */
  public PinataResponse hashMetaData(String pinataApiKey, String pinataSecretApiKey, String ipfsPinHash,
      JSONObject metadata) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    final String endpoint = BASE_URL + "/pinning/hashMetadata";

    if (StringUtils.isBlank(ipfsPinHash)) {
      throw new
          PinataException("ipfsPinHash value is required for changing the pin policy of a pin");
    }

    if (!IsIpfs.isCid(ipfsPinHash)) {
      throw new PinataException("hashToPin value is an invalid IPFS CID");
    }

    if (metadata == null || metadata.length() == 0) {
      throw new PinataException("no metadata object provided");
    }

    Validator.validateMetadata(metadata);

    JSONObject bodyContent = new JSONObject();
    bodyContent.put("ipfsPinHash", ipfsPinHash);

    if (metadata.has("name")) {
      bodyContent.put("name", metadata.get("name"));
    }
    if (metadata.has("keyvalues")) {
      bodyContent.put("keyvalues", metadata.get("keyvalues"));
    }

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(bodyContent.toString(), mediaType);

    return postOrPutRequest("PUT", endpoint, body, pinataApiKey, pinataSecretApiKey);

  }

  public PinataResponse hashPinPolicy(String ipfsPinHash, JSONObject newPinPolicy)
      throws PinataException, IOException {
    return hashPinPolicy(pinataApiKey, pinataSecretApiKey, ipfsPinHash, newPinPolicy);
  }

  /**
   * Change hash pin policy for individual content.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param ipfsPinHash Content hash
   * @param newPinPolicy New pin policy
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or hash or metadata provided
   * @throws IOException when failure to send request
   */
  public PinataResponse hashPinPolicy(String pinataApiKey, String pinataSecretApiKey,
      String ipfsPinHash, JSONObject newPinPolicy) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);
    Validator.validatePinPolicyStructure(newPinPolicy);

    if (StringUtils.isBlank(ipfsPinHash)) {
      throw new
          PinataException("ipfsPinHash value is required for changing the pin policy of a pin");
    }

    if (!IsIpfs.isCid(ipfsPinHash)) {
      throw new PinataException("hashToPin value is an invalid IPFS CID");
    }

    if (newPinPolicy == null || newPinPolicy.length() == 0) {
      throw new PinataException("newPinPolicy is required for changing the pin policy of a pin");
    }

    String endpoint = BASE_URL + "/pinning/hashPinPolicy";

    JSONObject bodyContent = new JSONObject();
    bodyContent.put("ipfsPinHash", ipfsPinHash);
    bodyContent.put("newPinPolicy", newPinPolicy);

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(bodyContent.toString(), mediaType);

    return postOrPutRequest("PUT", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse pinFileToIpfs(File file, JSONObject options) throws
      PinataException, IOException {
    return pinFileToIpfs(pinataApiKey, pinataSecretApiKey, file, options);
  }

  public PinataResponse pinFileToIpfs(File file) throws PinataException, IOException {
    return pinFileToIpfs(pinataApiKey, pinataSecretApiKey, file, null);
  }

  public PinataResponse pinFileToIpfs(String pinataApiKey, String pinataSecretApiKey, File file)
      throws PinataException, IOException {
    return pinFileToIpfs(pinataApiKey, pinataSecretApiKey, file, null);
  }

  /**
   * Send file to Pinata to pin to IPFS.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param file File to be pinned
   * @param options Pinning options
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or file
   * @throws IOException when failure to send request
   */
  public PinataResponse pinFileToIpfs(String pinataApiKey, String pinataSecretApiKey, File file,
      JSONObject options) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    if (!file.exists()) {
      throw new PinataException("file does not exist");
    }
    String endpoint = BASE_URL + "/pinning/pinFileToIPFS";

    MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file", file.getName(),
            RequestBody.create(file, MediaType.parse("application/octet-stream")));

    if (options != null) {
      addOptionsToMultiPartBody(bodyBuilder, options);
    }

    RequestBody body = bodyBuilder.build();

    return postOrPutRequest("POST", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }


  public PinataResponse pinJsonToIpfs(JSONObject pinataBody) throws PinataException, IOException {
    return pinJsonToIpfs(pinataApiKey, pinataSecretApiKey, pinataBody, null);
  }

  public PinataResponse pinJsonToIpfs(JSONObject pinataBody, JSONObject options)
      throws PinataException, IOException {
    return pinJsonToIpfs(pinataApiKey, pinataSecretApiKey, pinataBody, options);
  }

  public PinataResponse pinJsonToIpfs(String pinataApiKey, String pinataSecretApiKey,
      JSONObject pinataBody) throws PinataException, IOException {
    return pinJsonToIpfs(pinataApiKey, pinataSecretApiKey, pinataBody, null);
  }

  /**
   * Send JSON to Pinata to pin to IPFS.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param pinataBody JSON to be added
   * @param options Pinning options
   * @return Pinata response status and body
   * @throws PinataException when invalid keys
   * @throws IOException when failure to send request
   */
  public PinataResponse pinJsonToIpfs(String pinataApiKey, String pinataSecretApiKey,
      JSONObject pinataBody, JSONObject options) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    JSONObject bodyContent = pinataBody;

    if (options != null) {
      bodyContent = new JSONObject();
      bodyContent.put("pinataContent", pinataBody);

      if (options.has("pinataOptions")) {
        bodyContent.put("pinataOptions", options.getJSONObject("pinataOptions"));
      }
      if (options.has("pinataMetadata")) {
        Validator.validateMetadata(options.getJSONObject("pinataMetadata"));
        bodyContent.put("pinataMetadata", options.getJSONObject("pinataMetadata"));
      }
    }

    String endpoint = BASE_URL + "/pinning/pinJSONToIPFS";

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(bodyContent.toString(), mediaType);

    return postOrPutRequest("POST", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse unpin(String hashToUnpin) throws PinataException, IOException {
    return unpin(pinataApiKey, pinataSecretApiKey, hashToUnpin);
  }

  /**
   * Unpin content.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param hashToUnpin Content hash
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or content hash
   * @throws IOException when failure to send request
   */
  public PinataResponse unpin(String pinataApiKey, String pinataSecretApiKey, String hashToUnpin)
      throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    if (StringUtils.isBlank(hashToUnpin)) {
      throw new PinataException("hashToUnpin value is required for removing a pin from Pinata");
    }
    if (!IsIpfs.isCid(hashToUnpin)) {
      throw new PinataException(hashToUnpin + " is an invalid IPFS CID");
    }

    String endpoint = BASE_URL + "/pinning/unpin/" + hashToUnpin;

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create("", mediaType);

    return postOrPutRequest("POST", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse userPinPolicy(JSONObject newPinPolicy) throws PinataException, IOException {
    return userPinPolicy(pinataApiKey, pinataSecretApiKey, newPinPolicy);
  }

  /**
   * Change account pin policy.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param newPinPolicy New pin policy
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or pin policy
   * @throws IOException when failure to send request
   */
  public PinataResponse userPinPolicy(String pinataApiKey, String pinataSecretApiKey,
      JSONObject newPinPolicy) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);
    Validator.validatePinPolicyStructure(newPinPolicy);

    String endpoint = BASE_URL + "/pinning/userPinPolicy";

    JSONObject bodyContent = new JSONObject();
    bodyContent.put("newPinPolicy", newPinPolicy);

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(bodyContent.toString(), mediaType);

    return postOrPutRequest("PUT", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse pinFromFs(String sourcePath) throws PinataException, IOException {
    return pinFromFs(pinataApiKey, pinataSecretApiKey, sourcePath, null);
  }

  public PinataResponse pinFromFs(String pinataApiKey, String pinataSecretApiKey, String sourcePath)
      throws PinataException, IOException {
    return pinFromFs(pinataApiKey, pinataSecretApiKey, sourcePath, null);
  }

  public PinataResponse pinFromFs(String sourcePath, JSONObject options)
      throws PinataException, IOException {
    return pinFromFs(pinataApiKey, pinataSecretApiKey, sourcePath, options);
  }

  /**
   * Pin content from local file system.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param sourcePath Content path
   * @param options Pinning options
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or path
   * @throws IOException when failure to send request
   */
  public PinataResponse pinFromFs(String pinataApiKey, String pinataSecretApiKey, String sourcePath,
      JSONObject options) throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    final String endpoint = BASE_URL + "/pinning/pinFileToIPFS";

    if (!(new File(sourcePath)).exists()) {
      throw new PinataException("file does not exist");
    }

    if (Files.isRegularFile(Paths.get(sourcePath))) {
      return pinFileToIpfs(pinataApiKey, pinataSecretApiKey, new File(sourcePath), options);
    }

    // Windows
    sourcePath = sourcePath.replace("\\\\", "/");

    String[] splitted = sourcePath.split("/");
    String commonPath = sourcePath
        .substring(0, sourcePath.length() - splitted[splitted.length - 1].length());

    MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    File dir = new File(sourcePath);
    Collection<File> files = FileUtils
        .listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    for (File f : files) {
      String relativePath = f.getPath().replace(commonPath, "").replace("\\", "/");
      bodyBuilder.addFormDataPart("file", relativePath,
          RequestBody.create(f, MediaType.parse("application/octet-stream")));
    }

    if (options != null) {
      addOptionsToMultiPartBody(bodyBuilder, options);
    }

    RequestBody body = bodyBuilder.build();

    return postOrPutRequest("POST", endpoint, body, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse testAuthentication() throws PinataException, IOException {
    return testAuthentication(pinataApiKey, pinataSecretApiKey);
  }

  /**
   * Test authentication to Pinata.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @return Pinata response status and body
   * @throws PinataException when invalid keys
   * @throws IOException when failure to send request
   */
  public PinataResponse testAuthentication(String pinataApiKey, String pinataSecretApiKey)
      throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    String endpoint = BASE_URL + "/data/testAuthentication";

    return getRequest(endpoint, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse userPinnedDataTotal() throws PinataException, IOException {
    return userPinnedDataTotal(pinataApiKey, pinataSecretApiKey);
  }

  /**
   * Retrieve total combined size in bytes of all the content pinned by account on Pinata.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @return Pinata response status and body
   * @throws PinataException when invalid keys
   * @throws IOException when failure to send request
   */
  public PinataResponse userPinnedDataTotal(String pinataApiKey, String pinataSecretApiKey)
      throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    String endpoint = BASE_URL + "/data/userPinnedDataTotal";

    return getRequest(endpoint, pinataApiKey, pinataSecretApiKey);
  }

  public PinataResponse pinList(JSONObject filters) throws PinataException, IOException {
    return pinList(pinataApiKey, pinataSecretApiKey, filters);
  }

  /**
   * Retrieve pin records from Pinata.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param filters Record filters
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or failure to build url
   * @throws IOException when failure to send request
   */
  public PinataResponse pinList(String pinataApiKey, String pinataSecretApiKey, JSONObject filters)
      throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    String endpoint = BASE_URL + "/pinning/pinList";
    QueryBuilder queryBuilder = new PinListQueryBuilder();
    endpoint = queryBuilder.buildUrl(endpoint, filters);

    return getRequest(endpoint, pinataApiKey, pinataSecretApiKey);

  }

  public PinataResponse pinJobs(JSONObject filters) throws PinataException, IOException {
    return pinJobs(pinataApiKey, pinataSecretApiKey, filters);
  }

  /**
   * Retrieve status of all the hashes in the Pinata queue.
   *
   * @param pinataApiKey Pinata API Key
   * @param pinataSecretApiKey Pinata Secret API Key
   * @param filters Hash filters
   * @return Pinata response status and body
   * @throws PinataException when invalid keys or failure to build url
   * @throws IOException when failure to send request
   */
  public PinataResponse pinJobs(String pinataApiKey, String pinataSecretApiKey, JSONObject filters)
      throws PinataException, IOException {
    Validator.validateApiKeys(pinataApiKey, pinataSecretApiKey);

    String endpoint = BASE_URL + "/pinning/pinJobs";
    QueryBuilder queryBuilder = new PinJobsQueryBuilder();
    endpoint = queryBuilder.buildUrl(endpoint, filters);

    return getRequest(endpoint, pinataApiKey, pinataSecretApiKey);

  }

  private MultipartBody.Builder addOptionsToMultiPartBody(MultipartBody.Builder bodyBuilder,
      JSONObject options) throws PinataException {
    if (options.has("pinataOptions")) {
      Validator.validatePinataOptions(options.getJSONObject("pinataOptions"));
      bodyBuilder.addFormDataPart("pinataOptions",
          options.getJSONObject("pinataOptions").toString());
    }
    if (options.has("pinataMetadata")) {
      Validator.validateMetadata(options.getJSONObject("pinataMetadata"));
      bodyBuilder.addFormDataPart("pinataMetadata",
          options.getJSONObject("pinataMetadata").toString());
    }

    return bodyBuilder;
  }

}

