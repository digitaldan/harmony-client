# harmony-client
Logitech Harmony WebSocket Client

Licese EPL v2


# Running
 mvn clean compile assembly:single
 
 java -cp target/harmony-client-1.0-SNAPSHOT-jar-with-dependencies.jar com.digitaldan.harmony.App YOUR_HUB_IP
 
 This will launch a interactive shell, commands are:
 
  * list devices              - lists the configured devices and their id's
  * list activities           - lists the configured activities and their id's
  * show activity             - shows the current activity
  * start \<activity>         - starts an activity (takes a string or id)
  * press \<device> \<button> - perform a single button press
  * get_config                - Dumps the full config json, unformatted 
 
 
 
