package com.work.taskCatalog.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass


@Target(FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValueOfEnumValidator::class])
annotation class ValueOfEnum(
    val enumClass: KClass<out Enum<*>>,
    val message: String = "must be any of enum {enumClass}",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValueOfEnumValidator : ConstraintValidator<ValueOfEnum, String> {
    private lateinit var enumValues: Set<String>

    override fun initialize(constraintAnnotation: ValueOfEnum) {
        enumValues = constraintAnnotation.enumClass
            .java
            .enumConstants
            .map { it.name }
            .toSet()
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null || enumValues.contains(value)) {
            return true
        }

        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(
            "must be any of: ${enumValues.joinToString(", ")}"
        ).addConstraintViolation()

        return false
    }
}
