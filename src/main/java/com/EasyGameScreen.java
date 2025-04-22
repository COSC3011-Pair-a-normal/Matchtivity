/**
 * Easy‑difficulty game screen.
 * Loads easy.fxml and injects common UI.
 */
package com;

import javafx.stage.Stage;

public class EasyGameScreen extends BaseGameScreen {
    public EasyGameScreen(MainApp app, Stage stage) {
        super(app, stage, "/easy.fxml");
    }
}
