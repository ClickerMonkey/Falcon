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

package org.magnos.net.nio;

import static org.junit.Assert.*;

import org.junit.Test;
import org.magnos.net.nio.NioSelector;
import org.magnos.test.BaseTest;


public class TestNioSelector extends BaseTest
{

	@Test
	public void testDefault()
	{
		NioSelector selector = new NioSelector();
		assertEquals( 0, selector.getAcceptors().getResourceCount() );
		assertEquals( 0, selector.getWorkers().getResourceCount() );
		
		selector.start();
		
		assertEquals( 0, selector.getAcceptors().getResourceCount() );
		assertEquals( 1, selector.getWorkers().getResourceCount() );
		
		selector.stop();
		
		assertEquals( 0, selector.getAcceptors().getResourceCount() );
		assertEquals( 0, selector.getWorkers().getResourceCount() );
	}
	
	@Test
	public void testWorkers()
	{
		NioSelector selector = new NioSelector();
		selector.getWorkers().setCapacity(3);
		selector.start();

		assertEquals( 0, selector.getAcceptors().getResourceCount() );
		assertEquals( 3, selector.getWorkers().getResourceCount() );
		
		selector.stop();
		
		assertEquals( 0, selector.getAcceptors().getResourceCount() );
		assertEquals( 0, selector.getWorkers().getResourceCount() );
	}
	
	@Test
	public void testAcceptors()
	{
		NioSelector selector = new NioSelector();
		selector.getWorkers().setCapacity(0);
		selector.getAcceptors().setCapacity(2);
		selector.start();

		assertEquals( 2, selector.getAcceptors().getResourceCount() );
		assertEquals( 0, selector.getWorkers().getResourceCount() );
		
		selector.stop();
		
		assertEquals( 0, selector.getAcceptors().getResourceCount() );
		assertEquals( 0, selector.getWorkers().getResourceCount() );
	}
	
}
