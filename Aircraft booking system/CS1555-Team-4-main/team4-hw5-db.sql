--hw5-db.sql

--Q1
DROP TABLE IF EXISTS AIRLINE CASCADE;
DROP TABLE IF EXISTS FLIGHT CASCADE;
DROP TABLE IF EXISTS PLANE CASCADE;
DROP TABLE IF EXISTS PRICE CASCADE;
DROP TABLE IF EXISTS CUSTOMER CASCADE;
DROP TABLE IF EXISTS RESERVATION CASCADE;
DROP TABLE IF EXISTS RESERVATION_DETAIL CASCADE;
DROP TABLE IF EXISTS OURTIMESTAMP CASCADE;

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
    email               varchar(30)   NOT NULL,
    frequent_miles      varchar(10),
    CONSTRAINT CUSTOMER_PK PRIMARY KEY (cid),
    CONSTRAINT CUSTOMER_FK FOREIGN KEY (frequent_miles) REFERENCES AIRLINE(airline_abbreviation),
    CONSTRAINT CUSTOMER_CCN CHECK (credit_card_num ~ '\d{16}'),
    CONSTRAINT CUSTOMER_EMAIL CHECK (email ~ '^[a-zA-Z0-9.!#$%&''*+\/=?^_`{|}~\-]+@(?:[a-zA-Z0-9\-]+\.)+[a-zA-Z0-9\-]+$' ),
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
