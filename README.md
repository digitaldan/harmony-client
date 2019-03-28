# harmony-client

Logitech Harmony WebSocket Client

License EPL v2.0

[![TravisCI Build Status](https://travis-ci.org/digitaldan/harmony-client.svg?branch=master)](https://travis-ci.org/digitaldan/harmony-client)
# Running

`mvn clean compile assembly:single`
 
`java -cp target/harmony-client-*-jar-with-dependencies.jar com.digitaldan.harmony.shell.App YOUR_HUB_IP`
 
This will launch a interactive shell, commands are:
 
* list devices              - lists the configured devices and their id's
* list activities           - lists the configured activities and their id's
* show activity             - shows the current activity
* start \<activity>         - starts an activity (takes a string or id)
* press \<device> \<button> - perform a single button press
* get_config                - Dumps the full config json, unformatted 

