package ramble.sokol.hibyeapp

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT

fun getUserIdFromToken(accessToken: String): Int? {
    return try {
        val jwt: DecodedJWT = JWT.decode(accessToken)
        jwt.getClaim("userId").asInt()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}