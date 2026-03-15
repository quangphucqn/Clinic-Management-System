## Phân Tích Yêu Cầu Hệ Thống

Hệ thống quản lý phòng khám được xây dựng nhằm hỗ trợ quá trình quản lý và vận hành khám một cách hiệu quả, đồng thời cung cấp dịch vụ để bệnh nhân có thể đặt lịch và theo dõi quá trình khám chữa bệnh trực tuyến. Hệ thống phục vụ ba nhóm người dùng chính gồm: **Bệnh nhân**, **Bác sĩ** và **Quản trị viên (Admin)**.

### 1. Người dùng chung (User)

Tất cả người dùng khi muốn sử dụng các chức năng của hệ thống đều phải thực hiện đăng ký tài khoản và đăng nhập vào hệ thống. Sau khi đăng nhập thành công, hệ thống sẽ phân quyền tương ứng với vai trò của người dùng như bệnh nhân, bác sĩ hoặc quản trị viên.

### 2. Chức năng dành cho Bệnh nhân

Bệnh nhân là đối tượng sử dụng chính của hệ thống để đặt lịch và theo dõi quá trình khám bệnh. Các chức năng chính bao gồm:

* **Đăng ký tài khoản:** Người dùng có thể tạo tài khoản mới để sử dụng hệ thống.
* **Đăng nhập:** Người dùng đăng nhập vào hệ thống bằng tài khoản đã đăng ký.
* **Đặt lịch khám online:** Bệnh nhân có thể lựa chọn bác sĩ, chuyên khoa và khung giờ khám phù hợp để đặt lịch khám trực tuyến.
* **Thanh toán đặt cọc:** Khi đặt lịch khám, bệnh nhân cần thực hiện thanh toán đặt cọc để xác nhận lịch hẹn.
* **Xem lịch sử khám bệnh:** Bệnh nhân có thể xem lại các lần khám trước đây, bao gồm thông tin chẩn đoán và điều trị.
* **Xem đơn thuốc:** Sau khi khám, bệnh nhân có thể xem đơn thuốc được bác sĩ kê.
* **Xem kết quả xét nghiệm:** Bệnh nhân có thể truy cập và xem các kết quả xét nghiệm do bác sĩ yêu cầu.
* **Đánh giá bác sĩ:** Sau khi hoàn thành buổi khám, bệnh nhân có thể đánh giá chất lượng dịch vụ của bác sĩ.

### 3. Chức năng dành cho Bác sĩ

Bác sĩ sử dụng hệ thống để quản lý lịch khám và thực hiện các hoạt động chuyên môn liên quan đến khám chữa bệnh.

* **Xem lịch khám trong ngày:** Bác sĩ có thể xem danh sách các bệnh nhân đã đặt lịch khám trong ngày.
* **Quản lý hồ sơ cá nhân:** Bác sĩ có thể cập nhật thông tin cá nhân và thông tin chuyên môn.
* **Khám bệnh và ghi chẩn đoán:** Trong quá trình khám, bác sĩ ghi nhận tình trạng bệnh và chẩn đoán cho bệnh nhân.
* **Kê đơn thuốc:** Sau khi chẩn đoán, bác sĩ có thể kê đơn thuốc cho bệnh nhân.
* **Yêu cầu xét nghiệm:** Trong trường hợp cần thiết, bác sĩ có thể yêu cầu bệnh nhân thực hiện các xét nghiệm.
* **Xem lịch sử bệnh nhân:** Bác sĩ có thể truy cập lịch sử khám bệnh của bệnh nhân để hỗ trợ việc chẩn đoán và điều trị.

### 4. Chức năng dành cho Quản trị viên (Admin)

Quản trị viên chịu trách nhiệm quản lý và vận hành toàn bộ hệ thống.

* **Quản lý bác sĩ:** Thêm mới, chỉnh sửa hoặc xóa thông tin bác sĩ trong hệ thống.
* **Quản lý danh mục chuyên khoa:** Quản lý các chuyên khoa được cung cấp trong phòng khám.
* **Quản lý danh mục thuốc:** Quản lý thông tin các loại thuốc được sử dụng trong hệ thống.
* **Cấu hình slot khám:** Thiết lập các khung giờ khám cho từng bác sĩ.
* **Quản lý thông báo:** Tạo và gửi thông báo đến người dùng trong hệ thống.
* **Báo cáo doanh thu:** Theo dõi và thống kê doanh thu từ các hoạt động khám chữa bệnh.


