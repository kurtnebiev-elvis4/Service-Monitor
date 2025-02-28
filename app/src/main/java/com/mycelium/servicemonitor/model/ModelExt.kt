package com.mycelium.servicemonitor.model


fun ServiceEntity.toModel(): Service =
    Service(
        id = this.id,
        name = this.name,
        url = this.url,
        interval = this.interval,
        headers = this.headers,
        status = this.status,
        lastChecked = this.lastChecked,
        archived = this.archived
    )

fun Service.toEntity(): ServiceEntity =
    ServiceEntity(
        id = this.id,
        name = this.name,
        url = this.url,
        interval = this.interval,
        headers = this.headers,
        status = this.status,
        lastChecked = this.lastChecked,
        archived = this.archived
    )
