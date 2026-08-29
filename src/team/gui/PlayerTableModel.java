package team.gui;

import team.model.Player;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Backs the Players JTable with a live, filterable list of {@link Player} rows. */
class PlayerTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "#", "First Name", "Last Name", "Pos", "Status", "College", "Exp (yrs)", "Height", "Weight"
    };

    private List<Player> rows = new ArrayList<>();

    void setRows(List<Player> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }

    Player getRowObject(int row) {
        return rows.get(row);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Player p = rows.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getJerseyNumber();
            case 1: return p.getFirstName();
            case 2: return p.getLastName();
            case 3: return p.getPosition();
            case 4: return p.getStatus();
            case 5: return p.getCollege();
            case 6: return p.getYearsExperience();
            case 7: return p.getHeight();
            case 8: return p.getWeightLbs();
            default: return "";
        }
    }
}
