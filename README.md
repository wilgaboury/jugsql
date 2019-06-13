# JugSQL

This library is based off of a library called PugSQL (which is based on a library called HugSQL). At work I often have to do manual sql queries in java and I found that for most projects the syntax is extremely cumbersome, so after reading about PugSQL, I realized that a similar library could be made for java as well.

In essence this library is a big JDBC wrapper that allows for one to put their SQL statement into it's own file/s (instead of ugly strings at random places in your code) and then easily call them and either get back ResultSet's or populate POJO's. This library is defiantly going to be slower than manually using the conventional Java/SQL API but it will cause less developer headache while also being much more maintainable. It is also a lot less bloat then a full blown ORM and probably faster.

## Goals
+ have separate SQL files for all statements
+ be able to fill in sql parameters by name and easy string interpolation of statements
+ easy POJO mapping (even nested POJO's via database relations)
+ thread safe and able to use connection pools
+ extensive unit testing using HSQLDB