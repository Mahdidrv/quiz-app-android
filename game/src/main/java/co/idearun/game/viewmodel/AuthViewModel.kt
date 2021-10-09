package co.idearun.game.viewmodel

import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.idearun.auth.model.LoginRes
import co.idearun.auth.model.Token
import co.idearun.auth.model.register.RegisterInfo
import co.idearun.common.base.BaseViewModel
import co.idearun.auth.model.register.RegisterRes
import co.idearun.auth.repository.AuthRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class AuthViewModel(private val repository: AuthRepositoryImpl) : BaseViewModel() {

    private val _registerData = MutableLiveData<RegisterRes>()
    val registerData: LiveData<RegisterRes> = _registerData

    private val _loginData = MutableLiveData<LoginRes>()
    val loginData: LiveData<LoginRes> = _loginData

    private val _authorizeData = MutableLiveData<Token>()
    val authorizeData: LiveData<Token> = _authorizeData


    fun registerUser(registerInfo: RegisterInfo) = viewModelScope.launch {

        val req = ArrayMap<String, Any>()

        with(registerInfo) {
            req["first_name"] = name
            req["password"] = pass
            req["email"] = email
            req["phone_number"] = phone
            req["gender"] = gender
        }

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), JSONObject(req).toString()
        )

        val result = withContext(Dispatchers.IO) { repository.registerUser(body) }
        result.either(::handleFailure, ::handleRegisterData)
    }

    fun loginUser(userName: String, password: String) = viewModelScope.launch {
        val req = ArrayMap<String, Any>()
        req["password"] = password
        req["username"] = userName

        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(), JSONObject(req).toString()
        )

        val result = withContext(Dispatchers.IO) { repository.loginUser(body) }
        result.either(::handleFailure, ::handleLoginData)
    }

    fun authorizeUser(token: String) = viewModelScope.launch {
        val result = withContext(Dispatchers.IO) { repository.authorizeUser(token) }
        result.either(::handleFailure, ::handleAuthorizeData)
    }

    private fun handleRegisterData(res: RegisterRes) {
        res.let {
            _registerData.value = it
        }
    }

    private fun handleLoginData(res: LoginRes) {
        res.let {
            _loginData.value = it
        }
    }

    private fun handleAuthorizeData(res: Token) {
        res.let {
            _authorizeData.value = it
        }
    }


}