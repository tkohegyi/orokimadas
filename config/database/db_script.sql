--
-- PostgreSQL database dump
--

-- Dumped from database version 12.2
-- Dumped by pg_dump version 12.2

-- Started on 2020-06-26 17:06:18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: dbo; Type: SCHEMA; Schema: -; Owner: adorApp
--

CREATE SCHEMA dbo;


ALTER SCHEMA dbo OWNER TO "adorApp";

--
-- TOC entry 2855 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA dbo; Type: COMMENT; Schema: -; Owner: adorApp
--

COMMENT ON SCHEMA dbo IS 'standard public schema';


--
-- TOC entry 205 (class 1259 OID 32808)
-- Name: AdorationUniqueNumber; Type: SEQUENCE; Schema: dbo; Owner: adorApp
--

CREATE SEQUENCE dbo."AdorationUniqueNumber"
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dbo."AdorationUniqueNumber" OWNER TO "adorApp";

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 202 (class 1259 OID 32768)
-- Name: audittrail; Type: TABLE; Schema: dbo; Owner: adorApp
--

CREATE TABLE dbo.audittrail (
    id bigint NOT NULL,
    activitytype character varying(255) NOT NULL,
    atwhen character varying(255) NOT NULL,
    bywho character varying(255) NOT NULL,
    data character varying(255),
    description character varying(255) NOT NULL,
    refid bigint NOT NULL
);


ALTER TABLE dbo.audittrail OWNER TO "adorApp";

--
-- TOC entry 203 (class 1259 OID 32776)
-- Name: link; Type: TABLE; Schema: dbo; Owner: adorApp
--

CREATE TABLE dbo.link (
    id bigint NOT NULL,
    admincomment character varying(255),
    hourid integer NOT NULL,
    personid bigint NOT NULL,
    priority integer NOT NULL,
    publiccomment character varying(255),
    type integer NOT NULL
);


ALTER TABLE dbo.link OWNER TO "adorApp";

--
-- TOC entry 206 (class 1259 OID 49156)
-- Name: person; Type: TABLE; Schema: dbo; Owner: adorApp
--

CREATE TABLE dbo.person (
    id bigint NOT NULL,
    admincomment character varying(255),
    adorationstatus integer NOT NULL,
    coordinatorcomment character varying(255),
    dhcsigned boolean NOT NULL,
    dhcsigneddate character varying(255),
    email character varying(255) NOT NULL,
    emailvisible boolean NOT NULL,
    languagecode character varying(255) NOT NULL,
    mobile character varying(255),
    mobilevisible boolean NOT NULL,
    name character varying(255) NOT NULL,
    visiblecomment character varying(255),
    isanonymous boolean NOT NULL
);


ALTER TABLE dbo.person OWNER TO "adorApp";

--
-- TOC entry 207 (class 1259 OID 49164)
-- Name: social; Type: TABLE; Schema: dbo; Owner: adorApp
--

CREATE TABLE dbo.social (
    id bigint NOT NULL,
    socialstatus integer NOT NULL,
    facebookemail character varying(255),
    facebookfirstname character varying(255),
    facebookuserid character varying(255),
    facebookusername character varying(255),
    googleemail character varying(255),
    googleuserid character varying(255),
    googleusername character varying(255),
    googleuserpicture character varying(255),
    personid bigint
);


ALTER TABLE dbo.social OWNER TO "adorApp";

--
-- TOC entry 204 (class 1259 OID 32792)
-- Name: translator; Type: TABLE; Schema: dbo; Owner: adorApp
--

CREATE TABLE dbo.translator (
    textid character varying(255) NOT NULL,
    languagecode character varying(255) NOT NULL,
    text character varying(255)
);


ALTER TABLE dbo.translator OWNER TO "adorApp";

--
-- TOC entry 2857 (class 0 OID 0)
-- Dependencies: 205
-- Name: AdorationUniqueNumber; Type: SEQUENCE SET; Schema: dbo; Owner: adorApp
--

SELECT pg_catalog.setval('dbo."AdorationUniqueNumber"', 10132, true);


--
-- TOC entry 2709 (class 2606 OID 32775)
-- Name: audittrail audittrail_pkey; Type: CONSTRAINT; Schema: dbo; Owner: adorApp
--

ALTER TABLE ONLY dbo.audittrail
    ADD CONSTRAINT audittrail_pkey PRIMARY KEY (id);


--
-- TOC entry 2711 (class 2606 OID 32783)
-- Name: link link_pkey; Type: CONSTRAINT; Schema: dbo; Owner: adorApp
--

ALTER TABLE ONLY dbo.link
    ADD CONSTRAINT link_pkey PRIMARY KEY (id);


--
-- TOC entry 2715 (class 2606 OID 49163)
-- Name: person person_pkey; Type: CONSTRAINT; Schema: dbo; Owner: adorApp
--

ALTER TABLE ONLY dbo.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- TOC entry 2717 (class 2606 OID 49171)
-- Name: social social_pkey; Type: CONSTRAINT; Schema: dbo; Owner: adorApp
--

ALTER TABLE ONLY dbo.social
    ADD CONSTRAINT social_pkey PRIMARY KEY (id);


--
-- TOC entry 2713 (class 2606 OID 32799)
-- Name: translator translator_pkey; Type: CONSTRAINT; Schema: dbo; Owner: adorApp
--

ALTER TABLE ONLY dbo.translator
    ADD CONSTRAINT translator_pkey PRIMARY KEY (textid);


--
-- TOC entry 2856 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA dbo; Type: ACL; Schema: -; Owner: adorApp
--

REVOKE ALL ON SCHEMA dbo FROM postgres;
REVOKE ALL ON SCHEMA dbo FROM PUBLIC;
GRANT ALL ON SCHEMA dbo TO PUBLIC;
GRANT ALL ON SCHEMA dbo TO "adorApp";
GRANT ALL ON SCHEMA dbo TO postgres;


-- Completed on 2020-06-26 17:06:18

--
-- PostgreSQL database dump complete
--

