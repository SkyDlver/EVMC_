INSERT INTO employees
(id, first_name, last_name, sur_name, gender, department, hired_at, on_holiday, holiday_start_date, holiday_end_date, employee_role)
VALUES
    (gen_random_uuid(), 'Aziz', 'Karimov', 'Bek', 'MALE', 'IT', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Dilshod', 'Rasulov', 'Olim', 'MALE', 'HR', CURRENT_DATE, false, NULL, NULL, 'RAHBAR'),
    (gen_random_uuid(), 'Gulnora', 'Tursunova', 'Rustamovna', 'FEMALE', 'Finance', CURRENT_DATE, true, CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE + INTERVAL '5 days', 'XODIM'),
    (gen_random_uuid(), 'Javlon', 'Nazarov', 'Aliyevich', 'MALE', 'IT', CURRENT_DATE, false, NULL, NULL, 'KUZATUVCHI'),
    (gen_random_uuid(), 'Malika', 'Abduqodirova', 'Shamolovna', 'FEMALE', 'Marketing', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Sherzod', 'Raxmatov', 'Bek', 'MALE', 'Finance', CURRENT_DATE, true, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '6 days', 'RAHBAR'),
    (gen_random_uuid(), 'Zarnigor', 'Yusupova', 'Orifovna', 'FEMALE', 'HR', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Bahodir', 'Sodiqov', 'Karimovich', 'MALE', 'IT', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Nodira', 'Ismatova', 'Azimovna', 'FEMALE', 'Marketing', CURRENT_DATE, false, NULL, NULL, 'KUZATUVCHI'),
    (gen_random_uuid(), 'Rustam', 'Tillaev', 'Sardorovich', 'MALE', 'Finance', CURRENT_DATE, true, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '2 days', 'RAHBAR'),
    (gen_random_uuid(), 'Dilorom', 'Yoqubova', 'Shavkatovna', 'FEMALE', 'HR', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Islom', 'Xudoyberdiyev', 'Javohirovich', 'MALE', 'IT', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Madina', 'Ortiqova', 'Sirojovna', 'FEMALE', 'Marketing', CURRENT_DATE, false, NULL, NULL, 'KUZATUVCHI'),
    (gen_random_uuid(), 'Oybek', 'Soliyev', 'Shahbozovich', 'MALE', 'Finance', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Shahzoda', 'Raximova', 'Olimovna', 'FEMALE', 'HR', CURRENT_DATE, true, CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE + INTERVAL '4 days', 'RAHBAR'),
    (gen_random_uuid(), 'Anvar', 'Islomov', 'Sherzodovich', 'MALE', 'IT', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Dilafruz', 'Sattorova', 'Otaboyovna', 'FEMALE', 'Finance', CURRENT_DATE, false, NULL, NULL, 'KUZATUVCHI'),
    (gen_random_uuid(), 'Sherali', 'Ganiev', 'Rustamovich', 'MALE', 'IT', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Nafisa', 'Abdullayeva', 'Kamolovna', 'FEMALE', 'Marketing', CURRENT_DATE, false, NULL, NULL, 'XODIM'),
    (gen_random_uuid(), 'Ulugbek', 'Qodirov', 'Davronovich', 'MALE', 'Finance', CURRENT_DATE, true, CURRENT_DATE - INTERVAL '7 days', CURRENT_DATE + INTERVAL '3 days', 'RAHBAR');
