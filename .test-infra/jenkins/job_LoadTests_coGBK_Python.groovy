/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import CommonJobProperties as commonJobProperties
import CommonTestProperties
import LoadTestsBuilder as loadTestsBuilder
import PhraseTriggeringPostCommitBuilder
import CronJobBuilder

def now = new Date().format("MMddHHmmss", TimeZone.getTimeZone('UTC'))

def loadTestConfigurations = { datasetName -> [
        [
                title        : 'CoGroupByKey Python Load test: 2GB of 100B records with a single key',
                itClass      : 'apache_beam.testing.load_tests.co_group_by_key_test:CoGroupByKeyTest.testCoGroupByKey',
                runner       : CommonTestProperties.Runner.DATAFLOW,
                jobProperties: [
                        project              : 'apache-beam-testing',
                        job_name             : 'load-tests-python-dataflow-batch-cogbk-1-' + now,
                        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
                        publish_to_big_query : true,
                        metrics_dataset      : datasetName,
                        metrics_table        : "python_dataflow_batch_cogbk_1",
                        input_options        : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 1,' +
                                '"hot_key_fraction": 1}\'',
                        co_input_options      : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 1,' +
                                '"hot_key_fraction": 1}\'',
                        iterations           : 1,
                        max_num_workers      : 5,
                        num_workers          : 5,
                        autoscaling_algorithm: 'NONE'
                ]
        ],
        [
                title        : 'CoGroupByKey Python Load test: 2GB of 100B records with multiple keys',
                itClass      : 'apache_beam.testing.load_tests.co_group_by_key_test:CoGroupByKeyTest.testCoGroupByKey',
                runner       : CommonTestProperties.Runner.DATAFLOW,
                jobProperties: [
                        project              : 'apache-beam-testing',
                        job_name             : 'load-tests-python-dataflow-batch-cogbk-2-' + now,
                        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
                        publish_to_big_query : true,
                        metrics_dataset      : datasetName,
                        metrics_table        : 'python_dataflow_batch_cogbk_2',
                        input_options        : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 5,' +
                                '"hot_key_fraction": 1}\'',
                        co_input_options      : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 5,' +
                                '"hot_key_fraction": 1}\'',
                        iterations           : 1,
                        max_num_workers      : 5,
                        num_workers          : 5,
                        autoscaling_algorithm: 'NONE'
                ]
        ],
        [
                title        : 'CoGroupByKey Python Load test: reiterate 4 times 10kB values',
                itClass      : 'apache_beam.testing.load_tests.co_group_by_key_test:CoGroupByKeyTest.testCoGroupByKey',
                runner       : CommonTestProperties.Runner.DATAFLOW,
                jobProperties: [
                        project              : 'apache-beam-testing',
                        job_name             : 'load-tests-python-dataflow-batch-cogbk-3-' + now,
                        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
                        publish_to_big_query : true,
                        metrics_dataset      : datasetName,
                        metrics_table        : "python_dataflow_batch_cogbk_3",
                        input_options        : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 200000,' +
                                '"hot_key_fraction": 1}\'',
                        co_input_options      : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 200000,' +
                                '"hot_key_fraction": 1}\'',
                        iterations           : 4,
                        max_num_workers      : 5,
                        num_workers          : 5,
                        autoscaling_algorithm: 'NONE'
                ]
        ],
        [
                title        : 'CoGroupByKey Python Load test: reiterate 4 times 2MB values',
                itClass      : 'apache_beam.testing.load_tests.co_group_by_key_test:CoGroupByKeyTest.testCoGroupByKey',
                runner       : CommonTestProperties.Runner.DATAFLOW,
                jobProperties: [
                        project              : 'apache-beam-testing',
                        job_name             : 'load-tests-python-dataflow-batch-cogbk-4-' + now,
                        temp_location        : 'gs://temp-storage-for-perf-tests/loadtests',
                        publish_to_big_query : true,
                        metrics_dataset      : datasetName,
                        metrics_table        : 'python_dataflow_batch_cogbk_4',
                        input_options        : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 1000,' +
                                '"hot_key_fraction": 1}\'',
                        co_input_options      : '\'{' +
                                '"num_records": 20000000,' +
                                '"key_size": 10,' +
                                '"value_size": 90,' +
                                '"num_hot_keys": 1000,' +
                                '"hot_key_fraction": 1}\'',
                        iterations           : 4,
                        max_num_workers      : 5,
                        num_workers          : 5,
                        autoscaling_algorithm: 'NONE'
                ]
        ],
]}

def batchLoadTestJob = { scope, triggeringContext ->
    scope.description('Runs Python CoGBK load tests on Dataflow runner in batch mode')
    commonJobProperties.setTopLevelMainJobProperties(scope, 'master', 240)

    def datasetName = loadTestsBuilder.getBigQueryDataset('load_test', triggeringContext)
    for (testConfiguration in loadTestConfigurations(datasetName)) {
        loadTestsBuilder.loadTest(scope, testConfiguration.title, testConfiguration.runner, CommonTestProperties.SDK.PYTHON, testConfiguration.jobProperties, testConfiguration.itClass)
    }
}

CronJobBuilder.cronJob('beam_LoadTests_Python_CoGBK_Dataflow_Batch', 'H 16 * * *', this) {
    batchLoadTestJob(delegate, CommonTestProperties.TriggeringContext.POST_COMMIT)
}

PhraseTriggeringPostCommitBuilder.postCommitJob(
        'beam_LoadTests_Python_CoGBK_Dataflow_Batch',
        'Run Load Tests Python CoGBK Dataflow Batch',
        'Load Tests Python CoGBK Dataflow Batch suite',
        this
) {
    batchLoadTestJob(delegate, CommonTestProperties.TriggeringContext.PR)
}
