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

package org.magnos.net.oio;

import org.magnos.net.AbstractSelector;


public class OioSelector extends AbstractSelector
{

	public OioSelector() 
	{
		super(new OioAcceptorFactory(), new OioWorkerFactory());
		
		acceptorPool.setMinCapacity(0);
		acceptorPool.setMaxCapacity(10);
		acceptorPool.setAllocateSize(1);
		acceptorPool.setDeallocateSize(1);
		
		workerPool.setMinCapacity(1);
		workerPool.setMaxCapacity(100);
		workerPool.setAllocateSize(4);
		workerPool.setDeallocateSize(1);
	}
	
}
