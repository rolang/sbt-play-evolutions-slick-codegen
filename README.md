# sbt-play-evolutions-slick-codegen

Slick code generator sbt plugin for play evolutions.

```bash
# ensure the database service is running (if you don't use an in-memory database), e.g.
docker run -e POSTGRES_PASSWORD=passwd -p 5432:5432 -d postgres

# slick models will be generated on compile
sbt ~compile
```
