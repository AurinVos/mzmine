/*
 * Copyright (c) 2004-2026 The mzmine Development Team
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
package io.github.mzmine.util.web;


/** Public project links used by the MZmine user interface. */
public enum MZmineLinks {
  WIZARD_DOCUMENTATION("https://mzmine.github.io/mzmine_documentation/wizard.html"),
  PERFORMANCE_DOCU("https://mzmine.github.io/mzmine_documentation/troubleshooting/performance.html"),
  WIZARD_QUICKSTART_VIDEO("https://www.youtube.com/@mzmine"),
  USER_CONSOLE("https://mzmine.github.io/mzmine_documentation/"),
  PRIVACY_POLICY("https://mzmine.github.io/privacy-policy/"),
  TERMS_CONDITIONS("https://mzmine.github.io/terms-and-conditions/");
  private final String url;
  MZmineLinks(String url) { this.url = url; }
  public String getUrl() { return url; }
}
