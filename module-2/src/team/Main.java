package team;

import team.data.DataStore;
import team.data.TeamCatalog;
import team.data.TeamCatalog.TeamEntry;
import team.gui.MainFrame;
import team.gui.TeamSelectorDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Application entry point: lets the user pick an NFL team, loads its data, then shows the main GUI window. */
public class Main {

    private static final Path TEAMS_ROOT = Paths.get("data", "teams");

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            List<TeamEntry> teams;
            try {
                teams = TeamCatalog.discoverTeams(TEAMS_ROOT);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Could not read team folders:\n" + ex.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (teams.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No team folders found under data/teams/. Add at least one team folder"
                                + " (with players.csv, coaches.csv, staff.csv, team_info.csv) and try again.",
                        "No Teams Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TeamSelectorDialog selector = new TeamSelectorDialog(null, teams, null);
            selector.setVisible(true);
            if (!selector.isConfirmed()) {
                return;
            }

            DataStore dataStore = new DataStore(selector.getSelectedTeam().directory);
            try {
                dataStore.loadAll();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Could not load data for this team:\n" + ex.getMessage()
                                + "\n\nStarting with empty lists instead.",
                        "Load Error", JOptionPane.WARNING_MESSAGE);
            }
            new MainFrame(dataStore).setVisible(true);
        });
    }
}
