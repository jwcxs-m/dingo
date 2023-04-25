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

package io.dingodb.expr.runtime.evaluator.cast;

import io.dingodb.expr.annotations.Evaluators;

import java.math.BigDecimal;

@Evaluators(
    induceSequence = {
        float.class,
        BigDecimal.class,
        double.class,
        long.class,
        int.class,
        boolean.class,
        String.class
    }
)
final class FloatCastEvaluators {
    private FloatCastEvaluators() {
    }

    static float floatCast(float value) {
        return value;
    }
}