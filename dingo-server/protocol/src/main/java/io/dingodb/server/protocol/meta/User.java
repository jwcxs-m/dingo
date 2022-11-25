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

package io.dingodb.server.protocol.meta;

import io.dingodb.common.CommonId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User implements Meta {
    private String username;
    private String host;
    private String plugin;
    private String password;

    @Override
    public void setId(CommonId id) {

    }

    @Override
    public void setCreateTime(long createTime) {

    }

    @Override
    public void setUpdateTime(long updateTime) {

    }

    @Override
    public CommonId getId() {
        return null;
    }

    @Override
    public long getCreateTime() {
        return 0;
    }

    @Override
    public long getUpdateTime() {
        return 0;
    }
}
