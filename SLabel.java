import nz.sodium.*;
import javax.swing.*;

public class SLabel extends JLabel {

    private final Listener listener;

    public SLabel(Cell<String> text) {
        // Initially set the text to the current value of the cell
        super(text.sample());
        // Listen for updates to the cell
        this.listener = text.listen(t -> {
            // Make sure UI updates happen on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> setText(t));
        });
    }

    @Override
    public void removeNotify() {
        // Unlisten to the cell when the label is removed from the UI
        listener.unlisten();
        super.removeNotify();
    }
}
