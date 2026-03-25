package smartgis.project.app.smartgis.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.converter.gson.GsonConverterFactory
import smartgis.project.app.smartgis.data.repositories.service.WMSService
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

  companion object {
    const val BASE_WMS_URL = "http://180.178.109.123:8080/geoserver/"
  }

  @Provides
  @Singleton
  fun provideRetrofit(gson: Gson): Retrofit {
    val okHttpClient = OkHttpClient().newBuilder()
      .readTimeout(120, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
    return Retrofit.Builder()
      .baseUrl(BASE_WMS_URL)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }


  @Provides
  @Singleton
  fun providesGson(): Gson {
    return GsonBuilder().create()
  }

  @Provides
  @Singleton
  fun provideWmsService(retrofit: Retrofit): WMSService {
    return retrofit.create(WMSService::class.java)
  }
}