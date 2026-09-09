package team.gui;

import team.model.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Modal add/edit form for a {@link Player}. Construct with an existing player to edit it,
 * or with {@code null} to create a new one (an id is supplied separately for new players).
 */
class PlayerDialog extends JDialog {

    private final JTextField firstNameField = new JTextField(15);
    private final JTextField lastNameField = new JTextField(15);
    private final JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(24, 18, 50, 1));
    private final JTextField hometownField = new JTextField(15);
    private final JSpinner jerseySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private final JComboBox<String> positionCombo = new JComboBox<>(Player.POSITIONS);
    private final JTextField collegeField = new JTextField(15);
    private final JSpinner experienceSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 25, 1));
    private final JComboBox<String> statusCombo = new JComboBox<>(Player.STATUSES);
    private final JTextField heightField = new JTextField(15);
    private final JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(200, 150, 400, 1));

    private boolean confirmed = false;
    private final String id;

    PlayerDialog(Window owner, String id, Player existing) {
        super(owner, existing == null ? "Add Player" : "Edit Player", ModalityType.APPLICATION_MODAL);
        this.id = id;

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
        form.add(new JLabel("Jersey Number:"));
        form.add(jerseySpinner);
        form.add(new JLabel("Position:"));
        form.add(positionCombo);
        form.add(new JLabel("College:"));
        form.add(collegeField);
        form.add(new JLabel("Years Experience:"));
        form.add(experienceSpinner);
        form.add(new JLabel("Status:"));
        form.add(statusCombo);
        form.add(new JLabel("Height (e.g. 6-2):"));
        form.add(heightField);
        form.add(new JLabel("Weight (lbs):"));
        form.add(weightSpinner);

        if (existing != null) {
            firstNameField.setText(existing.getFirstName());
            lastNameField.setText(existing.getLastName());
            ageSpinner.setValue(existing.getAge());
            hometownField.setText(existing.getHometown());
            jerseySpinner.setValue(existing.getJerseyNumber());
            positionCombo.setSelectedItem(existing.getPosition());
            collegeField.setText(existing.getCollege());
            experienceSpinner.setValue(existing.getYearsExperience());
            statusCombo.setSelectedItem(existing.getStatus());
            heightField.setText(existing.getHeight());
            weightSpinner.setValue(existing.getWeightLbs());
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

    Player getPlayer() {
        return new Player(
                id,
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                (Integer) ageSpinner.getValue(),
                hometownField.getText().trim(),
                (Integer) jerseySpinner.getValue(),
                (String) positionCombo.getSelectedItem(),
                collegeField.getText().trim(),
                (Integer) experienceSpinner.getValue(),
                (String) statusCombo.getSelectedItem(),
                heightField.getText().trim(),
                (Integer) weightSpinner.getValue()
        );
    }
}
