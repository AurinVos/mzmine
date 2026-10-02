/*
 * Copyright (c) 2004-2026 The mzmine Development Team
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.mzmine.util.reporting.jasper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lowagie.text.pdf.PdfReader;
import java.nio.charset.StandardCharsets;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintRectangle;
import net.sf.jasperreports.pdf.PdfXmpCreator;
import org.junit.jupiter.api.Test;

class PdfExportWithoutAdobeXmpTest {

  @Test
  void exportsStandardPdfWithoutAdobeXmp() throws Exception {
    assertThrows(ClassNotFoundException.class,
        () -> Class.forName("com.adobe.internal.xmp.XMPMetaFactory"));
    assertFalse(PdfXmpCreator.supported());

    final JasperPrint report = new JasperPrint();
    report.setName("Standard PDF regression");
    report.setPageWidth(200);
    report.setPageHeight(200);
    final JRBasePrintRectangle rectangle = new JRBasePrintRectangle(report.getDefaultStyleProvider());
    rectangle.setX(20);
    rectangle.setY(20);
    rectangle.setWidth(160);
    rectangle.setHeight(160);
    final JRBasePrintPage page = new JRBasePrintPage();
    page.addElement(rectangle);
    report.addPage(page);

    final byte[] pdf = JasperExportManager.exportReportToPdf(report);
    assertTrue(pdf.length > 100);
    assertEquals("%PDF-", new String(pdf, 0, 5, StandardCharsets.US_ASCII));
    try (PdfReader reader = new PdfReader(pdf)) {
      assertEquals(1, reader.getNumberOfPages());
      assertTrue(reader.getPageContent(1).length > 0);
    }
  }
}


