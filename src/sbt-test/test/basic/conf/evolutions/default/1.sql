-- !Ups

CREATE TABLE test (
  id SERIAL PRIMARY KEY,
  name text,
  number int
);

-- !Downs

DROP TABLE IF EXISTS test CASCADE;
