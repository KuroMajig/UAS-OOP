# UAS-OOP
NamaA  : kbar Rizky Ramadhan

NIM    : 31231696

Kelas  : TI.23.A6

Mata Kuliah: Pemograman Berorientasi Objek

Dosen: Agung Nugroho, S.Kom., M.Kom.

<h1>CRUD Mahasiswa</h1>

# Deskripsi Proyek
Proyek ini dibuat untuk memenuhi tugas akhir mata kuliah **Pemrograman Berorientasi Objek (OOP)** dengan menerapkan konsep dasar **CRUD (Create, Read, Update, Delete)**. CRUD digunakan untuk mengelola data mahasiswa melalui antarmuka berbasis Java yang terhubung dengan MySQL untuk penyimpanan data. Program ini memanfaatkan pola desain MVC untuk memisahkan antara tampilan pengguna, logika bisnis, dan pengelolaan data.

<h1>Hasil Output</h1>

# Form Mahasiswa
 ![Screenshot 2025-01-19 194852](https://github.com/user-attachments/assets/276a33d8-2bfa-4d49-a4de-2c8020a988df)

# Form Nilai
![Screenshot 2025-01-19 194948](https://github.com/user-attachments/assets/47ad58ab-cf90-48b0-aeb8-4314630404c4)

# Form Input Nilai
![Screenshot 2025-01-19 195003](https://github.com/user-attachments/assets/7edb5695-c4ce-4a77-a9f6-c7a9a41b291b)

# Resource

<h4>Condfig.properties</h4>

config untuk menyambungkan databases yang telah dibuat di MySql.

```
db.url=jdbc:mysql://localhost:3306/akademik
db.user=root
db.password=
```
Program terdiri dari beberapa class

# Classes
<h4>Bases Model</h4>

```package classes;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseModel<T> {
    protected final Database database;
    protected final String tableName;
    protected final List<String> fields;

    public BaseModel(String tableName, List<String> fields) {
        this.database = new Database();
        this.tableName = tableName;
        this.fields = fields;
    }

    public T find(int id) {
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        List<T> results = database.read(query, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<T> find() {
        String query = "SELECT * FROM " + tableName;
        return database.read(query, this::mapRow);
    }

    public boolean save(T object) {
        if (isNewRecord(object)) {
            return insert(object);
        } else {
            return update(object);
        }
    }

    protected boolean insert(T object) {
        List<String> fieldsWithoutId = fields.stream()
                .filter(field -> !field.equalsIgnoreCase("id"))
                .collect(Collectors.toList());

        String fieldNames = String.join(", ", fieldsWithoutId);
        String placeholders = String.join(", ", fieldsWithoutId.stream().map(f -> "?").toArray(String[]::new));
        String query = "INSERT INTO " + tableName + " (" + fieldNames + ") VALUES (" + placeholders + ")";
        return database.executeUpdate(query, getValues(object, false)) > 0;
    }

    protected boolean update(T object) {
        List<String> fieldsWithoutId = fields.stream()
                .filter(field -> !field.equalsIgnoreCase("id"))
                .collect(Collectors.toList());

        String setClause = String.join(", ", fieldsWithoutId.stream().map(f -> f + " = ?").toArray(String[]::new));
        String query = "UPDATE " + tableName + " SET " + setClause + " WHERE id = ?";
        return database.executeUpdate(query, getValues(object, true)) > 0;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM " + tableName + " WHERE id = ?";
        return database.executeUpdate(query, id) > 0;
    }

    protected abstract boolean isNewRecord(T object);

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract Object[] getValues(T object, boolean includeId);

    public void close() {
        database.close();
    }
}
```
<h4>Database</h4>

```package classes;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private Connection conn;

    public Database() {
        try {
            // Memuat driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver loaded successfully.");

            // Membaca file konfigurasi
            Properties properties = new Properties();
            properties.load(new FileInputStream("resources/config.properties"));

            // Ambil koneksi
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password", ""); // Gunakan string kosong jika password tidak ada

            System.out.println("Connecting to database: " + url);

            // Membuka koneksi
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Driver MySQL tidak ditemukan.", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
            throw new RuntimeException("Gagal terhubung ke database.", e);
        } catch (IOException e) {
            System.err.println("Configuration file not found or invalid.");
            e.printStackTrace();
            throw new RuntimeException("File konfigurasi tidak ditemukan atau tidak valid.", e);
        }
    }

    public Connection getConn() {
        return conn;
    }

    /**
     * Execute Query SELECT
     */
    public ResultSet query(String query, Object... params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            setParameters(stmt, params);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Execute an update query (INSERT, UPDATE, DELETE)
     */
    public int executeUpdate(String query, Object... params) {
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            setParameters(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * General method to read data into a list
     *
     * @param <T>       The type of object to be returned
     * @param sql       The SELECT query
     * @param rowMapper A functional interface to map a ResultSet row into an object
     * @param params    The query parameters
     * @return A list of mapped objects
     */
    public <T> List<T> read(String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> result = new ArrayList<>();
        try (ResultSet rs = query(sql, params)) {
            while (rs != null && rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setParameters(PreparedStatement stmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

<h4>Rowmapper</h4>

```package classes; 
 
import java.sql.ResultSet; 
import java.sql.SQLException; 
 
public interface RowMapper<T> { 
    T mapRow(ResultSet rs) throws SQLException; 
}
```

# Controller

<h4>Mahasiswa Controller</h4>

```package controller;

import model.Mahasiswa;
import model.MahasiswaModel;
import model.NilaiModel;
import view.FormInputNilai;
import view.FormMahasiswa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MahasiswaController {
    private MahasiswaModel mahasiswaModel;
    private NilaiModel nilaiModel;  // Tambahkan objek NilaiModel
    private FormMahasiswa view;

    public MahasiswaController(FormMahasiswa view, NilaiModel nilaiModel) {  // Tambahkan NilaiModel ke konstruktor
        this.view = view;
        this.nilaiModel = nilaiModel;  // Inisialisasi NilaiModel
        view.btnSave.addActionListener(e -> saveData());
        view.btnUpdate.addActionListener(e -> updateData());
        view.btnDelete.addActionListener(e -> deleteData());
        view.btnViewNilai.addActionListener(e -> openFormInputNilai());  // Panggil untuk membuka form input nilai
        mahasiswaModel = new MahasiswaModel();
        loadData();
    }

    private void deleteData() {
        int selectedRow = view.tblMahasiswa.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(view.tblMahasiswa.getModel().getValueAt(selectedRow, 0).toString());
            if (mahasiswaModel.delete(id)) {
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menghapus data!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus!");
        }
    }

    private void updateData() {
        int selectedRow = view.tblMahasiswa.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(view.tblMahasiswa.getModel().getValueAt(selectedRow, 0).toString());
            Mahasiswa mhs = mahasiswaModel.find(id);
            view.txtNim.setText(mhs.getNim());
            view.txtNama.setText(mhs.getNama());
            view.txtJurusan.setText(mhs.getJurusan());
            view.txtAngkatan.setText(String.valueOf(mhs.getAngkatan()));
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan diubah!");
        }
    }

    private void saveData() {
        String nim = view.txtNim.getText();
        String nama = view.txtNama.getText();
        String jurusan = view.txtJurusan.getText();
        int angkatan = Integer.parseInt(view.txtAngkatan.getText());

        Mahasiswa mhs = new Mahasiswa();
        mhs.setNim(nim);
        mhs.setNama(nama);
        mhs.setJurusan(jurusan);
        mhs.setAngkatan(angkatan);

        int selectedRow = view.tblMahasiswa.getSelectedRow();
        if (selectedRow != -1) {
            mhs.setId(Integer.parseInt(view.tblMahasiswa.getModel().getValueAt(selectedRow, 0).toString()));
        }

        if (mahasiswaModel.save(mhs)) {
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(view, "Data berhasil disimpan");
        } else {
            JOptionPane.showMessageDialog(view, "Gagal menyimpan data");
        }
    }

    private void clearForm() {
        view.txtNim.setText("");
        view.txtNama.setText("");
        view.txtJurusan.setText("");
        view.txtAngkatan.setText("");
        view.tblMahasiswa.clearSelection();
    }

    private void loadData() {
        DefaultTableModel tableModel = (DefaultTableModel) view.tblMahasiswa.getModel();
        tableModel.setRowCount(0);
        view.tblMahasiswa.setModel(tableModel);
        List<Mahasiswa> mahasiswaList = mahasiswaModel.find();
        for (Mahasiswa mahasiswa : mahasiswaList) {
            tableModel.addRow(new Object[]{
                    mahasiswa.getId(),
                    mahasiswa.getNim(),
                    mahasiswa.getNama(),
                    mahasiswa.getJurusan(),
                    mahasiswa.getAngkatan()
            });
        }
    }

    private void openFormInputNilai() {
        // Pastikan form input nilai hanya terbuka saat tombol Grade ditekan
        FormInputNilai formInputNilai = new FormInputNilai(nilaiModel);
        formInputNilai.setVisible(true);
    }
}
```

<h4>Nilai Controller</h4>

```package controller;

import model.Nilai;
import model.NilaiModel;
import view.FormInputNilai;
import view.FormMahasiswa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NilaiController {
    private final NilaiModel nilaiModel;
    private final FormMahasiswa view;

    public NilaiController(FormMahasiswa view) {
        this.view = view;
        this.nilaiModel = new NilaiModel();

        // Tombol Grade untuk menampilkan form nilai
        view.btnViewNilai.addActionListener(e -> showNilai());
    }

    private void showNilai() {
        int selectedRow = view.tblMahasiswa.getSelectedRow();
        if (selectedRow != -1) {
            int mahasiswaId = Integer.parseInt(view.tblMahasiswa.getModel().getValueAt(selectedRow, 0).toString());
            String namaMahasiswa = view.tblMahasiswa.getModel().getValueAt(selectedRow, 2).toString();

            JFrame nilaiFrame = new JFrame("Nilai Mahasiswa: " + namaMahasiswa);
            nilaiFrame.setSize(600, 400);
            nilaiFrame.setLocationRelativeTo(view);

            DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Mata Kuliah", "Semester", "Nilai"}, 0);
            JTable nilaiTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(nilaiTable);

            JPanel buttonPanel = new JPanel();
            JButton btnTambah = new JButton("Create");
            JButton btnEdit = new JButton("Update");
            JButton btnHapus = new JButton("Delete");

            // Memuat data ke tabel
            List<Nilai> nilaiList = nilaiModel.findByMahasiswaId(mahasiswaId);
            for (Nilai nilai : nilaiList) {
                tableModel.addRow(new Object[]{nilai.getId(), nilai.getMataKuliah(), nilai.getSemester(), nilai.getNilai()});
            }

            // Logika tombol Create
            btnTambah.addActionListener(e -> {
                FormInputNilai formInputNilai = new FormInputNilai(nilaiModel);
                formInputNilai.setMahasiswaId(String.valueOf(mahasiswaId));
                formInputNilai.setMahasiswaNama(namaMahasiswa);
                formInputNilai.setVisible(true);

                formInputNilai.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        reloadTableData(tableModel, mahasiswaId);
                    }
                });
            });

            // Logika tombol Update
            btnEdit.addActionListener(e -> {
                int selectedRowInTable = nilaiTable.getSelectedRow();
                if (selectedRowInTable != -1) {
                    int nilaiId = Integer.parseInt(nilaiTable.getValueAt(selectedRowInTable, 0).toString());
                    String mataKuliah = nilaiTable.getValueAt(selectedRowInTable, 1).toString();
                    int semester = Integer.parseInt(nilaiTable.getValueAt(selectedRowInTable, 2).toString());
                    double nilai = Double.parseDouble(nilaiTable.getValueAt(selectedRowInTable, 3).toString());

                    FormInputNilai formInputNilai = new FormInputNilai(nilaiModel);
                    formInputNilai.setMahasiswaId(String.valueOf(mahasiswaId));
                    formInputNilai.setMahasiswaNama(namaMahasiswa);
                    formInputNilai.setIdNilai(nilaiId); // Set ID untuk update
                    formInputNilai.setMataKuliah(mataKuliah);
                    formInputNilai.setSemester(String.valueOf(semester));
                    formInputNilai.setNilai(String.valueOf(nilai));
                    formInputNilai.setVisible(true);

                    formInputNilai.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            reloadTableData(tableModel, mahasiswaId);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(nilaiFrame, "Pilih nilai yang akan diedit!");
                }
            });

            // Logika tombol Delete
            btnHapus.addActionListener(e -> {
                int selectedRowInTable = nilaiTable.getSelectedRow();
                if (selectedRowInTable != -1) {
                    int nilaiId = Integer.parseInt(nilaiTable.getValueAt(selectedRowInTable, 0).toString());
                    int confirm = JOptionPane.showConfirmDialog(nilaiFrame, "Yakin ingin menghapus nilai ini?");
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (nilaiModel.deleteNilai(nilaiId)) {
                            tableModel.removeRow(selectedRowInTable);
                            JOptionPane.showMessageDialog(nilaiFrame, "Nilai berhasil dihapus!");
                        } else {
                            JOptionPane.showMessageDialog(nilaiFrame, "Gagal menghapus nilai!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(nilaiFrame, "Pilih nilai yang akan dihapus!");
                }
            });

            nilaiFrame.setLayout(new BorderLayout());
            nilaiFrame.add(scrollPane, BorderLayout.CENTER);
            buttonPanel.add(btnTambah);
            buttonPanel.add(btnEdit);
            buttonPanel.add(btnHapus);
            nilaiFrame.add(buttonPanel, BorderLayout.SOUTH);
            nilaiFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(view, "Pilih mahasiswa terlebih dahulu!");
        }
    }

    private void reloadTableData(DefaultTableModel tableModel, int mahasiswaId) {
        tableModel.setRowCount(0);
        List<Nilai> nilaiList = nilaiModel.findByMahasiswaId(mahasiswaId);
        for (Nilai nilai : nilaiList) {
            tableModel.addRow(new Object[]{nilai.getId(), nilai.getMataKuliah(), nilai.getSemester(), nilai.getNilai()});
        }
    }
}
```

