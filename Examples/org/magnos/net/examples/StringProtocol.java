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
public class StringProtocol extends AbstractProtocol<String> 
{
	
	/**
	 * Instantiates a new String Protocol. 
	 */
	public StringProtocol() 
	{
		super(String.class);
	}

	/**
	 * Decodes a string from the given input.
	 */
	@Override
	public String decode(ByteReader in, Client client) 
	{
		// Attempt to read in a string. The length of the string is read in
		// with this method.
		String msg = in.getString();
		// If there was enough data to read the string in...
		if (in.isValid()) {
			// Discard the read bytes.
			in.sync();
		}
		// Return the read string. This will be null if it was invalid.
		return msg;
	}

	/**
	 * Encodes a string to the given output.
	 */
	@Override
	public void encode(ByteWriter out, String data, Client client) 
	{
		// Writes the string and its length
		out.putString(data);
	}
}