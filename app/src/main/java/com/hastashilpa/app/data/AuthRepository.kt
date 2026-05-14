package com.hastashilpa.app.data
import java.security.MessageDigest

class AuthRepository(private val userDao: UserDao) {

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun registerWithEmail(
        name: String, email: String, password: String,
        craft: String, location: String
    ): Result<UserEntity> {
        if (name.isBlank())
            return Result.failure(Exception("Name cannot be empty"))
        if (!email.contains("@"))
            return Result.failure(Exception("Enter a valid email address"))
        if (password.length < 6)
            return Result.failure(Exception("Password must be at least 6 characters"))
        if (userDao.emailExists(email.trim().lowercase()) > 0)
            return Result.failure(Exception("This email is already registered"))

        val user = UserEntity(
            name = name.trim(),
            email = email.trim().lowercase(),
            passwordHash = hash(password),
            craft = craft,
            location = location,
            loginMethod = "EMAIL"
        )
        val id = userDao.insertUser(user)
        return if (id > 0) Result.success(user.copy(id = id.toInt()))
        else Result.failure(Exception("Registration failed. Please try again."))
    }

    suspend fun registerWithPhone(
        name: String, phone: String, password: String
    ): Result<UserEntity> {
        if (phone.length < 10)
            return Result.failure(Exception("Enter a valid 10-digit phone number"))
        if (userDao.phoneExists(phone.trim()) > 0)
            return Result.failure(Exception("This phone number is already registered"))

        val user = UserEntity(
            name = name.trim(),
            email = "",
            phone = phone.trim(),
            passwordHash = hash(password),
            loginMethod = "PHONE"
        )
        val id = userDao.insertUser(user)
        return if (id > 0) Result.success(user.copy(id = id.toInt()))
        else Result.failure(Exception("Registration failed. Please try again."))
    }

    suspend fun loginWithEmail(email: String, password: String): Result<UserEntity> {
        val user = userDao.loginByEmail(email.trim().lowercase(), hash(password))
            ?: return Result.failure(Exception("Incorrect email or password"))
        return Result.success(user)
    }

    suspend fun loginWithPhone(phone: String, password: String): Result<UserEntity> {
        val user = userDao.loginByPhone(phone.trim(), hash(password))
            ?: return Result.failure(Exception("Incorrect phone number or password"))
        return Result.success(user)
    }
    suspend fun getUserById(id: Int): UserEntity? = userDao.getUserById(id)

    fun createGuestUser(): UserEntity = UserEntity(
        id = -1,
        name = "Guest Artisan",
        email = "",
        passwordHash = "",
        loginMethod = "GUEST"
    )
}