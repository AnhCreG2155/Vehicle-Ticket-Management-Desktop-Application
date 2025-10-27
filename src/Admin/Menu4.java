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

public class Menu4 extends javax.swing.JInternalFrame {
    private List<Object[]> ticketData = new ArrayList<>(); // Lưu trữ dữ liệu để tìm kiếm
    
    public Menu4() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI ui=(BasicInternalFrameUI)this.getUI();
        ui.setNorthPane(null);
        loadTicketsFromDatabase(); // Tải dữ liệu từ MySQL khi khởi tạo
    }
    
    // Tải dữ liệu từ bảng myuser
    private void loadTicketsFromDatabase() {
        DefaultTableModel model = (DefaultTableModel) TicketTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại
        ticketData.clear(); // Xóa dữ liệu cũ

        try (Connection conn = DBConnection.DBConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT t.TicketID, m.UserName, j.Route, t.TicketType, t.Price, t.TicketStatus, t.PaymentOption, t.BookingDate " +
                 "FROM ticket t " +
                 "JOIN myuser m ON t.UserID = m.UserID " +
                 "JOIN journey j ON t.JourneyID = j.JourneyID"
             );
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Object[] rowData = new Object[] {
                    rs.getInt("TicketID"),
                    rs.getString("UserName"), // Thay UserID bằng UserName
                    rs.getString("Route"),    // Thay RouteID bằng Route
                    rs.getString("TicketType"),
                    rs.getString("Price"),
                    rs.getString("TicketStatus"),
                    rs.getString("PaymentOption"),
                    rs.getString("BookingDate")
                };
                model.addRow(rowData); // Thêm vào cuối bảng
                ticketData.add(rowData); // Thêm vào ticketData
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        FindTicketbtn = new javax.swing.JButton();
        FindTicket = new javax.swing.JTextField();
        DeleteTicketbtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TicketTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(650, 550));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 592, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 220, Short.MAX_VALUE)
        );

        FindTicketbtn.setText("Tìm kiếm");
        FindTicketbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindTicketbtnActionPerformed(evt);
            }
        });

        DeleteTicketbtn.setText("Xóa");
        DeleteTicketbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteTicketbtnActionPerformed(evt);
            }
        });

        TicketTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã vé ", "Khách hàng", "Tuyến đường", "Loại vé", "Gía", "Trạng thái", "Phương thức thanh toán", "Ngày đặt vé"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TicketTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 19, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(FindTicketbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(FindTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(DeleteTicketbtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FindTicketbtn)
                    .addComponent(FindTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteTicketbtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void FindTicketbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindTicketbtnActionPerformed
        String searchText = FindTicket.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) TicketTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại

        if (searchText.isEmpty()) {
            // Hiển thị toàn bộ dữ liệu
            for (Object[] row : ticketData) {
                model.addRow(row);
            }
        } else {
            // Lọc dữ liệu theo tên hoặc ngày đặt vé
            for (Object[] row : ticketData) {
                String username = ((String) row[1]).toLowerCase();
                String bookingdate = ((String) row[7]).toLowerCase();
                if (username.contains(searchText) || bookingdate.contains(searchText)) {
                    model.addRow(row);
                }
            }
        }
    }//GEN-LAST:event_FindTicketbtnActionPerformed

    private void DeleteTicketbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteTicketbtnActionPerformed
        int selectedRow = TicketTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) TicketTable.getModel();
            int ticketIdToDelete = (Integer) model.getValueAt(selectedRow, 0);

            try (Connection conn = DBConnection.DBConnection()) {
                // Xóa khỏi bảng ticket
                String sql = "DELETE FROM ticket WHERE TicketID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, ticketIdToDelete);
                int rowsAffected = stmt.executeUpdate();

                // Xóa khỏi ticketData và TicketTable
                if(rowsAffected > 0) {
                    ticketData.removeIf(row -> (Integer) row[0] == ticketIdToDelete);
                    model.removeRow(selectedRow);
                    javax.swing.JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy vé để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE); 
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn để xóa!");
        }
    }//GEN-LAST:event_DeleteTicketbtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DeleteTicketbtn;
    private javax.swing.JTextField FindTicket;
    private javax.swing.JButton FindTicketbtn;
    private javax.swing.JTable TicketTable;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
