--hw5-insert.sql


--INSERT values of AIRLINE Table

INSERT INTO AIRLINE (airline_id, airline_name, airline_abbreviation, year_founded)
VALUES (1, 'Alaska Airlines', 'ALASKA', 1932);
INSERT INTO AIRLINE (airline_id, airline_name, airline_abbreviation, year_founded)
VALUES (2, 'Allegiant Air', 'ALLEGIANT', 1997);
INSERT INTO AIRLINE (airline_id, airline_name, airline_abbreviation, year_founded)
VALUES (3, 'American Airlines', 'AMERICAN', 1926);
INSERT INTO AIRLINE (airline_id, airline_name, airline_abbreviation, year_founded)
VALUES (4, 'Delta Air Lines', 'DELTA', 1924);
INSERT INTO AIRLINE (airline_id, airline_name, airline_abbreviation, year_founded)
VALUES (5, 'United Airlines', 'UNITED', 1926);


--INSERT values of PLANE Table

INSERT INTO PLANE (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('A320', 'Airbus', 186, TO_DATE('11-03-2020', 'MM-DD-YYYY'), 1988, 1);
INSERT INTO PLANE (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('E175', 'Embraer', 76, TO_DATE('10-22-2020', 'MM-DD-YYYY'), 2004, 2);
INSERT INTO PLANE (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('B737', 'Boeing', 125, TO_DATE('09-09-2020', 'MM-DD-YYYY'), 2006, 3);
INSERT INTO PLANE (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('E145', 'Embraer', 50, TO_DATE('06-15-2020', 'MM-DD-YYYY'), 2018, 4);
INSERT INTO PLANE (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('B777', 'Boeing', 368, TO_DATE('09-16-2020', 'MM-DD-YYYY'), 1995, 5);


--INSERT values of FLIGHT Table

INSERT INTO FLIGHT (flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time,
                    weekly_schedule)
VALUES (1, 1, 'A320', 'PIT', 'JFK', '1355', '1730', 'SMTWTFS');
INSERT INTO FLIGHT (flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time,
                    weekly_schedule)
VALUES (2, 2, 'E175', 'JFK', 'LAX', '0825', '1845', '-MTWTFS');
INSERT INTO FLIGHT (flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time,
                    weekly_schedule)
VALUES (3, 3, 'B737', 'LAX', 'SEA', '1415', '1725', 'SMT-TFS');
INSERT INTO FLIGHT (flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time,
                    weekly_schedule)
VALUES (4, 4, 'E145', 'SEA', 'IAH', '1005', '2035', 'SMTW--S');
INSERT INTO FLIGHT (flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time,
                    weekly_schedule)
VALUES (5, 5, 'B777', 'IAH', 'PIT', '0630', '1620', '-MTW--S');


--INSERT values of PRICE Table

INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('PIT', 'JFK', 1, 300, 165);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('JFK', 'LAX', 2, 480, 345);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('LAX', 'SEA', 3, 380, 270);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('SEA', 'IAH', 4, 515, 365);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('IAH', 'PIT', 5, 435, 255);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('JFK', 'PIT', 1, 440, 315);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('LAX', 'PIT', 2, 605, 420);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('SEA', 'LAX', 3, 245, 150);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('IAH', 'SEA', 4, 395, 260);
INSERT INTO PRICE (departure_city, arrival_city, airline_id, high_price, low_price)
VALUES ('PIT', 'IAH', 5, 505, 350);


--INSERT values of CUSTOMER Table

INSERT INTO CUSTOMER (cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state,
                      phone, email, frequent_miles)
VALUES (1, 'Mr', 'Jon', 'Smith', '6859941825383380', TO_DATE('04-13-2022', 'MM-DD-YYYY'), 'Bigelow Boulevard',
        'Pittsburgh', 'PA', '412222222', 'jsmith@gmail.com', 'ALASKA');
INSERT INTO CUSTOMER (cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state,
                      phone, email, frequent_miles)
VALUES (2, 'Mrs', 'Latanya', 'Wood', '7212080255339668', TO_DATE('07-05-2023', 'MM-DD-YYYY'), 'Houston Street',
        'New York', 'NY', '7187181717', 'lw@aol.com', 'ALLEGIANT');
INSERT INTO CUSTOMER (cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state,
                      phone, email, frequent_miles)
VALUES (3, 'Ms', 'Gabriella', 'Rojas', '4120892825130802', TO_DATE('09-22-2024', 'MM-DD-YYYY'), 'Melrose Avenue',
        'Los Angeles', 'CA', '2133234567', 'gar@yahoo.com', 'AMERICAN');
INSERT INTO CUSTOMER (cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state,
                      phone, email, frequent_miles)
VALUES (4, 'Mr', 'Abbas', 'Malouf', '4259758505178751', TO_DATE('10-17-2021', 'MM-DD-YYYY'), 'Pine Street', 'Seattle',
        'WA', '2066170345', 'malouf.a@outlook.com', 'DELTA');
INSERT INTO CUSTOMER (cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state,
                      phone, email, frequent_miles)
VALUES (5, 'Ms', 'Amy', 'Liu', '2538244543760285', TO_DATE('03-24-2022', 'MM-DD-YYYY'), 'Amber Drive', 'Houston', 'TX',
        '2818880102', 'amyliu45@icloud.com', 'UNITED');


--INSERT values of RESERVATION Table

INSERT INTO RESERVATION (reservation_number, cid, cost, credit_card_num, reservation_date, ticketed)
VALUES (1, 1, 1160, '6859941825383380', TO_TIMESTAMP('11-02-2020 10:55', 'MM-DD-YYYY HH24:MI'), TRUE);
INSERT INTO RESERVATION (reservation_number, cid, cost, credit_card_num, reservation_date, ticketed)
VALUES (2, 2, 620, '7212080255339668', TO_TIMESTAMP('11-22-2020 14:25', 'MM-DD-YYYY HH24:MI'), TRUE);
INSERT INTO RESERVATION (reservation_number, cid, cost, credit_card_num, reservation_date, ticketed)
VALUES (3, 3, 380, '4120892825130802', TO_TIMESTAMP('11-05-2020 17:20', 'MM-DD-YYYY HH24:MI'), FALSE);
INSERT INTO RESERVATION (reservation_number, cid, cost, credit_card_num, reservation_date, ticketed)
VALUES (4, 4, 255, '4259758505178751', TO_TIMESTAMP('12-01-2020 06:05', 'MM-DD-YYYY HH24:MI'), TRUE);
INSERT INTO RESERVATION (reservation_number, cid, cost, credit_card_num, reservation_date, ticketed)
VALUES (5, 5, 615, '2538244543760285', TO_TIMESTAMP('10-28-2020 22:45', 'MM-DD-YYYY HH24:MI'), FALSE);


--INSERT values of RESERVATION_DETAIL Table

INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (1, 1, TO_TIMESTAMP('11-02-2020 13:55', 'MM-DD-YYYY HH24:MI'), 1);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (1, 2, TO_TIMESTAMP('11-04-2020 08:25', 'MM-DD-YYYY HH24:MI'), 2);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (1, 3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI'), 3);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (2, 4, TO_TIMESTAMP('12-14-2020 10:05', 'MM-DD-YYYY HH24:MI'), 1);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (2, 5, TO_TIMESTAMP('12-15-2020 06:30', 'MM-DD-YYYY HH24:MI'), 2);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (3, 3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI'), 1);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (4, 5, TO_TIMESTAMP('12-15-2020 06:30', 'MM-DD-YYYY HH24:MI'), 1);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (5, 2, TO_TIMESTAMP('11-04-2020 08:25', 'MM-DD-YYYY HH24:MI'), 1);
INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (5, 3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI'), 2);


--INSERT values of OURTIMESTAMP Table
BEGIN;
INSERT INTO OURTIMESTAMP (c_timestamp)
VALUES (TO_TIMESTAMP('11-05-2020 02:15', 'MM-DD-YYYY HH24:MI'));
COMMIT;

BEGIN;
UPDATE OURTIMESTAMP
SET c_timestamp = TO_TIMESTAMP('11-03-2020 20:25', 'MM-DD-YYYY HH24:MI')
WHERE c_timestamp =  TO_TIMESTAMP('11-05-2020 02:15', 'MM-DD-YYYY HH24:MI');
COMMIT;

BEGIN;
UPDATE OURTIMESTAMP
SET c_timestamp = TO_TIMESTAMP('12-13-2020 22:05', 'MM-DD-YYYY HH24:MI')
WHERE c_timestamp =  TO_TIMESTAMP('11-03-2020 20:25', 'MM-DD-YYYY HH24:MI');
COMMIT;
