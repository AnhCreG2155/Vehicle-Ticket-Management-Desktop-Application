package LogAndRegis;

import DatabaseConnect.DBConnection;
import Admin.Dashboard;
import User.Home;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

public class Login extends javax.swing.JPanel {

    public Login() {
        initComponents();
        loadRememberedCredentials(); // Tải thông tin đã lưu khi khởi tạo form
    }
    
    public void addEventRegister(ActionListener event) {
        RegisterButton1.addActionListener(event);
    }
    
    // Tải thông tin từ file remember_me.properties
    private void loadRememberedCredentials() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("remember_me.properties")) {
            props.load(fis);
            String email = props.getProperty("email");
            String password = props.getProperty("password");
            if (email != null && password != null) {
                LogEmailText.setText(email);
                LogPasswordText.setText(password);
                jCheckBox1.setSelected(true);
            }
        } catch (IOException e) {
            // File không tồn tại hoặc lỗi, không làm gì
        }
    }
    
    // Lưu thông tin vào file remember_me.properties
    private void saveCredentials(String email, String password) {
        Properties props = new Properties();
        props.setProperty("email", email);
        props.setProperty("password", password);
        try (FileOutputStream fos = new FileOutputStream("remember_me.properties")) {
            props.store(fos, "Remember Me Credentials");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin Remember me: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Xóa file remember_me.properties
    private void clearCredentials() {
        File file = new File("remember_me.properties");
        if (file.exists()) {
            if (!file.delete()) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa thông tin Remember me.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        LogEmailText = new Swing.TextField();
        jLabel3 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        LogInButton1 = new javax.swing.JButton();
        RegisterButton1 = new javax.swing.JButton();
        LogPasswordText = new Swing.PasswordField();

        setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Đăng Nhập");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Email");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Mật khẩu");

        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("Nhớ tôi");

        LogInButton1.setBackground(new java.awt.Color(255, 102, 0));
        LogInButton1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N
        LogInButton1.setForeground(new java.awt.Color(255, 255, 255));
        LogInButton1.setText("Đăng nhập");
        LogInButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogInButton1ActionPerformed(evt);
            }
        });

        RegisterButton1.setBackground(new java.awt.Color(51, 153, 255));
        RegisterButton1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N
        RegisterButton1.setForeground(new java.awt.Color(255, 255, 255));
        RegisterButton1.setText("Tạo tài khoản mới");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                        .addGap(78, 78, 78))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(RegisterButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                            .addComponent(LogInButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LogEmailText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(LogPasswordText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogEmailText, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogPasswordText, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogInButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(RegisterButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void LogInButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogInButton1ActionPerformed
        String loginEmail = LogEmailText.getText().trim();
        String loginPass = LogPasswordText.getText().trim();
        
        System.out.println("Attempting login with email: " + loginEmail + ", password: " + loginPass);
        
        if(loginEmail.isEmpty() || loginPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!loginEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try(Connection conn = DBConnection.DBConnection()) {
            String sql = "SELECT UserID, Role FROM myuser WHERE Email = ? AND Userpassword = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loginEmail);
            stmt.setString(2, loginPass);
        
            ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String role = rs.getString("Role");
            int userID = rs.getInt("UserID");
            System.out.println("Login successful for email: " + loginEmail + ", role: " + role);
            if(role.equals("Admin")) {
                // Đóng form Login
                javax.swing.JFrame loginFrame = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
                loginFrame.dispose();
                // Mở Dashboard
                Dashboard dashboard = new Dashboard();
                dashboard.setVisible(true);
            } else {
                javax.swing.JFrame loginFrame = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
                loginFrame.dispose();
                // Mở Home
                Home home = new Home(userID);
                home.setVisible(true);
            }
            // Xử lý Remember me
            if(jCheckBox1.isSelected()) {
                saveCredentials(loginEmail, loginPass);
            } else {
                clearCredentials();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Email hoặc mật khẩu không đúng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } 
    }//GEN-LAST:event_LogInButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Swing.TextField LogEmailText;
    private javax.swing.JButton LogInButton1;
    private Swing.PasswordField LogPasswordText;
    private javax.swing.JButton RegisterButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
