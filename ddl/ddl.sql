--
-- PostgreSQL database dump
--

--
-- TOC entry 173 (class 1259 OID 49334)
-- Name: account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE account (
    id integer NOT NULL,
    cif character varying(20),
    account_number character varying(20),
    status boolean
);


ALTER TABLE public.account OWNER TO postgres;

--
-- TOC entry 172 (class 1259 OID 49332)
-- Name: account_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.account_id_seq OWNER TO postgres;

--
-- TOC entry 1971 (class 0 OID 0)
-- Dependencies: 172
-- Name: account_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE account_id_seq OWNED BY account.id;


--
-- TOC entry 175 (class 1259 OID 49351)
-- Name: balance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE balance (
    id integer NOT NULL,
    amount double precision,
    account_id bigint,
    version timestamp without time zone
);


ALTER TABLE public.balance OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 49349)
-- Name: balance_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE balance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.balance_id_seq OWNER TO postgres;

--
-- TOC entry 1972 (class 0 OID 0)
-- Dependencies: 174
-- Name: balance_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE balance_id_seq OWNED BY balance.id;


--
-- TOC entry 171 (class 1259 OID 49316)
-- Name: card; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE card (
    id integer NOT NULL,
    print_number character varying(16),
    cif character varying(20),
    status boolean
);


ALTER TABLE public.card OWNER TO postgres;

--
-- TOC entry 170 (class 1259 OID 49314)
-- Name: card_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE card_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.card_id_seq OWNER TO postgres;

--
-- TOC entry 1973 (class 0 OID 0)
-- Dependencies: 170
-- Name: card_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE card_id_seq OWNED BY card.id;


--
-- TOC entry 1836 (class 2604 OID 49337)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY account ALTER COLUMN id SET DEFAULT nextval('account_id_seq'::regclass);


--
-- TOC entry 1837 (class 2604 OID 49354)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY balance ALTER COLUMN id SET DEFAULT nextval('balance_id_seq'::regclass);


--
-- TOC entry 1835 (class 2604 OID 49319)
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY card ALTER COLUMN id SET DEFAULT nextval('card_id_seq'::regclass);


--
-- TOC entry 1960 (class 0 OID 49334)
-- Dependencies: 173
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO account (id, cif, account_number, status) VALUES (1, '000000000379700', '0020000000676471', true);


--
-- TOC entry 1974 (class 0 OID 0)
-- Dependencies: 172
-- Name: account_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('account_id_seq', 1, true);


--
-- TOC entry 1962 (class 0 OID 49351)
-- Dependencies: 175
-- Data for Name: balance; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO balance (id, amount, account_id, version) VALUES (1, 930000, 1, '2017-04-27 12:29:02.289');


--
-- TOC entry 1975 (class 0 OID 0)
-- Dependencies: 174
-- Name: balance_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('balance_id_seq', 1, true);


--
-- TOC entry 1958 (class 0 OID 49316)
-- Dependencies: 171
-- Data for Name: card; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO card (id, print_number, cif, status) VALUES (2, '140451100070649', '000000000379700', true);


--
-- TOC entry 1976 (class 0 OID 0)
-- Dependencies: 170
-- Name: card_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('card_id_seq', 2, true);


--
-- TOC entry 1843 (class 2606 OID 49343)
-- Name: cif_account_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY account
    ADD CONSTRAINT cif_account_unique UNIQUE (cif);


--
-- TOC entry 1845 (class 2606 OID 49339)
-- Name: id_account_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY account
    ADD CONSTRAINT id_account_pk PRIMARY KEY (id);


--
-- TOC entry 1847 (class 2606 OID 49356)
-- Name: id_balance_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY balance
    ADD CONSTRAINT id_balance_pk PRIMARY KEY (id);


--
-- TOC entry 1839 (class 2606 OID 49341)
-- Name: id_card_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY card
    ADD CONSTRAINT id_card_pk PRIMARY KEY (id);


--
-- TOC entry 1841 (class 2606 OID 49325)
-- Name: print_number_unique; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY card
    ADD CONSTRAINT print_number_unique UNIQUE (print_number);


--
-- TOC entry 1849 (class 2606 OID 49357)
-- Name: account_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY balance
    ADD CONSTRAINT account_id_fk FOREIGN KEY (account_id) REFERENCES account(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1848 (class 2606 OID 49344)
-- Name: cif_card_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY card
    ADD CONSTRAINT cif_card_fk FOREIGN KEY (cif) REFERENCES account(cif);


--
-- TOC entry 1969 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-04-27 15:36:27

--
-- PostgreSQL database dump complete
--

