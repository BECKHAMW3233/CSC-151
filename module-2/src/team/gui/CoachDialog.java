package team.gui;

import team.model.Coach;

import javax.swing.*;
import java.awt.*;

/**
 * Modal add/edit form for a {@link Coach}. Construct with an existing coach to edit it,
 * or with {@code null} to create a new one (an id is supplied separately for new coaches).
 */
class CoachDialog extends JDialog {

    private final JTextField firstNameField = new JTextField(15);
    private final JTextField lastNameField = new JTextField(15);
    private final JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(40, 22, 90, 1));
    private final JTextField hometownField = new JTextField(15);
    private final JComboBox<String> titleCombo = new JComboBox<>(Coach.TITLES);
    private final JComboBox<String> unitCombo = new JComboBox<>(Coach.UNITS);
    private final JSpinner yearsWithTeamSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 40, 1));
    private final JSpinner yearsTotalSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));

    private boolean confirmed = false;
    private final String id;

    CoachDialog(Window owner, String id, Coach existing) {
        super(owner, existing == null ? "Add Coach" : "Edit Coach", ModalityType.APPLICATION_MODAL);
        this.id = id;

        titleCombo.setEditable(true);

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
        form.add(new JLabel("Title:"));
        form.add(titleCombo);
        form.add(new JLabel("Unit:"));
        form.add(unitCombo);
        form.add(new JLabel("Years With Team:"));
        form.add(yearsWithTeamSpinner);
        form.add(new JLabel("Years Coaching (Total):"));
        form.add(yearsTotalSpinner);

        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            ageSpinner.setValue(existing.getAge());
            hometownField.setText(existing.getHometown());
            titleCombo.setSelectedItem(existing.getTitle());
            unitCombo.setSelectedItem(existing.getUnit());
            yearsWithTeamSpinner.setValue(existing.getYearsWithTeam());
            yearsTotalSpinner.setValue(existing.getYearsExperienceTotal());
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
        if (firstNameField.getText().isBlank() || lastNameField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "First and last name are required.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        confirmed = true;
        dispose();
    }

    boolean isConfirmed() {
        return confirmed;
    }

    Coach getCoach() {
        return new Coach(
                id,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                (Integer) ageSpinner.getValue(),
                hometownField.getText().trim(),
                String.valueOf(titleCombo.getSelectedItem()).trim(),
                (String) unitCombo.getSelectedItem(),
                (Integer) yearsWithTeamSpinner.getValue(),
                (Integer) yearsTotalSpinner.getValue()
        );
    }
}
