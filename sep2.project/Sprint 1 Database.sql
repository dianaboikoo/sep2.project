--
-- PostgreSQL database dump
--
-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: events; Type: SCHEMA; Schema: -; Owner: postgres
-- NOTE: Comment out the line below if the schema already exists
--

CREATE SCHEMA events;

ALTER SCHEMA events OWNER TO postgres;

SET default_tablespace = '';
SET default_table_access_method = heap;

--
-- Name: admin; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.admin (
    email character varying(100) NOT NULL,
    password character varying(250)
);

ALTER TABLE events.admin OWNER TO postgres;

--
-- Name: category; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.category (
    name character varying(100) NOT NULL,
    description text
);

ALTER TABLE events.category OWNER TO postgres;

--
-- Name: city; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.city (
    zip_code numeric(4,0) NOT NULL,
    name character varying(100)
);

ALTER TABLE events.city OWNER TO postgres;

--
-- Name: events; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.events (
    event_id integer NOT NULL,
    admin_email character varying(100) NOT NULL,
    category_name character varying(100) NOT NULL,
    zip_code numeric(4,0) NOT NULL,
    name character varying(150) NOT NULL,
    description text,
    date_time timestamp without time zone NOT NULL,
    venue character varying(255) NOT NULL,
    address character varying(255) NOT NULL,
    ticket_price numeric(10,2) NOT NULL,
    total_tickets integer NOT NULL,
    tickets_sold integer DEFAULT 0,
    status character varying(20) DEFAULT 'DRAFT'::character varying,
    imageurl character varying(500),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT events_ticket_price_check CHECK ((ticket_price >= (0)::numeric)),
    CONSTRAINT events_total_tickets_check CHECK ((total_tickets >= 1))
);

ALTER TABLE events.events OWNER TO postgres;

--
-- Name: events_event_id_seq; Type: SEQUENCE; Schema: events; Owner: postgres
--

CREATE SEQUENCE events.events_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE events.events_event_id_seq OWNER TO postgres;

ALTER SEQUENCE events.events_event_id_seq OWNED BY events.events.event_id;

ALTER TABLE ONLY events.events ALTER COLUMN event_id SET DEFAULT nextval('events.events_event_id_seq'::regclass);

--
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (email);

--
-- Name: category category_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (name);

--
-- Name: city city_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (zip_code);

--
-- Name: events events_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_pkey PRIMARY KEY (event_id);

--
-- Name: idx_unique_event; Type: INDEX; Schema: events; Owner: postgres
--

CREATE UNIQUE INDEX idx_unique_event ON events.events USING btree (name, date_time, venue);

--
-- Name: events events_admin_email_fkey; Type: FK CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_admin_email_fkey FOREIGN KEY (admin_email) REFERENCES events.admin(email);

--
-- Name: events events_category_name_fkey; Type: FK CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_category_name_fkey FOREIGN KEY (category_name) REFERENCES events.category(name);

--
-- Name: events events_zip_code_fkey; Type: FK CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_zip_code_fkey FOREIGN KEY (zip_code) REFERENCES events.city(zip_code);

--
-- Seed data: categories
--

INSERT INTO events.category (name, description) VALUES
('Concert', 'Live music events'),
('Sports', 'Sporting events and competitions'),
('Theatre', 'Plays, musicals and performing arts'),
('Comedy', 'Stand-up and comedy shows'),
('Food & Drink', 'Food festivals and dining experiences'),
('Uncategorized', 'Default category for reassigned events');

--
-- PostgreSQL database dump complete
--
