package uk.gov.justice.digital.hmpps.supervision.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasRole('PROBATION_API__SUPERVISION_PACKAGE__READ')")
class ApiController {
    @GetMapping("/case/{crn}/supervision-package")
    @Operation(summary = "Get the current supervision package for a person's supervised sentence")
    fun getCurrentPackage(@PathVariable crn: String) = null

    @GetMapping("/case/{crn}/supervision-package/history")
    @Operation(summary = "Get the supervision package history across all of a person's sentences")
    fun getHistory(@PathVariable crn: String) = null
}