import gui.DormitoryManagementGUI;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Khởi tạo và hiển thị giao diện quản lý ký túc xá
            DormitoryManagementGUI gui = new DormitoryManagementGUI();
            gui.setTitle("Dormitory Management");
            gui.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            gui.pack();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}
