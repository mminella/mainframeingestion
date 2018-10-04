/**
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
package io.spring.batch.filegenerator.configuration;

import java.util.Random;

import io.spring.batch.filegenerator.batch.Dtar1000ItemWriter;
import io.spring.batch.filegenerator.domain.Store;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Michael Minella
 */
@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	private Random random = new Random();

	private String [] storeNameSuffixes = new String [] {"Plaza",
			"Mall",
			"Center",
			"Centre",
			"Court",
			"Promenade",
			"Shops",
			"Place",
			"Outlet Mall",
			"Commons",
			"Shopping Center",
			"Shopping Centre",
			""};

	private String [] storeNamePrefixes = new String[] {"900 North Michigan",
			"Water Tower",
			"Oakbrook",
			"Fox Valley",
			"Old Orchard",
			"Stratford",
			"Yorktown",
			"Charlestown",
			"Geneva",
			"Alton Square",
			"Cherryvale",
			"College Hills",
			"Cross County",
			"Eastland",
			"Hickory Point",
			"Illinois Star",
			"Louis Joilet",
			"Machesney Park",
			"Market",
			"Northfield",
			"Northwoods",
			"Old Chicago",
			"Peru",
			"Quincy",
			"Woodfield",
			"SouthPark",
			"St. Clair",
			"University",
			"Urbana-Lincoln Hotel-Lincoln",
			"Village",
			"White Oaks",
			"Water Tower",
			"Westlake",
			"East Court",
			"The Arboretum",
			"River Oaks",
			"Junction City",
			"Metro",
			"Times Square",
			"Campustown",
			"Town Square",
			"Chicago Ridge",
			"Deer Park",
			"Danville Village",
			"Hawthorn",
			"Bourbonnais Towne",
			"Gurnee",
			"Orland Square",
			"Elmhurst City",
			"Northwestern",
			"Lombard Town",
			"Quincy",
			"Bradley",
			"Orland Park",
			"Harlem Irving",
			"Spring Hill"};

	private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private int itemCount = 0;

	@Bean
	public ItemReader<Store> reader() {
		return () -> {
			if(itemCount > 1000000) {
				return null;
			}
			itemCount++;
			Store curStore = new Store();

			curStore.setStoreNumber(random.nextInt(9999));
			curStore.setRegionNumber(random.nextInt(9) * 10);
			curStore.setStoreName(storeNamePrefixes[random.nextInt(storeNamePrefixes.length - 1)] + " " + storeNameSuffixes[random.nextInt(storeNameSuffixes.length - 1)]);
			curStore.setNewStore(random.nextBoolean());
			curStore.setActiveStore(random.nextBoolean());
			curStore.setClosedStore(!curStore.isActiveStore());
			curStore.setDcType(String.valueOf(alphabet.charAt(random.nextInt(25))));
			curStore.setSrcType(String.valueOf(alphabet.charAt(random.nextInt(25))));
			curStore.setHoType(String.valueOf(alphabet.charAt(random.nextInt(25))));

			return curStore;
		};
	}

	@Bean
	public MultiResourceItemWriter<Store> multiResourceItemWriter() {
		return new MultiResourceItemWriterBuilder<Store>()
					.name("itemWriter")
					.delegate(writer())
					.itemCountLimitPerResource(40000)
					.resource(new FileSystemResource("/Users/mminella/Documents/IntelliJWorkspace/mainframeingestion/file-generator/target/data/DTAR1000.bin"))
					.resourceSuffixCreator(index -> "_" + index + ".bin")
					.build();
	}

	@Bean
	public Dtar1000ItemWriter writer() {
		Dtar1000ItemWriter dtar1000ItemWriter = new Dtar1000ItemWriter(new ClassPathResource("DTAR1000.cbl"));
		dtar1000ItemWriter.setResource(new FileSystemResource("/Users/mminella/Documents/IntelliJWorkspace/mainframeingestion/file-generator/target/data/DTAR1000_1.bin"));

		return dtar1000ItemWriter;
	}

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("job")
				.start(step1())
				.build();
	}

	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
				.<Store, Store>chunk(1000)
				.reader(reader())
				.writer(multiResourceItemWriter())
				.build();
	}
}
