package util.querybuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PinJobsQueryBuilderTest {

  static final String BASE_URL = "testing.com/test";
  static final String GOOD_HASH_TO_PIN = "Qma6e8dovfLyiG2UUfdkSHNPAySzrWLX9qVXb44v1muqcp";
  PinJobsQueryBuilder pinJobsQueryBuilder = new PinJobsQueryBuilder();

  @Test
  void noBaseUrl() {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinJobsQueryBuilder.buildUrl("", null);
        });
    assertEquals("no baseUrl provided",
        thrown.getMessage());
  }

  @Test
  void noFilters() {
    try {
      String url = pinJobsQueryBuilder.buildUrl(BASE_URL, null);

      assertEquals(BASE_URL, url);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void validFilters() {
    JSONObject filter = new JSONObject();
    filter.put("status", "searching");
    filter.put("offset", 5);

    try {
      String url = pinJobsQueryBuilder.buildUrl(BASE_URL, filter);
      assertEquals(BASE_URL + "?status=searching&offset=5", url);
    } catch (Exception e) {
      fail();
    }
  }

  @ParameterizedTest
  @CsvSource( value = {
      "{ offset: 'badFilterValue', limit: 5}"
          + "|Invalid offset: badFilterValue. Please provide a positive integer for the offset",
      "{ sort: 'badFilterValue' }|Unknown sort value: badFilterValue provided",
      "{ status: 'badFilterValue' }|Unknown status value: badFilterValue provided",
      "{ ipfs_pin_hash: 'badFilterValue' }|Invalid IPFS hash: badFilterValue",
      "{ limit: 'badFilterValue' }|Invalid limit: badFilterValue. Valid limits are 1-100",
      "{ offset: 'badFilterValue' }|Invalid offset: badFilterValue. Please provide a positive integer for the offset",
  }, delimiter = '|')
  void invalidFilterValues(JSONObject filter, String errorMessage) {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinJobsQueryBuilder.buildUrl(BASE_URL, filter);
        });
    assertEquals(errorMessage,
        thrown.getMessage());
  }


  @ParameterizedTest
  @CsvSource( value = {
      "{ offset: 5}|" + BASE_URL + "?offset=5",
      "{ sort: 'ASC' }|" + BASE_URL + "?sort=ASC",
      "{ status: 'searching' }|" + BASE_URL + "?status=searching",
      "{ ipfs_pin_hash: '" + GOOD_HASH_TO_PIN + "' }|" + BASE_URL + "?ipfs_pin_hash=" + GOOD_HASH_TO_PIN,
      "{ limit: 5 }|" + BASE_URL + "?limit=5",
  }, delimiter = '|')
  void validFilterValues(JSONObject filters, String expectedUrl) {
    try {
      String url = pinJobsQueryBuilder.buildUrl(BASE_URL, filters);
      assertEquals(expectedUrl, url);
    } catch (Exception e) {
      fail();
    }
  }

}
