package team.gui;

import javax.swing.JOptionPane;
import java.awt.Component;

/** Simple About box describing the project for grading purposes. */
final class AboutDialog {

    private AboutDialog() {
    }

    static void show(Component parent, String teamName) {
        String message = String.join("\n",
                teamName + " — 2026 Team Management",
                " ",
                "A Java/Swing application for managing player, coaching, and support",
                "staff records for the 2026 season, backed by CSV files.",
                " ",
                "Course: Fall 2026 Java Programming (CSC-151-0901), Instructor David Teter",
                "Group Members: William Beckham, Christian Logan, Brandon Malave, Roberto Rendon-Valdez",
                "See CONTRIBUTIONS.md for individual contribution details."
        );
        JOptionPane.showMessageDialog(parent, message, "About This Program", JOptionPane.INFORMATION_MESSAGE);
    }
}
