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
package io.github.mzmine.util;

import org.jetbrains.annotations.Nullable;

/** Outcome of an operation that may carry an informational message. */
public record Result(Status status, @Nullable String message) {
  public enum Status { OK, WARNING, ERROR }
  public static final Result OK = ok();
  public static Result ok() { return new Result(Status.OK, null); }
  public static Result warning(String message) { return new Result(Status.WARNING, message); }
  public static Result error(String message) { return new Result(Status.ERROR, message); }
  public boolean isOk() { return status == Status.OK; }
  public boolean notOk() { return !isOk(); }
  public void throwOnError() {
    if (isError()) throw new IllegalStateException(message == null ? "Operation failed" : message);
  }
  public boolean isError() { return status == Status.ERROR; }
}
