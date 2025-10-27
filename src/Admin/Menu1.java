package Admin;

import DatabaseConnect.DBConnection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class Menu1 extends javax.swing.JInternalFrame {
    private List<Object[]> userData = new ArrayList<>(); // Lưu trữ dữ liệu để tìm kiếm
    
    public Menu1() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI ui=(BasicInternalFrameUI)this.getUI();
        ui.setNorthPane(null);
        loadUsersFromDatabase(); // Tải dữ liệu từ MySQL khi khởi tạo
        // Thêm TableModelListener để xử lý chỉnh sửa trực tiếp
        UserTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                handleTableCellEdit(e);
            }
        });
    }
    
    // Xử lý khi ô trong bảng được chỉnh sửa
    private void handleTableCellEdit(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            DefaultTableModel model = (DefaultTableModel) UserTable.getModel();

            // Chỉ xử lý các cột có thể chỉnh sửa (1: UserName, 2: UserPassword, 3: Email, 4: Gender, 5: Role)
            if (column < 1 || column > 5) {
                return;
            }

            int userID = (Integer) model.getValueAt(row, 0);
            String newValue = (String) model.getValueAt(row, column);
            String columnName;
            switch (column) {
                case 1: columnName = "UserName"; break;
                case 2: columnName = "UserPassword"; break;
                case 3: columnName = "Email"; break;
                case 4: columnName = "Gender"; break;
                case 5: columnName = "Role"; break;
                default: return;
            }

            // Kiểm tra dữ liệu hợp lệ
            if (column == 1 && (newValue == null || newValue.trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Tên người dùng không được để trống!");
                revertTableChange(row, column);
                return;
            }
            if (column == 2 && (newValue == null || newValue.trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!");
                revertTableChange(row, column);
                return;
            }
            if (column == 3 && (newValue == null || !newValue.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
                JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
                revertTableChange(row, column);
                return;
            }
            if (column == 4 && !newValue.matches("Nam|Nữ|Male|Female")) {
                JOptionPane.showMessageDialog(this, "Giới tính phải là 'Nam' hoặc 'Nữ' hoặc 'Male' hoặc 'Female'!");
                revertTableChange(row, column);
                return;
            }
            if (column == 5 && !newValue.matches("Admin|Khách hàng|User")) {
                JOptionPane.showMessageDialog(this, "Vai trò phải là 'Admin' hoặc 'Khách hàng' hoặc 'User'!");
                revertTableChange(row, column);
                return;
            }

            // Cập nhật cơ sở dữ liệu
            try (Connection conn = DBConnection.DBConnection()) {
                String sql = "UPDATE myuser SET " + columnName + " = ? WHERE UserID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newValue);
                stmt.setInt(2, userID);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Cập nhật userData
                    for (Object[] data : userData) {
                        if ((Integer) data[0] == userID) {
                            data[column] = newValue;
                            break;
                        }
                    }
                } else {
                    revertTableChange(row, column);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
                revertTableChange(row, column);
                ex.printStackTrace();
            }
        }
    }

    // Khôi phục giá trị cũ trong bảng nếu cập nhật thất bại
    private void revertTableChange(int row, int column) {
        DefaultTableModel model = (DefaultTableModel) UserTable.getModel();
        int userID = (Integer) model.getValueAt(row, 0);
        for (Object[] data : userData) {
            if ((Integer) data[0] == userID) {
                model.setValueAt(data[column], row, column);
                break;
            }
        }
    }
    
    // Tải dữ liệu từ bảng myuser
    private void loadUsersFromDatabase() {
        DefaultTableModel model = (DefaultTableModel) UserTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại
        userData.clear(); // Xóa dữ liệu cũ

        try (Connection conn = DBConnection.DBConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM myuser");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Object[] rowData = new Object[] {
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("UserPassword"),
                    rs.getString("Email"),
                    rs.getString("Gender"),
                    rs.getString("Role")
                };
                model.insertRow(0, rowData); // Thêm vào đầu bảng
                userData.add(0, rowData); // Thêm vào đầu userData
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Upass = new javax.swing.JTextField();
        Uname = new javax.swing.JTextField();
        Uemail = new javax.swing.JTextField();
        Urole = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        AddUserbtn = new javax.swing.JButton();
        Ugender = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        FindUserbtn = new javax.swing.JButton();
        FindUser = new javax.swing.JTextField();
        DeleteUserbtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        UserTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(650, 550));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Tên người dùng");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Mật khẩu");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Giới tính");

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Email");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Vai trò");

        Urole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Khách hàng" }));

        jButton1.setText("Làm trống");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        AddUserbtn.setText("Thêm");
        AddUserbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddUserbtnActionPerformed(evt);
            }
        });

        Ugender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Upass)
                    .addComponent(Uname)
                    .addComponent(Uemail)
                    .addComponent(Urole, 0, 285, Short.MAX_VALUE)
                    .addComponent(Ugender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AddUserbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Uname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(Upass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(Ugender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Uemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Urole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AddUserbtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        FindUserbtn.setText("Tìm kiếm");
        FindUserbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindUserbtnActionPerformed(evt);
            }
        });

        DeleteUserbtn.setText("Xóa");
        DeleteUserbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteUserbtnActionPerformed(evt);
            }
        });

        UserTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã người dùng", "Họ và tên", "Mật khẩu", "Email", "Giới tính", "Vai trò"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(UserTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(FindUserbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(FindUser, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DeleteUserbtn)
                .addContainerGap(70, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(FindUser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FindUserbtn)
                        .addComponent(DeleteUserbtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FindUserbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindUserbtnActionPerformed
        String searchText = FindUser.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) UserTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại

        if (searchText.isEmpty()) {
            // Hiển thị toàn bộ dữ liệu
            for (Object[] row : userData) {
                model.addRow(row);
            }
        } else {
            // Lọc dữ liệu theo tên hoặc email
            for (Object[] row : userData) {
                String username = ((String) row[1]).toLowerCase();
                String email = ((String) row[3]).toLowerCase();
                if (username.contains(searchText) || email.contains(searchText)) {
                    model.addRow(row);
                }
            }
        }
    }//GEN-LAST:event_FindUserbtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Uname.setText("");
        Upass.setText("");
        Ugender.setSelectedIndex(0);
        Uemail.setText("");
        Urole.setSelectedIndex(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void AddUserbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddUserbtnActionPerformed
        String username = Uname.getText().trim();
        String password = Upass.getText().trim();
        String email = Uemail.getText().trim();
        String role = (String) Urole.getSelectedItem();
        String gender = (String) Ugender.getSelectedItem();
        
        // Kiểm tra dữ liệu đầu vào
        if(username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin");
            return;
        }
        
        // Kiểm tra định dạng email cơ bản
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
            return;
        }
        
        try (Connection conn = DBConnection.DBConnection()) {
            // Thêm vào bảng myuser
            String sql = "INSERT INTO myuser (UserName, Userpassword, Email, Gender, Role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, gender);
            stmt.setString(5, role);
            stmt.executeUpdate();

            // Lấy UserID được tạo
            ResultSet rs = stmt.getGeneratedKeys();
            int userId = rs.next() ? rs.getInt(1) : -1;

            // Thêm vào UserTabel và userData
            DefaultTableModel model = (DefaultTableModel) UserTable.getModel();
            Object[] rowData = new Object[] {userId, username, password, email, gender, role};
            model.insertRow(0, rowData); // Thêm vào đầu bảng
            userData.add(0, rowData); // Thêm vào đầu userData

            // Cuộn đến hàng mới
            UserTable.scrollRectToVisible(UserTable.getCellRect(0, 0, true));

            // Reset form
            jButton1ActionPerformed(evt);
            javax.swing.JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!");
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi thêm người dùng: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_AddUserbtnActionPerformed

    private void DeleteUserbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteUserbtnActionPerformed
        int selectedRow = UserTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) UserTable.getModel();
            int userIdToDelete = (Integer) model.getValueAt(selectedRow, 0);

            try (Connection conn = DBConnection.DBConnection()) {
                // Xóa khỏi bảng myuser
                String sql = "DELETE FROM myuser WHERE UserID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userIdToDelete);
                stmt.executeUpdate();

                // Xóa khỏi userData và jTable1
                userData.removeIf(row -> (Integer) row[0] == userIdToDelete);
                model.removeRow(selectedRow);
                javax.swing.JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!");
            } catch (SQLException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi xóa người dùng: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa!");
        }
    }//GEN-LAST:event_DeleteUserbtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddUserbtn;
    private javax.swing.JButton DeleteUserbtn;
    private javax.swing.JTextField FindUser;
    private javax.swing.JButton FindUserbtn;
    private javax.swing.JTextField Uemail;
    private javax.swing.JComboBox<String> Ugender;
    private javax.swing.JTextField Uname;
    private javax.swing.JTextField Upass;
    private javax.swing.JComboBox<String> Urole;
    private javax.swing.JTable UserTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
