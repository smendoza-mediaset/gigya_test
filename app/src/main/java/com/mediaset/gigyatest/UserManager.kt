package com.mediaset.gigyatest

import android.content.Context
import android.util.Log
import com.gigya.android.sdk.Gigya
import com.gigya.android.sdk.GigyaCallback
import com.gigya.android.sdk.GigyaPluginCallback
import com.gigya.android.sdk.network.GigyaError
import com.gigya.android.sdk.network.adapter.RestAdapter
import com.gigya.android.sdk.ui.plugin.GigyaPluginEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mediaset.gigyatest.models.User
import org.json.JSONObject
import java.util.*

object UserManager : Observable() {

    enum class Event {
        LOGIN, LOGOUT, ERROR, DISMISS
    }

    const val TERMS = "terms"
    const val ALLOW_NEWSLETTER = "allowNewsletter"
    const val ALLOW_MAILING = "allowMailing"
    const val EVENT = "EVENT"
    const val LET_USER_CONTINUE = "LET_USER_CONTINUE"
    private const val GIGYA_API_DOMAIN = "eu1.gigya.com"
    const val FINISHED = "finished"

    private val userFilename = "VXNlckZpbGVOYW1l"

    private val AFTER_SUBMIT_EVENT = "afterSubmit"
    private val HIDE_EVENT = "hide"
    private val SUBMIT_EVENT = "submit"

    private val SERVICE_SIGNATURE_FIELD = "signature"
    private val SERVICE_TIMESTAMP_FIELD = "timestamp"

    private val UID = "UID"
    private val UID_SIGNATURE = "UIDSignature"
    private val SIGNATURE_TIMESTAMP = "signatureTimestamp"
    private val DATA = "data"
    private val PROFILE = "profile"

    private val TAG = "gigya"

    private var regSource: String? = null
    private var apiKey: String? = null

    private var onLoginListener: (() -> Unit)? = null
    private var onIgnoreListener: (() -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null
    private var onUserUpdatedListener: (() -> Unit)? = null

    //private var onEditListener: (() -> Unit)? = null

    lateinit var mGigya: Gigya<User>

//    private var offlineVideoEngine: OfflineVideoEngine? = null
//    private var mVirtuoso: Virtuoso? = null

    val userId: String
        get() = user?.uid ?: ""

    var user: User? = null



    val isLogged: Boolean
        get() = user != null

    var isPlus: Boolean = false
    private var mContext: Context? = null

    var isCurrentlyPlaying: Boolean = false

    var hasPendingPayment = false

    //private val isGDPRUser: Boolean
//        get() {
//            val registeredTimestamp = user?.createdTimestamp
//            //val gdprLaunchDate = policiesConfig?.startGdpr
//            return if (registeredTimestamp != null && gdprLaunchDate != null) {
//                (registeredTimestamp * 1000) >= gdprLaunchDate
//            } else false
//        }



    fun initialize(updateUser: Boolean) {
        //val gigyaConfig = Config.instance.servicesConfig?.gigya
        // gigyaConfig?.apiKey?.let { this.apiKey = it } ?: run { this.apiKey = "" }
        //gigyaConfig?.regSource?.let { this.regSource = it } ?: run { this.regSource = "" }

        this.regSource = "157"
        this.apiKey = "3_7sMzwxG5qpGQewmPMcSjiZeUkvQKZGOvOD-dIOY0Zu8RFFz0sA5uz7UF-BFynqW2"


        //Explicit init using API, and domain parameters
        mGigya = Gigya.getInstance(User::class.java)
        this.apiKey?.let { mGigya.init(it, GIGYA_API_DOMAIN) }
        //mGigya.init("3_hx-5Cpvf74oizFDvgSNptvBXP6h2cUA6X7F0KqcQA7yBd50gl81shPq7kd0WHB0z",  GIGYA_API_DOMAIN)

        if (updateUser) {
            if (isLogged && user != null) {
                Log.d("GetAccountInfo:", "YES")
                updateUserInfo()
            } else Log.d("GetAccountInfo:", "NO")
        }
    }

//    fun updateUserIfNeeded(onUserUpdated: () -> Unit = {}, onIgnore: () -> Unit = {}) {
//        onIgnoreListener = onIgnore
//        onUserUpdatedListener = onUserUpdated
//        if (!hasValidPolicyVersion() && !isGDPRUser) completeRegistration() else onUserUpdatedListener?.invoke()
//    }

    fun login(onLogin: () -> Unit = {}, onDismiss: () -> Unit = {}) {

        onLoginListener = onLogin
        onDismissListener = onDismiss

        val paramsLogin = mutableMapOf<String, String?>()
        paramsLogin["screenSet"] = "Default-RegistrationLogin"
        paramsLogin["lang"] = "es"
        paramsLogin["regSource"] = regSource
        paramsLogin["authFlow"] = "redirect"

        showLoginScreen(paramsLogin)
    }

//    fun edit(onEdit: () -> Unit = {}) {
//        //onEditListener = onEdit
//        val paramsLogin = mutableMapOf<String, String?>()
//        paramsLogin.put("screenSet", "Mobile-ProfileUpdate")
//        paramsLogin.put("lang", "es")
//        paramsLogin.put("regSource", regSource)
//
//        showLoginScreen(paramsLogin, true)
//
//    }

//    private fun completeRegistration() {
//        val paramsLogin = mutableMapOf<String, String?>()
//        paramsLogin.put("screenSet", "Default-RegistrationLogin")
//        paramsLogin.put("startScreen", "gigya-complete-registration-screen")
//        paramsLogin.put("lang", "es")
//        paramsLogin.put("regSource", regSource)
//
//
//        showLoginScreen(paramsLogin, isEditMode = true)
//    }

    private fun showLoginScreen(params: Map<String, String?>, isEditMode: Boolean = false) {

        if (!this::mGigya.isInitialized) {
            initialize(updateUser = false)
        }

        if (params["screenSet"] != null) {

            val screenSet = params["screenSet"]

            mGigya.showScreenSet(screenSet, false, params, object : GigyaPluginCallback<User>() {

                override fun onLogin(accountObj: User) {
                    // Login success.
                    Log.d(TAG, "User: $accountObj logged")
                    updateLastLoginSite(accountObj.uid)
                    onLoginComplete(accountObj)
                }

                //Override method for edit actions
                override fun onSubmit(event: GigyaPluginEvent) {
                    super.onSubmit(event)
                    Log.d(TAG, "User: $event edited")
                    try {
                        if (isEditMode) {
                            //Getting Event Name
                            val eventName = event.eventMap["eventName"]
                            //Getting old data
                            val accountInfo = JSONObject(event.eventMap["accountInfo"].toString())
                            val oldData = JSONObject(accountInfo["data"].toString())
                            //Getting new data
                            val formModel = JSONObject(event.eventMap["formModel"].toString())
                            if (!formModel.has("password")) {
                                val newData = JSONObject(formModel["data"].toString())

                                if (eventName == SUBMIT_EVENT) {

                                    val params = JsonObject()

                                    val oldNewsletter = oldData.has(ALLOW_NEWSLETTER) && oldData.getBoolean(ALLOW_NEWSLETTER)
                                    val newNewsletter = newData.has(ALLOW_NEWSLETTER) && newData.getBoolean(ALLOW_NEWSLETTER)

                                    val oldMailing = oldData.has(ALLOW_MAILING) && oldData.getBoolean(ALLOW_MAILING)
                                    val newMailing = newData.has(ALLOW_MAILING) && newData.getBoolean(ALLOW_MAILING)

                                    if (oldNewsletter != newNewsletter) params.addProperty(ALLOW_NEWSLETTER, newNewsletter)
                                    if (oldMailing != newMailing) params.addProperty(ALLOW_MAILING, newMailing)
                                    if (newData.has(TERMS)) params.addProperty(TERMS, true)

                                    updateInfoAfterManualUpdate(params)
                                    onUserUpdatedListener?.invoke()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                /*    override fun onAfterSubmit(event: GigyaPluginEvent) {
                        super.onAfterSubmit(event)
                        if (isEditMode)
                            getAccountInfo {
                                onEditListener?.invoke()
                            }
                    }*/

                override fun onError(event: GigyaPluginEvent?) {
                    super.onError(event)
                    val errorMap = mutableMapOf<String, Any?>()
                    errorMap["errorMessage"] = event?.eventMap?.get("errorMessage")
                    errorMap["errorCode"] = event?.eventMap?.get("errorCode")
                    manageError(errorMap)
                }

                override fun onHide(event: GigyaPluginEvent, reason: String?) {
                    super.onHide(event, reason)
                    if (reason == FINISHED) {
                        Log.d(TAG, "Finished")
                    }
                    onDismissListener?.invoke()
                    notifyObservers(Event.DISMISS)
                }

                override fun onConnectionAdded() {
                    super.onConnectionAdded()
                    Log.d(TAG, "Connection Added")
                }

                override fun onConnectionRemoved() {
                    super.onConnectionRemoved()
                    Log.d(TAG, "Connection Removed")
                }

                override fun onAfterValidation(event: GigyaPluginEvent) {
                    super.onAfterValidation(event)
                    Log.d(TAG, "After Validation")
                }

                override fun onAfterScreenLoad(event: GigyaPluginEvent) {
                    super.onAfterScreenLoad(event)
                    onDismissListener?.invoke()
                }

                override fun onCanceled() {
                    super.onCanceled()
                    Log.d(TAG, "Canceled")
                    onIgnoreListener?.invoke()
                }

            })
        }
    }

    private fun onLoginComplete(loggedUser: User) {
        if (isValidUser(loggedUser)) {
            user = loggedUser
            hasPendingPayment = loggedUser.data.isOverdue ?: false
//            saveUserAndRefreshInfoIfNeeded(user, ShouldCheckUser = true)
            onLoginListener?.invoke()

            //checkSalesforceId(user)
            //initOffline()
            notifyObservers(Event.LOGIN)
//            user?.uid?.let { saveUserForCrashlytics(it) }
        } else {
            //removeUser()
        }
    }



    private fun checkUserPlus() {
//        val disposable = CompositeDisposable()
//        val checkIfUserHasMitelePlusUseCase = CheckIfUserHasMitelePlusUseCaseImpl(
//            disposable
//        )
//        if (user?.signatureTimestamp != null) {
//            checkIfUserHasMitelePlusUseCase.isPlus(onMitelePlusError = {
//                isPlus = false
//                disposeNow(disposable)
//            }, onMiTelePlusSuccess = {
//                isPlus = it
//                disposeNow(disposable)
//            })
//        }
    }

//    private fun disposeNow(disposable: CompositeDisposable) {
//        if (!disposable.isDisposed) {
//            disposable.dispose()
//        }
//    }

//    private fun checkSalesforceId(user: User?) {
//        if (user?.salesforceId == null) {
//            // Update Salesforce Id
//            user?.uid?.let {
//                MiddlewareRest.updateSalesforceId(it)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({ Log.d(TAG, "SALESFORCE UPDATED") }, {})
//            }
//        }
//    }

    private fun isValidUser(loggedUser: User): Boolean {
        return try {
            return loggedUser.uid != null && loggedUser.isActive()
        } catch (e: Exception) {
            Log.e("ERROR", e.stackTraceToString())
            false
        }
    }

    private fun manageError(errorMap: MutableMap<String, Any?>) {
        val errorCode = errorMap["errorCode"].toString()
        if (errorCode != null) {
            if (errorCode.startsWith("5")) {
                val msg = errorMap["errorMessage"]
                val type = "Error Gigya Android"
                notifyObservers(Event.ERROR, true)
            } else {
                notifyObservers(Event.ERROR)
            }
        }

    }

    fun logout(isConcurrenceError: Boolean = true) {

        logoutRoutine()

    }

    private fun logoutRoutine() {

        mGigya.logout()
        isPlus = false
        user = null
    }

    private fun getAccountInfo(onResponse: (response: User) -> Unit) {
        Log.d(TAG, "is mGigya Initialized before getAccountInfo: " + this::mGigya.isInitialized.toString())

        if (!this::mGigya.isInitialized) {
            initialize(updateUser = false)
        }

        val params = mutableMapOf<String, Any?>()
        params.put(UID, user?.uid)
        params.put("include", DATA)
        params.put("include", PROFILE)

        Log.d(TAG, "is mGigya Initialized before send: " + this::mGigya.isInitialized.toString())
        //FirebaseCrashlytics.getInstance().log("is mGigya Initialized before send: " + this::mGigya.isInitialized.toString())

        mGigya.send(
            "accounts.getAccountInfo",
            params,
            RestAdapter.HttpMethod.GET.intValue(),
            User::class.java,
            object : GigyaCallback<User>() {

                override fun onSuccess(obj: User?) {
                    Log.d(TAG, "SUCCESS UPDATING LAST LOGIN INFO")
                    if (obj != null) {
                        onResponse(obj)
                    }
                }

                override fun onError(obj: GigyaError?) {
                    Log.d(TAG, "ERROR UPDATING LAST LOGIN INFO")
                    val data = obj?.data
                    val userData = Gson().fromJson(data.toString(), User::class.java)
                    onResponse(userData)
                }
            })
    }


    fun updateInfoAfterManualUpdate(updatedConsents: JsonObject, checkPlusUser: Boolean = false) {
        getAccountInfo { gsResponse ->
            callUpdateService(gsResponse, updatedConsents)
            if (checkPlusUser) {
                checkUserPlus()
            }
        }
    }

    private fun callUpdateService(gResponse: User, newConsents: JsonObject) {
        try {

            val uID = gResponse.uid
            val uIDSignature = gResponse.getuIDSignature()
            val signatureTimestamp = gResponse.signatureTimestamp

            newConsents.addProperty(SERVICE_SIGNATURE_FIELD, uIDSignature)
            newConsents.addProperty(SERVICE_TIMESTAMP_FIELD, signatureTimestamp.toString())

//            if (uID != null) {
//                updateUserOnMdw(uID, newConsents)
//            }

//            UserAccountServiceApi.updateUserConsents(uID, newConsents, object : Callback<JsonObject> {
//
//                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    updateUserInfo()
//                }
//
//                override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
//            })

        } catch (ignored: Exception) {
            Log.d("ErrorUpdating", ignored.toString())
        }
    }

    /*  fun updateInfoFromNewPolicesDialog(data: JsonObject) {
          updateInfoAfterManualUpdate(data)
          notifyObservers(Event.LOGIN)
      }*/

//    suspend fun refreshUserInfo(onRefreshed: () -> Unit) = withContext(Dispatchers.IO) {
//        try {
//            getAccountInfo { gsResponse ->
//                val updatedUser = user
//                hasPendingPayment = updatedUser?.data?.isOverdue
//                    ?: false  //ToDo: Should this variable be here?
//                gsResponse.data?.let { updatedUser?.data = it }
//                gsResponse.signatureTimestamp?.let { updatedUser?.signatureTimestamp = it }
//                gsResponse.uidSignature?.let { updatedUser?.uidSignature = it }
//                gsResponse.uid?.let { updatedUser?.uid = it }
//                gsResponse.profile?.let { updatedUser?.profile = it }
//                saveUserAndRefreshInfoIfNeeded(updatedUser, ShouldCheckUser = true)
//                onRefreshed()
//            }
//        } catch (e: Exception) {
//            onRefreshed()
//            FirebaseCrashlytics.getInstance().log(e.localizedMessage
//                ?: "Gigya error on getAccountInfo")
//        }
//
//    }

//    private fun removeUserSession() {
//        val sharedPreferences = MiteleApplication.getContext().getSharedPreferences(MiteleApplication.MAIN_PREF_FILE, Context.MODE_PRIVATE)
//
//        val editor = sharedPreferences.edit()
//        editor.remove(MiteleApplication.USER_SESSION_ID_KEY)
//        editor.remove(MiteleApplication.DISPLAY_USER_SESSION_ID_KEY)
//        editor.remove(MiteleApplication.VIDEO_USER_SESSION_ID_KEY)
//        editor.apply()
//    }

//    private fun removeUserOnSharedPreferences() {
//        val sharedPreferences = getContext().getSharedPreferences(MiteleApplication.MAIN_PREF_FILE, Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.remove("UID")
//        editor.remove("gat")
//        editor.apply()
//    }
//
//    private fun saveFirstKnownUser() {
//        val sharedPreferences = getContext().getSharedPreferences(MiteleApplication.MAIN_PREF_FILE, Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.putString("firstKnownUser", user?.uid)
//        editor.apply()
//    }
//
//    private fun saveUserOnSharedPreferences() {
//        val sharedPreferences = getContext().getSharedPreferences(MiteleApplication.MAIN_PREF_FILE, Context.MODE_PRIVATE)
//
//        val editor = sharedPreferences.edit()
//        editor.putString("UID", user?.uid)
//        editor.putString("uuID", user?.uid)
//        editor.putString("UIDSignature", user?.uidSignature)
//        editor.apply()
//
//        //Check if we have firstKnownUser already
//        //*firstKnownUser will be used as userId to Startup the backplane for Penthera SDK*//*
//        val firstKnownUser = sharedPreferences.getString("firstKnownUser", "")
//        if (firstKnownUser.isNullOrBlank()) {
//            //If not, create one
//            saveFirstKnownUser()
//        }
//    }


    private fun updateLastLoginSite(userId: String?) {
        val params = mutableMapOf<String, Any?>()

        val lastLoginSite = JSONObject()
        lastLoginSite.put("LastLoginSite", "appmitele android")

        params.put(UID, userId)
        params.put(DATA, lastLoginSite)
        //params.put(PROFILE, lastLoginSite)

        mGigya.send(
            "accounts.setAccountInfo",
            params,
            RestAdapter.HttpMethod.POST.intValue(),
            User::class.java,
            object : GigyaCallback<User>() {

                override fun onSuccess(p0: User?) {
                    Log.d(TAG, "SUCCESS UPDATING LAST LOGIN INFO")
                }

                override fun onError(p0: GigyaError?) {
                    Log.d(TAG, "ERROR UPDATING LAST LOGIN INFO")
                }

            })

        /*   mGigya.send(
                   "accounts.getSchema",
                   params,
                   RestAdapter.HttpMethod.POST.intValue(),
                   User::class.java,
                   object : GigyaCallback<User>() {

                       override fun onSuccess(p0: User?) {
                           Log.d(TAG, "SUCCESS GETTING SCHEMA")
                       }

                       override fun onError(p0: GigyaError?) {
                           Log.d(TAG, "ERROR GETTING SCHEMA")
                       }

                   })*/
    }

//    private fun removeUser() {
//        this.user = null
//        ObjectToFile.delete(MiteleApplication.getContext(), userFilename)
//        UserListsManager.resetUserLists()
//        removeUserSession()
//        removeUserOnSharedPreferences()
//    }

//    private fun saveUserAndRefreshInfoIfNeeded(user: User?, ShouldCheckUser: Boolean) {
//        this.user = user
//        if (ObjectToFile.exists(MiteleApplication.getContext(), userFilename)) {
//            ObjectToFile.delete(MiteleApplication.getContext(), userFilename)
//        }
//        ObjectToFile.write(MiteleApplication.getContext(), this.user, userFilename)
//        saveUserOnSharedPreferences()
//        UserManager.user?.uid?.let { saveUserForCrashlytics(it) }
//
//        if (ShouldCheckUser && !isCurrentlyPlaying) { //Everytime we call getAccountInfo after Splash we have to check privacyPolicyCurrentVersion and GDPR
//            if (!hasValidPolicyVersion() && !isGDPRUser) completeRegistration()
//            checkSalesforceId(user)
//        }
//    }

//

    private fun updateUserInfo() {
        getAccountInfo { gsResponse ->

            val updatedUser = user
            hasPendingPayment = updatedUser?.data?.isOverdue ?: false
            gsResponse.data?.let { updatedUser?.data = it }
            gsResponse.signatureTimestamp?.let { updatedUser?.signatureTimestamp = it }
            gsResponse.uidSignature?.let { updatedUser?.uidSignature = it }
            gsResponse.uid?.let { updatedUser?.uid = it }
            gsResponse.profile?.let { updatedUser?.profile = it }
            //saveUserAndRefreshInfoIfNeeded(updatedUser, ShouldCheckUser = false)
        }
    }

    private fun notifyObservers(event: Event, letUserContinue: Boolean = false) {
        setChanged()
        val data = HashMap<String, Any>()
        data[EVENT] = event
        data[LET_USER_CONTINUE] = letUserContinue
        super.notifyObservers(data)
    }

    //GDPR
//    private fun privacyPoliciesCheck() {
//        //val currentVersion = policiesConfig!!.policiesVersion
//
//        if (isGDPRUser) {
//            if (user?.hasTermsAccepted() == true) {
//                var data = JsonObject()
//                data.addProperty("terms", true)
//                updateInfoAfterManualUpdate(data, checkPlusUser = true)
//            }
//        } else {
//            GlobalScope.launch(Dispatchers.Main) {
//                refreshUserInfo {
//                    checkUserPlus()
//                }
//            }
//        }
//
//        EventBus.getDefault().post(NewPoliciesEvent())
//        /*¡if (hasValidPolicyVersion()) {
//            if (!isUpToDateInPolicies(currentVersion)) {
//                EventBus.getDefault().post(NewPoliciesEvent())
//            } else {
//                notifyObservers(Event.LOGIN)
//            }
//        } else {
//            if (isGDPRUser) {
//                if (user?.hasTermsAccepted() == true) {
//                    updateUserInfoAfterFirstLogin()
//                }
//                notifyObservers(Event.LOGIN)
//            } else {
//                EventBus.getDefault().post(NewPoliciesEvent())
//            }
//        }*/
//    }

    private fun hasValidPolicyVersion(): Boolean {
        var valid = false
        user?.let {
            valid = it.privacyPolicyCurrentVersion != null &&
                    it.privacyPolicyCurrentVersion.isNotEmpty()
            /*it.privacyPolicyDate != null &&
            it.privacyPolicyDate.isNotEmpty()*/
        }
        return valid
    }

//    private fun isUpToDateInPolicies(currentPoliciesVersion: String) =
//            hasValidPolicyVersion() && user?.privacyPolicyLastVersion.equals(currentPoliciesVersion, ignoreCase = true)

    fun isZuoraUser() = user?.isZuoraUser


//    private fun updateUserOnMdw(uuid: String, data: JsonObject) {
//        val disposable = MiddlewareRest.updateUserInfo(uuid, data)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { response ->
//                    Log.d("Response", response.toString())
//                },
//                { error ->
//                    Log.d("Error", error.toString())
//                }
//            )
//        disposables.add(disposable)
//    }
}