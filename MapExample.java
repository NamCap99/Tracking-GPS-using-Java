import javax.swing.*;
import java.awt.FlowLayout;
import swidgets.*;
import nz.sodium.*;

public class MapExample {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Create input
        STextField input1 = new STextField("");

        // Transform input stream
        Cell<String> text1 = input1.sUserChanges.map(t -> {
            return t.replace("curse word", "******");
        }).hold("");

        // Create output label
        SLabel label1 = new SLabel(text1);

        frame.add(input1);
        frame.add(label1);
        frame.setSize(400, 160);
        frame.setVisible(true);
    }

}

