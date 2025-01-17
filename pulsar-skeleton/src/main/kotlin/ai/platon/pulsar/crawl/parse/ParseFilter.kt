/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.platon.pulsar.crawl.parse

import ai.platon.pulsar.common.CheckState
import ai.platon.pulsar.common.config.Parameterized
import ai.platon.pulsar.crawl.parse.html.ParseContext

/**
 * Extension point for DOM-based parsers. A parse filter can have children, so all parse filters form a tree.
 * All parse filters at the same level are run sequentially.
 */
interface ParseFilter : Parameterized, AutoCloseable {
    val id: Int

    /**
     * The parent filter
     * */
    var parent: ParseFilter?
    /**
     * The child filters
     * */
    val children: List<ParseFilter>

    /**
     * The id of the parent filter
     * */
    val parentId get() = parent?.id ?: 0
    /**
     * Check if this filter is the root in the filter tree.
     * */
    val isRoot get() = parent == null
    /**
     * Check if this filter is a leaf in the filter tree.
     * */
    val isLeaf get() = children.isEmpty()

    /**
     * Add the filter to the head of the child filter list.
     * */
    fun addFirst(child: ParseFilter)

    /**
     * Add the filter to the tail of the child filter list.
     * */
    fun addLast(child: ParseFilter)

    /**
     * Initialize the filter.
     * */
    fun initialize()

    /**
     * Check if this parse filter is relevant to this page.
     * Sometimes we may want to write a separate [ParseFilter] for a category.
     * */
    fun isRelevant(parseContext: ParseContext): CheckState

    /**
     * Invoke before the filtering.
     * */
    fun onBeforeFilter(parseContext: ParseContext)

    /**
     * Do the filtering.
     * */
    fun filter(parseContext: ParseContext): FilterResult

    /**
     * Invoke after the filtering.
     * */
    fun onAfterFilter(parseContext: ParseContext)

    /**
     * Close the filter.
     * */
    override fun close() {}
}
