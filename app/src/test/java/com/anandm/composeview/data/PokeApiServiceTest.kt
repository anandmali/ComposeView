package com.anandm.composeview.data

import com.anandm.composeview.enqueueResponse
import com.anandm.composeview.mockPokeData
import com.anandm.composeview.network.PokeApiService
import com.anandm.composeview.network.PokeRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PokeRepositoryTest {

    private val mockWebServer = MockWebServer()

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val pokeApiService = retrofit.create(PokeApiService::class.java)

    private val sut = PokeRepositoryImpl(pokeApiService)

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should fetch movies correctly given 200 response`() {
        mockWebServer.enqueueResponse("pokemon-list.json", 200)

        runBlocking {
            val actual = sut.getPokes()

            val expected = listOf(
                mockPokeData(
                    name = "bulbasaur",
                    url = "https://pokeapi.co/api/v2/pokemon/1/"
                )
            )

            assertEquals(expected, actual)
        }
    }
}

