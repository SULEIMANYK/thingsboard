/**
 * Copyright © 2016-2022 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.actors.ruleChain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.thingsboard.server.cache.rule.RuleNodeCache;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.RuleNodeId;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.service.rule.TbRuleNodeCacheService;

import java.util.List;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class DefaultTbRuleNodeCacheService implements TbRuleNodeCacheService {

    private final RuleNodeId ruleNodeId;
    private final RuleNodeCache cache;

    @Override
    public void add(String key, String value) {
        cache.add(toRuleNodeCacheKey(key), value);
    }

    @Override
    public void add(String key, EntityId id) {
        cache.add(toRuleNodeCacheKey(key), id);
    }

    @Override
    public void add(String key, TbMsg value) {
        cache.add(toRuleNodeCacheKey(key), value);
    }

    @Override
    public void removeTbMsgList(String key, List<TbMsg> values) {
        cache.removeTbMsgList(toRuleNodeCacheKey(key), values);
    }

    @Override
    public void removeStringList(String key, List<String> values) {
        cache.removeStringList(toRuleNodeCacheKey(key), values);
    }

    @Override
    public void removeEntityIdList(String key, List<EntityId> values) {
        cache.removeEntityIdList(toRuleNodeCacheKey(key), values);
    }

    @Override
    public Set<String> getStrings(String key) {
        return cache.getStringSetByKey(toRuleNodeCacheKey(key));
    }

    @Override
    public Set<EntityId> getEntityIds(String key) {
        return cache.getEntityIdSetByKey(toRuleNodeCacheKey(key));
    }

    @Override
    public Set<TbMsg> getTbMsgs(String key, String queueName) {
        return cache.getTbMsgSetByKey(toRuleNodeCacheKey(key), queueName);
    }

    @Override
    public void evict(String key) {
        cache.evict(key);
    }

    private String toRuleNodeCacheKey(String key) {
        return String.format("%s::%s", ruleNodeId.toString(), key);
    }

}