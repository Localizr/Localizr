Localizr
========

Localizr was a student project as part of the course "Semantic Web Technologies" at the University of Mannheim in 2014. We wanted to share our code with the world.

For more detailed information see the [report](report.pdf) that had to be created for this project.

In the current version the points of interest and the photos do not work anymore. The reason for this are probably changes in APIs.

## Dependencies
* [Play Framework Version 2.4](https://playframework.com)
* [Bower](http://bower.io/)

## Setup API Keys
You need to have a Google maps API key and set it in `app/views/index.scala.html`. For the EventAPI you need an Eventful API key and set it up inside `app/models/eventful/EventfulAPI.java`

## Build instructions
* Run `bower install` in the public directory
* Run `activator -jvm-debug 9999 ~run` for debug or `activator ~run` for release configuration inside the root directory

## Last changes
* Fixed following issue with new SPARQL query: The SPARQL queries for fetching DBPedia information about a city did not work anymore as expected. Often the result is empty. In big cities like Berlin it still worked.
* Fixed following issue by adding Eventful API instead of using semantic web data from EventMedia: The EventMedia database we used for fetching event data is down most of the time. The authors seem not to maintain it anymore.
* Remove category filters
* Update search view to have a cleaner look
* Use Google maps API for city name validation

## Original team members
* Anna Primpeli
* [Maximilian Böhm](https://maximilian-boehm.com)
* [Martin Pfannemüller](https://pfannemueller.de)
