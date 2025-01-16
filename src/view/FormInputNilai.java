package view;

import model.Nilai;
import model.NilaiModel;

import javax.swing.*;
import java.awt.*;

public class FormInputNilai extends JFrame {
    private JPanel formPanel, buttonPanel;
    private JTextField txtMahasiswaId, txtNamaMahasiswa, txtMataKuliah, txtSemester, txtNilai;
    private JButton btnSave, btnCancel;
    private final NilaiModel nilaiModel;
    private Integer nilaiId;

    public FormInputNilai(NilaiModel nilaiModel) {
        this.nilaiModel = nilaiModel;

        setTitle("Form Input Nilai Mahasiswa");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Form dengan Titled Border
        formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE, 2), "Form Input"));
        formPanel.setBackground(new Color(230, 240, 255));

        // Label dan Input
        JLabel lblMahasiswaId = new JLabel("Mahasiswa ID:");
        lblMahasiswaId.setForeground(Color.BLUE);
        txtMahasiswaId = new JTextField();
        txtMahasiswaId.setEditable(false);

        JLabel lblNama = new JLabel("Nama Mahasiswa:");
        lblNama.setForeground(Color.BLUE);
        txtNamaMahasiswa = new JTextField();
        txtNamaMahasiswa.setEditable(false);

        JLabel lblMataKuliah = new JLabel("Mata Kuliah:");
        lblMataKuliah.setForeground(Color.BLUE);
        txtMataKuliah = new JTextField();

        JLabel lblSemester = new JLabel("Semester:");
        lblSemester.setForeground(Color.BLUE);
        txtSemester = new JTextField();

        JLabel lblNilai = new JLabel("Nilai:");
        lblNilai.setForeground(Color.BLUE);
        txtNilai = new JTextField();

        formPanel.add(lblMahasiswaId);
        formPanel.add(txtMahasiswaId);
        formPanel.add(lblNama);
        formPanel.add(txtNamaMahasiswa);
        formPanel.add(lblMataKuliah);
        formPanel.add(txtMataKuliah);
        formPanel.add(lblSemester);
        formPanel.add(txtSemester);
        formPanel.add(lblNilai);
        formPanel.add(txtNilai);

        // Panel Tombol dengan Desain Menarik
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSave = new JButton("Simpan Nilai");
        btnSave.setBackground(Color.GREEN);
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveNilai());

        btnCancel = new JButton("Batal");
        btnCancel.setBackground(Color.RED);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveNilai() {
        try {
            String mahasiswaIdText = txtMahasiswaId.getText();
            if (mahasiswaIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mahasiswa ID tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int mahasiswaId = Integer.parseInt(mahasiswaIdText);

            String mataKuliah = txtMataKuliah.getText();
            if (mataKuliah.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mata Kuliah tidak boleh kosong.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int semester = Integer.parseInt(txtSemester.getText());
            double nilai = Double.parseDouble(txtNilai.getText());

            if (nilaiId == null) {
                nilaiModel.createNilai(new Nilai(0, mahasiswaId, mataKuliah, semester, nilai));
            } else {
                nilaiModel.updateNilai(new Nilai(nilaiId, mahasiswaId, mataKuliah, semester, nilai));
            }
            JOptionPane.showMessageDialog(this, "Nilai berhasil disimpan.");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input tidak valid.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setIdNilai(Integer nilaiId) {
        this.nilaiId = nilaiId;
    }

    public void setMahasiswaId(String mahasiswaId) {
        txtMahasiswaId.setText(mahasiswaId);
    }

    public void setMahasiswaNama(String nama) {
        txtNamaMahasiswa.setText(nama);
    }

    public void setMataKuliah(String mataKuliah) {
        txtMataKuliah.setText(mataKuliah);
    }

    public void setSemester(String semester) {
        txtSemester.setText(semester);
    }

    public void setNilai(String nilai) {
        txtNilai.setText(nilai);
    }
}
