import javax.swing.*;
import java.awt.FlowLayout;
import swidgets.*;
import nz.sodium.*;

public class SnapshotExample {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Snapshot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Create inputs
        STextField input1 = new STextField("");
        STextField input2 = new STextField("a");

        // Apply snaphot to input stream
        Cell<String> text1 = input1.sUserChanges.snapshot(input2.text, (t1, t2) -> {
            return t1+" "+t2;
        }).hold("");

        // Create output label
        SLabel label1 = new SLabel(text1);

        frame.add(input2);
        frame.add(input1);
        frame.add(label1);
        frame.setSize(400, 160);
        frame.setVisible(true);
    }

}

