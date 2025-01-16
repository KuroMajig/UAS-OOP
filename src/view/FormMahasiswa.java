package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FormMahasiswa extends JFrame {
    private JPanel formPanel, buttonPanel;
    public JTextField txtNim, txtNama, txtJurusan, txtAngkatan;
    public JButton btnSave, btnUpdate, btnDelete, btnViewNilai;
    public JTable tblMahasiswa;

    public FormMahasiswa() {
        setTitle("Form Data Mahasiswa");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Menjadikan full window saat dijalankan
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Form
        formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE, 2), "Form Input"));
        formPanel.setBackground(new Color(230, 240, 255));

        JLabel lblNim = new JLabel("NIM:");
        lblNim.setForeground(Color.BLUE);
        txtNim = new JTextField();

        JLabel lblNama = new JLabel("Nama:");
        lblNama.setForeground(Color.BLUE);
        txtNama = new JTextField();

        JLabel lblJurusan = new JLabel("Jurusan:");
        lblJurusan.setForeground(Color.BLUE);
        txtJurusan = new JTextField();

        JLabel lblAngkatan = new JLabel("Angkatan:");
        lblAngkatan.setForeground(Color.BLUE);
        txtAngkatan = new JTextField();

        formPanel.add(lblNim);
        formPanel.add(txtNim);
        formPanel.add(lblNama);
        formPanel.add(txtNama);
        formPanel.add(lblJurusan);
        formPanel.add(txtJurusan);
        formPanel.add(lblAngkatan);
        formPanel.add(txtAngkatan);

        // Panel Tombol
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSave = new JButton("Simpan");
        btnSave.setBackground(Color.GREEN);
        btnSave.setForeground(Color.WHITE);

        btnUpdate = new JButton("Perbarui");
        btnUpdate.setBackground(Color.ORANGE);
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Hapus");
        btnDelete.setBackground(Color.RED);
        btnDelete.setForeground(Color.WHITE);

        btnViewNilai = new JButton("Nilai");
        btnViewNilai.setBackground(Color.CYAN);
        btnViewNilai.setForeground(Color.BLACK);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnViewNilai);

        // Tabel Mahasiswa
        tblMahasiswa = new JTable();
        tblMahasiswa.setGridColor(Color.GRAY);
        tblMahasiswa.setSelectionBackground(new Color(200, 230, 201));
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "NIM", "Nama", "Jurusan", "Angkatan"}, 0);
        tblMahasiswa.setModel(model);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(tblMahasiswa), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
