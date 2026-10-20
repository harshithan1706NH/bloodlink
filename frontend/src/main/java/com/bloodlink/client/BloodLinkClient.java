package com.bloodlink.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Swing frontend for the BloodLink REST API. */
public class BloodLinkClient extends JFrame {
    private static final String API = System.getProperty(
            "bloodlink.api", System.getenv().getOrDefault("BLOODLINK_API_URL", "http://localhost:8080/api"));
    private static final Color RED = new Color(180, 45, 65);
    private static final Color DARK = new Color(76, 24, 39);
    private static final Color BACKGROUND = new Color(250, 246, 247);
    private static final Color ROSE = new Color(166, 43, 63);
    private static final Color PALE = new Color(246, 232, 235);
    private static final Color PALE_LIGHT = new Color(255, 250, 250);
    private static final String FONT_FAMILY = "Avenir Next";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();
    private final DefaultTableModel donorRows = table("ID", "Name", "Phone", "Group", "City", "Available");
    private final DefaultTableModel requestRows = table("ID", "Patient", "Group", "Hospital", "City", "Units", "Needed by", "Status");
    private final JComboBox<BloodGroup> searchGroup = new JComboBox<>(BloodGroup.values());
    private final JTextField searchCity = new JTextField(12);
    private final JTabbedPane tabs = new JTabbedPane();

    public BloodLinkClient() {
        super("BloodLink - Donor Coordination");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 620);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        tabs.setFont(font(Font.BOLD, 14));
        tabs.setBackground(PALE);
        tabs.add("Dashboard", dashboardPanel());
        tabs.add("Find donors", donorSearchPanel());
        tabs.add("Register donor", donorForm());
        tabs.add("Blood requests", requestPanel());
        tabs.add("Create request", requestForm());

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND);
        content.add(header(), BorderLayout.NORTH);
        content.add(tabs, BorderLayout.CENTER);
        add(content);
        refreshDonors();
        refreshRequests();
    }

    private JPanel header() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        JLabel title = new JLabel("BloodLink");
        title.setFont(font(Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("  Donor search, requests, and donation coordination");
        subtitle.setFont(font(Font.PLAIN, 14));
        subtitle.setForeground(new Color(235, 225, 230));
        JPanel words = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        words.setOpaque(false);
        words.add(title);
        words.add(subtitle);
        JLabel badge = new JLabel("●  READY TO HELP");
        badge.setForeground(new Color(154, 231, 174));
        badge.setFont(font(Font.BOLD, 12));
        panel.add(words, BorderLayout.WEST);
        panel.add(badge, BorderLayout.EAST);
        return panel;
    }

    private JPanel dashboardPanel() {
        JPanel page = page();
        JPanel greeting = new JPanel(new BorderLayout());
        greeting.setBackground(ROSE);
        greeting.setBorder(BorderFactory.createEmptyBorder(22, 25, 22, 25));
        JLabel welcome = new JLabel("Welcome to your donation dashboard");
        welcome.setFont(font(Font.BOLD, 23));
        welcome.setForeground(Color.WHITE);
        JLabel help = new JLabel("Connect people who need blood with donors in their city.");
        help.setFont(font(Font.PLAIN, 14));
        help.setForeground(new Color(255, 235, 240));
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 5));
        text.setOpaque(false);
        text.add(welcome);
        text.add(help);
        greeting.add(text, BorderLayout.WEST);
        page.add(greeting, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setOpaque(false);
        cards.add(statCard("DONORS", "Search local donors", "♥", RED, () -> tabs.setSelectedIndex(1)));
        cards.add(statCard("REQUESTS", "Track urgent needs", "!", new Color(145, 55, 75), () -> tabs.setSelectedIndex(3)));
        cards.add(statCard("COORDINATE", "Make a difference", "✓", new Color(105, 75, 88), () -> tabs.setSelectedIndex(2)));
        JPanel actions = new JPanel(new GridLayout(1, 2, 18, 0));
        actions.setOpaque(false);
        actions.add(actionCard("Need blood urgently?", "Create a request and share the patient details.", "Create blood request", () -> tabs.setSelectedIndex(4)));
        actions.add(actionCard("Want to donate?", "Register a donor so patients can find help.", "Register donor", () -> tabs.setSelectedIndex(2)));
        JPanel center = new JPanel(new GridLayout(2, 1, 0, 20));
        center.setOpaque(false);
        center.add(cards);
        center.add(actions);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private JPanel statCard(String title, String description, String icon, Color color, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));
        JLabel symbol = new JLabel(icon, SwingConstants.RIGHT);
        symbol.setFont(font(Font.BOLD, 32));
        symbol.setForeground(Color.WHITE);
        JLabel heading = new JLabel(title);
        heading.setFont(font(Font.BOLD, 13));
        heading.setForeground(Color.WHITE);
        JLabel desc = new JLabel(description);
        desc.setFont(font(Font.PLAIN, 14));
        desc.setForeground(Color.WHITE);
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 5));
        text.setOpaque(false);
        text.add(heading);
        text.add(desc);
        card.add(text, BorderLayout.SOUTH);
        card.add(symbol, BorderLayout.NORTH);
        return clickable(card, action);
    }

    private JPanel actionCard(String title, String description, String buttonText, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(PALE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(228, 215, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        JLabel heading = new JLabel(title);
        heading.setFont(font(Font.BOLD, 17));
        heading.setForeground(DARK);
        JLabel desc = new JLabel("<html>" + description + "</html>");
        desc.setFont(font(Font.PLAIN, 13));
        JButton button = button(buttonText);
        button.addActionListener(event -> action.run());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(button);
        card.add(heading, BorderLayout.NORTH);
        card.add(desc, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return clickable(card, action);
    }

    private JPanel donorSearchPanel() {
        JPanel panel = page();
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        filters.setBackground(PALE);
        filters.add(new JLabel("Blood group:"));
        filters.add(searchGroup);
        filters.add(new JLabel("City:"));
        filters.add(searchCity);
        JButton search = button("Search available donors");
        search.addActionListener(event -> refreshDonors());
        filters.add(search);
        JButton showAll = outlineButton("Show all donors");
        showAll.addActionListener(event -> { searchCity.setText(""); refreshDonors(); });
        filters.add(showAll);
        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(sectionHeader("Find a nearby donor", "Filter by blood group and city to see available donors."), BorderLayout.NORTH);
        top.add(filters, BorderLayout.CENTER);
        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll(donorRows), BorderLayout.CENTER);
        return panel;
    }

    private JPanel donorForm() {
        JTextField name = new JTextField();
        JTextField phone = new JTextField();
        JTextField donorCity = new JTextField();
        JComboBox<BloodGroup> donorGroup = new JComboBox<>(BloodGroup.values());
        JCheckBox available = new JCheckBox("Available to donate", true);
        available.setBackground(PALE_LIGHT);
        JButton save = button("Register donor");
        JButton clear = outlineButton("Clear form");
        JPanel form = form();
        add(form, "Name", name);
        add(form, "Phone (unique)", phone);
        add(form, "Blood group", donorGroup);
        add(form, "City", donorCity);
        form.add(available);
        form.add(save);
        form.add(new JLabel());
        form.add(clear);
        clear.addActionListener(event -> clear(name, phone, donorCity));
        save.addActionListener(event -> post("/donors", Map.of(
                "name", name.getText().trim(),
                "phone", phone.getText().trim(),
                "bloodGroup", donorGroup.getSelectedItem().toString(),
                "city", donorCity.getText().trim(),
                "available", available.isSelected()), () -> {
            clear(name, phone, donorCity);
            refreshDonors();
        }));
        return formScreen("Become a lifesaver", "Add a donor profile so patients can find timely help.", form);
    }

    private JPanel requestPanel() {
        JPanel panel = page();
        JTable table = styledTable(requestRows);
        JButton fulfill = button("Mark selected request fulfilled");
        JButton refresh = outlineButton("Refresh list");
        fulfill.addActionListener(event -> {
            int row = table.getSelectedRow();
            if (row < 0) { message("Select a request first"); return; }
            patch("/requests/" + requestRows.getValueAt(row, 0) + "/fulfill", this::refreshRequests);
        });
        refresh.addActionListener(event -> refreshRequests());
        panel.add(sectionHeader("Current blood requests", "Review requests and mark them fulfilled when coordination is complete."), BorderLayout.NORTH);
        JScrollPane requestScroll = new JScrollPane(table);
        requestScroll.getViewport().setBackground(PALE);
        panel.add(requestScroll, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(BACKGROUND);
        actions.add(refresh);
        actions.add(fulfill);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel requestForm() {
        JTextField patient = new JTextField();
        JTextField hospital = new JTextField();
        JTextField requestCity = new JTextField();
        JTextField units = new JTextField("1");
        JTextField date = new JTextField(LocalDate.now().plusDays(1).toString());
        JComboBox<BloodGroup> requestGroup = new JComboBox<>(BloodGroup.values());
        JButton save = button("Create request");
        JButton clear = outlineButton("Clear form");
        JPanel form = form();
        add(form, "Patient name", patient);
        add(form, "Blood group", requestGroup);
        add(form, "Hospital", hospital);
        add(form, "City", requestCity);
        add(form, "Units needed", units);
        add(form, "Needed by (YYYY-MM-DD)", date);
        form.add(save);
        form.add(clear);
        clear.addActionListener(event -> clear(patient, hospital, requestCity, units, date));
        save.addActionListener(event -> {
            try {
                post("/requests", Map.of(
                        "patientName", patient.getText().trim(),
                        "bloodGroup", requestGroup.getSelectedItem().toString(),
                        "hospital", hospital.getText().trim(),
                        "city", requestCity.getText().trim(),
                        "unitsNeeded", Integer.parseInt(units.getText().trim()),
                        "neededBy", date.getText().trim()), () -> {
                    clear(patient, hospital, requestCity, units, date);
                    refreshRequests();
                });
            } catch (NumberFormatException exception) {
                message("Units must be a number");
            }
        });
        return formScreen("Create a blood request", "Share the essential patient and hospital details for coordination.", form);
    }

    private void refreshDonors() {
        try {
            String url = API + "/donors";
            if (!searchCity.getText().isBlank()) {
                url += "?bloodGroup=" + searchGroup.getSelectedItem()
                        + "&city=" + URLEncoder.encode(searchCity.getText().trim(), StandardCharsets.UTF_8);
            }
            donorRows.setRowCount(0);
            for (Map<String, Object> donor : get(url)) {
                donorRows.addRow(new Object[]{donor.get("id"), donor.get("name"), donor.get("phone"),
                        donor.get("bloodGroup"), donor.get("city"), donor.get("available")});
            }
        } catch (Exception exception) {
            message("Cannot reach API. Start the Spring Boot server.\n" + exception.getMessage());
        }
    }

    private void refreshRequests() {
        try {
            requestRows.setRowCount(0);
            for (Map<String, Object> request : get(API + "/requests")) {
                requestRows.addRow(new Object[]{request.get("id"), request.get("patientName"), request.get("bloodGroup"),
                        request.get("hospital"), request.get("city"), request.get("unitsNeeded"),
                        request.get("neededBy"), request.get("status")});
            }
        } catch (Exception exception) {
            // The donor search tab reports connection errors; keep startup quiet if the API is still starting.
        }
    }

    private List<Map<String, Object>> get(String url) throws Exception {
        HttpResponse<String> response = http.send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) throw new IllegalStateException(response.body());
        return json.readValue(response.body(), new TypeReference<>() { });
    }

    private void post(String path, Map<String, Object> body, Runnable done) {
        send("POST", path, body, done);
    }

    private void patch(String path, Runnable done) {
        send("PATCH", path, Map.of(), done);
    }

    private void send(String method, String path, Map<String, Object> body, Runnable done) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(API + path))
                    .header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) throw new IllegalStateException(response.body());
            message("Saved successfully");
            done.run();
        } catch (Exception exception) {
            message("Could not save: " + exception.getMessage());
        }
    }

    private DefaultTableModel table(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        return panel;
    }

    private JPanel formScreen(String title, String description, JPanel form) {
        JPanel panel = page();
        panel.add(sectionHeader(title, description), BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel sectionHeader(String title, String description) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setBackground(ROSE);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel heading = new JLabel(title);
        heading.setFont(font(Font.BOLD, 19));
        heading.setForeground(Color.WHITE);
        JLabel detail = new JLabel(description);
        detail.setFont(font(Font.PLAIN, 13));
        detail.setForeground(new Color(255, 235, 240));
        panel.add(heading);
        panel.add(detail);
        return panel;
    }

    private JScrollPane scroll(DefaultTableModel model) {
        JScrollPane pane = new JScrollPane(styledTable(model));
        pane.getViewport().setBackground(PALE);
        return pane;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(PALE_LIGHT);
        table.setRowHeight(32);
        table.setFont(font(Font.PLAIN, 13));
        table.setSelectionBackground(new Color(238, 205, 214));
        table.setSelectionForeground(DARK);
        table.setGridColor(new Color(225, 205, 212));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value, boolean selected,
                                                          boolean focused, int row, int column) {
                Component component = super.getTableCellRendererComponent(source, value, selected, focused, row, column);
                if (!selected) component.setBackground(row % 2 == 0 ? PALE_LIGHT : PALE);
                return component;
            }
        });
        table.getTableHeader().setFont(font(Font.BOLD, 12));
        table.getTableHeader().setBackground(DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(RED);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton outlineButton(String text) {
        JButton button = new JButton(text);
        button.setFont(font(Font.BOLD, 13));
        button.setForeground(DARK);
        button.setBackground(PALE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 160, 173)),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private <T extends JComponent> T clickable(T component, Runnable action) {
        activate(component, action);
        return component;
    }

    private void activate(JComponent component, Runnable action) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent event) { action.run(); }
        });
        for (Component child : component.getComponents()) {
            if (child instanceof JComponent nested && !(nested instanceof JButton)) activate(nested, action);
        }
    }

    private JPanel form() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 18, 18));
        panel.setBackground(PALE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 205, 212)),
                BorderFactory.createEmptyBorder(34, 110, 34, 110)));
        return panel;
    }

    private void add(JPanel panel, String label, JComponent input) {
        JLabel text = new JLabel(label);
        text.setFont(font(Font.BOLD, 14));
        text.setForeground(DARK);
        panel.add(text);
        input.setFont(font(Font.PLAIN, 15));
        input.setBackground(PALE_LIGHT);
        input.setPreferredSize(new Dimension(270, 42));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 160, 173)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        panel.add(input);
    }

    private Font font(int style, int size) {
        Font requested = new Font(FONT_FAMILY, style, size);
        return requested.getFamily().equalsIgnoreCase(FONT_FAMILY)
                ? requested : new Font("SansSerif", style, size);
    }

    private void clear(JTextField... fields) {
        for (JTextField field : fields) field.setText("");
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text, "BloodLink", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodLinkClient().setVisible(true));
    }
}
