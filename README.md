# 🏢 Hệ Thống Quản Lý Ký Túc Xá

<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Swing-GUI-yellow?style=for-the-badge" alt="Swing"/>
  <img src="https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit"/>
</div>

## 📝 Tổng Quan

<div align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-blue?style=flat-square" alt="Version"/>
  <img src="https://img.shields.io/badge/Release%20Date-2025-blue?style=flat-square" alt="Release Date"/>
  <img src="https://img.shields.io/badge/License-Educational-green?style=flat-square" alt="License"/>
</div>

### 🎯 Mục Tiêu
> Xây dựng hệ thống quản lý ký túc xá với giao diện đồ họa, giúp quản lý thông tin sinh viên, phòng ở, hợp đồng và phí một cách hiệu quả.

### 📋 Giới Thiệu
Đây là hệ thống quản lý ký túc xá được phát triển như một phần của học phần Lập Trình Hướng Đối Tượng (Học kỳ 3, 2025). Dự án này được xây dựng với các đặc điểm:

- 🎓 **Công nghệ**: Java Swing, JUnit
- 🏗️ **Kiến trúc**: Hướng đối tượng, Thread-safe
- 🔄 **Dữ liệu**: Lưu trữ local với file text
- 🤝 **Giao diện**: GUI thân thiện người dùng

## ✨ Tính Năng

### 👥 Quản Lý Sinh Viên
- 📝 CRUD thông tin sinh viên (mã số, họ tên, ngày sinh, giới tính, ...)
- 🏠 Phân phòng cho sinh viên
- 📊 Theo dõi trạng thái sinh viên (ACTIVE, INACTIVE, GRADUATED)

### 🏠 Quản Lý Phòng
- 🔄 Quản lý thông tin phòng (mã phòng, loại phòng, sức chứa, giá)
- 📋 Theo dõi tình trạng phòng (AVAILABLE, OCCUPIED, FULL)
- 🔍 Tìm kiếm phòng theo nhiều tiêu chí

### 📄 Quản Lý Hợp Đồng
- ✍️ Tạo và quản lý hợp đồng thuê phòng
- ⏱️ Theo dõi thời hạn hợp đồng
- 💰 Quản lý giá thuê phòng

### 💰 Quản Lý Phí
- 💵 Quản lý các loại phí:
  - Phí phòng (ROOM_FEE)
  - Phí điện (ELECTRICITY)
  - Phí nước (WATER)
  - Phí vệ sinh (CLEANING)
  - Phí internet (INTERNET)
  - Phí bảo trì (MAINTENANCE)
- 📅 Theo dõi hạn thanh toán
- 🔄 Cập nhật trạng thái thanh toán

### 📊 Báo Cáo & Thống Kê
- 📈 Báo cáo tổng quan tình trạng KTX
- 📉 Thống kê công suất sử dụng phòng
- 💹 Báo cáo tài chính (thu/chi)
- 📋 Báo cáo danh sách sinh viên theo tiêu chí
- 🔍 Xuất báo cáo theo khoảng thời gian
- 📑 Hỗ trợ xuất báo cáo PDF/Excel

## 🎯 Các Nguyên Lý OOP Được Áp Dụng

### 1. 🔒 Tính Đóng Gói (Encapsulation)
- Private fields với getters/setters
- Validation trong setters
- Access modifiers phù hợp cho từng thành phần

### 2. 🔄 Tính Kế Thừa (Inheritance)
- Kế thừa từ các lớp cơ sở của Swing (JPanel, JFrame)
- Tái sử dụng code cho các panel tương tự

### 3. 🔄 Tính Đa Hình (Polymorphism)
- Override các phương thức từ lớp cha
- Xử lý sự kiện đa dạng trong GUI
- Interface implementation cho các thành phần

### 4. 🎨 Tính Trừu Tượng (Abstraction)
- Thiết kế interface rõ ràng
- Che giấu chi tiết triển khai
- Tách biệt logic nghiệp vụ và giao diện

## 🏗️ Kiến Trúc Hệ Thống

### 1. 🗃️ Mô Hình Dữ Liệu
- **Entities**: 
  - `Student`: Thông tin sinh viên
  - `Room`: Thông tin phòng
  - `Contract`: Hợp đồng thuê phòng
  - `Fee`: Quản lý phí
  - `Report`: Mô hình báo cáo
  - `Statistics`: Thống kê số liệu
  - `FeeType`: Enum các loại phí

### 2. 🎨 Giao Diện (GUI)
- **Main Components**:
  - `LoginFrame`: Màn hình đăng nhập
  - `MainFrame`: Cửa sổ chính
  - `StudentPanel`: Quản lý sinh viên
  - `RoomPanel`: Quản lý phòng
  - `ContractPanel`: Quản lý hợp đồng
  - `FeePanel`: Quản lý phí
  - `ReportPanel`: Quản lý báo cáo
  - `StatisticsPanel`: Hiển thị thống kê

### 3. 🔧 Utility
- **DataStorage**: 
  - Singleton pattern
  - Thread-safe với ConcurrentHashMap
  - Atomic counters cho ID
  - Lưu trữ và đọc dữ liệu từ file

## 📁 Cấu Trúc Dự Án
```
project/
├── 📂 src/
│   ├── 📂 model/
│   │   ├── Student.java              # Thông tin sinh viên
│   │   ├── Room.java                 # Thông tin phòng
│   │   ├── Contract.java             # Hợp đồng
│   │   ├── Fee.java                  # Quản lý phí
│   │   ├── FeeType.java             # Enum loại phí
│   │   ├── Report.java              # Mô hình báo cáo
│   │   └── Statistics.java          # Thống kê
│   ├── 📂 gui/
│   │   ├── LoginFrame.java          # Màn hình đăng nhập
│   │   ├── MainFrame.java           # Cửa sổ chính
│   │   ├── StudentPanel.java        # Giao diện QL sinh viên
│   │   ├── RoomPanel.java           # Giao diện QL phòng
│   │   ├── ContractPanel.java       # Giao diện QL hợp đồng
│   │   ├── FeePanel.java            # Giao diện QL phí
│   │   ├── ReportPanel.java         # Giao diện báo cáo
│   │   └── StatisticsPanel.java     # Giao diện thống kê
│   └── 📂 util/
│       └── DataStorage.java         # Lưu trữ dữ liệu
├── 📂 test/
│   ├── 📂 model/
│   │   ├── StudentTest.java
│   │   ├── RoomTest.java
│   │   ├── ContractTest.java
│   │   ├── FeeTest.java
│   │   ├── FeeTypeTest.java
│   │   ├── ReportTest.java
│   │   └── StatisticsTest.java
│   └── 📂 util/
│       └── DataStorageTest.java
├── 📂 data/                          # Dữ liệu
│   ├── students.txt
│   ├── rooms.txt
│   ├── contracts.txt
│   └── fees.txt
├── 📂 lib/                           # Thư viện
│   ├── junit-4.13.2.jar
│   └── hamcrest-core-1.3.jar
└── 📂 reports/                       # Thư mục chứa báo cáo
    ├── pdf/
    └── excel/
```

## 🛠️ Công Nghệ Sử Dụng
- **Java**: JDK 8 trở lên
- **GUI**: Java Swing
- **Testing**: JUnit 4
- **Storage**: File-based (txt)

## 📋 Yêu Cầu Hệ Thống
- ☕ JDK 8 trở lên
- 💻 IDE hỗ trợ Java hoặc chạy trực tiếp từ command line
- 💾 Ít nhất 512MB RAM
- 📊 Độ phân giải màn hình tối thiểu 1280x720

## 🚀 Hướng Dẫn Cài Đặt & Chạy

1️⃣ Clone repository
```bash
git clone https://github.com/BachNguyenn/quanlyktx.git
```

2️⃣ Biên dịch project
```bash
javac -d bin -cp "lib/*" src/gui/*.java src/model/*.java src/util/*.java
```

3️⃣ Chạy chương trình
```bash
java -cp "bin;lib/*" gui.LoginFrame
```

4️⃣ Đăng nhập với tài khoản mặc định
- Username: admin
- Password: admin

## 🧪 Kiểm Thử
Dự án bao gồm các unit test toàn diện:

📝 **Test Classes**:
- `DataStorageTest`: Kiểm thử lưu trữ dữ liệu
- `StudentTest`: Kiểm thử model Student
- `RoomTest`: Kiểm thử model Room
- `ContractTest`: Kiểm thử model Contract
- `FeeTest`: Kiểm thử model Fee
- `FeeTypeTest`: Kiểm thử enum FeeType
- `ReportTest`: Kiểm thử model Report
- `StatisticsTest`: Kiểm thử model Statistics

Chạy test:
```bash
java -cp "bin;lib/*" org.junit.runner.JUnitCore model.FeeTypeTest model.ReportTest model.StatisticsTest model.StudentTest model.RoomTest model.ContractTest model.FeeTest util.DataStorageTest
```

## 👥 Thành Viên
- Nguyễn Tùng Bách - K17 CNTTVJ tại Đại học Phenikaa

## 📜 Giấy Phép
> Dự án này là một phần của chương trình học tại PHENIKAA UNIVERSITY.

---
<div align="center">
  <i>Developed with ❤️</i>
</div> 