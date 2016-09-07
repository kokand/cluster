/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.cache;

import org.apache.log4j.Logger;
import org.infinispan.Cache;
import org.restcomm.cache.tree.Fqn;
import org.restcomm.cache.tree.Node;


/**
 * Common base proxy for runtime cached data. 
 * @author martins
 * @author András Kőkuti
 *
 */
public class CacheData {

	private static final String IS_REMOVED_CACHE_NODE_MAP_KEY = "isremoved";
	
	private static final Logger logger = Logger.getLogger(CacheData.class);
	
	
	@SuppressWarnings("rawtypes")
	private Node node;
	
	//private final Fqn nodeFqn;
	
	
	
	private boolean isRemoved;
	private final MobicentsCache mobicentsCache;
	
	private final static boolean doTraceLogs = logger.isTraceEnabled();  
	
	public CacheData(Fqn nodeFqn, MobicentsCache mobicentsCache) {		
		//this.nodeFqn = nodeFqn;	
		this.mobicentsCache = mobicentsCache;
		//this.node = mobicentsCache.getJBossCache().getRoot().getChild(nodeFqn);
		
		this.node = new Node(this.mobicentsCache.getJBossCache(), nodeFqn);
		
		if (doTraceLogs) {
			logger.trace("cache node "+nodeFqn+" retrieved, result = "+this.node);
		}
		logger.info("cache node "+nodeFqn+" retrieved, result = "+this.node);
	}
	

	
	/**
	 * Verifies if node where data is stored exists in cache
	 * @return
	 */
	public boolean exists() {
		return this.node.exists();
		
		
	}

	/**
	 * Creates node to hold data in cache
	 */
	public boolean create() {
		if (!exists()) {
			//node = mobicentsCache.getJBossCache().getRoot().addChild(nodeFqn);
			//node.put(IS_REMOVED_CACHE_NODE_MAP_KEY, false);
			
			this.node.create();
			
			if (doTraceLogs) {
				logger.trace("created cache node "+ node);
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns true if it was requested to remove the data from cache
	 * @return
	 */
	public boolean isRemoved() {
		return isRemoved;
	}
	
	/**
	 * Removes node that holds data in cache
	 */
	public boolean remove() {
		if (exists() && !isRemoved()) {
			isRemoved = true;
			/*node.clearData();
			node.put(IS_REMOVED_CACHE_NODE_MAP_KEY, true);
			if (doTraceLogs) {
				logger.trace("removed cache node "+ node);
			}
			
			node = null;
			return true;*/
			this.node.remove();
			
			
			if (doTraceLogs) {
				logger.trace("removed cache node "+ node);
			}			
			
			return true;
			
		}
		else {
			return false;
		}
	}
	
	/**
	 * 
	 * Retrieves the cache {@link Node} which holds the data in cache
	 * 
	 * Throws {@link IllegalStateException} if remove() was invoked
	 */
	
	protected Node getNode() {
		if (isRemoved()) {
			throw new IllegalStateException();
		}
		return node;
	}
	
	/**
	 * 
	 * @return
	 */
	public MobicentsCache getMobicentsCache() {
		return mobicentsCache;
	}
	
	/**
	 * Retrieves the node fqn
	 * @return the nodeFqn
	 */
	
	public Fqn getNodeFqn() {
		return this.node.getNodeFqn();
	}
}