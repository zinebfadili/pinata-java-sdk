package util.querybuilder;

import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;
import pinata.PinataException;
import util.IsIpfs;

public class PinJobsQueryBuilder implements QueryBuilder {

  boolean firstFilterApplied;

  public String buildUrl(String baseUrl, JSONObject filters) throws PinataException {
    if (baseUrl.isBlank()) {
      throw new PinataException("no baseUrl provided");
    }

    if (filters == null || filters.length() == 0) {
      return baseUrl;
    }

    StringBuilder newUrl = new StringBuilder();
    newUrl.append(baseUrl);
    firstFilterApplied = false;

    List<String> validSortValues = Arrays.asList("ASC", "DESC");
    if (filters.has("sort")) {
      if (!(filters.get("sort") instanceof String) || !(validSortValues
          .contains(filters.getString("sort")))) {
        throw new PinataException("Unknown sort value: " + filters.get("sort") + " provided");
      }
      addFilter(newUrl, "sort=" + filters.getString("sort"));
    }

    List<String> validStatus = Arrays.asList("searching",
        "expired",
        "over_free_limit",
        "over_max_size",
        "invalid_object",
        "bad_host_node");

    final String status = "status";
    if (filters.has(status)) {
      if (!(filters.get(status) instanceof String) || !(validStatus
          .contains(filters.getString(status)))) {
        throw new PinataException("Unknown status value: " + filters.get(status) + " provided");
      }
      addFilter(newUrl, "status=" + filters.getString(status));
    }

    final String ipfsPinHash = "ipfs_pin_hash";
    if (filters.has(ipfsPinHash)) {
      if (!(filters.get(ipfsPinHash) instanceof String) || !IsIpfs
          .isCid(filters.getString(ipfsPinHash))) {
        throw new PinataException("Invalid IPFS hash: " + filters.get(ipfsPinHash));
      }
      addFilter(newUrl, "ipfs_pin_hash=" + filters.getString(ipfsPinHash));
    }

    final String limit = "limit";
    if (filters.has(limit)) {
      if (!(filters.get(limit) instanceof Integer) || filters.getInt(limit) <= 0
          || filters.getInt(limit) >= 100) {
        throw new
            PinataException("Invalid limit: " + filters.get(limit) + ". Valid limits are 1-100");
      }
      addFilter(newUrl, "limit=" + filters.getInt(limit));
    }

    final String offset = "offset";
    if (filters.has(offset)) {
      if (!(filters.get(offset) instanceof Integer) || filters.getInt(offset) <= 0) {
        throw new PinataException("Invalid offset: " + filters.get(offset)
            + ". Please provide a positive integer for the offset");
      }
      addFilter(newUrl, "offset=" + filters.getInt(offset));
    }

    return newUrl.toString();
  }

  public void addFilter(StringBuilder newUrl, String filter) {
    if (firstFilterApplied) {
      newUrl.append("&");
    } else {
      firstFilterApplied = true;
      newUrl.append("?");
    }
    newUrl.append(filter);
  }
}
