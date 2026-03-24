package smartgis.project.app.smartgis.di.modules

//import com.smartptsl.panutan.repository.remote.IChatRemote
//import com.smartptsl.panutan.repository.remote.ISurveyRemote
//import com.smartptsl.panutan.source.remote.ChatRemote
//import com.smartptsl.panutan.source.remote.SurveyRemote
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import smartgis.project.app.smartgis.data.repositories.remote.IFeatureRemote
import smartgis.project.app.smartgis.data.repositories.remote.IPengtanRemote
import smartgis.project.app.smartgis.data.repositories.service.WMSService
import smartgis.project.app.smartgis.data.source.remote.FeatureRemote
import smartgis.project.app.smartgis.data.source.remote.PengtanRemote
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemotesModule {
  @Singleton
  @Provides
  fun provideFeatureRemote(wmsService: WMSService): IFeatureRemote = FeatureRemote(wmsService)

  @Singleton
  @Provides
  fun providePengtanRemote(): IPengtanRemote = PengtanRemote()

//  @Singleton
//  @Provides
//  fun provideChatRemote(): IChatRemote = ChatRemote()

//  @Singleton
//  @Provides
//  fun provideSurveyRemote(): ISurveyRemote = SurveyRemote()
}