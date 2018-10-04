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
package io.spring.batch.customeringestion.batch;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.Numeric.Convert;

import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;

/**
 * @author Michael Minella
 */
public class FixedWidthCobolItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> {

	private Resource copybookFile;

	private Resource inputFile;

	private LineMapper<T> lineMapper;

	private AbstractLineReader reader;

	public FixedWidthCobolItemReader(Resource inputFile, Resource copybookFile, LineMapper<T> lineMapper) throws Exception {

		this.copybookFile = copybookFile;
		this.inputFile = inputFile;
		this.lineMapper = lineMapper;
	}

	@Override
	protected T doRead() throws Exception {
		AbstractLine line = this.reader.read();

		if(line != null) {
			return this.lineMapper.mapLine(line);
		}
		else {
			return null;
		}
	}

	@Override
	protected void doOpen() throws Exception {
		CobolIoProvider ioProvider = CobolIoProvider.getInstance();
		this.reader = ioProvider.getLineReader(Constants.IO_VB,
				Convert.FMT_MAINFRAME,
				CopybookLoader.SPLIT_NONE,
				copybookFile.getFile().getAbsolutePath(),
				inputFile.getFile().getAbsolutePath());
	}

	@Override
	protected void doClose() throws Exception {
		if(this.reader != null) {
			this.reader.close();
		}
	}
}
