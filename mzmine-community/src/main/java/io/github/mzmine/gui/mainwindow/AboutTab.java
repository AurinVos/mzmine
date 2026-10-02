/*
 * Copyright (c) 2004-2024 The MZmine Development Team
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

package io.github.mzmine.gui.mainwindow;

import io.github.mzmine.gui.mainwindow.dependenciestab.DependenciesTab;
import io.github.mzmine.javafx.components.factories.FxLabels;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.io.SemverVersionReader;
import io.github.mzmine.util.javafx.LightAndDarkModeIcon;
import io.github.mzmine.util.web.MZmineLinks;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class AboutTab extends SimpleTab {

  public AboutTab() {
    super("About mzmine");
    setContent(createContent());
  }

  private Node createContent() {
    VBox contentBox = new VBox(10);
    contentBox.setAlignment(Pos.CENTER);
    contentBox.setPadding(new Insets(20));

    LightAndDarkModeIcon mzmineImage = new LightAndDarkModeIcon(
        "icons/introductiontab/logos_mzio_mzmine.png",
        "icons/introductiontab/logos_mzio_mzmine_light.png", 350, 200);
    mzmineImage.setAlignment(Pos.CENTER);
    contentBox.getChildren().add(mzmineImage);
    contentBox.getChildren().add(new Rectangle(5, 20, Color.TRANSPARENT));

    // Software + version
    contentBox.getChildren().add(FxLabels.newBoldLabel("Software Name"));
    Label softwareName = new Label(
        "mzmine " + SemverVersionReader.getMZmineVersion() + ", by mzio GmbH");
    contentBox.getChildren().add(softwareName);

    // Privacy Policy
    contentBox.getChildren().add(FxLabels.newBoldLabel("Privacy Policy"));
    Hyperlink privacyPolicy = FxLabels.newWebHyperlink(MZmineLinks.PRIVACY_POLICY.getUrl());
    contentBox.getChildren().add(privacyPolicy);

    contentBox.getChildren().add(FxLabels.newBoldLabel("Application License"));
    Label applicationLicense = FxLabels.newLabel(
        "The combined application is distributed under GNU GPL version 3. "
            + "You may use it for any purpose, including commercial work. "
            + "Private use and internal modifications do not require publishing source; "
            + "redistribution must comply with the GPL, including corresponding-source obligations. "
            + "There is no warranty. Original MIT grants and third-party licenses remain in effect. "
            + "Independently installed vendor converters have their own terms.");
    applicationLicense.setMaxWidth(700);
    contentBox.getChildren().add(applicationLicense);
    contentBox.getChildren().add(FxLabels.newHyperlink(
        () -> showLicense("GNU GPL version 3", "GPL-3.0.txt"), "Read GPL version 3"));
    contentBox.getChildren().add(FxLabels.newHyperlink(
        () -> showLicense("Original MZmine MIT grant", "MZmine-MIT.txt"),
        "Read original MZmine MIT grant"));

    // Third-party Libraries
    contentBox.getChildren().add(FxLabels.newBoldLabel("Third-party Libraries"));
    contentBox.getChildren().add(
        FxLabels.newHyperlink(() -> MZmineCore.getDesktop().addTab(new DependenciesTab()),
            "Show libraries"));

    // Copyright Notice
    contentBox.getChildren().add(FxLabels.newBoldLabel("Copyright Notice"));
    Label copyrightNotice = new Label("©2025 by mzio GmbH and mzmine development team");
    contentBox.getChildren().add(copyrightNotice);

    ScrollPane scrollPane = new ScrollPane(contentBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setCenterShape(true);

    return scrollPane;
  }

  private static void showLicense(String title, String filename) {
    try (InputStream stream = AboutTab.class.getClassLoader()
        .getResourceAsStream("licenses/" + filename)) {
      if (stream == null) {
        MZmineCore.getDesktop().displayMessage(title,
            "License resource is missing. See external_tools/licenses/ in the application package.");
        return;
      }
      MZmineCore.getDesktop().displayMessage(title,
          new String(stream.readAllBytes(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      MZmineCore.getDesktop().displayMessage(title, "Cannot read license: " + e.getMessage());
    }
  }
}
