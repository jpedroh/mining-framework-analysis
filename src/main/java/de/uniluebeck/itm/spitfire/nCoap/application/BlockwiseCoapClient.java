<<<<<<< /usr/src/app/output/okleine/ncoap/c30070ba7131b8a69119638d8e1109f140d22e85/src/main/java/de/uniluebeck/itm/spitfire/nCoap/application/BlockwiseCoapClient.java/left.java
package de.uniluebeck.itm.spitfire.nCoap.application;

import de.uniluebeck.itm.spitfire.nCoap.communication.blockwise.Blocksize;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.InvalidMessageException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.InvalidOptionException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.ToManyOptionsException;

import java.net.URI;
import java.net.URISyntaxException;

import static de.uniluebeck.itm.spitfire.nCoap.message.header.Code.*;
import static de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType.*;

/**
 * Created with IntelliJ IDEA.
 * User: olli
 * Date: 13.09.12
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class BlockwiseCoapClient extends CoapClientApplication{

    public static void main(String[] args) throws URISyntaxException, ToManyOptionsException, InvalidOptionException, InvalidMessageException {
        BlockwiseCoapClient client = new BlockwiseCoapClient();
        URI targetURI = new URI("coap://[fd00:db08:0:c0a1:215:8d00:14:8e82]:5683/rdf");
        CoapRequest request = new CoapRequest(CON, GET, targetURI, client);
        request.setMaxBlocksizeForResponse(Blocksize.SIZE_128);
        client.writeCoapRequest(request);

    }


}
||||||| /usr/src/app/output/okleine/ncoap/c30070ba7131b8a69119638d8e1109f140d22e85/src/main/java/de/uniluebeck/itm/spitfire/nCoap/application/BlockwiseCoapClient.java/base.java
package de.uniluebeck.itm.spitfire.nCoap.application;

import de.uniluebeck.itm.spitfire.nCoap.communication.blockwise.Blocksize;
import de.uniluebeck.itm.spitfire.nCoap.communication.callback.ResponseCallback;
import de.uniluebeck.itm.spitfire.nCoap.message.CoapRequest;
import de.uniluebeck.itm.spitfire.nCoap.message.InvalidMessageException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.InvalidOptionException;
import de.uniluebeck.itm.spitfire.nCoap.message.options.ToManyOptionsException;

import java.net.URI;
import java.net.URISyntaxException;

import static de.uniluebeck.itm.spitfire.nCoap.message.header.Code.*;
import static de.uniluebeck.itm.spitfire.nCoap.message.header.MsgType.*;

/**
 * Created with IntelliJ IDEA.
 * User: olli
 * Date: 13.09.12
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public class BlockwiseCoapClient extends CoapClientApplication{

    public static void main(String[] args) throws URISyntaxException, ToManyOptionsException, InvalidOptionException, InvalidMessageException {
        BlockwiseCoapClient client = new BlockwiseCoapClient();
        URI targetURI = new URI("coap://[fd00:db08:0:c0a1:215:8d00:14:8e82]:5683/rdf");
        CoapRequest request = new CoapRequest(CON, GET, targetURI, client);
        request.setMaxBlocksizeForResponse(Blocksize.SIZE_128);
        client.writeCoapRequest(request);

    }


}
=======
fatal: path 'src/main/java/de/uniluebeck/itm/spitfire/nCoap/application/BlockwiseCoapClient.java' exists on disk, but not in 'ad88f96ad06b9e2856a91bc9ff316b37df250f78'
>>>>>>> /usr/src/app/output/okleine/ncoap/c30070ba7131b8a69119638d8e1109f140d22e85/src/main/java/de/uniluebeck/itm/spitfire/nCoap/application/BlockwiseCoapClient.java/right.java
