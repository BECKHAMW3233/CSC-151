package team.gui;

import team.model.Coach;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Backs the Coaches JTable with a live, filterable list of {@link Coach} rows. */
class CoachTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "First Name", "Last Name", "Title", "Unit", "Yrs w/ Team", "Yrs Experience"
    };

    private List<Coach> rows = new ArrayList<>();

    void setRows(List<Coach> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }

    Coach getRowObject(int row) {
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
        Coach c = rows.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getFirstName();
            case 1: return c.getLastName();
            case 2: return c.getTitle();
            case 3: return c.getUnit();
            case 4: return c.getYearsWithTeam();
            case 5: return c.getYearsExperienceTotal();
            default: return "";
        }
    }
}
