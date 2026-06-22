# Milks Backend Requirements And Roadmap

Tài liệu này dùng để bám sát yêu cầu bài làm trong quá trình xây dựng `milks-be`.
Hướng triển khai hiện tại: **Spring Boot REST API + JPA + SQL Server**, chuẩn bị để dùng với **Next.js frontend** sau này.

## 1. Công Nghệ

- Backend: Spring Boot MVC theo hướng REST API.
- Persistence: Spring Data JPA.
- Database: SQL Server.
- View:
  - Chính: JSON REST API cho Next.js.
  - Phụ: Thymeleaf chỉ thêm nếu môn học bắt buộc phải có giao diện server-rendered.

## 2. Yêu Cầu Dữ Liệu

| Yêu cầu | Trạng thái | Ghi chú |
| --- | --- | --- |
| Có 1 admin | Done | Đã có trong `db.sql` |
| Có 2 khách hàng | Done | Đã có trong `db.sql` |
| Ít nhất 30 sản phẩm | Done | Đã có 30 sản phẩm trong `db.sql` |
| Mỗi sản phẩm có 1 hình | Done | Đã có `imageUrl` cho từng sản phẩm |

## 3. Yêu Cầu Khách Hàng

| Chức năng | API dự kiến | Trạng thái |
| --- | --- | --- |
| Đăng ký | `POST /api/auth/register` | Not started |
| Đăng nhập | `POST /api/auth/login` | Not started |
| Đăng xuất | Frontend xóa token hoặc session | Not started |
| Xem sản phẩm | `GET /api/products` | In progress |
| Phân trang sản phẩm | `GET /api/products?page=0&size=12` | In progress |
| Tìm theo tên | `GET /api/products?keyword=vinamilk` | In progress |
| Tìm theo loại | `GET /api/products?categoryId=1` | In progress |
| Sắp xếp giá tăng dần | `GET /api/products?sort=price,asc` | In progress |
| Sắp xếp giá giảm dần | `GET /api/products?sort=price,desc` | In progress |
| Thêm vào giỏ hàng | Next.js localStorage hoặc server cart | Not started |
| Sửa số lượng giỏ hàng | Next.js localStorage hoặc server cart | Not started |
| Xóa khỏi giỏ hàng | Next.js localStorage hoặc server cart | Not started |
| Thanh toán | `POST /api/orders/checkout` | Not started |
| Nếu chưa login thì yêu cầu login | Security rule cho checkout | Not started |

## 4. Yêu Cầu Admin

| Chức năng | API dự kiến | Trạng thái |
| --- | --- | --- |
| Đăng nhập | `POST /api/auth/login` | Not started |
| Đăng xuất | Frontend xóa token hoặc session | Not started |
| Xem sản phẩm | `GET /api/admin/products` | Not started |
| Thêm sản phẩm | `POST /api/admin/products` | Not started |
| Sửa sản phẩm | `PUT /api/admin/products/{id}` | Not started |
| Xóa sản phẩm | `DELETE /api/admin/products/{id}` | Not started |
| Xem đơn hàng | `GET /api/admin/orders` | Not started |
| Cập nhật trạng thái đơn | `PATCH /api/admin/orders/{id}/status` | Not started |
| Xem doanh thu theo ngày/tháng/năm | `GET /api/admin/revenue?date=yyyy-MM-dd` | Not started |
| Biểu đồ doanh thu 7 ngày | Response trả mảng `last7Days` | Not started |

## 5. Trạng Thái Code Hiện Tại

Đã có:

- `db.sql` với dữ liệu mẫu.
- JPA entities:
  - `Account`
  - `Customer`
  - `Category`
  - `Product`
  - `OrderHeader`
  - `OrderDetail`
- Repositories cơ bản.
- DTO request/response cơ bản:
  - `RegisterRequest`
  - `LoginRequest`
  - `CheckoutRequest`
  - `CategoryResponse`
  - `ProductResponse`
  - `PageResponse`
- `ProductService` đang được xây dựng.

Cần sửa ngay:

- `ProductRepository` cần hỗ trợ search/filter/pagination.
- `ProductService` nên dùng cách dễ hiểu bằng `@Query` thay vì `Specification` nếu bạn thấy `Specification` khó.
- Chưa có controller REST API.
- Chưa có `SecurityConfig`, nên Spring Security sẽ chặn API mặc định.

## 6. Thứ Tự Làm Tiếp Theo

### Step 1: Sửa Product Search Cho Dễ Hiểu

Thay vì dùng `Specification`, dùng `@Query` trong `ProductRepository`.

Mục tiêu:

- Build không lỗi.
- `ProductService.findProducts(...)` gọi repository query rõ ràng.

### Step 2: Làm Category API

Tạo:

- `CategoryService`
- `CategoryApiController`

API:

```http
GET /api/categories
```

Mục tiêu:

- Kiểm tra Spring Boot kết nối SQL Server thành công.
- Trả được danh sách category từ database.

### Step 3: Làm Product API Public

Tạo:

- `ProductApiController`

API:

```http
GET /api/products
GET /api/products/{id}
```

Query cần hỗ trợ:

```http
GET /api/products?page=0&size=12
GET /api/products?keyword=vinamilk
GET /api/products?categoryId=1
GET /api/products?sort=price,asc
GET /api/products?sort=price,desc
```

### Step 4: Thêm Security Config Tạm Thời

Trong giai đoạn đầu, cho public các API đọc dữ liệu:

```text
GET /api/categories/**
GET /api/products/**
```

Sau đó mới siết quyền cho:

```text
/api/orders/**
/api/admin/**
```

### Step 5: Làm Auth API

API:

```http
POST /api/auth/register
POST /api/auth/login
GET /api/auth/me
```

Ban đầu có thể login bằng SHA-256 để khớp dữ liệu demo trong `db.sql`.
Sau khi chạy ổn, nâng lên JWT để Next.js dùng.

### Step 6: Làm Checkout API

API:

```http
POST /api/orders/checkout
```

Request chỉ gửi:

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

Backend bắt buộc tự đọc giá sản phẩm từ database, không tin giá từ frontend.

### Step 7: Làm Admin Product CRUD

API:

```http
GET    /api/admin/products
POST   /api/admin/products
GET    /api/admin/products/{id}
PUT    /api/admin/products/{id}
DELETE /api/admin/products/{id}
```

### Step 8: Làm Admin Order Management

API:

```http
GET   /api/admin/orders
GET   /api/admin/orders/{id}
PATCH /api/admin/orders/{id}/status
```

Status hợp lệ:

```text
NEW
SHIPPING
PAID
```

Mapping tiếng Việt:

```text
NEW      = Mới
SHIPPING = Đã vận chuyển
PAID     = Đã thanh toán
```

### Step 9: Làm Revenue API

API:

```http
GET /api/admin/revenue?date=2026-06-22
```

Response cần có:

```json
{
  "dateRevenue": 0,
  "monthRevenue": 0,
  "yearRevenue": 0,
  "last7Days": [
    {
      "date": "2026-06-22",
      "revenue": 0
    }
  ]
}
```

Chỉ tính đơn hàng có status `PAID`.

### Step 10: Tích Hợp Next.js

Next.js sẽ gọi API:

```text
GET /api/products
GET /api/categories
POST /api/auth/login
POST /api/orders/checkout
```

Cart nên lưu ở frontend bằng `localStorage` trong giai đoạn đầu.

## 7. Definition Of Done

Project được xem là đạt yêu cầu khi:

- Chạy được Spring Boot app.
- Kết nối được SQL Server.
- Có đủ dữ liệu seed theo yêu cầu.
- Customer API chạy đủ:
  - register
  - login
  - xem sản phẩm
  - search/filter/sort
  - checkout
- Admin API chạy đủ:
  - product CRUD
  - order status update
  - revenue report
- API trả JSON rõ ràng, không trả trực tiếp entity nếu có thể tránh.
- Các endpoint cần quyền admin/customer được bảo vệ bằng Spring Security.

