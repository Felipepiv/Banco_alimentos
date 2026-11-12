import ui.DoacaoFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DoacaoFrame frame = new DoacaoFrame();
            frame.setVisible(true);
        });
    }
}
