--PUT NAME/PITT USER HERE
--Jacob Bonhomme <jcb169>
--Yuze Wang <yuw136>
--Deaven Reed <der97>

--hw5-triggers.sql

--Q5 planeUpgrade Trigger
--Trigger Function for upgrading Plane
CREATE OR REPLACE PROCEDURE upgradePlaneHelper(flight_num integer, flight_time timestamp) AS
$$
DECLARE
    numberOfSeats    integer;
    upgradeFound     boolean := FALSE;
    currentPlaneType varchar(4);
    airplane_row     RECORD;
    airlinePlanes CURSOR FOR
        SELECT p.plane_type, p.plane_capacity
        FROM flight f
                 JOIN plane p ON f.airline_id = p.owner_id
        WHERE f.flight_number = flight_num
        ORDER BY plane_capacity;
BEGIN
    -- get number of seats for the flight
    numberOfSeats = getNumberOfSeats(flight_num, flight_time);
    raise notice '% number of seats for %', numberOfSeats, flight_num;

    -- get plane type
    SELECT plane_type
    INTO currentPlaneType
    FROM flight
    WHERE flight_number = flight_num;

    -- open cursor
    OPEN airlinePlanes;

    -- check if another plane owned by the airlines can fit current seats
    LOOP
        -- get next plane
        FETCH airlinePlanes INTO airplane_row;
        --exit when done
        EXIT WHEN NOT FOUND;

        -- found a plane can fit (we are starting from the smallest)
        IF numberOfSeats IS NULL OR numberOfSeats + 1 <= airplane_row.plane_capacity THEN
            upgradeFound := TRUE;
            raise notice '% should be upgraded', flight_num;
            -- if the next smallest plane can fit is not the one already scheduled for the flight, then change it
            IF airplane_row.plane_type <> currentPlaneType THEN
                raise notice '% is being upgraded to %', flight_num, airplane_row.plane_type;
                UPDATE flight SET plane_type = airplane_row.plane_type WHERE flight_number = flight_num;
            END IF;
            -- mission accomplished (either we changed the plane OR it is already the next smallest we can fit)
            EXIT;
        END IF;

    END LOOP;

    -- close cursor
    CLOSE airlinePlanes;
    IF NOT upgradeFound THEN
        RAISE EXCEPTION 'There is not any upgrade for the flight % on %',flight_num,flight_time;
    END IF;
END;
$$ language plpgsql;


CREATE OR REPLACE FUNCTION upgradePlane()
    RETURNS TRIGGER AS
$$
BEGIN
    raise notice '% is attempting upgrading', new.flight_number;
    -- downgrade plane in case it is upgradable
    CALL upgradePlaneHelper(new.flight_number, new.flight_date);
    RETURN NEW;
END;
$$ language plpgsql;

DROP TRIGGER IF EXISTS upgradePlane ON RESERVATION_DETAIL;
CREATE TRIGGER upgradePlane
    BEFORE INSERT
    ON RESERVATION_DETAIL
    FOR EACH ROW
EXECUTE PROCEDURE upgradePlane();

--TEST: Check the trigger upgradePlane

INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t001', 'Plane 01', 1, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t002', 'Plane 02', 2, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t003', 'Plane 03', 3, '2020-12-12', 2020, 3);
UPDATE flight
SET plane_type = 't001'
WHERE flight_number = 3;

INSERT INTO RESERVATION_DETAIL (reservation_number, flight_number, flight_date, leg)
VALUES (2, 3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI'), 3);

SELECT getNumberOfSeats(3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI')::timestamp without time zone);
-- should return 3

--Q6 cancelReservation Trigger
CREATE OR REPLACE PROCEDURE downgradePlaneHelper(flight_num integer, flight_time timestamp)
AS
$$
DECLARE
    numberOfSeats    integer;
    currentPlaneType varchar(4);
    airplane_row     RECORD;
    airlinePlanes CURSOR FOR
        SELECT p.plane_type, p.plane_capacity
        FROM flight f
                 JOIN plane p ON f.airline_id = p.owner_id
        WHERE f.flight_number = flight_num
        ORDER BY plane_capacity;
BEGIN
    -- get number of seats for the flight
    numberOfSeats = getNumberOfSeats(flight_num, flight_time);
    raise notice '% number of seats for %', numberOfSeats, flight_num;

    -- get plane type
    SELECT plane_type
    INTO currentPlaneType
    FROM flight
    WHERE flight_number = flight_num;

    -- open cursor
    OPEN airlinePlanes;

    -- check if another plane owned by the airlines can fit current seats
    LOOP
        -- get next plane
        FETCH airlinePlanes INTO airplane_row;
        --exit when done
        EXIT WHEN NOT FOUND;

        -- found a plane can fit (we are starting from the smallest)
        IF numberOfSeats - 1 <= airplane_row.plane_capacity THEN
            raise notice '% should be downgraded', flight_num;
            -- if the smallest plane can fit is not the one already scheduled for the flight, then change it
            IF airplane_row.plane_type <> currentPlaneType THEN
                raise notice '% is beign downgraded to %', flight_num, airplane_row.plane_type;
                UPDATE flight SET plane_type = airplane_row.plane_type WHERE flight_number = flight_num;
            END IF;
            -- mission accomplished (either we changed the plane OR it is already the smallest we can fit)
            EXIT;
        END IF;

    END LOOP;

    -- close cursor
    CLOSE airlinePlanes;

END;
$$ language plpgsql;


CREATE OR REPLACE FUNCTION downgradePlane()
    RETURNS TRIGGER AS
$$
BEGIN
    raise notice '% is attempting downgrading', old.flight_number;
    -- downgrade plane in case it is upgradable
    CALL downgradePlaneHelper(old.flight_number, old.flight_date);
    RETURN OLD;
END;
$$ language plpgsql;

DROP TRIGGER IF EXISTS downgradePlane ON RESERVATION_DETAIL;
CREATE TRIGGER downgradePlane
    AFTER DELETE
    ON RESERVATION_DETAIL
    FOR EACH ROW
EXECUTE PROCEDURE downgradePlane();

CREATE OR REPLACE FUNCTION reservationCancellation()
    RETURNS TRIGGER AS
$$
DECLARE
    currentTime      timestamp;
    cancellationTime timestamp;
    reservation_row  RECORD;
    reservations CURSOR FOR
        SELECT *
        FROM (SELECT DISTINCT reservation_number
              FROM RESERVATION AS R
              WHERE R.ticketed = FALSE) AS NONTICKETED
                 NATURAL JOIN (SELECT DISTINCT reservation_number, flight_date, flight_number
                               FROM RESERVATION_DETAIL AS RD
                               WHERE (RD.flight_date >= currentTime)) AS CANCELLABLEFLIGHT ;
BEGIN
    -- capture our simulated current time
    currentTime := new.c_timestamp;

    -- open cursor
    OPEN reservations;

    LOOP
        -- get the next reservation number that is not ticketed
        FETCH reservations INTO reservation_row;

        -- exit loop when all records are processed
        EXIT WHEN NOT FOUND;

        -- get the cancellation time for the fetched reservation
        cancellationTime = getcancellationtime(reservation_row.reservation_number);
        raise notice 'cancellationTime = % and currentTime = %', cancellationTime,currentTime;
        -- delete customer reservation if departures is less than or equal 12 hrs
        IF (cancellationTime <= currentTime) THEN
            raise notice '% is being cancelled', reservation_row.reservation_number;
            -- delete the reservation
            DELETE FROM RESERVATION WHERE reservation_number = reservation_row.reservation_number;
            raise notice '% is attempting downgrading', reservation_row.flight_number;
            CALL downgradePlaneHelper(reservation_row.flight_number, reservation_row.flight_date);
        END IF;

    END LOOP;

    -- close cursor
    CLOSE reservations;

    RETURN new;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS cancelReservation ON ourtimestamp;
CREATE TRIGGER cancelReservation
    AFTER UPDATE
    ON OURTIMESTAMP
    FOR EACH ROW
EXECUTE PROCEDURE reservationCancellation();

--TEST: Check the trigger cancelReservation
-- Insert the following tuples if you haven't already done it for Q5
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t001', 'Plane 01', 1, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t002', 'Plane 02', 2, '2020-12-12', 2020, 3);
INSERT INTO plane (plane_type, manufacturer, plane_capacity, last_service, year, owner_id)
VALUES ('t003', 'Plane 03', 3, '2020-12-12', 2020, 3);
UPDATE flight
SET plane_type = 't001'
WHERE flight_number = 3;

--INSERT values of RESERVATION_DETAIL Table
BEGIN;
INSERT INTO OURTIMESTAMP (c_timestamp)
VALUES (TO_TIMESTAMP('11-05-2020 02:15', 'MM-DD-YYYY HH24:MI'));
COMMIT;
SELECT getNumberOfSeats(3, TO_TIMESTAMP('11-05-2020 14:15', 'MM-DD-YYYY HH24:MI')::timestamp without time zone);
-- should return 3

BEGIN;
UPDATE OURTIMESTAMP
SET c_timestamp = TO_TIMESTAMP('11-03-2020 20:25', 'MM-DD-YYYY HH24:MI')
WHERE TRUE;
COMMIT;

DROP PROCEDURE IF EXISTS adjustTicketHelper1(changeLow int,departure char(3), arrival char(3), airline_id int) CASCADE;
CREATE OR REPLACE PROCEDURE adjustTicketHelper1(changeLow int,departure char(3), arrival char(3), airline_id int )
AS
$$
DECLARE
    --currentPrice    integer;
    leg     RECORD;

    low_reservations CURSOR FOR
         SELECT reservation_number,cid,cost from (price natural join flight natural join reservation_detail natural join reservation) big where reservation_number not in
        (SELECT reservation_number
        FROM ((price g join flight h on (g.departure_city = h.departure_city and g.arrival_city = h.arrival_city))
        b join (SELECT * from reservation_detail natural join (SELECT reservation_number from reservation_detail group by reservation_number having count(*)=1) as x) r on b.flight_number = r.flight_number) tbl where tbl.arrival_time > tbl.departure_time)
        and big.departure_city = departure and big.arrival_city = arrival and big.airline_id = adjustTicketHelper1.airline_id and big.ticketed = false;


BEGIN

  -- open cursor
    OPEN low_reservations;

    -- check if another plane owned by the airlines can fit current seats
    LOOP
        -- get next plane
        FETCH low_reservations INTO leg;
        raise notice '1 %',leg.cost;
        --exit when done
        EXIT WHEN NOT FOUND;

        -- found a plane can fit (we are starting from the smallest)

        raise notice 'price % is being changed to % for customer %',leg.cost, leg.cost+changeLow, leg.cid ;
         UPDATE reservation r SET cost = leg.cost+changeLow WHERE r.reservation_number = leg.reservation_number and r.cid = leg.cid;

    END LOOP;

    -- close cursor
    CLOSE low_reservations;

END;
$$ language plpgsql;

DROP PROCEDURE IF EXISTS adjustTicketHelper2(changeHigh int, departure char(3), arrival char(3), airline_id int) CASCADE;
CREATE OR REPLACE PROCEDURE adjustTicketHelper2(changeHigh int, departure char(3), arrival char(3), airline_id int )
AS
$$
DECLARE
    --currentPrice    integer;
    leg     RECORD;

    high_reservations CURSOR FOR
         SELECT reservation_number,cid,cost
        FROM (((price natural join flight)
        b join (SELECT * from reservation_detail natural join (SELECT reservation_number from reservation_detail group by reservation_number having count(*)=1) as x) r on b.flight_number = r.flight_number) t1
            natural join reservation t2) tbl where (tbl.arrival_time > tbl.departure_time and tbl.departure_city = departure and
            tbl.arrival_city = arrival and tbl.airline_id = adjustTicketHelper2.airline_id and tbl.ticketed = false);




BEGIN

  -- open cursor

    OPEN high_reservations;

    -- check if another plane owned by the airlines can fit current seats
    LOOP
        -- get next plane
        FETCH high_reservations INTO leg;
         raise notice '2 %',leg.cost;
        --exit when done
      EXIT WHEN NOT FOUND;

        -- found a plane can fit (we are starting from the smallest)

        raise notice 'price % is being changed to % for customer %',leg.cost, leg.cost+changeHigh, leg.cid ;
         UPDATE reservation r SET cost = leg.cost+changeHigh WHERE r.reservation_number = leg.reservation_number and r.cid = leg.cid;

    END LOOP;

    -- close cursor
    CLOSE high_reservations;

END;
$$ language plpgsql;

CREATE OR REPLACE FUNCTION adjustTicket()
    RETURNS TRIGGER AS
$$
BEGIN
    raise notice 'airline % for % to % is attempting ticket adjustment high: % low: %', old.airline_id ,old.departure_city,old.arrival_city,(new.high_price-old.high_price),(new.low_price-old.low_price);
    -- downgrade plane in case it is upgradable

    CALL adjustTicketHelper1((new.low_price-old.low_price), old.departure_city, old.arrival_city,old.airline_id);
   CALL adjustTicketHelper2((new.high_price-old.high_price), old.departure_city, old.arrival_city,old.airline_id);
    RETURN OLD;
END;
$$ language plpgsql;


DROP TRIGGER IF EXISTS adjustTicket ON price;
CREATE TRIGGER adjustTicket
    AFTER UPDATE
    ON price
    FOR EACH ROW
EXECUTE FunctiON adjustTicket();

UPDATE price set high_price = 2000 WHERE price.departure_city = 'LAX' and price.arrival_city = 'SEA' and airline_id = 3;
