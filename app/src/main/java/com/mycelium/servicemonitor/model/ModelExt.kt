package com.mycelium.servicemonitor.model

fun ServiceEntity.toModel(): Service =
    Service(
        id = this.id,
        name = this.name,
        url = this.url,
        interval = this.interval,
        headers = this.headers,
        method = this.method,
        body = this.body,
        responsePattern = this.responsePattern,
        useRegexPattern = this.useRegexPattern,
        sha1Certificate = this.sha1Certificate,
        status = this.status,
        lastChecked = this.lastChecked,
        lastSuccessfulCheck = this.lastSuccessfulCheck,
        archived = this.archived,
        position = this.position,
        groupName = this.groupName
    )

fun Service.toEntity(): ServiceEntity =
    ServiceEntity(
        id = this.id,
        name = this.name,
        url = this.url,
        interval = this.interval,
        headers = this.headers,
        method = this.method,
        body = this.body,
        responsePattern = this.responsePattern,
        useRegexPattern = this.useRegexPattern,
        sha1Certificate = this.sha1Certificate,
        status = this.status,
        lastChecked = this.lastChecked,
        lastSuccessfulCheck = this.lastSuccessfulCheck,
        archived = this.archived,
        position = this.position,
        groupName = this.groupName
    )