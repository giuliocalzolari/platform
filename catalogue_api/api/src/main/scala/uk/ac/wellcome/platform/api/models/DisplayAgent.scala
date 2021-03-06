package uk.ac.wellcome.platform.api.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import uk.ac.wellcome.models.AbstractAgent

@ApiModel(
  value = "Agent"
)
case class DisplayAgent(
  @ApiModelProperty(
    value = "The name of the agent"
  ) label: String,
  @JsonProperty("type") ontologyType: String = "Agent"
)

case object DisplayAgent {
  def apply(agent: AbstractAgent): DisplayAgent =
    DisplayAgent(
      label = agent.label,
      ontologyType = agent.ontologyType
    )
}
