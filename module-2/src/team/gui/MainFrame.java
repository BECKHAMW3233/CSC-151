package team.gui;

import team.data.DataStore;
import team.data.TeamCatalog;
import team.data.TeamCatalog.TeamEntry;
import team.model.Coach;
import team.model.Player;
import team.model.StaffMember;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * The application's main window: a Team Info tab plus Players / Coaches / Support Staff
 * tabs, each with search, add, edit, and remove controls backed by {@link DataStore}.
 */
public class MainFrame extends JFrame {

    private final DataStore dataStore;
    private final JLabel statusLabel = new JLabel(" ");

    private final PlayerTableModel playerTableModel = new PlayerTableModel();
    private final CoachTableModel coachTableModel = new CoachTableModel();
    private final StaffTableModel staffTableModel = new StaffTableModel();

    private JComboBox<String> positionFilterCombo;
    private JComboBox<String> unitFilterCombo;
    private JComboBox<String> departmentFilterCombo;

    private JTextField playerSearchField;
    private JTextField coachSearchField;
    private JTextField staffSearchField;

    private JTable teamInfoTable;
    private JTable playerTable;
    private JTable coachTable;
    private JTable staffTable;
    private DefaultTableModel teamInfoTableModel;

    private JPanel themeHeader;
    private JLabel themeHeaderLabel;

    private static final Path TEAMS_ROOT = Paths.get("data", "teams");
    private static final double ZOOM_STEP = 0.1;
    private static final double MIN_ZOOM = 0.7;
    private static final double MAX_ZOOM = 2.5;
    private static final int BASE_ROW_HEIGHT = 22;
    private final float baseFontSize;
    private double zoomFactor = 1.0;

    public MainFrame(DataStore dataStore) {
        super(buildTitle(dataStore));
        this.dataStore = dataStore;
        Font labelFont = UIManager.getFont("Label.font");
        this.baseFontSize = labelFont != null ? labelFont.getSize2D() : 12f;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Team Info", buildTeamInfoPanel());
        tabs.addTab("Players", buildPlayersPanel());
        tabs.addTab("Coaches", buildCoachesPanel());
        tabs.addTab("Support Staff", buildStaffPanel());

        themeHeaderLabel = new JLabel("", SwingConstants.CENTER);
        themeHeaderLabel.setFont(themeHeaderLabel.getFont().deriveFont(Font.BOLD, 16f));
        themeHeaderLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        themeHeader = new JPanel(new BorderLayout());
        themeHeader.add(themeHeaderLabel, BorderLayout.CENTER);

        add(themeHeader, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        applyTeamTheme();
        refreshAll();
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    // ---------- Menu bar ----------

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save All");
        saveItem.addActionListener(e -> saveAll());
        JMenuItem reloadItem = new JMenuItem("Reload From Files");
        reloadItem.addActionListener(e -> reloadAll());
        JMenuItem switchTeamItem = new JMenuItem("Switch Team...");
        switchTeamItem.addActionListener(e -> switchTeam());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(saveItem);
        fileMenu.add(reloadItem);
        fileMenu.addSeparator();
        fileMenu.add(switchTeamItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        zoomInItem.addActionListener(e -> zoomIn());
        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        zoomOutItem.addActionListener(e -> zoomOut());
        JMenuItem resetZoomItem = new JMenuItem("Reset Zoom");
        resetZoomItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK));
        resetZoomItem.addActionListener(e -> resetZoom());
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetZoomItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> AboutDialog.show(this, teamName()));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    // ---------- Zoom ----------

    private void zoomIn() {
        zoomFactor = Math.min(MAX_ZOOM, zoomFactor + ZOOM_STEP);
        applyZoom();
    }

    private void zoomOut() {
        zoomFactor = Math.max(MIN_ZOOM, zoomFactor - ZOOM_STEP);
        applyZoom();
    }

    private void resetZoom() {
        zoomFactor = 1.0;
        applyZoom();
    }

    private Font currentFont() {
        return new Font(Font.SANS_SERIF, Font.PLAIN, Math.round(baseFontSize * (float) zoomFactor));
    }

    private void applyZoom() {
        Font font = currentFont();
        applyFontRecursively(this, font);
        applyFontRecursively(getJMenuBar(), font);
        int rowHeight = Math.round(BASE_ROW_HEIGHT * (float) zoomFactor);
        for (JTable t : new JTable[] { teamInfoTable, playerTable, coachTable, staffTable }) {
            if (t != null) {
                t.setRowHeight(rowHeight);
            }
        }
        for (JTable t : new JTable[] { playerTable, coachTable, staffTable }) {
            if (t != null) {
                sizeColumnsToFit(t);
            }
        }
        if (teamInfoTable != null) {
            resizeTeamInfoRows();
        }
        setStatus("Zoom: " + Math.round(zoomFactor * 100) + "%");
        revalidate();
        repaint();
    }

    /** Shows a dialog scaled to the window's current zoom level, then re-centers it for its new size. */
    private void showZoomed(JDialog dialog) {
        applyFontRecursively(dialog, currentFont());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static void applyFontRecursively(Component component, Font font) {
        if (component == null) {
            return;
        }
        component.setFont(font);
        if (component instanceof JMenu) {
            for (Component item : ((JMenu) component).getMenuComponents()) {
                applyFontRecursively(item, font);
            }
        } else if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyFontRecursively(child, font);
            }
        }
    }

    private void saveAll() {
        try {
            dataStore.saveAll();
            setStatus("Saved all data to the data/ folder at " + timestamp() + ".");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save data:\n" + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Writes every in-memory change to disk right away, so nothing is lost if the window is closed. */
    private void persist() {
        try {
            dataStore.saveAll();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "The change was applied, but could not be saved to disk:\n" + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadAll() {
        try {
            dataStore.loadAll();
            refreshAll();
            setStatus("Reloaded data from the data/ folder at " + timestamp() + ".");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not reload data:\n" + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void switchTeam() {
        List<TeamEntry> teams;
        try {
            teams = TeamCatalog.discoverTeams(TEAMS_ROOT);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not read team folders:\n" + ex.getMessage(),
                    "Switch Team Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String currentSlug = dataStore.getTeamDir().getFileName().toString();
        TeamEntry current = teams.stream().filter(t -> t.slug.equals(currentSlug)).findFirst().orElse(null);

        TeamSelectorDialog selector = new TeamSelectorDialog(this, teams, current);
        applyFontRecursively(selector, currentFont());
        selector.setVisible(true);
        if (!selector.isConfirmed()) {
            return;
        }
        dataStore.switchTeam(selector.getSelectedTeam().directory);
        try {
            dataStore.loadAll();
            setTitle(buildTitle(dataStore));
            applyTeamTheme();
            refreshAll();
            setStatus("Switched to " + teamName() + ".");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load data for this team:\n" + ex.getMessage(),
                    "Switch Team Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Team theme ----------

    /** Applies the selected team's Primary Colors (from team_info.csv) across the whole window. */
    private void applyTeamTheme() {
        List<Color> colors = TeamTheme.parseColors(dataStore.getTeamInfo().get("Primary Colors"));
        TeamTheme.applyNimbusTheme(colors);
        SwingUtilities.updateComponentTreeUI(this);
        applyFontRecursively(this, currentFont());
        applyFontRecursively(getJMenuBar(), currentFont());

        Color primary = colors.isEmpty() ? themeHeader.getBackground() : colors.get(0);
        Color accent = colors.size() > 1 ? colors.get(1) : primary.darker();
        themeHeader.setBackground(primary);
        themeHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, accent));
        themeHeaderLabel.setText(teamName());
        themeHeaderLabel.setForeground(TeamTheme.readableTextColor(primary));

        applyMenuBarColors(accent);
    }

    /**
     * Nimbus paints the menu bar's own background from the accent color but leaves each
     * JMenu's text at its own fixed dark default instead of following it, so on a dark accent
     * (e.g. a team whose second Primary Color is "Black") the menu text becomes unreadable
     * unless set directly here.
     */
    private void applyMenuBarColors(Color accent) {
        Color menuText = TeamTheme.readableTextColor(accent);
        JMenuBar menuBar = getJMenuBar();
        menuBar.setBackground(accent);
        menuBar.setForeground(menuText);
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            menu.setForeground(menuText);
        }
    }

    // ---------- Team Info tab ----------

    private JComponent buildTeamInfoPanel() {
        teamInfoTableModel = new DefaultTableModel(new Object[] { "Field", "Value" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teamInfoTable = new JTable(teamInfoTableModel);
        teamInfoTable.setRowHeight(BASE_ROW_HEIGHT);
        teamInfoTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        teamInfoTable.getColumnModel().getColumn(1).setPreferredWidth(500);
        teamInfoTable.getColumnModel().getColumn(1).setCellRenderer(new WrappingCellRenderer());
        refreshTeamInfo();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(teamInfoTable), BorderLayout.CENTER);
        return panel;
    }

    private void refreshTeamInfo() {
        teamInfoTableModel.setRowCount(0);
        for (Map.Entry<String, String> entry : dataStore.getTeamInfo().entrySet()) {
            teamInfoTableModel.addRow(new Object[] { entry.getKey(), entry.getValue() });
        }
        resizeTeamInfoRows();
    }

    /** Re-measures every row's wrapped Value text so long fields like Note show in full instead of one clipped line. */
    private void resizeTeamInfoRows() {
        TableCellRenderer renderer = teamInfoTable.getColumnModel().getColumn(1).getCellRenderer();
        for (int row = 0; row < teamInfoTable.getRowCount(); row++) {
            teamInfoTable.prepareRenderer(renderer, row, 1);
        }
    }

    // ---------- Players tab ----------

    private JComponent buildPlayersPanel() {
        playerTable = new JTable(playerTableModel);
        JTable table = playerTable;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedPlayer(table);
                }
            }
        });

        playerSearchField = new JTextField(16);
        String[] positionOptions = prepend("All", Player.POSITIONS);
        positionFilterCombo = new JComboBox<>(positionOptions);

        JButton addButton = new JButton("Add Player");
        JButton editButton = new JButton("Edit Selected");
        JButton removeButton = new JButton("Release Selected");
        JButton clearButton = new JButton("Clear Filters");

        addButton.addActionListener(e -> addPlayer());
        editButton.addActionListener(e -> editSelectedPlayer(table));
        removeButton.addActionListener(e -> removeSelectedPlayer(table));
        clearButton.addActionListener(e -> {
            playerSearchField.setText("");
            positionFilterCombo.setSelectedIndex(0);
            refreshPlayers();
        });

        playerSearchField.getDocument().addDocumentListener(liveFilter(this::refreshPlayers));
        positionFilterCombo.addActionListener(e -> refreshPlayers());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(playerSearchField);
        top.add(new JLabel("Position:"));
        top.add(positionFilterCombo);
        top.add(clearButton);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(removeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void addPlayer() {
        PlayerDialog dialog = new PlayerDialog(this, dataStore.nextPlayerId(), null);
        showZoomed(dialog);
        if (dialog.isConfirmed()) {
            dataStore.addPlayer(dialog.getPlayer());
            persist();
            refreshPlayers();
            setStatus("Added player " + dialog.getPlayer().getFullName() + ".");
        }
    }

    private void editSelectedPlayer(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a player first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Player existing = playerTableModel.getRowObject(table.convertRowIndexToModel(row));
        PlayerDialog dialog = new PlayerDialog(this, existing.getId(), existing);
        showZoomed(dialog);
        if (dialog.isConfirmed()) {
            dataStore.removePlayer(existing.getId());
            dataStore.addPlayer(dialog.getPlayer());
            persist();
            refreshPlayers();
            setStatus("Updated player " + dialog.getPlayer().getFullName() + ".");
        }
    }

    private void removeSelectedPlayer(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a player first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Player existing = playerTableModel.getRowObject(table.convertRowIndexToModel(row));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Release " + existing.getFullName() + " from the roster?",
                "Confirm Release", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataStore.removePlayer(existing.getId());
            persist();
            refreshPlayers();
            setStatus("Released " + existing.getFullName() + " from the roster.");
        }
    }

    private void refreshPlayers() {
        String query = playerSearchField.getText();
        String position = (String) positionFilterCombo.getSelectedItem();
        playerTableModel.setRows(dataStore.searchPlayers(query, position));
        sizeColumnsToFit(playerTable);
        setStatus(dataStore.getPlayers().size() + " players on file.");
    }

    // ---------- Coaches tab ----------

    private JComponent buildCoachesPanel() {
        coachTable = new JTable(coachTableModel);
        JTable table = coachTable;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedCoach(table);
                }
            }
        });

        coachSearchField = new JTextField(16);
        String[] unitOptions = prepend("All", Coach.UNITS);
        unitFilterCombo = new JComboBox<>(unitOptions);

        JButton addButton = new JButton("Add Coach");
        JButton editButton = new JButton("Edit Selected");
        JButton removeButton = new JButton("Remove Selected");
        JButton clearButton = new JButton("Clear Filters");

        addButton.addActionListener(e -> addCoach());
        editButton.addActionListener(e -> editSelectedCoach(table));
        removeButton.addActionListener(e -> removeSelectedCoach(table));
        clearButton.addActionListener(e -> {
            coachSearchField.setText("");
            unitFilterCombo.setSelectedIndex(0);
            refreshCoaches();
        });

        coachSearchField.getDocument().addDocumentListener(liveFilter(this::refreshCoaches));
        unitFilterCombo.addActionListener(e -> refreshCoaches());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(coachSearchField);
        top.add(new JLabel("Unit:"));
        top.add(unitFilterCombo);
        top.add(clearButton);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(removeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void addCoach() {
        CoachDialog dialog = new CoachDialog(this, dataStore.nextCoachId(), null);
        showZoomed(dialog);
        if (dialog.isConfirmed()) {
            dataStore.addCoach(dialog.getCoach());
            persist();
            refreshCoaches();
            setStatus("Added coach " + dialog.getCoach().getFullName() + ".");
        }
    }

    private void editSelectedCoach(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a coach first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Coach existing = coachTableModel.getRowObject(table.convertRowIndexToModel(row));
        CoachDialog dialog = new CoachDialog(this, existing.getId(), existing);
        showZoomed(dialog);
        if (dialog.isConfirmed()) {
            dataStore.removeCoach(existing.getId());
            dataStore.addCoach(dialog.getCoach());
            persist();
            refreshCoaches();
            setStatus("Updated coach " + dialog.getCoach().getFullName() + ".");
        }
    }

    private void removeSelectedCoach(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a coach first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Coach existing = coachTableModel.getRowObject(table.convertRowIndexToModel(row));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + existing.getFullName() + " from the coaching staff?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataStore.removeCoach(existing.getId());
            persist();
            refreshCoaches();
            setStatus("Removed " + existing.getFullName() + " from the coaching staff.");
        }
    }

    private void refreshCoaches() {
        String query = coachSearchField.getText();
        String unit = (String) unitFilterCombo.getSelectedItem();
        coachTableModel.setRows(dataStore.searchCoaches(query, unit));
        sizeColumnsToFit(coachTable);
        setStatus(dataStore.getCoaches().size() + " coaches on file.");
    }

    // ---------- Support Staff tab ----------

    private JComponent buildStaffPanel() {
        staffTable = new JTable(staffTableModel);
        JTable table = staffTable;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedStaff(table);
                }
            }
        });

        staffSearchField = new JTextField(16);
        String[] deptOptions = prepend("All", StaffMember.DEPARTMENTS);
        departmentFilterCombo = new JComboBox<>(deptOptions);

        JButton addButton = new JButton("Add Staff Member");
        JButton editButton = new JButton("Edit Selected");
        JButton removeButton = new JButton("Remove Selected");
        JButton clearButton = new JButton("Clear Filters");

        addButton.addActionListener(e -> addStaff());
        editButton.addActionListener(e -> editSelectedStaff(table));
        removeButton.addActionListener(e -> removeSelectedStaff(table));
        clearButton.addActionListener(e -> {
            staffSearchField.setText("");
            departmentFilterCombo.setSelectedIndex(0);
            refreshStaff();
        });

        staffSearchField.getDocument().addDocumentListener(liveFilter(this::refreshStaff));
        departmentFilterCombo.addActionListener(e -> refreshStaff());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(staffSearchField);
        top.add(new JLabel("Department:"));
        top.add(departmentFilterCombo);
        top.add(clearButton);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(removeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void addStaff() {
        StaffDialog dialog = new StaffDialog(this, dataStore.nextStaffId(), null);
        showZoomed(dialog);
        if (dialog.isConfirmed()) {
            dataStore.addStaff(dialog.getStaffMember());
            persist();
            refreshStaff();
            setStatus("Added staff member " + dialog.getStaffMember().getFullName() + ".");
        }
    }

    private void editSelectedStaff(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StaffMember existing = staffTableModel.getRowObject(table.convertRowIndexToModel(row));
        StaffDialog dialog = new StaffDialog(this, existing.getId(), existing);
        showZoomed(dialog);
        if (dialog.isConfirmed()) {
            dataStore.removeStaff(existing.getId());
            dataStore.addStaff(dialog.getStaffMember());
            persist();
            refreshStaff();
            setStatus("Updated staff member " + dialog.getStaffMember().getFullName() + ".");
        }
    }

    private void removeSelectedStaff(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a staff member first.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StaffMember existing = staffTableModel.getRowObject(table.convertRowIndexToModel(row));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + existing.getFullName() + " from the organization?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataStore.removeStaff(existing.getId());
            persist();
            refreshStaff();
            setStatus("Removed " + existing.getFullName() + " from the organization.");
        }
    }

    private void refreshStaff() {
        String query = staffSearchField.getText();
        String department = (String) departmentFilterCombo.getSelectedItem();
        staffTableModel.setRows(dataStore.searchStaff(query, department));
        sizeColumnsToFit(staffTable);
        setStatus(dataStore.getStaff().size() + " support staff on file.");
    }

    // ---------- Shared helpers ----------

    private void refreshAll() {
        if (teamInfoTableModel != null) {
            refreshTeamInfo();
        }
        refreshPlayers();
        refreshCoaches();
        refreshStaff();
        setStatus("Loaded " + dataStore.getPlayers().size() + " players, "
                + dataStore.getCoaches().size() + " coaches, "
                + dataStore.getStaff().size() + " support staff.");
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private static String buildTitle(DataStore dataStore) {
        String teamName = dataStore.getTeamInfo().getOrDefault("Team Name", "Team");
        return teamName + " — 2026 Team Management";
    }

    private String teamName() {
        return dataStore.getTeamInfo().getOrDefault("Team Name", "Team");
    }

    private static String timestamp() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a"));
    }

    private static String[] prepend(String first, String[] rest) {
        String[] result = new String[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }

    private static DocumentListener liveFilter(Runnable action) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }

    /** Grows each column just wide enough for its header and current cell content, so values aren't clipped. */
    private static void sizeColumnsToFit(JTable table) {
        TableColumnModel columnModel = table.getColumnModel();
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = columnModel.getColumn(col);
            int width = headerRenderer
                    .getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, col)
                    .getPreferredSize().width;
            for (int row = 0; row < table.getRowCount(); row++) {
                Component cell = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
                width = Math.max(width, cell.getPreferredSize().width);
            }
            width += 12;
            column.setPreferredWidth(width);
            column.setWidth(width);
        }
        table.revalidate();
        table.repaint();
    }

    /** Renders a table cell as wrapped, multi-line text and grows its row to fit, instead of clipping to one line. */
    private static class WrappingCellRenderer extends JTextArea implements TableCellRenderer {
        WrappingCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            Color themeText = UIManager.getColor("text");
            setForeground(isSelected ? table.getSelectionForeground()
                    : themeText != null ? themeText : table.getForeground());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setText(value == null ? "" : value.toString());

            int columnWidth = table.getColumnModel().getColumn(column).getWidth();
            setSize(columnWidth, Short.MAX_VALUE);
            int preferredHeight = getPreferredSize().height;
            if (table.getRowHeight(row) != preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }
            return this;
        }
    }
}
