-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users
(
    id                 serial       NOT NULL,
    username           varchar(255) NOT NULL,
    "password"         varchar(64)  NULL,
    enabled            bool         NOT NULL DEFAULT true,
    created_date       timestamp    NULL     DEFAULT now(),
    last_modified_date timestamp    NULL,
    provider           varchar(255) NOT NULL,
    provider_user_id   varchar(255) NULL,
    email              varchar(255) NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);


-- public.authorities definition

-- Drop table

-- DROP TABLE public.authorities;

CREATE TABLE public.authorities
(
    user_id   int4        NOT NULL,
    authority varchar(50) NOT NULL,
    CONSTRAINT authorities_pkey PRIMARY KEY (user_id, authority),
    CONSTRAINT fk_authorities_users FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);


-- public.symbols definition

-- Drop table

-- DROP TABLE public.symbols;

CREATE TABLE public.symbols
(
    code         varchar(8)   NOT NULL,
    currency     varchar(8)   NULL,
    "name"       varchar(255) NULL,
    country_flag varchar(16)  NULL,
    created_by   varchar(50)  NULL,
    created_date timestamp    NULL DEFAULT now(),
    CONSTRAINT symbols_pkey PRIMARY KEY (code),
    CONSTRAINT fk_symbols_currency FOREIGN KEY (currency) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX fk_symbols_currency_idx ON public.symbols USING btree (currency);


-- public.favorite_symbol definition

-- Drop table

-- DROP TABLE public.favorite_symbol;

CREATE TABLE public.favorite_symbol
(
    user_id          int4       NOT NULL,
    from_symbol_code varchar(8) NOT NULL,
    to_symbol_code   varchar(8) NOT NULL,
    CONSTRAINT favorite_symbol_pkey PRIMARY KEY (user_id, from_symbol_code, to_symbol_code),
    CONSTRAINT fk_favorite_symbol_from_symbol FOREIGN KEY (from_symbol_code) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_favorite_symbol_to_symbol FOREIGN KEY (to_symbol_code) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_favorite_symbol_users FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX fk_favorite_symbol_from_symbol ON public.favorite_symbol USING btree (from_symbol_code);
CREATE INDEX fk_favorite_symbol_to_symbol ON public.favorite_symbol USING btree (to_symbol_code);

-- public.latestrates definition

-- Drop table

-- DROP TABLE public.latestrates;

CREATE TABLE public.latestrates
(
    "date"             timestamp       NOT NULL,
    fromcur            varchar(8)      NOT NULL,
    tocur              varchar(8)      NOT NULL,
    value              numeric(20, 10) NOT NULL,
    created_date       timestamp       NULL DEFAULT now(),
    last_modified_date timestamp       NULL,
    CONSTRAINT latestrates_pkey PRIMARY KEY (fromcur, tocur),
    CONSTRAINT fk_latestrates_fromcur FOREIGN KEY (fromcur) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_latestrates_tocur FOREIGN KEY (tocur) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX fk_latestrates_fromcur_idx ON public.latestrates USING btree (fromcur);
CREATE INDEX fk_latestrates_tocur_idx ON public.latestrates USING btree (tocur);


-- public.pair definition

-- Drop table

-- DROP TABLE public.pair;

CREATE TABLE public.pair
(
    id            serial       NOT NULL,
    symbol        varchar(16)  NOT NULL,
    "name"        varchar(255) NOT NULL,
    "source"      varchar(8)   NOT NULL,
    target        varchar(8)   NOT NULL,
    exchange      varchar(45)  NULL,
    provider_code varchar(8)   NOT NULL,
    created_by    varchar(50)  NULL,
    created_date  timestamp    NULL DEFAULT now(),
    CONSTRAINT pair_pkey PRIMARY KEY (id),
    CONSTRAINT pair_symbol_provider_code_key UNIQUE (symbol, provider_code),
    CONSTRAINT fk_pair_source FOREIGN KEY (source) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_pair_target FOREIGN KEY (target) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX source_fk_idx ON public.pair USING btree (source);
CREATE INDEX target_fk_idx ON public.pair USING btree (target);


-- public.portofolio definition

-- Drop table

-- DROP TABLE public.portofolio;

CREATE TABLE public.portofolio
(
    id                 serial          NOT NULL,
    user_id            int4            NOT NULL,
    base_currency_code varchar(8)      NOT NULL,
    amount             numeric(20, 10) NOT NULL,
    "version"          int8            NOT NULL,
    created_by         varchar(50)     NULL,
    created_date       timestamp       NULL DEFAULT now(),
    last_modified_by   varchar(50)     NULL,
    last_modified_date timestamp       NULL,
    CONSTRAINT portofolio_pkey PRIMARY KEY (id),
    CONSTRAINT portofolio_user_id_key UNIQUE (user_id),
    CONSTRAINT fk_portofolio_base_currency_code FOREIGN KEY (base_currency_code) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT portofolio_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX portofolio_ibfk_2 ON public.portofolio USING btree (base_currency_code);


-- public.rates definition

-- Drop table

-- DROP TABLE public.rates;

CREATE TABLE public.rates
(
    "date"  timestamp       NOT NULL,
    fromcur varchar(8)      NOT NULL,
    tocur   varchar(8)      NOT NULL,
    value   numeric(20, 10) NOT NULL,
    CONSTRAINT rates_pkey PRIMARY KEY (fromcur, tocur, date),
    CONSTRAINT fk_rates_fromcur FOREIGN KEY (fromcur) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_rates_tocur FOREIGN KEY (tocur) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX fk_rates_fromcur_idx ON public.rates USING btree (fromcur);
CREATE INDEX fk_rates_tocur_idx ON public.rates USING btree (tocur);


-- public.assets definition

-- Drop table

-- DROP TABLE public.assets;

CREATE TABLE public.assets
(
    id                 serial          NOT NULL,
    portofolio_id      int4            NOT NULL,
    symbol_code        varchar(8)      NOT NULL,
    quantity           numeric(20, 10) NOT NULL,
    currency_code      varchar(8)      NOT NULL,
    currency_amount    numeric(20, 10) NOT NULL,
    "version"          int8            NOT NULL,
    created_by         varchar(50)     NULL,
    created_date       timestamp       NULL DEFAULT now(),
    last_modified_by   varchar(50)     NULL,
    last_modified_date timestamp       NULL,
    CONSTRAINT assets_pkey PRIMARY KEY (id),
    CONSTRAINT fk_assets_currency_code FOREIGN KEY (currency_code) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_assets_portofolio_id FOREIGN KEY (portofolio_id) REFERENCES portofolio (id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_assets_symbol_code FOREIGN KEY (symbol_code) REFERENCES symbols (code) ON UPDATE RESTRICT ON DELETE RESTRICT
);
CREATE INDEX assets_ibfk_1 ON public.assets USING btree (currency_code);
CREATE INDEX fk_assets_portofolio_idx ON public.assets USING btree (portofolio_id);
CREATE INDEX fk_assets_symbol_idx ON public.assets USING btree (symbol_code);
