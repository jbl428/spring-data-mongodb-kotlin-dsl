package com.github.inflab.spring.data.mongodb.core.aggregation.search

import com.github.inflab.spring.data.mongodb.core.util.shouldBeJson
import io.kotest.core.spec.style.FreeSpec

class NumericFacetDefinitionDslTest : FreeSpec({
    fun numericFacet(block: NumericFacetDefinitionDsl.() -> Unit) =
        NumericFacetDefinitionDsl().apply(block)

    "boundaries" - {
        "should set boundaries" {
            // given
            val stage = numericFacet {
                boundaries(
                    1,
                    2,
                )
            }

            // when
            val result = stage.get()

            // then
            result.shouldBeJson(
                """
                {
                  "type": "number",
                  "boundaries": [
                    1,
                    2
                  ]
                }
                """.trimIndent(),
            )
        }
    }

    "path" - {
        "should set path by string value" {
            // given
            val stage = numericFacet {
                path("path")
            }

            // when
            val result = stage.get()

            // then
            result.shouldBeJson(
                """
                {
                  "type": "number",
                  "path": "path"
                }
                """.trimIndent(),
            )
        }

        "should set path by property" {
            // given
            data class Test(val path: Long)
            val stage = numericFacet {
                path(Test::path)
            }

            // when
            val result = stage.get()

            // then
            result.shouldBeJson(
                """
                {
                  "type": "number",
                  "path": "path"
                }
                """.trimIndent(),
            )
        }
    }

    "default" - {
        "should set default" {
            // given
            val stage = numericFacet {
                default("default")
            }

            // when
            val result = stage.get()

            // then
            result.shouldBeJson(
                """
                {
                  "type": "number",
                  "default": "default"
                }
                """.trimIndent(),
            )
        }
    }
})