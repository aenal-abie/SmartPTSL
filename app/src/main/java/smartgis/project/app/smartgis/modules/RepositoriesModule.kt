package smartgis.project.app.smartgis.di.modules

//import com.smartptsl.panutan.entity.repository.IChatRepository
//import com.smartptsl.panutan.entity.repository.ISurveyRepository
//import com.smartptsl.panutan.repository.ChatRepository
//import com.smartptsl.panutan.repository.SurveyRepository
//import com.smartptsl.panutan.repository.remote.IChatRemote
//import com.smartptsl.panutan.repository.remote.ISurveyRemote
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import smartgis.project.app.smartgis.data.repositories.FeatureRepository
import smartgis.project.app.smartgis.data.repositories.remote.IFeatureRemote
import smartgis.project.app.smartgis.data.repositories.remote.IPengtanRemote
import smartgis.project.app.smartgis.repository.base.IFeatureRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoriesModule {
  @Singleton
  @Provides
  fun provideFeatureRepositories(
    featureRemote: IFeatureRemote,
    pengtanRemote: IPengtanRemote
  ): IFeatureRepository = FeatureRepository(featureRemote, pengtanRemote)

//  @Singleton
//  @Provides
//  fun provideChatRepositories(
//    chatRemote: IChatRemote
//  ): IChatRepository = ChatRepository(chatRemote)


//  @Singleton
//  @Provides
//  fun provideSurveyRepositories(
//    surveyRemote: ISurveyRemote
//  ): ISurveyRepository = SurveyRepository(surveyRemote)

}