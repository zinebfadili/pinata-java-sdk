package util.querybuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PinListQueryBuilderTest {

  static final String BASE_URL = "testing.com/test";
  static final String VALID_ISO_DATE = "2019-03-12T20:42:26.743Z";
  static final int VALID_INTEGER = 5;
  PinListQueryBuilder pinListQueryBuilder = new PinListQueryBuilder();

  @Test
  void noBaseUrl() {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinListQueryBuilder.buildUrl("", null);
        });
    assertEquals("no baseUrl provided",
        thrown.getMessage());
  }

  @Test
  void noFilters() {
    try {
      String url = pinListQueryBuilder.buildUrl(BASE_URL, null);
      assertEquals(BASE_URL, url);
    } catch (Exception e) {
      fail();
    }
  }

  @ParameterizedTest
  @CsvSource( value = {
      "{ hashContains: { test: 'test' } }|hashContains value is not a string",
      "{ pinStart: 'test' }|dates must be in valid ISO_8601 format",
      "{ pinEnd: 'test' }|dates must be in valid ISO_8601 format",
      "{ unpinStart: 'test' }|dates must be in valid ISO_8601 format",
      "{ unpinEnd: 'test' }|dates must be in valid ISO_8601 format",
      "{ pinSizeMin: 'test' }|Please make sure the pinSizeMin is a valid positive integer",
      "{ pinSizeMax: 'test'}|Please make sure the pinSizeMax is a valid positive integer",
      "{ status: 'test' }|status value must be either: all, pinned, or unpinned",
      "{ pageLimit: 'test'}|Please make sure the pageLimit is a valid integer between 1-1000",
      "{ pageLimit: 1001 }|Please make sure the pageLimit is a valid integer between 1-1000",
      "{ pageOffset: 'test' }|Please make sure the pageOffset is a positive integer",
      "{ pageOffset: -1 '}|Please make sure the pageOffset is a positive integer",
  }, delimiter = '|')
  void invalidFilterValues(JSONObject filter, String errorMessage) {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinListQueryBuilder.buildUrl(BASE_URL, filter);
        });
    assertEquals(errorMessage,
        thrown.getMessage());
  }

  @ParameterizedTest
  @CsvSource( value ={
      "{ hashContains: 'test' }|"+ BASE_URL + "?hashContains=test&",
      "{ pinStart: '" + VALID_ISO_DATE + "' }|" + BASE_URL + "?pinStart=" + VALID_ISO_DATE + "&",
      "{ pinEnd: '" + VALID_ISO_DATE + "' }|" + BASE_URL + "?pinEnd=" + VALID_ISO_DATE + "&",
      "{ unpinStart: '" + VALID_ISO_DATE + "' }|" + BASE_URL + "?unpinStart=" + VALID_ISO_DATE + "&",
      "{ unpinEnd: '" + VALID_ISO_DATE + "' }|" + BASE_URL + "?unpinEnd=" + VALID_ISO_DATE + "&",
      "{ pinSizeMin:" + VALID_INTEGER + "}|" + BASE_URL + "?pinSizeMin=" + VALID_INTEGER + "&",
      "{ pinSizeMax:" + VALID_INTEGER + "}|" + BASE_URL + "?pinSizeMax=" + VALID_INTEGER + "&",
      "{ status: 'pinned' }|" + BASE_URL + "?status=pinned&",
      "{ pageLimit: 500.7 }|" + BASE_URL + "?pageLimit=500&",
      "{ pageLimit: 500 }|" + BASE_URL + "?pageLimit=500&",
      "{ pageOffset: 500.7 }|" + BASE_URL + "?pageOffset=500&",
      "{ pageOffset: 500 }|" + BASE_URL + "?pageOffset=500&",
  }, delimiter = '|')
  void validFilterValues(JSONObject filters, String expectedUrl) {
    try {
      String url = pinListQueryBuilder.buildUrl(BASE_URL, filters);
      assertEquals(expectedUrl, url);
    } catch (Exception e) {
      fail();
    }
  }

  @ParameterizedTest
  @CsvSource( value = {
      "{ metadata: 'test' }|metadata value must be an object",
      "{ metadata: { keyvalues: 'test' }}|metadata keyvalues must be an object",
  }, delimiter = '|')
  void invalidMetadata(JSONObject filter, String errorMessage) {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinListQueryBuilder.buildUrl(BASE_URL, filter);
        });
    assertEquals(errorMessage,
        thrown.getMessage());
  }

  @ParameterizedTest
  @CsvSource( value = {
      "{ metadata: { keyvalues: { testKeyValue: 'nonObject' }}}"
          + "|keyValue: testKeyValue is not an object",
      "{ metadata: { keyvalues: { testKeyValue: { test: 'test' }}}}"
          + "|keyValue: testKeyValue must have both value and op attributes",
      "{ metadata: { keyvalues: { testKeyValue:{ value: 'test', op: 'invalid' }}}}"
          + "|keyValue op: invalid is not a valid op code",
      "{ metadata: { keyvalues: { testKeyValue: { value: 'test', op: 'between' }}}}"
          + "|Because between op code was passed in, keyValue: testKeyValue must have both also include a secondValue",
      "{ metadata: { keyvalues: { testKeyValue: { value: 'test', secondValue: { test: 'test' }, op: 'between' }}}}"
          + "|Metadata keyvalue secondValue must be a string, boolean, or number",
      "{ metadata: { keyvalues: { testKeyValue: { value: 'test', op: 'notBetween' }}}}"
          + "|Because notBetween op code was passed in, keyValue: testKeyValue must have both also include a secondValue",
      "{ metadata: { keyvalues: { testKeyValue: { value: 'test', secondValue: { test: 'test' }, op: 'notBetween' }}}}"
          + "|Metadata keyvalue secondValue must be a string, boolean, or number",
  }, delimiter = '|')
  void invalidMetadataKeyValues(JSONObject filter, String errorMessage) {
    Exception thrown = assertThrows(Exception.class,
        () -> {
          pinListQueryBuilder.buildUrl(BASE_URL, filter);
        });
    assertEquals(errorMessage,
        thrown.getMessage());
  }

  @Test
  void validMetadata() {
    JSONObject filters = new JSONObject("{ metadata: { name: 'name' } }");
    try {
      String url = pinListQueryBuilder.buildUrl(BASE_URL, filters);
      assertEquals(BASE_URL + "?metadata[name]=name&", url);
    } catch (Exception e) {
      fail();
    }
  }


  @ParameterizedTest
  @ValueSource(strings = {
      "{metadata: { keyvalues: { testKeyValue: { value: 'test', op: 'eq' }}}}",
      "{ metadata: { keyvalues: { testKeyValue: { value: 'test', op: 'eq' }, testKeyValue2: { value: 'test2', op: 'eq' }}}}",
      "{ metadata: { keyvalues: { testKeyValue: { value: 'test', op: 'between', secondValue: 'test2' }}}}",
  })
  void validKeyValues(JSONObject filters) {
    String query = filters.getJSONObject("metadata").get("keyvalues").toString();
    try {
      String url = pinListQueryBuilder.buildUrl(BASE_URL, filters);
      assertEquals(BASE_URL + "?metadata[keyvalues]=" + query, url);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void validKeyValueWithName() {
    JSONObject filters = new JSONObject(
        "{ metadata: { name: 'testName', keyvalues: { testKeyValue: { value: 'test', op: 'between', secondValue: 'test2' } } } }");
    String query = filters.getJSONObject("metadata").get("keyvalues").toString();
    try {
      String url = pinListQueryBuilder.buildUrl(BASE_URL, filters);
      assertEquals(BASE_URL + "?metadata[name]=testName&metadata[keyvalues]=" + query, url);
    } catch (Exception e) {
      fail();
    }
  }

}

