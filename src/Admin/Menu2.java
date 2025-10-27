package Admin;

import DatabaseConnect.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableModel;

public class Menu2 extends javax.swing.JInternalFrame {
    // Lưu trữ quan hệ tỉnh -> quận/huyện -> xã/phường
    private final Map<String, List<String>> districtsByProvince = new HashMap<>();
    private final Map<String, List<String>> wardsByDistrict = new HashMap<>();
    private List<Object[]> StationData = new ArrayList<>(); // Lưu trữ dữ liệu để tìm kiếm
    
    public Menu2() {
        initComponents();
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
        BasicInternalFrameUI ui=(BasicInternalFrameUI)this.getUI();
        ui.setNorthPane(null);
        // Khởi tạo danh sách tỉnh, quận, xã
        initializeLocationData();
        // Thêm listener để cập nhật quận/huyện và xã/phường
        Province_City.addActionListener(evt -> updateDistricts());
        District.addActionListener(evt -> updateWards());
        loadTerminalFromDatabase();
        // Thêm TableModelListener để xử lý chỉnh sửa trực tiếp
        StationTable.getModel().addTableModelListener(new TableModelListener() {
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
            DefaultTableModel model = (DefaultTableModel) StationTable.getModel();

            // Chỉ xử lý cột TerminalName (1) và TerminalPhone (5)
            if (column != 1 && column != 5) {
                return;
            }

            int stationID = (Integer) model.getValueAt(row, 0);
            String newValue = (String) model.getValueAt(row, column);
            String columnName = column == 1 ? "StationName" : "StationlPhone";

            // Kiểm tra dữ liệu hợp lệ
            if (column == 1 && (newValue == null || newValue.trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Tên nhà xe không được để trống!");
                revertTableChange(row, column);
                return;
            }
            if (column == 5 && (newValue == null || !newValue.matches("^(0[235789][0-9]{8}|[0-9]{3,4}[0-9]{6,7}|\\\\+84[235789][0-9]{8}|\\\\+84[0-9]{3,4}[0-9]{6,7})([- ]?[0-9]{3,4})*$"))) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10-11 chữ số!");
                revertTableChange(row, column);
                return;
            }

            // Cập nhật cơ sở dữ liệu
            try (Connection conn = DBConnection.DBConnection()) {
                String sql = "UPDATE station SET " + columnName + " = ? WHERE StationID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, newValue);
                stmt.setInt(2, stationID);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    // Cập nhật TerminalData
                    for (Object[] data : StationData) {
                        if ((Integer) data[0] == stationID) {
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
        DefaultTableModel model = (DefaultTableModel) StationTable.getModel();
        int terminalID = (Integer) model.getValueAt(row, 0);
        for (Object[] data : StationData) {
            if ((Integer) data[0] == terminalID) {
                model.setValueAt(data[column], row, column);
                break;
            }
        }
    }
    
    //Tải dữ liệu từ bảng Terminal
    private void loadTerminalFromDatabase() {
        DefaultTableModel model = (DefaultTableModel) StationTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại
        StationData.clear(); // Xóa dữ liệu cũ

        try (Connection conn = DBConnection.DBConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM station")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] rowData = new Object[] {
                    rs.getInt("StationID"),
                    rs.getString("StationName"),
                    rs.getString("Ward_Commune"),
                    rs.getString("District"),
                    rs.getString("Province_City"),
                    rs.getString("StationlPhone")
                };
                model.insertRow(0, rowData); // Thêm vào đầu bảng
                StationData.add(0, rowData); // Thêm vào đầu Terminal data
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Khởi tạo dữ liệu tỉnh, quận, xã (mã hóa cứng)
    private void initializeLocationData() {
        // Đầy đủ 63 tỉnh/thành phố và quận/huyện mẫu
        districtsByProvince.put("An Giang", Arrays.asList("Long Xuyên", "Châu Đốc", "Tân Châu"));
        districtsByProvince.put("Bà Rịa - Vũng Tàu", Arrays.asList("Vũng Tàu", "Bà Rịa", "Long Điền"));
        districtsByProvince.put("Bắc Giang", Arrays.asList("Bắc Giang", "Lục Nam", "Yên Dũng"));
        districtsByProvince.put("Bắc Kạn", Arrays.asList("Bắc Kạn", "Ba Bể", "Ngân Sơn"));
        districtsByProvince.put("Bạc Liêu", Arrays.asList("Bạc Liêu", "Hòa Bình", "Vĩnh Lợi"));
        districtsByProvince.put("Bắc Ninh", Arrays.asList("Bắc Ninh", "Từ Sơn", "Quế Võ"));
        districtsByProvince.put("Bến Tre", Arrays.asList("Bến Tre", "Châu Thành", "Mỏ Cày Bắc"));
        districtsByProvince.put("Bình Định", Arrays.asList("Quy Nhơn", "An Nhơn", "Tuy Phước"));
        districtsByProvince.put("Bình Dương", Arrays.asList("Thủ Dầu Một", "Dĩ An", "Tân Uyên"));
        districtsByProvince.put("Bình Phước", Arrays.asList("Đồng Xoài", "Bình Long", "Chơn Thành"));
        districtsByProvince.put("Bình Thuận", Arrays.asList("Phan Thiết", "La Gi", "Tánh Linh"));
        districtsByProvince.put("Cà Mau", Arrays.asList("Cà Mau", "Thới Bình", "U Minh"));
        districtsByProvince.put("Cần Thơ", Arrays.asList("Ninh Kiều", "Cái Răng", "Bình Thủy"));
        districtsByProvince.put("Cao Bằng", Arrays.asList("Cao Bằng", "Hòa An", "Thạch An"));
        districtsByProvince.put("Đà Nẵng", Arrays.asList("Hải Châu", "Thanh Khê", "Sơn Trà", "Ngũ Hành Sơn", "Liên Chiểu", "Cẩm Lệ", "Hòa Vang"));
        districtsByProvince.put("Đắk Lắk", Arrays.asList("Buôn Ma Thuột", "Buôn Hồ", "Krông Búk"));
        districtsByProvince.put("Đắk Nông", Arrays.asList("Gia Nghĩa", "Đắk Mil", "Krông Nô"));
        districtsByProvince.put("Điện Biên", Arrays.asList("Điện Biên Phủ", "Mường Lay", "Điện Biên"));
        districtsByProvince.put("Đồng Nai", Arrays.asList("Biên Hòa", "Long Khánh", "Nhơn Trạch"));
        districtsByProvince.put("Đồng Tháp", Arrays.asList("Cao Lãnh", "Sa Đéc", "Hồng Ngự"));
        districtsByProvince.put("Gia Lai", Arrays.asList("Pleiku", "An Khê", "Ayun Pa"));
        districtsByProvince.put("Hà Giang", Arrays.asList("Hà Giang", "Vị Xuyên", "Bắc Mê"));
        districtsByProvince.put("Hà Nam", Arrays.asList("Phủ Lý", "Duy Tiên", "Kim Bảng"));
        districtsByProvince.put("Hà Nội", Arrays.asList("Ba Đình", "Hoàn Kiếm", "Đống Đa"));
        districtsByProvince.put("Hà Tĩnh", Arrays.asList("Hà Tĩnh", "Hồng Lĩnh", "Kỳ Anh"));
        districtsByProvince.put("Hải Dương", Arrays.asList("Hải Dương", "Chí Linh", "Nam Sách"));
        districtsByProvince.put("Hải Phòng", Arrays.asList("Hồng Bàng", "Ngô Quyền", "Lê Chân"));
        districtsByProvince.put("Hậu Giang", Arrays.asList("Vị Thanh", "Ngã Bảy", "Châu Thành"));
        districtsByProvince.put("Hòa Bình", Arrays.asList("Hòa Bình", "Lương Sơn", "Kỳ Sơn"));
        districtsByProvince.put("Hưng Yên", Arrays.asList("Hưng Yên", "Văn Lâm", "Ân Thi"));
        districtsByProvince.put("Khánh Hòa", Arrays.asList("Nha Trang", "Cam Ranh", "Diên Khánh"));
        districtsByProvince.put("Kiên Giang", Arrays.asList("Rạch Giá", "Hà Tiên", "Phú Quốc"));
        districtsByProvince.put("Kon Tum", Arrays.asList("Kon Tum", "Đắk Glei", "Sa Thầy"));
        districtsByProvince.put("Lai Châu", Arrays.asList("Lai Châu", "Tam Đường", "Mường Tè"));
        districtsByProvince.put("Lâm Đồng", Arrays.asList("Đà Lạt", "Bảo Lộc", "Đơn Dương"));
        districtsByProvince.put("Lạng Sơn", Arrays.asList("Lạng Sơn", "Cao Lộc", "Tràng Định"));
        districtsByProvince.put("Lào Cai", Arrays.asList("Lào Cai", "Sa Pa", "Bát Xát"));
        districtsByProvince.put("Long An", Arrays.asList("Tân An", "Kiến Tường", "Bến Lức"));
        districtsByProvince.put("Nam Định", Arrays.asList("Nam Định", "Mỹ Lộc", "Vụ Bản"));
        districtsByProvince.put("Nghệ An", Arrays.asList("Vinh", "Cửa Lò", "Thái Hòa"));
        districtsByProvince.put("Ninh Bình", Arrays.asList("Ninh Bình", "Tam Điệp", "Gia Viễn"));
        districtsByProvince.put("Ninh Thuận", Arrays.asList("Phan Rang - Tháp Chàm", "Ninh Hải", "Ninh Phước"));
        districtsByProvince.put("Phú Thọ", Arrays.asList("Việt Trì", "Phù Ninh", "Thanh Ba"));
        districtsByProvince.put("Phú Yên", Arrays.asList("Tuy Hòa", "Sông Cầu", "Đông Hòa"));
        districtsByProvince.put("Quảng Bình", Arrays.asList("Đồng Hới", "Ba Đồn", "Lệ Thủy"));
        districtsByProvince.put("Quảng Nam", Arrays.asList("Tam Kỳ", "Hội An", "Điện Bàn"));
        districtsByProvince.put("Quảng Ngãi", Arrays.asList("Quảng Ngãi", "Dung Quất", "Sơn Tịnh"));
        districtsByProvince.put("Quảng Ninh", Arrays.asList("Hạ Long", "Cẩm Phả", "Uông Bí"));
        districtsByProvince.put("Quảng Trị", Arrays.asList("Đông Hà", "Triệu Phong", "Hải Lăng"));
        districtsByProvince.put("Sóc Trăng", Arrays.asList("Sóc Trăng", "Ngã Năm", "Thạnh Trị"));
        districtsByProvince.put("Sơn La", Arrays.asList("Sơn La", "Mộc Châu", "Yên Châu"));
        districtsByProvince.put("Tây Ninh", Arrays.asList("Tây Ninh", "Trảng Bàng", "Gò Dầu"));
        districtsByProvince.put("Thái Bình", Arrays.asList("Thái Bình", "Quỳnh Phụ", "Hưng Hà"));
        districtsByProvince.put("Thái Nguyên", Arrays.asList("Thái Nguyên", "Sông Công", "Phổ Yên"));
        districtsByProvince.put("Thanh Hóa", Arrays.asList("Thanh Hóa", "Sầm Sơn", "Bỉm Sơn"));
        districtsByProvince.put("Thừa Thiên Huế", Arrays.asList("Huế", "Hương Thủy", "Hương Trà"));
        districtsByProvince.put("Tiền Giang", Arrays.asList("Mỹ Tho", "Gò Công", "Cai Lậy"));
        districtsByProvince.put("Trà Vinh", Arrays.asList("Trà Vinh", "Càng Long", "Châu Thành"));
        districtsByProvince.put("Tuyên Quang", Arrays.asList("Tuyên Quang", "Sơn Dương", "Yên Sơn"));
        districtsByProvince.put("Vĩnh Long", Arrays.asList("Vĩnh Long", "Bình Minh", "Long Hồ"));
        districtsByProvince.put("Vĩnh Phúc", Arrays.asList("Vĩnh Yên", "Phúc Yên", "Tam Dương"));
        districtsByProvince.put("Yên Bái", Arrays.asList("Yên Bái", "Nghĩa Lộ", "Trấn Yên"));
        districtsByProvince.put("TP Hồ Chí Minh", Arrays.asList("Quận 1", "Quận 3", "Quận 7"));

        // Quận/huyện và xã/phường
        // Hà Nội
        wardsByDistrict.put("Ba Đình", Arrays.asList("Phúc Xá", "Trúc Bạch", "Vĩnh Phúc"));
        wardsByDistrict.put("Hoàn Kiếm", Arrays.asList("Hàng Bạc", "Hàng Bông", "Cửa Đông"));
        wardsByDistrict.put("Đống Đa", Arrays.asList("Cát Linh", "Láng Thượng", "Ô Chợ Dừa"));

        // TP Hồ Chí Minh
        wardsByDistrict.put("Quận 1", Arrays.asList("Bến Nghé", "Bến Thành", "Cầu Ông Lãnh"));
        wardsByDistrict.put("Quận 3", Arrays.asList("Phường 7", "Phường 8", "Phường 9"));
        wardsByDistrict.put("Quận 7", Arrays.asList("Tân Phú", "Tân Quy", "Phú Mỹ"));

        // Đà Nẵng (đầy đủ)
        wardsByDistrict.put("Hải Châu", Arrays.asList("Bình Hiên", "Bình Thuận", "Hải Châu I", "Hải Châu II", "Hòa Cường Bắc", "Hòa Cường Nam", "Hòa Thuận Đông", "Hòa Thuận Tây", "Nam Dương", "Phước Ninh", "Thạch Thang", "Thanh Bình", "Thuận Phước"));
        wardsByDistrict.put("Thanh Khê", Arrays.asList("An Khê", "Chính Gián", "Hòa Khê", "Tam Thuận", "Tân Chính", "Thạc Gián", "Thanh Khê Đông", "Thanh Khê Tây", "Vĩnh Trung", "Xuân Hà"));
        wardsByDistrict.put("Sơn Trà", Arrays.asList("An Hải Bắc", "An Hải Đông", "An Hải Tây", "Mân Thái", "Nại Hiên Đông", "Phước Mỹ", "Thọ Quang"));
        wardsByDistrict.put("Ngũ Hành Sơn", Arrays.asList("Hòa Hải", "Hòa Quý", "Khuê Mỹ", "Mỹ An"));
        wardsByDistrict.put("Liên Chiểu", Arrays.asList("Hòa Hiệp Bắc", "Hòa Hiệp Nam", "Hòa Khánh Bắc", "Hòa Khánh Nam", "Hòa Minh"));
        wardsByDistrict.put("Cẩm Lệ", Arrays.asList("Hòa An", "Hòa Phát", "Hòa Thọ Đông", "Hòa Thọ Tây", "Hòa Xuân", "Khuê Trung"));
        wardsByDistrict.put("Hòa Vang", Arrays.asList("Hòa Bắc", "Hòa Châu", "Hòa Khương", "Hòa Liên", "Hòa Nhơn", "Hòa Ninh", "Hòa Phong", "Hòa Phú", "Hòa Phước", "Hòa Sơn", "Hòa Tiến"));

        // An Giang
        wardsByDistrict.put("Long Xuyên", Arrays.asList("Mỹ Bình", "Mỹ Long", "Đông Xuyên"));
        wardsByDistrict.put("Châu Đốc", Arrays.asList("Châu Phú A", "Châu Phú B", "Vĩnh Mỹ"));
        wardsByDistrict.put("Tân Châu", Arrays.asList("Long Thạnh", "Long Hưng", "Vĩnh Hòa"));

        // Cần Thơ
        wardsByDistrict.put("Ninh Kiều", Arrays.asList("Cái Khế", "An Hội", "Tân An"));
        wardsByDistrict.put("Cái Răng", Arrays.asList("Lê Bình", "Hưng Phú", "Hưng Thạnh"));
        wardsByDistrict.put("Bình Thủy", Arrays.asList("Bình Thủy", "Trà An", "Trà Nóc"));

        // Các tỉnh khác (mẫu cho mỗi quận/huyện)
        wardsByDistrict.put("Vũng Tàu", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Bà Rịa", Arrays.asList("Phước Hưng", "Phước Nguyên", "Long Hương"));
        wardsByDistrict.put("Long Điền", Arrays.asList("Long Điền", "An Ngãi", "Tam Phước"));
        wardsByDistrict.put("Bắc Giang", Arrays.asList("Trần Phú", "Ngô Quyền", "Lê Lợi"));
        wardsByDistrict.put("Lục Nam", Arrays.asList("Đồi Ngô", "Đông Hưng", "Tam Dị"));
        wardsByDistrict.put("Yên Dũng", Arrays.asList("Tiền Phong", "Tân Liễu", "Đồng Phúc"));
        wardsByDistrict.put("Bắc Kạn", Arrays.asList("Đức Xuân", "Nguyễn Thị Minh Khai", "Sông Cầu"));
        wardsByDistrict.put("Ba Bể", Arrays.asList("Chợ Rã", "Hà Hiệu", "Yến Dương"));
        wardsByDistrict.put("Ngân Sơn", Arrays.asList("Bằng Vân", "Cốc Đán", "Lãng Ngàn"));
        wardsByDistrict.put("Bạc Liêu", Arrays.asList("Phường 2", "Phường 3", "Phường 5"));
        wardsByDistrict.put("Hòa Bình", Arrays.asList("Hòa Bình", "Hiệp Thành", "Vĩnh Mỹ"));
        wardsByDistrict.put("Vĩnh Lợi", Arrays.asList("Châu Hưng", "Vĩnh Hưng", "Hưng Thành"));
        wardsByDistrict.put("Bắc Ninh", Arrays.asList("Suối Hoa", "Võ Cường", "Đại Phúc"));
        wardsByDistrict.put("Từ Sơn", Arrays.asList("Đồng Nguyên", "Đông Ngàn", "Tam Sơn"));
        wardsByDistrict.put("Quế Võ", Arrays.asList("Phù Lương", "Phù Lãng", "Việt Hùng"));
        wardsByDistrict.put("Bến Tre", Arrays.asList("Phường 4", "Phường 5", "Phường 6"));
        wardsByDistrict.put("Châu Thành", Arrays.asList("Tân Phú", "Tân Thạch", "Quới Sơn"));
        wardsByDistrict.put("Mỏ Cày Bắc", Arrays.asList("Phú Mỹ", "Hòa Lộc", "Tân Thành Bình"));
        wardsByDistrict.put("Quy Nhơn", Arrays.asList("Nhơn Bình", "Nhơn Phú", "Trần Quang Diệu"));
        wardsByDistrict.put("An Nhơn", Arrays.asList("Bình Định", "Nhơn Hậu", "Nhơn Phong"));
        wardsByDistrict.put("Tuy Phước", Arrays.asList("Phước Hưng", "Phước Nghĩa", "Phước Sơn"));
        wardsByDistrict.put("Thủ Dầu Một", Arrays.asList("Phú Cường", "Phú Hòa", "Hòa Phú"));
        wardsByDistrict.put("Dĩ An", Arrays.asList("An Bình", "Dĩ An", "Đông Hòa"));
        wardsByDistrict.put("Tân Uyên", Arrays.asList("Uyên Hưng", "Tân Phước Khánh", "Khánh Bình"));
        wardsByDistrict.put("Đồng Xoài", Arrays.asList("Tân Bình", "Tân Phú", "Tân Thiện"));
        wardsByDistrict.put("Bình Long", Arrays.asList("An Lộc", "Hưng Chiến", "Phú Thịnh"));
        wardsByDistrict.put("Chơn Thành", Arrays.asList("Nha Bích", "Minh Long", "Thành Tâm"));
        wardsByDistrict.put("Phan Thiết", Arrays.asList("Mũi Né", "Phú Hài", "Phú Thủy"));
        wardsByDistrict.put("La Gi", Arrays.asList("Tân An", "Tân Bình", "Phước Hội"));
        wardsByDistrict.put("Tánh Linh", Arrays.asList("Lạc Tánh", "Đức Phú", "Huy Khiêm"));
        wardsByDistrict.put("Cà Mau", Arrays.asList("Phường 2", "Phường 4", "Phường 5"));
        wardsByDistrict.put("Thới Bình", Arrays.asList("Thới Bình", "Biển Bạch", "Hồ Thị Kỷ"));
        wardsByDistrict.put("U Minh", Arrays.asList("U Minh", "Khánh Hòa", "Khánh Thuận"));
        wardsByDistrict.put("Cao Bằng", Arrays.asList("Hợp Giang", "Sông Hiến", "Sông Bằng"));
        wardsByDistrict.put("Hòa An", Arrays.asList("Nước Hai", "Bình Long", "Dân Chủ"));
        wardsByDistrict.put("Thạch An", Arrays.asList("Đức Xuân", "Lê Lợi", "Thị Ngân"));
        wardsByDistrict.put("Buôn Ma Thuột", Arrays.asList("Tân Lợi", "Tân Thành", "Tự An"));
        wardsByDistrict.put("Buôn Hồ", Arrays.asList("An Lạc", "An Bình", "Bình Tân"));
        wardsByDistrict.put("Krông Búk", Arrays.asList("Cư Pơng", "Ea Ngai", "Pơng Drang"));
        wardsByDistrict.put("Gia Nghĩa", Arrays.asList("Nghĩa Đức", "Nghĩa Thành", "Nghĩa Trung"));
        wardsByDistrict.put("Đắk Mil", Arrays.asList("Đắk Lao", "Đắk R’La", "Đức Mạnh"));
        wardsByDistrict.put("Krông Nô", Arrays.asList("Đắk Mâm", "Nam Đà", "Quảng Phú"));
        wardsByDistrict.put("Điện Biên Phủ", Arrays.asList("Mường Thanh", "Nam Thanh", "Thanh Bình"));
        wardsByDistrict.put("Mường Lay", Arrays.asList("Lay Nưa", "Na Lay", "Sông Đà"));
        wardsByDistrict.put("Điện Biên", Arrays.asList("Mường Pồn", "Thanh Hưng", "Thanh Luông"));
        wardsByDistrict.put("Biên Hòa", Arrays.asList("Tân Phong", "Trảng Dài", "Hóa An"));
        wardsByDistrict.put("Long Khánh", Arrays.asList("Xuân Bình", "Xuân Hòa", "Xuân Thanh"));
        wardsByDistrict.put("Nhơn Trạch", Arrays.asList("Phú Hội", "Phước An", "Long Tân"));
        wardsByDistrict.put("Cao Lãnh", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Sa Đéc", Arrays.asList("An Hòa", "Tân Quy Đông", "Tân Quy Tây"));
        wardsByDistrict.put("Hồng Ngự", Arrays.asList("An Lộc", "An Thạnh", "Bình Thạnh"));
        wardsByDistrict.put("Pleiku", Arrays.asList("Hoa Lư", "Tây Sơn", "Yên Đổ"));
        wardsByDistrict.put("An Khê", Arrays.asList("An Bình", "An Phú", "An Tân"));
        wardsByDistrict.put("Ayun Pa", Arrays.asList("Cheo Reo", "Hòa Bình", "Ia RBol"));
        wardsByDistrict.put("Hà Giang", Arrays.asList("Ngọc Hà", "Nguyễn Trãi", "Quang Trung"));
        wardsByDistrict.put("Vị Xuyên", Arrays.asList("Phong Quang", "Thuận Hòa", "Vị Xuyên"));
        wardsByDistrict.put("Bắc Mê", Arrays.asList("Đường Âm", "Minh Ngọc", "Yên Phú"));
        wardsByDistrict.put("Phủ Lý", Arrays.asList("Hai Bà Trưng", "Lê Hồng Phong", "Minh Khai"));
        wardsByDistrict.put("Duy Tiên", Arrays.asList("Bạch Thượng", "Châu Sơn", "Tiên Nội"));
        wardsByDistrict.put("Kim Bảng", Arrays.asList("Đồng Hóa", "Khả Phong", "Ngọc Sơn"));
        wardsByDistrict.put("Hà Tĩnh", Arrays.asList("Bắc Hà", "Nam Hà", "Trần Phú"));
        wardsByDistrict.put("Hồng Lĩnh", Arrays.asList("Bắc Hồng", "Nam Hồng", "Trung Lương"));
        wardsByDistrict.put("Kỳ Anh", Arrays.asList("Kỳ Liên", "Kỳ Long", "Kỳ Phương"));
        wardsByDistrict.put("Hải Dương", Arrays.asList("Cẩm Thượng", "Nhị Châu", "Quang Trung"));
        wardsByDistrict.put("Chí Linh", Arrays.asList("Phả Lại", "Sao Đỏ", "Bến Tắm"));
        wardsByDistrict.put("Nam Sách", Arrays.asList("Ái Quốc", "Nam Hưng", "Thanh Quang"));
        wardsByDistrict.put("Hồng Bàng", Arrays.asList("Hạ Lý", "Hùng Vương", "Sở Dầu"));
        wardsByDistrict.put("Ngô Quyền", Arrays.asList("Cầu Đất", "Lạc Viên", "Máy Tơ"));
        wardsByDistrict.put("Lê Chân", Arrays.asList("An Biên", "Cát Dài", "Đông Hải"));
        wardsByDistrict.put("Vị Thanh", Arrays.asList("Phường I", "Phường III", "Phường V"));
        wardsByDistrict.put("Ngã Bảy", Arrays.asList("Hiệp Thành", "Lái Hiếu", "Ngã Bảy"));
        wardsByDistrict.put("Châu Thành", Arrays.asList("Đông Phú", "Nguyệt Hóa", "Phú Hữu"));
        wardsByDistrict.put("Hòa Bình", Arrays.asList("Chợ Mới", "Đồng Tâm", "Thủy Tây"));
        wardsByDistrict.put("Lương Sơn", Arrays.asList("Cư Yên", "Hòa Sơn", "Trường Sơn"));
        wardsByDistrict.put("Kỳ Sơn", Arrays.asList("Hữu Kiệm", "Mông Hóa", "Yên Quang"));
        wardsByDistrict.put("Hưng Yên", Arrays.asList("Hiến Nam", "Lam Sơn", "Lê Lợi"));
        wardsByDistrict.put("Văn Lâm", Arrays.asList("Đình Dù", "Lạc Đạo", "Minh Hải"));
        wardsByDistrict.put("Ân Thi", Arrays.asList("Bãi Sậy", "Hồng Vân", "Quảng Lãng"));
        wardsByDistrict.put("Nha Trang", Arrays.asList("Lộc Thọ", "Phước Hải", "Vĩnh Phước"));
        wardsByDistrict.put("Cam Ranh", Arrays.asList("Cam Lợi", "Cam Phúc Bắc", "Cam Thuận"));
        wardsByDistrict.put("Diên Khánh", Arrays.asList("Diên An", "Diên Lạc", "Diên Phú"));
        wardsByDistrict.put("Rạch Giá", Arrays.asList("Vĩnh Thanh", "Vĩnh Lợi", "Vĩnh Thông"));
        wardsByDistrict.put("Hà Tiên", Arrays.asList("Bình San", "Đông Hồ", "Tô Châu"));
        wardsByDistrict.put("Phú Quốc", Arrays.asList("Dương Đông", "Cửa Cạn", "Cửa Dương"));
        wardsByDistrict.put("Kon Tum", Arrays.asList("Quang Trung", "Thắng Lợi", "Trần Hưng Đạo"));
        wardsByDistrict.put("Đắk Glei", Arrays.asList("Đắk Blô", "Đắk Môn", "Đắk Nhoong"));
        wardsByDistrict.put("Sa Thầy", Arrays.asList("Rơ Kơi", "Sa Bình", "Sa Nghĩa"));
        wardsByDistrict.put("Lai Châu", Arrays.asList("Đoàn Kết", "Quyết Thắng", "Tân Phong"));
        wardsByDistrict.put("Tam Đường", Arrays.asList("Bản Bo", "Bản Giang", "Bình Lư"));
        wardsByDistrict.put("Mường Tè", Arrays.asList("Mường Tè", "Pa Vệ Sử", "Thu Lũm"));
        wardsByDistrict.put("Đà Lạt", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Bảo Lộc", Arrays.asList("B’Lao", "Đại Lào", "Lộc Phát"));
        wardsByDistrict.put("Đơn Dương", Arrays.asList("Đ’Rong", "Ka Đô", "Lạc Xuân"));
        wardsByDistrict.put("Lạng Sơn", Arrays.asList("Chi Lăng", "Hoàng Đồng", "Vĩnh Trại"));
        wardsByDistrict.put("Cao Lộc", Arrays.asList("Đồng Đăng", "Hòa Cư", "Xuất Lễ"));
        wardsByDistrict.put("Tràng Định", Arrays.asList("Chí Minh", "Đoàn Kết", "Kháng Chiến"));
        wardsByDistrict.put("Lào Cai", Arrays.asList("Bắc Cường", "Bắc Lệnh", "Cốc Lếu"));
        wardsByDistrict.put("Sa Pa", Arrays.asList("Hàm Rồng", "Sa Pa", "Ô Quý Hồ"));
        wardsByDistrict.put("Bát Xát", Arrays.asList("Bản Vược", "Cốc Mỳ", "Trịnh Tường"));
        wardsByDistrict.put("Tân An", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Kiến Tường", Arrays.asList("Phường 1", "Phường 2", "Tuyên Thạnh"));
        wardsByDistrict.put("Bến Lức", Arrays.asList("An Thạnh", "Bến Lức", "Tân Bửu"));
        wardsByDistrict.put("Nam Định", Arrays.asList("Hạ Long", "Lộc Vượng", "Vị Hoàng"));
        wardsByDistrict.put("Mỹ Lộc", Arrays.asList("Mỹ Hà", "Mỹ Phúc", "Mỹ Thắng"));
        wardsByDistrict.put("Vụ Bản", Arrays.asList("Cộng Hòa", "Hiển Khánh", "Tam Thanh"));
        wardsByDistrict.put("Vinh", Arrays.asList("Hà Huy Tập", "Hưng Bình", "Trường Thi"));
        wardsByDistrict.put("Cửa Lò", Arrays.asList("Nghi Hòa", "Nghi Hương", "Nghi Thu"));
        wardsByDistrict.put("Thái Hòa", Arrays.asList("Hòa Hiếu", "Long Sơn", "Quang Tiến"));
        wardsByDistrict.put("Ninh Bình", Arrays.asList("Đông Thành", "Nam Bình", "Tân Thành"));
        wardsByDistrict.put("Tam Điệp", Arrays.asList("Bắc Sơn", "Nam Sơn", "Tân Bình"));
        wardsByDistrict.put("Gia Viễn", Arrays.asList("Gia Hòa", "Gia Lạc", "Gia Sinh"));
        wardsByDistrict.put("Phan Rang - Tháp Chàm", Arrays.asList("Đô Vinh", "Kinh Dinh", "Tấn Tài"));
        wardsByDistrict.put("Ninh Hải", Arrays.asList("Hải Thạnh", "Nhơn Hải", "Tri Hải"));
        wardsByDistrict.put("Ninh Phước", Arrays.asList("An Hải", "Phước Dân", "Phước Hải"));
        wardsByDistrict.put("Việt Trì", Arrays.asList("Bạch Hạc", "Dữu Lâu", "Vân Cơ"));
        wardsByDistrict.put("Phù Ninh", Arrays.asList("An Đạo", "Bình Phú", "Phù Ninh"));
        wardsByDistrict.put("Thanh Ba", Arrays.asList("Đồng Xuân", "Hanh Cù", "Thanh Hà"));
        wardsByDistrict.put("Tuy Hòa", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Sông Cầu", Arrays.asList("Xuân Cảnh", "Xuân Lộc", "Xuân Thịnh"));
        wardsByDistrict.put("Đông Hòa", Arrays.asList("Hòa Hiệp Bắc", "Hòa Vinh", "Hòa Xuân Tây"));
        wardsByDistrict.put("Đồng Hới", Arrays.asList("Bắc Lý", "Bắc Nghĩa", "Hải Thành"));
        wardsByDistrict.put("Ba Đồn", Arrays.asList("Quảng Long", "Quảng Phong", "Quảng Thọ"));
        wardsByDistrict.put("Lệ Thủy", Arrays.asList("Kiến Giang", "Ngư Thủy Bắc", "Phong Thủy"));
        wardsByDistrict.put("Tam Kỳ", Arrays.asList("An Mỹ", "An Xuân", "Hòa Hương"));
        wardsByDistrict.put("Hội An", Arrays.asList("Cẩm Châu", "Cẩm Nam", "Minh An"));
        wardsByDistrict.put("Điện Bàn", Arrays.asList("Điện Hòa", "Điện Nam Bắc", "Điện Ngọc"));
        wardsByDistrict.put("Quảng Ngãi", Arrays.asList("Lê Hồng Phong", "Nghĩa Chánh", "Trần Phú"));
        wardsByDistrict.put("Dung Quất", Arrays.asList("Bình Chánh", "Bình Đông", "Bình Thuận"));
        wardsByDistrict.put("Sơn Tịnh", Arrays.asList("Tịnh Ấn Đông", "Tịnh Hà", "Tịnh Sơn"));
        wardsByDistrict.put("Hạ Long", Arrays.asList("Bãi Cháy", "Hà Khẩu", "Hồng Hải"));
        wardsByDistrict.put("Cẩm Phả", Arrays.asList("Cẩm Bình", "Cẩm Đông", "Cẩm Phú"));
        wardsByDistrict.put("Uông Bí", Arrays.asList("Bắc Sơn", "Nam Khê", "Thanh Sơn"));
        wardsByDistrict.put("Đông Hà", Arrays.asList("Phường 1", "Phường 2", "Phường 5"));
        wardsByDistrict.put("Triệu Phong", Arrays.asList("Triệu An", "Triệu Đại", "Triệu Thành"));
        wardsByDistrict.put("Hải Lăng", Arrays.asList("Hải Ba", "Hải Phú", "Hải Quế"));
        wardsByDistrict.put("Sóc Trăng", Arrays.asList("Phường 2", "Phường 3", "Phường 6"));
        wardsByDistrict.put("Ngã Năm", Arrays.asList("Long Bình", "Mỹ Bình", "Tân Long"));
        wardsByDistrict.put("Thạnh Trị", Arrays.asList("Phú Lộc", "Thạnh Trị", "Vĩnh Lợi"));
        wardsByDistrict.put("Sơn La", Arrays.asList("Chiềng Lề", "Chiềng Cơi", "Quyết Tâm"));
        wardsByDistrict.put("Mộc Châu", Arrays.asList("Chiềng Hắc", "Mộc Châu", "NT Mộc Châu"));
        wardsByDistrict.put("Yên Châu", Arrays.asList("Chiềng Đông", "Sập Vạt", "Yên Sơn"));
        wardsByDistrict.put("Tây Ninh", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Trảng Bàng", Arrays.asList("An Tịnh", "Gia Bình", "Lộc Hưng"));
        wardsByDistrict.put("Gò Dầu", Arrays.asList("Cẩm Giang", "Hiệp Thạnh", "Phước Đông"));
        wardsByDistrict.put("Thái Bình", Arrays.asList("Bồ Xuyên", "Lê Hồng Phong", "Trần Lãm"));
        wardsByDistrict.put("Quỳnh Phụ", Arrays.asList("An Bài", "An Khê", "Quỳnh Hội"));
        wardsByDistrict.put("Hưng Hà", Arrays.asList("Chi Lăng", "Hòa Bình", "Tân Tiến"));
        wardsByDistrict.put("Thái Nguyên", Arrays.asList("Hoàng Văn Thụ", "Quang Vinh", "Trưng Vương"));
        wardsByDistrict.put("Sông Công", Arrays.asList("Bá Xuyên", "Cải Đan", "Mỏ Chè"));
        wardsByDistrict.put("Phổ Yên", Arrays.asList("Ba Hàng", "Đắc Sơn", "Đồng Tiến"));
        wardsByDistrict.put("Thanh Hóa", Arrays.asList("Ba Đình", "Đông Thọ", "Ngọc Trạo"));
        wardsByDistrict.put("Sầm Sơn", Arrays.asList("Bắc Sơn", "Quảng Cư", "Trường Sơn"));
        wardsByDistrict.put("Bỉm Sơn", Arrays.asList("Ba Đình", "Ngọc Trạo", "Phú Sơn"));
        wardsByDistrict.put("Huế", Arrays.asList("Phú Hậu", "Phú Nhuận", "Thuận Thành"));
        wardsByDistrict.put("Hương Thủy", Arrays.asList("Dương Hòa", "Phú Bài", "Thủy Vân"));
        wardsByDistrict.put("Hương Trà", Arrays.asList("Hương Chữ", "Hương Văn", "Tứ Hạ"));
        wardsByDistrict.put("Mỹ Tho", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Gò Công", Arrays.asList("Phường 1", "Phường 2", "Long Chánh"));
        wardsByDistrict.put("Cai Lậy", Arrays.asList("Mỹ Hạnh Đông", "Mỹ Hạnh Trung", "Nhị Quý"));
        wardsByDistrict.put("Trà Vinh", Arrays.asList("Phường 2", "Phường 3", "Phường 8"));
        wardsByDistrict.put("Càng Long", Arrays.asList("Càng Long", "Mỹ Cẩm", "Tân An"));
        wardsByDistrict.put("Châu Thành", Arrays.asList("Hòa Lợi", "Hòa Minh", "Long Hòa"));
        wardsByDistrict.put("Tuyên Quang", Arrays.asList("Nông Tiến", "Phan Thiết", "Tân Quang"));
        wardsByDistrict.put("Sơn Dương", Arrays.asList("Hào Phú", "Sơn Nam", "Thiện Kế"));
        wardsByDistrict.put("Yên Sơn", Arrays.asList("Đội Bình", "Lang Quán", "Tân Long"));
        wardsByDistrict.put("Vĩnh Long", Arrays.asList("Phường 1", "Phường 2", "Phường 3"));
        wardsByDistrict.put("Bình Minh", Arrays.asList("Cái Vồn", "Đông Bình", "Thuận An"));
        wardsByDistrict.put("Long Hồ", Arrays.asList("An Bình", "Long Phước", "Tân Hạnh"));
        wardsByDistrict.put("Vĩnh Yên", Arrays.asList("Đồng Tâm", "Ngô Quyền", "Tích Sơn"));
        wardsByDistrict.put("Phúc Yên", Arrays.asList("Hùng Vương", "Trưng Nhì", "Trưng Trắc"));
        wardsByDistrict.put("Tam Dương", Arrays.asList("Đồng Tĩnh", "Hợp Hòa", "Hướng Đạo"));
        wardsByDistrict.put("Yên Bái", Arrays.asList("Đồng Tâm", "Hồng Hà", "Yên Ninh"));
        wardsByDistrict.put("Nghĩa Lộ", Arrays.asList("Pú Trạng", "Tân An", "Trung Tâm"));
        wardsByDistrict.put("Trấn Yên", Arrays.asList("Báo Đáp", "Hồng Ca", "Yên Bình"));
    }
    
    // Cập nhật danh sách quận/huyện dựa trên tỉnh/thành phố
    private void updateDistricts() {
        String selectedProvince = (String) Province_City.getSelectedItem();
        DefaultComboBoxModel<String> districtModel = new DefaultComboBoxModel<>();
        districtModel.addElement("Quận/Huyện");
        Ward_Commune.setModel(new DefaultComboBoxModel<>(new String[] { "Xã/Phường" })); // Reset xã/phường

        if (selectedProvince != null && districtsByProvince.containsKey(selectedProvince)) {
            for (String district : districtsByProvince.get(selectedProvince)) {
                districtModel.addElement(district);
            }
        }
        District.setModel(districtModel);
    }

    // Cập nhật danh sách xã/phường dựa trên quận/huyện
    private void updateWards() {
        String selectedDistrict = (String) District.getSelectedItem();
        DefaultComboBoxModel<String> wardModel = new DefaultComboBoxModel<>();
        wardModel.addElement("Xã/Phường");

        if (selectedDistrict != null && wardsByDistrict.containsKey(selectedDistrict)) {
            for (String ward : wardsByDistrict.get(selectedDistrict)) {
                wardModel.addElement(ward);
            }
        }
        Ward_Commune.setModel(wardModel);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Sname = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        AddTerminalbtn = new javax.swing.JButton();
        Province_City = new javax.swing.JComboBox<>();
        Ward_Commune = new javax.swing.JComboBox<>();
        District = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        Sphone = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        FindStationbtn = new javax.swing.JButton();
        FindStation = new javax.swing.JTextField();
        DeleteStationbtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        StationTable = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(650, 550));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Nhà xe");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Xã/Phường");

        jLabel4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Quận/Huyện");

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel5.setText("Tỉnh/Thành Phố");

        Sname.setPreferredSize(new java.awt.Dimension(0, 25));

        jButton1.setText("Làm trống");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        AddTerminalbtn.setText("Thêm");
        AddTerminalbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddTerminalbtnActionPerformed(evt);
            }
        });

        Province_City.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tỉnh/Thành Phố", "An Giang", "Bà Rịa - Vũng Tàu", "Bạc Liêu", "Bắc Giang", "Bắc Kạn", "Bắc Ninh", "Bến Tre", "Bình Dương", "Bình Định", "Bình Phước", "Bình Thuận", "Cà Mau", "Cao Bằng", "Cần Thơ", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lạng Sơn", "Lào Cai", "Lâm Đồng", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thành phố Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái" }));

        Ward_Commune.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Xã/Phường" }));

        District.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quận/Huyện" }));

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("SĐT Nhà xe");

        Sphone.setPreferredSize(new java.awt.Dimension(0, 25));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Sname, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
                    .addComponent(Province_City, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Ward_Commune, 0, 256, Short.MAX_VALUE)
                            .addComponent(District, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Sphone, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddTerminalbtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(Sname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(Province_City, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(District, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(Ward_Commune, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(Sphone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(AddTerminalbtn)))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        FindStationbtn.setText("Tìm kiếm");
        FindStationbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindStationbtnActionPerformed(evt);
            }
        });

        DeleteStationbtn.setText("Xóa");
        DeleteStationbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteStationbtnActionPerformed(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        StationTable.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã nhà xe", "Tên nhà xe", "Xã/Phường", "Quận/Huyện", "Tỉnh/Thành phố", "Số điện thoại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(StationTable);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(FindStationbtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(FindStation, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(DeleteStationbtn)
                .addContainerGap(99, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FindStationbtn)
                    .addComponent(FindStation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteStationbtn))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setBounds(0, 0, 648, 550);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Sname.setText("");
        Province_City.setSelectedIndex(0);
        District.setSelectedIndex(0);
        Ward_Commune.setSelectedIndex(0);
        Sphone.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void AddTerminalbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddTerminalbtnActionPerformed
        String stationname = Sname.getText().trim();
        String stationphone = Sphone.getText().trim();
        String province_city = (String) Province_City.getSelectedItem();
        String ward_Commnue = (String) Ward_Commune.getSelectedItem();
        String district = (String) District.getSelectedItem();
        
        // Kiểm tra dữ liệu đầu vào
        if(stationname.isEmpty() || stationphone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin");
            return;
        }
        
        //Kiểm tra định dạng số điện thoại
        if(!stationphone.matches("^(0[235789][0-9]{8}|[0-9]{3,4}[0-9]{6,7}|\\+84[235789][0-9]{8}|\\+84[0-9]{3,4}[0-9]{6,7})([- ]?[0-9]{3,4})*$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ");
            return;
        }
        
        try (Connection conn = DBConnection.DBConnection()) {
            // Thêm vào bảng myuser
            String sql = "INSERT INTO station (StationName, Ward_Commune, District, Province_City, StationlPhone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, stationname);
            stmt.setString(2, ward_Commnue);
            stmt.setString(3, district);
            stmt.setString(4, province_city);
            stmt.setString(5, stationphone);
            stmt.executeUpdate();

            // Lấy user_id được tạo
            ResultSet rs = stmt.getGeneratedKeys();
            int stationID = rs.next() ? rs.getInt(1) : -1;

            // Thêm vào jTable1 và userData
            DefaultTableModel model = (DefaultTableModel) StationTable.getModel();
            Object[] rowData = new Object[] {stationID, stationname, ward_Commnue, district, province_city, stationphone};
            model.insertRow(0, rowData); // Thêm vào đầu bảng
            StationData.add(0, rowData); // Thêm vào đầu terminalData

            // Cuộn đến hàng mới
            StationTable.scrollRectToVisible(StationTable.getCellRect(0, 0, true));

            // Reset form
            jButton1ActionPerformed(evt);
            javax.swing.JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi" + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_AddTerminalbtnActionPerformed

    private void FindStationbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindStationbtnActionPerformed
        String searchText = FindStation.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) StationTable.getModel();
        model.setRowCount(0); // Xóa bảng hiện tại

        if (searchText.isEmpty()) {
            // Hiển thị toàn bộ dữ liệu
            for (Object[] row : StationData) {
                model.addRow(row);
            }
        } else {
            // Lọc dữ liệu theo tên hoặc email
            for (Object[] row : StationData) {
                String username = ((String) row[1]).toLowerCase();
                String email = ((String) row[3]).toLowerCase();
                if (username.contains(searchText) || email.contains(searchText)) {
                    model.addRow(row);
                }
            }
        }
    }//GEN-LAST:event_FindStationbtnActionPerformed

    private void DeleteStationbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteStationbtnActionPerformed
        int selectedRow = StationTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) StationTable.getModel();
            int userIdToDelete = (Integer) model.getValueAt(selectedRow, 0);

            try (Connection conn = DBConnection.DBConnection()) {
                // Xóa khỏi bảng terminal
                String sql = "DELETE FROM station WHERE StationID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userIdToDelete);
                stmt.executeUpdate();

                // Xóa khỏi terminalDât và TerminalTable
                StationData.removeIf(row -> (Integer) row[0] == userIdToDelete);
                model.removeRow(selectedRow);
                javax.swing.JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } catch (SQLException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Lỗi" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn để xóa!");
        }
    }//GEN-LAST:event_DeleteStationbtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddTerminalbtn;
    private javax.swing.JButton DeleteStationbtn;
    private javax.swing.JComboBox<String> District;
    private javax.swing.JTextField FindStation;
    private javax.swing.JButton FindStationbtn;
    private javax.swing.JComboBox<String> Province_City;
    private javax.swing.JTextField Sname;
    private javax.swing.JTextField Sphone;
    private javax.swing.JTable StationTable;
    private javax.swing.JComboBox<String> Ward_Commune;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
