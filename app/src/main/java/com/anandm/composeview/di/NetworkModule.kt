package com.anandm.composeview.di

import com.anandm.composeview.BuildConfig
import com.anandm.composeview.network.*
import com.anandm.composeview.network.mapper.PokemonDomainMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val baseUrl: String = "https://pokeapi.co/api/v2/"

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply { HttpLoggingInterceptor.Level.BODY }

        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun providePokeApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providePokeRepository(
        apiService: ApiService,
        pokemonDomainMapper: PokemonDomainMapper
    ): RemoteRepository {
        return RemoteRepositoryImpl(apiService, pokemonDomainMapper)
    }

    @Singleton
    @Provides
    fun providePokemonListMapper(): PokemonDomainMapper = PokemonDomainMapper()

}