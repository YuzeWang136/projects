--PUT NAME/PITT USER HERE
--Jacob Bonhomme <jcb169>
--Yuze Wang <yuw136>
--Deaven Reed <der97>

--Task 2--

DROP FUNCTION IF EXISTS getCancellationTime(resNumber INTEGER);
CREATE OR REPLACE FUNCTION getCancellationTime(resNumber INTEGER) RETURNS timestamp AS
    $$
    DECLARE cancelTime TIMESTAMP;
    BEGIN
        SELECT (reservation_date) INTO cancelTime FROM reservation  WHERE resNumber = reservation_number;
        RETURN cancelTime - (INTERVAL '12 hours');
    END;
    $$LANGUAGE plpgsql;

SELECT getCancellationTime(1);


-- Task 3 --

DROP FUNCTION IF EXISTS isPlaneFull(flightNumber INT);
CREATE OR REPLACE FUNCTION isPlaneFull(flightNumber INT) RETURNS INT AS
    $$
    declare
    flight_capacity int;
    passengers_on_board int;
    BEGIN
    SELECT plane_capacity INTO flight_capacity FROM Plane WHERE plane_type = (SELECT plane_type FROM Flight WHERE flight_number = flightNumber);
    SELECT COUNT(1) INTO passengers_on_board FROM reservation_detail WHERE flight_number = flightNumber;
    if passengers_on_board = flight_capacity THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
    END;
    $$ LANGUAGE plpgsql;

SELECT isPlaneFull(1);

--Task 4

CREATE OR REPLACE PROCEDURE makeReservation(reservation_number int, flight_num int, departure_date timestamp, "order" int)
LANGUAGE plpgsql
AS
    $$
    DECLARE timeString char(5);
    DECLARE newTimeStamp timestamp;
    BEGIN
        SELECT departure_time  INTO timeString FROM FLIGHT WHERE flight_num = flight_number;
        timeString = CONCAT((substr(timeString,1,2)),':',(substr(timeString,3,2)));
        newTimeStamp = departure_date + (CAST(timeString AS time));
        INSERT INTO reservation_detail(reservation_number, flight_number, flight_date, leg) VALUES (makeReservation.reservation_number,makeReservation.flight_num,newTimeStamp,makeReservation.order);
    END;
    $$;

call makeReservation(3,1,'10-25-2020',2);
