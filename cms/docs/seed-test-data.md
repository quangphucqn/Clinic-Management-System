# Seed data test nhanh cho CMS

File seed: `scripts/seed-test-data.sql`

## Cac tai khoan co san

Tat ca tai khoan deu co mat khau: `password`

- Admin: `seed.admin`
- Doctor 1: `seed.doctor1`
- Doctor 2: `seed.doctor2`
- Patient 1: `seed.patient1`
- Patient 2: `seed.patient2`
- Patient 3: `seed.patient3`

## Cach nap du lieu vao PostgreSQL

### Cach 1: psql

```bash
psql -h <DB_HOST> -p <DB_PORT> -U <DB_USERNAME> -d <DB_NAME> -f scripts/seed-test-data.sql
```

### Cach 2: PgAdmin

1. Mo Query Tool
2. Mo file `scripts/seed-test-data.sql`
3. Run script

## Du lieu da bao phu cac chuc nang

- Dang nhap theo 3 role: ADMIN / DOCTOR / PATIENT
- Quan ly bac si, chuyen khoa, slot
- Dat lich kham voi day du status: `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`
- Giao dich thanh toan voi status: `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED`
- Ho so benh an, don thuoc, chi tiet don thuoc
- Lenh xet nghiem va ket qua xet nghiem
- Danh gia bac si (co appointment da review va appointment chua review de test create review)
- Notification theo role va theo user cu the

## Kiem tra nhanh sau khi seed

```sql
select role, count(*) from users where username like 'seed.%' group by role;
select status, count(*) from appointments where id::text like '00000000-0000-0000-0000-0000000005%' group by status;
select payment_status, count(*) from payment_transactions where id::text like '00000000-0000-0000-0000-0000000006%' group by payment_status;
```

Luu y: script duoc viet theo kieu upsert theo `id` de co the run lai ma khong tao ban ghi trung.
