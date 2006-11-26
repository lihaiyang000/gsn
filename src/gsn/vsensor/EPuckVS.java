package gsn.vsensor;

import gsn.beans.StreamElement;
import gsn.beans.VSensorConfig;
import gsn.utils.protocols.ProtocolManager;
import gsn.utils.protocols.EPuck.SerComProtocol;
import gsn.wrappers.AbstractWrapper;
import gsn.wrappers.general.SerialWrapper;

import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

public class EPuckVS extends AbstractVirtualSensor {
   
   private static final transient Logger logger = Logger.getLogger( EPuckVS.class );
   
   private TreeMap < String , String >   params;
   
   private ProtocolManager               protocolManager;
   
   private AbstractWrapper                       wrapper;
   
   private VSensorConfig                 vsensor;
   
   public boolean initialize ( ) {
      params = getVirtualSensorConfiguration( ).getMainClassInitialParams( );
      wrapper = getVirtualSensorConfiguration( ).getInputStream( "input1" ).getSource( "source1" ).getActiveSourceProducer( );
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
   
   boolean actionA = false;
   
   public void dataAvailable ( String inputStreamName , StreamElement data ) {
      if ( logger.isDebugEnabled( ) ) logger.debug( "I just received some data from the robot" );
      System.out.println( new String( ( byte [ ] ) data.getData( SerialWrapper.RAW_PACKET ) ) );
      AbstractWrapper wrapper = vsensor.getInputStream( "input1" ).getSource( "source1" ).getActiveSourceProducer( );
      if ( actionA == false ) {
         actionA = true;
         try {
            // wrapper.sendToWrapper( "h\n" );
            wrapper.sendToWrapper( "d,1000,-1000\n" );
         } catch ( OperationNotSupportedException e ) {
            logger.error( e.getMessage( ) , e );
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
