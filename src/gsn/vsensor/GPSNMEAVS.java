package gsn.vsensor;

import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.beans.VSensorConfig;
import gsn.utils.protocols.ProtocolManager;
import gsn.utils.protocols.EPuck.SerComProtocol;

import gsn.wrappers.AbstractWrapper;
import gsn.wrappers.general.SerialWrapper;

import java.io.Serializable;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;


/**
 * Virtual sensor to support GPS coord given by NMEA specification over serial
 * Only the $GPRMC values are required.
 * (works as well on bluetooth GPS mapped to serial)
 * 
 * @author Clement Beffa ( clement.beffa@epfl.ch )
 */
public class GPSNMEAVS extends AbstractVirtualSensor {
   
   private static final transient Logger logger = Logger.getLogger( GPSNMEAVS.class );
   
   private TreeMap < String , String >   params;
   
   private ProtocolManager               protocolManager;
   
   private AbstractWrapper                       wrapper;
   
   private VSensorConfig                 vsensor;
   
   private static final String [ ] fieldNames = new String [ ] { "latitude" , "longitude" };
   
   private static final Integer [ ] fieldTypes = new Integer [ ] { DataTypes.DOUBLE, DataTypes.DOUBLE};
   
   private Serializable [ ] outputData = new Serializable [ fieldNames.length ];
   
   public boolean initialize ( ) {
      vsensor = getVirtualSensorConfiguration( );
      params = vsensor.getMainClassInitialParams( );
      wrapper = vsensor.getInputStream( "input1" ).getSource( "source1" ).getActiveSourceProducer( );
      protocolManager = new ProtocolManager( new SerComProtocol( ) , wrapper );
      if ( logger.isDebugEnabled( ) ) logger.debug( "Created protocolManager" );
      try {
         wrapper.sendToWrapper( "h\n" );
      } catch ( OperationNotSupportedException e ) {
         e.printStackTrace( );
      }      
      
      // protocolManager.sendQuery( SerComProtocol.RESET , null );
      if ( logger.isDebugEnabled( ) ) logger.debug( "Initialization complete." );
      return true;
   }
   
   public void dataAvailable ( String inputStreamName , StreamElement data ) {
      if ( logger.isDebugEnabled( ) ) logger.debug( "SERIAL RAW DATA :"+new String((byte[])data.getData(SerialWrapper.RAW_PACKET)));
      
      //needed? ######
      AbstractWrapper wrapper = vsensor.getInputStream( "input1" ).getSource( "source1" ).getActiveSourceProducer( );
      
      //raw data from serial
      String s = new String( ( byte [ ] ) data.getData( SerialWrapper.RAW_PACKET ) );
      String [ ] line = s.split( "\n" );
      //iterate on every line
      for ( int i = 0 ; i < line.length ; i++ ) {
         String [ ] part = line[ i ].split( "," );
         //Only the $GPRMC line are analyed for GPS coord
         //Using $GPGGA might be better but wouldn't give any result when no sat are tracked
         if ( part[ 0 ].equals( "$GPRMC" ) ) {
            //converting latitude from DDMM.MMMM to decimal notation
        	Double d = Double.valueOf( part[ 3 ] );
        	Double lat = d / 100.0;
            lat = Math.floor( lat );
            lat += Double.valueOf( d % 100.0 ) / 60.0;
            if ( part[ 4 ].equals( "S" ) )
               lat = -lat; // south coord
            else if ( !part[ 4 ].equals( "N" ) ) 
            	continue; // neither south or north: invalid format -> skip 
            
            //converting longitude
            d = Double.valueOf( part[ 5 ] );
            Double lon = Math.floor( d / 100.0 );
            lon += Double.valueOf( d % 100.0 ) / 60.0;
            if ( part[ 6 ].equals( "W" ) ) 
            	lon = -lon; // west coord
            
            logger.debug( "latitude:" + lat + " longitude:" + lon );
            
            //send back the data
            outputData[ 0 ] = lat;
            outputData[ 1 ] = lon;
            StreamElement output = new StreamElement( fieldNames , fieldTypes , outputData , System.currentTimeMillis( ) );
            dataProduced( output );
            break;//one $GPRMC line is enough
         }
      }
      
   }
   
   public void finalize ( ) {
      try {
         vsensor.getInputStream( "input1" ).getSource( "source1" ).getActiveSourceProducer( ).sendToWrapper( "R\n" );
      } catch ( OperationNotSupportedException e ) {
         logger.error( e.getMessage( ) , e );
      }
   }
   
}
