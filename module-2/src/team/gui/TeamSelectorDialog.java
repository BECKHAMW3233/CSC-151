package team.gui;

import team.data.TeamCatalog.TeamEntry;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modal team picker shown at startup (and from File > Switch Team) grouped by
 * Conference then Division, e.g. NFC > South > Carolina Panthers.
 */
public class TeamSelectorDialog extends JDialog {

    private final JTree tree;
    private TeamEntry selected;
    private boolean confirmed = false;

    public TeamSelectorDialog(Window owner, List<TeamEntry> teams, TeamEntry currentSelection) {
        super(owner, "Select a Team", ModalityType.APPLICATION_MODAL);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("NFL");
        Map<String, DefaultMutableTreeNode> conferenceNodes = new LinkedHashMap<>();
        Map<String, DefaultMutableTreeNode> divisionNodes = new LinkedHashMap<>();

        DefaultMutableTreeNode nodeToSelect = null;
        for (TeamEntry team : teams) {
            DefaultMutableTreeNode confNode = conferenceNodes.computeIfAbsent(team.conference, c -> {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode(c);
                root.add(n);
                return n;
            });
            String divisionKey = team.conference + " > " + team.division;
            DefaultMutableTreeNode divNode = divisionNodes.computeIfAbsent(divisionKey, k -> {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode(team.division);
                confNode.add(n);
                return n;
            });
            DefaultMutableTreeNode teamNode = new DefaultMutableTreeNode(team);
            divNode.add(teamNode);
            if (currentSelection != null && currentSelection.slug.equals(team.slug)) {
                nodeToSelect = teamNode;
            }
        }

        tree = new JTree(new DefaultTreeModel(root));
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        JButton selectButton = new JButton("Select Team");
        selectButton.setEnabled(false);

        tree.addTreeSelectionListener(e -> {
            Object node = tree.getLastSelectedPathComponent();
            boolean isTeam = node instanceof DefaultMutableTreeNode
                    && ((DefaultMutableTreeNode) node).getUserObject() instanceof TeamEntry;
            selectButton.setEnabled(isTeam);
        });
        tree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && selectButton.isEnabled()) {
                    confirmSelection();
                }
            }
        });

        if (nodeToSelect != null) {
            TreePath path = new TreePath(nodeToSelect.getPath());
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
        }

        selectButton.addActionListener(e -> confirmSelection());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(selectButton);
        buttons.add(cancelButton);

        JLabel heading = new JLabel("Choose an NFL team to load:");
        heading.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(selectButton);
        setSize(420, 520);
        setLocationRelativeTo(owner);
    }

    private void confirmSelection() {
        Object node = tree.getLastSelectedPathComponent();
        if (node instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
            if (userObject instanceof TeamEntry) {
                selected = (TeamEntry) userObject;
                confirmed = true;
                dispose();
            }
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public TeamEntry getSelectedTeam() {
        return selected;
    }
}
