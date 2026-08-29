package team.gui;

import team.model.StaffMember;

import javax.swing.*;
import java.awt.*;

/**
 * Modal add/edit form for a {@link StaffMember}. Construct with an existing member to edit it,
 * or with {@code null} to create a new one (an id is supplied separately for new members).
 */
class StaffDialog extends JDialog {

    private final JTextField firstNameField = new JTextField(15);
    private final JTextField lastNameField = new JTextField(15);
    private final JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(35, 18, 100, 1));
    private final JTextField hometownField = new JTextField(15);
    private final JComboBox<String> departmentCombo = new JComboBox<>(StaffMember.DEPARTMENTS);
    private final JTextField titleField = new JTextField(15);
    private final JSpinner yearsSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 75, 1));

    private boolean confirmed = false;
    private final String id;

    StaffDialog(Window owner, String id, StaffMember existing) {
        super(owner, existing == null ? "Add Staff Member" : "Edit Staff Member", ModalityType.APPLICATION_MODAL);
        this.id = id;

        departmentCombo.setEditable(true);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);
        form.add(new JLabel("Age:"));
        form.add(ageSpinner);
        form.add(new JLabel("Hometown:"));
        form.add(hometownField);
        form.add(new JLabel("Department:"));
        form.add(departmentCombo);
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Years With Organization:"));
        form.add(yearsSpinner);

        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            ageSpinner.setValue(existing.getAge());
            hometownField.setText(existing.getHometown());
            departmentCombo.setSelectedItem(existing.getDepartment());
            titleField.setText(existing.getTitle());
            yearsSpinner.setValue(existing.getYearsWithOrganization());
        }

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(okButton);
        buttons.add(cancelButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(okButton);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void onOk() {
        if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()
                || titleField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "First name, last name, and title are required.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        confirmed = true;
        dispose();
    }

    boolean isConfirmed() {
        return confirmed;
    }

    StaffMember getStaffMember() {
        return new StaffMember(
                id,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                (Integer) ageSpinner.getValue(),
                hometownField.getText().trim(),
                (String) departmentCombo.getSelectedItem(),
                titleField.getText().trim(),
                (Integer) yearsSpinner.getValue()
        );
    }
}
