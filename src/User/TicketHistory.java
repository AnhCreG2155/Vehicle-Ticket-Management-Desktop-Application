package User;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import javax.swing.JOptionPane;
import DatabaseConnect.DBConnection;
import com.lowagie.text.Element;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketHistory extends javax.swing.JPanel {
    private List<Object[]> ticketData = new ArrayList<>(); // Lưu trữ dữ liệu để tìm kiếm
    private int userID;

    public TicketHistory(int userID) {
        initComponents();
        this.userID = userID;
        loadTicketHistory();
    }
    
    public void loadTicketHistory() {
        if(userID <= 0) {
            JOptionPane.showMessageDialog(this, "UserID không hợp lệ. Vui lòng đăng nhập lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ticketData.clear(); // Xóa dữ liệu cũ trước khi tải mới
        try {
            Connection conn = DBConnection.DBConnection();
            String sql = "SELECT t.TicketID, u.UserName, j.Route, t.TicketType, t.Price, t.TicketStatus, t.PaymentOption, t.BookingDate " +
                        "FROM ticket t " +
                        "JOIN myuser u ON t.UserID = u.UserID " +
                        "JOIN journey j ON t.JourneyID = j.JourneyID " +
                        "WHERE t.UserID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            
            DefaultTableModel model = (DefaultTableModel) TicketTable.getModel();
            model.setRowCount(0); // Xóa bảng hiện tại
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("TicketID"),
                    rs.getString("UserName"),
                    rs.getString("Route") != null ? rs.getString("Route") : (rs.getString("StartingPoint") + " - " + rs.getString("ArrivalPoint")),
                    rs.getString("TicketType"),
                    rs.getString("Price"),
                    rs.getString("TicketStatus"),
                    rs.getString("PaymentOption"),
                    rs.getString("BookingDate")
                };
                ticketData.add(row);
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử vé: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }    
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        FindTicketbtn = new javax.swing.JButton();
        FindTicket = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        TicketTable = new javax.swing.JTable();
        ticketIssuance = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 102, 0));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel3MousePressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("<-- Quay lại");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        FindTicketbtn.setText("Tìm kiếm");
        FindTicketbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindTicketbtnActionPerformed(evt);
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

        ticketIssuance.setBackground(new java.awt.Color(255, 102, 0));
        ticketIssuance.setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N
        ticketIssuance.setForeground(new java.awt.Color(255, 255, 255));
        ticketIssuance.setText("Xuất Vé");
        ticketIssuance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ticketIssuanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(FindTicketbtn)
                        .addGap(18, 18, 18)
                        .addComponent(FindTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(ticketIssuance, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FindTicketbtn)
                    .addComponent(FindTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ticketIssuance, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MousePressed
        JPanel parentPanel = (JPanel) this.getParent();
        parentPanel.removeAll();
        parentPanel.setLayout(new BorderLayout());
        searchFormPanel searchform = new searchFormPanel(userID);
        parentPanel.add(searchform, BorderLayout.CENTER);
        parentPanel.repaint();
        parentPanel.validate();
    }//GEN-LAST:event_jPanel3MousePressed

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

    private void ticketIssuanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ticketIssuanceActionPerformed
        int selectedRow = TicketTable.getSelectedRow();
        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé để xuất!");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) TicketTable.getModel();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Ticket.pdf"));
            document.open();
            
            // Canh giữa tiêu đề
            Paragraph title = new Paragraph("=== Chi tiết vé ===");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Ngày in vé
            Paragraph date = new Paragraph("Ngày: " + new java.util.Date().toString());
            date.setAlignment(Element.ALIGN_CENTER); // Canh giữa luôn nếu bạn muốn
            document.add(date);

            // Dòng trắng
            document.add(new Paragraph("\n"));

            // 📄 Nội dung vé
            document.add(new Paragraph("Mã vé: " + model.getValueAt(selectedRow, 0)));
            document.add(new Paragraph("Khách hàng: " + model.getValueAt(selectedRow, 1)));
            document.add(new Paragraph("Tuyến đường: " + model.getValueAt(selectedRow, 2)));
            document.add(new Paragraph("Loại vé: " + model.getValueAt(selectedRow, 3)));
            document.add(new Paragraph("Gía: " + model.getValueAt(selectedRow, 4)));
            document.add(new Paragraph("Trạng thái: " + model.getValueAt(selectedRow, 5)));
            document.add(new Paragraph("Phương thức thanh toán: " + model.getValueAt(selectedRow, 6)));
            document.add(new Paragraph("Ngày đặt: " + model.getValueAt(selectedRow, 7)));

            // Đường phân cách
            Paragraph line = new Paragraph("---------------------");
            line.setAlignment(Element.ALIGN_CENTER); // Canh giữa dòng gạch
            document.add(line);

            document.close();
            javax.swing.JOptionPane.showMessageDialog(this, "Xuất file thành công!");
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage());
        }
    }//GEN-LAST:event_ticketIssuanceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField FindTicket;
    private javax.swing.JButton FindTicketbtn;
    private javax.swing.JTable TicketTable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton ticketIssuance;
    // End of variables declaration//GEN-END:variables
}
