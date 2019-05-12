-- !Ups

CREATE TABLE test (
  id SERIAL PRIMARY KEY,
  name text,
  number int,
  document jsonb,
  list text[]
);

-- !Downs

DROP TABLE IF EXISTS test CASCADE;
