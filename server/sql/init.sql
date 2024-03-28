CREATE TABLE  public.game
(
    id bigint NOT NULL PRIMARY KEY,
    "totalTime" bigint NOT NULL,
    "lastRound" smallint NOT NULL,
    date character varying(10) COLLATE pg_catalog."default" NOT NULL,
    title character varying(20) COLLATE pg_catalog."default" NOT NULL,
    "lastDayType" character varying(10) COLLATE pg_catalog."default" NOT NULL,
    result character varying(10) COLLATE pg_catalog."default" NOT NULL
);

CREATE TABLE public.player
(
    id bigint NOT NULL  PRIMARY KEY,
    name character varying(30) COLLATE pg_catalog."default" NOT NULL
);

CREATE TABLE public.playergame
(
    "playerId" bigint NOT NULL,
    "gameId" bigint NOT NULL,
    "number" smallint NOT NULL,
    role character varying(15) COLLATE pg_catalog."default" NOT NULL,
    "isWinner" boolean NOT NULL,
    "isAlive" boolean NOT NULL,
    "isDeleted" boolean NOT NULL,
    actions bytea NOT NULL,
    CONSTRAINT "unique" UNIQUE ("playerId", "gameId") WITH (FILLFACTOR=90)
);