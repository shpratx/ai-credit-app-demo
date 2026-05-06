package com.lloyds.creditcoach.consent.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GrantConsentRequest(
        @NotNull @Pattern(regexp = "EXPERIAN|EQUIFAX|TRANSUNION") String craProvider,
        @NotBlank @Size(max = 10) String consentTextVersion,
        @NotBlank @Size(min = 64, max = 64) String consentTextHash,
        @NotNull @Pattern(regexp = "IOS|ANDROID|WEB") String channel,
        @AssertTrue(message = "Privacy notice must be accepted") Boolean privacyNoticeAccepted
) {}
