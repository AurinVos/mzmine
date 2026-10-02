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
package io.github.mzmine.gui.mainwindow.workspace;

import java.util.LinkedHashMap;
import java.util.Map;
import javafx.scene.control.Menu;

/** Registry and platform hooks shared by workspace menus. */
public abstract class WorkspaceMenuHelper {
  private static final Map<String, Workspace> WORKSPACES = new LinkedHashMap<>();
  private static String defaultWorkspaceId;
  public static void addWorkspace(Workspace workspace) { WORKSPACES.put(workspace.getUniqueId(), workspace); }
  public static Map<String, Workspace> getWorkspaces() { return Map.copyOf(WORKSPACES); }
  public static String getDefaultWorkspaceId() { return defaultWorkspaceId; }
  public static void setDefaultWorkspace(Workspace workspace) { defaultWorkspaceId = workspace.getUniqueId(); }
  public static Workspace getDefaultWorkspaceOrElse(Workspace fallback) {
    return WORKSPACES.getOrDefault(defaultWorkspaceId, fallback);
  }
  public abstract void openUsersDirectory();
  public abstract void fillRecentProjects(Menu menu);
  public abstract void saveConfiguration();
  public abstract void loadConfiguration();
  public abstract void handleShowLogFile();
  public abstract void versionCheck();
}
