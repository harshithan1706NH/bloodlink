package com.bloodlink.client;

import com.bloodlink.model.BloodGroup;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.util.*;

/** A deliberately small Swing client. Start the Spring API first, then run this class. */
public class BloodLinkClient extends JFrame {
    private static final String API = "http://localhost:8080/api";
    private static final Color RED = new Color(111, 78, 181);
    private static final Color DARK = new Color(45, 38, 79);
    private static final Color BACKGROUND = new Color(247, 245, 252);
    private static final Color VIOLET = new Color(103, 75, 177);
    private static final Color LAVENDER = new Color(238, 232, 248);
    private static final Color LAVENDER_LIGHT = new Color(246, 241, 252);
    private static final String FONT_FAMILY = "Avenir Next";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();
    private final DefaultTableModel donorRows = table("ID", "Name", "Phone", "Group", "City", "Available");
    private final DefaultTableModel requestRows = table("ID", "Patient", "Group", "Hospital", "City", "Units", "Needed by", "Status");
    private final JComboBox<BloodGroup> group = new JComboBox<>(BloodGroup.values());
    private final JTextField city = new JTextField(12);
    private final JTabbedPane tabs = new JTabbedPane();

    public BloodLinkClient() {
        super("BloodLink - Donor Coordination");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(960, 620); setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        tabs.setFont(font(Font.BOLD, 14));
        tabs.setBackground(LAVENDER);
        tabs.add("Dashboard", dashboardPanel());
        tabs.add("Find donors", donorSearchPanel());
        tabs.add("Register donor", donorForm());
        tabs.add("Blood requests", requestPanel());
        tabs.add("Create request", requestForm());
        JPanel content = new JPanel(new BorderLayout()); content.setBackground(BACKGROUND);
        content.add(header(), BorderLayout.NORTH); content.add(tabs, BorderLayout.CENTER);
        add(content); refreshDonors(); refreshRequests();
    }
    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(DARK); p.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));
        JLabel title = new JLabel("BloodLink"); title.setFont(font(Font.BOLD, 26)); title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("  Donor search, requests, and donation coordination"); subtitle.setFont(font(Font.PLAIN, 14)); subtitle.setForeground(new Color(220, 225, 230));
        JPanel words = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); words.setOpaque(false); words.add(title); words.add(subtitle);
        JLabel badge = new JLabel("●  READY TO HELP"); badge.setForeground(new Color(154, 231, 174)); badge.setFont(font(Font.BOLD, 12));
        p.add(words, BorderLayout.WEST); p.add(badge, BorderLayout.EAST); return p;
    }
    private JPanel dashboardPanel() {
        JPanel page = new JPanel(new BorderLayout(18, 18)); page.setBackground(BACKGROUND); page.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JPanel greeting = new JPanel(new BorderLayout()); greeting.setBackground(VIOLET); greeting.setBorder(BorderFactory.createEmptyBorder(22, 25, 22, 25));
        JLabel welcome = new JLabel("Welcome to your donation dashboard"); welcome.setFont(font(Font.BOLD, 23)); welcome.setForeground(Color.WHITE);
        JLabel help = new JLabel("Connect people who need blood with donors in their city."); help.setFont(font(Font.PLAIN, 14)); help.setForeground(new Color(235, 230, 255));
        JPanel greetingText = new JPanel(new GridLayout(2, 1, 0, 5)); greetingText.setOpaque(false); greetingText.add(welcome); greetingText.add(help); greeting.add(greetingText, BorderLayout.WEST); page.add(greeting, BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(2, 1, 0, 20)); center.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0)); cards.setOpaque(false);
        cards.add(statCard("DONORS", "Search local donors", "♥", new Color(130, 94, 202), () -> tabs.setSelectedIndex(1)));
        cards.add(statCard("REQUESTS", "Track urgent needs", "!", new Color(93, 121, 194), () -> tabs.setSelectedIndex(3)));
        cards.add(statCard("COORDINATE", "Make a difference", "✓", new Color(83, 153, 172), () -> tabs.setSelectedIndex(2)));
        center.add(cards);
        JPanel actions = new JPanel(new GridLayout(1, 2, 18, 0)); actions.setOpaque(false);
        actions.add(actionCard("Need blood urgently?", "Create a request and share the patient details.", "Create blood request", () -> tabs.setSelectedIndex(4), new Color(238, 232, 250)));
        actions.add(actionCard("Want to donate?", "Register a donor so patients can find help.", "Register donor", () -> tabs.setSelectedIndex(2), new Color(229, 241, 247)));
        center.add(actions); page.add(center, BorderLayout.CENTER); return page;
    }
    private JPanel statCard(String title, String description, String icon, Color color, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(8, 8)); card.setBackground(color); card.setBorder(BorderFactory.createEmptyBorder(22, 20, 22, 20));
        JLabel symbol = new JLabel(icon, SwingConstants.RIGHT); symbol.setFont(font(Font.BOLD, 32)); symbol.setForeground(new Color(255,255,255,220));
        JLabel heading = new JLabel(title); heading.setFont(font(Font.BOLD, 13)); heading.setForeground(Color.WHITE);
        JLabel desc = new JLabel(description); desc.setFont(font(Font.PLAIN, 14)); desc.setForeground(new Color(255,255,255,225));
        JPanel text = new JPanel(new GridLayout(2,1,0,5)); text.setOpaque(false); text.add(heading); text.add(desc); card.add(text, BorderLayout.SOUTH); card.add(symbol, BorderLayout.NORTH); return clickable(card, action);
    }
    private JPanel actionCard(String title, String description, String buttonText, Runnable action, Color color) {
        JPanel card = new JPanel(new BorderLayout(8, 8)); card.setBackground(color); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,225,230)), BorderFactory.createEmptyBorder(20,20,20,20)));
        JLabel heading = new JLabel(title); heading.setFont(font(Font.BOLD, 17)); heading.setForeground(DARK);
        JLabel desc = new JLabel("<html>" + description + "</html>"); desc.setFont(font(Font.PLAIN, 13)); desc.setForeground(new Color(85, 93, 108));
        JButton button = button(buttonText); button.addActionListener(e -> action.run()); JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); bottom.setOpaque(false); bottom.add(button);
        card.add(heading, BorderLayout.NORTH); card.add(desc, BorderLayout.CENTER); card.add(bottom, BorderLayout.SOUTH); return clickable(card, action);
    }
    private JPanel donorSearchPanel() {
        JPanel p = page(); JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12)); filters.setBackground(LAVENDER);
        filters.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(228, 231, 237)), BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        filters.add(new JLabel("Blood group:")); filters.add(group); filters.add(new JLabel("City:")); filters.add(city);
        JButton search = button("Search available donors"); search.addActionListener(e -> refreshDonors()); filters.add(search);
        JButton showAll = outlineButton("Show all donors"); showAll.addActionListener(e -> { city.setText(""); refreshDonors(); }); filters.add(showAll);
        JPanel top = new JPanel(new BorderLayout(0, 10)); top.setOpaque(false); top.add(sectionHeader("Find a nearby donor", "Filter by blood group and city to see available donors.", VIOLET), BorderLayout.NORTH); top.add(filters, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH); p.add(scroll(donorRows), BorderLayout.CENTER); return p;
    }
    private JPanel donorForm() {
        JTextField name = new JTextField(), phone = new JTextField(), donorCity = new JTextField(); JComboBox<BloodGroup> donorGroup = new JComboBox<>(BloodGroup.values());
        JCheckBox available = new JCheckBox("Available to donate", true); available.setBackground(LAVENDER_LIGHT); JButton save = button("Register donor"); JButton clear = outlineButton("Clear form");
        JPanel p = form(); add(p,"Name",name); add(p,"Phone (unique)",phone); add(p,"Blood group",donorGroup); add(p,"City",donorCity); p.add(available); p.add(save);
        p.add(new JLabel()); p.add(clear); clear.addActionListener(e -> clear(name, phone, donorCity));
        save.addActionListener(e -> post("/donors", Map.of("name",name.getText(),"phone",phone.getText(),"bloodGroup",donorGroup.getSelectedItem().toString(),"city",donorCity.getText(),"available",available.isSelected()), () -> { clear(name,phone,donorCity); refreshDonors(); })); return formScreen("Become a lifesaver", "Add a donor profile so patients can find timely help.", VIOLET, p);
    }
    private JPanel requestPanel() {
        JPanel p = page(); JTable table = styledTable(requestRows); JButton fulfill = button("Mark selected request fulfilled"); JButton refresh = outlineButton("Refresh list");
        fulfill.addActionListener(e -> { int row=table.getSelectedRow(); if(row<0) { message("Select a request first"); return; } patch("/requests/"+requestRows.getValueAt(row,0)+"/fulfill", this::refreshRequests); });
        refresh.addActionListener(e -> refreshRequests());
        p.add(sectionHeader("Current blood requests", "Review urgent cases and mark fulfilled requests when coordination is complete.", VIOLET), BorderLayout.NORTH);
        JScrollPane requestScroll = new JScrollPane(table); requestScroll.getViewport().setBackground(LAVENDER); p.add(requestScroll, BorderLayout.CENTER); JPanel action = new JPanel(new FlowLayout(FlowLayout.RIGHT)); action.setBackground(BACKGROUND); action.add(refresh); action.add(fulfill); p.add(action, BorderLayout.SOUTH); return p;
    }
    private JPanel requestForm() {
        JTextField patient = new JTextField(), hospital = new JTextField(), requestCity = new JTextField(), units = new JTextField("1"), date = new JTextField(LocalDate.now().plusDays(1).toString()); JComboBox<BloodGroup> requestGroup = new JComboBox<>(BloodGroup.values()); JButton save = button("Create request"); JButton clear = outlineButton("Clear form");
        JPanel p=form(); add(p,"Patient name",patient); add(p,"Blood group",requestGroup); add(p,"Hospital",hospital); add(p,"City",requestCity); add(p,"Units needed",units); add(p,"Needed by (YYYY-MM-DD)",date); p.add(save);
        p.add(clear); clear.addActionListener(e -> clear(patient,hospital,requestCity,units,date));
        save.addActionListener(e -> { try { post("/requests", Map.of("patientName",patient.getText(),"bloodGroup",requestGroup.getSelectedItem().toString(),"hospital",hospital.getText(),"city",requestCity.getText(),"unitsNeeded",Integer.parseInt(units.getText()),"neededBy",date.getText()), () -> { clear(patient,hospital,requestCity,units,date); refreshRequests(); }); } catch(NumberFormatException x) { message("Units must be a number"); }}); return formScreen("Create a blood request", "Share the essential patient and hospital details for fast coordination.", VIOLET, p);
    }
    private void refreshDonors() { try { String url=API+"/donors"; if(!city.getText().isBlank()) url += "?bloodGroup="+group.getSelectedItem()+"&city="+java.net.URLEncoder.encode(city.getText(), java.nio.charset.StandardCharsets.UTF_8); donorRows.setRowCount(0); for(Map<String,Object> d: get(url)) donorRows.addRow(new Object[]{d.get("id"),d.get("name"),d.get("phone"),d.get("bloodGroup"),d.get("city"),d.get("available")}); } catch(Exception e) { message("Cannot reach API. Start the Spring Boot server.\n"+e.getMessage()); } }
    private void refreshRequests() { try { requestRows.setRowCount(0); for(Map<String,Object> r: get(API+"/requests")) requestRows.addRow(new Object[]{r.get("id"),r.get("patientName"),r.get("bloodGroup"),r.get("hospital"),r.get("city"),r.get("unitsNeeded"),r.get("neededBy"),r.get("status")}); } catch(Exception ignored) { } }
    private java.util.List<Map<String,Object>> get(String url) throws Exception { HttpResponse<String> r=http.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),HttpResponse.BodyHandlers.ofString()); if(r.statusCode()/100!=2) throw new RuntimeException(r.body()); return json.readValue(r.body(),new TypeReference<>(){}); }
    private void post(String path, Map<String,Object> body, Runnable done) { send("POST",path,body,done); } private void patch(String path,Runnable done) { send("PATCH",path,Map.of(),done); }
    private void send(String method,String path,Map<String,Object> body,Runnable done) { try { HttpRequest r=HttpRequest.newBuilder(URI.create(API+path)).header("Content-Type","application/json").method(method,HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body))).build(); HttpResponse<String> response=http.send(r,HttpResponse.BodyHandlers.ofString()); if(response.statusCode()/100!=2) throw new RuntimeException(response.body()); message("Saved successfully"); done.run(); } catch(Exception e) { message("Could not save: "+e.getMessage()); } }
    private DefaultTableModel table(String... columns) { return new DefaultTableModel(columns,0) { public boolean isCellEditable(int r,int c){return false;} }; }
    private JPanel page() { JPanel p = new JPanel(new BorderLayout(12, 12)); p.setBackground(BACKGROUND); p.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22)); return p; }
    private JPanel formScreen(String title, String description, Color color, JPanel form) { JPanel p = page(); p.add(sectionHeader(title, description, color), BorderLayout.NORTH); p.add(form, BorderLayout.CENTER); return p; }
    private JPanel sectionHeader(String title, String description, Color color) { JPanel p = new JPanel(new GridLayout(2, 1, 0, 4)); p.setBackground(color); p.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18)); JLabel h = new JLabel(title); h.setFont(font(Font.BOLD, 19)); h.setForeground(Color.WHITE); JLabel d = new JLabel(description); d.setFont(font(Font.PLAIN, 13)); d.setForeground(new Color(255,255,255,225)); p.add(h); p.add(d); return p; }
    private JScrollPane scroll(DefaultTableModel model) { JScrollPane pane = new JScrollPane(styledTable(model)); pane.getViewport().setBackground(LAVENDER); return pane; }
    private JTable styledTable(DefaultTableModel model) { JTable t = new JTable(model); t.setBackground(LAVENDER_LIGHT); t.setRowHeight(32); t.setFont(font(Font.PLAIN, 13)); t.setSelectionBackground(new Color(220, 209, 245)); t.setSelectionForeground(DARK); t.setGridColor(new Color(219, 210, 234)); t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() { public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) { Component c = super.getTableCellRendererComponent(table, value, selected, focused, row, column); if (!selected) c.setBackground(row % 2 == 0 ? LAVENDER_LIGHT : LAVENDER); return c; } }); t.getTableHeader().setFont(font(Font.BOLD, 12)); t.getTableHeader().setBackground(DARK); t.getTableHeader().setForeground(Color.WHITE); t.getTableHeader().setReorderingAllowed(false); return t; }
    private JButton button(String text) { JButton b = new JButton(text); b.setFont(font(Font.BOLD, 13)); b.setForeground(Color.WHITE); b.setBackground(RED); b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(9, 15, 9, 15)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b; }
    private JButton outlineButton(String text) { JButton b = new JButton(text); b.setFont(font(Font.BOLD, 13)); b.setForeground(DARK); b.setBackground(LAVENDER); b.setFocusPainted(false); b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 168, 212)), BorderFactory.createEmptyBorder(8, 14, 8, 14))); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b; }
    private <T extends JComponent> T clickable(T component, Runnable action) { activate(component, action); return component; }
    private void activate(JComponent component, Runnable action) { component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); component.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent e) { action.run(); } }); for (Component child : component.getComponents()) if (child instanceof JComponent nested && !(nested instanceof JButton)) activate(nested, action); }
    private JPanel form() { JPanel p=new JPanel(new GridLayout(0,2,18,18)); p.setBackground(LAVENDER); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(216, 208, 235)), BorderFactory.createEmptyBorder(34,130,34,130))); return p; }
    private void add(JPanel p,String label,JComponent input) { JLabel l = new JLabel(label); l.setFont(font(Font.BOLD, 14)); l.setForeground(DARK); p.add(l); input.setFont(font(Font.PLAIN, 15)); input.setBackground(LAVENDER_LIGHT); input.setPreferredSize(new Dimension(270, 42)); input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(184, 169, 220)), BorderFactory.createEmptyBorder(7, 10, 7, 10))); p.add(input); } private Font font(int style, int size) { Font requested = new Font(FONT_FAMILY, style, size); return requested.getFamily().equalsIgnoreCase(FONT_FAMILY) ? requested : new Font("SansSerif", style, size); } private void clear(JTextField... fields){for(JTextField f:fields)f.setText("");} private void message(String s){JOptionPane.showMessageDialog(this,s,"BloodLink",JOptionPane.INFORMATION_MESSAGE);}
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new BloodLinkClient().setVisible(true)); }
}
