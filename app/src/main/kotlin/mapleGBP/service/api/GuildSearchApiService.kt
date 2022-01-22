package mapleGBP.service.api

import mapleGBP.model.World
import mapleGBP.model.dto.GuildSearchApiResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.lang.RuntimeException

@Service
open class GuildSearchApiService {

    @Value("\${api.guild.searchUrl}")
    lateinit var guildSearchUrl: String

    open fun getGuildInfo(guildName: String, world: World): GuildSearchApiResponse {
        val client: WebClient = WebClient.create(guildSearchUrl.format(world.worldName, guildName))

        return client.get()
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(
                {httpStatus -> httpStatus.isError},
                {clientResponse: ClientResponse ->
                    clientResponse.createException().flatMap {
                        exception: WebClientResponseException -> Mono.error(RuntimeException("API Server returned ${exception.rawStatusCode} response code, response=${exception.responseBodyAsString}"))
                }}
            ).bodyToMono(GuildSearchApiResponse::class.java)
            .onErrorResume {
                    throwable -> Mono.error(RuntimeException("Failed to call Guild Search API, ${throwable.message ?: ""}", throwable))
            }
            .block() ?: throw RuntimeException("Failed to call Guild Search API")
    }
}