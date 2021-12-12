package mapleGBP.model.converter

import mapleGBP.model.World
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class WorldConverter : AttributeConverter<World, String> {
    override fun convertToDatabaseColumn(world: World?): String {
        return world ?. worldName ?: World.NONE.worldName
    }

    override fun convertToEntityAttribute(worldName: String?): World {
        return worldName ?. let { World.from(worldName) } ?: World.NONE
    }
}