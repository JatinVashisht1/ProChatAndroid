package com.example.demochatapplication.features.accounts.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.accounts.data.database.entities.SearchUserEntity
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
class SearchUserEntityAndModelMapper @Inject constructor() : Mapper<SearchUserEntity, UserModel> {
    override fun mapAtoB(objectTypeA: SearchUserEntity): UserModel =
        UserModel(username = objectTypeA.username)

    override fun mapBtoA(objectTypeB: UserModel): SearchUserEntity =
        SearchUserEntity(username = objectTypeB.username, primaryId = null)
}