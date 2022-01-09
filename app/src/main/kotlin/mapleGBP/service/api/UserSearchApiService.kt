package mapleGBP.service.api

import mapleGBP.model.dto.UserSearchApiResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.lang.RuntimeException

@Service
class UserSearchApiService {

    @Value("\${api.user.searchUrl}")
    lateinit var userSearchUrl: String

    fun getUserInfo(username: String): UserSearchApiResponse {
        val client: WebClient = WebClient.create(userSearchUrl.format(username))

        return client.get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                {httpStatus: HttpStatus -> httpStatus.isError},
                {clientResponse: ClientResponse ->
                    clientResponse.createException().flatMap {
                            exception: WebClientResponseException -> Mono.error(RuntimeException("API Server returned ${exception.rawStatusCode} response code, response=${exception.responseBodyAsString}"))
                    }
                }
            )
            .bodyToMono(UserSearchApiResponse::class.java)
            .onErrorResume {
                    throwable -> Mono.error(RuntimeException("Failed to call User Info Search API. ${throwable.message ?: ""}", throwable))
            }
            .block() ?: throw RuntimeException("Failed to call User Info Search API")
    }
}