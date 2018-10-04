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
package io.spring.batch.filegenerator;

import java.util.Random;

import net.sf.JRecord.Common.Constants;
import net.sf.JRecord.Details.AbstractLine;
import net.sf.JRecord.Details.LayoutDetail;
import net.sf.JRecord.Details.Line;
import net.sf.JRecord.External.CopybookLoader;
import net.sf.JRecord.External.CopybookLoaderFactory;
import net.sf.JRecord.External.ExternalRecord;
import net.sf.JRecord.IO.AbstractLineReader;
import net.sf.JRecord.IO.AbstractLineWriter;
import net.sf.JRecord.IO.CobolIoProvider;
import net.sf.JRecord.IO.LineIOProvider;
import net.sf.JRecord.Numeric.Convert;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Michael Minella
 */
public class JunkTests {

	private Random random = new Random();

	@Test
	public void testWhatTheFuckIsGoingOn() throws Exception {
		CobolIoProvider ioProvider = CobolIoProvider.getInstance();
		AbstractLineReader reader = ioProvider.getLineReader(Constants.IO_VB,
				Convert.FMT_MAINFRAME,
				CopybookLoader.SPLIT_NONE,
				new ClassPathResource("DTAR1000.cbl").getFile().getAbsolutePath(),
				new FileSystemResource("/Applications/RecordEdit/Generic/SampleFiles/DTAR1000_Store_file_std.bin").getFile().getAbsolutePath());

		AbstractLine line = reader.read();
	}

	@Test
	public void testWritingWithThisCrap() throws Exception {

		int counter = 0;

		String fileOut ="DTAR1000_i_hope_this_works.bin";
		String copybookName = new ClassPathResource("DTAR1000.cbl").getFile().getAbsolutePath();
		CopybookLoader loader = CopybookLoaderFactory.getInstance().getLoader(CopybookLoaderFactory.COBOL_LOADER);
		ExternalRecord externalLayout = loader.loadCopyBook(copybookName, CopybookLoader.SPLIT_NONE, 0,"CP037", Convert.FMT_MAINFRAME, 0, null);
		LayoutDetail layout = externalLayout.asLayoutDetail();
		AbstractLineWriter writer = LineIOProvider.getInstance().getLineWriter(Constants.IO_VB);
		writer.open(fileOut);

		for(int i = 0; i < 100; i++) {
			AbstractLine record = new Line(layout);

			record.getFieldValue("STORE-NO").set(counter++);
			record.getFieldValue("REGION-NO").set(random.nextInt(9) * 10);
			record.getFieldValue("STORE-NAME").set("Store name " + random.nextInt());
			record.getFieldValue("NEW-STORE").set(random.nextBoolean()?"Y":"N");
			String active = random.nextBoolean() ? "Y" : "N";
			record.getFieldValue("ACTIVE-STORE").set(active);
			record.getFieldValue("CLOSED-STORE").set(active.equalsIgnoreCase("Y")?"N":"Y");
			record.getFieldValue("DC-TYPE").set("A");
			record.getFieldValue("SRC-TYPE").set("B");
			record.getFieldValue("HO-TYPE").set("C");

			writer.write(record);
		}

		writer.close();
	}
}
