package com.github.jinahya.assertj.validation.example.service;

/*-
 * #%L
 * assertj-validation
 * %%
 * Copyright (C) 2021 - 2022 Jinahya, Inc.
 * %%
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
 * #L%
 */

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * A fixture carrying executable (method and constructor) constraints.
 */
public class Greeter {

    public static final String METHOD_NAME_GREET = "greet";

    @NotNull
    @Named
    public Greeter(@NotBlank final String name) {
        this.name = name;
    }

    @NotBlank
    public String greet(@NotBlank final String whom, @Positive final int times) {
        return (name + " greets " + whom + ' ').repeat(Math.max(times, 1));
    }

    public String getName() {
        return name;
    }

    @NotBlank
    private final String name;
}
