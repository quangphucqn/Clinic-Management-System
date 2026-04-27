BEGIN;

-- ===============================================================
-- CMS seed data for end-to-end feature testing
-- Safe to re-run: all inserts are upserted by fixed UUID id
-- Password for all seed users: password
-- BCrypt hash source: "password"
-- ===============================================================

-- Shared bcrypt hash for raw password: password
-- $2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m

-- 1) USERS
INSERT INTO users (
    id, username, password, role, full_name, email, phone_number, active, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000001', 'seed.admin',   '$2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m', 'ADMIN',   'Seed Admin',      'seed.admin@cms.local',   '0900000001', TRUE, '2026-01-01 08:00:00', '2026-04-25 08:00:00'),
    ('00000000-0000-0000-0000-000000000011', 'seed.doctor1', '$2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m', 'DOCTOR',  'Dr. Seed Heart',  'seed.doctor1@cms.local', '0900000011', TRUE, '2026-01-02 08:00:00', '2026-04-25 08:00:00'),
    ('00000000-0000-0000-0000-000000000012', 'seed.doctor2', '$2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m', 'DOCTOR',  'Dr. Seed Skin',   'seed.doctor2@cms.local', '0900000012', TRUE, '2026-01-03 08:00:00', '2026-04-25 08:00:00'),
    ('00000000-0000-0000-0000-000000000021', 'seed.patient1','$2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m', 'PATIENT', 'Nguyen Van Seed A','seed.patient1@cms.local','0900000021', TRUE, '2026-01-05 08:00:00', '2026-04-25 08:00:00'),
    ('00000000-0000-0000-0000-000000000022', 'seed.patient2','$2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m', 'PATIENT', 'Tran Thi Seed B',  'seed.patient2@cms.local','0900000022', TRUE, '2026-02-10 08:00:00', '2026-04-25 08:00:00'),
    ('00000000-0000-0000-0000-000000000023', 'seed.patient3','$2a$10$7UTSoPho3BoNhy7lbgqKNOUkm9ltNSBZoUvu355XAJgxeCZvwyH9m', 'PATIENT', 'Le Van Seed C',    'seed.patient3@cms.local','0900000023', TRUE, '2026-03-12 08:00:00', '2026-04-25 08:00:00')
ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
    password = EXCLUDED.password,
    role = EXCLUDED.role,
    full_name = EXCLUDED.full_name,
    email = EXCLUDED.email,
    phone_number = EXCLUDED.phone_number,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 2) SPECIALTIES
INSERT INTO specialties (
    id, name, description, active, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000101', 'Seed Tim mach',      'Khoa tim mach de test booking va thong ke', TRUE, '2026-01-02 09:00:00', '2026-04-25 09:00:00'),
    ('00000000-0000-0000-0000-000000000102', 'Seed Da lieu',       'Khoa da lieu de test doctor filter',          TRUE, '2026-01-02 09:00:00', '2026-04-25 09:00:00'),
    ('00000000-0000-0000-0000-000000000103', 'Seed Than kinh',     'Khoa than kinh de test danh sach khoa',       TRUE, '2026-01-02 09:00:00', '2026-04-25 09:00:00')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 3) DOCTORS
INSERT INTO doctors (
    id, user_account_id, specialty_id, license_number, room_number,
    years_of_experience, biography, active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000201',
        '00000000-0000-0000-0000-000000000011',
        '00000000-0000-0000-0000-000000000101',
        'SEED-LIC-DOC-001',
        'P101',
        12,
        'Bac si tim mach co kinh nghiem theo doi tang huyet ap va suy tim.',
        TRUE,
        '2026-01-05 10:00:00',
        '2026-04-25 10:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000202',
        '00000000-0000-0000-0000-000000000012',
        '00000000-0000-0000-0000-000000000102',
        'SEED-LIC-DOC-002',
        'P202',
        8,
        'Bac si da lieu chuyen dieu tri viem da co dia va mun trung ca.',
        TRUE,
        '2026-01-06 10:00:00',
        '2026-04-25 10:00:00'
    )
ON CONFLICT (id) DO UPDATE SET
    user_account_id = EXCLUDED.user_account_id,
    specialty_id = EXCLUDED.specialty_id,
    license_number = EXCLUDED.license_number,
    room_number = EXCLUDED.room_number,
    years_of_experience = EXCLUDED.years_of_experience,
    biography = EXCLUDED.biography,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 4) PATIENTS
INSERT INTO patients (
    id, user_account_id, gender, date_of_birth, address,
    emergency_contact_name, emergency_contact_phone,
    active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000301',
        '00000000-0000-0000-0000-000000000021',
        'MALE',
        '1996-07-12',
        'Quan 1, TP.HCM',
        'Nguyen Thi A',
        '0911000001',
        TRUE,
        '2026-01-05 11:00:00',
        '2026-04-25 11:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000302',
        '00000000-0000-0000-0000-000000000022',
        'FEMALE',
        '1994-11-03',
        'Quan 7, TP.HCM',
        'Tran Van B',
        '0911000002',
        TRUE,
        '2026-02-10 11:00:00',
        '2026-04-25 11:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000303',
        '00000000-0000-0000-0000-000000000023',
        'OTHER',
        '2000-01-20',
        'Thu Duc, TP.HCM',
        'Le Thi C',
        '0911000003',
        TRUE,
        '2026-03-12 11:00:00',
        '2026-04-25 11:00:00'
    )
ON CONFLICT (id) DO UPDATE SET
    user_account_id = EXCLUDED.user_account_id,
    gender = EXCLUDED.gender,
    date_of_birth = EXCLUDED.date_of_birth,
    address = EXCLUDED.address,
    emergency_contact_name = EXCLUDED.emergency_contact_name,
    emergency_contact_phone = EXCLUDED.emergency_contact_phone,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 5) TIME SLOT CONFIGS
INSERT INTO time_slot_configs (
    id, slot_code, start_time, end_time, max_patients_per_slot, enabled,
    active, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000401', 'SEED-SLOT-08', '08:00:00', '08:30:00', 10, TRUE, TRUE, '2026-01-01 07:00:00', '2026-04-25 07:00:00'),
    ('00000000-0000-0000-0000-000000000402', 'SEED-SLOT-09', '09:00:00', '09:30:00', 10, TRUE, TRUE, '2026-01-01 07:00:00', '2026-04-25 07:00:00'),
    ('00000000-0000-0000-0000-000000000403', 'SEED-SLOT-10', '10:00:00', '10:30:00', 8,  TRUE, TRUE, '2026-01-01 07:00:00', '2026-04-25 07:00:00'),
    ('00000000-0000-0000-0000-000000000404', 'SEED-SLOT-14', '14:00:00', '14:30:00', 8,  TRUE, TRUE, '2026-01-01 07:00:00', '2026-04-25 07:00:00')
ON CONFLICT (id) DO UPDATE SET
    slot_code = EXCLUDED.slot_code,
    start_time = EXCLUDED.start_time,
    end_time = EXCLUDED.end_time,
    max_patients_per_slot = EXCLUDED.max_patients_per_slot,
    enabled = EXCLUDED.enabled,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 6) APPOINTMENTS (mix statuses for testing)
INSERT INTO appointments (
    id, patient_id, doctor_id, time_slot_config_id, appointment_date, status,
    deposit_amount, reason, note, active, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000501', '00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000401', '2026-03-10', 'COMPLETED', 150000.00, 'Dau nguc nhe',      'Tai kham sau 2 tuan',          TRUE, '2026-03-05 09:15:00', '2026-03-10 09:00:00'),
    ('00000000-0000-0000-0000-000000000502', '00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000402', '2026-04-20', 'CONFIRMED', 150000.00, 'Hoi hop tim dap nhanh','Cho den som 10 phut',         TRUE, '2026-04-10 10:00:00', '2026-04-20 08:00:00'),
    ('00000000-0000-0000-0000-000000000503', '00000000-0000-0000-0000-000000000302', '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000401', '2026-05-05', 'PENDING',     0.00,      'Noi man do',        'Dang cho thanh toan',           TRUE, '2026-04-25 08:30:00', '2026-04-25 08:30:00'),
    ('00000000-0000-0000-0000-000000000504', '00000000-0000-0000-0000-000000000302', '00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000403', '2026-04-01', 'CANCELLED',   0.00,      'Kiem tra tong quat', 'Benh nhan huy lich',             TRUE, '2026-03-28 15:40:00', '2026-04-01 08:00:00'),
    ('00000000-0000-0000-0000-000000000505', '00000000-0000-0000-0000-000000000303', '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000402', '2026-02-15', 'COMPLETED', 200000.00, 'Ngua da keo dai',   'Da lam xet nghiem da',          TRUE, '2026-02-10 14:20:00', '2026-02-15 09:00:00'),
    ('00000000-0000-0000-0000-000000000506', '00000000-0000-0000-0000-000000000303', '00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000404', '2026-05-10', 'CONFIRMED', 150000.00, 'Kiem tra huyet ap',  'Dat coc thanh cong',            TRUE, '2026-04-22 12:00:00', '2026-04-22 12:00:00'),
    ('00000000-0000-0000-0000-000000000507', '00000000-0000-0000-0000-000000000301', '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000403', '2026-01-12', 'COMPLETED', 180000.00, 'Mun trung ca',      'Co toa thuoc boi ngoai da',      TRUE, '2026-01-07 09:00:00', '2026-01-12 10:00:00')
ON CONFLICT (id) DO UPDATE SET
    patient_id = EXCLUDED.patient_id,
    doctor_id = EXCLUDED.doctor_id,
    time_slot_config_id = EXCLUDED.time_slot_config_id,
    appointment_date = EXCLUDED.appointment_date,
    status = EXCLUDED.status,
    deposit_amount = EXCLUDED.deposit_amount,
    reason = EXCLUDED.reason,
    note = EXCLUDED.note,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 7) PAYMENT TRANSACTIONS
INSERT INTO payment_transactions (
    id, appointment_id, patient_id, amount, payment_method, payment_status,
    transaction_code, paid_at, active, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000601', '00000000-0000-0000-0000-000000000501', '00000000-0000-0000-0000-000000000301', 150000.00, 'MOMO', 'SUCCESS',  'SEED-APPT-501-TX', '2026-03-05 09:16:00', TRUE, '2026-03-05 09:16:00', '2026-03-05 09:16:00'),
    ('00000000-0000-0000-0000-000000000602', '00000000-0000-0000-0000-000000000502', '00000000-0000-0000-0000-000000000301', 150000.00, 'MOMO', 'SUCCESS',  'SEED-APPT-502-TX', '2026-04-10 10:01:00', TRUE, '2026-04-10 10:01:00', '2026-04-10 10:01:00'),
    ('00000000-0000-0000-0000-000000000603', '00000000-0000-0000-0000-000000000503', '00000000-0000-0000-0000-000000000302', 120000.00, 'MOMO', 'PENDING',  'SEED-APPT-503-TX', '2026-04-25 08:31:00', TRUE, '2026-04-25 08:31:00', '2026-04-25 08:31:00'),
    ('00000000-0000-0000-0000-000000000604', '00000000-0000-0000-0000-000000000504', '00000000-0000-0000-0000-000000000302', 120000.00, 'MOMO', 'FAILED',   'SEED-APPT-504-TX', '2026-03-28 15:41:00', TRUE, '2026-03-28 15:41:00', '2026-03-28 15:41:00'),
    ('00000000-0000-0000-0000-000000000605', '00000000-0000-0000-0000-000000000505', '00000000-0000-0000-0000-000000000303', 200000.00, 'MOMO', 'SUCCESS',  'SEED-APPT-505-TX', '2026-02-10 14:22:00', TRUE, '2026-02-10 14:22:00', '2026-02-10 14:22:00'),
    ('00000000-0000-0000-0000-000000000606', '00000000-0000-0000-0000-000000000507', '00000000-0000-0000-0000-000000000301', 180000.00, 'MOMO', 'REFUNDED', 'SEED-APPT-507-TX', '2026-01-07 09:01:00', TRUE, '2026-01-07 09:01:00', '2026-01-20 09:00:00')
ON CONFLICT (id) DO UPDATE SET
    appointment_id = EXCLUDED.appointment_id,
    patient_id = EXCLUDED.patient_id,
    amount = EXCLUDED.amount,
    payment_method = EXCLUDED.payment_method,
    payment_status = EXCLUDED.payment_status,
    transaction_code = EXCLUDED.transaction_code,
    paid_at = EXCLUDED.paid_at,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 8) MEDICAL RECORDS (for completed appointments)
INSERT INTO medical_records (
    id, appointment_id, patient_id, doctor_id, symptoms, diagnosis, conclusion,
    visited_at, active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000701',
        '00000000-0000-0000-0000-000000000501',
        '00000000-0000-0000-0000-000000000301',
        '00000000-0000-0000-0000-000000000201',
        'Hoi hop, kho tho nhe khi gang suc',
        'Tang huyet ap do 1',
        'Theo doi huyet ap tai nha, tai kham sau 2 tuan',
        '2026-03-10 08:45:00',
        TRUE,
        '2026-03-10 08:45:00',
        '2026-03-10 08:45:00'
    ),
    (
        '00000000-0000-0000-0000-000000000702',
        '00000000-0000-0000-0000-000000000505',
        '00000000-0000-0000-0000-000000000303',
        '00000000-0000-0000-0000-000000000202',
        'Ngua da, noi ban do o canh tay',
        'Viem da co dia',
        'Dung thuoc boi, giu am da, hen tai kham 10 ngay',
        '2026-02-15 09:10:00',
        TRUE,
        '2026-02-15 09:10:00',
        '2026-02-15 09:10:00'
    ),
    (
        '00000000-0000-0000-0000-000000000703',
        '00000000-0000-0000-0000-000000000507',
        '00000000-0000-0000-0000-000000000301',
        '00000000-0000-0000-0000-000000000202',
        'Da dau va mun viem',
        'Mun trung ca muc do vua',
        'Dieu tri ket hop thuoc boi va cham soc da',
        '2026-01-12 10:15:00',
        TRUE,
        '2026-01-12 10:15:00',
        '2026-01-12 10:15:00'
    )
ON CONFLICT (id) DO UPDATE SET
    appointment_id = EXCLUDED.appointment_id,
    patient_id = EXCLUDED.patient_id,
    doctor_id = EXCLUDED.doctor_id,
    symptoms = EXCLUDED.symptoms,
    diagnosis = EXCLUDED.diagnosis,
    conclusion = EXCLUDED.conclusion,
    visited_at = EXCLUDED.visited_at,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 9) UNITS
INSERT INTO units (
    id, name, active, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000801', 'Vien', TRUE, '2026-01-01 06:00:00', '2026-04-25 06:00:00'),
    ('00000000-0000-0000-0000-000000000802', 'Tuyp', TRUE, '2026-01-01 06:00:00', '2026-04-25 06:00:00'),
    ('00000000-0000-0000-0000-000000000803', 'Chai', TRUE, '2026-01-01 06:00:00', '2026-04-25 06:00:00')
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 10) MEDICINES
INSERT INTO medicines (
    id, code, name, unit_id, ingredient, manufacturer, price, stock_quantity,
    description, image_url, image_public_id, active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000901',
        'SEED-AMLO-5',
        'Amlodipine 5mg',
        '00000000-0000-0000-0000-000000000801',
        'Amlodipine besylate',
        'Seed Pharma',
        4500.00,
        500,
        'Thuoc ho tro kiem soat tang huyet ap',
        NULL,
        NULL,
        TRUE,
        '2026-01-10 09:00:00',
        '2026-04-25 09:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000902',
        'SEED-CETI-10',
        'Cetirizine 10mg',
        '00000000-0000-0000-0000-000000000801',
        'Cetirizine dihydrochloride',
        'Seed Pharma',
        3000.00,
        600,
        'Thuoc khang histamine giam ngua',
        NULL,
        NULL,
        TRUE,
        '2026-01-10 09:00:00',
        '2026-04-25 09:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000903',
        'SEED-KETO-C',
        'Kem ketoconazole',
        '00000000-0000-0000-0000-000000000802',
        'Ketoconazole 2%',
        'SkinCare Lab',
        55000.00,
        120,
        'Kem boi ngoai da khang nam',
        NULL,
        NULL,
        TRUE,
        '2026-01-10 09:00:00',
        '2026-04-25 09:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000904',
        'SEED-SALINE',
        'Nuoc muoi sinh ly',
        '00000000-0000-0000-0000-000000000803',
        'NaCl 0.9%',
        'Hospital Supply',
        12000.00,
        200,
        'Ve sinh va lam sach vung da',
        NULL,
        NULL,
        TRUE,
        '2026-01-10 09:00:00',
        '2026-04-25 09:00:00'
    )
ON CONFLICT (id) DO UPDATE SET
    code = EXCLUDED.code,
    name = EXCLUDED.name,
    unit_id = EXCLUDED.unit_id,
    ingredient = EXCLUDED.ingredient,
    manufacturer = EXCLUDED.manufacturer,
    price = EXCLUDED.price,
    stock_quantity = EXCLUDED.stock_quantity,
    description = EXCLUDED.description,
    image_url = EXCLUDED.image_url,
    image_public_id = EXCLUDED.image_public_id,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 11) PRESCRIPTIONS
INSERT INTO prescriptions (
    id, medical_record_id, patient_id, doctor_id, instructions, issued_at,
    active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000a01',
        '00000000-0000-0000-0000-000000000701',
        '00000000-0000-0000-0000-000000000301',
        '00000000-0000-0000-0000-000000000201',
        'Uong deu theo huong dan, tranh bo bua sang.',
        '2026-03-10 09:00:00',
        TRUE,
        '2026-03-10 09:00:00',
        '2026-03-10 09:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000a02',
        '00000000-0000-0000-0000-000000000702',
        '00000000-0000-0000-0000-000000000303',
        '00000000-0000-0000-0000-000000000202',
        'Boi kem sau khi ve sinh da, tai kham neu khong giam sau 7 ngay.',
        '2026-02-15 09:20:00',
        TRUE,
        '2026-02-15 09:20:00',
        '2026-02-15 09:20:00'
    )
ON CONFLICT (id) DO UPDATE SET
    medical_record_id = EXCLUDED.medical_record_id,
    patient_id = EXCLUDED.patient_id,
    doctor_id = EXCLUDED.doctor_id,
    instructions = EXCLUDED.instructions,
    issued_at = EXCLUDED.issued_at,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 12) PRESCRIPTION ITEMS
INSERT INTO prescription_items (
    id, prescription_id, medicine_id, quantity, dosage, frequency, duration_days,
    note, active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000b01',
        '00000000-0000-0000-0000-000000000a01',
        '00000000-0000-0000-0000-000000000901',
        30,
        '1 vien',
        '2 lan/ngay',
        15,
        'Do huyet ap moi sang',
        TRUE,
        '2026-03-10 09:05:00',
        '2026-03-10 09:05:00'
    ),
    (
        '00000000-0000-0000-0000-000000000b02',
        '00000000-0000-0000-0000-000000000a02',
        '00000000-0000-0000-0000-000000000902',
        14,
        '1 vien',
        '1 lan/ngay buoi toi',
        14,
        'Uong sau an',
        TRUE,
        '2026-02-15 09:21:00',
        '2026-02-15 09:21:00'
    ),
    (
        '00000000-0000-0000-0000-000000000b03',
        '00000000-0000-0000-0000-000000000a02',
        '00000000-0000-0000-0000-000000000903',
        2,
        'Boi mong',
        '2 lan/ngay',
        10,
        'Boi vung da ton thuong',
        TRUE,
        '2026-02-15 09:21:10',
        '2026-02-15 09:21:10'
    )
ON CONFLICT (id) DO UPDATE SET
    prescription_id = EXCLUDED.prescription_id,
    medicine_id = EXCLUDED.medicine_id,
    quantity = EXCLUDED.quantity,
    dosage = EXCLUDED.dosage,
    frequency = EXCLUDED.frequency,
    duration_days = EXCLUDED.duration_days,
    note = EXCLUDED.note,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 13) LAB TEST ORDERS
INSERT INTO lab_test_orders (
    id, medical_record_id, patient_id, doctor_id, test_name, request_note, status,
    requested_at, active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000c01',
        '00000000-0000-0000-0000-000000000701',
        '00000000-0000-0000-0000-000000000301',
        '00000000-0000-0000-0000-000000000201',
        'Dinh luong cholesterol',
        'Nhin an truoc khi lay mau',
        'COMPLETED',
        '2026-03-10 09:10:00',
        TRUE,
        '2026-03-10 09:10:00',
        '2026-03-10 09:10:00'
    ),
    (
        '00000000-0000-0000-0000-000000000c02',
        '00000000-0000-0000-0000-000000000702',
        '00000000-0000-0000-0000-000000000303',
        '00000000-0000-0000-0000-000000000202',
        'Soi tuoi da lieu',
        'Danh gia muc do viem',
        'REQUESTED',
        '2026-02-15 09:25:00',
        TRUE,
        '2026-02-15 09:25:00',
        '2026-02-15 09:25:00'
    )
ON CONFLICT (id) DO UPDATE SET
    medical_record_id = EXCLUDED.medical_record_id,
    patient_id = EXCLUDED.patient_id,
    doctor_id = EXCLUDED.doctor_id,
    test_name = EXCLUDED.test_name,
    request_note = EXCLUDED.request_note,
    status = EXCLUDED.status,
    requested_at = EXCLUDED.requested_at,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 14) LAB TEST RESULTS
INSERT INTO lab_test_results (
    id, lab_test_order_id, result_value, normal_range, attachment_url, reported_at,
    active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000d01',
        '00000000-0000-0000-0000-000000000c01',
        'Cholesterol toan phan: 5.9 mmol/L',
        '3.9 - 5.2 mmol/L',
        NULL,
        '2026-03-11 08:00:00',
        TRUE,
        '2026-03-11 08:00:00',
        '2026-03-11 08:00:00'
    )
ON CONFLICT (id) DO UPDATE SET
    lab_test_order_id = EXCLUDED.lab_test_order_id,
    result_value = EXCLUDED.result_value,
    normal_range = EXCLUDED.normal_range,
    attachment_url = EXCLUDED.attachment_url,
    reported_at = EXCLUDED.reported_at,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 15) DOCTOR REVIEWS
INSERT INTO doctor_reviews (
    id, appointment_id, patient_id, doctor_id, rating, comment, reviewed_at,
    active, created_at, updated_at
) VALUES
    (
        '00000000-0000-0000-0000-000000000e01',
        '00000000-0000-0000-0000-000000000501',
        '00000000-0000-0000-0000-000000000301',
        '00000000-0000-0000-0000-000000000201',
        5,
        'Bac si tu van ky, de hieu va nhiet tinh.',
        '2026-03-11 10:00:00',
        TRUE,
        '2026-03-11 10:00:00',
        '2026-03-11 10:00:00'
    ),
    (
        '00000000-0000-0000-0000-000000000e02',
        '00000000-0000-0000-0000-000000000505',
        '00000000-0000-0000-0000-000000000303',
        '00000000-0000-0000-0000-000000000202',
        4,
        'Phong kham gon gang, bac si giai thich ro rang.',
        '2026-02-16 10:30:00',
        TRUE,
        '2026-02-16 10:30:00',
        '2026-02-16 10:30:00'
    )
ON CONFLICT (id) DO UPDATE SET
    appointment_id = EXCLUDED.appointment_id,
    patient_id = EXCLUDED.patient_id,
    doctor_id = EXCLUDED.doctor_id,
    rating = EXCLUDED.rating,
    comment = EXCLUDED.comment,
    reviewed_at = EXCLUDED.reviewed_at,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

-- 16) NOTIFICATIONS
INSERT INTO notifications (
    id, title, content, email_sent, target_role, target_user_id,
    created_at, updated_at, active
) VALUES
    (
        '00000000-0000-0000-0000-000000000f01',
        'Seed thong bao he thong',
        'Du lieu seed da san sang de test toan bo chuc nang.',
        TRUE,
        'ADMIN',
        '00000000-0000-0000-0000-000000000001',
        '2026-04-25 12:00:00',
        '2026-04-25 12:00:00',
        TRUE
    ),
    (
        '00000000-0000-0000-0000-000000000f02',
        'Nhac lich kham',
        'Ban co lich kham da duoc xac nhan vao ngay 2026-04-20.',
        FALSE,
        'PATIENT',
        '00000000-0000-0000-0000-000000000021',
        '2026-04-19 18:00:00',
        '2026-04-19 18:00:00',
        TRUE
    ),
    (
        '00000000-0000-0000-0000-000000000f03',
        'Thong bao bac si',
        'Ban co benh nhan moi da dat lich thanh cong.',
        FALSE,
        'DOCTOR',
        '00000000-0000-0000-0000-000000000011',
        '2026-04-10 10:05:00',
        '2026-04-10 10:05:00',
        TRUE
    )
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    content = EXCLUDED.content,
    email_sent = EXCLUDED.email_sent,
    target_role = EXCLUDED.target_role,
    target_user_id = EXCLUDED.target_user_id,
    updated_at = EXCLUDED.updated_at,
    active = EXCLUDED.active;

COMMIT;
