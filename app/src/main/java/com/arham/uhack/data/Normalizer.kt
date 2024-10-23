package com.arham.uhack.data

import kotlin.math.sqrt

class Normalizer {
    private val categoryScores = mutableMapOf<String, MutableList<Double>>()
    private var mean: Map<String, Map<String, Double>> = HashMap()
    private var meanOfMean: Map<String, Double> = HashMap()
    private var standardDeviation: Map<String, Map<String, Double>> = HashMap()
    private val normalizationThreshold = 6.0
    private var normalizationFactor: Map<String, Map<String, Double>> = HashMap()

    fun normalize(marks: Map<String, Map<String, Map<String, Int>>>, mentors: Map<String, Map<String, List<String>>>): Map<String, Map<String, Map<String, Double>>> {
        mean = mentors.mapValues { (_, rounds) ->

            rounds.forEach { (round, teams) ->
                teams.forEach { teamId ->
                    val teamMarks = marks[teamId]?.get(round)
                    teamMarks?.forEach { (category, score) ->
                        categoryScores.getOrPut(category) { mutableListOf() }.add(score.toDouble())
                    }
                }
            }

            categoryScores.mapValues { (_, scores) -> scores.average() }
        }

        // Calculate meanOfMean
        mean.forEach { (_, categoryMeans) ->
            categoryMeans.forEach { (category, meanScore) ->
                categoryScores.getOrPut(category) { mutableListOf() }.add(meanScore)
            }
        }

        meanOfMean = categoryScores.mapValues { (_, scores) -> scores.average() }

        // Calculate standard deviation
        standardDeviation = mentors.mapValues { (mentorId, rounds) ->

            rounds.forEach { (round, teams) ->
                teams.forEach { teamId ->
                    val teamMarks = marks[teamId]?.get(round)
                    teamMarks?.forEach { (category, score) ->
                        categoryScores.getOrPut(category) { mutableListOf() }.add(score.toDouble())
                    }
                }
            }

            categoryScores.mapValues { (category, scores) ->
                val meanForCategory = mean[mentorId]?.get(category) ?: 0.0
                val variance = scores.map { (it - meanForCategory) * (it - meanForCategory) }.average()
                sqrt(variance)
            }
        }

        // Calculate normalization factor
        normalizationFactor = mentors.mapValues { (mentorId, _) ->
            meanOfMean.mapValues { (category, meanOfMeanValue) ->
                val standardDeviationForCategory = standardDeviation[mentorId]?.get(category) ?: 0.0
                if (standardDeviationForCategory <= normalizationThreshold) {
                    meanOfMeanValue / (mean[mentorId]?.get(category) ?: 1.0) // Avoid division by zero
                } else {
                    1.0 // Default normalization factor if condition is not met
                }
            }
        }

        // Calculate normalizedMarks with round-specific normalization
        val normalizedMarks = marks.mapValues { (teamId, rounds) ->
            rounds.mapValues { (round, categories) ->
                categories.mapValues { (category, score) ->
                    val mentorId = mentors.entries.find { (_, mentorRounds) ->
                        mentorRounds.any { (mentorRound, teams) -> mentorRound == round && teams.contains(teamId) }
                    }?.key

                    val normalizationFactorForCategory = mentorId?.let { normalizationFactor[it]?.get(category) } ?: 1.0
                    score * normalizationFactorForCategory
                }
            }
        }

        return normalizedMarks
    }
}