package User;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import DatabaseConnect.DBConnection;
import java.awt.Panel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ListTrip extends javax.swing.JPanel {
    private List<Object[]> lisTripData = new ArrayList<>();
    private int userID;
    private java.sql.Date returnDate; // Thêm biến để lưu returnDate

    public ListTrip(String startingPoint, String arrivalPoint, java.sql.Date departureDate, java.sql.Date returnDate, int userID) {
        initComponents();
        loadJourneyFromDatabase(startingPoint, arrivalPoint, departureDate, returnDate); // Tải dữ liệu với bộ lọc
        this.userID = userID;
        this.returnDate = returnDate; // Lưu returnDate
    }
    
    private void loadJourneyFromDatabase(String startingPoint, String arrivalPoint, java.sql.Date departureDate, java.sql.Date returnDate) {
        DefaultTableModel model = (DefaultTableModel) lisTripTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại
        lisTripData.clear(); // Xóa dữ liệu cũ
    
        String sql = "SELECT j.JourneyID, t.TransportType, j.DepartureDate, j.ReturnDate, t.NumberOfSeat, t.NumberPlate, j.JourneyPrice " +
                       "FROM do_an_co_so.journey j " +
                       "JOIN do_an_co_so.transport t ON j.TransportID = t.TransportID " +
                       "WHERE j.StartingPoint = ? AND j.ArrivalPoint = ? AND j.DepartureDate >= ?";
        if (returnDate != null) {
            sql += " AND j.ReturnDate <= ?";
        }
    
        try (Connection conn = DBConnection.DBConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Gán các tham số vào PreparedStatement
            stmt.setString(1, startingPoint);
            stmt.setString(2, arrivalPoint);
            stmt.setDate(3, departureDate);
            int paramIndex = 4;
            if (returnDate != null) {
                stmt.setDate(paramIndex, returnDate);
            }
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Date departureDateValue = rs.getDate("DepartureDate");
            String departureDateStr = (departureDateValue != null) ? departureDateValue.toString() : "Không có";
            
            Date returnDateValue = rs.getDate("ReturnDate");
            String returnDateStr = (returnDateValue != null) ? returnDateValue.toString() : "Không có";
            
            Object[] rowData = new Object[] {
                rs.getString("TransportType"),  // Loại phương tiện
                departureDateStr,              // Ngày đi
                returnDateStr,                 // Ngày về
                rs.getInt("NumberOfSeat"),     // Số chỗ ngồi
                rs.getString("NumberPlate"),   // Biển số xe
                rs.getDouble("JourneyPrice")   // Giá vé
            };
            model.addRow(rowData); // Thêm vào cuối bảng
            lisTripData.add(new Object[] { rs.getInt("JourneyID"), rowData }); // Thêm vào danh sách
        }
        
        if (model.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến đi phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lisTripTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        lisTripTable.setModel(new javax.swing.table.DefaultTableModel(
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
                "Loại phương tiện", "Ngày đi", "Ngày về", "Số chỗ ngồi", "Biển số", "Giá vé"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(lisTripTable);

        jButton1.setText("Đặt vé");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 102, 0));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel1.setText("<-- Quay lại");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 11, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        JPanel parentPanel = (JPanel) this.getParent();
        parentPanel.removeAll();
        parentPanel.setLayout(new BorderLayout());
        searchFormPanel searchform = new searchFormPanel(userID);
        parentPanel.add(searchform, BorderLayout.CENTER);
        parentPanel.repaint();
        parentPanel.validate();
    }//GEN-LAST:event_jPanel1MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int selectedRow = lisTripTable.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn vé để đặt");
        } else {
            Object[] selectedData = lisTripData.get(selectedRow);
            int selectedJourneyID = (Integer) selectedData[0];
            double selectedPrice = (Double) ((Object[]) selectedData[1])[5];
            int numberOfSeats = (Integer) ((Object[]) selectedData[1])[3];
        
            if (userID <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để đặt vé!");
                return;
            }

            if (numberOfSeats <= 0) {
                JOptionPane.showMessageDialog(this, "Chuyến đi này đã hết ghế!");
                return;
            }
            
            JPanel panelParent = (JPanel) this.getParent();
            panelParent.removeAll();
            panelParent.setLayout(new BorderLayout());
            PayMent payment = new PayMent(selectedJourneyID, userID, selectedPrice, returnDate); // Truyền returnDate
            panelParent.add(payment, BorderLayout.CENTER);
            panelParent.repaint();
            panelParent.validate();
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable lisTripTable;
    // End of variables declaration//GEN-END:variables
}
