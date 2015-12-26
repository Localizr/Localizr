Localizr
========

Localizr was a student project as part of the course "Semantic Web Technologies" at the University of Mannheim in 2014. We wanted to share our code with the world. You can find a running version of it [here](https://localizr.info).

##Dependencies
* [Play Framework Version 2.4](https://playframework.com)
* [Bower](http://bower.io/)

##Build instructions
* Run `bower install` in the public directory
* Run `activator -jvm-debug 9999 ~run` for debug or `activator ~run` for release configuration inside the root directory

## Known issues
* The SPARQL queries for fetching DBPedia information about a city do not work anymore as expected. Often the result is empty. In big cities like Berlin it still works.
* The EventMedia database we used for fetching event data is down most of the time. The authors seem not to maintain it anymore.

## Original team members
* Anna Primpeli
* Maximilian Böhm
* Martin Pfannemüller