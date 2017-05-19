package uk.ac.wellcome.transformer.modules

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import uk.ac.wellcome.finatra.annotations.{CalmDynamoConfig, MiroDynamoConfig}
import uk.ac.wellcome.models.Transformable
import uk.ac.wellcome.models.aws.DynamoConfig
import uk.ac.wellcome.transformer.parsers.{
  CalmParser,
  MiroParser,
  TransformableParser
}

object TransformableParserModule extends TwitterModule {

  @Singleton
  @Provides
  def providesTransformableParser(
    @CalmDynamoConfig calmDynamoConfig: DynamoConfig,
    @MiroDynamoConfig miroDynamoConfig: DynamoConfig)
    : TransformableParser[Transformable] = {

    val dynamoConfig =
      DynamoConfig.findWithTable(List(calmDynamoConfig, miroDynamoConfig))

    dynamoConfig.table match {
      case "MiroData" => new MiroParser
      case "CalmData" => new CalmParser
      case tableName =>
        throw new RuntimeException(s"$tableName is not a recognised source")
    }
  }
}
