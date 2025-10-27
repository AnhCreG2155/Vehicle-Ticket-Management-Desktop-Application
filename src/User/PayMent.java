package User;

import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import DatabaseConnect.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PayMent extends javax.swing.JPanel {
    private int journeyID;
    private int userID;
    private double price;
    private java.sql.Date returnDate; // Thêm biến để lưu returnDate
    
    public PayMent(int journeyID, int userID, double price, java.sql.Date returnDate) {
        this.returnDate = returnDate; // Lưu returnDate
        this.journeyID = journeyID;
        this.userID = userID;
        this.price = price;
        initComponents();
        PricePayment.setText(String.valueOf(price));
        PricePayment.setEditable(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        PaymentOption = new javax.swing.JComboBox<>();
        Paymentbtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        PricePayment = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel1.setText("Phương thức thanh toán");

        PaymentOption.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        PaymentOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền mặt", "Chuyển khoản" }));

        Paymentbtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Paymentbtn.setText("Thanh toán");
        Paymentbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PaymentbtnActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Số tiền");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PaymentOption, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PricePayment)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(Paymentbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(314, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(PaymentOption, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PricePayment, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addComponent(Paymentbtn)
                .addContainerGap(167, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void PaymentbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PaymentbtnActionPerformed
        String paymentOption = (String) PaymentOption.getSelectedItem();
        String priceText = PricePayment.getText().trim();

        try {
            double enteredPrice = Double.parseDouble(priceText);
            if (enteredPrice != price) {
                JOptionPane.showMessageDialog(this, "Số tiền không khớp với giá vé!");
                return;
            }

            if (userID <= 0 || journeyID <= 0) {
                JOptionPane.showMessageDialog(this, "Thông tin người dùng hoặc chuyến đi không hợp lệ!");
                return;
            }

            try (Connection conn = DBConnection.DBConnection()) {
                // Kiểm tra số ghế còn lại
                String checkSeatsSql = "SELECT t.NumberOfSeat FROM journey j JOIN transport t ON j.TransportID = t.TransportID WHERE j.JourneyID = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSeatsSql);
                checkStmt.setInt(1, journeyID);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt("NumberOfSeat") <= 0) {
                    JOptionPane.showMessageDialog(this, "Chuyến đi này đã hết ghế!");
                    rs.close();
                    checkStmt.close();
                    return;
                }
                rs.close();
                checkStmt.close();
                
                // Lấy ReturnDate từ cơ sở dữ liệu để xác định TicketType
                String ticketType;
                String checkReturnDateSql = "SELECT ReturnDate FROM journey WHERE JourneyID = ?";
                PreparedStatement returnDateStmt = conn.prepareStatement(checkReturnDateSql);
                returnDateStmt.setInt(1, journeyID);
                ResultSet returnDateRs = returnDateStmt.executeQuery();
                if (returnDateRs.next() && returnDateRs.getDate("ReturnDate") != null) {
                    ticketType = "Khứ hồi";
                } else {
                    ticketType = "Một chiều";
                }
                returnDateRs.close();
                returnDateStmt.close();

                // Lưu vé vào bảng ticket
                String sql = "INSERT INTO ticket (UserID, JourneyID, TicketStatus, TicketType, Price, PaymentOption, BookingDate) " +
                             "VALUES (?, ?, 'Xác nhận', ?, ?, ?, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userID);
                stmt.setInt(2, journeyID);
                stmt.setString(3, ticketType);
                stmt.setDouble(4, price);
                stmt.setString(5, paymentOption.equals("Tiền mặt") ? "Tiền mặt" : "Chuyển Khoản");
                stmt.executeUpdate();
                stmt.close();

                // Giảm số ghế còn lại
                String updateSeatsSql = "UPDATE transport t JOIN journey j ON t.TransportID = j.TransportID SET t.NumberOfSeat = t.NumberOfSeat - 1 WHERE j.JourneyID = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSeatsSql);
                updateStmt.setInt(1, journeyID);
                updateStmt.executeUpdate();
                updateStmt.close();

                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");

                // Chuyển hướng sang TicketHistory
                JPanel parentPanel = (JPanel) this.getParent();
                parentPanel.removeAll();
                parentPanel.setLayout(new BorderLayout());
                TicketHistory ticketHistory = new TicketHistory(userID);
                parentPanel.add(ticketHistory, BorderLayout.CENTER);
                parentPanel.repaint();
                parentPanel.validate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
        }
    }//GEN-LAST:event_PaymentbtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> PaymentOption;
    private javax.swing.JButton Paymentbtn;
    private javax.swing.JTextField PricePayment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
