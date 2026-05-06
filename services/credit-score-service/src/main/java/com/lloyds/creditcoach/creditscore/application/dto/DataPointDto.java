package com.lloyds.creditcoach.creditscore.application.dto;

import java.time.LocalDate;

public record DataPointDto(LocalDate date, Integer score, String band) {}
