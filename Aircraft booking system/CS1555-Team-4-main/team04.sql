--PUT NAME/PITT USER HERE
--Jacob Bonhomme <jcb169>
--Yuze Wang <yuw136>
--Deaven Reed <der97>

--Q1
DROP TABLE IF EXISTS AIRLINE CASCADE;
DROP TABLE IF EXISTS FLIGHT CASCADE;
DROP TABLE IF EXISTS PLANE CASCADE;
DROP TABLE IF EXISTS PRICE CASCADE;
DROP TABLE IF EXISTS CUSTOMER CASCADE;
DROP TABLE IF EXISTS RESERVATION CASCADE;
DROP TABLE IF EXISTS RESERVATION_DETAIL CASCADE;
DROP TABLE IF EXISTS OURTIMESTAMP CASCADE;
DROP DOMAIN IF EXISTS EMAIL_DOMAIN CASCADE;

--Note: This is a simplified email domain and is not intended to exhaustively check for all requirements of an email
CREATE DOMAIN EMAIL_DOMAIN AS varchar(30)
    CHECK ( value ~ '^[a-zA-Z0-9.!#$%&''*+\/=?^_`{|}~\-]+@(?:[a-zA-Z0-9\-]+\.)+[a-zA-Z0-9\-]+$' );

CREATE TABLE AIRLINE (
  airline_id            integer,
  airline_name          varchar(50)     NOT NULL,
  airline_abbreviation  varchar(10)     NOT NULL,
  year_founded          integer         NOT NULL,
  CONSTRAINT AIRLINE_PK PRIMARY KEY (airline_id),
  CONSTRAINT AIRLINE_UQ1 UNIQUE (airline_name),
  CONSTRAINT AIRLINE_UQ2 UNIQUE (airline_abbreviation)
);

CREATE TABLE PLANE (
    plane_type      char(4),
    manufacturer    varchar(10)     NOT NULL,
    plane_capacity  integer         NOT NULL,
    last_service    date            NOT NULL,
    year            integer         NOT NULL,
    owner_id        integer         NOT NULL,
    CONSTRAINT PLANE_PK PRIMARY KEY (plane_type,owner_id),
    CONSTRAINT PLANE_FK FOREIGN KEY (owner_id) REFERENCES AIRLINE(airline_id)
);

CREATE TABLE FLIGHT (
    flight_number   integer,
    airline_id      integer     NOT NULL,
    plane_type      char(4)     NOT NULL,
    departure_city  char(3)     NOT NULL,
    arrival_city    char(3)     NOT NULL,
    departure_time  varchar(4)  NOT NULL,
    arrival_time    varchar(4)  NOT NULL,
    weekly_schedule varchar(7)  NOT NULL,
    CONSTRAINT FLIGHT_PK PRIMARY KEY (flight_number),
    CONSTRAINT FLIGHT_FK1 FOREIGN KEY (plane_type,airline_id) REFERENCES PLANE(plane_type,owner_id),
    CONSTRAINT FLIGHT_FK2 FOREIGN KEY (airline_id) REFERENCES AIRLINE(airline_id),
    CONSTRAINT FLIGHT_UQ UNIQUE (departure_city, arrival_city)
);

CREATE TABLE PRICE (
    departure_city  char(3),
    arrival_city    char(3),
    airline_id      integer,
    high_price      integer     NOT NULL,
    low_price       integer     NOT NULL,
    CONSTRAINT PRICE_PK PRIMARY KEY (departure_city, arrival_city),
    CONSTRAINT PRICE_FK FOREIGN KEY (airline_id) REFERENCES AIRLINE(airline_id),
    CONSTRAINT PRICE_CHECK_HIGH CHECK (high_price >= 0),
    CONSTRAINT PRICE_CHECK_LOW CHECK (low_price >= 0)
);

--Assuming salutation can be NULL as many people don't use salutations on online forms
--Assuming last_name can be NULL as not everyone has a last name, like Cher
--Assuming phone is optional (can be NULL) but email is required
--Assuming that duplicate first_name and last_name pairs are possible since cid will be unique
--Assuming that email addresses should be unique in the table since multiple customers shouldn't sign up with
---the same email
CREATE TABLE CUSTOMER (
    cid                 integer,
    salutation          varchar(3),
    first_name          varchar(30)     NOT NULL,
    last_name           varchar(30),
    credit_card_num     varchar(16)     NOT NULL,
    credit_card_expire  date            NOT NULL,
    street              varchar(30)     NOT NULL,
    city                varchar(30)     NOT NULL,
    state               varchar(2)      NOT NULL,
    phone               varchar(10),
    email               EMAIL_DOMAIN    NOT NULL,
    frequent_miles      varchar(10),
    CONSTRAINT CUSTOMER_PK PRIMARY KEY (cid),
    CONSTRAINT CUSTOMER_FK FOREIGN KEY (frequent_miles) REFERENCES AIRLINE(airline_abbreviation),
    CONSTRAINT CUSTOMER_CCN CHECK (credit_card_num ~ '\d{16}'),
    CONSTRAINT CUSTOMER_UQ1 UNIQUE (credit_card_num),
    CONSTRAINT CUSTOMER_UQ2 UNIQUE (email)
);

--Assuming that a customer can make multiple reservations, i.e., cid and credit_card_num are not unique here
---since multiple reservations will have unique reservation_numbers
CREATE TABLE RESERVATION (
  reservation_number    integer,
  cid                   integer     NOT NULL,
  cost                  decimal     NOT NULL,
  credit_card_num       varchar(16) NOT NULL,
  reservation_date      timestamp   NOT NULL,
  ticketed              boolean     NOT NULL    DEFAULT FALSE,
  CONSTRAINT RESERVATION_PK PRIMARY KEY (reservation_number),
  CONSTRAINT RESERVATION_FK1 FOREIGN KEY (cid) REFERENCES CUSTOMER(cid),
  CONSTRAINT RESERVATION_FK2 FOREIGN KEY (credit_card_num) REFERENCES CUSTOMER(credit_card_num),
  CONSTRAINT RESERVATION_COST CHECK (cost >= 0)
);

CREATE TABLE RESERVATION_DETAIL (
  reservation_number    integer,
  flight_number         integer     NOT NULL,
  flight_date           timestamp   NOT NULL,
  leg                   integer,
  CONSTRAINT RESERVATION_DETAIL_PK PRIMARY KEY (reservation_number, leg),
  CONSTRAINT RESERVATION_DETAIL_FK1 FOREIGN KEY (reservation_number) REFERENCES RESERVATION(reservation_number) ON DELETE CASCADE,
  CONSTRAINT RESERVATION_DETAIL_FK2 FOREIGN KEY (flight_number) REFERENCES FLIGHT(flight_number),
  CONSTRAINT RESERVATION_DETAIL_CHECK_LEG CHECK (leg > 0)
);

-- The c_timestamp is initialized once using INSERT and updated subsequently
CREATE TABLE OURTIMESTAMP (
    c_timestamp     timestamp,
    CONSTRAINT OURTIMESTAMP_PK PRIMARY KEY (c_timestamp)
);




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


CREATE OR REPLACE PROCEDURE frequentFlyerHelper(cid integer, airline_ab varchar(10))
AS
$$
DECLARE
    check1 integer;
    check2 integer;
    a1 varchar(10);
    a2 varchar(10);
    flyer     RECORD;
    flyers1 CURSOR FOR
        SELECT airline_abbreviation, count(*) c from
        (airline natural join flight natural join reservation natural join customer natural join reservation_detail natural join price) tbl
        where tbl.cid = frequentFlyerHelper.cid group by airline_abbreviation order by count(*) desc;
    flyers2 CURSOR FOR
        SELECT airline_id,cost,reservation_number,airline_abbreviation,frequent_miles,leg from
        (airline natural join flight natural join reservation natural join customer natural join reservation_detail natural join price) tbl
        where tbl.cid = frequentFlyerHelper.cid;




BEGIN


    OPEN flyers1;
    FETCH flyers1 INTO flyer;
    IF NOT FOUND then
        RETURN;
    end if;
    check1 = flyer.c;
    a1 = flyer.airline_abbreviation;

    FETCH flyers1 INTO flyer;
    IF NOT FOUND THEN
        raise notice 'no contest, %', a1;
        UPDATE customer set frequent_miles = a1 where customer.cid = frequentFlyerHelper.cid;
         CLOSE flyers1;
        RETURN;

    end if;
    check2 = flyer.c;
    a2 = flyer.airline_abbreviation;
    If check1>check2 then
        raise notice 'some contest, %', a1;
        UPDATE customer set frequent_miles = a1 where customer.cid = frequentFlyerHelper.cid;
         CLOSE flyers1;
        RETURN;
    end if;

    CLOSE flyers1;

    check1 = 0;
    check2 = 0;

    OPEN flyers2;
    LOOP
        FETCH flyers2 INTO flyer;
        EXIT WHEN NOT FOUND;
        IF flyer.airline_abbreviation = a1 then
            check1 = check1 + flyer.cost;
        end if;
        IF flyer.airline_abbreviation = a2 then
            check2 = check2 + flyer.cost;
        end if;

    END LOOP;

    if check1>check2 then
        raise notice 'true contest, % won with %', a1, check1;
        UPDATE customer set frequent_miles = a1 where customer.cid = frequentFlyerHelper.cid;

    else if check2>check1 then
        raise notice 'true contest, % won with %', a2, check2;
        UPDATE customer set frequent_miles = a2 where customer.cid = frequentFlyerHelper.cid;
    else
        if a1 = frequentFlyerHelper.airline_ab or a2 = frequentFlyerHelper.airline_ab then
            raise notice 'too much contest, % won by default with %', airline_ab, check1;
            UPDATE customer set frequent_miles = frequentFlyerHelper.airline_ab where customer.cid = frequentFlyerHelper.cid;
        else
            raise notice 'toss up, % won with %', a1, check1;
            UPDATE customer set frequent_miles = a1 where customer.cid = frequentFlyerHelper.cid;
        end if;
    end if;
    end if;


    -- close cursor
    CLOSE flyers2;

END;
$$ language plpgsql;


CREATE OR REPLACE FUNCTION frequentFlyer()
    RETURNS TRIGGER AS
$$
DECLARE
    r RECORD;
    cur cursor for SELECT airline_abbreviation,cid FROM
    ((SELECT * from reservation where reservation_number = new.reservation_number) t natural join reservation_detail natural join flight natural join airline) tbl
    WHERE leg = 1;
BEGIN
    raise notice 'running frequent flyer for reservation %', new.reservation_number;
    -- downgrade plane in case it is upgradable

    open cur;
    fetch cur into r;
    if not found then
        return new;
    end if;
    close cur;
    CALL frequentFlyerHelper(r.cid,r.airline_abbreviation);
    RETURN NEW;
END;
$$ language plpgsql;

DROP TRIGGER IF EXISTS frequentFlyer ON reservation_detail;
CREATE TRIGGER frequentFlyer
    AFTER INSERT
    ON reservation_detail
    FOR EACH ROW
EXECUTE FUNCTION frequentFlyer();
