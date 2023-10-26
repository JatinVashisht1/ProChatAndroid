package com.example.demochatapplication.features.accounts.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
class SearchUserEntityAndModelMapper @Inject constructor() : Mapper<AccountsUserEntity, UserModel> {
    override fun mapAtoB(objectTypeA: AccountsUserEntity): UserModel =
        UserModel(username = objectTypeA.username)

    override fun mapBtoA(objectTypeB: UserModel): AccountsUserEntity =
        AccountsUserEntity(username = objectTypeB.username, primaryId = null)
}