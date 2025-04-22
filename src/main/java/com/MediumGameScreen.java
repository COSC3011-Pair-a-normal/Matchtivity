/**
 * Medium‑difficulty game screen.
 * Loads medium.fxml and injects common UI.
 */
package com;

import javafx.stage.Stage;

public class MediumGameScreen extends BaseGameScreen {
    public MediumGameScreen(MainApp app, Stage stage) {
        super(app, stage, "/medium.fxml");
    }
}
