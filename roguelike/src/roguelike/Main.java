package roguelike;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.ui.MainWindow;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String... args) {
        try {
            LOG.info("Starting game session.");
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOG.fatal("Failed to start game.");
        }

        new MainWindow();
    }

}
