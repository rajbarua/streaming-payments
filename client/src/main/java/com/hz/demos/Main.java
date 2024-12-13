/*
 * Copyright (c) 2008-2024, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hz.demos;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.Functions;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.accumulator.LongAccumulator;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.metrics.JobMetrics;
import com.hazelcast.jet.core.metrics.Measurement;
import com.hazelcast.jet.core.metrics.MetricTags;
import com.hazelcast.jet.core.metrics.Metrics;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.ServiceFactories;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.test.TestSources;

public class Main {
	private static final String MY_METRIC_PREFIX = "raj-";
	private static final String STAGE1 = MY_METRIC_PREFIX + "stage1";
	private static final String STAGE2 = MY_METRIC_PREFIX + "stage2";
	
	public static void main(String[] args) throws Exception {
		Config config = new Config();
		
		config.getJetConfig().setEnabled(true).setResourceUploadEnabled(true);
		config.setLicenseKey(System.getenv("HZ_LICENSEKEY"));
		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

		// Pipeline pipeline = getSimplePipeline();
		Pipeline pipeline = getPipeline();
		JobConfig jobConfig = new JobConfig();
		
		Job job = hazelcastInstance.getJet().newJob(pipeline, jobConfig);
		// var metric = hazelcastInstance.getJet().getJob("acknckservice").getMetrics();
		TimeUnit.SECONDS.sleep(5L);
		
		// for (int i = 0; i < 60; i++) {
		// 	System.out.println("--- " + LocalTime.now());
			
		// 	JobMetrics jobMetrics = job.getMetrics();
		// 	jobMetrics.forEach(jobMetric -> {
		// 		if (jobMetric.getKey().startsWith(MY_METRIC_PREFIX)) {
		// 			List<Measurement> myMetrics = jobMetric.getValue();
		// 			myMetrics.forEach(measurement -> {
		// 				System.out.println(" '" + measurement.metric() + "' processor:" + measurement.tag(MetricTags.PROCESSOR) 
		// 						+ " ==" + measurement.value());
		// 				// System.out.println("\n****"+measurement.toString()+"****\n");
								
		// 			});
		// 		}
		// 	});
			
		// 	System.out.println("");
		// 	TimeUnit.SECONDS.sleep(5L);
		// }
				
		TimeUnit.MINUTES.sleep(60L);
		job.cancel();
		hazelcastInstance.shutdown();
    }

	private static Pipeline getPipeline() {
		return
		Pipeline
		.create()
		.readFrom(TestSources.itemStream(1, 
				(timestamp, sequence) -> 
					Tuple2.tuple2(new String("K" + (sequence % 2)), new String(sequence + "," + timestamp))
					)).withIngestionTimestamps()
		.groupingKey(Functions.entryKey())
		.mapUsingService(
                ServiceFactories.sharedService(__ -> {
                	Object[] tally = new Object[2];
                    tally[0] = new LongAccumulator(0L);
                    tally[1] = new LongAccumulator(0L);
                    return tally;
                }),
                (tally, key, entry) -> {
                	long start = System.currentTimeMillis();
                	LongAccumulator timer = (LongAccumulator) tally[0];
                	LongAccumulator counter = (LongAccumulator) tally[1];
                	counter.add(1L);
                	try {
                		TimeUnit.MILLISECONDS.sleep(10);
                		// TimeUnit.MILLISECONDS.sleep(10 * counter.get());
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                	long elapsed = System.currentTimeMillis() - start;
                	tally[0] = timer.add(elapsed);
                	tally[1] = counter;
                	Metrics.metric(STAGE1+"::Counter").increment();//1, 2, 3, 4
                	Metrics.metric(STAGE1+"::Timer").increment(elapsed);//0, 10, 20, 30
                	return entry;
                }
				).setName(STAGE1)
		.mapStateful(
				LongAccumulator::new,
				(LongAccumulator state, Tuple2<String, String> tuple2) -> {
					state.add(1L);
					Metrics.metric(STAGE2). increment(state.get());
					return tuple2;
				}
				).setName(STAGE2)
		//.writeTo(Sinks.logger(o -> "===> " + o))
		.writeTo(Sinks.noop())
		.getPipeline();
	}

	private static Pipeline getSimplePipeline() {
		return
		Pipeline
		.create()
		.readFrom(TestSources.itemStream(1)).withIngestionTimestamps()
		.mapUsingService(
                ServiceFactories.sharedService(__ -> {
                	Object[] tally = new Object[2];
                    tally[0] = new LongAccumulator(0L);
                    tally[1] = new LongAccumulator(0L);
                    return tally;
                }),
                (tally, entry) -> {
                	long start = System.currentTimeMillis();
                	try {
                		TimeUnit.MILLISECONDS.sleep(10);
                	} catch (Exception e) {
                		e.printStackTrace();
                	}
                	long elapsed = System.currentTimeMillis() - start;
                	Metrics.metric(STAGE1+"-Counter").increment();//1, 2, 3, 4
                	Metrics.metric(STAGE1+"-Timer").increment(elapsed);//0, 10, 20, 30
                	return entry;
                }
				).setName(STAGE1)
		// .mapStateful(
		// 		LongAccumulator::new,
		// 		(LongAccumulator state, Tuple2<String, String> tuple2) -> {
		// 			state.add(1L);
		// 			// Metrics.metric(STAGE2). increment(state.get());
		// 			return tuple2;
		// 		}
		// 		).setName(STAGE2)
		//.writeTo(Sinks.logger(o -> "===> " + o))
		.writeTo(Sinks.noop())
		.getPipeline();
	}
}