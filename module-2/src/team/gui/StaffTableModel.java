package team.gui;

import team.model.StaffMember;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Backs the Support Staff JTable with a live, filterable list of {@link StaffMember} rows. */
class StaffTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "First Name", "Last Name", "Department", "Title", "Yrs w/ Org"
    };

    private List<StaffMember> rows = new ArrayList<>();

    void setRows(List<StaffMember> rows) {
        this.rows = rows;
        fireTableDataChanged();
    }

    StaffMember getRowObject(int row) {
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
        StaffMember s = rows.get(rowIndex);
        switch (columnIndex) {
            case 0: return s.getFirstName();
            case 1: return s.getLastName();
            case 2: return s.getDepartment();
            case 3: return s.getTitle();
            case 4: return s.getYearsWithOrganization();
            default: return "";
        }
    }
}
