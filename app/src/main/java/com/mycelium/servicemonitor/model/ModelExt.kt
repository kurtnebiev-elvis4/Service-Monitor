package com.mycelium.servicemonitor.model

fun ServiceEntity.toModel(): Service =
    Service(
        id = this.id,
        name = this.name,
        url = this.url,
        interval = this.interval,
        headers = this.headers,
        method = this.method,  // new field conversion
        body = this.body,      // new field conversion
        sha1Certificate = this.sha1Certificate, // new field conversion
        status = this.status,
        lastChecked = this.lastChecked,
        lastSuccessfulCheck = this.lastSuccessfulCheck,
        archived = this.archived,
        position = this.position
    )

fun Service.toEntity(): ServiceEntity =
    ServiceEntity(
        id = this.id,
        name = this.name,
        url = this.url,
        interval = this.interval,
        headers = this.headers,
        method = this.method,  // new field conversion
        body = this.body,      // new field conversion
        sha1Certificate = this.sha1Certificate, // new field conversion
        status = this.status,
        lastChecked = this.lastChecked,
        lastSuccessfulCheck = this.lastSuccessfulCheck,
        archived = this.archived,
        position = this.position
    )