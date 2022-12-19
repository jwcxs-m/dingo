/*
 * Copyright 2021 DataCanvas
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

package io.dingodb.common.domain;

import io.dingodb.common.CommonId;
import io.dingodb.common.auth.DingoRole;
import io.dingodb.common.privilege.PrivilegeGather;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Domain {

    public static DingoRole role;

    public static Properties info = new Properties();

    public static Domain INSTANCE = new Domain();

    public volatile Map<String, PrivilegeGather> privilegeGatherMap = new ConcurrentHashMap<>();

    public Map<String, CommonId> schemaIdMap = new ConcurrentHashMap<>();

    public Map<CommonId, Map<String, CommonId>> tableIdMap = new ConcurrentHashMap<>();

    public boolean containsKey(String key) {
        return info.containsKey(key);
    }

    public Object getInfo(String field) {
        if (info.containsKey(field)) {
            return info.getProperty(field);
        } else {
            return null;
        }
    }

    public void remove(String field) {
        info.remove(field);
    }

    public void setInfo(String key, Object value) {
        info.put(key, value);
    }

    public void putAll(Properties properties) {
        info.putAll(properties);
    }
}
