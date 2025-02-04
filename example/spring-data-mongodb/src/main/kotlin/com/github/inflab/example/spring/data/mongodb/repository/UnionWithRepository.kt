package com.github.inflab.example.spring.data.mongodb.repository

import com.github.inflab.spring.data.mongodb.core.aggregation.aggregation
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.SetOperation
import org.springframework.stereotype.Repository

@Repository
class UnionWithRepository(
    private val mongoTemplate: MongoTemplate,
) {

    data class SalesDto(
        val id: String,
        val store: String,
        val item: String,
        val quantity: Int,
    )

    /**
     * @see <a href="https://www.mongodb.com/docs/manual/reference/operator/aggregation/unionWith/#report-1--all-sales-by-year-and-stores-and-items">All Sales by Year and Stores and Items</a>
     */
    fun findSalesByYearAndStoresAndItems(): AggregationResults<SalesDto> {
        // TODO: apply $set dsl
        val aggregation = aggregation {
            stage(SetOperation.set("_id").toValue("2017"))

            unionWith {
                coll(SALES_2018)
                pipeline {
                    stage(SetOperation.set("_id").toValue("2018"))
                }
            }
            unionWith {
                coll(SALES_2019)
                pipeline {
                    stage(SetOperation.set("_id").toValue("2019"))
                }
            }
            unionWith {
                coll(SALES_2020)
                pipeline {
                    stage(SetOperation.set("_id").toValue("2020"))
                }
            }

            sort {
                "_id" by asc
                "store" by asc
                "item" by asc
            }
        }

        return mongoTemplate.aggregate(aggregation, SALES_2017, SalesDto::class.java)
    }

    data class SalesByItemsDto(val id: String, val total: Int)

    /**
     * @see <a href="https://www.mongodb.com/docs/manual/reference/operator/aggregation/unionWith/#report-2--aggregated-sales-by-items">Aggregated Sales by Items</a>
     */
    fun findSalesByItems(): AggregationResults<SalesByItemsDto> {
        val aggregation = aggregation {
            unionWith { coll(SALES_2018) }
            unionWith { coll(SALES_2019) }
            unionWith { coll(SALES_2020) }

            // TODO: apply $group dsl
            stage(Aggregation.group("item").sum("quantity").`as`("total"))

            sort {
                "total" by desc
            }
        }

        return mongoTemplate.aggregate(aggregation, SALES_2017, SalesByItemsDto::class.java)
    }

    companion object {
        const val SALES_2017 = "sales_2017"
        const val SALES_2018 = "sales_2018"
        const val SALES_2019 = "sales_2019"
        const val SALES_2020 = "sales_2020"
    }
}
