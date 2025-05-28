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
Đây là hệ thống quản lý ký túc xá được phát triển như một phần của học phần Lập Trình Hướng Đối Tượng (Học kỳ 3, 2025) bởi Nhóm 9, K17. Dự án này được xây dựng với các đặc điểm:

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
- 📋 Theo dõi tình trạng phòng (còn trống/đã đầy)
- 🔍 Tìm kiếm phòng theo nhiều tiêu chí

### 📄 Quản Lý Hợp Đồng
- ✍️ Tạo và quản lý hợp đồng thuê phòng
- ⏱️ Theo dõi thời hạn hợp đồng
- 💰 Quản lý giá thuê phòng

### 💰 Quản Lý Phí
- 💵 Quản lý các loại phí (phí phòng, phí dịch vụ)
- 📅 Theo dõi hạn thanh toán
- 🔄 Cập nhật trạng thái thanh toán

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

### 2. 🎨 Giao Diện (GUI)
- **Main Components**:
  - `MainFrame`: Cửa sổ chính
  - `StudentPanel`: Quản lý sinh viên
  - `RoomPanel`: Quản lý phòng
  - `ContractPanel`: Quản lý hợp đồng
  - `FeePanel`: Quản lý phí

### 3. 🔧 Utility
- **DataStorage**: 
  - Singleton pattern
  - Thread-safe với ConcurrentHashMap
  - Atomic counters cho ID
  - Lưu trữ và đọc dữ liệu từ file

## 📁 Cấu Trúc Dự Án
```
src/
├── 📂 main/       # Entry point
├── 📂 model/      # Các entity classes
├── 📂 gui/        # Giao diện người dùng
├── 📂 util/       # DataStorage và utilities
└── 📂 test/       # Unit tests
```

## 🛠️ Công Nghệ Sử Dụng
- **Java**: JDK 8+
- **GUI**: Java Swing
- **Testing**: JUnit 4
- **Build Tool**: Maven
- **Storage**: File-based (txt)

## 📋 Yêu Cầu Hệ Thống
- ☕ JDK 8 trở lên
- 💻 IDE hỗ trợ Java (Eclipse, IntelliJ IDEA, NetBeans)
- 💾 Ít nhất 512MB RAM
- 📊 Độ phân giải màn hình tối thiểu 1280x720

## 🚀 Hướng Dẫn Cài Đặt

1️⃣ Clone repository
```bash
git clone https://github.com/BachNguyenn/quanlyktx.git
```

2️⃣ Mở dự án trong IDE  
3️⃣ Build dự án  
4️⃣ Chạy file Main trong package main

## 🧪 Kiểm Thử
Dự án bao gồm các unit test toàn diện:

📝 **Test Classes**:
- `DataStorageTest`: Kiểm thử lưu trữ dữ liệu
- `StudentTest`: Kiểm thử model Student
- `RoomTest`: Kiểm thử model Room
- `ContractTest`: Kiểm thử model Contract
- `FeeTest`: Kiểm thử model Fee

## 👥 Thành Viên
### Nhóm 9 - K17
- Nguyễn Tùng Bách - K17 CNTTVJ tại Đại học Phenikaa

## 📜 Giấy Phép
> Dự án này là một phần của chương trình học tại PHENIKAA UNIVERSITY.

---
<div align="center">
  <i>Developed with ❤️ by BachNguyenn</i>
</div> 