/* 
 * NOTICE OF LICENSE
 * 
 * This source file is subject to the Open Software License (OSL 3.0) that is 
 * bundled with this package in the file LICENSE.txt. It is also available 
 * through the world-wide-web at http://opensource.org/licenses/osl-3.0.php
 * If you did not receive a copy of the license and are unable to obtain it 
 * through the world-wide-web, please send an email to pdiffenderfer@gmail.com 
 * so we can send you a copy immediately. If you use any of this software please
 * notify me via my website or email, your feedback is much appreciated. 
 * 
 * @copyright   Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * @license     http://opensource.org/licenses/osl-3.0.php
 * 				Open Software License (OSL 3.0)
 */

package org.magnos.net;

import org.magnos.io.bytes.ByteReader;
import org.magnos.io.bytes.ByteWriter;

/**
 * A protocol decodes an object from a ByteReader and encodes an object to a
 * ByteWriter.
 * 
 * @author Philip Diffenderfer
 *
 * @param <D>
 * 		The data encoded and decoded.
 */
public interface Protocol<D> 
{
	
	/**
	 * Returns the data type of the protocol.
	 * 
	 * @return
	 * 		The class of the data.
	 */
	public Class<D> getType();
	
	/**
	 * Attempts to decode an object of type D from the given ByteReader. If
	 * there is not enough data in the ByteReader null should be returned. If
	 * an object has been successfully decoded the ByteReader should be synced
	 * to avoid receiving the same message indefinitely. For example:
	 * <pre>
	 * D result = new D();
	 * // ... invokes various get methods of in
	 * if (in.isValid()) {
	 *     in.sync();
	 *     return result;
	 * }
	 * return null;
	 * </pre>
	 * If the input is indecodable by this protocol the appropriate methods
	 * should be invoked to the client (maybe change the protocol or close it).
	 * 
	 * @param in
	 * 		The reader to use to decode the object.
	 * @param client
	 * 		The client which is decoding objects from its input.
	 * @return
	 * 		The object decoded or null if an object could 
	 */
	public D decode(ByteReader in, Client client);
	
	/**
	 * Encodes an object of type D to the given ByteWriter. Once the data is 
	 * written the protocol does not have to invoke the flush method, that will
	 * be handled by the server/client encoding the object.
	 * 
	 * @param out
	 * 		The writer to use to encode the object.
	 * @param data
	 * 		The object to encode.
	 * @param client
	 * 		The client which is encoding data to its output.
	 */
	public void encode(ByteWriter out, D data, Client client);
	
	/**
	 * Returns the listener to this protocol.
	 * 
	 * @return
	 * 		The reference to the listener of this protocol.
	 */
	public ProtocolListener<D> getListener();
	
	/**
	 * Sets the listener to this protocol.
	 * 
	 * @param listener
	 * 		The new listener to this protocol.
	 */
	public void setListener(ProtocolListener<D> listener);
	
}