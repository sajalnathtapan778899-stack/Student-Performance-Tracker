package com.example

import com.example.data.calculation.PerformanceCalculator
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testGradeCalculation() {
        assertEquals("A+", PerformanceCalculator.determineGrade(95.0))
        assertEquals("A", PerformanceCalculator.determineGrade(85.0))
        assertEquals("B", PerformanceCalculator.determineGrade(75.0))
        assertEquals("C", PerformanceCalculator.determineGrade(65.0))
        assertEquals("D", PerformanceCalculator.determineGrade(45.0))
        assertEquals("F", PerformanceCalculator.determineGrade(35.0))
    }

    @Test
    fun testPassFailDetermination() {
        assertTrue(PerformanceCalculator.isPassing(40.0, 40.0))
        assertTrue(PerformanceCalculator.isPassing(75.0, 40.0))
        assertFalse(PerformanceCalculator.isPassing(39.9, 40.0))
    }

    @Test
    fun testGradeCutoffParsing() {
        val cutoffs = PerformanceCalculator.parseGradeCutoffs("")
        assertEquals(6, cutoffs.size)
        assertEquals("A+", cutoffs[0].grade)
        assertEquals("F", cutoffs.last().grade)
    }
}
