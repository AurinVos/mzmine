/*
 * Copyright (c) 2026 The MZmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.mzmine.modules.dataprocessing.id_pubchemsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.mzmine.modules.dataprocessing.id_pubchemsearch.PubChemApiClient.PubChemApiException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PubChemResponseParsingTest {

  @Test
  void waitingResponseExtractsPollingKey() {
    String fixture = """
        {"Waiting":{"ListKey":"123456789","Message":"Your request is running"}}
        """;
    assertTrue(PubChemApiClient.isWaitingResponseInternal(fixture));
    assertEquals("123456789", PubChemApiClient.extractListKeyInternal(fixture));
    assertNull(PubChemApiClient.extractListKeyInternal("{\"Waiting\":{}}"));
    assertNull(PubChemApiClient.extractListKeyInternal("{\"Waiting\":null}"));
    assertFalse(PubChemApiClient.isWaitingResponseInternal("not JSON"));
  }

  @Test
  void cidResponsePreservesNumericAndStringIdentifiers() throws PubChemApiException {
    assertEquals(List.of("2244", "962", "4294967296"),
        PubChemApiClient.parseCidResponseInternal("""
            {"IdentifierList":{"CID":[2244,"962",4294967296]}}
            """));
    assertEquals(List.of(),
        PubChemApiClient.parseCidResponseInternal("{\"IdentifierList\":{\"CID\":[]}}"));
  }

  @Test
  void noRecordsFaultIsAnEmptySearchWhileOtherFaultsAreErrors() throws PubChemApiException {
    String noRecords = """
        {"Fault":{"Code":"PUGREST.NotFound","Message":"No records found"}}
        """;
    assertTrue(PubChemApiClient.parseCidResponseInternal(noRecords).isEmpty());
    String fault = """
        {"Fault":{"Code":"PUGREST.BadRequest","Message":"Invalid formula","Details":"Invalid atom"}}
        """;
    PubChemApiException exception = assertThrows(PubChemApiException.class,
        () -> PubChemApiClient.parseCidResponseInternal(fault));
    assertTrue(exception.getMessage().contains("Invalid formula Details: [Invalid atom]"));
    assertThrows(PubChemApiException.class,
        () -> PubChemApiClient.parseCidResponseInternal("{invalid JSON"));
  }
}
