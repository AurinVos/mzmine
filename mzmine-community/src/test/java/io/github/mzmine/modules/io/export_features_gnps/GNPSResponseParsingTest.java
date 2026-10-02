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

package io.github.mzmine.modules.io.export_features_gnps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;

class GNPSResponseParsingTest {

  private static final String JOB_URL = "https://gnps.ucsd.edu/ProteoSAFe/status.jsp?task=fixture";

  @Test
  void extractsJobLinkFromJsonAndRetainsHttpStatusAndBody() {
    String fixture = "{\"url\":\"" + JOB_URL + "\",\"status\":\"success\"}";
    var result = GNPSUtils.getResponse(response(fixture, 200));
    assertEquals(JOB_URL, result.url());
    assertEquals(fixture, result.response());
    assertEquals(200, result.statusCode());
  }

  @Test
  void plainHtmlResponseStillExtractsJobLink() {
    String fixture = "<a href=\"" + JOB_URL + "\">View submitted job</a>";
    assertEquals(JOB_URL, GNPSUtils.getResponse(response(fixture, 200)).url());
  }

  @Test
  void errorsWithoutJobLinksReturnEmptyUrl() {
    assertEquals("", GNPSUtils.getResponse(response("{\"error\":\"Invalid spectrum\"}", 400)).url());
    assertEquals("", GNPSUtils.getResponse(response("Service temporarily unavailable", 503)).url());
  }

  private static CloseableHttpResponse response(String fixture, int status) {
    var response = mock(CloseableHttpResponse.class);
    when(response.getEntity()).thenReturn(new StringEntity(fixture, StandardCharsets.UTF_8));
    when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, status, ""));
    return response;
  }
}
