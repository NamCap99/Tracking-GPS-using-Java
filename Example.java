// import java.util.stream.Stream;

import nz.sodium.*;
import nz.sodium.Stream;

// import java.util.stream.Stream;

/** 
 * An example of how to use the GpsService class
 */
public class Example {

    public static void main(String[] args){

        // Initialise the GPS Service
        GpsService serv = new GpsService();
        // Retrieve Event Streams
        nz.sodium.Stream<GpsEvent>[] streams = (nz.sodium.Stream<GpsEvent>[]) serv.getEventStreams();
        // Attach a handler method to each stream
        for(Stream<GpsEvent> s : streams){
            s.listen((GpsEvent ev) -> System.out.println(ev));
        }
    }

} 
