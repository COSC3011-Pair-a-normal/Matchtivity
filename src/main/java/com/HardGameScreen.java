/**
 * Hard‑difficulty game screen.
 * Loads hard.fxml and injects common UI.
 */
package com;

import javafx.stage.Stage;

public class HardGameScreen extends BaseGameScreen {
    public HardGameScreen(MainApp app, Stage stage) {
        super(app, stage, "/hard.fxml");
    }
}
