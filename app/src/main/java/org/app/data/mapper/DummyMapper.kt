package org.app.data.mapper

import org.app.data.model.DummyUser
import org.app.data.remote.dto.GetUserListResponse

fun GetUserListResponse.UserData.toDummyUser() =
    DummyUser(
        id = this.id ?: 0,
        email = this.email.orEmpty(),
        firstName = this.firstName.orEmpty(),
        lastName = this.lastName.orEmpty(),
        profileImage = this.profileImage.orEmpty(),
    )
