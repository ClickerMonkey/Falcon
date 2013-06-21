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

import java.util.Set;

import org.magnos.resource.Resource;
import org.magnos.util.ConcurrentSet;



/**
 * An acceptor is a service which handles a set of servers. The acceptor will
 * process the accepting of clients by its servers. An acceptor can either 
 * handle a single server or an infinite number of servers depending on the
 * underlying I/O.
 * 
 * @author Philip Diffenderfer
 *
 */
public interface Acceptor extends Resource
{
	
	/**
	 * Returns the set of servers this Acceptor is handling. This should only
	 * be used for iterative purposes, it returns the internal set managed by
	 * the Acceptor.
	 * 
	 * @return
	 * 		The reference to the set of servers handled by this Acceptor.
	 * @see ConcurrentSet
	 */
	public Set<Server> getServers();
	
}
