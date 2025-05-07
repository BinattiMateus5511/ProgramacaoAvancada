package jsonlib.test

import jsonlib.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonVisitorTest {

    @Test
    fun testUniqueKeysValidator() {
        val obj = JSONObject(
            mapOf(
                "a" to JSONNumber(1),
                "b" to JSONNumber(2)
            )
        )
        val validator = UniqueKeysValidator()
        obj.accept(validator)
        assertTrue(validator.isValid)
    }

    @Test
    fun testHomogeneousArrayValidator() {
        val arr = JSONArray(
            listOf(
                JSONString("x"),
                JSONString("y"),
                JSONNull
            )
        )
        val validator = HomogeneousArrayValidator()
        arr.accept(validator)
        assertTrue(validator.isValid)
    }

    @Test
    fun testHomogeneousArrayValidatorFails() {
        val arr = JSONArray(
            listOf(
                JSONString("x"),
                JSONNumber(1)
            )
        )
        val validator = HomogeneousArrayValidator()
        arr.accept(validator)
        assertFalse(validator.isValid)
    }
}