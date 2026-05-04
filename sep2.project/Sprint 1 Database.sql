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
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: events; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA events;


ALTER SCHEMA events OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: admin; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.admin (
    admin_id uuid NOT NULL,
    name character varying(100),
    email character varying(150),
    password character varying(250)
);


ALTER TABLE events.admin OWNER TO postgres;

--
-- Name: categories; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.categories (
    category_id uuid NOT NULL,
    name character varying(100),
    description text
);


ALTER TABLE events.categories OWNER TO postgres;

--
-- Name: city; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.city (
    city_id uuid NOT NULL,
    name character varying(100)
);


ALTER TABLE events.city OWNER TO postgres;

--
-- Name: events; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.events (
    event_id uuid NOT NULL,
    admin_id uuid NOT NULL,
    category_id uuid NOT NULL,
    city_id uuid NOT NULL,
    name character varying(150) NOT NULL,
    description text,
    date_time timestamp without time zone NOT NULL,
    venue character varying(255) NOT NULL,
    ticket_price numeric(10,2) NOT NULL,
    total_tickets integer NOT NULL,
    tickets_sold integer DEFAULT 0,
    status character varying(20) DEFAULT 'DRAFT'::character varying,
    banner_image_url character varying(500),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT events_ticket_price_check CHECK ((ticket_price >= (0)::numeric)),
    CONSTRAINT events_total_tickets_check CHECK ((total_tickets >= 1))
);


ALTER TABLE events.events OWNER TO postgres;

--
-- Data for Name: admin; Type: TABLE DATA; Schema: events; Owner: postgres
--

COPY events.admin (admin_id, name, email, password) FROM stdin;
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: events; Owner: postgres
--

COPY events.categories (category_id, name, description) FROM stdin;
\.


--
-- Data for Name: city; Type: TABLE DATA; Schema: events; Owner: postgres
--

COPY events.city (city_id, name) FROM stdin;
\.


--
-- Data for Name: events; Type: TABLE DATA; Schema: events; Owner: postgres
--

COPY events.events (event_id, admin_id, category_id, city_id, name, description, date_time, venue, ticket_price, total_tickets, tickets_sold, status, banner_image_url, created_at, updated_at) FROM stdin;
\.


--
-- Name: admin admin_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.admin
    ADD CONSTRAINT admin_pkey PRIMARY KEY (admin_id);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (category_id);


--
-- Name: city city_pkey; Type: CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (city_id);


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
-- Name: events events_admin_id_fkey; Type: FK CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES events.admin(admin_id);


--
-- Name: events events_category_id_fkey; Type: FK CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_category_id_fkey FOREIGN KEY (category_id) REFERENCES events.categories(category_id);


--
-- Name: events events_city_id_fkey; Type: FK CONSTRAINT; Schema: events; Owner: postgres
--

ALTER TABLE ONLY events.events
    ADD CONSTRAINT events_city_id_fkey FOREIGN KEY (city_id) REFERENCES events.city(city_id);


--
-- PostgreSQL database dump complete
--


