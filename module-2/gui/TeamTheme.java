package team.gui;

import javax.swing.UIManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns a team's "Primary Colors" field (a comma-separated list of color names, e.g.
 * "Cardinal Red, Black, White" from team_info.csv) into actual {@link Color} values for
 * theming the GUI. Names specific to one team (e.g. "Honolulu Blue") use that team's real
 * brand hex; plain English color words shared across several teams' data (e.g. "Red",
 * "Gold") use one representative shade, since the CSV itself doesn't specify more precisely.
 */
final class TeamTheme {

    private static final Map<String, Color> NAMED_COLORS = new LinkedHashMap<>();
    static {
        // Generic words shared by multiple teams' data - one representative shade each.
        NAMED_COLORS.put("black", new Color(0x00, 0x00, 0x00));
        NAMED_COLORS.put("white", new Color(0xFF, 0xFF, 0xFF));
        NAMED_COLORS.put("silver", new Color(0xA5, 0xAC, 0xAF));
        NAMED_COLORS.put("gold", new Color(0xFF, 0xB6, 0x12));
        NAMED_COLORS.put("red", new Color(0xC8, 0x10, 0x2E));
        NAMED_COLORS.put("orange", new Color(0xFF, 0x69, 0x00));
        NAMED_COLORS.put("purple", new Color(0x3A, 0x1E, 0x7B));
        NAMED_COLORS.put("navy blue", new Color(0x00, 0x22, 0x44));
        NAMED_COLORS.put("royal blue", new Color(0x00, 0x35, 0x94));
        NAMED_COLORS.put("brown", new Color(0x31, 0x1D, 0x00));
        NAMED_COLORS.put("green", new Color(0x20, 0x37, 0x31));
        NAMED_COLORS.put("burgundy", new Color(0x5A, 0x14, 0x14));

        // Team-specific brand names - real hex values from each team's own style guide.
        NAMED_COLORS.put("cardinal red", new Color(0x97, 0x23, 0x3F));
        NAMED_COLORS.put("panther blue", new Color(0x00, 0x85, 0xCA));
        NAMED_COLORS.put("honolulu blue", new Color(0x00, 0x76, 0xB6));
        NAMED_COLORS.put("giants blue", new Color(0x0B, 0x22, 0x65));
        NAMED_COLORS.put("deep steel blue", new Color(0x03, 0x20, 0x2F));
        NAMED_COLORS.put("battle red", new Color(0xA7, 0x19, 0x30));
        NAMED_COLORS.put("liberty white", new Color(0xFF, 0xFF, 0xFF));
        NAMED_COLORS.put("sol gold", new Color(0xFF, 0xA3, 0x00));
        NAMED_COLORS.put("titans blue", new Color(0x4B, 0x92, 0xDB));
        NAMED_COLORS.put("nautical blue", new Color(0x00, 0x22, 0x44));
        NAMED_COLORS.put("buccaneer red", new Color(0xD5, 0x0A, 0x0A));
        NAMED_COLORS.put("pewter", new Color(0x34, 0x30, 0x2B));
        NAMED_COLORS.put("old gold", new Color(0xD3, 0xBC, 0x8D));
        NAMED_COLORS.put("powder blue", new Color(0x00, 0x80, 0xC6));
        NAMED_COLORS.put("broncos orange", new Color(0xFB, 0x4F, 0x14));
        NAMED_COLORS.put("speed blue", new Color(0x00, 0x2C, 0x5F));
        NAMED_COLORS.put("burnt orange", new Color(0xC8, 0x38, 0x03));
        NAMED_COLORS.put("action green", new Color(0x69, 0xBE, 0x28));
        NAMED_COLORS.put("college navy", new Color(0x00, 0x22, 0x44));
        NAMED_COLORS.put("wolf grey", new Color(0xA5, 0xAC, 0xAF));
        NAMED_COLORS.put("gotham green", new Color(0x12, 0x57, 0x40));
        NAMED_COLORS.put("midnight green", new Color(0x00, 0x4C, 0x54));
        NAMED_COLORS.put("aqua", new Color(0x00, 0x8E, 0x97));
        NAMED_COLORS.put("teal", new Color(0x00, 0x67, 0x78));
        NAMED_COLORS.put("metallic gold", new Color(0x9E, 0x7C, 0x0C));
    }

    private static final Color FALLBACK = new Color(0x50, 0x50, 0x50);

    private TeamTheme() {
    }

    /** Parses a comma-separated "Primary Colors" value (from team_info.csv) into Colors. */
    static List<Color> parseColors(String primaryColorsValue) {
        List<Color> colors = new ArrayList<>();
        if (primaryColorsValue == null) {
            return colors;
        }
        for (String name : primaryColorsValue.split(",")) {
            colors.add(NAMED_COLORS.getOrDefault(name.trim().toLowerCase(), FALLBACK));
        }
        return colors;
    }

    /** Picks black or white text for readable contrast against the given background. */
    static Color readableTextColor(Color background) {
        double luminance = 0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue();
        return luminance > 140 ? Color.BLACK : Color.WHITE;
    }

    /**
     * Applies the team's colors as bold, fully-saturated Nimbus theme keys, so every Nimbus-
     * rendered control (panels, buttons, tabs, tables, dialogs) across the whole app picks them
     * up. Callers must follow this with {@code SwingUtilities.updateComponentTreeUI(...)} on any
     * already-visible windows so their already-built components re-derive this new look.
     */
    static void applyNimbusTheme(List<Color> colors) {
        Color primary = colors.isEmpty() ? new Color(0x43, 0x46, 0x4B) : colors.get(0);
        Color accent = colors.size() > 1 ? colors.get(1) : primary.darker();
        Color text = readableTextColor(primary);
        Color accentText = readableTextColor(accent);

        UIManager.put("control", primary);
        UIManager.put("nimbusBase", primary);
        UIManager.put("nimbusBlueGrey", accent);
        UIManager.put("nimbusLightBackground", primary);
        UIManager.put("info", primary);
        UIManager.put("text", text);
        UIManager.put("controlText", text);
        UIManager.put("textForeground", text);
        UIManager.put("nimbusSelectionBackground", accent);
        UIManager.put("nimbusSelectedText", accentText);
        UIManager.put("nimbusFocus", accent);

        // Nimbus gives Menu/MenuItem/Table/etc. their own hardcoded default foreground
        // colors that do NOT derive from "text"/"controlText" above, so on a dark team
        // color they render as unreadable dark-on-dark unless set here explicitly.
        for (String key : new String[] {
                "Menu.foreground", "MenuItem.foreground", "MenuBar.foreground", "PopupMenu.foreground",
                "Label.foreground", "Table.foreground", "TableHeader.foreground", "TabbedPane.foreground",
                "Button.foreground", "CheckBox.foreground", "ComboBox.foreground",
                "TextField.foreground", "TextArea.foreground", "List.foreground", "ToolTip.foreground" }) {
            UIManager.put(key, text);
        }
    }
}
