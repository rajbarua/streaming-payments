/*
 * Copyright (c) 2008-2023, Hazelcast, Inc. All Rights Reserved.
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

package com.hz.demo.pmt;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>Constants shared across the application.
 * </p>
 */
public class MyConstants {
	// Prefix for maps, etc, so can use same demo in shared cluster. Acts as a namespace.
	public static final String PREFIX = "";
	
	// For Mappings. Also needs to match fields in Finos.js
	public static final double BIC_INITIAL_LIMIT = 100_000_000.00d;
	public static final String BIC_FIELD_AMOUNT = "Amount";
	public static final String BIC_FIELD_BIC = "Bank Code";
	public static final String BIC_FIELD_DESCRIPTION = "Description";
	public static final String BIC_FIELD_AVAILABLE_FUNDS = "Available Funds";
	public static final String BIC_FIELD_PROCESSED_FUNDS = "Processed Funds";
	public static final String BIC_FIELD_PMTINFID = "Payment Information Id";
	public static final String BIC_FIELD_PROCESSED_PMTINFID = "Processed Payment Information Id";
	public static final String BIC_FIELD_PROCESSED_BIC_ID_CR = "Credit Bank";
	public static final String BIC_FIELD_PROCESSED_BIC_ID_DR = "Debit Bank";
	public static final String BIC_FIELD_PROCESSED_APPLIED_CR = "Credit Applied";
	public static final String BIC_FIELD_PROCESSED_CTRL_NO_CR = "Credit Control Sequence Number";
	public static final String BIC_FIELD_PROCESSED_TIMESTAMP_CR = "Processed Credit Timestamp";
	public static final String BIC_FIELD_PROCESSED_APPLIED_DR = "Debit Applied";
	public static final String BIC_FIELD_PROCESSED_CTRL_NO_DR = "Debit Control Sequence Number";
	public static final String BIC_FIELD_PROCESSED_TIMESTAMP_DR = "Processed Debit Timestamp";
	public static final String BIC_FIELD_SETTLED_FUNDS = "Settled Funds";
	
	// Maps
	public static final String IMAP_BICS_EMBARGOED = PREFIX + "Embargoed Banks";
	public static final String IMAP_BICS_STATUS = PREFIX + "Bank Status";
	public static final String IMAP_CDTTRFTX_DUPLICATES = PREFIX + "Credit Transfers - Duplicates";
	public static final String IMAP_CDTTRFTX_GRPHDR = PREFIX + "Credit Transfers - Grouped Payment Header";
	public static final String IMAP_CDTTRFTX_PMTINF_APPLIED = PREFIX + "Payment Information - History";
	public static final String IMAP_CDTTRFTX_PMTINF_APPLY_CONTROL = PREFIX + "Payment Information - Inflight";
	public static final String IMAP_CDTTRFTX_PMTINF_APPROVED = PREFIX + "Payment Information - Approved";
	public static final String IMAP_CDTTRFTX_PMTINF_EMBARGOED = PREFIX + "Payment Information - Rejected - Embargoed";
	public static final String IMAP_CDTTRFTX_PMTINF_INSUFFICIENT_FUNDS = PREFIX + "Payment Information - Rejected - Insufficient Funds";
	public static final String IMAP_CDTTRFTX_PMTINF_REPORTING = PREFIX + "Payment Information - Reporting";
	public static final String IMAP_FAILED_PMTINF_APPLY_CONTROL = PREFIX + "Payment Information - Failed";
	public static final String IMAP_KAFKA_CONFIG = PREFIX + "Kafka Config";
	public static final String IMAP_TRANSACTION_STORE = PREFIX + "Transaction Store";
	public static final List<String> IMAPS = List.of(
			IMAP_BICS_EMBARGOED, IMAP_BICS_STATUS,
			IMAP_CDTTRFTX_DUPLICATES, IMAP_CDTTRFTX_GRPHDR, 
			IMAP_CDTTRFTX_PMTINF_APPLIED,
			IMAP_CDTTRFTX_PMTINF_APPLY_CONTROL,
			IMAP_CDTTRFTX_PMTINF_APPROVED,
			IMAP_CDTTRFTX_PMTINF_EMBARGOED,
			IMAP_CDTTRFTX_PMTINF_INSUFFICIENT_FUNDS,
			IMAP_CDTTRFTX_PMTINF_REPORTING,
			IMAP_FAILED_PMTINF_APPLY_CONTROL,
			IMAP_KAFKA_CONFIG, IMAP_TRANSACTION_STORE
			);
	public static final String IMAP_PAIN_PARSE_ERROR = PREFIX + "Parse Errors";
	public static final String IMAP_PAIN_CRD_DTL = PREFIX + "Credit Details";
	public static final String IMAP_PAIN_CDRTRFTXINF_DEDUP = PREFIX + "CDRTRFTXINF Dedup";
	public static final String IMAP_PAIN_CDRTRFTXINF_INPROC_DEDUP = PREFIX + "CDRTRFTXINF In Process Dedup";
	public static final String IMAP_PAIN_CDRTRFTXINF_DUPLICATE = PREFIX + "CDRTRFTXINF Duplicate";

	// See topic-create module
	public static final String KAFKA_TOPIC_TRANSACTIONS_CDT_TRF_TX = "credit-transfer-transactions";
	public static final int KAFKA_TOPIC_TRANSACTIONS_PARTITIONS = 271;
	
	public static final int DATA_CREATION_RATE_PER_SECOND = 5;
	public static final int PENDING_EXPIRY_SECONDS = 60;
	
	// Define bans for data volumes, random percentage, most likely in 0-79 than in 80-94, etc.
	public static final int[] DATA_CREATION_PERCENTILES = new int[] { 80, 95 };

	// Frequency for the Settlement job. Would normally be daily but not for a demo.
	public static final ChronoUnit SETTLEMENT_SCHEDULE_CHRONO = ChronoUnit.MINUTES;
	public static final long SETTLEMENT_SCHEDULE_INTERVAL = 10L;

    // Web Sockets, for Finos module
    public static final String WEBSOCKET_ENDPOINT = "hazelcast";
    public static final String WEBSOCKET_FEED_PREFIX = "feed";
    public static final String WEBSOCKET_DATA_SUFFIX = "data";

	//status for the transaction
	public static final Integer CDRTRFTXINF_DEDUP_IN_PROCESS = 0;
	public static final Integer CDRTRFTXINF_DEDUP_PROCESSED = 1;

    
}
