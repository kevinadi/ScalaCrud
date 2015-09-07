# ScalaCrud

Usage: `scala ScalaCrud.scala`


### Synopsis ###

ScalaCrud is my own experiment into [Scala](http://scala-lang.org/); to see whether 
it can replace [Java](http://java.com/en/), and use Java libraries as easy as Java 
itself. Also, it is an experimentation in [AngularJS](https://angularjs.org/), 
which appear to simplify front-end maintenance compared to traditional
DOM-mutating processes of [JQuery](https://jquery.com/). Front-end niceties are
built using [Bootstrap](http://getbootstrap.com/), which is another first for me.

This is a project among friends, where a couple of friends need help compiling and
summarizing their data into something more automated, readable, and modern. This
between-friends project has so far begat two distinct projects:

1. *CLD*: where a broker needs a method to keep track of his bonuses and satisfied
   (or unsatisifed) customers. CLD lets him keep track of his income, and see
   his new customers and his exiting customers. For this reason, a working database
   of CLD data cannot be included in GitHub. I'm working on a sanitized CLD database
   so at least the CLD portion can run without crashing.

2. *JetSeater*: where an analyst in an airline wanted a way to summarize his
   data using something that is faster than Excel, and a method to enter new
   data into the database.

Above all, ScalaCrud is trying to prove that both applications can be coded using
one single .scala source file. Backend database is deployed using 
[H2 Database](http://www.h2database.com), and communication between front-end and
back-end was done using Ajax. Just to make things more interesting, I coded a
Scala-to-JSON encoder that is not tested and might not work properly, but is
useful for me to learn more about Scala's pattern-matching.

Please note that ScalaCrud is highly customized for those two purposes, and
there is minimal documentation on the code itself. When I have more time, maybe I
should put some documentation here & there so I don't forget what I was doing.