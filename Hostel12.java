import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class Hostel12 extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, ageField, deptField, admNoField, roomNoField, roomTypeField, bedCountField;
    private final JLabel totalCountLabel;
    private final JLabel vacancyLabel;

    public Hostel12() {
        setTitle("🏠 Hostel Management System");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JLabel header = new JLabel("Hostel Management System", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        
        String[] columns = {"ID", "Name", "Age", "Department", "Admission No", "Room No", "Room Type", "Bed Count"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        formPanel.setBackground(new Color(236, 240, 241));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        nameField = new JTextField();
        ageField = new JTextField();
        deptField = new JTextField();
        admNoField = new JTextField();
        roomNoField = new JTextField();
        roomTypeField = new JTextField();
        bedCountField = new JTextField();

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = new Color(44, 62, 80);

        formPanel.add(createLabel("Name:", labelFont, labelColor)); formPanel.add(nameField);
        formPanel.add(createLabel("Age:", labelFont, labelColor)); formPanel.add(ageField);
        formPanel.add(createLabel("Department:", labelFont, labelColor)); formPanel.add(deptField);
        formPanel.add(createLabel("Admission No:", labelFont, labelColor)); formPanel.add(admNoField);
        formPanel.add(createLabel("Room No:", labelFont, labelColor)); formPanel.add(roomNoField);
        formPanel.add(createLabel("Room Type:", labelFont, labelColor)); formPanel.add(roomTypeField);
        formPanel.add(createLabel("Bed Count:", labelFont, labelColor)); formPanel.add(bedCountField);

        
        JButton addBtn = createButton("➕ Add");
        JButton updateBtn = createButton("✏️ Update");
        JButton deleteBtn = createButton("🗑️ Delete");

        formPanel.add(addBtn); formPanel.add(updateBtn);
        formPanel.add(deleteBtn);

        totalCountLabel = createLabel("Total Students: 0", labelFont, labelColor);
        vacancyLabel = createLabel("Vacant Beds: ", labelFont, labelColor);
        formPanel.add(totalCountLabel); formPanel.add(vacancyLabel);

        add(formPanel, BorderLayout.SOUTH);

        
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                nameField.setText(model.getValueAt(row, 1).toString());
                ageField.setText(model.getValueAt(row, 2).toString());
                deptField.setText(model.getValueAt(row, 3).toString());
                admNoField.setText(model.getValueAt(row, 4).toString());
                roomNoField.setText(model.getValueAt(row, 5).toString());
                roomTypeField.setText(model.getValueAt(row, 6).toString());
                bedCountField.setText(model.getValueAt(row, 7).toString());
            }
        });

        loadStudentData();
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    private void loadStudentData() {
        model.setRowCount(0);
        int total = 0;
        int usedBeds = 0;

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM hostel_students");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                total++;
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("student_name"),
                    rs.getInt("age"),
                    rs.getString("department"),
                    rs.getString("admission_no"),
                    rs.getString("room_no"),
                    rs.getString("room_type"),
                    rs.getInt("bed_count")
                };
                usedBeds += rs.getInt("bed_count");
                model.addRow(row);
            }
            totalCountLabel.setText("Total Students: " + total);
            vacancyLabel.setText("Vacant Beds: " + (50 - usedBeds)); // assuming 50 beds total
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/hostel";
        String user = "root";
        String password = "#Maddy@007";
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, user, password);
    }

    private void addStudent() {
        try {
            if (nameField.getText().trim().isEmpty() || ageField.getText().trim().isEmpty() || deptField.getText().trim().isEmpty() ||
                admNoField.getText().trim().isEmpty() || roomNoField.getText().trim().isEmpty() || roomTypeField.getText().trim().isEmpty() ||
                bedCountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!");
                return;
            }

            int age = Integer.parseInt(ageField.getText());
            int bedCount = Integer.parseInt(bedCountField.getText());

            if (age <= 0 || bedCount <= 0) {
                JOptionPane.showMessageDialog(this, "Age and Bed Count must be positive numbers!");
                return;
            }

            try (Connection conn = getConnection()) {
                String query = "INSERT INTO hostel_students (student_name, age, department, admission_no, room_no, room_type, bed_count) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, nameField.getText());
                ps.setInt(2, age);
                ps.setString(3, deptField.getText());
                ps.setString(4, admNoField.getText());
                ps.setString(5, roomNoField.getText());
                ps.setString(6, roomTypeField.getText());
                ps.setInt(7, bedCount);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student Added!");
                loadStudentData();
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate Admission No. Not Allowed!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to update.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        try {
            if (nameField.getText().trim().isEmpty() || ageField.getText().trim().isEmpty() || deptField.getText().trim().isEmpty() ||
                admNoField.getText().trim().isEmpty() || roomNoField.getText().trim().isEmpty() || roomTypeField.getText().trim().isEmpty() ||
                bedCountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!");
                return;
            }

            int age = Integer.parseInt(ageField.getText());
            int bedCount = Integer.parseInt(bedCountField.getText());

            if (age <= 0 || bedCount <= 0) {
                JOptionPane.showMessageDialog(this, "Age and Bed Count must be positive numbers!");
                return;
            }

            try (Connection conn = getConnection()) {
                String query = "UPDATE hostel_students SET student_name=?, age=?, department=?, admission_no=?, room_no=?, room_type=?, bed_count=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, nameField.getText());
                ps.setInt(2, age);
                ps.setString(3, deptField.getText());
                ps.setString(4, admNoField.getText());
                ps.setString(5, roomNoField.getText());
                ps.setString(6, roomTypeField.getText());
                ps.setInt(7, bedCount);
                ps.setInt(8, id);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student Updated!");
                loadStudentData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);

        try (Connection conn = getConnection()) {
            String query = "DELETE FROM hostel_students WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student Deleted!");
            loadStudentData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Hostel12().setVisible(true));
    }
}
