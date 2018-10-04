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
package io.spring.batch.filegenerator.batch;

import java.io.IOException;
import java.util.List;

import io.spring.batch.filegenerator.domain.Store;
import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LayoutDetail;
import net.sf.JRecord.Details.Line;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.External.CopybookLoaderFactory;
import net.sf.JRecord.External.ExternalRecord;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.JRecord.IO.LineIOProvider;
import net.sf.JRecord.Numeric.Convert;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

/**
 * @author Michael Minella
 */
public class Dtar1000ItemWriter implements ResourceAwareItemWriterItemStream<Store> {

	private Resource outputFile;

	private Resource copybook;

	private AbstractLineWriter writer;

	private LayoutDetail layoutDetail;

	public Dtar1000ItemWriter(Resource copybook) {
		this.copybook = copybook;
	}

	@Override
	public void setResource(Resource resource) {
		this.outputFile = resource;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		try {
			String copybookName = this.copybook.getFile().getAbsolutePath();
			CopybookLoader loader = CopybookLoaderFactory.getInstance().getLoader(CopybookLoaderFactory.COBOL_LOADER);
			ExternalRecord externalLayout = loader.loadCopyBook(copybookName, CopybookLoader.SPLIT_NONE, 0,"CP037", Convert.FMT_MAINFRAME, 0, null);
			this.layoutDetail = externalLayout.asLayoutDetail();
			this.writer = LineIOProvider.getInstance().getLineWriter(Constants.IO_VB);
			this.writer.open(this.outputFile.getFile().getAbsolutePath());
		}
		catch (Exception e) {
			throw new ItemStreamException("An error occured while opening the file to write", e);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {

	}

	@Override
	public void close() throws ItemStreamException {
		try {
			this.writer.close();
		}
		catch (IOException e) {
			throw new ItemStreamException("An error occured while closing the file", e);
		}
	}

	@Override
	public void write(List<? extends Store> items) throws Exception {

		for (Store store : items) {
			AbstractLine record = new Line(this.layoutDetail);

			record.getFieldValue("STORE-NO").set(store.getStoreNumber());
			record.getFieldValue("REGION-NO").set(store.getRegionNumber());
			record.getFieldValue("STORE-NAME").set(store.getStoreName());
			record.getFieldValue("NEW-STORE").set(store.isNewStore()?"Y":"N");
			record.getFieldValue("ACTIVE-STORE").set(store.isActiveStore()?"Y":"N");
			record.getFieldValue("CLOSED-STORE").set(store.isClosedStore()?"Y":"N");
			record.getFieldValue("DC-TYPE").set(store.getDcType());
			record.getFieldValue("SRC-TYPE").set(store.getSrcType());
			record.getFieldValue("HO-TYPE").set(store.getHoType());

			writer.write(record);
		}
	}
}
