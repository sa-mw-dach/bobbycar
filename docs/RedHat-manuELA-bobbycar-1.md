![Red Hat logo supposed to be here][logo]

# Bobbycar - A hitchikers guide in one page

IoT Transportation demo using Red Hat OpenShift and Middleware technologies.

In this demo you will see a fleet management system to track vehicles (called bobbycars). Special zones are implemented ( called BobbycarZone) and based on the location data shared by the bobbycar special rules and restrictions apply for the bobbycar(s) beeing located by the system in the BobycarZone.


![map view][02_map] <div style="text-align: right"><sup><span style="color:blue">(c) Google maps</span></sup></div>

[02_map]: images/02_map.png "bobbycar map view"

**WHY?** you might ask.

We try to cover several scenarios in a simplified way at the same time.

In order to achieve the above view on your fleet you need:
+ Connection to external systems like data providers
+ Connection to systems transferring their data via wire or even completely disconnected via air
+ There is a mixture of data
  + real time data
  + historic data
  + non real time data with low priority
  + different data protection needs for several data streams
  + data with high bandwidth needs (like onboard camera)
  + data with low bandwidth need (like geo location)

Depending on the data you refer to also different software components need to be used in order to address the several data stream constraints and applying security needs.


[logo]: images/Logo-RedHat-A-Color-RGB.png "I should be on the top"
