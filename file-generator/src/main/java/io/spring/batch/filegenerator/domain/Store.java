/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.batch.filegenerator.domain;

/**
 * @author Michael Minella
 */
public class Store {

	private int storeNumber;
	private int regionNumber;
	private String storeName;
	private boolean newStore;
	private boolean activeStore;
	private boolean closedStore;
	private String dcType;
	private String srcType;
	private String hoType;

	public int getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(int storeNumber) {
		this.storeNumber = storeNumber;
	}

	public int getRegionNumber() {
		return regionNumber;
	}

	public void setRegionNumber(int regionNumber) {
		this.regionNumber = regionNumber;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public boolean isNewStore() {
		return newStore;
	}

	public void setNewStore(boolean newStore) {
		this.newStore = newStore;
	}

	public boolean isActiveStore() {
		return activeStore;
	}

	public void setActiveStore(boolean activeStore) {
		this.activeStore = activeStore;
	}

	public boolean isClosedStore() {
		return closedStore;
	}

	public void setClosedStore(boolean closedStore) {
		this.closedStore = closedStore;
	}

	public String getDcType() {
		return dcType;
	}

	public void setDcType(String dcType) {
		this.dcType = dcType;
	}

	public String getSrcType() {
		return srcType;
	}

	public void setSrcType(String srcType) {
		this.srcType = srcType;
	}

	public String getHoType() {
		return hoType;
	}

	public void setHoType(String hoType) {
		this.hoType = hoType;
	}
}
