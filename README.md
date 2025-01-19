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
