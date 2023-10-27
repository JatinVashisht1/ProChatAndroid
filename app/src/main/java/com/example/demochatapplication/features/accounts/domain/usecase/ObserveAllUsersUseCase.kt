package com.example.demochatapplication.features.accounts.domain.usecase

import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */
class ObserveAllUsersUseCase @Inject constructor(
    private val accountsUserRepository: AccountsUserRepository,
) {
    val userModelState: MutableStateFlow<Resource<List<UserModel>>> =
        MutableStateFlow(Resource.Loading<List<UserModel>>())

    suspend operator fun invoke(shouldLoadFromNetwork: Boolean = false) {
        withContext(Dispatchers.IO) {
            try {
                accountsUserRepository.getAllUsers(shouldLoadFromNetwork = shouldLoadFromNetwork)
                    .collectLatest {
                        userModelState.value = Resource.Success(result = it)
                    }
            } catch (e: Exception) {
                Timber.tag(TAG).d("unable to get all users: $e\n${e.localizedMessage}")
                userModelState.value = Resource.Error(error = e.message ?: "Something went wrong")
            }
        }
    }

    companion object {
        const val TAG = "observeallusersusecase"
    }
}