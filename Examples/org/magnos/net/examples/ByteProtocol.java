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

package org.magnos.net.examples;


import org.magnos.io.bytes.ByteReader;
import org.magnos.io.bytes.ByteWriter;
import org.magnos.net.AbstractProtocol;
import org.magnos.net.Client;


/**
 * A simple protocol that uses Strings as the medium for communication.
 * 
 * @author Philip Diffenderfer
 *
 */
public class ByteProtocol extends AbstractProtocol<byte[]> 
{
	
	/**
	 * Instantiates a new ByteProtocol.
	 */
	public ByteProtocol() 
	{
		super(byte[].class);
	}

	/**
	 * Decodes a string from the given input.
	 */
	@Override
	public byte[] decode(ByteReader in, Client client) 
	{
		String magic = in.getString(4);
		
		if (in.isValid() && !magic.equals("Wo0t")) {
			client.disconnect();
			return null;
		}
		
		byte[] bytes = in.getByteArray();

		if (in.isValid()) {
			in.sync();
		}
		
		return bytes;
	}

	/**
	 * Encodes a string to the given output.
	 */
	@Override
	public void encode(ByteWriter out, byte[] data, Client client) 
	{
		out.putBytes("Wo0t".getBytes());
		out.putByteArray(data);
	}
}