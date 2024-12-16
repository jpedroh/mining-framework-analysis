/**
 * Copyright (c) 2012-2015, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.dynamo;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * Scan-based valve.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@ToString
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "limit", "attributes" })
public final class ScanValve implements Valve {
    /**
     * Limit to use for every query.
     */
    private final transient int limit;

    /**
     * Attributes to fetch.
     */
    @Immutable.Array
    private final transient String[] attributes;

    /**
     * Public ctor.
     */
    public ScanValve() {
        this(Tv.HUNDRED, new ArrayList<String>(0));
    }

    /**
     * Public ctor.
     *
     * @param lmt
     * 		Limit
     * @param attrs
     * 		Attributes to pre-load
     */
    private ScanValve(final int lmt, final Iterable<String> attrs) {
        this.limit = lmt;
        this.attributes = Iterables.toArray(attrs, java.lang.String.class);
    }

    // @checkstyle ParameterNumber (5 lines)
    @Override
    public Dosage fetch(final Credentials credentials, final String table, final Map<String, Condition> conditions, final Collection<String> keys) throws IOException {
        final AmazonDynamoDB aws = credentials.aws();
        try {
            final Collection<String> attrs = new HashSet<String>(Arrays.asList(this.attributes));
            attrs.addAll(keys);
            final ScanRequest request = new ScanRequest().withTableName(table).withAttributesToGet(attrs).withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL).withScanFilter(conditions).withLimit(this.limit);
            final long start = System.currentTimeMillis();
            final ScanResult result = aws.scan(request);
            Logger.info(this, "#items(): loaded %d item(s) from '%s' using %s, %s, in %[ms]s", result.getCount(), table, conditions, new PrintableConsumedCapacity(result.getConsumedCapacity()).print(), System.currentTimeMillis() - start);
            return new ScanValve.NextDosage(credentials, request, result);
        } catch (final AmazonClientException ex) {
            throw new IOException(String.format("failed to fetch from \"%s\" by %s and %s", table, conditions, keys), ex);
        } finally {
            aws.shutdown();
        }
    }

    @Override
    public int count(final Credentials credentials, final String table, final Map<String, Condition> conditions) throws IOException {
        final AmazonDynamoDB aws = credentials.aws();
        try {
            final ScanRequest request = new ScanRequest().withTableName(table).withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL).withScanFilter(conditions).withSelect(Select.COUNT).withLimit(Integer.MAX_VALUE);
            final long start = System.currentTimeMillis();
            final ScanResult rslt = aws.scan(request);
            final int count = rslt.getCount();
                // @checkstyle LineLength (1 line)
            Logger.info(this, "#total(): COUNT=%d in '%s' using %s, %s, in %[ms]s", count, request.getTableName(), request.getFilterExpression(), AwsTable.print(rslt.getConsumedCapacity()), System.currentTimeMillis() - start);
            return count;
        } finally {
            aws.shutdown();
        }
    }

    /**
     * With given limit.
     * @param lmt Limit to use
     * @return New query valve
     */
    public ScanValve withLimit(final int lmt) {
        return new ScanValve(lmt, Arrays.asList(this.attributes));
    }

    /**
     * With this extra attribute to pre-fetch.
     * @param name Name of attribute to pre-load
     * @return New query valve
     */
    public ScanValve withAttributeToGet(final String name) {
        return new ScanValve(
            this.limit,
            Iterables.concat(
                Arrays.asList(this.attributes),
                Collections.singletonList(name)
            )
        );
    }

    /**
     * With these extra attributes to pre-fetch.
     * @param names Name of attributes to pre-load
     * @return New query valve
     */
    public ScanValve withAttributeToGet(final String... names) {
        return new ScanValve(
            this.limit,
            Iterables.concat(
                Arrays.asList(this.attributes),
                Arrays.asList(names)
            )
        );
    }

    /**
     * Next dosage.
     */
    @ToString
    @Loggable(Loggable.DEBUG)
    @EqualsAndHashCode(of = { "credentials", "request", "result" })
    private final class NextDosage implements Dosage {
        /**
         * AWS client.
         */
        private final transient Credentials credentials;

        /**
         * Query request.
         */
        private final transient ScanRequest request;

        /**
         * Query request.
         */
        private final transient ScanResult result;

        /**
         * Public ctor.
         *
         * @param creds
         * 		Credentials
         * @param rqst
         * 		Query request
         * @param rslt
         * 		Query result
         */
        NextDosage(final Credentials creds, final ScanRequest rqst, final ScanResult rslt) {
            this.credentials = creds;
            this.request = rqst;
            this.result = rslt;
        }

        @Override
        public List<Map<String, AttributeValue>> items() {
            return this.result.getItems();
        }

        @Override
        public boolean hasNext() {
            return this.result.getLastEvaluatedKey() != null;
        }

        @Override
        public Dosage next() {
            if (!this.hasNext()) {
                throw new IllegalStateException("nothing left in the iterator");
            }
            final AmazonDynamoDB aws = this.credentials.aws();
            try {
                final ScanRequest rqst = this.request.withExclusiveStartKey(this.result.getLastEvaluatedKey());
                final long start = System.currentTimeMillis();
                final ScanResult rslt = aws.scan(rqst);
                    // @checkstyle LineLength (1 line)
                Logger.info(this, "#next(): loaded %d item(s) from '%s' using %s, %s, in %[ms]s", rslt.getCount(), rqst.getTableName(), rqst.getScanFilter(), new PrintableConsumedCapacity(rslt.getConsumedCapacity()).print(), System.currentTimeMillis() - start);
                return new ScanValve.NextDosage(this.credentials, rqst, rslt);
            } finally {
                aws.shutdown();
            }
        }
    }
}