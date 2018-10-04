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
package io.spring.batch.customeringestion.configuration;

import java.io.IOException;
import javax.sql.DataSource;

import io.spring.batch.customeringestion.batch.FixedWidthCobolItemReader;
import io.spring.batch.customeringestion.domain.Store;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

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

	@Bean
	@StepScope
	public FixedWidthCobolItemReader<Store> reader(
			@Value("file:#{jobParameters['localFilePath']}") Resource inputFile,
			@Value("${job.copybook}") Resource copybook) throws Exception {
		FixedWidthCobolItemReader<Store> itemReader =
				new FixedWidthCobolItemReader<>(inputFile, copybook, line -> {
			Store store = new Store();

			store.setStoreNumber(line.getFieldValue("STORE-NO").asInt());
			store.setRegionNumber(line.getFieldValue("REGION-NO").asInt());
			store.setStoreName(line.getFieldValue("STORE-NAME").asString());
			store.setNewStore(line.getFieldValue("NEW-STORE").asString().equals("Y"));
			store.setActiveStore(line.getFieldValue("ACTIVE-STORE").asString().equals("Y"));
			store.setClosedStore(line.getFieldValue("CLOSED-STORE").asString().equals("Y"));
			store.setDcType(line.getFieldValue("DC-TYPE").asString());
			store.setSrcType(line.getFieldValue("SRC-TYPE").asString());
			store.setHoType(line.getFieldValue("HO-TYPE").asString());

			return store;
		});

		itemReader.setName("cobolReader");

		return itemReader;
	}

	@Bean
	public JdbcBatchItemWriter<Store> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Store>()
				.dataSource(dataSource)
				.beanMapped()
				.sql("INSERT INTO STORE (STORE_NUMBER, " +
						"REGION_NUMBER, " +
						"STORE_NAME, " +
						"NEW_STORE, " +
						"ACTIVE_STORE, " +
						"CLOSED_STORE, " +
						"DC_TYPE, " +
						"SRC_TYPE, " +
						"HO_TYPE) VALUES (:storeNumber, " +
						":regionNumber, " +
						":storeName, " +
						":newStore, " +
						":activeStore, " +
						":closedStore, " +
						":dcType, " +
						":srcType, " +
						":hoType)")
				.build();
	}

	@Bean
	@JobScope
	public JobExecutionListener listener(
			@Value("file:#{jobParameters['localFilePath']}") Resource inputFile) {
		return new JobExecutionListener() {
			@Override
			public void beforeJob(JobExecution jobExecution) {

			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				if(jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
					try {
						inputFile.getFile().delete();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	@Bean
	public Job ingestJob() throws Exception {
		return this.jobBuilderFactory.get("ingestJob")
				.start(step1())
				.listener(listener(null))
				.build();
	}

	@Bean
	public Step step1() throws Exception {
		return this.stepBuilderFactory.get("step1")
				.<Store, Store>chunk(100)
				.reader(reader(null, null))
				.writer(writer(null))
				.build();
	}
}
