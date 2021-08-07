package util.querybuilder;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import pinata.PinataException;

public class PinListQueryBuilder implements QueryBuilder {

  public String validateAndReturnDate(String date) throws PinataException {
    try {
      TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(date);
      Instant instant = Instant.from(temporalAccessor);
      java.util.Date.from(instant);
      return date;
    } catch (Exception e) {
      throw new PinataException("dates must be in valid ISO_8601 format");
    }
  }

  public String buildUrl(String baseUrl, JSONObject filters) throws PinataException {
    if (baseUrl.isBlank()) {
      throw new PinataException("no baseUrl provided");
    }

    StringBuilder filterQuery = new StringBuilder();
    filterQuery.append("?");
    StringBuilder metadataQuery = new StringBuilder();

    if (filters == null || filters.length() == 0) {
      return baseUrl;
    }

    final String hashContains = "hashContains";
    if (filters.has(hashContains)) {
      if (!(filters.get(hashContains) instanceof String)) {
        throw new PinataException("hashContains value is not a string");
      }
      filterQuery.append(hashContains + "=").append(filters.getString(hashContains)).append("&");
    }

    if (filters.has("pinStart")) {
      filterQuery.append("pinStart=").append(validateAndReturnDate(filters.getString("pinStart")))
          .append("&");
    }

    if (filters.has("pinEnd")) {
      filterQuery.append("pinEnd=").append(validateAndReturnDate(filters.getString("pinEnd")))
          .append("&");
    }

    if (filters.has("unpinStart")) {
      filterQuery.append("unpinStart=")
          .append(validateAndReturnDate(filters.getString("unpinStart"))).append("&");
    }

    if (filters.has("unpinEnd")) {
      filterQuery.append("unpinEnd=").append(validateAndReturnDate(filters.getString("unpinEnd")))
          .append("&");
    }

    final String pinSizeMin = "pinSizeMin";
    if (filters.has(pinSizeMin)) {
      if (!(NumberUtils.isCreatable(filters.get(pinSizeMin).toString()))
          || filters.getInt(pinSizeMin) < 0) {
        throw new PinataException("Please make sure the pinSizeMin is a valid positive integer");
      }
      filterQuery.append(pinSizeMin + "=").append((int) filters.getDouble(pinSizeMin)).append("&");
    }

    final String pinSizeMax = "pinSizeMax";
    if (filters.has(pinSizeMax)) {
      if (!(NumberUtils.isCreatable(filters.get(pinSizeMax).toString()))
          || filters.getInt(pinSizeMax) < 0) {
        throw new PinataException("Please make sure the pinSizeMax is a valid positive integer");
      }
      filterQuery.append(pinSizeMax + "=").append((int) filters.getDouble(pinSizeMax)).append("&");
    }

    List<String> validStatus = Arrays.asList("all", "pinned", "unpinned");
    final String status = "status";
    if (filters.has(status)) {
      if (!(filters.get(status) instanceof String) || !(validStatus
          .contains(filters.getString(status)))) {
        throw new PinataException("status value must be either: all, pinned, or unpinned");
      }
      filterQuery.append("status=").append(filters.getString(status)).append("&");
    }

    final String pageLimit = "pageLimit";
    if (filters.has(pageLimit)) {
      if (!(NumberUtils.isCreatable(filters.get(pageLimit).toString()))
          || filters.getInt(pageLimit) <= 0
          || filters.getInt(pageLimit) > 1000) {
        throw new
            PinataException("Please make sure the pageLimit is a valid integer between 1-1000");
      }
      filterQuery.append("pageLimit=").append((int) filters.getDouble(pageLimit)).append("&");
    }

    final String pageOffset = "pageOffset";
    if (filters.has(pageOffset)) {
      if (!(NumberUtils.isCreatable(filters.get(pageOffset).toString()))
          || filters.getInt(pageOffset) <= 0) {
        throw new PinataException("Please make sure the pageOffset is a positive integer");
      }
      filterQuery.append("pageOffset=").append((int) filters.getDouble(pageOffset)).append("&");
    }

    final String metadata = "metadata";
    if (filters.has(metadata)) {
      if (!(filters.get(metadata) instanceof JSONObject)) {
        throw new PinataException("metadata value must be an object");
      }

      JSONObject metadataValues = filters.getJSONObject(metadata);

      if (metadataValues.has("name")) {
        metadataQuery.append("metadata[name]=").append(metadataValues.get("name")).append("&");
      }

      final String keyValues = "keyvalues";
      if (metadataValues.has(keyValues)) {
        metadataQuery.append("metadata[keyvalues]=");

        if (!(metadataValues.get(keyValues) instanceof JSONObject)) {
          throw new PinataException("metadata keyvalues must be an object");
        }

        JSONObject prunedKeyValues = new JSONObject();

        JSONObject values = metadataValues.getJSONObject(keyValues);
        Iterator<String> keys = values.keys();
        while (keys.hasNext()) {
          String key = keys.next();

          if (!(values.get(key) instanceof JSONObject)) {
            throw new PinataException("keyValue: " + key + " is not an object");
          }

          JSONObject keyValue = values.getJSONObject(key);

          final String value = "value";
          if (keyValue.length() == 0 || !keyValue.has(value) || !keyValue.has("op")) {
            throw new
                PinataException("keyValue: " + key + " must have both value and op attributes");
          }

          Object val = keyValue.get(value);
          if (!(val instanceof String || val instanceof Boolean || NumberUtils
              .isCreatable(val.toString()))) {
            throw new
                PinataException("Metadata keyvalue values must be strings, booleans, or numbers");
          }

          Object op = keyValue.get("op");

          if (!(op instanceof String)) {
            throw new PinataException("keyValue op: " + op + " is not a valid op code");
          }

          String secondValue = "secondValue";
          switch (keyValue.getString("op")) {
            case "gt":
            case "gte":
            case "lt":
            case "lte":
            case "ne":
            case "eq":
            case "like":
            case "notLike":
            case "iLike":
            case "notILike":
            case "regexp":
            case "iRegexp":
              JSONObject subObj = new JSONObject();
              subObj.put(value, keyValue.get(value));
              subObj.put("op", keyValue.get("op"));
              prunedKeyValues.put(key, subObj);
              break;
            case "between":
            case "notBetween":
              if (!keyValue.has(secondValue)) {
                throw new PinataException(
                    "Because " + keyValue.getString("op") + " op code was passed in, keyValue: "
                        + key + " must have both also include a secondValue");
              }
              Object keySecondValue = keyValue.get(secondValue);
              if (!(keySecondValue instanceof String || keySecondValue instanceof Boolean
                  || NumberUtils.isCreatable(keySecondValue.toString()))) {
                throw new PinataException(
                    "Metadata keyvalue secondValue must be a string, boolean, or number");
              }
              JSONObject betweenSubObj = new JSONObject();
              betweenSubObj.put(value, keyValue.get(value));
              betweenSubObj.put("op", keyValue.get("op"));
              betweenSubObj.put(secondValue, keyValue.get(secondValue));
              prunedKeyValues.put(key, betweenSubObj);
              break;
            default:
              throw new PinataException(
                  "keyValue op: " + keyValue.getString("op") + " is not a valid op code");
          }
        }
        metadataQuery.append(prunedKeyValues);
      }
    }
    return baseUrl + filterQuery.toString() + metadataQuery.toString();
  }

}
