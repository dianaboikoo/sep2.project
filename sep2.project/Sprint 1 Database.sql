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

--CREATE SCHEMA events;

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
-- Name: users; Type: TABLE; Schema: events; Owner: postgres
--

CREATE TABLE events.users (
    user_id SERIAL PRIMARY KEY,
    email character varying(255) NOT NULL UNIQUE,
    password character varying(255) NOT NULL,
    name character varying(100) NOT NULL
);

ALTER TABLE events.users OWNER TO postgres;

--
-- Name: tickets; Type: TABLE; Schema: events; Owner: postgres
-- Note: ticket_id is a UUID string (generated in Java with UUID.randomUUID().toString())
-- Note: quantity column allows purchasing multiple tickets in one transaction
--

CREATE TABLE events.tickets (
    ticket_id VARCHAR(36) PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES events.events(event_id),
    user_email VARCHAR(255) NOT NULL REFERENCES events.users(email),
    purchase_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    quantity INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

ALTER TABLE events.tickets OWNER TO postgres;

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
-- Seed data: admin account
--

INSERT INTO events.admin (email, password)
VALUES ('admin@events.com', 'admin123')
ON CONFLICT (email) DO NOTHING;

--
-- Seed data: test user account
--

INSERT INTO events.users (email, password, name)
VALUES ('user@events.com', 'user123', 'Test User')
ON CONFLICT (email) DO NOTHING;

--
-- Seed data: Danish cities and ZIP codes
--

INSERT INTO events.city (zip_code, name) VALUES
-- Copenhagen and surroundings
(1000, 'København K'),
(1050, 'København K'),
(1100, 'København K'),
(1150, 'København K'),
(1200, 'København K'),
(1250, 'København K'),
(1300, 'København K'),
(1350, 'København K'),
(1400, 'København K'),
(1410, 'København K'),
(1420, 'København K'),
(1430, 'København K'),
(1440, 'København K'),
(1450, 'København K'),
(1460, 'København K'),
(1470, 'København K'),
(1500, 'København V'),
(1550, 'København V'),
(1600, 'København V'),
(1620, 'København V'),
(1650, 'København V'),
(1700, 'København V'),
(1720, 'København V'),
(1730, 'København V'),
(1750, 'København V'),
(1760, 'København V'),
(1770, 'København V'),
(1780, 'København V'),
(1800, 'Frederiksberg C'),
(1820, 'Frederiksberg C'),
(1850, 'Frederiksberg C'),
(1900, 'Frederiksberg C'),
(1950, 'Frederiksberg C'),
(1970, 'Frederiksberg C'),
(2000, 'Frederiksberg'),
(2100, 'København Ø'),
(2150, 'Nordhavn'),
(2200, 'København N'),
(2300, 'København S'),
(2400, 'København NV'),
(2450, 'København SV'),
(2500, 'Valby'),
(2600, 'Glostrup'),
(2605, 'Brøndby'),
(2610, 'Rødovre'),
(2620, 'Albertslund'),
(2625, 'Vallensbæk'),
(2630, 'Taastrup'),
(2635, 'Ishøj'),
(2640, 'Hedehusene'),
(2650, 'Hvidovre'),
(2660, 'Brøndby Strand'),
(2665, 'Vallensbæk Strand'),
(2670, 'Greve'),
(2680, 'Solrød Strand'),
(2690, 'Karlslunde'),
(2700, 'Brønshøj'),
(2720, 'Vanløse'),
(2730, 'Herlev'),
(2740, 'Skovlunde'),
(2750, 'Ballerup'),
(2760, 'Måløv'),
(2765, 'Smørum'),
(2770, 'Kastrup'),
(2791, 'Dragør'),
-- North Zealand
(2800, 'Kongens Lyngby'),
(2820, 'Gentofte'),
(2830, 'Virum'),
(2840, 'Holte'),
(2850, 'Nærum'),
(2860, 'Søborg'),
(2870, 'Dyssegård'),
(2880, 'Bagsværd'),
(2900, 'Hellerup'),
(2920, 'Charlottenlund'),
(2930, 'Klampenborg'),
(2942, 'Skodsborg'),
(2950, 'Vedbæk'),
(2960, 'Rungsted Kyst'),
(2970, 'Hørsholm'),
(2980, 'Kokkedal'),
(2990, 'Nivå'),
(3000, 'Helsingør'),
(3050, 'Humlebæk'),
(3060, 'Espergærde'),
(3070, 'Snekkersten'),
(3100, 'Hornbæk'),
(3120, 'Dronningmølle'),
(3140, 'Ålsgårde'),
(3150, 'Hellebæk'),
(3200, 'Helsinge'),
(3210, 'Vejby'),
(3220, 'Tisvildeleje'),
(3230, 'Græsted'),
(3250, 'Gilleleje'),
(3300, 'Frederiksværk'),
(3320, 'Skævinge'),
(3330, 'Gørløse'),
(3360, 'Liseleje'),
(3370, 'Melby'),
(3390, 'Hundested'),
(3400, 'Hillerød'),
(3450, 'Allerød'),
(3460, 'Birkerød'),
(3480, 'Fredensborg'),
(3490, 'Kvistgård'),
(3500, 'Værløse'),
(3520, 'Farum'),
(3540, 'Lynge'),
(3550, 'Slangerup'),
(3600, 'Frederikssund'),
(3630, 'Jægerspris'),
(3650, 'Ølstykke'),
(3660, 'Stenløse'),
(3670, 'Veksø Sjælland'),
-- Bornholm
(3700, 'Rønne'),
(3720, 'Aakirkeby'),
(3730, 'Nexø'),
(3740, 'Svaneke'),
(3760, 'Gudhjem'),
(3770, 'Allinge'),
(3790, 'Hasle'),
-- Zealand
(4000, 'Roskilde'),
(4030, 'Tune'),
(4040, 'Jyllinge'),
(4050, 'Skibby'),
(4060, 'Kirke Såby'),
(4070, 'Kirke Hyllinge'),
(4100, 'Ringsted'),
(4130, 'Viby Sjælland'),
(4140, 'Borup'),
(4160, 'Herlufmagle'),
(4171, 'Glumsø'),
(4180, 'Sorø'),
(4190, 'Munke Bjergby'),
(4200, 'Slagelse'),
(4220, 'Korsør'),
(4230, 'Skælskør'),
(4250, 'Fuglebjerg'),
(4270, 'Høng'),
(4281, 'Gørlev'),
(4291, 'Ruds Vedby'),
(4293, 'Dianalund'),
(4295, 'Stenlille'),
(4300, 'Holbæk'),
(4320, 'Lejre'),
(4330, 'Hvalsø'),
(4340, 'Tølløse'),
(4350, 'Ugerløse'),
(4370, 'Store Merløse'),
(4390, 'Vipperød'),
(4400, 'Kalundborg'),
(4420, 'Regstrup'),
(4440, 'Mørkøv'),
(4450, 'Jyderup'),
(4460, 'Snertinge'),
(4470, 'Svebølle'),
(4480, 'Store Fuglede'),
(4490, 'Jerslev Sjælland'),
(4500, 'Nykøbing Sjælland'),
(4520, 'Svinninge'),
(4532, 'Gislinge'),
(4534, 'Hørve'),
(4540, 'Fårevejle'),
(4550, 'Asnæs'),
(4560, 'Vig'),
(4583, 'Sjællands Odde'),
(4591, 'Føllenslev'),
(4600, 'Køge'),
(4621, 'Gadstrup'),
(4622, 'Havdrup'),
(4623, 'Lille Skensved'),
(4632, 'Bjæverskov'),
(4640, 'Faxe'),
(4652, 'Hårlev'),
(4653, 'Karise'),
(4654, 'Faxe Ladeplads'),
(4660, 'Store Heddinge'),
(4671, 'Strøby'),
(4672, 'Klippinge'),
(4673, 'Rødvig Stevns'),
(4681, 'Herfølge'),
(4682, 'Tureby'),
(4683, 'Rønnede'),
(4690, 'Haslev'),
(4700, 'Næstved'),
(4720, 'Præstø'),
(4736, 'Karrebæksminde'),
(4750, 'Lundby'),
(4760, 'Vordingborg'),
(4780, 'Stege'),
-- Falster / Lolland
(4800, 'Nykøbing Falster'),
(4840, 'Nørre Alslev'),
(4850, 'Stubbekøbing'),
(4862, 'Guldborg'),
(4863, 'Eskilstrup'),
(4874, 'Gedser'),
(4880, 'Nysted'),
(4900, 'Nakskov'),
(4920, 'Søllested'),
(4930, 'Maribo'),
(4941, 'Bandholm'),
(4960, 'Holeby'),
(4970, 'Rødby'),
(4990, 'Sakskøbing'),
-- Funen
(5000, 'Odense C'),
(5200, 'Odense V'),
(5210, 'Odense NV'),
(5220, 'Odense SØ'),
(5230, 'Odense M'),
(5240, 'Odense NØ'),
(5250, 'Odense SV'),
(5260, 'Odense S'),
(5270, 'Odense N'),
(5290, 'Marslev'),
(5300, 'Kerteminde'),
(5320, 'Agedrup'),
(5330, 'Munkebo'),
(5370, 'Mesinge'),
(5400, 'Bogense'),
(5450, 'Otterup'),
(5462, 'Morud'),
(5464, 'Brenderup'),
(5471, 'Søndersø'),
(5474, 'Veflinge'),
(5491, 'Blommenslyst'),
(5492, 'Vissenbjerg'),
(5500, 'Middelfart'),
(5540, 'Ullerslev'),
(5550, 'Langeskov'),
(5560, 'Aarup'),
(5580, 'Nørre Aaby'),
(5591, 'Gelsted'),
(5592, 'Ejby'),
(5600, 'Faaborg'),
(5610, 'Assens'),
(5620, 'Glamsbjerg'),
(5631, 'Ebberup'),
(5642, 'Millinge'),
(5672, 'Broby'),
(5683, 'Haarby'),
(5690, 'Tommerup'),
(5700, 'Svendborg'),
(5750, 'Ringe'),
(5771, 'Stenstrup'),
(5772, 'Kværndrup'),
(5792, 'Årslev'),
(5800, 'Nyborg'),
(5853, 'Ørbæk'),
(5871, 'Frørup'),
(5874, 'Hesselager'),
(5881, 'Skårup Fyn'),
(5900, 'Rudkøbing'),
(5932, 'Humble'),
(5960, 'Marstal'),
(5970, 'Ærøskøbing'),
(5985, 'Søby Ærø'),
-- South Jutland
(6000, 'Kolding'),
(6040, 'Egtved'),
(6051, 'Almind'),
(6064, 'Jordrup'),
(6070, 'Christiansfeld'),
(6091, 'Bjert'),
(6094, 'Hejls'),
(6100, 'Haderslev'),
(6200, 'Aabenraa'),
(6230, 'Rødekro'),
(6240, 'Løgumkloster'),
(6261, 'Bredebro'),
(6270, 'Tønder'),
(6280, 'Højer'),
(6300, 'Gråsten'),
(6310, 'Broager'),
(6320, 'Egernsund'),
(6330, 'Padborg'),
(6340, 'Kruså'),
(6360, 'Tinglev'),
(6392, 'Bolderslev'),
(6400, 'Sønderborg'),
(6430, 'Nordborg'),
(6440, 'Augustenborg'),
(6470, 'Sydals'),
(6500, 'Vojens'),
(6510, 'Gram'),
(6520, 'Toftlund'),
(6560, 'Sommersted'),
(6580, 'Vamdrup'),
(6600, 'Vejen'),
(6621, 'Gesten'),
(6630, 'Rødding'),
(6640, 'Lunderskov'),
(6650, 'Brørup'),
(6670, 'Holsted'),
(6690, 'Gørding'),
-- South-West Jutland / Esbjerg
(6700, 'Esbjerg'),
(6705, 'Esbjerg Ø'),
(6710, 'Esbjerg V'),
(6715, 'Esbjerg N'),
(6720, 'Fanø'),
(6731, 'Tjæreborg'),
(6740, 'Bramming'),
(6752, 'Glejbjerg'),
(6760, 'Ribe'),
(6771, 'Gredstedbro'),
(6780, 'Skærbæk'),
(6792, 'Rømø'),
(6800, 'Varde'),
(6823, 'Ansager'),
(6830, 'Nørre Nebel'),
(6840, 'Oksbøl'),
(6854, 'Henne'),
(6857, 'Blåvand'),
(6862, 'Tistrup'),
(6870, 'Ølgod'),
(6880, 'Tarm'),
(6900, 'Skjern'),
(6920, 'Videbæk'),
(6933, 'Kibæk'),
(6940, 'Lem St'),
(6950, 'Ringkøbing'),
(6960, 'Hvide Sande'),
(6971, 'Spjald'),
(6980, 'Tim'),
(6990, 'Ulfborg'),
-- East Jutland / Fredericia / Vejle
(7000, 'Fredericia'),
(7080, 'Børkop'),
(7100, 'Vejle'),
(7120, 'Vejle Ø'),
(7130, 'Juelsminde'),
(7140, 'Stouby'),
(7160, 'Tørring'),
(7171, 'Uldum'),
(7182, 'Bredsten'),
(7190, 'Billund'),
(7200, 'Grindsted'),
(7250, 'Hejnsvig'),
(7260, 'Sønder Omme'),
(7280, 'Sønder Felding'),
(7300, 'Jelling'),
(7321, 'Gadbjerg'),
(7323, 'Give'),
(7330, 'Brande'),
(7361, 'Ejstrupholm'),
(7400, 'Herning'),
(7430, 'Ikast'),
(7441, 'Bording'),
(7451, 'Sunds'),
(7470, 'Karup J'),
(7480, 'Vildbjerg'),
(7490, 'Aulum'),
(7500, 'Holstebro'),
(7540, 'Haderup'),
(7550, 'Sørvad'),
(7560, 'Hjerm'),
(7570, 'Vemb'),
(7600, 'Struer'),
(7620, 'Lemvig'),
(7650, 'Bøvlingbjerg'),
(7660, 'Bækmarksbro'),
(7673, 'Harboøre'),
(7680, 'Thyborøn'),
-- Thy / Mors
(7700, 'Thisted'),
(7730, 'Hanstholm'),
(7741, 'Frøstrup'),
(7752, 'Snedsted'),
(7755, 'Bedsted Thy'),
(7760, 'Hurup Thy'),
(7790, 'Thyholm'),
(7800, 'Skive'),
(7830, 'Vinderup'),
(7840, 'Højslev'),
(7850, 'Stoholm Jylland'),
(7860, 'Spøttrup'),
(7870, 'Roslev'),
(7884, 'Fur'),
(7900, 'Nykøbing Mors'),
(7950, 'Erslev'),
(7960, 'Karby'),
(7970, 'Redsted M'),
(7980, 'Vils'),
(7990, 'Øster Assels'),
-- Aarhus
(8000, 'Aarhus C'),
(8200, 'Aarhus N'),
(8210, 'Aarhus V'),
(8220, 'Brabrand'),
(8230, 'Åbyhøj'),
(8240, 'Risskov'),
(8250, 'Egå'),
(8260, 'Viby J'),
(8270, 'Højbjerg'),
(8300, 'Odder'),
(8305, 'Samsø'),
(8310, 'Tranbjerg J'),
(8320, 'Mårslet'),
(8330, 'Beder'),
(8340, 'Malling'),
(8350, 'Hundslund'),
(8355, 'Solbjerg'),
(8361, 'Hasselager'),
(8362, 'Hørning'),
(8370, 'Hadsten'),
(8380, 'Trige'),
(8381, 'Tilst'),
(8382, 'Hinnerup'),
(8400, 'Ebeltoft'),
(8410, 'Rønde'),
(8420, 'Knebel'),
(8450, 'Hammel'),
(8462, 'Harlev J'),
(8464, 'Galten'),
(8471, 'Sabro'),
(8500, 'Grenaa'),
(8520, 'Lystrup'),
(8530, 'Hjortshøj'),
(8541, 'Skødstrup'),
(8543, 'Hornslet'),
(8544, 'Mørke'),
(8550, 'Ryomgård'),
(8560, 'Kolind'),
(8570, 'Trustrup'),
(8592, 'Anholt'),
-- Silkeborg / Skanderborg
(8600, 'Silkeborg'),
(8620, 'Kjellerup'),
(8641, 'Sorring'),
(8653, 'Them'),
(8654, 'Bryrup'),
(8660, 'Skanderborg'),
(8670, 'Låsby'),
(8680, 'Ry'),
-- Horsens
(8700, 'Horsens'),
(8721, 'Daugaard'),
(8722, 'Hedensted'),
(8723, 'Løsning'),
(8732, 'Hovedgård'),
(8740, 'Brædstrup'),
(8751, 'Gedved'),
(8752, 'Østbirk'),
(8762, 'Flemming'),
(8765, 'Klovborg'),
(8766, 'Nørre Snede'),
(8781, 'Stenderup'),
(8783, 'Hornsyld'),
-- Viborg / Randers
(8800, 'Viborg'),
(8830, 'Tjele'),
(8831, 'Løgstrup'),
(8832, 'Skals'),
(8840, 'Rødkærsbro'),
(8850, 'Bjerringbro'),
(8860, 'Ulstrup'),
(8870, 'Langå'),
(8881, 'Thorsø'),
(8882, 'Fårvang'),
(8883, 'Gjern'),
(8900, 'Randers C'),
(8920, 'Randers NV'),
(8930, 'Randers NØ'),
(8940, 'Randers SV'),
(8950, 'Ørsted'),
(8960, 'Randers SØ'),
(8961, 'Allingåbro'),
(8963, 'Auning'),
(8970, 'Havndal'),
(8981, 'Spentrup'),
(8983, 'Gjerlev J'),
(8990, 'Fårup'),
-- Aalborg
(9000, 'Aalborg'),
(9200, 'Aalborg SV'),
(9210, 'Aalborg SØ'),
(9220, 'Aalborg Ø'),
(9230, 'Svenstrup J'),
(9240, 'Nibe'),
(9260, 'Gistrup'),
(9270, 'Klarup'),
(9280, 'Storvorde'),
(9293, 'Kongerslev'),
(9300, 'Sæby'),
(9310, 'Vodskov'),
(9320, 'Hjallerup'),
(9330, 'Dronninglund'),
(9340, 'Asaa'),
(9352, 'Dybvad'),
(9362, 'Gandrup'),
(9370, 'Hals'),
(9380, 'Vestbjerg'),
(9400, 'Nørresundby'),
(9430, 'Vadum'),
(9440, 'Aabybro'),
(9460, 'Brovst'),
(9480, 'Løkken'),
(9490, 'Pandrup'),
(9492, 'Blokhus'),
(9493, 'Saltum'),
-- Himmerland / Rebild
(9500, 'Hobro'),
(9510, 'Arden'),
(9520, 'Skørping'),
(9530, 'Støvring'),
(9541, 'Suldrup'),
(9550, 'Mariager'),
(9560, 'Hadsund'),
(9574, 'Bælum'),
(9575, 'Terndrup'),
-- Vesthimmerland
(9600, 'Aars'),
(9610, 'Nørager'),
(9620, 'Aalestrup'),
(9631, 'Gedsted'),
(9632, 'Møldrup'),
(9640, 'Farsø'),
(9670, 'Løgstør'),
(9681, 'Ranum'),
(9690, 'Fjerritslev'),
-- Brønderslev / Hjørring / Frederikshavn
(9700, 'Brønderslev'),
(9740, 'Jerslev J'),
(9750, 'Østervrå'),
(9760, 'Vrå'),
(9800, 'Hjørring'),
(9830, 'Tårs'),
(9850, 'Hirtshals'),
(9870, 'Sindal'),
(9881, 'Bindslev'),
(9900, 'Frederikshavn'),
(9940, 'Læsø'),
(9970, 'Strandby'),
(9981, 'Jerup'),
(9982, 'Ålbæk'),
(9990, 'Skagen');

--
-- PostgreSQL database dump complete
--
