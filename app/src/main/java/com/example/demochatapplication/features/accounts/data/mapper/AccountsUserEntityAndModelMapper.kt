package com.example.demochatapplication.features.accounts.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.domain.model.AccountUserModel
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
class AccountsUserEntityAndModelMapper @Inject constructor() : Mapper<AccountsUserEntity, AccountUserModel> {
    override fun mapAtoB(objectTypeA: AccountsUserEntity): AccountUserModel =
        AccountUserModel(username = objectTypeA.username)

    override fun mapBtoA(objectTypeB: AccountUserModel): AccountsUserEntity =
        AccountsUserEntity(username = objectTypeB.username, primaryId = null)
}