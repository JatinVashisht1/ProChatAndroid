package com.example.demochatapplication.features.searchuseraccounts.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel

class SearchUserDbEntityAndModelMapper: Mapper<SearchUserDatabaseEntity, SearchUserDomainModel> {
    override fun mapAtoB(objectTypeA: SearchUserDatabaseEntity): SearchUserDomainModel = SearchUserDomainModel(username = objectTypeA.username)

    override fun mapBtoA(objectTypeB: SearchUserDomainModel): SearchUserDatabaseEntity = SearchUserDatabaseEntity(username = objectTypeB.username)
}