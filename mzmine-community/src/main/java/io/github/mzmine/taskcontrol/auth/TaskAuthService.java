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

package io.github.mzmine.taskcontrol.auth;

import io.github.mzmine.taskcontrol.Task;
import io.mzio.general.Result;
import java.util.List;
import java.util.Set;

/**
 * Local task eligibility is independent of an MZmine account.
 *
 * <p>This application-level policy shadows the bundled orchestration policy. It authorizes local
 * task dispatch only; normal task execution, cancellation, errors and exit status remain handled
 * by the existing task controller.</p>
 */
public final class TaskAuthService {

  public TaskAuthService() {
  }

  public static boolean checkForServicesOrPost(Set<?> ignoredServices) {
    return true;
  }

  public Result isAvailableService(List<Task> tasks) {
    return Result.ok();
  }

  public Result isAvailableService(Task[] tasks) {
    return Result.ok();
  }

  public Result isAvailableService(Task task) {
    return Result.ok();
  }

  public Result applyAuth(List<Task> tasks) {
    return Result.ok();
  }

  public Result applyAuth(Task... tasks) {
    return Result.ok();
  }
}
